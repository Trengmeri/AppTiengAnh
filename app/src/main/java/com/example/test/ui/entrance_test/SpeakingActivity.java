package com.example.test.ui.entrance_test;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import android.os.Handler;
import android.media.AudioAttributes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.test.PopupHelper;
import com.example.test.R;
import com.example.test.SpeechRecognitionCallback;
import com.example.test.SpeechRecognitionHelper;
import com.example.test.api.ApiCallback;
import com.example.test.api.ApiService;
import com.example.test.api.LessonManager;
import com.example.test.api.MediaManager;
import com.example.test.api.QuestionManager;
import com.example.test.api.ResultManager;
import com.example.test.model.EvaluationResult;
import com.example.test.model.Lesson;
import com.example.test.model.MediaFile;
import com.example.test.model.Question;
import com.example.test.model.QuestionChoice;
import com.example.test.ui.question_data.PointResultCourseActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SpeakingActivity extends AppCompatActivity implements SpeechRecognitionCallback {

    private MediaPlayer mediaPlayer;
    private ImageView imgVoice;
    private TextView tvTranscription;
    private SpeechRecognitionHelper speechRecognitionHelper;

    private List<String> userAnswers = new ArrayList<>();
    List<String> correctAnswers = new ArrayList<>();
    private List<Integer> questionIds;
    private int currentStep = 0;
    private int totalSteps;
    QuestionManager quesManager = new QuestionManager(this);
    LessonManager lesManager = new LessonManager();
    ResultManager resultManager = new ResultManager(this);
    MediaManager mediaManager = new MediaManager(this);
    private int answerIds;
    private Handler handler = new Handler();
    private  String questype;
    private Runnable updateSeekBar;
    private boolean isPlaying = false;
    TextView tvQuestion;
    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_question);

        imgVoice = findViewById(R.id.imgVoice);
        tvTranscription = findViewById(R.id.tvTranscription);
        Button btnCheckResult = findViewById(R.id.btnCheckResult);
        tvQuestion = findViewById(R.id.tvQuestion);
        int lessonId = 4;
        fetchLessonAndQuestions(lessonId);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        } else {
            imgVoice.setOnClickListener(v -> initializeSpeechRecognition());
        }

        LinearLayout progressBar = findViewById(R.id.progressBar);

        btnCheckResult.setOnClickListener(v -> {
            String userAnswer = tvTranscription.getText().toString().trim();
            userAnswers.clear();
            userAnswers.add(userAnswer);

            if (userAnswers.isEmpty()) {
                Toast.makeText(SpeakingActivity.this, "Vui lòng trả lời câu hỏi!", Toast.LENGTH_SHORT).show();
            } else {
                checkAnswer(userAnswer);
            }
        });
    }

    private void checkAnswer(String userAnswer) {
        String questionContent = tvQuestion.getText().toString().trim();
        ApiService apiService = new ApiService(this);
        // Hiển thị ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.load));
        progressDialog.setCancelable(false);
        progressDialog.show();

        apiService.sendAnswerToApi(questionContent, userAnswer, new ApiCallback<EvaluationResult>() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onSuccess(EvaluationResult result) {


                // Lưu kết quả vào hệ thống
                quesManager.saveUserAnswer(questionIds.get(currentStep), userAnswer, result.getPoint(), result.getimprovements(), new ApiCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("SpeakingActivity.this", "Lưu thành công!");
                        progressDialog.dismiss();
                        runOnUiThread(() -> {
                            PopupHelper.showResultPopup(SpeakingActivity.this, questype, null, null, result.getPoint(), result.getimprovements(), result.getevaluation(), () -> {
                                currentStep++;
                                if (currentStep < totalSteps) {
                                    fetchQuestion(questionIds.get(currentStep));
                                } else {
                                    Intent intent = new Intent(SpeakingActivity.this, WritingActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        });
                    }

                    @Override
                    public void onSuccess(Object result) {

                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Log.e("SpeakingActivity.this", "Lỗi lưu câu trả lời: " + errorMessage);
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("SpeakingActivity", "Lỗi API: " + errorMessage);
            }
        });
    }

    private void fetchLessonAndQuestions(int lessonId) {
        lesManager.fetchLessonById(lessonId, new ApiCallback<Lesson>() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onSuccess(Lesson lesson) {
                if (lesson != null) {
                    questionIds = lesson.getQuestionIds();
                    totalSteps = questionIds.size();
                    if (questionIds != null && !questionIds.isEmpty()) {
                        fetchQuestion(questionIds.get(currentStep));
                    }
                }
            }
            @Override
            public void onFailure(String errorMessage) {}
        });
    }

    private void fetchQuestion(int questionId) {
        quesManager.fetchQuestionContentFromApi(questionId, new ApiCallback<Question>() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onSuccess(Question question) {
                if (question != null) {
                    questype = question.getQuesType();
                    runOnUiThread(() -> {
                        tvQuestion.setText(question.getQuesContent());
                    });
                }
            }
            @Override
            public void onFailure(String errorMessage) {}
        });
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

    private void initializeSpeechRecognition() {
        speechRecognitionHelper = new SpeechRecognitionHelper(this, this);
        speechRecognitionHelper.startListening();
    }
}
