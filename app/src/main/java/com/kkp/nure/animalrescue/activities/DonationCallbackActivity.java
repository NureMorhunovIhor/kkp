package com.kkp.nure.animalrescue.activities;

import static com.kkp.nure.animalrescue.utils.AndroidUtils.getTokenOrDie;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.kkp.nure.animalrescue.R;
import com.kkp.nure.animalrescue.api.ApiClient;
import com.kkp.nure.animalrescue.api.services.DonationService;
import com.kkp.nure.animalrescue.entities.Donation;

import java.util.Objects;

public class DonationCallbackActivity extends AppCompatActivity {
    private LinearLayout successLayout;
    private LinearLayout errorLayout;
    private LinearLayout loadingLayout;

    private DonationService donationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_donation_callback);

        successLayout = findViewById(R.id.panel_success);
        errorLayout = findViewById(R.id.panel_error);
        loadingLayout = findViewById(R.id.panel_loading);
        Button tryAgainButton = findViewById(R.id.button_error_try_again);

        successLayout.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.VISIBLE);

        Uri data=  getIntent().getData();
        if(data == null || !Objects.equals(data.getScheme(), "com.kkp.nure.animalrescue.payment") || data.getAuthority() == null || data.getAuthority().isEmpty()) {
            finish();
            return;
        }

        String[] paymentIds = data.getAuthority().split(":")[0].split("_");
        if(paymentIds.length < 2) {
            finish();
            return;
        }

        long goalId, donationId;
        try {
            goalId = Long.parseLong(paymentIds[0]);
            donationId = Long.parseLong(paymentIds[1]);
        } catch (NumberFormatException e) {
            Log.e("payment", "Failed to parse goal/donation id");
            finish();
            return;
        }

        String token = getTokenOrDie(DonationCallbackActivity.this);
        if(token == null) return;
        donationService = ApiClient.getAuthClient(token).create(DonationService.class);

        tryAgainButton.setOnClickListener(v -> finishPayment(goalId, donationId));

        finishPayment(goalId, donationId);
    }

    private void finishPayment(long goalId, long donationId) {
        donationService.finishPayment(goalId, donationId).enqueue(new ApiClient.CustomCallback<>() {
            @Override
            public void onResponse(@NonNull Donation resp) {
                successLayout.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.GONE);
                Toast.makeText(DonationCallbackActivity.this, R.string.payment_finished_successfully, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(@NonNull String error, int statusCode) {
                successLayout.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
                loadingLayout.setVisibility(View.GONE);
                Toast.makeText(DonationCallbackActivity.this, getString(R.string.failed_to_process_payment, error), Toast.LENGTH_SHORT).show();
            }
        });
    }
}