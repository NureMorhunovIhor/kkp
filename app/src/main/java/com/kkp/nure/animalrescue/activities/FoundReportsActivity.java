package com.kkp.nure.animalrescue.activities;

import static com.kkp.nure.animalrescue.utils.AndroidUtils.getLongExtraOrDie;
import static com.kkp.nure.animalrescue.utils.AndroidUtils.getTokenOrDie;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kkp.nure.animalrescue.R;
import com.kkp.nure.animalrescue.adapters.FoundReportAdapter;
import com.kkp.nure.animalrescue.api.ApiClient;
import com.kkp.nure.animalrescue.api.responses.PaginatedResponse;
import com.kkp.nure.animalrescue.api.services.AnimalService;
import com.kkp.nure.animalrescue.entities.FoundReport;

import java.util.ArrayList;

public class FoundReportsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FoundReportAdapter adapter;
    private boolean isLoading = false;
    private int page = 1;
    private final int pageSize = 10;
    private boolean hasMore = true;

    private long animalId;
    private AnimalService animalService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found_reports);

        String token = getTokenOrDie(this);
        if(token == null) return;

        Long animalIdOpt = getLongExtraOrDie(this, "animalId");
        if(animalIdOpt == null) return;
        animalId = animalIdOpt;

        recyclerView = findViewById(R.id.reports_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FoundReportAdapter(new ArrayList<>(), FoundReportsActivity.this);
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

        animalService.getFoundReports(animalId, page, pageSize).enqueue(new ApiClient.CustomCallback<>() {
            @Override
            public void onResponse(@NonNull PaginatedResponse<FoundReport> response) {
                adapter.addReports(response.getResult());
                page++;

                if(adapter.getItemCount() >= response.getCount()) {
                    hasMore = false;
                }

                isLoading = false;
            }

            @Override
            public void onError(@NonNull String error, int code) {
                Toast.makeText(FoundReportsActivity.this, getString(R.string.failed_to_fetch_animal_reports_fmt, error), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
