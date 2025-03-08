package com.example.test.ui.question_data;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.test.R;
import com.example.test.api.ApiCallback;
import com.example.test.api.LessonManager;
import com.example.test.api.QuestionManager;
import com.example.test.api.ResultManager;
import com.example.test.model.Answer;
import com.example.test.model.Course;
import com.example.test.model.Lesson;
import com.example.test.model.Question;
import com.example.test.model.QuestionChoice;

import java.util.List;
import java.util.stream.Collectors;

public class ReviewAnswerActivity extends AppCompatActivity {
    LinearLayout lessonsContainer;
    TextView courseTitle,lessonTitle;
    ImageView btnBackto;
    LessonManager lesManager = new LessonManager();
    QuestionManager quesManager = new QuestionManager(this);
    ResultManager resultManager = new ResultManager(this);
    int lessonId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_review_answer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        courseTitle = findViewById(R.id.courseTitle);
        btnBackto = findViewById(R.id.btnBackto);
        lessonsContainer = findViewById(R.id.lessonsContainer);


        btnBackto.setOnClickListener(v -> {
            finish();
        });
        fetchCourseData(1); // Thay courseId bằng courseId thực tế
    }

    private LinearLayout addLessonLayout(String lessonName) {
        LinearLayout lessonLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.item_lesson, null);
        TextView lessonTitleTextView = lessonLayout.findViewById(R.id.lessonTitle);
        lessonTitleTextView.setText(lessonName);
        lessonsContainer.addView(lessonLayout);
        return lessonLayout; // Trả về lessonLayout
    }

    private void fetchCourseData(int courseId) {
        lesManager.fetchCourseById(courseId, new ApiCallback<Course>() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onSuccess(Course course) {
                runOnUiThread(() -> {
                    if (course!= null) {
                        courseTitle.setText(course.getName());

                        List<Integer> lessonIds = course.getLessonIds();

                        for (int i =0 ; i< lessonIds.size(); i++) {
                            lessonId = lessonIds.get(i);
                            fetchLessonData(lessonId);
                            Log.e("LessonId: ", lessonIds.get(i).toString());
                        }
                    } else {
                        Log.e("ReviewAnswerActivity", "Không có khóa học nào.");
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });
    }

    private void fetchLessonData(int lessonId) {
        lesManager.fetchLessonById(lessonId, new ApiCallback<Lesson>() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onSuccess(Lesson lesson) {
                runOnUiThread(() -> {
                    if (lesson!= null && lesson.getQuestionIds()!= null) {
                        String lessonContext = lesson.getName() + ": " + lesson.getSkillType();
                        LinearLayout lessonLayout = addLessonLayout(lessonContext);
                        LinearLayout questionsContainer = lessonLayout.findViewById(R.id.questionsContainer);
                        for (Integer questionId: lesson.getQuestionIds()) {
                            fetchQuestionAndAnswer(questionId, questionsContainer);
                        }
                    } else {
                        Log.e("ReviewAnswerActivity", "Không có câu hỏi nào.");
                    }
                });
            }


            @Override
            public void onFailure(String errorMessage) {

            }

        });
    }

    private void fetchQuestionAndAnswer(int questionId, LinearLayout questionsContainer) {
        quesManager.fetchQuestionContentFromApi(questionId, new ApiCallback<Question>() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onSuccess(Question question) {
                resultManager.fetchAnswerPointsByQuesId(questionId, new ApiCallback<Answer>() {
                    @Override
                    public void onSuccess() {

                    }
                    @Override
                    public void onSuccess(Answer answer) {
                        runOnUiThread(() -> {
                            if (question!= null && answer!= null) {
                                View answerView = getLayoutInflater().inflate(R.layout.item_answer, null);

                                TextView questionContent = answerView.findViewById(R.id.question);
                                TextView correctAnswer = answerView.findViewById(R.id.correct_answer);
                                TextView yourAnswer = answerView.findViewById(R.id.youranswer);
                                TextView point = answerView.findViewById(R.id.point);

                                questionContent.setText("Question: " +question.getQuesContent());
                                // Lấy correctAnswer từ question
                                List<QuestionChoice> choices = question.getQuestionChoices();
                                List<String> correctAnswers = question.getQuestionChoices().stream()
                                        .filter(QuestionChoice::isChoiceKey)
                                        .map(QuestionChoice::getChoiceContent)
                                        .collect(Collectors.toList());

                                String correctAnswerString = String.join(", ", correctAnswers);

                                if(answer.getPointAchieved() == 0){
                                    yourAnswer.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                                } else {
                                    yourAnswer.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                                }

                                correctAnswer.setText("Correct answer: " + correctAnswerString);

                                yourAnswer.setText("Your answer: " + answer.getAnswerContent());
                                point.setText("Point: " + answer.getPointAchieved());

                                questionsContainer.addView(answerView);
                            } else {
                                Log.e("ReviewAnswerActivity", "Câu hỏi hoặc câu trả lời không hợp lệ.");
                            }
                        });
                    }

                    @Override
                    public void onFailure(String errorMessage) {

                    }

                });
            }

            @Override
            public void onFailure(String errorMessage) {

            }

        });
    }
}