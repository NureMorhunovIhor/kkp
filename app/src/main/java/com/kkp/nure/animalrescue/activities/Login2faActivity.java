package com.kkp.nure.animalrescue.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kkp.nure.animalrescue.R;
import com.kkp.nure.animalrescue.api.ApiClient;
import com.kkp.nure.animalrescue.api.requests.MfaVerifyRequest;
import com.kkp.nure.animalrescue.api.responses.AuthResponse;
import com.kkp.nure.animalrescue.api.services.AuthService;

public class Login2faActivity extends AppCompatActivity {
    private EditText codeEdit;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login2fa);

        codeEdit = findViewById(R.id.edit_2fa_code);
        loginButton = findViewById(R.id.button_login);

        String mfaToken = getIntent().getStringExtra(MfaContract.EXTRA_INPUT);
        if(mfaToken == null) {
            finish();
            return;
        }

        AuthService authService = ApiClient.getClient().create(AuthService.class);

        loginButton.setOnClickListener(v -> {
            String code = codeEdit.getText().toString();

            authService.verifyMfa(new MfaVerifyRequest(code, mfaToken)).enqueue(new ApiClient.CustomCallback<>() {
                @Override
                public void onResponse(@NonNull AuthResponse response) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(MfaContract.EXTRA_OUTPUT, response.getToken());
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }

                @Override
                public void onError(@NonNull String error, int code) {
                    Toast.makeText(Login2faActivity.this, getString(R.string.failed_to_verify_2fa, error), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    public static class MfaContract extends ActivityResultContract<String, String> {

        public static final String EXTRA_INPUT = "mfa_token";
        public static final String EXTRA_OUTPUT = "auth_token";

        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, String input) {
            Intent intent = new Intent(context, Login2faActivity.class);
            intent.putExtra(EXTRA_INPUT, input);
            return intent;
        }

        @Override
        public String parseResult(int resultCode, @Nullable Intent intent) {
            if (resultCode == Activity.RESULT_OK && intent != null) {
                return intent.getStringExtra(EXTRA_OUTPUT);
            }
            return null;
        }
    }
}