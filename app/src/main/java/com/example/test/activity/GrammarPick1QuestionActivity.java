package com.example.test.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

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
import java.util.stream.Collectors;

public class GrammarPick1QuestionActivity extends AppCompatActivity {
    List<String> correctAnswers = new ArrayList<>();
    private List<String> userAnswers = new ArrayList<>();
    private int currentStep = 0; // Bước hiện tại (bắt đầu từ 0)
    private int totalSteps; // Tổng số bước trong thanh tiến trình
    private AppCompatButton selectedAnswer = null;
    private AppCompatButton btnAnswer1, btnAnswer2, btnAnswer3, btnAnswer4;
    private Button btnCheckAnswer;
    ApiManager apiManager;
    TextView tvContent;
    NetworkChangeReceiver networkReceiver;
    private List<Integer> questionIds; // Danh sách questionIds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grammar_question);

        // Ánh xạ các thành phần UI
        btnAnswer1 = findViewById(R.id.btnOption1);
        btnAnswer2 = findViewById(R.id.btnOption2);
        btnAnswer3 = findViewById(R.id.btnOption3);
        btnAnswer4 = findViewById(R.id.btnOption4);
        btnCheckAnswer = findViewById(R.id.btnCheckAnswer);
        tvContent = findViewById(R.id.tvContent);
        LinearLayout progressBar = findViewById(R.id.progressBar); // Ánh xạ ProgressBar
        setupAnswerClickListeners();
        updateProgressBar(progressBar, currentStep);
        networkReceiver = new NetworkChangeReceiver();
        apiManager = new ApiManager();

        // Lấy lessonId từ intent hoặc một nguồn khác
        int lessonId = 1;
        fetchLessonAndQuestions(lessonId); // Gọi phương thức để lấy bài học và câu hỏi

        btnCheckAnswer.setOnClickListener(v -> {
            if (userAnswers.isEmpty()) {
                Toast.makeText(GrammarPick1QuestionActivity.this, "Vui lòng trả lời câu hỏi!", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // Hiển thị popup
                PopupHelper.showResultPopup(findViewById(R.id.popupContainer), userAnswers, correctAnswers, () -> {
                    // Callback khi nhấn Next Question trên popup
                    resetAnswerColors();
                    currentStep++; // Tăng currentStep

                    // Kiểm tra nếu hoàn thành
                    if (currentStep < totalSteps) {
                        fetchQuestion(questionIds.get(currentStep)); // Lấy câu hỏi tiếp theo
                        updateProgressBar(progressBar, currentStep); // Cập nhật thanh tiến trình
                    } else {
                        Intent intent = new Intent(GrammarPick1QuestionActivity.this, PointResultActivity.class);
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
                    questionIds = lesson.getQuestionIds(); // Lưu trữ danh sách questionIds
                    totalSteps = questionIds.size(); // Cập nhật tổng số bước
                    if (questionIds != null && !questionIds.isEmpty()) {
                        fetchQuestion(questionIds.get(currentStep)); // Lấy câu hỏi đầu tiên
                    } else {
                        Log.e("GrammarPick1QuestionActivity", "Bài học không có câu hỏi.");
                    }
                } else {
                    Log.e("GrammarPick1QuestionActivity", "Bài học trả về là null.");
                }
            }

            @Override
            public void onSuccess(Course course) {}

            @Override
            public void onFailure(String errorMessage) {
                Log.e("GrammarPick1QuestionActivity", errorMessage);
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
                    Log.d("GrammarPick1QuestionActivity", "Câu hỏi: " + questionContent);

                    // Lấy danh sách lựa chọn
                    List<QuestionChoice> choices = question.getQuestionChoices();
                    if (choices != null && !choices.isEmpty()) {
                        for (QuestionChoice choice : choices) {
                            Log.d("GrammarPick1QuestionActivity", "Lựa chọn: " + choice.getChoiceContent() +
                                    " (Đáp án đúng: " + choice.isChoiceKey() + ")");
                        }

                        // Cập nhật giao diện người dùng
                        runOnUiThread(() -> {
                            tvContent.setText(questionContent);
                            btnAnswer1.setText(choices.get(0).getChoiceContent());
                            btnAnswer2.setText(choices.get(1).getChoiceContent());
                            btnAnswer3.setText(choices.get(2).getChoiceContent());
                            btnAnswer4.setText(choices.get(3).getChoiceContent());

                            correctAnswers = choices.stream()
                                    .filter(QuestionChoice::isChoiceKey) // Lọc ra các đáp án đúng
                                    .map(QuestionChoice::getChoiceContent) // Chuyển đổi thành nội dung đáp án
                                    .collect(Collectors.toList());
                        });
                    } else {
                        Log.e("GrammarPick1QuestionActivity", "Câu hỏi không có lựa chọn.");
                    }
                } else {
                    Log.e("GrammarPick1QuestionActivity", "Câu hỏi trả về là null.");
                }
            }

            @Override
            public void onSuccess(Lesson lesson) {}

            @Override
            public void onSuccess(Course course) {}

            @Override
            public void onFailure(String errorMessage) {
                Log.e("GrammarPick1QuestionActivity", errorMessage);
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
                    Color.parseColor("#E0E0E0"), // Màu ban đầu
                    Color.parseColor("#C4865E") // Màu đã hoàn thành
            );
            colorAnimator.setDuration(300); // Thời gian chuyển đổi màu
            colorAnimator.start();
        }
    }

    private void setupAnswerClickListeners() {
        View.OnClickListener answerClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatButton clickedButton = (AppCompatButton) view;

                // Kiểm tra nếu đáp án đã được chọn trước đó
                if (selectedAnswer != null && selectedAnswer == clickedButton) {
                    // Nếu đáp án đang được chọn lại, gỡ bỏ chọn
                    clickedButton.setBackgroundResource(R.drawable.bg_answer); // Màu nền mặc định
                    selectedAnswer = null;

                    // Xóa đáp án khỏi danh sách
                    userAnswers.remove(clickedButton.getText().toString());
                } else {
                    // Nếu có đáp án được chọn trước đó, gỡ màu của đáp án cũ
                    if (selectedAnswer != null) {
                        selectedAnswer.setBackgroundResource(R.drawable.bg_answer);
                    }

                    // Đặt màu cho đáp án mới được chọn
                    clickedButton.setBackgroundResource(R.drawable.bg_answer_pressed);

                    // Cập nhật đáp án được chọn
                    selectedAnswer = clickedButton;

                    // Thêm đáp án mới vào danh sách
                    String answerText = clickedButton.getText().toString();
                    if (!userAnswers.contains(answerText)) {
                        userAnswers.add(answerText);
                    }
                }
            }
        };

        // Đăng ký lắng nghe sự kiện cho tất cả các nút đáp án
        btnAnswer1.setOnClickListener(answerClickListener);
        btnAnswer2.setOnClickListener(answerClickListener);
        btnAnswer3.setOnClickListener(answerClickListener);
        btnAnswer4.setOnClickListener(answerClickListener);
    }

    private void resetAnswerColors() {
        // Đặt lại màu nền cho tất cả các đáp án về màu mặc định
        userAnswers.clear();
        btnAnswer1.setBackgroundResource(R.drawable.bg_answer);
        btnAnswer2.setBackgroundResource(R.drawable.bg_answer);
        btnAnswer3.setBackgroundResource(R.drawable.bg_answer);
        btnAnswer4.setBackgroundResource(R.drawable.bg_answer);
    }
}