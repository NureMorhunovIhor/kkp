package com.kkp.nure.animalrescue.activities;

import static com.kkp.nure.animalrescue.utils.AndroidUtils.getTokenOrDie;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.kkp.nure.animalrescue.R;
import com.kkp.nure.animalrescue.adapters.RecentFoundReportAdapter;
import com.kkp.nure.animalrescue.api.ApiClient;
import com.kkp.nure.animalrescue.api.responses.PaginatedResponse;
import com.kkp.nure.animalrescue.api.services.AnimalReportService;
import com.kkp.nure.animalrescue.entities.FoundReport;

import java.util.ArrayList;

public class RecentReportsActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSION_LOCATION = 201;

    private RecyclerView recyclerView;
    private RecentFoundReportAdapter adapter;
    private boolean isLoading = true;
    private int page = 1;
    private final int pageSize = 10;
    private boolean hasMore = true;

    private AnimalReportService reportService;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recent_reports);

        String token = getTokenOrDie(this);
        if(token == null) return;

        reportService = ApiClient.getAuthClient(token).create(AnimalReportService.class);

        recyclerView = findViewById(R.id.reports_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecentFoundReportAdapter(new ArrayList<>(), RecentReportsActivity.this, reportService);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (!isLoading && !recyclerView.canScrollVertically(1)) {
                    loadNextPage();
                }
            }
        });

        if (ActivityCompat.checkSelfPermission(RecentReportsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(RecentReportsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSION_LOCATION);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_PERMISSION_LOCATION && resultCode == Activity.RESULT_OK) {
            getCurrentLocation();
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(RecentReportsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(RecentReportsActivity.this, R.string.location_is_needed_to_get_recent_animal_reports, Toast.LENGTH_SHORT).show();
            return;
        }

        FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(RecentReportsActivity.this);
        locationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location == null) {
                Toast.makeText(RecentReportsActivity.this, R.string.unable_to_get_location, Toast.LENGTH_SHORT).show();
                return;
            }

            latitude = location.getLatitude();
            longitude = location.getLongitude();
            isLoading = false;

            loadNextPage();
        });
    }

    private void loadNextPage() {
        if(isLoading)
            return;

        isLoading = true;
        if(!hasMore)
            return;

        reportService.getRecent(page, pageSize, latitude, longitude).enqueue(new ApiClient.CustomCallback<>() {
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
                Toast.makeText(RecentReportsActivity.this, getString(R.string.failed_to_fetch_recent_reports, error), Toast.LENGTH_SHORT).show();
            }
        });
    }
}