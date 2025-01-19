package com.example.test;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

public class PopupHelper {

    public static void showResultPopup(View anchorView, List<String> userAnswers, List<String> correctAnswers, Runnable onNextQuestion) {
        //boolean isCorrect = userAnswer.equalsIgnoreCase(correctAnswer);
//        boolean isCorrect = correctAnswers.stream()
//                .anyMatch(answer -> answer.equalsIgnoreCase(userAnswers));
        // Tạo pop-up
        View popupView = LayoutInflater.from(anchorView.getContext()).inflate(R.layout.popup_result, null);
        PopupWindow popupWindow = new PopupWindow(popupView,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);

        TextView tvMessage = popupView.findViewById(R.id.tvResultMessage);
        TextView tvDetail = popupView.findViewById(R.id.tvResultDetail);
        Button btnNext = popupView.findViewById(R.id.btnNextQuestion);

        if (userAnswers.equals(correctAnswers)) {
            tvMessage.setText("That's right!\nAnswer:");
            tvMessage.setTextColor(anchorView.getResources().getColor(android.R.color.holo_green_dark));
            String correctAnswerText = String.join(", ", correctAnswers);
            tvDetail.setText(correctAnswerText);
            popupView.setBackgroundResource(R.drawable.popup_background_correct);
        } else {
            tvMessage.setText("Oops... That's not the answer.\nCorrect answer:");
            tvMessage.setTextColor(anchorView.getResources().getColor(android.R.color.holo_red_dark));
            String correctAnswerText = String.join(", ", correctAnswers);
            tvDetail.setText(correctAnswerText);
            popupView.setBackgroundResource(R.drawable.popup_background_incorrect);
        }
        btnNext.setOnClickListener(v -> {
            popupWindow.dismiss(); // Đóng popup
            onNextQuestion.run();
            // Gọi hàm để chuyển sang câu hỏi tiếp theo
        });

        // Hiển thị popup ở dưới cùng của view cụ thể
        popupWindow.showAtLocation(anchorView, Gravity.BOTTOM, 0, 0);
    }
}
