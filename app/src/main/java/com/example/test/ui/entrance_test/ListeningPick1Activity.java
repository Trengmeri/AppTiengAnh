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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.PopupHelper;
import com.example.test.R;
import com.example.test.adapter.ChoiceAdapter;
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
import java.util.stream.Collectors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ListeningPick1Activity extends AppCompatActivity {
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
    LinearLayout progressBar;
    Button btnCheckResult;

    QuestionManager quesManager = new QuestionManager(this);
    LessonManager lesManager = new LessonManager();
    ResultManager resultManager = new ResultManager(this);
    MediaManager mediaManager = new MediaManager(this);
    private RecyclerView recyclerViewChoices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_listening_choice);

        btnListen = findViewById(R.id.btnListen);
        btnCheckResult = findViewById(R.id.btnCheckResult);
        tvQuestion = findViewById(R.id.tvQuestion);
        recyclerViewChoices = findViewById(R.id.recyclerViewChoices);
        int columnCount = 2; // Số cột
        GridLayoutManager layoutManager = new GridLayoutManager(this, columnCount);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1; // Mỗi button chiếm 1 cột
            }
        });
        recyclerViewChoices.setLayoutManager(layoutManager);
        recyclerViewChoices.setHasFixedSize(true);
        int lessonId = 3;
        fetchLessonAndQuestions(lessonId);

        progressBar = findViewById(R.id.progressBar); // Ánh xạ ProgressBar



        btnCheckResult.setOnClickListener(v -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();  // Dừng âm thanh nếu đang phát
                mediaPlayer.release();
                mediaPlayer = null;
            }
            Log.d("ListeningPick1Activity", "User Answers: " + userAnswers);
            if (userAnswers.isEmpty()) {
                Toast.makeText(ListeningPick1Activity.this, "Vui lòng trả lời câu hỏi!", Toast.LENGTH_SHORT).show();
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
                quesManager.saveUserAnswer(questionIds.get(currentStep), answerContent, 0,null, new ApiCallback() {

                    @Override
                    public void onSuccess() {
                        Log.e("ListeningPick1Activity", "Câu trả lời đã được lưu: " + answerContent);
                        // Hiển thị popup
                        runOnUiThread(() -> {
                            PopupHelper.showResultPopup(ListeningPick1Activity.this, questype, userAnswers, correctAnswers, null, null, null, () -> {
                                currentStep++; // Tăng currentStep

                                // Kiểm tra nếu hoàn thành
                                if (currentStep < totalSteps) {
                                    fetchQuestion(questionIds.get(currentStep)); // Lấy câu hỏi tiếp theo
                                    updateProgressBar(progressBar, currentStep); // Cập nhật thanh tiến trình// Cập nhật thanh tiến trình
                                } else {
                                    Intent intent = new Intent(ListeningPick1Activity.this, SpeakingActivity.class);
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
                                    Log.e("ListeningPick1Activity", "Answer ID từ API: " + answer.getId());
                                    if (answerIds != 0) {
                                        QuestionManager.gradeAnswer(answerIds, new Callback() {
                                            @Override
                                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                                Log.e("ListeningPick1Activity", "Lỗi khi chấm điểm: " + e.getMessage());
                                            }

                                            @Override
                                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                                if (response.isSuccessful()) {
                                                    Log.e("ListeningPick1Activity", "Chấm điểm thành công cho Answer ID: " + answerIds);
                                                } else {
                                                    Log.e("ListeningPick1Activity", "Lỗi từ server: " + response.code());
                                                }
                                            }
                                        });
                                    } else {
                                        Log.e("ListeningPick1Activity", "Bài học không có câu trl.");
                                    }
                                } else {
                                    Log.e("ListeningPick1Activity", "Không nhận được câu trả lời từ API.");
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
                    totalSteps = questionIds.size(); // Cập nhật tổng số bước
                    if (questionIds != null && !questionIds.isEmpty()) {
                        fetchQuestion(questionIds.get(currentStep)); // Lấy câu hỏi đầu tiên
                        fetchAudioUrl(questionIds.get(currentStep));
                    } else {
                        Log.e("ListeningPick1Activity", "Bài học không có câu hỏi.");
                    }
                } else {
                    Log.e("ListeningPick1Activity", "Bài học trả về là null.");
                }
            }



            @Override
            public void onFailure(String errorMessage) {
                Log.e("ListeningPick1Activity", errorMessage);
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
                    // Lấy nội dung câu hỏi
                    String questionContent = question.getQuesContent();
                    Log.d("ListeningPick1Activity", "Câu hỏi: " + questionContent);

                    List<QuestionChoice> choices = question.getQuestionChoices();
                    if (choices != null && !choices.isEmpty()) {
                        runOnUiThread(() -> {
                            tvQuestion.setText(questionContent);
                            userAnswers.clear();
                            ChoiceAdapter choiceAdapter = new ChoiceAdapter(ListeningPick1Activity.this, choices, userAnswers);
                            recyclerViewChoices.setAdapter(choiceAdapter);
                            correctAnswers.clear();
                            for (QuestionChoice choice : choices) {
                                if (choice.isChoiceKey()) {
                                    correctAnswers.add(choice.getChoiceContent());
                                }
                            }
                        });
                    } else {
                        Log.e("ListeningPick1Activity", "Câu hỏi không có lựa chọn.");
                    }
                } else {
                    Log.e("ListeningPick1Activity", "Câu hỏi trả về là null.");
                }
            }



            @Override
            public void onFailure(String errorMessage) {
                Log.e("ListeningPick1Activity", errorMessage);
            }



            @Override
            public void onSuccess() {}
        });
    }

    private void updateProgressBar(LinearLayout progressBarSteps, int step) {
        if (step < progressBarSteps.getChildCount()) {
            final View currentStepView = progressBarSteps.getChildAt(step);

            // Animation thay đổi màu
            ObjectAnimator colorAnimator = ObjectAnimator.ofArgb(
                    currentStepView,
                    "backgroundColor",
                    Color.parseColor("#E0E0E0"), // Màu ban đầu
                    Color.parseColor("#C4865E") // Màu đã hoàn thành
            );
            colorAnimator.setDuration(300); // Thời gian chuyển đổi màu
            colorAnimator.start();
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
