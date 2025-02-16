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
import android.util.Log;
import android.media.MediaPlayer;
import android.media.AudioAttributes;
import java.io.IOException;

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
    private AppCompatButton btnDefinition;
    private AppCompatButton btnExample;
    private ImageView btnSound;
    private MediaPlayer mediaPlayer;

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

        initializeViews();
        setupAnimations();
        setupClickListeners();

        // Lấy ID flashcard từ Intent
        int flashcardId = getIntent().getIntExtra("FLASHCARD_ID", -1);
        if (flashcardId != -1) {
            Log.d("FlashcardInfo", "Starting to fetch flashcard with ID: " + flashcardId);
            fetchFlashcardData(flashcardId);
        } else {
            Log.e("FlashcardInfo", "Invalid flashcard ID");
            Toast.makeText(this, "Không tìm thấy flashcard", Toast.LENGTH_SHORT).show();
        }

        mediaPlayer = new MediaPlayer();
    }

    private void initializeViews() {
        frontSide = findViewById(R.id.frontSide);
        backSide = findViewById(R.id.backSide);
        tvBackContent = findViewById(R.id.tvBackContent);
        btnDefinition = findViewById(R.id.btnDefinition);
        btnExample = findViewById(R.id.btnExample);
        btnX = findViewById(R.id.btnX);
        tvAddedDate = findViewById(R.id.tvAddedDate);
        tvExamples = findViewById(R.id.tvExamples);
        tvWord = findViewById(R.id.tvWord);
        tvPronunciation = findViewById(R.id.tvPronunciation);
        btnSound = findViewById(R.id.btnAudio);
    }

    private void setupAnimations() {
        flipIn = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.flip_in);
        flipOut = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.flip_out);
    }

    private void setupClickListeners() {
        btnX.setOnClickListener(view -> {
            finish();
            overridePendingTransition(R.anim.stay, R.anim.slide_down);
        });

    }

    private void fetchFlashcardData(int flashcardId) {
        Log.d("FlashcardInfo", "Fetching flashcard with ID: " + flashcardId);

        FlashcardManager flashcardManager = new FlashcardManager();
        flashcardManager.fetchFlashcardById(flashcardId, new FlashcardApiCallback() {
            @Override
            public void onSuccess(ApiResponseOneFlashcard response) {
                Log.d("FlashcardInfo", "API call successful");
                if (response != null && response.getData() != null) {
                    Flashcard flashcard = response.getData();
                    Log.d("FlashcardInfo", "Received flashcard: " + flashcard.toString());
                    runOnUiThread(() -> updateUI(flashcard));
                } else {
                    Log.e("FlashcardInfo", "Response or data is null");
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("FlashcardInfo", "API call failed: " + errorMessage);
                runOnUiThread(() -> {
                    Toast.makeText(FlashcardInformationActivity.this,
                            "Lỗi khi lấy dữ liệu: " + errorMessage,
                            Toast.LENGTH_LONG).show();
                });
            }

            // Implement remaining callback methods
            @Override
            public void onSuccess(ApiResponseFlashcardGroup response) {
            }

            @Override
            public void onSuccess(FlashcardGroupResponse response) {
            }

            @Override
            public void onSuccess(ApiResponseFlashcard response) {
            }
        });
    }

    private void updateUI(Flashcard flashcard) {
        if (flashcard != null) {
            Log.d("FlashcardInfo", "Updating UI with flashcard data");
            tvWord.setText(flashcard.getWord());
            tvPronunciation.setText(flashcard.getPhoneticText());
            tvAddedDate.setText("Added date: " + flashcard.getAddedDate());

            // Thiết lập sự kiện click cho các nút với dữ liệu từ flashcard
            final String definitions = flashcard.getDefinitions();
            final String examples = flashcard.getExamples();

            btnDefinition.setOnClickListener(v -> {
                Log.d("FlashcardInfo", "Definition button clicked. Content: " + definitions);
                flipCard(definitions);
            });

            btnExample.setOnClickListener(v -> {
                Log.d("FlashcardInfo", "Example button clicked. Content: " + examples);
                flipCard(examples);
            });

            // Xử lý nút phát âm thanh
            String audioUrlRaw = flashcard.getPhoneticAudio();
            if (audioUrlRaw != null && !audioUrlRaw.isEmpty()) {
                // Loại bỏ dấu chấm phẩy và khoảng trắng ở cuối URL
                final String audioUrl = audioUrlRaw.trim().replaceAll(";\\s*$", "");
                Log.d("FlashcardInfo", "Cleaned Audio URL: " + audioUrl);

                btnSound.setEnabled(true);
                btnSound.setOnClickListener(v -> {
                    Toast.makeText(this, "Đang tải âm thanh...", Toast.LENGTH_SHORT).show();
                    playAudio(audioUrl);
                });
            } else {
                Log.w("FlashcardInfo", "No audio URL available");
                btnSound.setEnabled(false);
            }

            Log.d("FlashcardInfo", "UI update completed");
        } else {
            Log.e("FlashcardInfo", "Cannot update UI - flashcard is null");
        }
    }

    private void flipCard(String content) {
        Log.d("FlashcardInfo", "Flipping card with content: " + content);
        if (isFrontVisible) {
           // flipOut.setTarget(frontSide);
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

    private void playAudio(String audioUrl) {
        try {
            // Reset MediaPlayer nếu đang phát
            mediaPlayer.reset();

            // Hiển thị loading indicator
            btnSound.setEnabled(false);

            Log.d("FlashcardInfo", "Starting to play audio from URL: " + audioUrl);

            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build());

            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(mp -> {
                Log.d("FlashcardInfo", "MediaPlayer prepared, starting playback");
                btnSound.setEnabled(true);
                mp.start();
                Toast.makeText(this, "Đang phát âm thanh", Toast.LENGTH_SHORT).show();
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                Log.d("FlashcardInfo", "Playback completed");
                btnSound.setEnabled(true);
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                btnSound.setEnabled(true);
                String errorMessage = "Lỗi: " + what + ", " + extra;
                Toast.makeText(this, "Không thể phát âm thanh: " + errorMessage, Toast.LENGTH_SHORT).show();
                Log.e("FlashcardInfo", "MediaPlayer error: " + errorMessage);
                return true;
            });

        } catch (IOException e) {
            Log.e("FlashcardInfo", "Error playing audio", e);
            Toast.makeText(this, "Lỗi khi phát âm thanh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            btnSound.setEnabled(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}