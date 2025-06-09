package com.kkp.nure.animalrescue.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.kkp.nure.animalrescue.R;
import com.kkp.nure.animalrescue.api.ApiClient;
import com.kkp.nure.animalrescue.api.requests.GoogleIdTokenRequest;
import com.kkp.nure.animalrescue.api.requests.LoginRequest;
import com.kkp.nure.animalrescue.api.responses.AuthResponse;
import com.kkp.nure.animalrescue.api.responses.ClientIdResponse;
import com.kkp.nure.animalrescue.api.responses.MfaResponse;
import com.kkp.nure.animalrescue.api.services.AuthService;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {
    ActivityResultLauncher<String> mfaLauncher = registerForActivityResult(new Login2faActivity.MfaContract(), authToken -> {
        if (authToken == null) {
            return;
        }

        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        prefs.edit().putString("authToken", authToken).apply();

        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        EditText emailEdit = findViewById(R.id.edit_email);
        EditText passwordEdit = findViewById(R.id.edit_password);
        Button loginButton = findViewById(R.id.button_login);
        Button loginGoogleButton = findViewById(R.id.button_login_google);
        TextView registerLink = findViewById(R.id.text_register_link);

        AuthService authService = ApiClient.getClient().create(AuthService.class);
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);

        loginButton.setOnClickListener(v -> {
            String email = emailEdit.getText().toString();
            String password = passwordEdit.getText().toString();

            authService.login(new LoginRequest(email, password)).enqueue(new ApiClient.CustomCallback<>() {
                @Override
                public void onResponse(@NonNull AuthResponse response) {
                    prefs.edit().putString("authToken", response.getToken()).apply();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }

                @Override
                public void onError(@NonNull String error, int code) {
                    Toast.makeText(LoginActivity.this, getString(R.string.failed_to_login, error), Toast.LENGTH_SHORT).show();
                }

                @Override
                public boolean onParseError(String body) {
                    MfaResponse mfaResp;
                    try {
                        mfaResp = new ObjectMapper().readValue(body, MfaResponse.class);
                    } catch (JsonProcessingException e) {
                        return false;
                    }

                    mfaLauncher.launch(mfaResp.getMfaToken());
                    return true;
                }
            });
        });

        registerLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });

        loginGoogleButton.setOnClickListener(v -> authService.getGoogleClientId().enqueue(new ApiClient.CustomCallback<>() {
            @Override
            public void onResponse(@NonNull ClientIdResponse response) {
                CredentialManager credentialManager = CredentialManager.create(LoginActivity.this);

                GetGoogleIdOption googleIdTokenRequest = new GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(false)
                        .setServerClientId(response.getClientId())
                        .setAutoSelectEnabled(false)
                        .build();

                GetCredentialRequest request = new GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdTokenRequest)
                        .build();

                credentialManager.getCredentialAsync(LoginActivity.this, request, null, executor, new CredentialManagerCallback<>() {
                    @Override
                    public void onResult(GetCredentialResponse getCredentialResponse) {
                        Credential credential = getCredentialResponse.getCredential();
                        if(!(credential instanceof GoogleIdTokenCredential)){
                            return;
                        }

                        GoogleIdTokenCredential idToken = (GoogleIdTokenCredential)credential;

                        runOnUiThread(() -> authService.loginWithGoogleIdToken(new GoogleIdTokenRequest(idToken.getIdToken())).enqueue(new ApiClient.CustomCallback<>() {
                            @Override
                            public void onResponse(@NonNull AuthResponse response) {
                                prefs.edit().putString("authToken", response.getToken()).apply();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            }

                            @Override
                            public void onError(@NonNull String error, int code) {
                                Toast.makeText(LoginActivity.this, getString(R.string.failed_to_login, error), Toast.LENGTH_SHORT).show();
                            }
                        }));
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {

                    }
                });
            }

            @Override
            public void onError(@NonNull String error, int code) {
                Toast.makeText(LoginActivity.this, getString(R.string.failed_to_get_google_client_id, error), Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private final Executor executor = Executors.newSingleThreadExecutor();
}