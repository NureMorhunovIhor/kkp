package com.kkp.nure.animalrescue.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.kkp.nure.animalrescue.R;
import com.kkp.nure.animalrescue.activities.DonationGoalsActivity;
import com.kkp.nure.animalrescue.activities.EditProfileActivity;
import com.kkp.nure.animalrescue.activities.LoginActivity;
import com.kkp.nure.animalrescue.activities.RecentReportsActivity;
import com.kkp.nure.animalrescue.activities.TreatingAnimalsActivity;
import com.kkp.nure.animalrescue.entities.User;
import com.kkp.nure.animalrescue.enums.UserRole;
import com.kkp.nure.animalrescue.utils.GlideMediaUrl;

public class AccountFragment extends CustomFragment {

    private ImageView avatarImageView;
    private TextView userNameTextView;
    private Button editProfileButton;
    private Button treatingAnimalsButton, recentReportsButton;

    public AccountFragment() {
        super(R.layout.fragment_account);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        avatarImageView = view.findViewById(R.id.image_avatar);
        userNameTextView = view.findViewById(R.id.text_user_name);
        editProfileButton = view.findViewById(R.id.button_edit_profile);
        treatingAnimalsButton = view.findViewById(R.id.button_animals_treating);
        recentReportsButton = view.findViewById(R.id.button_recent_reports);
        Button donateButton = view.findViewById(R.id.button_donate);

        Button logoutButton = view.findViewById(R.id.button_logout);

        updateLayout();

        editProfileButton.setOnClickListener(v -> startActivity(new Intent(getContext(), EditProfileActivity.class)));
        treatingAnimalsButton.setOnClickListener(v -> startActivity(new Intent(getContext(), TreatingAnimalsActivity.class)));
        recentReportsButton.setOnClickListener(v -> startActivity(new Intent(getContext(), RecentReportsActivity.class)));

        logoutButton.setOnClickListener(v -> {
            SharedPreferences prefs = requireContext().getSharedPreferences("app_prefs", MODE_PRIVATE);
            prefs.edit().remove("authToken").apply();

            startActivity(new Intent(getContext(), LoginActivity.class));
            requireActivity().finish();
        });

        donateButton.setOnClickListener(v -> startActivity(new Intent(requireActivity(), DonationGoalsActivity.class)));
    }

    private void updateLayout() {
        User currentUser = getCurrentUser();
        editProfileButton.setEnabled(currentUser != null);
        treatingAnimalsButton.setEnabled(currentUser != null);
        treatingAnimalsButton.setEnabled(currentUser != null);

        if(currentUser == null) {
            return;
        }

        userNameTextView.setText(requireContext().getString(R.string.full_name_fmt, currentUser.getFirstName(), currentUser.getLastName()));

        if (currentUser.getRole() == UserRole.VET || currentUser.getRole() == UserRole.VOLUNTEER) {
            treatingAnimalsButton.setVisibility(View.VISIBLE);
            recentReportsButton.setVisibility(View.VISIBLE);
        }

        if(currentUser.getPhoto() != null) {
            Glide.with(AccountFragment.this)
                    .load(new GlideMediaUrl(currentUser.getPhoto()))
                    .placeholder(R.drawable.baseline_hide_image_24)
                    .into(avatarImageView);
        }
    }

    @Override
    public void setCurrentUser(User user) {
        super.setCurrentUser(user);
        updateLayout();
    }
}
