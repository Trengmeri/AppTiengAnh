package com.example.test.ui.explore;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.test.R;
import com.example.test.ui.GroupFlashcardActivity;
import com.example.test.ui.ScheduleActivity;
import com.example.test.ui.home.HomeActivity;

public class ExploreFragment extends Fragment {
    private ImageView btnstudy,btnhome,btnprofile;
    LinearLayout btnChat, btnDic, btnVoice, btnFlash, btnSche;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_explore, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnstudy = view.findViewById(R.id.ic_study);
        btnhome = view.findViewById(R.id.ic_home);
        btnprofile = view.findViewById(R.id.ic_profile);
        btnFlash= view.findViewById(R.id.btnFlash);
        btnSche= view.findViewById(R.id.btnSche);

//        btnhome.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), HomeActivity.class);
//                startActivity(intent);
//            }
//        });

        btnFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GroupFlashcardActivity.class);
                startActivity(intent);
            }
        });
        btnSche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ScheduleActivity.class);
                startActivity(intent);
            }
        });
    }
}
