package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PointResultActivity extends AppCompatActivity {
    private TextView timeTextView, pointTextView;
    private Button btnReview, btnNext;
    private TextView correctRead, compRead;
    private TextView correctLis, compLis;
    private TextView correctSpeak, compSpeak;
    private TextView correctWrite, compWrite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_result);

        timeTextView = findViewById(R.id.time);
        pointTextView = findViewById(R.id.point);
        btnReview = findViewById(R.id.btnReview);
        btnNext = findViewById(R.id.btnNext);
        correctRead = findViewById(R.id.correct_read);
        compRead = findViewById(R.id.comp_read);
        correctLis = findViewById(R.id.correct_lis);
        compLis = findViewById(R.id.comp_lis);
        correctSpeak = findViewById(R.id.correct_speak);
        compSpeak = findViewById(R.id.comp_speak);
        correctWrite = findViewById(R.id.correct_write);
        compWrite = findViewById(R.id.comp_write);

        // Giả sử dữ liệu sẽ được nhận từ API hoặc Intent
        String time = getIntent().getStringExtra("TIME");
        String points = getIntent().getStringExtra("POINTS");

        // Hiển thị thời gian và điểm
        timeTextView.setText(time);
        pointTextView.setText(points);

        // Sự kiện cho nút Review
        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý sự kiện Review
                Toast.makeText(PointResultActivity.this, "Review button clicked", Toast.LENGTH_SHORT).show();
            }
        });

        // Sự kiện cho nút Next
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang Activity tiếp theo
                Intent intent = new Intent(PointResultActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }
}
