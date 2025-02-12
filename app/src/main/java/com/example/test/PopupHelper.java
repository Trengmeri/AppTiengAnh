package com.example.test;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.IBinder; // Import IBinder
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

public class PopupHelper {

    public static void showResultPopup(View anchorView, List<String> userAnswers, List<String> correctAnswers, Runnable onNextQuestion) {
        final Context context = anchorView.getContext();
        View popupView = LayoutInflater.from(context).inflate(R.layout.popup_result, null);

        PopupWindow popupWindow = new PopupWindow(popupView,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        // Get the window token from the anchor view (crucial!)
        IBinder token = anchorView.getWindowToken();

        // Dim the background (improved version)
        if (context instanceof Activity && token != null) {
            Activity activity = (Activity) context;
            WindowManager.LayoutParams params = activity.getWindow().getAttributes();
            params.alpha = 0.5f;
            activity.getWindow().setAttributes(params);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

            popupWindow.setOnDismissListener(() -> {
                WindowManager.LayoutParams p = activity.getWindow().getAttributes();
                p.alpha = 1.0f;
                activity.getWindow().setAttributes(p);
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            });
        }

        popupWindow.showAtLocation(anchorView, Gravity.BOTTOM, 0, 0); // Use the token here!



        TextView tvMessage = popupView.findViewById(R.id.tvResultMessage);
        TextView tvDetail = popupView.findViewById(R.id.tvResultDetail);
        Button btnNext = popupView.findViewById(R.id.btnNextQuestion);

        if (userAnswers.equals(correctAnswers)) {
            tvMessage.setText("That's right!\nAnswer:");
            tvMessage.setTextColor(anchorView.getResources().getColor(android.R.color.holo_green_dark));
            String correctAnswerText = String.join(", ", correctAnswers);
            tvDetail.setText(correctAnswerText);
            popupView.setBackgroundResource(R.drawable.popup_background_correct);
            btnNext.setBackgroundColor(anchorView.getResources().getColor(android.R.color.holo_green_dark));
        } else {
            tvMessage.setText("Oops... That's not the answer.\nCorrect answer:");
            tvMessage.setTextColor(anchorView.getResources().getColor(android.R.color.holo_red_dark));
            String correctAnswerText = String.join(", ", correctAnswers);
            tvDetail.setText(correctAnswerText);
            popupView.setBackgroundResource(R.drawable.popup_background_incorrect);
            btnNext.setBackgroundColor(anchorView.getResources().getColor(android.R.color.holo_red_dark));
        }
        btnNext.setOnClickListener(v -> {
            popupWindow.dismiss(); // Đóng popup
            onNextQuestion.run();
            // Gọi hàm để chuyển sang câu hỏi tiếp theo
        });
    }
}
