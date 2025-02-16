package com.example.test.ui;

import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.os.Bundle;
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
import android.widget.Toast;

import com.example.test.R;
import com.example.test.response.ApiResponseFlashcard;
import com.example.test.api.FlashcardManager;
import com.example.test.api.FlashcardApiCallback;
import com.example.test.response.ApiResponseFlashcardGroup;
import com.example.test.response.ApiResponseOneFlashcard;
import com.example.test.response.FlashcardGroupResponse;
import com.example.test.model.Flashcard;

public class FlashcardInformationActivity extends AppCompatActivity {
    private boolean isFrontVisible = true;
    private View frontSide, backSide;
    private TextView tvBackContent;
    private AnimatorSet flipIn, flipOut;
    private ImageView btnX;
    private TextView tvAddedDate;
    private TextView tvExamples;
    private TextView tvWord;
    private TextView tvPronunciation;

    @SuppressLint({ "ResourceType", "MissingInflatedId" })
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_flashcard_information);
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
        btnX = findViewById(R.id.btnX);
        tvAddedDate = findViewById(R.id.tvAddedDate);
        tvExamples = findViewById(R.id.tvExamples);
        tvWord = findViewById(R.id.tvWord);
        tvPronunciation = findViewById(R.id.tvPronunciation);

        flipIn = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.anim.flip_in);
        flipOut = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.anim.flip_out);

        btnDefinition.setOnClickListener(v -> flipCard(
                "Definition: A message or document, or a set of messages or documents, sent using this system"));
        btnExample.setOnClickListener(v -> flipCard("Example: I sent an email to my boss"));

        btnX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // Đóng Activity hiện tại
                overridePendingTransition(R.anim.stay, R.anim.slide_down); // Áp dụng hiệu ứng
            }
        });

        // Lấy ID flashcard từ Intent
        int flashcardId = getIntent().getIntExtra("FLASHCARD_ID", -1);
        if (flashcardId != -1) {
            // Gọi phương thức để lấy dữ liệu flashcard dựa trên ID
            fetchFlashcardData(flashcardId);
        } else {
            // Xử lý trường hợp không có ID hợp lệ
            Toast.makeText(this, "Không tìm thấy flashcard", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchFlashcardData(int flashcardId) {
        FlashcardManager flashcardManager = new FlashcardManager();
        flashcardManager.fetchFlashcardById(flashcardId, new FlashcardApiCallback() {
            @Override
            public void onSuccess(ApiResponseFlashcardGroup response) {

            }

            @Override
            public void onSuccess(FlashcardGroupResponse response) {

            }

            @Override
            public void onSuccess(ApiResponseFlashcard response) {

            }

            @Override
            public void onSuccess(ApiResponseOneFlashcard response) {
                runOnUiThread(() -> {
                    // Lấy dữ liệu flashcard từ response
                    Flashcard flashcard = response.getData(); // Bây giờ getData() trả về Flashcard

                    // Cập nhật UI với dữ liệu flashcard
                    tvWord.setText(flashcard.getWord());
                    tvPronunciation.setText(flashcard.getPhoneticText());
                    tvBackContent.setText(flashcard.getDefinitions());
                    tvAddedDate.setText("Added date: " + flashcard.getAddedDate());

                    // Hiển thị ví dụ nếu có
                    if (flashcard.getExamples() != null && !flashcard.getExamples().isEmpty()) {
                        tvExamples.setText(flashcard.getExamples());
                    } else {
                        tvExamples.setText("No examples available.");
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    Toast.makeText(FlashcardInformationActivity.this, "Error fetching flashcard: " + errorMessage,
                            Toast.LENGTH_LONG).show();
                });
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

                @Override
                public void onAnimationStart(android.animation.Animator animation) {
                }

                @Override
                public void onAnimationCancel(android.animation.Animator animation) {
                }

                @Override
                public void onAnimationRepeat(android.animation.Animator animation) {
                }
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

                @Override
                public void onAnimationStart(android.animation.Animator animation) {
                }

                @Override
                public void onAnimationCancel(android.animation.Animator animation) {
                }

                @Override
                public void onAnimationRepeat(android.animation.Animator animation) {
                }
            });
        }
        isFrontVisible = !isFrontVisible;
    }
}