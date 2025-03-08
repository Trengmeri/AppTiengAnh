package com.example.test.ui.question_data;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.test.PopupHelper;
import com.example.test.R;
import com.example.test.SpeechRecognitionCallback;
import com.example.test.SpeechRecognitionHelper;
import com.example.test.api.*;
import com.example.test.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RecordQuestionActivity extends AppCompatActivity implements SpeechRecognitionCallback {

    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private ImageView imgVoice, btnPlayAudio;
    private TextView tvTranscription;
    private SpeechRecognitionHelper speechRecognitionHelper;

    private List<String> userAnswers = new ArrayList<>();
    List<String> correctAnswers = new ArrayList<>();
    private List<Question> questions;
    private int currentQuestionIndex;
    private int currentStep = 0;
    private int lessonID, courseID;
    private  String questype;
    private int totalSteps;
    QuestionManager quesManager = new QuestionManager(this);
    LessonManager lesManager = new LessonManager();
    ResultManager resultManager = new ResultManager(this);
    MediaManager mediaManager = new MediaManager(this);
    private int answerIds;
    private Handler handler = new Handler();
    private Runnable updateSeekBar;
    private boolean isPlaying = false;
    TextView tvQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_question);

        imgVoice = findViewById(R.id.imgVoice);
        tvTranscription = findViewById(R.id.tvTranscription);
        btnPlayAudio = findViewById(R.id.btn_play);
        Button btnCheckResult = findViewById(R.id.btnCheckResult);
        seekBar = findViewById(R.id.seekBar);
        tvQuestion = findViewById(R.id.tvQuestion);


        currentQuestionIndex = getIntent().getIntExtra("currentQuestionIndex", 0);
        questions = (List<Question>) getIntent().getSerializableExtra("questions");
        courseID = getIntent().getIntExtra("courseID", 1);
        lessonID = getIntent().getIntExtra("lessonID", 1);

        loadQuestion(currentQuestionIndex);
        fetchAudioUrl(questions.get(currentQuestionIndex).getId());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        } else {
            imgVoice.setOnClickListener(v -> initializeSpeechRecognition());
        }

        LinearLayout progressBar = findViewById(R.id.progressBar);

        btnCheckResult.setOnClickListener(v -> {
            mediaPlayer.pause();
            String userAnswer = tvTranscription.getText().toString().trim();
            userAnswers.clear();
            userAnswers.add(userAnswer);
            if (userAnswers.isEmpty()) {
                Toast.makeText(this, "Vui lòng trả lời câu hỏi!", Toast.LENGTH_SHORT).show();
            } else {
                checkAnswer(userAnswer);
            }
        });
    }

    private void checkAnswer(String userAnswer) {
        String questionContent = tvQuestion.getText().toString().trim();
        ApiService apiService = new ApiService(this);

        apiService.sendAnswerToApi(questionContent, userAnswer, new ApiCallback<EvaluationResult>() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onSuccess(EvaluationResult result) {


                // Lưu kết quả vào hệ thống
                quesManager.saveUserAnswer(questions.get(currentStep).getId(), userAnswer, result.getPoint(), result.getimprovements(), new ApiCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("RecordQuestionActivity.this", "Lưu thành công!");
                        runOnUiThread(() -> {
                            PopupHelper.showResultPopup(RecordQuestionActivity.this, questype, null, null, result.getPoint(), result.getimprovements(), result.getevaluation(), () -> {
                                currentStep++; // Tăng currentStep
                                currentQuestionIndex++;
                                if (currentQuestionIndex < questions.size()) {
                                    updateProgressBar(findViewById(R.id.progressBar), currentStep);
                                    fetchAudioUrl(questions.get(currentQuestionIndex).getId());
                                    loadQuestion(currentQuestionIndex);
                                } else {
                                    finishLesson();
                                }
                            });
                        });
                    }

                    @Override
                    public void onSuccess(Object result) {

                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Log.e("RecordQuestionActivity.this", "Lỗi lưu câu trả lời: " + errorMessage);
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("RecordQuestionActivity", "Lỗi API: " + errorMessage);
            }
        });
    }

    private void fetchAudioUrl(int questionId) {
        mediaManager.fetchMediaByQuesId(questionId, new ApiCallback<MediaFile>() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onSuccess(MediaFile mediaFile) {
                runOnUiThread(() -> {
                    if (mediaFile != null && mediaFile.getMaterLink() != null) {
                        String modifiedLink = mediaFile.getMaterLink().replace("0.0.0.0", "14.225.198.3");
                        setupAudioPlayer(modifiedLink);
                    } else {
                        Log.e("MediaPlayerError", "Media file is null or has no link");
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("MediaPlayerError", "Error fetching audio: " + errorMessage);
            }
        });
    }

    private void setupAudioPlayer(String audioUrl) {
        btnPlayAudio.setOnClickListener(v -> {
            if (mediaPlayer == null) playAudio(audioUrl);

            if (isPlaying) {
                mediaPlayer.pause();
                btnPlayAudio.setImageResource(R.drawable.btn_play); // Đổi icon play
            } else {
                mediaPlayer.start();
                btnPlayAudio.setImageResource(R.drawable.ic_pause); // Đổi icon pause
                updateSeekBar();
            }
            isPlaying = !isPlaying;
        });


        // Xử lý khi kéo SeekBar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(updateSeekBar);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null) {
                    mediaPlayer.seekTo(seekBar.getProgress());
                    updateSeekBar();
                }
            }
        });
    }

    private void playAudio(String audioUrl) {
        if (audioUrl == null || audioUrl.isEmpty()) {
            Log.e("MediaPlayerError", "Audio URL is null or empty");
            return;
        }

        try {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build());

            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.setOnPreparedListener(mp -> {
                seekBar.setMax(mediaPlayer.getDuration());
                mediaPlayer.start();
                isPlaying = true;
                btnPlayAudio.setImageResource(R.drawable.ic_pause);
                updateSeekBar();
            });

            // Khi nhạc phát xong, reset lại nút Play
            mediaPlayer.setOnCompletionListener(mp -> {
                btnPlayAudio.setImageResource(R.drawable.btn_play);
                isPlaying = false;
                seekBar.setProgress(0);
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e("MediaPlayerError", "Error: what=" + what + ", extra=" + extra);
                return true;
            });

            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            Log.e("MediaPlayerError", "IOException: " + e.getMessage());
        }
    }

    // Cập nhật SeekBar theo thời gian
    private void updateSeekBar() {
        updateSeekBar = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    handler.postDelayed(this, 500);
                }
            }
        };
        handler.postDelayed(updateSeekBar, 500);
    }


    private void initializeSpeechRecognition() {
        speechRecognitionHelper = new SpeechRecognitionHelper(this, this);
        speechRecognitionHelper.startListening();
    }

    @Override
    public void onReadyForSpeech() {
        Toast.makeText(this, "Listening...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBeginningOfSpeech() {
    }

    @Override
    public void onRmsChanged(float rmsdB) {
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
    }

    @Override
    public void onEndOfSpeech() {
        Toast.makeText(this, "Processing...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(int error) {
        Log.e("SpeechRecognizerError", "Error code: " + error);
        Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResults(ArrayList<String> matches) {
        if (matches != null && !matches.isEmpty()) {
            tvTranscription.setText(matches.get(0));
            userAnswers.clear();
            userAnswers.add(matches.get(0));
        }
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
    }

    private void loadQuestion(int index) {
        if (index < questions.size()) {
            Question question = questions.get(index);
            quesManager.fetchQuestionContentFromApi(question.getId(), new ApiCallback<Question>() {
                @Override
                public void onSuccess(Question question) {
                    if (question != null) {
                        questype = question.getQuesType();
                        runOnUiThread(() -> {
                            TextView tvQuestion = findViewById(R.id.tvQuestion);
                            tvQuestion.setText(question.getQuesContent());

                            List<QuestionChoice> choices = question.getQuestionChoices();
                            correctAnswers.clear();
                            for (QuestionChoice choice : choices) {
                                if (choice.isChoiceKey()) {
                                    correctAnswers.add(choice.getChoiceContent());
                                }
                            }
                        });
                    } else {
                        Log.e("RecordQuestionActivity", "Câu hỏi trả về là null.");
                    }
                }

                @Override
                public void onFailure(String errorMessage) {
                    Log.e("GrammarPick1QuestionActivity", errorMessage);
                }

                @Override
                public void onSuccess() {
                }
            });
        } else {
            finishLesson();
        }
    }

    private void finishLesson() {
        Intent intent = new Intent(RecordQuestionActivity.this, PointResultLessonActivity.class);
        intent.putExtra("lessonId", lessonID);
        intent.putExtra("courseId", courseID);
        startActivity(intent);
        finish();
    }

    private void updateProgressBar(LinearLayout progressBarSteps, int step) {
        if (step < progressBarSteps.getChildCount()) {
            final View currentStepView = progressBarSteps.getChildAt(step);

            ObjectAnimator colorAnimator = ObjectAnimator.ofArgb(
                    currentStepView,
                    "backgroundColor",
                    Color.parseColor("#E0E0E0"),
                    Color.parseColor("#C4865E")
            );
            colorAnimator.setDuration(200);
            colorAnimator.start();
        }
    }
}
