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

import org.json.JSONObject;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PopupHelper {

    public static void showResultPopup(Activity activity, String questType, List<String> userAnswers, List<String> correctAnswers, Double score, String improvements, String evaluation, Runnable onNextQuestion) {
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View popupView = LayoutInflater.from(activity).inflate(R.layout.popup_result, null);
        dialog.setContentView(popupView);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        if (dialog.getWindow() != null) {
            Window window = dialog.getWindow();
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.BOTTOM;
            layoutParams.dimAmount = 0.5f;
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setAttributes(layoutParams);
        }

        TextView tvMessage = popupView.findViewById(R.id.tvResultMessage);
        TextView tvDetail = popupView.findViewById(R.id.tvResultDetail);
        Button btnNext = popupView.findViewById(R.id.btnNextQuestion);
        Button btnview = popupView.findViewById(R.id.btnview);

        if ("MULTIPLE".equals(questType) || "CHOICE".equals(questType) || "TEXT".equals(questType)) {
            Set<String> userSet = userAnswers.stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());

            Set<String> correctSet = correctAnswers.stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());

            if (userSet.equals(correctSet)) {
                tvMessage.setText(String.format("%s\n%s", activity.getString(R.string.correct), activity.getString(R.string.ANS)));
                tvMessage.setTextColor(activity.getResources().getColor(android.R.color.holo_green_dark));
                tvDetail.setText(String.join(", ", correctAnswers));
                popupView.setBackgroundResource(R.drawable.popup_background_correct);
                btnNext.setBackgroundColor(activity.getResources().getColor(android.R.color.holo_green_dark));
            }else {
                tvMessage.setText(String.format("%s\n%s", activity.getString(R.string.oops), activity.getString(R.string.COANS)));
                tvMessage.setTextColor(activity.getResources().getColor(android.R.color.holo_red_dark));
                tvDetail.setText(String.join(", ", correctAnswers));
                popupView.setBackgroundResource(R.drawable.popup_background_incorrect);
                btnNext.setBackgroundColor(activity.getResources().getColor(android.R.color.holo_red_dark));
            }
        } else {
            try {
                btnview.setVisibility(View.VISIBLE);
                tvMessage.setText(String.format("%s :%.1f",activity.getString(R.string.point), score));
                tvDetail.setText(evaluation);
                btnview.setOnClickListener(view -> {
                    tvMessage.setText(activity.getString(R.string.improvements));
                    tvDetail.setText(improvements);
                    btnview.setVisibility(View.GONE);
                });

                // Đặt màu nền theo điểm
                if (score < 33) {
                    popupView.setBackgroundResource(R.drawable.popup_background_incorrect);
                    tvMessage.setTextColor(activity.getResources().getColor(android.R.color.holo_red_dark));
                    btnNext.setBackgroundColor(activity.getResources().getColor(android.R.color.holo_red_dark));
                    btnview.setBackgroundColor(activity.getResources().getColor(android.R.color.holo_red_light));
                } else if (score < 66) {
                    popupView.setBackgroundResource(R.drawable.popup_yellow);
                    tvMessage.setTextColor(activity.getResources().getColor(android.R.color.holo_orange_dark));
                    btnNext.setBackgroundColor(activity.getResources().getColor(android.R.color.holo_orange_dark));
                    btnview.setBackgroundColor(activity.getResources().getColor(android.R.color.holo_orange_light));
                } else {
                    popupView.setBackgroundResource(R.drawable.popup_background_correct);
                    tvMessage.setTextColor(activity.getResources().getColor(android.R.color.holo_green_dark));
                    btnNext.setBackgroundColor(activity.getResources().getColor(android.R.color.holo_green_dark));
                    btnview.setBackgroundColor(activity.getResources().getColor(android.R.color.holo_green_light));
                }
            } catch (Exception e) {
                tvMessage.setText("Error displaying result!");
                tvDetail.setText(e.getMessage());
            }
        }
        btnNext.setOnClickListener(v -> {
            dialog.dismiss();
            onNextQuestion.run();
        });

        dialog.show();
    }
}
