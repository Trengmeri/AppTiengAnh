package com.example.test.ui.entrance_test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.test.ui.question_data.PointResultCourseActivity;

import java.util.List;

public class WritingActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writting);

        tvContent = findViewById(R.id.tvContent);
        etAnswer = findViewById(R.id.etAnswer);
        btnCheckAnswers = findViewById(R.id.btnCheckAnswers);
        quesManager = new QuestionManager(this);

        fetchLessonAndQuestions(lessonId);

        btnCheckAnswers.setOnClickListener(view -> {
            String userAnswer = etAnswer.getText().toString().trim();

            if (userAnswer.isEmpty()) {
                Toast.makeText(this, "Please enter an answer!", Toast.LENGTH_SHORT).show();
            } else {
                checkAnswer(userAnswer);
            }
        });
    }

    private void fetchLessonAndQuestions(int lessonId) {
        lessonManager.fetchLessonById(lessonId, new ApiCallback<Lesson>() {
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
            public void onSuccess(Question question) {
                if (question != null) {
                    questype = question.getQuesType();
                    String questionContent = question.getQuesContent();
                    Log.d("WritingActivity", "Câu hỏi: " + questionContent);
                    runOnUiThread(() -> tvContent.setText(questionContent));
                } else {
                    Log.e("WritingActivity", "Không tìm thấy câu hỏi.");
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("WritingActivity", errorMessage);
            }

            @Override
            public void onSuccess() {}
        });
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
                quesManager.saveUserAnswer(questionIds.get(currentStep), userAnswer, result.getPoint(), result.getimprovements(), new ApiCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("WritingActivity.this", "Lưu thành công!");
                        runOnUiThread(() -> {
                            PopupHelper.showResultPopup(WritingActivity.this, questype, null, null, result.getPoint(), result.getimprovements(), result.getevaluation(), () -> {
                                currentStep++;
                                if (currentStep < totalSteps) {
                                    fetchQuestion(questionIds.get(currentStep));
                                } else {
                                    Intent intent = new Intent(WritingActivity.this, PointResultCourseActivity.class);
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
                        Log.e("WritingActivity.this", "Lỗi lưu câu trả lời: " + errorMessage);
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("WritingActivity", "Lỗi API: " + errorMessage);
            }
        });
    }


}
