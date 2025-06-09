package com.kkp.nure.animalrescue.activities;

import static com.kkp.nure.animalrescue.utils.AndroidUtils.getTokenOrDie;
import static com.kkp.nure.animalrescue.utils.UriUtil.readUriToByteArray;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kkp.nure.animalrescue.R;
import com.kkp.nure.animalrescue.adapters.MediaAdapter;
import com.kkp.nure.animalrescue.api.ApiClient;
import com.kkp.nure.animalrescue.api.requests.CreateVolunteerRequestRequest;
import com.kkp.nure.animalrescue.api.services.MediaService;
import com.kkp.nure.animalrescue.api.services.VolunteerRequestService;
import com.kkp.nure.animalrescue.entities.Media;
import com.kkp.nure.animalrescue.entities.VolunteerRequest;

import java.util.ArrayList;
import java.util.List;

public class RequestVolunteerStatusActivity extends AppCompatActivity {

    private EditText descriptionEdit, nameEdit, phoneEdit, cityEdit, telegramEdit, viberEdit, whatsappEdit;
    private CheckBox hasVehicleCheck, availableWeekdaysCheck, availableWeekendsCheck;
    private CheckBox helpShelterCheck, helpDeliveryCheck, helpVisitCheck, helpMedicalCheck, helpInformationCheck;
    private RecyclerView mediaRecyclerView;
    private MediaAdapter mediaAdapter;

    private static final int REQUEST_MEDIA_PICK = 101;
    private final List<Media> medias = new ArrayList<>();
    private MediaService mediaService;
    private VolunteerRequestService volunteerRequestService;

    private final ActivityResultLauncher<Intent> pickMediaLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
            handleImagePicked(result.getData());
        }
    });

    private void handleImagePicked(Intent data) {
        if (data.getClipData() != null) {
            ClipData clipData = data.getClipData();
            int limit = 15 - medias.size();
            for (int i = 0; i < clipData.getItemCount() && i < limit; i++) {
                Uri uri = clipData.getItemAt(i).getUri();
                uploadUriMedia(uri);
            }
        } else if (data.getData() != null) {
            uploadUriMedia(data.getData());
        }
    }

    private void uploadUriMedia(Uri uri) {
        byte[] mediaToUpload = readUriToByteArray(RequestVolunteerStatusActivity.this, uri);
        if(mediaToUpload == null) {
            Toast.makeText(RequestVolunteerStatusActivity.this, R.string.failed_to_read_image, Toast.LENGTH_SHORT).show();
            return;
        }

        new MediaService.Uploader(mediaService).uploadPhoto(mediaToUpload, new MediaService.PhotoUploadedCallback() {
            @Override
            public void onSuccess(Media media) {
                medias.add(media);
                mediaAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(RequestVolunteerStatusActivity.this, getString(R.string.failed_to_upload_photo, error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_request_volunteer_status);

        String token = getTokenOrDie(RequestVolunteerStatusActivity.this);
        if(token == null) return;

        mediaService = ApiClient.getAuthClient(token).create(MediaService.class);
        volunteerRequestService = ApiClient.getAuthClient(token).create(VolunteerRequestService.class);

        descriptionEdit = findViewById(R.id.edit_description);
        nameEdit = findViewById(R.id.edit_full_name);
        phoneEdit = findViewById(R.id.edit_phone_number);
        cityEdit = findViewById(R.id.edit_city);

        telegramEdit = findViewById(R.id.edit_telegram_username);
        viberEdit = findViewById(R.id.edit_viber_phone);
        whatsappEdit = findViewById(R.id.edit_whatsapp_phone);

        hasVehicleCheck = findViewById(R.id.check_has_vehicle);
        availableWeekdaysCheck = findViewById(R.id.check_availability_weekdays);
        availableWeekendsCheck = findViewById(R.id.check_availability_weekends);

        helpShelterCheck = findViewById(R.id.check_help_shelter);
        helpDeliveryCheck = findViewById(R.id.check_help_clinic_delivery);
        helpVisitCheck = findViewById(R.id.check_help_onsite_visit);
        helpMedicalCheck = findViewById(R.id.check_help_medical_care);
        helpInformationCheck = findViewById(R.id.check_help_information);

        mediaRecyclerView = findViewById(R.id.media_recycler_view);

        mediaAdapter = new MediaAdapter(medias, RequestVolunteerStatusActivity.this);
        mediaRecyclerView.setAdapter(mediaAdapter);
        mediaRecyclerView.setLayoutManager(new LinearLayoutManager(RequestVolunteerStatusActivity.this, LinearLayoutManager.HORIZONTAL, false));

        Button addMediaButton = findViewById(R.id.button_add_media);
        Button submitButton = findViewById(R.id.button_submit);
        Button viewRequestsButton = findViewById(R.id.button_view_requests);

        addMediaButton.setOnClickListener(v -> {
            if(medias.size() >= 10) {
                Toast.makeText(RequestVolunteerStatusActivity.this, R.string.you_cannot_add_more_than_10_medias, Toast.LENGTH_SHORT).show();
                return;
            }
            selectMedia();
        });
        submitButton.setOnClickListener(v -> submitRequest());
        viewRequestsButton.setOnClickListener(v -> startActivity(new Intent(RequestVolunteerStatusActivity.this, VolunteerRequestsActivity.class)));
    }

    private void selectMedia() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*"});
        pickMediaLauncher.launch(Intent.createChooser(intent, getString(R.string.select_media)));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_MEDIA_PICK && resultCode == Activity.RESULT_OK && data != null) {
            handleImagePicked(data);
        }
    }

    private void submitRequest() {
        String description = descriptionEdit.getText().toString().trim();
        String name = nameEdit.getText().toString().trim();
        String phone = phoneEdit.getText().toString().trim();
        String city = cityEdit.getText().toString().trim();
        String telegram = telegramEdit.getText().toString().trim();
        String viber = viberEdit.getText().toString().trim();
        String whatsapp = whatsappEdit.getText().toString().trim();
        boolean hasVehicle = hasVehicleCheck.isChecked();
        boolean availableWeekdays = availableWeekdaysCheck.isChecked();
        boolean availableWeekends = availableWeekendsCheck.isChecked();
        boolean helpShelter = helpShelterCheck.isChecked();
        boolean helpDelivery = helpDeliveryCheck.isChecked();
        boolean helpVisit = helpVisitCheck.isChecked();
        boolean helpMedical = helpMedicalCheck.isChecked();
        boolean helpInformation = helpInformationCheck.isChecked();

        if(telegram.isEmpty())
            telegram = null;
        if(viber.isEmpty())
            viber = null;
        if(whatsapp.isEmpty())
            whatsapp = null;

        if (description.isEmpty() || name.isEmpty() || phone.isEmpty() || city.isEmpty()) {
            Toast.makeText(RequestVolunteerStatusActivity.this, R.string.please_fill_required_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        if(!availableWeekdays && !availableWeekends) {
            Toast.makeText(RequestVolunteerStatusActivity.this, "You need select at least one availability option", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!helpShelter && !helpDelivery && !helpVisit && !helpMedical && !helpInformation) {
            Toast.makeText(RequestVolunteerStatusActivity.this, "You need select at least one help option", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Long> mediaIds = new ArrayList<>() {{
            for(Media media : medias)
                add(media.getId());
        }};

        int availability = 0;
        if(availableWeekdays)
            availability |= VolunteerRequest.AVAILABILITY_WEEKDAYS;
        if(availableWeekends)
            availability |= VolunteerRequest.AVAILABILITY_WEEKENDS;

        int help = 0;
        if (helpShelter)
            help |= VolunteerRequest.HELP_SHELTER;
        if (helpDelivery)
            help |= VolunteerRequest.HELP_CLINIC_DELIVERY;
        if (helpVisit)
            help |= VolunteerRequest.HELP_ONSITE_VISIT;
        if (helpMedical)
            help |= VolunteerRequest.HELP_MEDICAL_CARE;
        if (helpInformation)
            help |= VolunteerRequest.HELP_INFORMATION;

        CreateVolunteerRequestRequest request = new CreateVolunteerRequestRequest(name, description, mediaIds, hasVehicle, phone, city, availability, help, telegram, viber, whatsapp);
        volunteerRequestService.createRequest(request).enqueue(new ApiClient.CustomCallback<>() {
            @Override
            public void onResponse(@NonNull VolunteerRequest resp) {
                Toast.makeText(RequestVolunteerStatusActivity.this, R.string.request_submitted, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(@NonNull String error, int statusCode) {
                Toast.makeText(RequestVolunteerStatusActivity.this, getString(R.string.failed_to_submit_volunteer_request, error), Toast.LENGTH_SHORT).show();
            }
        });
    }
}