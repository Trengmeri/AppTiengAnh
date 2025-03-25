package com.example.test.ui.question_data;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.NetworkChangeReceiver;
import com.example.test.PopupHelper;
import com.example.test.R;
import com.example.test.adapter.ChoiceAdapter;
import com.example.test.adapter.MultipleAdapter;
import com.example.test.api.ApiCallback;
import com.example.test.api.LessonManager;
import com.example.test.api.QuestionManager;
import com.example.test.api.ResultManager;
import com.example.test.model.Answer;
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

public class GrammarPick1QuestionActivity extends AppCompatActivity {
    String correctAnswers ;
    private List<String> userAnswers = new ArrayList<>();
    private int totalSteps; // Tổng số bước trong thanh tiến trình
    private AppCompatButton selectedAnswer = null;
    private Button btnCheckAnswer;
    private RecyclerView recyclerViewChoices;
    private boolean isImageVisible = true; // Trạng thái ban đầu: ảnh hiển thị
    QuestionManager quesManager = new QuestionManager(this);
    LessonManager lesManager = new LessonManager();
    ResultManager resultManager = new ResultManager(this);
    TextView tvContent;
    private int lessonID,courseID, enrollmentId;
    NetworkChangeReceiver networkReceiver;
    private int answerIds;
    private  String questype;
    private List<Question> questions; // Danh sách câu hỏi
    private int currentQuestionIndex; // Vị trí câu hỏi hiện tại

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grammar_question);

        // Ánh xạ các thành phần UI
        btnCheckAnswer = findViewById(R.id.btnCheckAnswer);
        tvContent = findViewById(R.id.tvContent);
        recyclerViewChoices = findViewById(R.id.recyclerViewChoices);
        int columnCount = 2; // Số cột
        GridLayoutManager layoutManager = new GridLayoutManager(this, columnCount);
//        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
//                return 1; // Mỗi button chiếm 1 cột
//            }
//        });
        recyclerViewChoices.setLayoutManager(layoutManager);
        recyclerViewChoices.setHasFixedSize(false);
        LinearLayout progressBar = findViewById(R.id.progressBar);
        updateProgressBar(progressBar, currentQuestionIndex);
        networkReceiver = new NetworkChangeReceiver();
        anHienAnh();

        // Nhận dữ liệu từ Intent
        currentQuestionIndex = getIntent().getIntExtra("currentQuestionIndex", 0);
        questions = (List<Question>) getIntent().getSerializableExtra("questions");
        courseID = getIntent().getIntExtra("courseID",1);
        lessonID = getIntent().getIntExtra("lessonID",1);
        enrollmentId = getIntent().getIntExtra("enrollmentId", 1);
        Log.e("pick1","Lesson ID: "+ lessonID + "courseID: "+ courseID);


        // Hiển thị câu hỏi hiện tại
        loadQuestion(currentQuestionIndex);

//        // Lấy lessonId từ intent hoặc một nguồn khác
//        int lessonId = 1;
//        fetchLessonAndQuestions(lessonId); // Gọi phương thức để lấy bài học và câu hỏi

        btnCheckAnswer.setOnClickListener(v -> {
            if (userAnswers.isEmpty()) {
                Toast.makeText(GrammarPick1QuestionActivity.this, "Vui lòng trả lời câu hỏi!", Toast.LENGTH_SHORT)
                        .show();
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
                quesManager.saveUserAnswer(questions.get(currentQuestionIndex).getId(), answerContent,0,null,enrollmentId, new ApiCallback() {

                    @Override
                    public void onSuccess() {
                        Log.e("GrammarPick1QuestionActivity", "Câu trả lời đã được lưu: " + answerContent);
                        // Hiển thị popup
                        runOnUiThread(() -> {
                            PopupHelper.showResultPopup(GrammarPick1QuestionActivity.this,questype, answerContent, correctAnswers, null, null, null, () -> {
                                // Callback khi nhấn Next Question trên popup
                                currentQuestionIndex++;
                                if (currentQuestionIndex < questions.size()) {
                                    Question nextQuestion = questions.get(currentQuestionIndex);
                                    updateProgressBar(progressBar, currentQuestionIndex);
                                    if (nextQuestion.getQuesType().equals("MULTIPLE")) {
                                        Intent intent = new Intent(GrammarPick1QuestionActivity.this, GrammarPickManyActivity.class);
                                        intent.putExtra("currentQuestionIndex", currentQuestionIndex);
                                        Log.e("pick1","currentQuestionIndex");
                                        intent.putExtra("questions", (Serializable) questions);
                                        intent.putExtra("courseID",courseID);
                                        intent.putExtra("lessonID",lessonID);
                                        intent.putExtra("enrollmentId", enrollmentId);
                                        startActivity(intent);
                                        finish(); // Đóng Activity hiện tại
                                    } else if (nextQuestion.getQuesType().equals("TEXT")) {
                                        Intent intent = new Intent(GrammarPick1QuestionActivity.this, ReadingTextActivity.class);
                                        intent.putExtra("currentQuestionIndex", currentQuestionIndex);
                                        Log.e("pick1","currentQuestionIndex");
                                        intent.putExtra("questions", (Serializable) questions);
                                        intent.putExtra("courseID",courseID);
                                        intent.putExtra("lessonID",lessonID);
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
                                        Log.e("GrammarPick1QuestionActivity", "Answer ID từ API: " + answer.getId());
                                        if (answerIds != 0) {
                                            QuestionManager.gradeAnswer(answerIds, new Callback() {
                                                @Override
                                                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                                    Log.e("GrammarPick1QuestionActivity", "Lỗi khi chấm điểm: " + e.getMessage());
                                                }

                                                @Override
                                                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                                    if (response.isSuccessful()) {
                                                        Log.e("GrammarPick1QuestionActivity", "Chấm điểm thành công cho Answer ID: " + answerIds +"Diem: "+ answer.getPointAchieved());
                                                    } else {
                                                        Log.e("GrammarPick1QuestionActivity", "Lỗi từ server: " + response.code());
                                                    }
                                                }
                                            });
                                        } else {
                                            Log.e("GrammarPick1QuestionActivity", "Bài học không có câu trl.");
                                        }
                                    } else {
                                        Log.e("GrammarPick1QuestionActivity", "Không nhận được câu trả lời từ API.");
                                    }
                                }

                                @Override
                                public void onFailure(String errorMessage) {

                                }
                            });
                        }

                    @Override
                    public void onSuccess(Object result) {}
                    @Override
                    public void onFailure(String errorMessage) {
                        Log.e("GrammarPick1QuestionActivity", errorMessage);
                    }
                });

            }
        });
    }

    private void anHienAnh() {
        ImageView imgLessonMaterial = findViewById(R.id.imgLessonMaterial);
        Button btnToggleImage = findViewById(R.id.btnToggleImage);
        btnToggleImage.setOnClickListener(v -> {
            if (isImageVisible) {
                imgLessonMaterial.setVisibility(View.GONE); // Ẩn ảnh
                btnToggleImage.setText("Hiện ảnh");
            } else {
                imgLessonMaterial.setVisibility(View.VISIBLE); // Hiện ảnh
                btnToggleImage.setText("Ẩn ảnh");
            }
            isImageVisible = !isImageVisible; // Đảo trạng thái
        });
    }

    private void loadQuestion(int index) {
        if (index < questions.size()) {
            Question question = questions.get(index);
            questype = question.getQuesType();
            quesManager.fetchQuestionContentFromApi(question.getId(), new ApiCallback<Question>() {
                @Override
                public void onSuccess(Question question) {
                    if (question != null) {
                        // Lấy nội dung câu hỏi
                        String questionContent = question.getQuesContent();
                        Log.d("GrammarPick1QuestionActivity", "Câu hỏi: " + questionContent);

                        List<QuestionChoice> choices = question.getQuestionChoices();
                        if (choices != null && !choices.isEmpty()) {
                            runOnUiThread(() -> {
                                tvContent.setText(questionContent);
                                userAnswers.clear();
                                ChoiceAdapter choiceAdapter = new ChoiceAdapter(GrammarPick1QuestionActivity.this, choices, userAnswers);
                                recyclerViewChoices.setAdapter(choiceAdapter);
                                for (QuestionChoice choice : choices) {
                                    if (choice.isChoiceKey()) {
                                        correctAnswers = (choice.getChoiceContent());
                                    }
                                }
                            });
                        } else {
                            Log.e("GrammarPick1QuestionActivity", "Câu hỏi không có lựa chọn.");
                        }
                    } else {
                        Log.e("GrammarPick1QuestionActivity", "Câu hỏi trả về là null.");
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
        Intent intent = new Intent(GrammarPick1QuestionActivity.this, PointResultLessonActivity.class);
        intent.putExtra("lessonId",lessonID);
        intent.putExtra("courseId",courseID);
        intent.putExtra("enrollmentId", enrollmentId);
        startActivity(intent);
        finish();
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
}