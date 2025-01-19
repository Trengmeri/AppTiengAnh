package com.example.test.activity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.widget.AppCompatButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.test.NetworkChangeReceiver;
import com.example.test.PopupHelper;
import com.example.test.R;
import com.example.test.api.ApiCallback;
import com.example.test.api.ApiManager;
import com.example.test.model.Course;
import com.example.test.model.Lesson;
import com.example.test.model.Question;
import com.example.test.model.QuestionChoice;

import java.util.ArrayList;
import java.util.List;

public class GrammarPickManyActivity extends AppCompatActivity {
    List<String> correctAnswers = new ArrayList<>(); // Đáp án đúng
    private int currentStep = 0; // Bước hiện tại (bắt đầu từ 0)
    private int totalSteps; // Tổng số bước trong thanh tiến trình
    //private String userAnswer = "";
    private List<String> userAnswers = new ArrayList<>();
    List<String> selectedAnswers = new ArrayList<>();
    //private String selectedAnswers  ;
   // private Set<String> selectedAnswers = new HashSet<>();
   AppCompatButton checkbox1,checkbox2,checkbox3,checkbox4,checkbox5;
    TextView tvContent;
    ApiManager apiManager;
    NetworkChangeReceiver networkReceiver;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grammar_pick_many);

        // Ánh xạ các thành phần UI
         checkbox1 = findViewById(R.id.checkbox1);
         checkbox2 = findViewById(R.id.checkbox2);
         checkbox3 = findViewById(R.id.checkbox3);
         checkbox4 = findViewById(R.id.checkbox4);
         checkbox5 = findViewById(R.id.checkbox5);
         tvContent= findViewById(R.id.tvContent);
        Button btnCheckAnswers = findViewById(R.id.btnCheckAnswers);
        LinearLayout progressBar = findViewById(R.id.progressBar);

        checkbox1.setOnClickListener(v -> toggleAnswer(checkbox1));
        checkbox2.setOnClickListener(v -> toggleAnswer(checkbox2));
        checkbox3.setOnClickListener(v -> toggleAnswer(checkbox3));
        checkbox4.setOnClickListener(v -> toggleAnswer(checkbox4));
        checkbox5.setOnClickListener(v -> toggleAnswer(checkbox5));
        //setupAnswerClickListeners();
        networkReceiver = new NetworkChangeReceiver();
        apiManager = new ApiManager();
        int lessonId = 2;
        fetchLessonAndQuestions(lessonId); // Gọi phương thức để lấy bài học và câu hỏi

        btnCheckAnswers.setOnClickListener(v -> {
            Log.d("GrammarPickManyActivity", "User Answers: " + userAnswers);
            if (userAnswers.isEmpty()) {
                Toast.makeText(GrammarPickManyActivity.this, "Vui lòng trả lời câu hỏi!", Toast.LENGTH_SHORT).show();
            } else {
                // Hiển thị popup
                PopupHelper.showResultPopup(findViewById(R.id.popupContainer), userAnswers, correctAnswers, () -> {
                    // Callback khi nhấn Next Question trên popup
                    resetAnswerColors();
                    updateProgressBar(progressBar, currentStep);
                    currentStep++; // Cập nhật thanh tiến trình

                    // Kiểm tra nếu hoàn thành
                    if (currentStep >= totalSteps) {
                        Intent intent = new Intent(GrammarPickManyActivity.this, ListeningQuestionActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
    }

    private void fetchLessonAndQuestions(int lessonId) {
        apiManager.fetchLessonById(lessonId, new ApiCallback() {
            @Override
            public void onSuccess(Lesson lesson) {
                if (lesson != null) {
                    // Lấy danh sách questionIds từ lesson
                    List<Integer> questionIds = lesson.getQuestionIds();
                    totalSteps = questionIds.size();
                    if (questionIds != null && !questionIds.isEmpty()) {
                        fetchQuestion(questionIds.get(currentStep)); // Lấy câu hỏi đầu tiên
                    } else {
                        Log.e("GrammarPickManyActivity", "Bài học không có câu hỏi.");
                    }
                } else {
                    Log.e("GrammarPickManyActivity", "Bài học trả về là null.");
                }
            }

            @Override
            public void onSuccess(Course course) {}

            @Override
            public void onFailure(String errorMessage) {
                Log.e("GrammarPickManyActivity", errorMessage);
            }

            @Override
            public void onSuccessWithOtpID(String otpID) {}

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
                    // Lấy nội dung câu hỏi
                    String questionContent = question.getQuesContent();
                    Log.d("GrammarPickManyActivity", "Câu hỏi: " + questionContent);

                    // Lấy danh sách lựa chọn
                    List<QuestionChoice> choices = question.getQuestionChoices();
                    if (choices != null && !choices.isEmpty()) {
                        for (QuestionChoice choice : choices) {
                            Log.d("GrammarPickManyActivity", "Lựa chọn: " + choice.getChoiceContent() +
                                    " (Đáp án đúng: " + choice.isChoiceKey() + ")");
                        }

                        // Cập nhật giao diện người dùng
                        runOnUiThread(() -> {
                            tvContent.setText(questionContent);
                            checkbox1.setText(choices.get(0).getChoiceContent());
                            checkbox2.setText(choices.get(1).getChoiceContent());
                            checkbox3.setText(choices.get(2).getChoiceContent());
                            checkbox4.setText(choices.get(3).getChoiceContent());
                            checkbox5.setText(choices.get(4).getChoiceContent());
                            correctAnswers.clear();
                            for (QuestionChoice choice : choices) {
                                if (choice.isChoiceKey()) {
                                    correctAnswers.add(choice.getChoiceContent());
                                }
                            }
                        });
                    } else {
                        Log.e("GrammarPickManyActivity", "Câu hỏi không có lựa chọn.");
                    }
                } else {
                    Log.e("GrammarPickManyActivity", "Câu hỏi trả về là null.");
                }
            }

            @Override
            public void onSuccess(Lesson lesson) {}

            @Override
            public void onSuccess(Course course) {}

            @Override
            public void onFailure(String errorMessage) {
                Log.e("GrammarPickManyActivity", errorMessage);
            }

            @Override
            public void onSuccessWithOtpID(String otpID) {}

            @Override
            public void onSuccess() {}
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
    private void resetAnswerColors() {
        // Đặt lại màu nền cho tất cả các đáp án về màu mặc định
        userAnswers.clear();
        checkbox1.setBackgroundResource(R.color.colorDefault);
        checkbox2.setBackgroundResource(R.color.colorDefault);
        checkbox3.setBackgroundResource(R.color.colorDefault);
        checkbox4.setBackgroundResource(R.color.colorDefault);
        checkbox5.setBackgroundResource(R.color.colorDefault);

    }
    private void toggleAnswer(AppCompatButton button) {
        String answer = button.getText().toString();

        if (userAnswers.contains(answer)) {
            // Bỏ chọn: gỡ đáp án và đặt lại màu mặc định
            userAnswers.remove(answer);
            button.setBackgroundResource(R.color.colorDefault); // Màu nền mặc định
        } else {
            // Chọn: thêm đáp án và đổi màu
            userAnswers.add(answer);
            button.setBackgroundResource(R.color.colorPressed);// Màu khi chọn
        }

        // Log kết quả để kiểm tra
        Log.d("ToggleAnswer", "Current userAnswers: " + userAnswers);
    }
}