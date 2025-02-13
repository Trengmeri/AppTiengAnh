package com.example.test.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import android.animation.AnimatorInflater;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.test.R;

public class FlashcardInfomationActivity extends AppCompatActivity {
    private boolean isFrontVisible = true;
    private View frontSide, backSide;
    private TextView tvBackContent;
    private AnimatorSet flipIn, flipOut;
    private ImageView btnX;
    @SuppressLint({"ResourceType", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_flashcard_infomation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        frontSide = findViewById(R.id.frontSide);
        backSide = findViewById(R.id.backSide);
        tvBackContent = findViewById(R.id.tvBackContent);
        AppCompatButton btnDefinition = findViewById(R.id.btnDefinition);
        AppCompatButton btnExample = findViewById(R.id.btnExample);
        btnX= findViewById(R.id.btnX);

        flipIn = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.anim.flip_in);
        flipOut = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.anim.flip_out);

        btnDefinition.setOnClickListener(v -> flipCard("Definition: A message or document, or a set of messages or documents, sent using this system"));
        btnExample.setOnClickListener(v -> flipCard("Example: I sent an email to my boss"));

        btnX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // Đóng Activity hiện tại
                overridePendingTransition(R.anim.stay, R.anim.slide_down); // Áp dụng hiệu ứng
            }
        });

    }
    private void flipCard(String content) {
        if (isFrontVisible) {
            flipOut.setTarget(frontSide);
            flipIn.setTarget(backSide);
            flipOut.start();
            flipOut.addListener(new android.animation.Animator.AnimatorListener() {
                @Override
                public void onAnimationEnd(android.animation.Animator animation) {
                    frontSide.setVisibility(View.GONE);
                    backSide.setVisibility(View.VISIBLE);
                    tvBackContent.setText(content);
                    flipIn.start();
                }
                @Override public void onAnimationStart(android.animation.Animator animation) {}
                @Override public void onAnimationCancel(android.animation.Animator animation) {}
                @Override public void onAnimationRepeat(android.animation.Animator animation) {}
            });
        } else {
            flipOut.setTarget(backSide);
            flipIn.setTarget(frontSide);
            flipOut.start();
            flipOut.addListener(new android.animation.Animator.AnimatorListener() {
                @Override
                public void onAnimationEnd(android.animation.Animator animation) {
                    backSide.setVisibility(View.GONE);
                    frontSide.setVisibility(View.VISIBLE);
                    flipIn.start();
                }
                @Override public void onAnimationStart(android.animation.Animator animation) {}
                @Override public void onAnimationCancel(android.animation.Animator animation) {}
                @Override public void onAnimationRepeat(android.animation.Animator animation) {}
            });
        }
        isFrontVisible = !isFrontVisible;
    }

}