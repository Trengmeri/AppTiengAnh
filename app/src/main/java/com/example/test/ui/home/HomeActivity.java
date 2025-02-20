package com.example.test.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.test.R;
import com.example.test.ui.home.adapter.MainAdapter;
import com.example.test.api.LessonManager;
import com.example.test.api.QuestionManager;
import com.example.test.api.ResultManager;

public class HomeActivity extends AppCompatActivity {
    ImageView btnstudy,btnexplore,btnprofile, icHome;
    ViewPager2 vpgMain;
    GridLayout bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        vpgMain = findViewById(R.id.vpg_main);
        bottomBar = findViewById(R.id.bottom_bar);

        // Gán Adapter cho ViewPager2
        vpgMain.setAdapter(new MainAdapter(this));
        vpgMain.setCurrentItem(0);

        icHome= bottomBar.findViewById(R.id.ic_home);
        btnexplore= bottomBar.findViewById(R.id.ic_explore);
        btnprofile= bottomBar.findViewById(R.id.ic_profile);

        vpgMain.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateIconColors(position);
            }
        });
        icHome.setOnClickListener(v -> {
            vpgMain.setCurrentItem(0);
        });

        btnexplore.setOnClickListener(v -> {
            vpgMain.setCurrentItem(1);
        });
        btnprofile.setOnClickListener(v -> {
            vpgMain.setCurrentItem(2);
        });

        updateIconColors(0);

    }
    private void updateIconColors(int position) {
        int selectedColor = ContextCompat.getColor(this, R.color.color_selected);
        int unselectedColor = ContextCompat.getColor(this, R.color.color_unselected);

        icHome.setColorFilter(position == 0 ? selectedColor : unselectedColor);
        btnexplore.setColorFilter(position == 1 ? selectedColor : unselectedColor);
        btnprofile.setColorFilter(position == 2 ? selectedColor : unselectedColor);
    }
}