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
import com.kkp.nure.animalrescue.adapters.TreatmentReportAdapter;
import com.kkp.nure.animalrescue.adapters.VolunteerRequestAdapter;
import com.kkp.nure.animalrescue.api.ApiClient;
import com.kkp.nure.animalrescue.api.services.AnimalService;
import com.kkp.nure.animalrescue.api.services.MediaService;
import com.kkp.nure.animalrescue.api.services.VolunteerRequestService;
import com.kkp.nure.animalrescue.entities.VolunteerRequest;

import java.util.ArrayList;
import java.util.List;

public class VolunteerRequestsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_volunteer_requests);

        String token = getTokenOrDie(VolunteerRequestsActivity.this);
        if(token == null) return;

        VolunteerRequestService volunteerRequestService = ApiClient.getAuthClient(token).create(VolunteerRequestService.class);

        RecyclerView recyclerView = findViewById(R.id.recycler_requests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        VolunteerRequestAdapter adapter = new VolunteerRequestAdapter(new ArrayList<>(), VolunteerRequestsActivity.this);
        recyclerView.setAdapter(adapter);

        volunteerRequestService.getRequests().enqueue(new ApiClient.CustomCallback<>() {
            @Override
            public void onResponse(@NonNull List<VolunteerRequest> resp) {
                adapter.setRequests(resp);
            }

            @Override
            public void onError(@NonNull String error, int statusCode) {
                Toast.makeText(VolunteerRequestsActivity.this, getString(R.string.failed_to_fetch_volunteer_requests, error), Toast.LENGTH_SHORT).show();
            }
        });
    }
}