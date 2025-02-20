package com.example.test.ui;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.PopupHelper;
import com.example.test.R;
import com.example.test.adapter.ChoiceAdapter;
import com.example.test.api.ApiCallback;
import com.example.test.api.LessonManager;
import com.example.test.api.QuestionManager;
import com.example.test.api.ResultManager;
import com.example.test.model.Answer;
import com.example.test.model.Course;
import com.example.test.model.Discussion;
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

public class GrammarPickManyActivity extends AppCompatActivity {
    private List<String> correctAnswers = new ArrayList<>();
    private List<String> userAnswers = new ArrayList<>();
    private int currentStep = 0;
    private int totalSteps;
    private List<Integer> questionIds;
    private int answerIds;// Danh sách questionIds
    private TextView tvContent;
    private RecyclerView recyclerViewChoices;
    private LinearLayout progressBar;
    QuestionManager quesManager = new QuestionManager(this);
    LessonManager lesManager = new LessonManager();
    ResultManager resultManager = new ResultManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grammar_pick_many);

        recyclerViewChoices = findViewById(R.id.recyclerViewChoices);
        recyclerViewChoices.setLayoutManager(new LinearLayoutManager(this));
        tvContent = findViewById(R.id.tvContent);
        Button btnCheckAnswers = findViewById(R.id.btnCheckAnswers);
        progressBar = findViewById(R.id.progressBar);

        int lessonId = 4;
        fetchLessonAndQuestions(lessonId);

        btnCheckAnswers.setOnClickListener(v -> {
            Log.d("GrammarPickManyActivity", "User Answers: " + userAnswers);
            if (userAnswers.isEmpty()) {
                Toast.makeText(GrammarPickManyActivity.this, "Vui lòng trả lời câu hỏi!", Toast.LENGTH_SHORT).show();
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
                        Log.e("GrammarPickManyActivity", "Câu trả lời đã được lưu: " + answerContent);
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
                                    Intent intent = new Intent(GrammarPickManyActivity.this, ListeningQuestionActivity.class);
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
                                    Log.e("GrammarPickManyActivity", "Answer ID từ API: " + answer.getId());
                                    if (answerIds != 0) {
                                        QuestionManager.gradeAnswer(answerIds, new Callback() {
                                            @Override
                                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                                Log.e("GrammarPickManyActivity", "Lỗi khi chấm điểm: " + e.getMessage());
                                            }

                                            @Override
                                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                                if (response.isSuccessful()) {
                                                    Log.e("GrammarPickManyActivity", "Chấm điểm thành công cho Answer ID: " + answerIds);
                                                } else {
                                                    Log.e("GrammarPickManyActivity", "Lỗi từ server: " + response.code());
                                                }
                                            }
                                        });
                                    } else {
                                        Log.e("GrammarPickManyActivity", "Bài học không có câu trl.");
                                    }
                                } else {
                                    Log.e("GrammarPickManyActivity", "Không nhận được câu trả lời từ API.");
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
                        Log.e("GrammarPickManyActivity", errorMessage);
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
                    questionIds = lesson.getQuestionIds();
                    Log.d("GrammarPickManyActivity", "Danh sách questionIds: " + questionIds);
                    totalSteps = questionIds.size();
                    if (questionIds != null && !questionIds.isEmpty()) {
                        fetchQuestion(questionIds.get(currentStep));
                    } else {
                        Log.e("GrammarPickManyActivity", "Bài học không có câu hỏi.");
                    }
                } else {
                    Log.e("GrammarPickManyActivity", "Bài học trả về là null.");
                }
            }


            @Override
            public void onFailure(String errorMessage) {
                Log.e("GrammarPickManyActivity", errorMessage);
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
                    String questionContent = question.getQuesContent();
                    Log.d("GrammarPickManyActivity", "Câu hỏi: " + questionContent);

                    List<QuestionChoice> choices = question.getQuestionChoices();
                    if (choices != null && !choices.isEmpty()) {
                        runOnUiThread(() -> {
                            tvContent.setText(questionContent);
                            ChoiceAdapter choiceAdapter = new ChoiceAdapter(GrammarPickManyActivity.this, choices, userAnswers);
                            recyclerViewChoices.setAdapter(choiceAdapter);
                            correctAnswers.clear();
                            for (QuestionChoice choice : choices) {
                                if (choice.isChoiceKey()) {
                                    correctAnswers.add(choice.getChoiceContent());
                                }
                            }
                        });
                    } else {
                        Log.e("GrammarPickManyActivity", "Câu hỏi không có lựa chọn.");
                    }
                } else {
                    Log.e("GrammarPickManyActivity", "Câu hỏi trả về là null.");
                }
            }
            @Override
            public void onFailure(String errorMessage) {
                Log.e("GrammarPickManyActivity", errorMessage);
            }

            @Override
            public void onSuccess() {}
        });
    }

    private void updateProgressBar(LinearLayout progressBarSteps, int step) {
        if (step < progressBarSteps.getChildCount()) {
            final View currentStepView = progressBarSteps.getChildAt(step);

            ObjectAnimator colorAnimator = ObjectAnimator.ofArgb(
                    currentStepView,
                    "backgroundColor",
                    Color.parseColor("#E0E0E0"),
                    Color.parseColor("#C4865E")
            );
            colorAnimator.setDuration(200);
            colorAnimator.start();
        }
    }
}