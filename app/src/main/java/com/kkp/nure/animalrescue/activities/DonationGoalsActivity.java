package com.kkp.nure.animalrescue.activities;

import static com.kkp.nure.animalrescue.utils.AndroidUtils.getTokenOrDie;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kkp.nure.animalrescue.R;
import com.kkp.nure.animalrescue.adapters.DonationGoalsAdapter;
import com.kkp.nure.animalrescue.api.ApiClient;
import com.kkp.nure.animalrescue.api.responses.PaginatedResponse;
import com.kkp.nure.animalrescue.api.services.DonationService;
import com.kkp.nure.animalrescue.entities.Dialog;
import com.kkp.nure.animalrescue.entities.DonationGoal;

import java.util.ArrayList;

public class DonationGoalsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DonationGoalsAdapter adapter;
    private boolean isLoading = false;
    private int page = 1;
    private final int pageSize = 10;
    private boolean hasMore = true;

    private DonationService donationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_donation_goals);

        String token = getTokenOrDie(DonationGoalsActivity.this);
        if(token == null) return;
        donationService = ApiClient.getAuthClient(token).create(DonationService.class);

        recyclerView = findViewById(R.id.recycler_goals);
        recyclerView.setLayoutManager(new LinearLayoutManager(DonationGoalsActivity.this));

        adapter = new DonationGoalsAdapter(DonationGoalsActivity.this, new ArrayList<>());
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (!isLoading && !recyclerView.canScrollVertically(1)) {
                    loadNextPage();
                }
            }
        });

        loadNextPage();
    }

    private void loadNextPage() {
        isLoading = true;
        if(!hasMore)
            return;

        donationService.getGoals(page, pageSize).enqueue(new ApiClient.CustomCallback<>() {
            @Override
            public void onResponse(@NonNull PaginatedResponse<DonationGoal> response) {
                adapter.addDonationGoals(response.getResult());
                page++;

                if(adapter.getItemCount() >= response.getCount()) {
                    hasMore = false;
                }

                isLoading = false;
            }

            @Override
            public void onError(@NonNull String error, int code) {
                Toast.makeText(DonationGoalsActivity.this, getString(R.string.failed_to_fetch_goals, error), Toast.LENGTH_SHORT).show();
            }
        });
    }
}