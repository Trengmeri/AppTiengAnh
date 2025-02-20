package com.example.test.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.test.R;
import com.example.test.ui.home.HomeActivity;

public class PointResultLessonActivity extends AppCompatActivity {

    Button btnDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_point_result_lesson);
        AnhXa();
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PointResultLessonActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void AnhXa(){
        btnDone = findViewById(R.id.btnDone);
    }
}