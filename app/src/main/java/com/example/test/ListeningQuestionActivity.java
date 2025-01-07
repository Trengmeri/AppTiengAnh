package com.example.test;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ListeningQuestionActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private EditText etAnswer;
    private String correctAnswer = "expected answer"; // Đáp án đúng
    String userAnswer = "";
    private ProgressBar progressBar;
    private int progressStatus = 0; // Trạng thái tiến trình

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_listening_question);

        ImageView btnListen = findViewById(R.id.btnListen);
        Button btnCheckResult = findViewById(R.id.btnCheckResult);
        etAnswer = findViewById(R.id.etAnswer);

        progressBar = findViewById(R.id.progressBar); // Ánh xạ ProgressBar

        btnListen.setOnClickListener(v -> playAudio());

        btnCheckResult.setOnClickListener(v -> {
            String userAnswer = etAnswer.getText().toString(); // Lấy giá trị từ EditText

            if (userAnswer.isEmpty()) {
                Toast.makeText(ListeningQuestionActivity.this, "Vui lòng trả lời câu hỏi!", Toast.LENGTH_SHORT).show();
            } else {
                // Truyền view cụ thể vào PopupHelper
                PopupHelper.showResultPopup(findViewById(R.id.popupContainer), userAnswer, correctAnswer, () -> {
                    // Tăng giá trị ProgressBar
                    progressStatus += 20;
                    progressBar.setProgress(progressStatus);
                    etAnswer.setText("");

                    // Kiểm tra xem ProgressBar đã đạt 100 chưa
                    if (progressStatus >= 100) {
                        // Chuyển sang Activity tiếp theo
                        Intent intent = new Intent(ListeningQuestionActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish(); // Kết thúc Activity hiện tại
                    }
                });
            }
        });
    }

    private void playAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(this, R.raw.uiafein); // Thay 'your_audio_file' bằng tên file âm thanh của bạn
        mediaPlayer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}