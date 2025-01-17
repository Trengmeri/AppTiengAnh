package com.example.test;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomeActivity extends AppCompatActivity {

    Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home); // Sử dụng layout mà bạn đã cung cấp

        // Tìm button "Continue Studying"
        continueButton = findViewById(R.id.btn_continue);


        // Thiết lập sự kiện click cho nút
        continueButton.setOnClickListener(v -> {
            // Hiển thị một thông báo khi nút được nhấn
            Toast.makeText(HomeActivity.this, "Continue studying clicked!", Toast.LENGTH_SHORT).show();
        });
    }
}
