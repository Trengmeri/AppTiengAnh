package com.example.test;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.test.R;

public class GrammarQuestionActivity extends AppCompatActivity {
    private String correctAnswer = "internet"; // Giả sử dữ liệu đúng từ backend
    private String userAnswer = ""; // Đáp án mà người dùng chọn


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grammar_question);

        // Đáp án đúng
        correctAnswer = "internet";

        // Ánh xạ các nút đáp án
        Button btnAnswer1 = findViewById(R.id.btnOption1);
        Button btnAnswer2 = findViewById(R.id.btnOption2);
        Button btnAnswer3 = findViewById(R.id.btnOption3);
        Button btnAnswer4 = findViewById(R.id.btnOption4);

        // Thiết lập sự kiện click cho từng đáp án
        btnAnswer1.setOnClickListener(v -> userAnswer = btnAnswer1.getText().toString());
        btnAnswer2.setOnClickListener(v -> userAnswer = btnAnswer2.getText().toString());
        btnAnswer3.setOnClickListener(v -> userAnswer = btnAnswer3.getText().toString());
        btnAnswer4.setOnClickListener(v -> userAnswer = btnAnswer4.getText().toString());

        Button btnCheckAnswer = findViewById(R.id.btnCheckAnswer);
        btnCheckAnswer.setOnClickListener(v -> {
            if ((userAnswer == "")) {
                Toast.makeText(GrammarQuestionActivity.this, "Vui long tra loi cau hoi!", Toast.LENGTH_SHORT).show();
            } else {
                PopupHelper.showResultPopup(GrammarQuestionActivity.this, userAnswer, correctAnswer);
            }
        });
    }

}

