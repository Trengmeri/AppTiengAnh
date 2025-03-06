package com.example.test.ui;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RecordQuestionActivity extends AppCompatActivity implements SpeechRecognitionCallback {

    private MediaPlayer mediaPlayer;
    private MediaRecorder mediaRecorder;
    private String audioFilePath;
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
    private int totalSteps;
    QuestionManager quesManager = new QuestionManager(this);
    LessonManager lesManager = new LessonManager();
    ResultManager resultManager = new ResultManager(this);
    MediaManager mediaManager = new MediaManager(this);
    private int answerIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_question);

        imgVoice = findViewById(R.id.imgVoice);
        tvTranscription = findViewById(R.id.tvTranscription);
        btnPlayAudio = findViewById(R.id.btn_play);
        Button btnCheckResult = findViewById(R.id.btnCheckResult);
        seekBar = findViewById(R.id.seekBar);

        audioFilePath = getExternalCacheDir().getAbsolutePath() + "/audio_record.3gp";

        currentQuestionIndex = getIntent().getIntExtra("currentQuestionIndex", 0);
        questions = (List<Question>) getIntent().getSerializableExtra("questions");
        courseID = getIntent().getIntExtra("courseID", 1);
        lessonID = getIntent().getIntExtra("lessonID", 1);

        loadQuestion(currentQuestionIndex);
        fetchAudioUrl(questions.get(currentQuestionIndex).getId());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        } else {
            imgVoice.setOnClickListener(v -> startRecording());
        }

        LinearLayout progressBar = findViewById(R.id.progressBar);

        btnCheckResult.setOnClickListener(v -> {
            stopRecording();
            String userAnswer = tvTranscription.getText().toString().trim();
            userAnswers.clear();
            userAnswers.add(userAnswer);
            handleUserAnswer();
        });
    }

    private void fetchAudioUrl(int questionId) {
        mediaManager.fetchMediaByQuesId(questionId, new ApiCallback<MediaFile>() {
            @Override
            public void onSuccess(MediaFile mediaFile) {
                runOnUiThread(() -> {
                    if (mediaFile != null) {
                        btnPlayAudio.setOnClickListener(v -> {
                            playAudio(mediaFile.getMaterLink());
                        });
                    }
                });
            }

            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("media", errorMessage);
            }
        });
    }

    private void playAudio(String audioUrl) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(MediaPlayer::start);
            mediaPlayer.setOnCompletionListener(mp -> seekBar.setProgress(0));
        } catch (Exception e) {
            Log.e("MediaPlayerError", "Error: " + e.getMessage());
        }
    }

    private void initializeMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(audioFilePath);
    }

    private void startRecording() {
        try {
            initializeMediaRecorder();
            mediaRecorder.prepare();
            mediaRecorder.start();
            Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("MediaRecorder", "prepare() failed");
        }
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show();
                mediaManager.uploadMp3File(audioFilePath);
            } catch (RuntimeException e) {
                Log.e("MediaRecorder", "stop() failed: " + e.getMessage());
            }
        }
    }

    private void handleUserAnswer() {
        if (userAnswers.isEmpty()) {
            Toast.makeText(this, "Vui lòng trả lời câu hỏi!", Toast.LENGTH_SHORT).show();
        } else {
            quesManager.saveUserAnswer(questions.get(currentStep).getId(), userAnswers.get(0), new ApiCallback() {
                @Override
                public void onSuccess() {
                    Log.d("RecordQuestionActivity", "Câu trả lời đã được lưu: " + userAnswers.toString());
                    runOnUiThread(() -> {
                        PopupHelper.showResultPopup(RecordQuestionActivity.this, userAnswers, correctAnswers, () -> {
                            currentStep++;
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
                    gradeAnswer();
                }

                @Override
                public void onSuccess(Object result) {}

                @Override
                public void onFailure(String errorMessage) {
                    Log.e("RecordQuestionActivity", errorMessage);
                }
            });
        }
    }

    private void loadQuestion(int index) {
        if (index < questions.size()) {
            Question question = questions.get(index);
            quesManager.fetchQuestionContentFromApi(question.getId(), new ApiCallback<Question>() {
                @Override
                public void onSuccess(Question question) {
                    if (question != null) {
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

    private void gradeAnswer() {
        resultManager.fetchAnswerPointsByQuesId(questions.get(currentStep).getId(), new ApiCallback<Answer>() {
            @Override
            public void onSuccess(Answer answer) {
                if (answer != null) {
                    answerIds = answer.getId();
                    if (answerIds != 0) {
                        QuestionManager.gradeAnswer(answerIds, new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {}

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {}
                        });
                    } else {
                        Log.e("RecordQuestionActivity", "Bài học không có câu trả lời.");
                    }
                } else {
                    Log.e("RecordQuestionActivity", "Không nhận được câu trả lời từ API.");
                }
            }

            @Override
            public void onSuccess() {}

            @Override
            public void onFailure(String errorMessage) {}
        });
    }


    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
        if (speechRecognitionHelper != null) {
            speechRecognitionHelper.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            imgVoice.setOnClickListener(v -> startRecording());
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onReadyForSpeech() {

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

    }

    @Override
    public void onError(int error) {

    }

    @Override
    public void onResults(ArrayList<String> matches) {

    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }
}
