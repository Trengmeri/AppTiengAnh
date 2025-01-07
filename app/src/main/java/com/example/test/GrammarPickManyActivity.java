package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class GrammarPickManyActivity extends AppCompatActivity {
    private String correctAnswers = "JavaPythonC#"; // Đáp án đúng
    private ProgressBar progressBar;
    private int progressStatus = 0; // Trạng thái tiến trình
    private String username = "";

    int correctCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grammar_pick_many);

        // Ánh xạ các thành phần UI
        CheckBox checkboxJava = findViewById(R.id.checkboxJava);
        CheckBox checkboxPython = findViewById(R.id.checkboxPython);
        CheckBox checkboxCSharp = findViewById(R.id.checkboxCSharp);
        CheckBox checkboxHTML = findViewById(R.id.checkboxHTML);
        CheckBox checkboxJavaScript = findViewById(R.id.checkboxJavaScript);
        Button btnCheckAnswers = findViewById(R.id.btnCheckAnswers);
        progressBar = findViewById(R.id.progressBar); // Ánh xạ ProgressBar

        checkboxJava.setOnClickListener(v -> username += checkboxJava.getText().toString());
        checkboxPython.setOnClickListener(v -> username += checkboxPython.getText().toString());
        checkboxCSharp.setOnClickListener(v -> username += checkboxCSharp.getText().toString());
        checkboxHTML.setOnClickListener(v -> username += checkboxHTML.getText().toString());
        checkboxJavaScript.setOnClickListener(v -> username += checkboxJavaScript.getText().toString());

        btnCheckAnswers.setOnClickListener(v -> {
            if (username.isEmpty()) {
                Toast.makeText(GrammarPickManyActivity.this, "Vui lòng trả lời câu hỏi!", Toast.LENGTH_SHORT).show();
            } else {
                PopupHelper.showResultPopup(findViewById(R.id.popupContainer), username, correctAnswers, () -> {
                    // Tăng giá trị ProgressBar
                    progressStatus += 20;
                    progressBar.setProgress(progressStatus);
                    username = "";

                    // Tắt các CheckBox đã chọn
                    checkboxJava.setChecked(false);
                    checkboxPython.setChecked(false);
                    checkboxCSharp.setChecked(false);
                    checkboxHTML.setChecked(false);
                    checkboxJavaScript.setChecked(false);

                    // Kiểm tra xem ProgressBar đã đạt 100 chưa
                    if (progressStatus >= 100) {
                        // Chuyển sang Activity tiếp theo
                        Intent intent = new Intent(GrammarPickManyActivity.this, ListeningQuestionActivity.class);
                        startActivity(intent);
                        finish(); // Kết thúc Activity hiện tại
                    }
                });
            }
        });
    }
}