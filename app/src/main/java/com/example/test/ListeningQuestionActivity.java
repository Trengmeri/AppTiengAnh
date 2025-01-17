package com.example.test;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private String correctAnswer = "u i a fein"; // Đáp án đúng
    String userAnswer = "";
    private int currentStep = 0; // Bước hiện tại (bắt đầu từ 0)
    private int totalSteps = 5; // Tổng số bước trong thanh tiến trình

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_listening_question);

        ImageView btnListen = findViewById(R.id.btnListen);
        Button btnCheckResult = findViewById(R.id.btnCheckResult);
        etAnswer = findViewById(R.id.etAnswer);

        LinearLayout progressBar = findViewById(R.id.progressBar); // Ánh xạ ProgressBar

        //btnListen.setOnClickListener(v -> playAudio());

        btnCheckResult.setOnClickListener(v -> {
            String userAnswer = etAnswer.getText().toString(); // Lấy giá trị từ EditText

            if (userAnswer.isEmpty()) {
                Toast.makeText(ListeningQuestionActivity.this, "Vui lòng trả lời câu hỏi!", Toast.LENGTH_SHORT).show();
            } else {
                // Truyền view cụ thể vào PopupHelper
                PopupHelper.showResultPopup(findViewById(R.id.popupContainer), userAnswer, correctAnswer, () -> {
                    // Callback khi nhấn Next Question trên popup
                    updateProgressBar(progressBar, currentStep);
                    currentStep++; // Cập nhật thanh tiến trình

                    // Kiểm tra nếu hoàn thành
                    if (currentStep >= totalSteps) {
                        Intent intent = new Intent(ListeningQuestionActivity.this, RecordQuestionActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
    }
//    private void fetchAudioUrl() {
//        ApiManager apiManager = new ApiManager();
//
//        // Gọi phương thức fetchAudioUrl từ ApiManager
//        apiManager.fetchAudioUrl(apiUrl, new ApiManager.ApiCallback() {
//            @Override
//            public void onSuccess(String audioUrl) {
//                // Khi nhận được URL âm thanh từ API, gọi phương thức playAudio
//                playAudio(audioUrl);
//            }
//
//            @Override
//            public void onFailure(String errorMessage) {
//                // Hiển thị thông báo lỗi nếu có
//                runOnUiThread(() -> Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show());
//            }
//        });
//        private void playAudio(String audioUrl) {
//            try {
//                // Tạo MediaPlayer để phát âm thanh
//                mediaPlayer = new MediaPlayer();
//                mediaPlayer.setDataSource(audioUrl); // Đường dẫn đến file âm thanh
//                mediaPlayer.prepare(); // Chuẩn bị phát
//                mediaPlayer.start(); // Bắt đầu phát âm thanh
//            } catch (Exception e) {
//                e.printStackTrace();
//                Toast.makeText(this, "Không thể phát âm thanh", Toast.LENGTH_SHORT).show();
//            }
//        }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    private void updateProgressBar(LinearLayout progressBarSteps, int step) {
        if (step < progressBarSteps.getChildCount()) {
            final View currentStepView = progressBarSteps.getChildAt(step);

            // Animation thay đổi màu
            ObjectAnimator colorAnimator = ObjectAnimator.ofArgb(
                    currentStepView,
                    "backgroundColor",
                    Color.parseColor("#E0E0E0"),  // Màu ban đầu
                    Color.parseColor("#C4865E")   // Màu đã hoàn thành
            );
            colorAnimator.setDuration(200); // Thời gian chuyển đổi màu
            colorAnimator.start();
        }
    }
}