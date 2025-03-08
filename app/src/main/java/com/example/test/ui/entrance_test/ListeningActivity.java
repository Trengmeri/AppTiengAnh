package com.example.test.ui.entrance_test;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.test.PopupHelper;
import com.example.test.R;
import com.example.test.api.ApiCallback;
import com.example.test.api.ApiService;
import com.example.test.api.LessonManager;
import com.example.test.api.MediaManager;
import com.example.test.api.QuestionManager;
import com.example.test.api.ResultManager;
import com.example.test.model.Answer;
import com.example.test.model.Course;
import com.example.test.model.Discussion;
import com.example.test.model.EvaluationResult;
import com.example.test.model.Lesson;
import com.example.test.model.MediaFile;
import com.example.test.model.Question;
import com.example.test.model.QuestionChoice;
import com.example.test.model.Result;
import com.example.test.ui.question_data.PointResultCourseActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ListeningActivity extends AppCompatActivity {
    List<String> correctAnswers = new ArrayList<>();
    private MediaPlayer mediaPlayer;
    private EditText etAnswer;
    private List<Integer> questionIds;
    private  String questype;
    private List<String> userAnswers = new ArrayList<>();
    private int currentStep = 0; // Bước hiện tại (bắt đầu từ 0)
    private int totalSteps; // Tổng số bước trong thanh tiến trình
    private int answerIds;
    ImageView btnListen;
    TextView tvQuestion;


    QuestionManager quesManager = new QuestionManager(this);
    LessonManager lesManager = new LessonManager();
    ResultManager resultManager = new ResultManager(this);
    MediaManager mediaManager = new MediaManager(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_listening_question);

        btnListen = findViewById(R.id.btnListen);
        Button btnCheckResult = findViewById(R.id.btnCheckResult);
        tvQuestion = findViewById(R.id.tvQuestion);
        etAnswer = findViewById(R.id.etAnswer);
        int lessonId = 3;
        fetchLessonAndQuestions(lessonId);

        LinearLayout progressBar = findViewById(R.id.progressBar); // Ánh xạ ProgressBar



        btnCheckResult.setOnClickListener(v -> {
            String userAnswer = etAnswer.getText().toString().trim();
            userAnswers.clear(); // Xóa các câu trả lời trước đó
            userAnswers.add(userAnswer); // Thêm câu trả lời mới vào danh sách
            Log.d("ListeningActivity", "User Answers: " + userAnswers);
            if (userAnswers.isEmpty()) {
                Toast.makeText(ListeningActivity.this, "Vui lòng trả lời câu hỏi!", Toast.LENGTH_SHORT).show();
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
                quesManager.saveUserAnswer(questionIds.get(currentStep), userAnswer, result.getPoint(), result.getimprovements(), new ApiCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("ListeningActivity.this", "Lưu thành công!");
                        runOnUiThread(() -> {
                            PopupHelper.showResultPopup(ListeningActivity.this, questype, null, null, result.getPoint(), result.getimprovements(), result.getevaluation(), () -> {
                                currentStep++;
                                if (currentStep < totalSteps) {
                                    fetchQuestion(questionIds.get(currentStep));
                                } else {
                                    Intent intent = new Intent(ListeningActivity.this, WritingActivity.class);
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
                        Log.e("ListeningActivity.this", "Lỗi lưu câu trả lời: " + errorMessage);
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("ListeningActivity", "Lỗi API: " + errorMessage);
            }
        });
    }

    private void fetchAudioUrl(int questionId) {

        // Gọi phương thức fetchAudioUrl từ ApiManager
        mediaManager.fetchMediaByQuesId(questionId, new ApiCallback<MediaFile>() {

            @Override
            public void onSuccess() {

            }

            @Override
            public void onSuccess(MediaFile mediaFile) {
                runOnUiThread(() -> { // Sử dụng runOnUiThread ở đây
                    if (mediaFile!= null) {
                        btnListen.setOnClickListener(v -> {
                            String modifiedLink = mediaFile.getMaterLink().replace("0.0.0.0", "14.225.198.3");
                            playAudio(modifiedLink);
                        });

                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                // Hiển thị thông báo lỗi nếu có
                Log.e("media",errorMessage);
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
                mediaPlayer.release(); // Giải phóng MediaPlayer cũ nếu có
            }

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(audioUrl);

            mediaPlayer.setOnPreparedListener(mp -> mediaPlayer.start()); // Chạy khi đã sẵn sàng
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e("MediaPlayerError", "Error occurred: what=" + what + ", extra=" + extra);
                return true;
            });

            mediaPlayer.prepareAsync(); // Dùng prepareAsync() để tránh chặn UI
        } catch (IOException e) {
            Log.e("MediaPlayerError", "IOException: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            Log.e("MediaPlayerError", "IllegalArgumentException: " + e.getMessage());
        }
    }


    private void fetchLessonAndQuestions(int lessonId) {
        lesManager.fetchLessonById(lessonId, new ApiCallback<Lesson>() {
            @Override
            public void onSuccess(Lesson lesson) {
                if (lesson != null) {
                    questionIds = lesson.getQuestionIds();
                    Log.d("ListeningActivity", "Danh sách questionIds: " + questionIds);
                    totalSteps = questionIds.size();
                    if (questionIds != null && !questionIds.isEmpty()) {
                        fetchQuestion(questionIds.get(currentStep));
                        fetchAudioUrl(questionIds.get(currentStep));
                    } else {
                        Log.e("ListeningActivity", "Bài học không có câu hỏi.");
                    }
                } else {
                    Log.e("ListeningActivity", "Bài học trả về là null.");
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("ListeningActivity", errorMessage);
            }
            @Override
            public void onSuccess() {}
        });
    }

    private void fetchQuestion(int questionId) {
        quesManager.fetchQuestionContentFromApi(questionId, new ApiCallback<Question>() {
            @Override
            public void onSuccess(Question question) {
                if (question != null) {
                    questype = question.getQuesType();
                    // Cập nhật giao diện người dùng với nội dung câu hỏi
                    runOnUiThread(() -> {
                        // Giả sử bạn có một TextView để hiển thị câu hỏi
                        tvQuestion.setText(question.getQuesContent());
                    });
                } else {
                    Log.e("ListeningActivity", "Câu hỏi trả về là null.");
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("ListeningActivity", errorMessage);
            }

            @Override
            public void onSuccess() {}
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
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
}
