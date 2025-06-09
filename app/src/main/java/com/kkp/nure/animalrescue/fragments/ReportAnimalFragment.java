package com.kkp.nure.animalrescue.fragments;

import static com.kkp.nure.animalrescue.utils.UriUtil.readUriToByteArray;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.kkp.nure.animalrescue.R;
import com.kkp.nure.animalrescue.activities.AnimalInfoActivity;
import com.kkp.nure.animalrescue.adapters.MediaAdapter;
import com.kkp.nure.animalrescue.api.ApiClient;
import com.kkp.nure.animalrescue.api.requests.CreateAnimalReportRequest;
import com.kkp.nure.animalrescue.api.services.AnimalReportService;
import com.kkp.nure.animalrescue.api.services.MediaService;
import com.kkp.nure.animalrescue.entities.FoundReport;
import com.kkp.nure.animalrescue.entities.Media;
import com.kkp.nure.animalrescue.enums.AnimalGender;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

public class ReportAnimalFragment extends CustomFragment {

    private EditText nameEdit, breedEdit, stateEdit, descriptionEdit;
    private RecyclerView mediaRecyclerView;
    private MediaAdapter mediaAdapter;
    private Button getLocationButton;
    private Spinner genderSelect;

    private MapView mapView;
    private Marker marker;

    private static final int REQUEST_MEDIA_PICK = 101;
    private static final int REQUEST_PERMISSION_LOCATION = 201;
    private final List<Media> medias = new ArrayList<>();
    private MediaService mediaService;
    private AnimalReportService reportService;

    private final ActivityResultLauncher<Intent> pickMediaLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
            handleImagePicked(result.getData());
        }
    });

    private void handleImagePicked(Intent data) {
        if (data.getClipData() != null) {
            ClipData clipData = data.getClipData();
            int limit = 10 - medias.size();
            for (int i = 0; i < clipData.getItemCount() && i < limit; i++) {
                Uri uri = clipData.getItemAt(i).getUri();
                uploadUriMedia(uri);
            }
        } else if (data.getData() != null) {
            uploadUriMedia(data.getData());
        }
    }

    private void uploadUriMedia(Uri uri) {
        byte[] mediaToUpload = readUriToByteArray(requireContext(), uri);
        if(mediaToUpload == null) {
            Toast.makeText(requireContext(), R.string.failed_to_read_image, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), "Failed to upload photo: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public ReportAnimalFragment() {
        super(R.layout.fragment_report_animal);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        nameEdit = view.findViewById(R.id.edit_name);
        breedEdit = view.findViewById(R.id.edit_breed);
        stateEdit = view.findViewById(R.id.edit_state);
        descriptionEdit = view.findViewById(R.id.edit_description);
        mediaRecyclerView = view.findViewById(R.id.media_recycler_view);
        getLocationButton = view.findViewById(R.id.button_get_my_location);
        genderSelect = view.findViewById(R.id.spinner_gender);

        SharedPreferences prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = prefs.getString("authToken", null);
        mediaService = ApiClient.getAuthClient(token).create(MediaService.class);
        reportService = ApiClient.getAuthClient(token).create(AnimalReportService.class);

        mediaAdapter = new MediaAdapter(medias, requireContext());
        mediaRecyclerView.setAdapter(mediaAdapter);
        mediaRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        Button addMediaButton = view.findViewById(R.id.button_add_media);
        Button submitButton = view.findViewById(R.id.button_submit);

        addMediaButton.setOnClickListener(v -> {
            if(medias.size() >= 10) {
                Toast.makeText(getContext(), R.string.you_cannot_add_more_than_10_medias, Toast.LENGTH_SHORT).show();
                return;
            }
            selectMedia();
        });
        submitButton.setOnClickListener(v -> submitReport());

        setupOsm(view);

        getLocationButton.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_PERMISSION_LOCATION);
            }
        });

        String[] items = new String[]{AnimalGender.UNKNOWN.strName, AnimalGender.MALE.strName, AnimalGender.FEMALE.strName};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, items);
        genderSelect.setAdapter(adapter);
    }

    private void setupOsm(@NonNull View view) {
        Context ctx = requireContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        mapView = view.findViewById(R.id.osm_map);
        mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        GeoPoint startPoint = new GeoPoint(50.4015698, 30.2030643);
        mapView.getController().setZoom(12.0);
        mapView.getController().setCenter(startPoint);

        mapView.setOnTouchListener((v, event) -> false);

        mapView.getOverlays().add(new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                if (marker == null) {
                    marker = new Marker(mapView);
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    mapView.getOverlays().add(marker);
                }
                marker.setPosition(p);
                marker.setTitle(getString(R.string.found_here));
                mapView.invalidate();
                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        }));
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        locationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                GeoPoint myPos = new GeoPoint(location.getLatitude(), location.getLongitude());
                mapView.getController().animateTo(myPos);
                mapView.getController().setZoom(15.0);

                if (marker == null) {
                    marker = new Marker(mapView);
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    mapView.getOverlays().add(marker);
                }

                marker.setPosition(myPos);
                marker.setTitle(getString(R.string.my_location));
                mapView.invalidate();
            } else {
                Toast.makeText(getContext(), R.string.unable_to_get_location, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) mapView.onPause();
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
        } else if(requestCode == REQUEST_PERMISSION_LOCATION && resultCode == Activity.RESULT_OK) {
            getCurrentLocation();
        }
    }

    private void submitReport() {
        String name = nameEdit.getText().toString().trim();
        String breed = breedEdit.getText().toString().trim();
        String state = stateEdit.getText().toString().trim();
        String description = descriptionEdit.getText().toString().trim();

        if (state.isEmpty() || breed.isEmpty()) {
            Toast.makeText(getContext(), R.string.please_fill_required_fields, Toast.LENGTH_SHORT).show();
            return;
        } else if(marker == null) {
            Toast.makeText(getContext(), R.string.please_select_location, Toast.LENGTH_SHORT).show();
            return;
        }

        List<Long> mediaIds = new ArrayList<>();
        for(Media media : medias) {
            mediaIds.add(media.getId());
        }

        CreateAnimalReportRequest request = new CreateAnimalReportRequest(null, name, breed, description, marker.getPosition().getLatitude(),
                marker.getPosition().getLongitude(), mediaIds, AnimalGender.fromValue(genderSelect.getSelectedItemPosition()));

        reportService.reportAnimal(request).enqueue(new ApiClient.CustomCallback<>() {
            @Override
            public void onResponse(@NonNull FoundReport response) {
                Toast.makeText(getContext(), R.string.report_sent, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), AnimalInfoActivity.class);
                intent.putExtra("animalId", response.getAnimal().getId());
                startActivity(intent);
            }

            @Override
            public void onError(@NonNull String error, int code) {
                Toast.makeText(getContext(), getString(R.string.failed_to_send_report, error), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
