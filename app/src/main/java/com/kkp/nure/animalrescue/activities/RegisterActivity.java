package com.kkp.nure.animalrescue.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.kkp.nure.animalrescue.R;
import com.kkp.nure.animalrescue.api.ApiClient;
import com.kkp.nure.animalrescue.api.requests.RegisterRequest;
import com.kkp.nure.animalrescue.api.responses.AuthResponse;
import com.kkp.nure.animalrescue.api.services.AuthService;

public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        EditText firstNameEdit = findViewById(R.id.edit_first_name);
        EditText lastNameEdit = findViewById(R.id.edit_last_name);
        EditText emailEdit = findViewById(R.id.edit_email);
        EditText passwordEdit = findViewById(R.id.edit_password);
        EditText confirmEdit = findViewById(R.id.edit_confirm_password);
        Button registerButton = findViewById(R.id.button_register);
        TextView loginLink = findViewById(R.id.text_login_link);

        AuthService authService = ApiClient.getClient().create(AuthService.class);
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);

        registerButton.setOnClickListener(v -> {
            String first = firstNameEdit.getText().toString();
            String last = lastNameEdit.getText().toString();
            String email = emailEdit.getText().toString();
            String password = passwordEdit.getText().toString();
            String confirm = confirmEdit.getText().toString();

            if (!password.equals(confirm)) {
                Toast.makeText(this, R.string.passwords_do_not_match, Toast.LENGTH_SHORT).show();
                return;
            }

            authService.register(new RegisterRequest(email, password, first, last)).enqueue(new ApiClient.CustomCallback<>() {
                @Override
                public void onResponse(@NonNull AuthResponse response) {
                    prefs.edit().putString("authToken", response.getToken()).apply();
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    finish();
                }

                @Override
                public void onError(@NonNull String error, int code) {
                    Toast.makeText(RegisterActivity.this, getString(R.string.failed_to_register, error), Toast.LENGTH_SHORT).show();
                }
            });
        });

        loginLink.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }
}