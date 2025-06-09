package com.kkp.nure.animalrescue.activities;

import static com.kkp.nure.animalrescue.utils.UriUtil.readUriToByteArray;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.kkp.nure.animalrescue.R;
import com.kkp.nure.animalrescue.api.ApiClient;
import com.kkp.nure.animalrescue.api.requests.Disable2faRequest;
import com.kkp.nure.animalrescue.api.requests.Enable2faRequest;
import com.kkp.nure.animalrescue.api.requests.UpdateUserInfoRequest;
import com.kkp.nure.animalrescue.api.services.MediaService;
import com.kkp.nure.animalrescue.api.services.UserService;
import com.kkp.nure.animalrescue.entities.Media;
import com.kkp.nure.animalrescue.entities.User;
import com.kkp.nure.animalrescue.enums.UserRole;
import com.kkp.nure.animalrescue.utils.GlideMediaUrl;

import java.util.Random;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TOTP_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

    private static final int REQUEST_PICK_IMAGE = 100;
    private ImageView avatarImage;
    private EditText firstNameEdit, lastNameEdit, emailEdit, telegramUsername, viberPhone, whatsappPhone;
    Button saveButton;
    Button applyForVolunteer;
    private Switch switch2FA;
    private byte[] avatarToUpload = null;

    private UserService userService;
    private MediaService mediaService;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        if(prefs.getString("authToken", null) == null) {
            startActivity(new Intent(EditProfileActivity.this, LoginActivity.class));
            finish();
            return;
        }

        String token = prefs.getString("authToken", null);
        userService = ApiClient.getAuthClient(token).create(UserService.class);
        mediaService = ApiClient.getAuthClient(token).create(MediaService.class);
        userService.getUser().enqueue(new ApiClient.CustomCallback<>() {
            @Override
            public void onResponse(@NonNull User response) {
                currentUser = response;
                updateLayout();
            }

            @Override
            public void onError(@NonNull String error, int code) {
                if(code == 401) {
                    prefs.edit().remove("authToken").apply();
                    startActivity(new Intent(EditProfileActivity.this, LoginActivity.class));
                    finish();
                    return;
                }
                Toast.makeText(EditProfileActivity.this, getString(R.string.failed_to_fetch_user_fmt, error), Toast.LENGTH_SHORT).show();
            }
        });

        avatarImage = findViewById(R.id.image_avatar);
        firstNameEdit = findViewById(R.id.edit_first_name);
        lastNameEdit = findViewById(R.id.edit_last_name);
        emailEdit = findViewById(R.id.edit_email);
        telegramUsername = findViewById(R.id.edit_telegram_username);
        viberPhone = findViewById(R.id.edit_viber_phone);
        whatsappPhone = findViewById(R.id.edit_whatsapp_phone);
        switch2FA = findViewById(R.id.switch_2fa);
        saveButton = findViewById(R.id.button_save);
        applyForVolunteer = findViewById(R.id.button_volunteer_apply);

        updateLayout();

        avatarImage.setOnClickListener(v -> openImagePicker());

        switch2FA.setOnClickListener((buttonView) -> {
            if (switch2FA.isChecked()) {
                enableTOTP();
            } else {
                disableTOTP();
            }
        });

        saveButton.setOnClickListener(v -> saveProfile());

        applyForVolunteer.setOnClickListener(v -> {
            startActivity(new Intent(EditProfileActivity.this, RequestVolunteerStatusActivity.class));
        });
    }

    private void updateLayout() {
        saveButton.setEnabled(currentUser != null);
        applyForVolunteer.setEnabled(currentUser != null);

        if(currentUser == null) {
            return;
        }

        firstNameEdit.setText(currentUser.getFirstName());
        lastNameEdit.setText(currentUser.getLastName());
        emailEdit.setText(currentUser.getEmail());
        switch2FA.setChecked(currentUser.isMfaEnabled());

        if(currentUser.getRole() == UserRole.REGULAR) {
            applyForVolunteer.setVisibility(ImageView.VISIBLE);
        } else {
            applyForVolunteer.setVisibility(ImageView.GONE);
        }

        if(currentUser.getPhoto() != null) {
            Glide.with(EditProfileActivity.this)
                    .load(new GlideMediaUrl(currentUser.getPhoto()))
                    .placeholder(R.drawable.baseline_hide_image_24)
                    .into(avatarImage);
        }

        telegramUsername.setText(currentUser.getTelegramUsername() == null ? "" : currentUser.getTelegramUsername());
        viberPhone.setText(currentUser.getViberPhone() == null ? "" : currentUser.getViberPhone());
        whatsappPhone.setText(currentUser.getWhatsappPhone() == null ? "" : currentUser.getWhatsappPhone());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_avatar)), REQUEST_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            avatarToUpload = readUriToByteArray(EditProfileActivity.this, data.getData());
            if(avatarToUpload == null) {
                Toast.makeText(EditProfileActivity.this, R.string.failed_to_read_image, Toast.LENGTH_SHORT).show();
                return;
            }

            Bitmap bmp = BitmapFactory.decodeByteArray(avatarToUpload, 0, avatarToUpload.length);
            avatarImage.setImageBitmap(Bitmap.createScaledBitmap(bmp, avatarImage.getWidth(), avatarImage.getHeight(), false));
        }
    }

    private String genTOTPSecret() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for(int i = 0; i < 16; i++) {
            sb.append(TOTP_ALPHABET.charAt(random.nextInt(TOTP_ALPHABET.length())));
        }

        return sb.toString();
    }

    private static Bitmap encodeQrAsBitmap(String str) throws WriterException {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode(str, BarcodeFormat.QR_CODE, 400, 400);

        int w = bitMatrix.getWidth();
        int h = bitMatrix.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                pixels[y * w + x] = bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
        return bitmap;
    }

    private void enableTOTP() {
        switch2FA.setChecked(false);

        String secret = genTOTPSecret();

        AlertDialog.Builder dialog = new AlertDialog.Builder(EditProfileActivity.this);
        dialog.setTitle(R.string.enable_2fa);

        String otpauthUrl = "otpauth://totp/"+currentUser.getEmail()+"?secret="+secret+"&issuer=AnimalRescue";

        Bitmap qr;
        try {
            qr = encodeQrAsBitmap(otpauthUrl);
        } catch (WriterException e) {
            Log.e("AnimalRescue", "Failed to create 2fa qr code", e);
            return;
        }

        LinearLayout layout = new LinearLayout(EditProfileActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        ImageView qrImage = new ImageView(EditProfileActivity.this);
        qrImage.setImageBitmap(qr);
        qrImage.setOnClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(otpauthUrl));
            startActivity(browserIntent);
        });
        layout.addView(qrImage);

        EditText secretInput = new EditText(EditProfileActivity.this);
        secretInput.setEnabled(false);
        secretInput.setText(secret);
        layout.addView(secretInput);

        EditText passwordInput = new EditText(EditProfileActivity.this);
        passwordInput.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordInput.setHint(R.string.password);
        layout.addView(passwordInput);

        EditText codeInput = new EditText(EditProfileActivity.this);
        codeInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        codeInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
        codeInput.setHint(R.string._6_digit_code);
        layout.addView(codeInput);

        dialog.setView(layout);

        dialog.setPositiveButton(R.string.enable, (di, arg1) -> {
            String password = passwordInput.getText().toString();
            String code = codeInput.getText().toString();

            userService.enable2fa(new Enable2faRequest(password, secret, code)).enqueue(new ApiClient.CustomCallback<>() {
                @Override
                public void onResponse(@NonNull User response) {
                    currentUser = response;
                    updateLayout();
                    di.dismiss();
                }

                @Override
                public void onError(@NonNull String error, int code) {
                    Toast.makeText(EditProfileActivity.this, getString(R.string.failed_to_enable_2fa, error), Toast.LENGTH_SHORT).show();
                }
            });
        });
        dialog.setNegativeButton(R.string.close, (di, arg1) -> di.dismiss());

        dialog.show();
    }

    private void disableTOTP() {
        switch2FA.setChecked(true);

        AlertDialog.Builder dialog = new AlertDialog.Builder(EditProfileActivity.this);
        dialog.setTitle(R.string.disable_2fa);

        LinearLayout layout = new LinearLayout(EditProfileActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        EditText passwordInput = new EditText(EditProfileActivity.this);
        passwordInput.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordInput.setHint(R.string.password);
        layout.addView(passwordInput);

        EditText codeInput = new EditText(EditProfileActivity.this);
        codeInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        codeInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
        codeInput.setHint(R.string._6_digit_code);
        layout.addView(codeInput);

        dialog.setView(layout);

        dialog.setPositiveButton(R.string.disable, (di, arg1) -> {
            String password = passwordInput.getText().toString();
            String code = codeInput.getText().toString();

            userService.disable2fa(new Disable2faRequest(password, code)).enqueue(new ApiClient.CustomCallback<>() {
                @Override
                public void onResponse(@NonNull User response) {
                    currentUser = response;
                    updateLayout();
                    di.dismiss();
                }

                @Override
                public void onError(@NonNull String error, int code) {
                    Toast.makeText(EditProfileActivity.this, getString(R.string.failed_to_disable_2fa, error), Toast.LENGTH_SHORT).show();
                }
            });
        });
        dialog.setNegativeButton(R.string.close, (di, arg1) -> di.dismiss());

        dialog.show();
    }

    private void editUser(String first, String last, String email, Long photoId) {
        String telegramText = telegramUsername.getText().toString().trim();
        String viberText = viberPhone.getText().toString().trim();
        String whatsappText = whatsappPhone.getText().toString().trim();


        userService.editUser(new UpdateUserInfoRequest(first, last, email, photoId, telegramText, viberText, whatsappText)).enqueue(new ApiClient.CustomCallback<>() {
            @Override
            public void onResponse(@NonNull User response) {
                currentUser = response;
                updateLayout();
                Toast.makeText(EditProfileActivity.this, R.string.profile_updated, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(@NonNull String error, int code) {
                Toast.makeText(EditProfileActivity.this, getString(R.string.failed_to_update_profile, error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfile() {
        String first = firstNameEdit.getText().toString().trim();
        String last = lastNameEdit.getText().toString().trim();
        String email = emailEdit.getText().toString().trim();

        if(avatarToUpload != null) {
            new MediaService.Uploader(mediaService).uploadPhoto(avatarToUpload, new MediaService.PhotoUploadedCallback() {
                @Override
                public void onSuccess(Media media) {
                    editUser(first, last, email, media.getId());
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(EditProfileActivity.this, getString(R.string.failed_to_upload_photo_fmt, error), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            editUser(first, last, email, null);
        }
    }
}