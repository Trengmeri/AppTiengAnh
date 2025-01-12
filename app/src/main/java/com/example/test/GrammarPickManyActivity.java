package com.example.test;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.Set;

public class GrammarPickManyActivity extends AppCompatActivity {
    private String correctAnswers = "JavaPythonC#"; // Đáp án đúng
    private int currentStep = 0; // Bước hiện tại (bắt đầu từ 0)
    private int totalSteps = 5; // Tổng số bước trong thanh tiến trình
    private String userAnswers = "";
    private String selectedAnswer  ;
   // private Set<String> selectedAnswers = new HashSet<>();
    CheckBox checkbox1,checkbox2,checkbox3,checkbox4,checkbox5;
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
        Button btnCheckAnswers = findViewById(R.id.btnCheckAnswers);
        LinearLayout progressBar = findViewById(R.id.progressBar);
        checkbox1.setOnClickListener(v -> userAnswers += checkbox1.getText().toString());
        checkbox2.setOnClickListener(v -> userAnswers += checkbox2.getText().toString());
        checkbox3.setOnClickListener(v -> userAnswers += checkbox3.getText().toString());
        checkbox4.setOnClickListener(v -> userAnswers += checkbox4.getText().toString());
        checkbox5.setOnClickListener(v -> userAnswers += checkbox5.getText().toString());
        //setupAnswerClickListeners();

        btnCheckAnswers.setOnClickListener(v -> {
            if (userAnswers.isEmpty()) {
                Toast.makeText(GrammarPickManyActivity.this, "Vui lòng trả lời câu hỏi!", Toast.LENGTH_SHORT).show();
            } else {
                // Hiển thị popup
                PopupHelper.showResultPopup(findViewById(R.id.popupContainer), userAnswers, correctAnswers, () -> {
                    // Callback khi nhấn Next Question trên popup
                    resetAnswerColors();
                    //currentStep++;
                    updateProgressBar(progressBar, currentStep);
                    currentStep++; // Cập nhật thanh tiến trình
                    checkbox1.setChecked(false);
                    checkbox2.setChecked(false);
                    checkbox3.setChecked(false);
                    checkbox4.setChecked(false);
                    checkbox5.setChecked(false);

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
//    private void setupAnswerClickListeners() {
//        View.OnClickListener answerClickListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Button selectedButton = (CheckBox) view;
//                userAnswers = selectedButton.getText().toString();
//
//                if (selectedAnswers.contains(userAnswers)) {
//                    // Nếu đáp án đã được chọn, gỡ bỏ đáp án và đổi màu về mặc định
//                    selectedAnswers.remove(userAnswers);
//                    selectedButton.setBackgroundColor(Color.TRANSPARENT);  // Đặt lại màu nền
//                } else {
//                    // Nếu đáp án chưa được chọn, thêm đáp án vào danh sách và đổi màu
//                    selectedAnswers.add(userAnswers);
//                    selectedButton.setBackgroundColor(Color.parseColor("#FADAC1"));  // Màu khi chọn đáp án
//                }
//                //userAnswers = ((CheckBox) view).getText().toString();
//            }
//        };
//
//        // Gán sự kiện cho các nút đáp án
//        checkbox1.setOnClickListener(answerClickListener);
//        checkbox2.setOnClickListener(answerClickListener);
//        checkbox3.setOnClickListener(answerClickListener);
//        checkbox4.setOnClickListener(answerClickListener);
//        checkbox5.setOnClickListener(answerClickListener);
//    }

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
        checkbox1.setBackgroundColor(Color.TRANSPARENT);
        checkbox2.setBackgroundColor(Color.TRANSPARENT);
        checkbox3.setBackgroundColor(Color.TRANSPARENT);
        checkbox4.setBackgroundColor(Color.TRANSPARENT);
        checkbox5.setBackgroundColor(Color.TRANSPARENT);
    }
}