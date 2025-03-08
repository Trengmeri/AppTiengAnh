package com.example.test.ui.question_data;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.test.PopupHelper;
import com.example.test.R;
import com.example.test.api.ApiCallback;
import com.example.test.api.ApiService;
import com.example.test.api.LessonManager;
import com.example.test.api.QuestionManager;
import com.example.test.model.EvaluationResult;
import com.example.test.model.Lesson;
import com.example.test.model.Question;

import java.util.List;

public class WrittingActivity extends AppCompatActivity {

    private TextView tvContent;
    private EditText etAnswer;
    private Button btnCheckAnswers;
    private QuestionManager quesManager;
    private LessonManager lessonManager = new LessonManager();
    private  String questype;
    int lessonId = 5;
    private List<Integer> questionIds;
    private int currentStep = 0;
    private int totalSteps;
    private ApiService apiService = new ApiService(this);
    private List<Question> questions; // Danh sách câu hỏi
    private int currentQuestionIndex;
    private int lessonID,courseID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writting);

        tvContent = findViewById(R.id.tvContent);
        etAnswer = findViewById(R.id.etAnswer);
        btnCheckAnswers = findViewById(R.id.btnCheckAnswers);
        quesManager = new QuestionManager(this);

        currentQuestionIndex = getIntent().getIntExtra("currentQuestionIndex", 0);
        questions = (List<Question>) getIntent().getSerializableExtra("questions");
        courseID = getIntent().getIntExtra("courseID",1);
        lessonID = getIntent().getIntExtra("lessonID",1);


        // Hiển thị câu hỏi hiện tại
        loadQuestion(currentQuestionIndex);

        btnCheckAnswers.setOnClickListener(view -> {
            String userAnswer = etAnswer.getText().toString().trim();

            if (userAnswer.isEmpty()) {
                Toast.makeText(this, "Please enter an answer!", Toast.LENGTH_SHORT).show();
            } else {
                checkAnswer(userAnswer);
            }
        });
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
                            tvContent.setText(question.getQuesContent());
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
        } else {
            finishLesson();
        }
    }

    private void finishLesson() {
        Intent intent = new Intent(WrittingActivity.this, PointResultLessonActivity.class);
        intent.putExtra("lessonId",lessonID);
        intent.putExtra("courseId",courseID);
        startActivity(intent);
        finish();
    }

    private void checkAnswer(String userAnswer) {
        String questionContent = tvContent.getText().toString().trim();
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
                        Log.d("WrittingActivity.this", "Lưu thành công!");
                        runOnUiThread(() -> {
                            PopupHelper.showResultPopup(WrittingActivity.this, questype, null, null, result.getPoint(), result.getimprovements(), result.getevaluation(), () -> {
                                currentStep++; // Tăng currentStep
                                currentQuestionIndex++;
                                if (currentQuestionIndex < questions.size()) {
                                    updateProgressBar(findViewById(R.id.progressBar), currentStep);
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
                        Log.e("WrittingActivity.this", "Lỗi lưu câu trả lời: " + errorMessage);
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("WrittingActivity", "Lỗi API: " + errorMessage);
            }
        });
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
