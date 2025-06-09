package com.kkp.nure.animalrescue.activities;

import static com.kkp.nure.animalrescue.utils.AndroidUtils.getLongExtraOrDie;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.kkp.nure.animalrescue.R;
import com.kkp.nure.animalrescue.api.ApiClient;
import com.kkp.nure.animalrescue.api.requests.CreateTreatmentReportRequest;
import com.kkp.nure.animalrescue.api.requests.UpdateAnimalRequest;
import com.kkp.nure.animalrescue.api.services.AnimalService;
import com.kkp.nure.animalrescue.api.services.TreatmentReportService;
import com.kkp.nure.animalrescue.entities.Animal;
import com.kkp.nure.animalrescue.entities.TreatmentReport;
import com.kkp.nure.animalrescue.enums.AnimalStatus;

public class CreateTreatmentReportActivity extends AppCompatActivity {
    private static final AnimalStatus[] statusItems = new AnimalStatus[]{AnimalStatus.RELEASED, AnimalStatus.WAITING_FOR_ADOPTION, AnimalStatus.ADOPTED};
    private static final String[] statusItemsStr = new String[statusItems.length];

    static {
        for(int i = 0; i < statusItems.length; ++i)
            statusItemsStr[i] = statusItems[i].strName;
    }

    private EditText nameEdit, descriptionEdit, moneySpentEdit;
    private Spinner status;

    private TreatmentReportService reportService;
    private AnimalService animalService;
    private long reportId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_treatment_report);

        nameEdit = findViewById(R.id.edit_name);
        descriptionEdit = findViewById(R.id.edit_description);
        moneySpentEdit = findViewById(R.id.edit_money_spent);
        status = findViewById(R.id.spinner_status);

        Long reportId = getLongExtraOrDie(CreateTreatmentReportActivity.this, "reportId");
        if(reportId == null) {
            finish();
            return;
        }
        this.reportId = reportId;

        SharedPreferences prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = prefs.getString("authToken", null);
        reportService = ApiClient.getAuthClient(token).create(TreatmentReportService.class);
        animalService = ApiClient.getAuthClient(token).create(AnimalService.class);

        Button submitButton = findViewById(R.id.button_submit);
        submitButton.setOnClickListener(v -> submitReport());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, statusItemsStr);
        status.setAdapter(adapter);
    }

    private void submitReport() {
        String name = nameEdit.getText().toString();
        String description = descriptionEdit.getText().toString();
        double moneySpent = Double.parseDouble(moneySpentEdit.getText().toString());
        AnimalStatus newStatus = statusItems[status.getSelectedItemPosition()];

        if(name.isEmpty())
            name = null;

        UpdateAnimalRequest request = new UpdateAnimalRequest(name, null, newStatus, null, null, null, null, null, null);
        reportService.createTreatmentReport(new CreateTreatmentReportRequest(reportId, description, moneySpent)).enqueue(new ApiClient.CustomCallback<>() {
            @Override
            public void onResponse(@NonNull TreatmentReport resp) {
                Animal animal = resp.getReport().getAnimal();
                animalService.updateAnimal(animal.getId(), request).enqueue(new ApiClient.CustomCallback<>() {
                    @Override
                    public void onResponse(@NonNull Animal resp) {
                        Toast.makeText(CreateTreatmentReportActivity.this, R.string.animal_info_updated, Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onError(@NonNull String error, int statusCode) {
                        Toast.makeText(CreateTreatmentReportActivity.this, getString(R.string.failed_to_update_animal_info, error), Toast.LENGTH_SHORT).show();
                    }
                });

                Toast.makeText(CreateTreatmentReportActivity.this, R.string.treatment_report_created, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(@NonNull String error, int statusCode) {
                Toast.makeText(CreateTreatmentReportActivity.this, getString(R.string.failed_to_create_treatment_report, error), Toast.LENGTH_SHORT).show();
            }
        });
    }
}