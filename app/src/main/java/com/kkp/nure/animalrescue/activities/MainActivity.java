package com.kkp.nure.animalrescue.activities;

import static com.kkp.nure.animalrescue.utils.AndroidUtils.getTokenOrDie;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kkp.nure.animalrescue.R;
import com.kkp.nure.animalrescue.api.ApiClient;
import com.kkp.nure.animalrescue.api.requests.RegisterFcmRequest;
import com.kkp.nure.animalrescue.api.requests.UpdateLocationRequest;
import com.kkp.nure.animalrescue.api.services.UserService;
import com.kkp.nure.animalrescue.entities.User;
import com.kkp.nure.animalrescue.enums.UserRole;
import com.kkp.nure.animalrescue.fragments.AccountFragment;
import com.kkp.nure.animalrescue.fragments.CustomFragment;
import com.kkp.nure.animalrescue.fragments.MessagesFragment;
import com.kkp.nure.animalrescue.fragments.MyAnimalsFragment;
import com.kkp.nure.animalrescue.fragments.ReportAnimalFragment;
import com.kkp.nure.animalrescue.fragments.ScanQrFragment;

import org.osmdroid.config.Configuration;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSION_LOCATION = 201;

    private UserService userService;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Configuration.getInstance().setUserAgentValue("AnimalRescue/1.0");

        String token = getTokenOrDie(this);
        if(token == null) return;

        userService = ApiClient.getAuthClient(token).create(UserService.class);
        userService.getUser().enqueue(new ApiClient.CustomCallback<>() {
            @Override
            public void onResponse(@NonNull User response) {
                currentUser = response;
                onGotUser();
            }

            @Override
            public void onError(@NonNull String error, int code) {
                if(code == 401) {
                    SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
                    prefs.edit().remove("authToken").apply();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                    return;
                }
                Toast.makeText(MainActivity.this, getString(R.string.failed_to_fetch_user_fmt, error), Toast.LENGTH_SHORT).show();
            }
        });

        if (savedInstanceState == null) {
            goToFragment(AccountFragment.class);
        }

        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_account);
        nav.setOnItemSelectedListener(item -> {
            final int id = item.getItemId();
            if(id == R.id.nav_messages) {
                goToFragment(MessagesFragment.class);
                return true;
            } else if(id == R.id.nav_my_animals) {
                goToFragment(MyAnimalsFragment.class);
                return true;
            } else if(id == R.id.nav_qr_scanner) {
                goToFragment(ScanQrFragment.class);
                return true;
            } else if(id == R.id.nav_report) {
                goToFragment(ReportAnimalFragment.class);
                return true;
            } else if(id == R.id.nav_account) {
                goToFragment(AccountFragment.class);
                return true;
            }

            return false;
        });

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w("fcm", "Fetching FCM registration token failed", task.getException());
                return;
            }

            String fcmToken = task.getResult();

            userService.registerDevice(new RegisterFcmRequest(fcmToken)).enqueue(new ApiClient.CustomCallback<>() {
                @Override
                public void onResponse(@NonNull Void response) {
                }

                @Override
                public void onError(@NonNull String error, int code) {
                }
            });

            Log.d("fcm", fcmToken);
        });


    }

    private void onGotUser() {
        setUserInFragments();

        if(currentUser != null && (currentUser.getRole() == UserRole.VET || currentUser.getRole() == UserRole.VOLUNTEER)) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_PERMISSION_LOCATION);
            }
        }
    }

    private void setUserInFragments() {
        for(Fragment fragment : getSupportFragmentManager().getFragments()) {
            if(!(fragment instanceof CustomFragment)) {
                continue;
            }

            ((CustomFragment)fragment).setCurrentUser(currentUser);
        }
    }

    private void goToFragment(Class<? extends Fragment> cls) {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container, cls, null)
                .runOnCommit(this::setUserInFragments)
                .commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_PERMISSION_LOCATION && resultCode == Activity.RESULT_OK) {
            getCurrentLocation();
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        locationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location == null) {
                Toast.makeText(MainActivity.this, R.string.unable_to_get_location, Toast.LENGTH_SHORT).show();
                return;
            }

            userService.sendLocation(new UpdateLocationRequest(location.getLatitude(), location.getLongitude())).enqueue(new ApiClient.CustomCallback<>() {
                @Override
                public void onResponse(@NonNull Void response) {
                }

                @Override
                public void onError(@NonNull String error, int code) {
                }
            });
        });
    }
}