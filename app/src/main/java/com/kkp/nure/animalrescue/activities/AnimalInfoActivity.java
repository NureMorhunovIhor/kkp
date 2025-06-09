package com.kkp.nure.animalrescue.activities;

import static com.kkp.nure.animalrescue.utils.AndroidUtils.getLongExtraOrDie;
import static com.kkp.nure.animalrescue.utils.AndroidUtils.getTokenOrDie;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.kkp.nure.animalrescue.R;
import com.kkp.nure.animalrescue.adapters.PhotoCarouselAdapter;
import com.kkp.nure.animalrescue.api.ApiClient;
import com.kkp.nure.animalrescue.api.services.AnimalService;
import com.kkp.nure.animalrescue.api.services.SubscriptionService;
import com.kkp.nure.animalrescue.entities.Animal;

import retrofit2.Call;

public class AnimalInfoActivity extends AppCompatActivity {

    private TextView nameText, breedText, statusText, descriptionText;
    private ViewPager2 photoCarousel;
    private PhotoCarouselAdapter photoAdapter;
    private Button subButton;

    private AnimalService animalService;
    private SubscriptionService subscriptionService;
    private Animal animal;
    private long animalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_info);

        String token = getTokenOrDie(this);
        if(token == null) return;

        Long animalIdOpt = getLongExtraOrDie(this, "animalId");
        if(animalIdOpt == null) return;
        animalId = animalIdOpt;

        photoCarousel = findViewById(R.id.photo_carousel);
        nameText = findViewById(R.id.text_name);
        breedText = findViewById(R.id.text_breed);
        statusText = findViewById(R.id.text_status);
        descriptionText = findViewById(R.id.text_description);
        subButton = findViewById(R.id.button_subscribe);
        Button foundReportsButton = findViewById(R.id.button_found_reports);
        Button treatmentReportsButton = findViewById(R.id.button_treatment_reports);

        animalService = ApiClient.getAuthClient(token).create(AnimalService.class);
        subscriptionService = ApiClient.getAuthClient(token).create(SubscriptionService.class);

        animalService.getAnimal(animalId).enqueue(new ApiClient.CustomCallback<>() {
            @Override
            public void onResponse(@NonNull Animal response) {
                animal = response;
                updateLayout();
            }

            @Override
            public void onError(@NonNull String error, int code) {
                Toast.makeText(AnimalInfoActivity.this, getString(R.string.failed_to_fetch_animal, error), Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        foundReportsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, FoundReportsActivity.class);
            intent.putExtra("animalId", animalId);
            startActivity(intent);
        });

        treatmentReportsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, TreatmentReportsActivity.class);
            intent.putExtra("animalId", animalId);
            startActivity(intent);
        });

        subButton.setOnClickListener(v -> {
            if(animal == null) {
                return;
            }

            Call<Void> request;
            if(animal.isSubscribed()) {
                request = subscriptionService.unsubscribeFromAnimal(animalId);
            } else {
                request = subscriptionService.subscribeToAnimal(animalId);
            }

            request.enqueue(new ApiClient.CustomCallback<>() {
                @Override
                public void onResponse(@NonNull Void response) {
                    animal.setSubscribed(!animal.isSubscribed());
                    updateLayout();
                    Toast.makeText(AnimalInfoActivity.this, R.string.updated_subscription_to_animal, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(@NonNull String error, int code) {
                    Toast.makeText(AnimalInfoActivity.this, getString(R.string.failed_to_update_subscription, error), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void updateLayout() {
        subButton.setEnabled(animal != null);

        if(animal == null) {
            return;
        }

        nameText.setText(animal.getName());
        breedText.setText(animal.getBreed());
        statusText.setText(animal.getStatus().strName);
        descriptionText.setText(animal.getDescription());

        photoAdapter = new PhotoCarouselAdapter(animal.getMedia().getResult(), this);
        photoCarousel.setAdapter(photoAdapter);

        if(animal.isSubscribed())
            subButton.setText(R.string.unsubscribe_from_animal);
        else
            subButton.setText(R.string.subscribe_to_animal);
    }
}