package com.example.test.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.test.PopupHelper;
import com.example.test.R;

import java.util.ArrayList;
import java.util.List;

public class ListeningQuestionActivity extends AppCompatActivity {
    List<String> correctAnswers = new ArrayList<>();
    private MediaPlayer mediaPlayer;
    private EditText etAnswer;
    //private String correctAnswer = "u i a fein"; // Đáp án đúng
   // String userAnswer = "";
    private List<String> userAnswers = new ArrayList<>();
    private int currentStep = 0; // Bước hiện tại (bắt đầu từ 0)
    private int totalSteps; // Tổng số bước trong thanh tiến trình

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
            Log.d("GrammarPickManyActivity", "User Answers: " + userAnswers);
            if (userAnswers.isEmpty()) {
                Toast.makeText(ListeningQuestionActivity.this, "Vui lòng trả lời câu hỏi!", Toast.LENGTH_SHORT).show();
            } else {
                PopupHelper.showResultPopup(findViewById(R.id.popupContainer), userAnswers, correctAnswers, () -> {
                    userAnswers.clear();
                    currentStep++;
                    Log.d("GrammarPickManyActivity", "Current step: " + currentStep + ", Total steps: " + totalSteps);

                    if (currentStep < totalSteps) {
                    /*    fetchQuestion(questionIds.get(currentStep));*/
                        updateProgressBar(progressBar, currentStep);
                    } else {
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