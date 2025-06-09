package com.kkp.nure.animalrescue.activities;

import static com.kkp.nure.animalrescue.utils.AndroidUtils.getTokenOrDie;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kkp.nure.animalrescue.R;
import com.kkp.nure.animalrescue.adapters.AnimalsAdapter;
import com.kkp.nure.animalrescue.adapters.TreatingAnimalsAdapter;
import com.kkp.nure.animalrescue.api.ApiClient;
import com.kkp.nure.animalrescue.api.responses.PaginatedResponse;
import com.kkp.nure.animalrescue.api.services.AnimalReportService;
import com.kkp.nure.animalrescue.api.services.SubscriptionService;
import com.kkp.nure.animalrescue.entities.Animal;
import com.kkp.nure.animalrescue.entities.FoundReport;

import java.util.ArrayList;

public class TreatingAnimalsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TreatingAnimalsAdapter adapter;
    private boolean isLoading = false;
    private int page = 1;
    private final int pageSize = 10;
    private boolean hasMore = true;

    private AnimalReportService reportService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_treating_animals);

        String token = getTokenOrDie(this);
        if(token == null) return;

        reportService = ApiClient.getAuthClient(token).create(AnimalReportService.class);

        recyclerView = findViewById(R.id.my_animals_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(TreatingAnimalsActivity.this));
        adapter = new TreatingAnimalsAdapter(TreatingAnimalsActivity.this, new ArrayList<>());
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

        reportService.getMy(page, pageSize).enqueue(new ApiClient.CustomCallback<>() {
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
                Toast.makeText(TreatingAnimalsActivity.this, getString(R.string.failed_to_fetch_animal_reports_fmt, error), Toast.LENGTH_SHORT).show();
            }
        });
    }
}