package com.kkp.nure.animalrescue.fragments;

import androidx.annotation.ContentView;
import androidx.annotation.LayoutRes;
import androidx.fragment.app.Fragment;

import com.kkp.nure.animalrescue.entities.User;

public class CustomFragment extends Fragment {
    private User currentUser;

    @ContentView
    public CustomFragment(@LayoutRes int contentLayoutId) {
        super(contentLayoutId);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        currentUser = user;
    }
}
