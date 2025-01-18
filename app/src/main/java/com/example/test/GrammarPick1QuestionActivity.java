package com.example.test;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.test.model.Question;
import com.example.test.model.QuestionChoice;
import com.example.test.model.QuestionChoice;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GrammarPick1QuestionActivity extends AppCompatActivity {
    List<String> correctAnswers = new ArrayList<>(); // Giả sử dữ liệu đúng từ backend
    private String userAnswer = ""; // Đáp án mà người dùng chọn
    private int currentStep = 0; // Bước hiện tại (bắt đầu từ 0)
    private int totalSteps = 5; // Tổng số bước trong thanh tiến trình
    private AppCompatButton selectedAnswer = null;
    private AppCompatButton btnAnswer1, btnAnswer2, btnAnswer3, btnAnswer4;
    private Button  btnCheckAnswer;
    ApiManager apiManager;
    TextView tvContent;
    NetworkChangeReceiver networkReceiver;

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
        fetchQuestion();
        btnCheckAnswer.setOnClickListener(v -> {
            if (userAnswer.isEmpty()) {
                Toast.makeText(GrammarPick1QuestionActivity.this, "Vui lòng trả lời câu hỏi!", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // Hiển thị popup
                PopupHelper.showResultPopup(findViewById(R.id.popupContainer), userAnswer, correctAnswers, () -> {
                    // Callback khi nhấn Next Question trên popup
                    resetAnswerColors();
                    currentStep++;
                    updateProgressBar(progressBar, currentStep); // Cập nhật thanh tiến trình

                    // Kiểm tra nếu hoàn thành
                    if (currentStep >= totalSteps) {
                        Intent intent = new Intent(GrammarPick1QuestionActivity.this, GrammarPickManyActivity.class);
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
                    Color.parseColor("#E0E0E0"), // Màu ban đầu
                    Color.parseColor("#C4865E") // Màu đã hoàn thành
            );
            colorAnimator.setDuration(300); // Thời gian chuyển đổi màu
            colorAnimator.start();
        }
    }

    private void fetchQuestion() {
        if (apiManager != null) {
            apiManager.fetchQuestionContentFromApi(new ApiCallback() {
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
//                                correctAnswers = choices.stream()
//                                        .filter(QuestionChoice::isChoiceKey)
//                                        .findFirst()
//                                        .map(QuestionChoice::getChoiceContent)
//                                        .orElse(""); // Lấy đáp án đúng
                                 correctAnswers = choices.stream()
                                        .filter(QuestionChoice::isChoiceKey) // Lọc ra các đáp án đúng
                                        .map(QuestionChoice::getChoiceContent) // Chuyển đổi thành nội dung đáp án
                                        .collect(Collectors.toList());

                                // Trong trường hợp có một đáp án đúng, correctAnswers chỉ chứa một phần tử
//                                if (!correctAnswers.isEmpty()) {
//                                    // Hiển thị đáp án đúng đầu tiên (hoặc hiển thị tất cả nếu có nhiều đáp án đúng)
//                                    String correctAnswer = correctAnswers.get(0);
//                                    Log.d("GrammarPick1QuestionActivity", "Đáp án đúng: " + correctAnswer);
//                                }
                            });
                        } else {
                            Log.e("GrammarPick1QuestionActivity", "Câu hỏi không có lựa chọn.");
                        }
                    } else {
                        Log.e("GrammarPick1QuestionActivity", "Câu hỏi trả về là null.");
                    }
                }

                @Override
                public void onFailure(String errorMessage) {
                    // Xử lý lỗi
                    Log.e("GrammarPick1QuestionActivity", errorMessage);
                }

                @Override
                public void onSuccessWithOtpID(String otpID) {

                }


                @Override
                public void onSuccess() {
                    // Nếu cần xử lý trường hợp này, thêm logic ở đây
                }
            });
        } else {
            Log.e("GrammarPick1QuestionActivity", "ApiManager chưa được khởi tạo!");
        }
    }

    private void setupAnswerClickListeners() {
        View.OnClickListener answerClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Nếu có đáp án đã được chọn trước đó, gỡ màu
                if (selectedAnswer != null) {
                    selectedAnswer.setBackgroundResource(R.drawable.bg_answer); // Hoặc màu nền mặc định
                }

                // Đặt màu cho đáp án được chọn
                view.setBackgroundResource(R.drawable.bg_answer_pressed); // Màu bạn muốn áp dụng

                // Cập nhật biến selectedAnswer
                selectedAnswer = (AppCompatButton) view;
                userAnswer = selectedAnswer.getText().toString();
            }
        };

        // Gán sự kiện cho các đáp án
        btnAnswer1.setOnClickListener(answerClickListener);
        btnAnswer2.setOnClickListener(answerClickListener);
        btnAnswer3.setOnClickListener(answerClickListener);
        btnAnswer4.setOnClickListener(answerClickListener);
    }

    private void resetAnswerColors() {
        // Đặt lại màu nền cho tất cả các đáp án về màu mặc định
        btnAnswer1.setBackgroundResource(R.drawable.bg_answer);
        btnAnswer2.setBackgroundResource(R.drawable.bg_answer);
        btnAnswer3.setBackgroundResource(R.drawable.bg_answer);
        btnAnswer4.setBackgroundResource(R.drawable.bg_answer);

    }
}