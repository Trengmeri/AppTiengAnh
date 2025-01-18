package com.example.test;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import androidx.appcompat.widget.AppCompatButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.test.model.Question;
import com.example.test.model.QuestionChoice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GrammarPickManyActivity extends AppCompatActivity {
    List<String> correctAnswers = new ArrayList<>(); // Đáp án đúng
    private int currentStep = 0; // Bước hiện tại (bắt đầu từ 0)
    private int totalSteps = 5; // Tổng số bước trong thanh tiến trình
    private String userAnswers = "";
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
//        checkbox1.setOnClickListener(v -> userAnswers += checkbox1.getText().toString());
//        checkbox2.setOnClickListener(v -> userAnswers += checkbox2.getText().toString());
//        checkbox3.setOnClickListener(v -> userAnswers += checkbox3.getText().toString());
//        checkbox4.setOnClickListener(v -> userAnswers += checkbox4.getText().toString());
//        checkbox5.setOnClickListener(v -> userAnswers += checkbox5.getText().toString());
        checkbox1.setOnClickListener(v -> toggleAnswer(checkbox1));
        checkbox2.setOnClickListener(v -> toggleAnswer(checkbox2));
        checkbox3.setOnClickListener(v -> toggleAnswer(checkbox3));
        checkbox4.setOnClickListener(v -> toggleAnswer(checkbox4));
        checkbox5.setOnClickListener(v -> toggleAnswer(checkbox5));
        setupAnswerClickListeners();
        networkReceiver = new NetworkChangeReceiver();
        apiManager = new ApiManager();
        fetchQuestion();

        btnCheckAnswers.setOnClickListener(v -> {
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
    private void setupAnswerClickListeners() {

        // Tạo sự kiện click cho các nút đáp án
        View.OnClickListener answerClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatButton selectedButton = (AppCompatButton) view;
                String userAnswer = selectedButton.getText().toString();
                if (selectedAnswers.contains(userAnswer)) {
                    // Nếu đáp án đã được chọn, gỡ bỏ đáp án và đặt màu về mặc định
                    selectedAnswers.remove(userAnswer);
                    view.setBackgroundResource(R.color.colorDefault); // Màu nền mặc định
                } else {
                    // Nếu đáp án chưa được chọn, thêm vào danh sách và đổi màu
                    selectedAnswers.add(userAnswer);
                    view.setBackgroundResource(R.color.colorPressed); // Màu khi chọn
                }

                // Log danh sách đáp án đã chọn (tùy chọn, dùng để kiểm tra)
                Log.d("SelectedAnswers", "Current selected answers: " + selectedAnswers);
            }
        };

        // Gán sự kiện click cho các AppCompatButton đáp án
        checkbox1.setOnClickListener(answerClickListener);
        checkbox2.setOnClickListener(answerClickListener);
        checkbox3.setOnClickListener(answerClickListener);
        checkbox4.setOnClickListener(answerClickListener);
        checkbox5.setOnClickListener(answerClickListener);
    }

    private void fetchQuestion() {
    if (apiManager != null) {
        apiManager.fetchQuestionContentFromApi(new ApiCallback() {
            @Override
            public void onSuccess(Question question) {
                if (question != null) {
                    // Lấy nội dung câu hỏi
                    String questionContent = question.getQuesContent();
                    Log.d("GrammarPickManyQuestionActivity", "Câu hỏi: " + questionContent);

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
                            checkbox1.setText(choices.get(0).getChoiceContent());
                            checkbox2.setText(choices.get(1).getChoiceContent());
                            checkbox3.setText(choices.get(2).getChoiceContent());
                            checkbox4.setText(choices.get(3).getChoiceContent());
                            //checkbox5.setText(choices.get(4).getChoiceContent());
                            correctAnswers.clear();
                            for (QuestionChoice choice : choices) {
                                if (choice.isChoiceKey()) {
                                    correctAnswers.add(choice.getChoiceContent());
                                }
                            }
                        });
                    } else {
                        Log.e("GrammarPickManyQuestionActivity", "Câu hỏi không có lựa chọn.");
                    }
                } else {
                    Log.e("GrammarPickManyQuestionActivity", "Câu hỏi trả về là null.");
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                // Xử lý lỗi
                Log.e("GrammarPickManyQuestionActivity", errorMessage);
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
        Log.e("GrammarPickManyQuestionActivity", "ApiManager chưa được khởi tạo!");
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
    private void resetAnswerColors() {
        // Đặt lại màu nền cho tất cả các đáp án về màu mặc định
        checkbox1.setBackgroundResource(R.color.colorDefault);
        checkbox2.setBackgroundResource(R.color.colorDefault);
        checkbox3.setBackgroundResource(R.color.colorDefault);
        checkbox4.setBackgroundResource(R.color.colorDefault);
        checkbox5.setBackgroundResource(R.color.colorDefault);
    }
    private void toggleAnswer(AppCompatButton checkbox) {
        String answer = checkbox.getText().toString();
        if (selectedAnswers.contains(answer)) {
            selectedAnswers.remove(answer);  // Bỏ chọn
        } else {
            selectedAnswers.add(answer);     // Chọn
        }
        // Cập nhật userAnswers từ danh sách selectedAnswers
        userAnswers = String.join(", ", selectedAnswers); // Nối các đáp án chọn được thành chuỗi
    }
}