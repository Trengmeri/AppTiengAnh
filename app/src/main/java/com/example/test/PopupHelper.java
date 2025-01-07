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

public class PopupHelper {

    public static void showResultPopup(Context context, String userAnswer, String correctAnswer) {
        boolean isCorrect = userAnswer.equals(correctAnswer);

        // Tạo pop-up
        View popupView = LayoutInflater.from(context).inflate(R.layout.popup_result, null);
        PopupWindow popupWindow = new PopupWindow(popupView,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);

        TextView tvMessage = popupView.findViewById(R.id.tvResultMessage);
        TextView tvDetail = popupView.findViewById(R.id.tvResultDetail);
        Button btnNext = popupView.findViewById(R.id.btnNextQuestion);

        if (isCorrect) {
            tvMessage.setText("That's right!\nAnswer:");
            tvMessage.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
            tvDetail.setText("The " + correctAnswer + " is a global network that connects millions of private, public, academic, and business networks.");
            popupView.setBackgroundResource(R.drawable.popup_background_correct);
            btnNext.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_dark));

        } else {
            tvMessage.setText("Oops... That's not the answer.\nCorrect answer:");
            tvMessage.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
            tvDetail.setText("The " + correctAnswer + " is a global network that connects millions of private, public, academic, and business networks.");
            popupView.setBackgroundResource(R.drawable.popup_background_incorrect);
            btnNext.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_dark));

        }

        btnNext.setOnClickListener(v -> popupWindow.dismiss());

        // Hiển thị popup ở dưới cùng màn hình
        popupWindow.showAtLocation(((GrammarQuestionActivity) context).findViewById(android.R.id.content), Gravity.BOTTOM, 0, 0);
    }
}
