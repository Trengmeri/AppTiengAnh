package com.example.test.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.test.R;

public class CourseActivity extends AppCompatActivity {

    AppCompatButton btnAbout, btnLesson;
    LinearLayout contentAbout, contentLes;
    ImageView btnLike,btnLike1;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_course);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnAbout= findViewById(R.id.btnAbout);
        btnLesson= findViewById(R.id.btnLesson);
        contentAbout = findViewById(R.id.contentAbout);
        contentLes = findViewById(R.id.contentLes);

        btnAbout.setOnClickListener(v -> {
            btnAbout.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_about));
            btnLesson.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_lesson));
            contentAbout.setVisibility(View.VISIBLE);
            contentLes.setVisibility(View.GONE);
        });

        btnLesson.setOnClickListener(v -> {
            btnLesson.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_about));
            btnAbout.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_lesson));
            contentAbout.setVisibility(View.GONE);
            contentLes.setVisibility(View.VISIBLE);
        });

        btnLike = findViewById(R.id.btnLike);
        btnLike1 = findViewById(R.id.btnLike1);
        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected()); // Đổi trạng thái selected
            }
        });
        btnLike1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected()); // Đổi trạng thái selected
            }
        });


    }
}