package com.example.test.ui.question_data;

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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.PopupHelper;
import com.example.test.R;
import com.example.test.adapter.ChoiceAdapter;
import com.example.test.api.ApiCallback;
import com.example.test.api.LearningMaterialsManager;
import com.example.test.api.LessonManager;
import com.example.test.api.MediaManager;
import com.example.test.api.QuestionManager;
import com.example.test.api.ResultManager;
import com.example.test.model.Answer;
import com.example.test.model.MediaFile;
import com.example.test.model.Question;
import com.example.test.model.QuestionChoice;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ListeningChoiceActivity extends AppCompatActivity {
    String correctAnswers;
    private List<String> userAnswers = new ArrayList<>();
    private MediaPlayer mediaPlayer;
    private List<Question> questions; // Danh sách câu hỏi
    private int currentQuestionIndex; // Vị trí câu hỏi hiện tại
    private int currentStep = 0; // Bước hiện tại (bắt đầu từ 0)
    private int totalSteps; // Tổng số bước trong thanh tiến trình
    private int lessonID,courseID,enrollmentId;
    private int answerIds;
    private  String questype;
    ImageView btnListen, imgLessonMaterial;
    TextView tvQuestion;
    Button btnCheckResult;

    QuestionManager quesManager = new QuestionManager(this);
    LessonManager lesManager = new LessonManager();
    ResultManager resultManager = new ResultManager(this);
    MediaManager mediaManager = new MediaManager(this);
    LearningMaterialsManager materialsManager = new LearningMaterialsManager(this);
    private RecyclerView recyclerViewChoices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_listening_choice);

        btnListen = findViewById(R.id.btnListen);
        tvQuestion = findViewById(R.id.tvQuestion);
        btnCheckResult = findViewById(R.id.btnCheckResult);
        recyclerViewChoices = findViewById(R.id.recyclerViewChoices);
        imgLessonMaterial = findViewById(R.id.imgLessonMaterial);
        createProgressBars(totalSteps, currentQuestionIndex);
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
        LinearLayout progressBar = findViewById(R.id.progressBar);
        // Nhận dữ liệu từ Intent
        currentQuestionIndex = getIntent().getIntExtra("currentQuestionIndex", 0);
        questions = (List<Question>) getIntent().getSerializableExtra("questions");
        courseID = getIntent().getIntExtra("courseID",1);
        lessonID = getIntent().getIntExtra("lessonID",1);
        totalSteps= questions.size();
        createProgressBars(totalSteps, currentQuestionIndex);
        enrollmentId = getIntent().getIntExtra("enrollmentId", 1);


        // Hiển thị câu hỏi hiện tại
        loadQuestion(currentQuestionIndex);
        materialsManager.fetchAndLoadImageByLesId(lessonID, imgLessonMaterial);
        materialsManager.fetchAudioByLesId(lessonID,  new ApiCallback<String>() {

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

        btnCheckResult.setOnClickListener(v -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();  // Dừng âm thanh nếu đang phát
                mediaPlayer.release();
                mediaPlayer = null;
            }
            Log.d("ListeningChoiceActivity", "User Answers: " + userAnswers);
            if (userAnswers.isEmpty()) {
                Toast.makeText(ListeningChoiceActivity.this, "Vui lòng trả lời câu hỏi!", Toast.LENGTH_SHORT).show();
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
                quesManager.saveUserAnswer(questions.get(currentQuestionIndex).getId(), answerContent, 0, null,enrollmentId, new ApiCallback() {

                    @Override
                    public void onSuccess() {
                        Log.e("ListeningChoiceActivity", "Câu trả lời đã được lưu: " + answerContent);
                        // Hiển thị popup
                        runOnUiThread(() -> {
                            PopupHelper.showResultPopup(ListeningChoiceActivity.this, questype, answerContent, correctAnswers, null, null, null, () -> {
                                // Callback khi nhấn Next Question trên popup
                                currentQuestionIndex++;
                                if (currentQuestionIndex < questions.size()) {
                                    Question nextQuestion = questions.get(currentQuestionIndex);
                                    createProgressBars(totalSteps, currentQuestionIndex);
                                    if (nextQuestion.getQuesType().equals("TEXT")) {
                                        Intent intent = new Intent(ListeningChoiceActivity.this, ListeningQuestionActivity.class);
                                        intent.putExtra("currentQuestionIndex", currentQuestionIndex);
                                        Log.e("pick1", "currentQuestionIndex");
                                        intent.putExtra("questions", (Serializable) questions);
                                        intent.putExtra("courseID", courseID);
                                        intent.putExtra("lessonID", lessonID);
                                        intent.putExtra("enrollmentId", enrollmentId);
                                        startActivity(intent);
                                        finish(); // Đóng Activity hiện tại
                                    } else {
                                        loadQuestion(currentQuestionIndex);
                                    }
                                } else {
                                    finishLesson();
                                }
                            });
                        });
                        resultManager.fetchAnswerPointsByQuesId(questions.get(currentQuestionIndex).getId(), new ApiCallback<Answer>() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onSuccess(Answer answer) {
                                if (answer != null) {
                                    answerIds = answer.getId();
                                    Log.e("ListeningChoiceActivity", "Answer ID từ API: " + answer.getId());
                                    if (answerIds != 0) {
                                        QuestionManager.gradeAnswer(answerIds, new Callback() {
                                            @Override
                                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                                Log.e("ListeningChoiceActivity", "Lỗi khi chấm điểm: " + e.getMessage());
                                            }

                                            @Override
                                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                                if (response.isSuccessful()) {
                                                    Log.e("ListeningChoiceActivity", "Chấm điểm thành công cho Answer ID: " + answerIds + "Diem: " + answer.getPointAchieved());
                                                } else {
                                                    Log.e("ListeningChoiceActivity", "Lỗi từ server: " + response.code());
                                                }
                                            }
                                        });
                                    } else {
                                        Log.e("ListeningChoiceActivity", "Bài học không có câu trl.");
                                    }
                                } else {
                                    Log.e("ListeningChoiceActivity", "Không nhận được câu trả lời từ API.");
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
                        Log.e("ListeningChoiceActivity", errorMessage);
                    }
                });
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

    private void loadQuestion(int index) {
        if (index < questions.size()) {
            Question question = questions.get(index);
            quesManager.fetchQuestionContentFromApi(question.getId(), new ApiCallback<Question>() {
                @Override
                public void onSuccess(Question question) {
                    if (question != null) {
                        questype = question.getQuesType();
                        // Lấy nội dung câu hỏi
                        String questionContent = question.getQuesContent();

                        Log.d("ListeningChoiceActivity", "Câu hỏi: " + questionContent);

                        List<QuestionChoice> choices = question.getQuestionChoices();
                        if (choices != null && !choices.isEmpty()) {
                            runOnUiThread(() -> {
                                tvQuestion.setText(questionContent);
                                userAnswers.clear();
                                ChoiceAdapter choiceAdapter = new ChoiceAdapter(ListeningChoiceActivity.this, choices, userAnswers);
                                recyclerViewChoices.setAdapter(choiceAdapter);
                                for (QuestionChoice choice : choices) {
                                    if (choice.isChoiceKey()) {
                                        correctAnswers=(choice.getChoiceContent());
                                    }
                                }
                            });
                        } else {
                                Log.e("ListeningChoiceActivity", "Câu hỏi không có lựa chọn.");
                            }
                    } else {
                        Log.e("ListeningChoiceActivity", "Câu hỏi trả về là null.");
                    }
                }

                @Override
                public void onFailure(String errorMessage) {
                    Log.e("ListeningChoiceActivity", errorMessage);
                }

                @Override
                public void onSuccess() {}
            });
        } else {
            finishLesson();
        }
    }

    private void finishLesson() {
        Intent intent = new Intent(ListeningChoiceActivity.this, PointResultLessonActivity.class);
        intent.putExtra("lessonId",lessonID);
        intent.putExtra("courseId",courseID);
        intent.putExtra("enrollmentId", enrollmentId);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
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
}