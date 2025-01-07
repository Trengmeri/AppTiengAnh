package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class GrammarQuestionActivity extends AppCompatActivity {
    private String correctAnswer = "internet"; // Giả sử dữ liệu đúng từ backend
    private String userAnswer = ""; // Đáp án mà người dùng chọn
    private ProgressBar progressBar;
    private int progressStatus = 0; // Trạng thái tiến trình

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grammar_question);

        // Ánh xạ các thành phần UI
        Button btnAnswer1 = findViewById(R.id.btnOption1);
        Button btnAnswer2 = findViewById(R.id.btnOption2);
        Button btnAnswer3 = findViewById(R.id.btnOption3);
        Button btnAnswer4 = findViewById(R.id.btnOption4);
        Button btnCheckAnswer = findViewById(R.id.btnCheckAnswer);
        TextView tvContent = findViewById(R.id.tvContent);
        progressBar = findViewById(R.id.progressBar); // Ánh xạ ProgressBar

        /*// Lấy nội dung câu hỏi từ API
        loadQuestion();*/

        // Thiết lập sự kiện click cho từng đáp án
        btnAnswer1.setOnClickListener(v -> userAnswer = btnAnswer1.getText().toString());
        btnAnswer2.setOnClickListener(v -> userAnswer = btnAnswer2.getText().toString());
        btnAnswer3.setOnClickListener(v -> userAnswer = btnAnswer3.getText().toString());
        btnAnswer4.setOnClickListener(v -> userAnswer = btnAnswer4.getText().toString());

        // ... existing code ...

        btnCheckAnswer.setOnClickListener(v -> {

            if (userAnswer.isEmpty()) {
                Toast.makeText(GrammarQuestionActivity.this, "Vui lòng trả lời câu hỏi!", Toast.LENGTH_SHORT).show();
            } else {
                // Truyền view cụ thể vào PopupHelper
                PopupHelper.showResultPopup(findViewById(R.id.popupContainer), userAnswer, correctAnswer, () -> {
                    // Tăng giá trị ProgressBar
                    progressStatus += 20;
                    progressBar.setProgress(progressStatus);
                    userAnswer = "";

                    // Kiểm tra xem ProgressBar đã đạt 100 chưa
                    if (progressStatus >= 100) {
                        // Chuyển sang Activity tiếp theo
                        Intent intent = new Intent(GrammarQuestionActivity.this, ListeningQuestionActivity.class);
                        startActivity(intent);
                        finish(); // Kết thúc Activity hiện tại
                    }
                });
            }
        });

// ... existing code ...
    }

    /*private void loadQuestion() {
        // Lấy nội dung câu hỏi từ API
        ApiManager apiManager = new ApiManager();
        apiManager.fetchQuestionContentFromApi(new ApiCallback() {
            @Override
            public void onSuccess(String questionContent) {
                runOnUiThread(() -> {
                    // Cập nhật TextView với nội dung câu hỏi
                    TextView tvContent = findViewById(R.id.tvContent);
                    tvContent.setText(questionContent);
                    // Reset userAnswer
                    userAnswer = "";
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> Toast.makeText(GrammarQuestionActivity.this, errorMessage, Toast.LENGTH_SHORT).show());
            }
        });
    }*/
}