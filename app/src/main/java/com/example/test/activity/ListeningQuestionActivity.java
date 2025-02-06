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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.test.PopupHelper;
import com.example.test.R;
import com.example.test.api.ApiCallback;
import com.example.test.api.ApiManager;
import com.example.test.api.ApiResponseAnswer;
import com.example.test.model.Answer;
import com.example.test.model.Course;
import com.example.test.model.Lesson;
import com.example.test.model.Question;
import com.example.test.model.QuestionChoice;
import com.example.test.model.Result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListeningQuestionActivity extends AppCompatActivity {
    List<String> correctAnswers = new ArrayList<>();
    private MediaPlayer mediaPlayer;
    private EditText etAnswer;
    private List<Integer> questionIds;
    private List<String> userAnswers = new ArrayList<>();
    private int currentStep = 0; // Bước hiện tại (bắt đầu từ 0)
    private int totalSteps; // Tổng số bước trong thanh tiến trình

    ApiManager apiManager = new ApiManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_listening_question);

        ImageView btnListen = findViewById(R.id.btnListen);
        Button btnCheckResult = findViewById(R.id.btnCheckResult);
        etAnswer = findViewById(R.id.etAnswer);
        int lessonId = 3;
        fetchLessonAndQuestions(lessonId);

        LinearLayout progressBar = findViewById(R.id.progressBar); // Ánh xạ ProgressBar

        //btnListen.setOnClickListener(v -> playAudio());

        btnCheckResult.setOnClickListener(v -> {
            String userAnswer = etAnswer.getText().toString().trim();
            userAnswers.clear(); // Xóa các câu trả lời trước đó
            userAnswers.add(userAnswer); // Thêm câu trả lời mới vào danh sách
            Log.d("ListeningQuestionActivity", "User Answers: " + userAnswers);
            if (userAnswers.isEmpty()) {
                Toast.makeText(ListeningQuestionActivity.this, "Vui lòng trả lời câu hỏi!", Toast.LENGTH_SHORT).show();
            } else {
                // Lưu câu trả lời của người dùng
                apiManager.saveUserAnswer(questionIds.get(currentStep), userAnswers.toString(), new ApiCallback() {

                    @Override
                    public void onSuccess() {
                        Log.e("ListeningQuestionActivity", "Câu trả lời đã được lưu: " + userAnswers.toString());
                        // Hiển thị popup
                        runOnUiThread(() -> {
                            PopupHelper.showResultPopup(findViewById(R.id.popupContainer), userAnswers, correctAnswers, () -> {
                                // Callback khi nhấn Next Question trên popup
                                currentStep++; // Tăng currentStep

                                // Kiểm tra nếu hoàn thành
                                if (currentStep < totalSteps) {
                                    fetchQuestion(questionIds.get(currentStep)); // Lấy câu hỏi tiếp theo
                                    updateProgressBar(progressBar, currentStep); // Cập nhật thanh tiến trình
                                } else {
                                    Intent intent = new Intent(ListeningQuestionActivity.this, RecordQuestionActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        });
                    }

                    @Override
                    public void onSuccess(Question question) {
                    }

                    @Override
                    public void onSuccess(Lesson lesson) {
                    }

                    @Override
                    public void onSuccess(Course course) {
                    }

                    @Override
                    public void onSuccess(Result result) {
                    }

                    @Override
                    public void onSuccess(List<Answer> answer) {}

                    @Override
                    public void onSuccess(ApiResponseAnswer response) {

                    }

                    @Override
                    public void onFailure(String errorMessage) {
                    }

                    @Override
                    public void onSuccessWithOtpID(String otpID) {
                    }

                    @Override
                    public void onSuccessWithToken(String token) {

                    }
                });

            }
        });
    }
    /*private void fetchAudioUrl() {

        // Gọi phương thức fetchAudioUrl từ ApiManager
        apiManager.fetchAudioUrl(apiUrl, new ApiManager.ApiCallback() {
            @Override
            public void onSuccess(String audioUrl) {
                // Khi nhận được URL âm thanh từ API, gọi phương thức playAudio
                playAudio(audioUrl);
            }

            @Override
            public void onFailure(String errorMessage) {
                // Hiển thị thông báo lỗi nếu có
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show());
            }
        });
        private void playAudio (String audioUrl){
            try {
                // Tạo MediaPlayer để phát âm thanh
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(audioUrl); // Đường dẫn đến file âm thanh
                mediaPlayer.prepare(); // Chuẩn bị phát
                mediaPlayer.start(); // Bắt đầu phát âm thanh
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Không thể phát âm thanh", Toast.LENGTH_SHORT).show();
            }
        }
    }*/

    private void fetchLessonAndQuestions(int lessonId) {
        apiManager.fetchLessonById(lessonId, new ApiCallback() {
            @Override
            public void onSuccess(Lesson lesson) {
                if (lesson != null) {
                    questionIds = lesson.getQuestionIds();
                    Log.d("ListeningQuestionActivity", "Danh sách questionIds: " + questionIds);
                    totalSteps = questionIds.size();
                    if (questionIds != null && !questionIds.isEmpty()) {
                        fetchQuestion(questionIds.get(currentStep));
                    } else {
                        Log.e("ListeningQuestionActivity", "Bài học không có câu hỏi.");
                    }
                } else {
                    Log.e("ListeningQuestionActivity", "Bài học trả về là null.");
                }
            }

            @Override
            public void onSuccess(Course course) {}

            @Override
            public void onSuccess(Result result) {}

            @Override
            public void onSuccess(List<Answer> answer) {}

            @Override
            public void onSuccess(ApiResponseAnswer response) {

            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("ListeningQuestionActivity", errorMessage);
            }

            @Override
            public void onSuccessWithOtpID(String otpID) {}

            @Override
            public void onSuccessWithToken(String token) {

            }

            @Override
            public void onSuccess() {}

            @Override
            public void onSuccess(Question question) {}
        });
    }

    private void fetchQuestion(int questionId) {
        apiManager.fetchQuestionContentFromApi(questionId, new ApiCallback() {
            @Override
            public void onSuccess(Question question) {
                if (question != null) {
                    // Cập nhật giao diện người dùng với nội dung câu hỏi
                    runOnUiThread(() -> {
                        // Giả sử bạn có một TextView để hiển thị câu hỏi
                        TextView tvQuestion = findViewById(R.id.tvQuestion);
                        tvQuestion.setText(question.getQuesContent());

                        // Lưu trữ đáp án đúng để kiểm tra sau
                        List<QuestionChoice> choices = question.getQuestionChoices();
                        correctAnswers.clear();
                        for (QuestionChoice choice : choices) {
                            if (choice.isChoiceKey()) {
                                correctAnswers.add(choice.getChoiceContent());
                            }
                        }
                    });
                } else {
                    Log.e("ListeningQuestionActivity", "Câu hỏi trả về là null.");
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("ListeningQuestionActivity", errorMessage);
            }

            @Override
            public void onSuccess(Lesson lesson) {}

            @Override
            public void onSuccess(Result result) {}

            @Override
            public void onSuccess(List<Answer> answer) {}

            @Override
            public void onSuccess(ApiResponseAnswer response) {

            }

            @Override
            public void onSuccess(Course course) {}

            @Override
            public void onSuccessWithOtpID(String otpID) {}

            @Override
            public void onSuccessWithToken(String token) {

            }

            @Override
            public void onSuccess() {}
        });
    }

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