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

import com.example.test.PopupHelper;
import com.example.test.R;
import com.example.test.api.ApiCallback;
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
    List<String> correctAnswers = new ArrayList<>();
    private List<String> userAnswers = new ArrayList<>();
    private AppCompatButton selectedAnswer = null;
    private AppCompatButton btnAnswer1, btnAnswer2, btnAnswer3, btnAnswer4;
    private MediaPlayer mediaPlayer;
    private List<Question> questions; // Danh sách câu hỏi
    private int currentQuestionIndex; // Vị trí câu hỏi hiện tại
    private int currentStep = 0; // Bước hiện tại (bắt đầu từ 0)
    private int totalSteps; // Tổng số bước trong thanh tiến trình
    private int lessonID,courseID;
    private int answerIds;
    private  String questype;
    ImageView btnListen;
    TextView tvQuestion;
    Button btnCheckResult;

    QuestionManager quesManager = new QuestionManager(this);
    LessonManager lesManager = new LessonManager();
    ResultManager resultManager = new ResultManager(this);
    MediaManager mediaManager = new MediaManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_listening_choice);

        btnListen = findViewById(R.id.btnListen);
        tvQuestion = findViewById(R.id.tvQuestion);
        btnCheckResult = findViewById(R.id.btnCheckResult);
        btnAnswer1 = findViewById(R.id.btnOption1);
        btnAnswer2 = findViewById(R.id.btnOption2);
        btnAnswer3 = findViewById(R.id.btnOption3);
        btnAnswer4 = findViewById(R.id.btnOption4);
        LinearLayout progressBar = findViewById(R.id.progressBar); // Ánh xạ ProgressBar
        setupAnswerClickListeners();
        // Nhận dữ liệu từ Intent
        currentQuestionIndex = getIntent().getIntExtra("currentQuestionIndex", 0);
        questions = (List<Question>) getIntent().getSerializableExtra("questions");
        courseID = getIntent().getIntExtra("courseID",1);
        lessonID = getIntent().getIntExtra("lessonID",1);


        // Hiển thị câu hỏi hiện tại
        loadQuestion(currentQuestionIndex);
        fetchAudioUrl(questions.get(currentQuestionIndex).getId());

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
                quesManager.saveUserAnswer(questions.get(currentQuestionIndex).getId(), answerContent, 0, null, new ApiCallback() {

                    @Override
                    public void onSuccess() {
                        Log.e("ListeningChoiceActivity", "Câu trả lời đã được lưu: " + answerContent);
                        // Hiển thị popup
                        runOnUiThread(() -> {
                            PopupHelper.showResultPopup(ListeningChoiceActivity.this, questype, userAnswers, correctAnswers, null, null, null, () -> {
                                // Callback khi nhấn Next Question trên popup
                                resetAnswerColors();
                                currentQuestionIndex++;
                                if (currentQuestionIndex < questions.size()) {
                                    Question nextQuestion = questions.get(currentQuestionIndex);
                                    updateProgressBar(progressBar, currentQuestionIndex);
                                    if (nextQuestion.getQuesType().equals("TEXT")) {
                                        Intent intent = new Intent(ListeningChoiceActivity.this, ListeningQuestionActivity.class);
                                        intent.putExtra("currentQuestionIndex", currentQuestionIndex);
                                        Log.e("pick1", "currentQuestionIndex");
                                        intent.putExtra("questions", (Serializable) questions);
                                        intent.putExtra("courseID", courseID);
                                        intent.putExtra("lessonID", lessonID);
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
                            // Lấy danh sách lựa chọn
                            List<QuestionChoice> choices = question.getQuestionChoices();
                            if (choices != null && !choices.isEmpty()) {
                                for (QuestionChoice choice : choices) {
                                    Log.d("ListeningChoiceActivity", "Lựa chọn: " + choice.getChoiceContent() +
                                            " (Đáp án đúng: " + choice.isChoiceKey() + ")");
                                }

                                // Cập nhật giao diện người dùng
                                runOnUiThread(() -> {

                                    tvQuestion.setText(question.getQuesContent());
                                    btnAnswer1.setText(choices.get(0).getChoiceContent());
                                    btnAnswer2.setText(choices.get(1).getChoiceContent());
                                    btnAnswer3.setText(choices.get(2).getChoiceContent());
                                    btnAnswer4.setText(choices.get(3).getChoiceContent());

                                    correctAnswers = choices.stream()
                                            .filter(QuestionChoice::isChoiceKey) // Lọc ra các đáp án đúng
                                            .map(QuestionChoice::getChoiceContent) // Chuyển đổi thành nội dung đáp án
                                            .collect(Collectors.toList());
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
    private void setupAnswerClickListeners() {
        View.OnClickListener answerClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatButton clickedButton = (AppCompatButton) view;

                // Kiểm tra nếu đáp án đã được chọn trước đó
                if (selectedAnswer != null && selectedAnswer == clickedButton) {
                    // Nếu đáp án đang được chọn lại, gỡ bỏ chọn
                    clickedButton.setBackgroundResource(R.drawable.bg_answer); // Màu nền mặc định
                    selectedAnswer = null;

                    // Xóa đáp án khỏi danh sách
                    userAnswers.remove(clickedButton.getText().toString());
                } else {
                    // Nếu có đáp án được chọn trước đó, gỡ màu của đáp án cũ
                    if (selectedAnswer != null) {
                        selectedAnswer.setBackgroundResource(R.drawable.bg_answer);
                    }

                    // Đặt màu cho đáp án mới được chọn
                    clickedButton.setBackgroundResource(R.drawable.bg_answer_pressed);

                    // Cập nhật đáp án được chọn
                    selectedAnswer = clickedButton;

                    // Thêm đáp án mới vào danh sách
                    String answerText = clickedButton.getText().toString();
                    if (!userAnswers.contains(answerText)) {
                        userAnswers.add(answerText);
                    }
                }
            }
        };

        // Đăng ký lắng nghe sự kiện cho tất cả các nút đáp án
        btnAnswer1.setOnClickListener(answerClickListener);
        btnAnswer2.setOnClickListener(answerClickListener);
        btnAnswer3.setOnClickListener(answerClickListener);
        btnAnswer4.setOnClickListener(answerClickListener);
    }

    private void resetAnswerColors() {
        // Đặt lại màu nền cho tất cả các đáp án về màu mặc định
        userAnswers.clear();
        btnAnswer1.setBackgroundResource(R.drawable.bg_answer);
        btnAnswer2.setBackgroundResource(R.drawable.bg_answer);
        btnAnswer3.setBackgroundResource(R.drawable.bg_answer);
        btnAnswer4.setBackgroundResource(R.drawable.bg_answer);
    }
}