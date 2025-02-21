package com.example.test;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class PopupHelper {

    public static void showResultPopup(Activity activity, List<String> userAnswers, List<String> correctAnswers, Runnable onNextQuestion) {
        // Tạo Dialog thay vì PopupWindow
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View popupView = LayoutInflater.from(activity).inflate(R.layout.popup_result, null);
        dialog.setContentView(popupView);

        // Ngăn chặn bấm ra ngoài và nút Back
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        // Đặt nền trong suốt
        if (dialog.getWindow() != null) {
            Window window = dialog.getWindow();
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Nền trong suốt

            // Hiển thị ở cuối màn hình
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT; // Chiều rộng = màn hình
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT; // Chiều cao tự động theo nội dung
            layoutParams.gravity = Gravity.BOTTOM; // Hiển thị ở cuối màn hình

            // Tạo lớp phủ mờ phía sau
            layoutParams.dimAmount = 0.5f; // Độ mờ nền (0.0 = không mờ, 1.0 = tối đen)
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

            window.setAttributes(layoutParams);
        }

        TextView tvMessage = popupView.findViewById(R.id.tvResultMessage);
        TextView tvDetail = popupView.findViewById(R.id.tvResultDetail);
        Button btnNext = popupView.findViewById(R.id.btnNextQuestion);

        if (userAnswers.equals(correctAnswers)) {
            tvMessage.setText("That's right!\nAnswer:");
            tvMessage.setTextColor(activity.getResources().getColor(android.R.color.holo_green_dark));
            tvDetail.setText(String.join(", ", correctAnswers));
            popupView.setBackgroundResource(R.drawable.popup_background_correct);
            btnNext.setBackgroundColor(activity.getResources().getColor(android.R.color.holo_green_dark));
        } else {
            tvMessage.setText("Oops... That's not the answer.\nCorrect answer:");
            tvMessage.setTextColor(activity.getResources().getColor(android.R.color.holo_red_dark));
            tvDetail.setText(String.join(", ", correctAnswers));
            popupView.setBackgroundResource(R.drawable.popup_background_incorrect);
            btnNext.setBackgroundColor(activity.getResources().getColor(android.R.color.holo_red_dark));
        }

        btnNext.setOnClickListener(v -> {
            dialog.dismiss();
            onNextQuestion.run();
        });

        // Hiển thị Dialog
        dialog.show();
    }
}
