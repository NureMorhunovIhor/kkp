package com.kkp.nure.animalrescue.activities;

import static com.kkp.nure.animalrescue.utils.AndroidUtils.DATE_FMT;
import static com.kkp.nure.animalrescue.utils.AndroidUtils.getLongExtraOrDie;
import static com.kkp.nure.animalrescue.utils.AndroidUtils.getTokenOrDie;
import static com.kkp.nure.animalrescue.utils.GlideUtils.loadMediaToImage;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.kkp.nure.animalrescue.R;
import com.kkp.nure.animalrescue.adapters.DonationsAdapter;
import com.kkp.nure.animalrescue.api.ApiClient;
import com.kkp.nure.animalrescue.api.requests.CreateDonationRequest;
import com.kkp.nure.animalrescue.api.responses.DonationCreatedResponse;
import com.kkp.nure.animalrescue.api.responses.PaginatedResponse;
import com.kkp.nure.animalrescue.api.services.DonationService;
import com.kkp.nure.animalrescue.entities.Donation;
import com.kkp.nure.animalrescue.entities.DonationGoal;
import com.paypal.android.corepayments.CoreConfig;
import com.paypal.android.corepayments.Environment;
import com.paypal.android.paypalwebpayments.PayPalPresentAuthChallengeResult;
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutClient;
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutFundingSource;
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutRequest;

import java.util.ArrayList;
import java.util.Date;

public class DonationGoalActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DonationsAdapter adapter;
    private boolean isLoading = false;
    private int page = 1;
    private final int pageSize = 10;
    private boolean hasMore = true;

    private DonationService donationService;
    private long donationGoalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_donation_goal);

        String token = getTokenOrDie(DonationGoalActivity.this);
        if(token == null) return;
        donationService = ApiClient.getAuthClient(token).create(DonationService.class);

        Long donationGoalIdOpt = getLongExtraOrDie(DonationGoalActivity.this, "donationGoalId");
        if(donationGoalIdOpt == null) return;
        donationGoalId = donationGoalIdOpt;

        TextView goalName = findViewById(R.id.text_name);
        TextView goalDesc = findViewById(R.id.text_description);
        TextView goalAmounts = findViewById(R.id.text_amounts);
        TextView goalDates = findViewById(R.id.text_dates);
        Button donateButton = findViewById(R.id.button_donate);

        recyclerView = findViewById(R.id.recycler_donations);
        recyclerView.setLayoutManager(new LinearLayoutManager(DonationGoalActivity.this));

        adapter = new DonationsAdapter(DonationGoalActivity.this, new ArrayList<>());
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (!isLoading && !recyclerView.canScrollVertically(1)) {
                    loadNextPage();
                }
            }
        });

        donationService.getGoal(donationGoalId).enqueue(new ApiClient.CustomCallback<>() {
            @Override
            public void onResponse(@NonNull DonationGoal resp) {
                goalName.setText(resp.getName());
                goalDesc.setText(resp.getDescription());
                goalAmounts.setText(getString(R.string.raised_fmt, resp.getGotAmount(), resp.getNeedAmount()));

                String datesText;
                String from = DATE_FMT.format(new Date(resp.getCreatedAt() * 1000));
                if(resp.getEndedAt() != null) {
                    datesText = getString(R.string.from_to, from, DATE_FMT.format(new Date(resp.getEndedAt() * 1000)));
                } else {
                    datesText = getString(R.string.from, from);
                }

                goalDates.setText(datesText);
            }

            @Override
            public void onError(@NonNull String error, int statusCode) {
                Toast.makeText(DonationGoalActivity.this, getString(R.string.failed_to_fetch_donation_goal, error), Toast.LENGTH_SHORT).show();
            }
        });

        donateButton.setOnClickListener(v -> openDonateDialog());

        loadNextPage();
    }

    private void loadNextPage() {
        isLoading = true;
        if(!hasMore)
            return;

        donationService.getGoalDonations(donationGoalId, page, pageSize).enqueue(new ApiClient.CustomCallback<>() {
            @Override
            public void onResponse(@NonNull PaginatedResponse<Donation> response) {
                adapter.addDonations(response.getResult());
                page++;

                if(adapter.getItemCount() >= response.getCount()) {
                    hasMore = false;
                }

                isLoading = false;
            }

            @Override
            public void onError(@NonNull String error, int code) {
                Toast.makeText(DonationGoalActivity.this, getString(R.string.failed_to_fetch_donations, error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openDonateDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(DonationGoalActivity.this);
        View dialogView = LayoutInflater.from(DonationGoalActivity.this).inflate(R.layout.dialog_donate, null, false);
        dialog.setContentView(dialogView);

        TextView amountText = dialogView.findViewById(R.id.donate_amount);
        TextView commentText = dialogView.findViewById(R.id.donate_comment);
        CheckBox anonCheckbox = dialogView.findViewById(R.id.donate_anon);
        Button donate = dialogView.findViewById(R.id.button_donate);
        Button close = dialogView.findViewById(R.id.button_close);

        donate.setOnClickListener(v -> {
            double amount;
            try {
                amount = Double.parseDouble(amountText.getText().toString());
            } catch (NumberFormatException e) {
                Log.e("payment", "Failed to parse amount = "+amountText.getText().toString());
                return;
            }

            String comment = commentText.getText().toString();
            boolean anon = anonCheckbox.isChecked();

            makeDonation(amount, anon, comment);
            dialog.dismiss();
        });
        close.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void makeDonation(double amount, boolean anon, String comment) {
        donationService.createDonation(donationGoalId, new CreateDonationRequest(amount, anon, comment)).enqueue(new ApiClient.CustomCallback<>() {
            @Override
            public void onResponse(@NonNull DonationCreatedResponse resp) {
                String returnUrl = "com.kkp.nure.animalrescue.payment://"+donationGoalId+"_"+resp.getId();
                Log.d("payment", "return url = "+returnUrl);

                CoreConfig config = new CoreConfig("AUHPZ6xZAFKv6YDkPRsu72hz65WaIfkLABWsnsQ--BmsZK79U9KlqlmB1j5-4aBeCFigHRQVW4E-tYPA", Environment.SANDBOX);
                PayPalWebCheckoutClient payPalClient = new PayPalWebCheckoutClient(DonationGoalActivity.this, config, returnUrl);
                PayPalWebCheckoutRequest payPalRequest = new PayPalWebCheckoutRequest(resp.getPaypalId(), PayPalWebCheckoutFundingSource.PAYPAL);
                PayPalPresentAuthChallengeResult result = payPalClient.start(DonationGoalActivity.this, payPalRequest);
                if(result instanceof PayPalPresentAuthChallengeResult.Success) {
                    PayPalPresentAuthChallengeResult.Success success = (PayPalPresentAuthChallengeResult.Success)result;
                    Log.i("payment", "success = "+success.getAuthState());
                } else if(result instanceof PayPalPresentAuthChallengeResult.Failure) {
                    PayPalPresentAuthChallengeResult.Failure failure = (PayPalPresentAuthChallengeResult.Failure)result;
                    Log.i("payment", "failure = "+failure.getError().getCode() + ": "+failure.getError().getErrorDescription());
                    Toast.makeText(DonationGoalActivity.this, getString(R.string.failed_to_initiate_payment, failure.getError().getErrorDescription()), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(@NonNull String error, int statusCode) {
                Toast.makeText(DonationGoalActivity.this, getString(R.string.failed_to_create_donation, error), Toast.LENGTH_SHORT).show();
            }
        });
    }
}