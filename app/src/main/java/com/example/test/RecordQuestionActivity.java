package com.example.test;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecordQuestionActivity extends AppCompatActivity {
    List<String> correctAnswers = new ArrayList<>();
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private ImageView imgVoice, btnPlayAudio;
    private TextView tvTranscription;
    private SpeechRecognizer speechRecognizer;

    //private String correctAnswer = "u i a fein"; // Đáp án đúng
    String userAnswer = "";
    private int currentStep = 0; // Bước hiện tại (bắt đầu từ 0)
    private int totalSteps = 5; // Tổng số bước trong thanh tiến trình


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_question);

        imgVoice = findViewById(R.id.imgVoice);
        tvTranscription = findViewById(R.id.tvTranscription);
        btnPlayAudio = findViewById(R.id.btn_play);
        Button btnCheckResult = findViewById(R.id.btnCheckResult);
        seekBar = findViewById(R.id.seekBar);

        // Kiểm tra quyền microphone
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        } else {
            // Speech-to-text setup
            imgVoice.setOnClickListener(v -> startSpeechRecognition());
        }

        // Audio playback setup
        mediaPlayer = MediaPlayer.create(this, R.raw.uiafein); // Thay thế với tệp âm thanh của bạn
        btnPlayAudio.setOnClickListener(v -> {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                updateSeekBar();
            }
        });

        mediaPlayer.setOnCompletionListener(mp -> seekBar.setProgress(0));

        LinearLayout progressBar = findViewById(R.id.progressBar); // Ánh xạ ProgressBar

        //btnListen.setOnClickListener(v -> playAudio());

        btnCheckResult.setOnClickListener(v -> {
            String userAnswer = tvTranscription.getText().toString(); // Lấy giá trị từ EditText

            if (userAnswer.isEmpty()) {
                Toast.makeText(RecordQuestionActivity.this, "Vui lòng trả lời câu hỏi!", Toast.LENGTH_SHORT).show();
            } else {
                // Truyền view cụ thể vào PopupHelper
                PopupHelper.showResultPopup(findViewById(R.id.popupContainer), userAnswer, correctAnswers, () -> {
                    // Callback khi nhấn Next Question trên popup
                    updateProgressBar(progressBar, currentStep);
                    currentStep++; // Cập nhật thanh tiến trình

                    // Kiểm tra nếu hoàn thành
                    if (currentStep >= totalSteps) {
                        Intent intent = new Intent(RecordQuestionActivity.this, PointResultActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
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

    private void startSpeechRecognition() {
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        }

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                Toast.makeText(RecordQuestionActivity.this, "Listening...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBeginningOfSpeech() {
                // Gọi khi bắt đầu nhận diện giọng nói
            }

            @Override
            public void onRmsChanged(float rmsdB) {
                // Gọi khi thay đổi âm lượng
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                // Gọi khi nhận được dữ liệu
            }

            @Override
            public void onEndOfSpeech() {
                Toast.makeText(RecordQuestionActivity.this, "Processing...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int error) {
                Log.e("SpeechRecognizerError", "Error code: " + error);
                Toast.makeText(RecordQuestionActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    tvTranscription.setText(matches.get(0));
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                // Gọi khi nhận được kết quả từng phần
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                // Gọi khi có sự kiện tùy chỉnh
            }
        });

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.startListening(intent);
    }

    private void updateSeekBar() {
        seekBar.setMax(mediaPlayer.getDuration());
        new Thread(() -> {
            while (mediaPlayer.isPlaying()) {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
            speechRecognizer = null;
        }
        super.onDestroy();
    }

    // Xử lý yêu cầu quyền
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startSpeechRecognition();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }
}
