package com.example.test;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.test.model.Question;

public class GrammarPick1QuestionActivity extends AppCompatActivity {
    private String correctAnswer = ""; // Giả sử dữ liệu đúng từ backend
    private String userAnswer = ""; // Đáp án mà người dùng chọn
    private int currentStep = 0; // Bước hiện tại (bắt đầu từ 0)
    private int totalSteps = 5; // Tổng số bước trong thanh tiến trình
    private Button selectedAnswer = null;
    private Button btnAnswer1, btnAnswer2, btnAnswer3, btnAnswer4, btnCheckAnswer;
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
                Toast.makeText(GrammarPick1QuestionActivity.this, "Vui lòng trả lời câu hỏi!", Toast.LENGTH_SHORT).show();
            } else {
//                boolean isCorrect = userAnswer.equals(correctAnswer);
                // Hiển thị popup
                PopupHelper.showResultPopup(findViewById(R.id.popupContainer), userAnswer, correctAnswer, () -> {
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
                    Color.parseColor("#E0E0E0"),  // Màu ban đầu
                    Color.parseColor("#C4865E")   // Màu đã hoàn thành
            );
            colorAnimator.setDuration(300); // Thời gian chuyển đổi màu
            colorAnimator.start();
        }
    }


private void fetchQuestion() {
        if(apiManager!=null){
        apiManager.fetchQuestionContentFromApi(new ApiCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onSuccess(Question question) {
                // Kiểm tra question không phải null và có questionChoices hợp lệ
                if (question != null) {
                    // Log toàn bộ question và questionChoices để kiểm tra
                    Log.d("GrammarActivity", "Question: " + question.getQuesContent());
                    Log.d("GrammarActivity", "Question Choices: " + question.getQuestionChoices());

                    if (question.getQuestionChoices() != null && question.getQuestionChoices().size() >= 4) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Cập nhật các view trên giao diện người dùng
                                tvContent.setText(question.getQuesContent());
                                btnAnswer1.setText(question.getQuestionChoices().get(0).getChoiceContent());
                                btnAnswer2.setText(question.getQuestionChoices().get(1).getChoiceContent());
                                btnAnswer3.setText(question.getQuestionChoices().get(2).getChoiceContent());
                                btnAnswer4.setText(question.getQuestionChoices().get(3).getChoiceContent());
                                correctAnswer = question.getChoiceKey();
                            }
                        });
                } else {
                    Log.e("GrammarActivity", "Danh sách câu trả lời không hợp lệ.");

                }
            }
        }

            @Override
            public void onFailure(String errorMessage) {

            }

            @Override
            public void onSuccess(String token) {

            }
        });
        }else {
            Log.e("GrammarPick1QuestionActivity", "ApiManager chưa được khởi tạo!");
        }
        }
    private void setupAnswerClickListeners() {
        View.OnClickListener answerClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Nếu có đáp án đã được chọn trước đó, gỡ màu
                if (selectedAnswer != null) {
                    selectedAnswer.setBackgroundColor(Color.TRANSPARENT); // Hoặc màu nền mặc định
                }

                // Đặt màu cho đáp án được chọn
                view.setBackgroundColor(Color.parseColor("#C4865E")); // Màu bạn muốn áp dụng

                // Cập nhật biến selectedAnswer
                selectedAnswer = (Button) view;
                userAnswer = ((Button) view).getText().toString();
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
        btnAnswer1.setBackgroundColor(Color.TRANSPARENT);
        btnAnswer2.setBackgroundColor(Color.TRANSPARENT);
        btnAnswer3.setBackgroundColor(Color.TRANSPARENT);
        btnAnswer4.setBackgroundColor(Color.TRANSPARENT);
    }
}