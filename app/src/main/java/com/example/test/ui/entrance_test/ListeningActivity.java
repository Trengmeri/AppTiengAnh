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
import androidx.appcompat.widget.AppCompatButton;

import com.example.test.PopupHelper;
import com.example.test.R;
import com.example.test.api.ApiCallback;
import com.example.test.api.ApiService;
import com.example.test.api.LearningMaterialsManager;
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
import java.util.stream.Collectors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ListeningActivity extends AppCompatActivity {
    String correctAnswer;
    private MediaPlayer mediaPlayer;
    private EditText etAnswer;
    private List<Integer> questionIds;
    private  String questype;
    private String userAnswer;
    private int currentStep = 0; // Bước hiện tại (bắt đầu từ 0)
    private int totalSteps; // Tổng số bước trong thanh tiến trình
    private int answerIds;
    ImageView btnListen;
    TextView tvQuestion;
    LinearLayout progressBar;
    Button btnCheckResult;
    ImageView imgLessonMaterial;

    QuestionManager quesManager = new QuestionManager(this);
    LessonManager lesManager = new LessonManager();
    ResultManager resultManager = new ResultManager(this);
    MediaManager mediaManager = new MediaManager(this);
    LearningMaterialsManager materialsManager = new LearningMaterialsManager(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_listening_question);

        btnListen = findViewById(R.id.btnListen);
        btnCheckResult = findViewById(R.id.btnCheckResult);
        tvQuestion = findViewById(R.id.tvQuestion);
        etAnswer = findViewById(R.id.etAnswer);
        imgLessonMaterial = findViewById(R.id.imgLessonMaterial);
        int lessonId = 4;
        int enrollmentId = getIntent().getIntExtra("enrollmentId", 1);
        fetchLessonAndQuestions(lessonId);

        progressBar = findViewById(R.id.progressBar); // Ánh xạ ProgressBar
        createProgressBars(totalSteps, currentStep); // Tạo progress bar dựa trên số câu hỏi thực tế


        btnCheckResult.setOnClickListener(v -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();  // Dừng âm thanh nếu đang phát
                mediaPlayer.release();
                mediaPlayer = null;
            }
            String userAnswer = etAnswer.getText().toString().trim();
            // Xóa nội dung EditText ngay khi bấm "Check Answers"
            etAnswer.setText("");
            Log.d("ListeningActivity", "User Answers: " + userAnswer);
            if (userAnswer.isEmpty()) {
                Toast.makeText(ListeningActivity.this, "Vui lòng trả lời câu hỏi!", Toast.LENGTH_SHORT).show();
            } else {
                String answerContent = userAnswer;
                // Lưu câu trả lời của người dùng
                quesManager.saveUserAnswer(questionIds.get(currentStep), answerContent, 0,null,enrollmentId, new ApiCallback() {

                    @Override
                    public void onSuccess() {
                        Log.e("ListeningActivity", "Câu trả lời đã được lưu: " + answerContent);
                        // Hiển thị popup
                        runOnUiThread(() -> {
                            PopupHelper.showResultPopup(ListeningActivity.this, questype, userAnswer, correctAnswer, null, null, null, () -> {
                                currentStep++; // Tăng currentStep

                                // Kiểm tra nếu hoàn thành
                                if (currentStep < totalSteps) {
                                    fetchQuestion(questionIds.get(currentStep)); // Lấy câu hỏi tiếp theo
                                    createProgressBars(totalSteps, currentStep); // Tạo progress bar dựa trên số câu hỏi thực tế
                                } else {
                                    Intent intent = new Intent(ListeningActivity.this, ListeningPick1Activity.class);
                                    intent.putExtra("enrollmentId", enrollmentId);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        });
                        resultManager.fetchAnswerPointsByQuesId(questionIds.get(currentStep), new ApiCallback<Answer>() {
                            @Override
                            public void onSuccess() {
                            }


                            @Override
                            public void onSuccess(Answer answer) {
                                if (answer != null) {
                                    answerIds = answer.getId();
                                    Log.e("ListeningActivity", "Answer ID từ API: " + answer.getId());
                                    if (answerIds != 0) {
                                        QuestionManager.gradeAnswer(answerIds, new Callback() {
                                            @Override
                                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                                Log.e("ListeningActivity", "Lỗi khi chấm điểm: " + e.getMessage());
                                            }

                                            @Override
                                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                                if (response.isSuccessful()) {
                                                    Log.e("ListeningActivity", "Chấm điểm thành công cho Answer ID: " + answerIds);
                                                } else {
                                                    Log.e("ListeningActivity", "Lỗi từ server: " + response.code());
                                                }
                                            }
                                        });
                                    } else {
                                        Log.e("ListeningActivity", "Bài học không có câu trl.");
                                    }
                                } else {
                                    Log.e("ListeningActivity", "Không nhận được câu trả lời từ API.");
                                }
                            }

                            @Override
                            public void onFailure(String errorMessage) {

                            }
                        });
                    }

                    @Override
                    public void onSuccess(Object result) {

                    }

                    @Override
                    public void onFailure(String errorMessage) {

                    }
                });
            }
        });
    }

    private void fetchLessonAndQuestions(int lessonId) {
        lesManager.fetchLessonById(lessonId, new ApiCallback<Lesson>() {
            @Override
            public void onSuccess(Lesson lesson) {
                if (lesson != null) {
                    // Lấy danh sách questionIds từ lesson
                    questionIds = lesson.getQuestionIds(); // Lưu trữ danh sách questionIds
                    runOnUiThread(() -> {
                        totalSteps = questionIds.size(); // Cập nhật tổng số câu hỏi thực tế từ API
                        createProgressBars(totalSteps, currentStep); // Tạo progress bar dựa trên số câu hỏi thực tế
                    });
                    if (questionIds != null && !questionIds.isEmpty()) {
                        fetchQuestion(questionIds.get(currentStep));
                        materialsManager.fetchAudioByLesId(lessonId, new ApiCallback<String>() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onSuccess(String result) {
                                runOnUiThread(() -> { // Sử dụng runOnUiThread ở đây
                                    if (result!= null) {
                                        btnListen.setOnClickListener(v -> {
                                            playAudio(result);
                                        });

                                    }
                                });
                            }

                            @Override
                            public void onFailure(String errorMessage) {

                            }
                        });
                        materialsManager.fetchAndLoadImageByLesId(lessonId, imgLessonMaterial);
//                        fetchAudioUrl(questionIds.get(currentStep));
                    } else {
                        Log.e("Pick1Activity", "Bài học không có câu hỏi.");
                    }
                } else {
                    Log.e("Pick1Activity", "Bài học trả về là null.");
                }
            }



            @Override
            public void onFailure(String errorMessage) {
                Log.e("Pick1Activity", errorMessage);
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
                    runOnUiThread(() -> {
                        tvQuestion.setText(question.getQuesContent());
                        materialsManager.fetchAndLoadImage(questionId, imgLessonMaterial);
                        List<QuestionChoice> choices = question.getQuestionChoices();
                        for (QuestionChoice choice : choices) {
                            if (choice.isChoiceKey()) {
                                correctAnswer = choice.getChoiceContent();
                            }
                        }
                    });
                } else {
                    Log.e("ListeningQuestionActivity", "Câu hỏi trả về là null.");
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("GrammarPick1QuestionActivity", errorMessage);
            }

            @Override
            public void onSuccess() {}
        });
    }

    private void createProgressBars(int totalQuestions, int currentProgress) {
        LinearLayout progressContainer = findViewById(R.id.progressContainer);
        progressContainer.removeAllViews(); // Xóa thanh cũ nếu có

        for (int i = 0; i < totalQuestions; i++) {
            View bar = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(32, 8); // Kích thước mỗi thanh
            params.setMargins(4, 4, 4, 4); // Khoảng cách giữa các thanh
            bar.setLayoutParams(params);

            if (i < currentProgress) {
                bar.setBackgroundColor(Color.parseColor("#C4865E")); // Màu đã hoàn thành
            } else {
                bar.setBackgroundColor(Color.parseColor("#E0E0E0")); // Màu chưa hoàn thành
            }
            progressContainer.addView(bar);
        }
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
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();  // Dừng âm thanh nếu đang phát
                mediaPlayer.release();
                mediaPlayer = null;
            }

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(audioUrl);

            mediaPlayer.setOnPreparedListener(mp -> {
                mediaPlayer.start();
                btnCheckResult.setEnabled(true);  // Kích hoạt lại nút CheckResult
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e("MediaPlayerError", "Error occurred: what=" + what + ", extra=" + extra);
                return true;
            });

            mediaPlayer.prepareAsync();
        } catch (IOException | IllegalArgumentException e) {
            Log.e("MediaPlayerError", e.getMessage());
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
