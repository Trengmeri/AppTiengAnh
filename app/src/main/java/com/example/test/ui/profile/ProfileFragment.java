package com.example.test.ui.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.test.R;
import com.example.test.model.User;

public class ProfileFragment extends Fragment {
    private User user;
    TextView userName, userEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        userEmail= view.findViewById(R.id.userEmail);
//        userName= view.findViewById(R.id.userName);
//
//        String username = user.getName();
//        String email = user.getEmail();
//
//        userName.setText(username);
//        userEmail.setText(email);
    }
}