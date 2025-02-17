package com.example.test.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
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
import com.example.test.api.BaseApiManager;
import com.example.test.api.LessonManager;
import com.example.test.api.QuestionManager;
import com.example.test.api.ResultManager;
import com.example.test.model.Answer;
import com.example.test.model.Course;
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

public class ListeningQuestionActivity extends AppCompatActivity {
    List<String> correctAnswers = new ArrayList<>();
    private MediaPlayer mediaPlayer;
    private EditText etAnswer;
    private List<Integer> questionIds;
    private List<String> userAnswers = new ArrayList<>();
    private int currentStep = 0; // Bước hiện tại (bắt đầu từ 0)
    private int totalSteps; // Tổng số bước trong thanh tiến trình
    private int answerIds;
    ImageView btnListen;

    QuestionManager quesManager = new QuestionManager();
    LessonManager lesManager = new LessonManager();
    ResultManager resultManager = new ResultManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_listening_question);

        btnListen = findViewById(R.id.btnListen);
        Button btnCheckResult = findViewById(R.id.btnCheckResult);
        etAnswer = findViewById(R.id.etAnswer);
        int lessonId = 3;
        fetchLessonAndQuestions(lessonId);

        LinearLayout progressBar = findViewById(R.id.progressBar); // Ánh xạ ProgressBar



        btnCheckResult.setOnClickListener(v -> {
            String userAnswer = etAnswer.getText().toString().trim();
            userAnswers.clear(); // Xóa các câu trả lời trước đó
            userAnswers.add(userAnswer); // Thêm câu trả lời mới vào danh sách
            Log.d("ListeningQuestionActivity", "User Answers: " + userAnswers);
            if (userAnswers.isEmpty()) {
                Toast.makeText(ListeningQuestionActivity.this, "Vui lòng trả lời câu hỏi!", Toast.LENGTH_SHORT).show();
            } else {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < userAnswers.size(); i++) {
                    sb.append(userAnswers.get(i));
                    if (i < userAnswers.size() - 1) {
                        sb.append(", "); // Hoặc ký tự phân cách khác
                    }
                }
                String answerContent = sb.toString();
                // Lưu câu trả lời của người dùng
                quesManager.saveUserAnswer(questionIds.get(currentStep), answerContent, new ApiCallback() {

                    @Override
                    public void onSuccess() {
                        Log.e("ListeningQuestionActivity", "Câu trả lời đã được lưu: " + answerContent);
                        // Hiển thị popup
                        runOnUiThread(() -> {
                            PopupHelper.showResultPopup(findViewById(R.id.popupContainer), userAnswers, correctAnswers, () -> {
                                // Callback khi nhấn Next Question trên popup
                                currentStep++; // Tăng currentStep

                                // Kiểm tra nếu hoàn thành
                                if (currentStep < totalSteps) {
                                    fetchQuestion(questionIds.get(currentStep)); // Lấy câu hỏi tiếp theo
                                    updateProgressBar(progressBar, currentStep); // Cập nhật thanh tiến trình
                                } else {
                                    Intent intent = new Intent(ListeningQuestionActivity.this, RecordQuestionActivity.class);
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
                                    Log.e("ListeningQuestionActivity", "Answer ID từ API: " + answer.getId());
                                    if (answerIds != 0) {
                                        QuestionManager.gradeAnswer(answerIds, new Callback() {
                                            @Override
                                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                                Log.e("ListeningQuestionActivity", "Lỗi khi chấm điểm: " + e.getMessage());
                                            }

                                            @Override
                                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                                if (response.isSuccessful()) {
                                                    Log.e("ListeningQuestionActivity", "Chấm điểm thành công cho Answer ID: " + answerIds);
                                                } else {
                                                    Log.e("ListeningQuestionActivity", "Lỗi từ server: " + response.code());
                                                }
                                            }
                                        });
                                    } else {
                                        Log.e("ListeningQuestionActivity", "Bài học không có câu trl.");
                                    }
                                } else {
                                    Log.e("ListeningQuestionActivity", "Không nhận được câu trả lời từ API.");
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
                    public void onSuccess(Question question) {
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
                    public void onSuccess(Answer answer) {}

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
        });
    }
    private void fetchAudioUrl(int questionId) {

        // Gọi phương thức fetchAudioUrl từ ApiManager
        quesManager.fetchMediaByQuesId(questionId, new ApiCallback() {

            @Override
            public void onSuccess() {

            }

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

            }

            @Override
            public void onSuccess(MediaFile mediaFile) {
                runOnUiThread(() -> { // Sử dụng runOnUiThread ở đây
                    if (mediaFile!= null) {
                        btnListen.setOnClickListener(v -> playAudio(mediaFile.getMaterLink()));
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                // Hiển thị thông báo lỗi nếu có
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onSuccessWithOtpID(String otpID) {

            }

            @Override
            public void onSuccessWithToken(String token) {

            }
        });
    }
    private void playAudio (String audioUrl){
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer = MediaPlayer.create(this, R.raw.uaifein);
            mediaPlayer.start();
            /*mediaPlayer.setDataSource(BaseApiManager.BASE_URL + "/" + audioUrl); // Sử dụng đường dẫn đầy đủ
            mediaPlayer.prepare();
            mediaPlayer.start();*/
        } catch (IllegalArgumentException e) {
            Log.e("MediaPlayerError", "IllegalArgumentException: " + e.getMessage()); // In ra lỗi chi tiết
        } /*catch (SecurityException e) {
            Log.e("MediaPlayerError", "SecurityException: " + e.getMessage()); // In ra lỗi chi tiết
        } catch (IllegalStateException e) {
            Log.e("MediaPlayerError", "IllegalStateException: " + e.getMessage()); // In ra lỗi chi tiết
        } catch (IOException e) {
            Log.e("MediaPlayerError", "IOException: " + e.getMessage()); // In ra lỗi chi tiết
        }*/
    }

    private void fetchLessonAndQuestions(int lessonId) {
        lesManager.fetchLessonById(lessonId, new ApiCallback() {
            @Override
            public void onSuccess(Lesson lesson) {
                if (lesson != null) {
                    questionIds = lesson.getQuestionIds();
                    Log.d("ListeningQuestionActivity", "Danh sách questionIds: " + questionIds);
                    totalSteps = questionIds.size();
                    if (questionIds != null && !questionIds.isEmpty()) {
                        fetchQuestion(questionIds.get(currentStep));
                        fetchAudioUrl(questionIds.get(currentStep));
                    } else {
                        Log.e("ListeningQuestionActivity", "Bài học không có câu hỏi.");
                    }
                } else {
                    Log.e("ListeningQuestionActivity", "Bài học trả về là null.");
                }
            }

            @Override
            public void onSuccess(Course course) {}

            @Override
            public void onSuccess(Result result) {}

            @Override
            public void onSuccess(Answer answer) {

            }

            @Override
            public void onSuccess(MediaFile mediaFile) {

            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("ListeningQuestionActivity", errorMessage);
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
                    Log.e("ListeningQuestionActivity", "Câu hỏi trả về là null.");
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("ListeningQuestionActivity", errorMessage);
            }

            @Override
            public void onSuccess(Lesson lesson) {}

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