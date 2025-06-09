package com.kkp.nure.animalrescue.activities;

import static com.kkp.nure.animalrescue.utils.AndroidUtils.getLongExtraOrDie;
import static com.kkp.nure.animalrescue.utils.AndroidUtils.getTokenOrDie;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kkp.nure.animalrescue.R;
import com.kkp.nure.animalrescue.adapters.TreatmentReportAdapter;
import com.kkp.nure.animalrescue.api.ApiClient;
import com.kkp.nure.animalrescue.api.responses.PaginatedResponse;
import com.kkp.nure.animalrescue.api.services.AnimalService;
import com.kkp.nure.animalrescue.entities.TreatmentReport;

import java.util.ArrayList;

public class TreatmentReportsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TreatmentReportAdapter adapter;
    private boolean isLoading = false;
    private int page = 1;
    private final int pageSize = 10;
    private boolean hasMore = true;

    private long animalId;
    private AnimalService animalService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_treatment_reports);

        String token = getTokenOrDie(this);
        if(token == null) return;

        Long animalIdOpt = getLongExtraOrDie(this, "animalId");
        if(animalIdOpt == null) return;
        animalId = animalIdOpt;

        recyclerView = findViewById(R.id.recycler_reports);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TreatmentReportAdapter(new ArrayList<>(), TreatmentReportsActivity.this);
        recyclerView.setAdapter(adapter);

        animalService = ApiClient.getAuthClient(token).create(AnimalService.class);

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

        animalService.getTreatmentReports(animalId, page, pageSize).enqueue(new ApiClient.CustomCallback<>() {
            @Override
            public void onResponse(@NonNull PaginatedResponse<TreatmentReport> response) {
                adapter.addReports(response.getResult());
                page++;

                if(adapter.getItemCount() >= response.getCount()) {
                    hasMore = false;
                }

                isLoading = false;
            }

            @Override
            public void onError(@NonNull String error, int code) {
                Toast.makeText(TreatmentReportsActivity.this, getString(R.string.failed_to_fetch_treatment_reports, error), Toast.LENGTH_SHORT).show();
            }
        });
    }
}