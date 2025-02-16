//package com.example.test.api;
package com.example.test.ui;
import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
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
import com.example.test.api.ApiCallback;
import com.example.test.api.LessonManager;
import com.example.test.api.QuestionManager;
import com.example.test.api.ResultManager;
import com.example.test.model.Answer;
import com.example.test.model.Course;
import com.example.test.model.Enrollment;
import com.example.test.model.Lesson;
import com.example.test.model.MediaFile;
import com.example.test.model.Question;
import com.example.test.model.QuestionChoice;
import com.example.test.model.Result;

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
    private List<Integer> questionIds;
    private int currentStep = 0; // Bước hiện tại (bắt đầu từ 0)
    private int totalSteps; // Tổng số bước trong thanh tiến trình
    QuestionManager quesManager = new QuestionManager(this);
    LessonManager lesManager = new LessonManager();
    ResultManager resultManager = new ResultManager(this);
    private int answerIds;// Danh sách questionIds


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_question);

        imgVoice = findViewById(R.id.imgVoice);
        tvTranscription = findViewById(R.id.tvTranscription);
        btnPlayAudio = findViewById(R.id.btn_play);
        Button btnCheckResult = findViewById(R.id.btnCheckResult);
        seekBar = findViewById(R.id.seekBar);
        int lessonId = 4;
        fetchLessonAndQuestions(lessonId);

        // Kiểm tra quyền microphone
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        } else {
            // Khởi tạo SpeechRecognitionHelper
            imgVoice.setOnClickListener(v ->initializeSpeechRecognition());
        }

        // Audio playback setup
        mediaPlayer = MediaPlayer.create(this, R.raw.uaifein); // Thay thế với tệp âm thanh của bạn
        btnPlayAudio.setOnClickListener(v -> {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                updateSeekBar();
            }
        });

        mediaPlayer.setOnCompletionListener(mp -> seekBar.setProgress(0));

        LinearLayout progressBar = findViewById(R.id.progressBar); // Ánh xạ ProgressBar


        btnCheckResult.setOnClickListener(v -> {
            String userAnswer = tvTranscription.getText().toString().trim();
            userAnswers.clear(); // Xóa các câu trả lời trước đó
            userAnswers.add(userAnswer); // Thêm câu trả lời mới vào danh sách
            Log.d("RecordQuestionActivity", "User Answers: " + userAnswers);

            if (userAnswers.isEmpty()) {
                Toast.makeText(RecordQuestionActivity.this, "Vui lòng trả lời câu hỏi!", Toast.LENGTH_SHORT).show();
            } else {
                // Lưu câu trả lời của người dùng
                quesManager.saveUserAnswer(questionIds.get(currentStep), userAnswer, new ApiCallback() {
                    @Override
                    public void onSuccess() {
                        Log.e("RecordQuestionActivity", "Câu trả lời đã được lưu: " + userAnswers.toString());
                        // Hiển thị kết quả sau khi lưu thành công
                        runOnUiThread(() -> {
                            PopupHelper.showResultPopup(findViewById(R.id.popupContainer), userAnswers, correctAnswers, () -> {
                                // Callback khi nhấn Next Question trên popup
                                currentStep++; // Tăng currentStep

                                // Kiểm tra nếu hoàn thành
                                if (currentStep < totalSteps) {
                                    fetchQuestion(questionIds.get(currentStep)); // Lấy câu hỏi tiếp theo
                                    updateProgressBar(progressBar, currentStep); // Cập nhật thanh tiến trình
                                } else {
                                    Intent intent = new Intent(RecordQuestionActivity.this, PointResultActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        });
                        resultManager.fetchAnswerPointsByQuesId(questionIds.get(currentStep), new ApiCallback() {
                            @Override
                            public void onSuccess() {
                            }
                            @Override
                            public void onSuccess(Enrollment enrollment) {}

                            @Override
                            public void onSuccess(Question questions) {

                            }

                            @Override
                            public void onSuccess(Lesson lesson) {

                            }

                            @Override
                            public void onSuccess(Course course) {

                            }

                            @Override
                            public void onSuccess(Result result) {

                            }

                            @Override
                            public void onSuccess(Answer answer) {
                                if (answer != null) {
                                    answerIds = answer.getId();
                                    Log.e("RecordQuestionActivity", "Answer ID từ API: " + answer.getId());
                                    if (answerIds != 0) {
                                        QuestionManager.gradeAnswer(answerIds, new Callback() {
                                            @Override
                                            public void onFailure(@NonNull Call call, @NonNull IOException e) {

                                            }

                                            @Override
                                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                                            }
                                        });
                                    } else {
                                        Log.e("RecordQuestionActivity", "Bài học không có câu trl.");
                                    }
                                } else {
                                    Log.e("RecordQuestionActivity", "Không nhận được câu trả lời từ API.");
                                }
                            }

                            @Override
                            public void onSuccess(MediaFile mediaFile) {

                            }


                            @Override
                            public void onFailure(String errorMessage) {

                            }

                            @Override
                            public void onSuccessWithOtpID(String otpID) {

                            }

                            @Override
                            public void onSuccessWithToken(String token) {

                            }
                        });
                    }

                    @Override
                    public void onSuccess(Question question) {}

                    @Override
                    public void onSuccess(Lesson lesson) {}
                    @Override
                    public void onSuccess(Enrollment enrollment) {}

                    @Override
                    public void onSuccess(Course course) {}

                    @Override
                    public void onSuccess(Result result) {}

                    @Override
                    public void onSuccess(Answer answer) {}

                    @Override
                    public void onSuccess(MediaFile mediaFile) {

                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Log.e("RecordQuestionActivity", errorMessage);

                    }

                    @Override
                    public void onSuccessWithOtpID(String otpID) {

                    }

                    @Override
                    public void onSuccessWithToken(String token) {

                    }
                });
            }
        });
    }

    private void fetchLessonAndQuestions(int lessonId) {
        lesManager.fetchLessonById(lessonId, new ApiCallback() {
            @Override
            public void onSuccess(Lesson lesson) {
                if (lesson != null) {
                    questionIds = lesson.getQuestionIds();
                    Log.d("RecordQuestionActivity", "Danh sách questionIds: " + questionIds);
                    totalSteps = questionIds.size();
                    if (questionIds != null && !questionIds.isEmpty()) {
                        fetchQuestion(questionIds.get(currentStep));
                    } else {
                        Log.e("RecordQuestionActivity", "Bài học không có câu hỏi.");
                    }
                } else {
                    Log.e("RecordQuestionActivity", "Bài học trả về là null.");
                }
            }

            @Override
            public void onSuccess(Course course) {}
            @Override
            public void onSuccess(Enrollment enrollment) {}

            @Override
            public void onSuccess(Result result) {}

            @Override
            public void onSuccess(Answer answer) {}

            @Override
            public void onSuccess(MediaFile mediaFile) {

            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("RecordQuestionActivity", errorMessage);
            }

            @Override
            public void onSuccessWithOtpID(String otpID) {}

            @Override
            public void onSuccessWithToken(String token) {

            }

            @Override
            public void onSuccess() {}

            @Override
            public void onSuccess(Question question) {}
        });
    }

    private void fetchQuestion(int questionId) {
        quesManager.fetchQuestionContentFromApi(questionId, new ApiCallback() {
            @Override
            public void onSuccess(Question question) {
                if (question != null) {
                    // Cập nhật giao diện người dùng với nội dung câu hỏi
                    runOnUiThread(() -> {
                        // Giả sử bạn có một TextView để hiển thị câu hỏi
                        TextView tvQuestion = findViewById(R.id.tvQuestion);
                        tvQuestion.setText(question.getQuesContent());

                        // Lưu trữ đáp án đúng để kiểm tra sau
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
                Log.e("RecordQuestionActivity", errorMessage);
            }

            @Override
            public void onSuccess(Lesson lesson) {}
            @Override
            public void onSuccess(Enrollment enrollment) {}

            @Override
            public void onSuccess(Result result) {}

            @Override
            public void onSuccess(Answer answer) {}

            @Override
            public void onSuccess(MediaFile mediaFile) {

            }

            @Override
            public void onSuccess(Course course) {}

            @Override
            public void onSuccessWithOtpID(String otpID) {}

            @Override
            public void onSuccessWithToken(String token) {

            }

            @Override
            public void onSuccess() {}
        });
    }

    private void initializeSpeechRecognition() {
        speechRecognitionHelper = new SpeechRecognitionHelper(this, this);
        speechRecognitionHelper.startListening();
    }

    private void updateProgressBar(LinearLayout progressBarSteps, int step) {
        if (step < progressBarSteps.getChildCount()) {
            final View currentStepView = progressBarSteps.getChildAt(step);

            // Animation thay đổi màu
            ObjectAnimator colorAnimator = ObjectAnimator.ofArgb(
                    currentStepView,
                    "backgroundColor",
                    Color.parseColor("#E0E0E0"),  // Màu ban đầu
                    Color.parseColor("#C4865E")   // Màu đã hoàn thành
            );
            colorAnimator.setDuration(200); // Thời gian chuyển đổi màu
            colorAnimator.start();
        }
    }

    @Override
    public void onReadyForSpeech() {
        Toast.makeText(this, "Listening...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBeginningOfSpeech() {
        // Gọi khi bắt đầu nhận diện giọng nói
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        // Gọi khi thay đổi âm lượng
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        // Gọi khi nhận được dữ liệu
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
        }
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        // Gọi khi nhận được kết quả từng phần
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        // Gọi khi có sự kiện tùy chỉnh
    }

    private void updateSeekBar() {
        seekBar.setMax(mediaPlayer.getDuration());
        new Thread(() -> {
            while (mediaPlayer.isPlaying()) {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (speechRecognitionHelper != null) {
            speechRecognitionHelper.destroy();
        }
        super.onDestroy();
    }

    // Xử lý yêu cầu quyền
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initializeSpeechRecognition();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }
}
