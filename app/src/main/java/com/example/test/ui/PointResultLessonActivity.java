package com.example.test.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.example.test.model.Enrollment;
import com.example.test.model.Lesson;
import com.example.test.model.Question;
import com.example.test.model.QuestionChoice;
import com.example.test.model.Result;
import com.example.test.ui.home.HomeActivity;

import java.util.List;
import java.util.stream.Collectors;

public class PointResultLessonActivity extends AppCompatActivity {

    Button btnDone;
    private int lessonID,courseID;
    QuestionManager quesManager = new QuestionManager(this);
    LessonManager lesManager = new LessonManager();
    ResultManager resultManager = new ResultManager(this);
    TextView point;
    ImageView star1,star2,star3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_point_result_lesson);
        AnhXa();

        courseID = getIntent().getIntExtra("courseId",1);
        lessonID = getIntent().getIntExtra("lessonId",1);

        fetchCourseData(courseID,lessonID);
        fetchLessonData(lessonID);
        btnDone.setOnClickListener(v -> {
                Intent intent = new Intent(PointResultLessonActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
        });
    }

    public void AnhXa(){
        btnDone = findViewById(R.id.btnDone);
        point = findViewById(R.id.point);
        star1 = findViewById(R.id.star1);
        star2 = findViewById(R.id.star2);
        star3 = findViewById(R.id.star3);
    }

    private void fetchCourseData(int courseId, int lessonId) {
        resultManager.createEnrollment(courseId, new ApiCallback() {
            @Override
            public void onSuccess() {
                resultManager.getEnrollments(courseId, new ApiCallback<Enrollment>() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onSuccess(Enrollment enrollment) {
                        if (enrollment != null) {
                            int enrollmentId = enrollment.getId();
                            Log.e("ErollmentId: ", String.valueOf(enrollment.getId()));
                            lesManager.fetchLessonById(lessonId, new ApiCallback<Lesson>() {
                                @Override
                                public void onSuccess() {
                                }

                                @Override
                                public void onSuccess(Lesson lesson) {
                                    if (lesson != null && lesson.getSkillType() != null) {
                                        if (lesson != null && lesson.getQuestionIds() != null) {
                                            for (Integer questionId : lesson.getQuestionIds()) {
                                                resultManager.fetchAnswerPointsByQuesId(questionId, new ApiCallback<Answer>() {
                                                    @Override
                                                    public void onSuccess() {
                                                    }

                                                    @Override
                                                    public void onSuccess(Answer answer) {
                                                        if (answer != null) {
                                                            createResultForLesson(lessonId, answer.getSessionId(), enrollmentId);
                                                        } else {
                                                            Log.e("PointResultActivity", "Không có câu trả lời nào.");
                                                        }
                                                    }


                                                    @Override
                                                    public void onFailure(String errorMessage) {
                                                        Log.e("PointResultActivity", errorMessage);
                                                    }

                                                });
                                            }
                                        }
                                    } else {
                                        Log.e("PointResultActivity", "Bài học hoặc skillType không hợp lệ.");
                                    }
                                }

                                @Override
                                public void onFailure(String errorMessage) {
                                    Log.e("PointResultActivity", errorMessage);
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(String errorMessage) {

                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {

            }

            @Override
            public void onSuccess(Object result){}
        });
    }


    private void createResultForLesson(int lessonId, int sessionId, int enrollmentId) {
        resultManager.createResult(lessonId, sessionId, enrollmentId, new ApiCallback() {
            @Override
            public void onSuccess() {
                Log.d("PointResultActivity", "createResultForLesson: Gọi fetchResultByLesson"); // Log trước khi gọi fetchResultByLesson
                resultManager.fetchResultByLesson(lessonId, new ApiCallback<Result>() {
                    @Override
                    public void onSuccess() {}

                    @Override
                    public void onSuccess(Result result) {
                        if (result!= null) {
                            Log.d("PointResultActivity", "fetchResultByLesson: Lấy Result thành công");
                            runOnUiThread(() -> {
                                point.setText(String.valueOf(result.getTotalPoints()));
                                if (result.getComLevel() > 90) {
                                    star3.setBackgroundTintList(getResources().getColorStateList(R.color.yellow));
                                }
                                if (result.getComLevel() > 60) {
                                    star2.setBackgroundTintList(getResources().getColorStateList(R.color.yellow));
                                }
                                if (result.getComLevel() > 30) {
                                    star1.setBackgroundTintList(getResources().getColorStateList(R.color.yellow));
                                }
                            });
                        } else {
                            Log.e("PointResultActivity", "fetchResultByLesson: Kết quả không hợp lệ.");
                        }
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Log.e("PointResultActivity", "fetchResultByLesson: " + errorMessage);
                    }
                });
            }

            @Override
            public void onSuccess(Object result){}


            @Override
            public void onFailure(String errorMessage) {
                Log.e("PointResultActivity",errorMessage);
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
                        LinearLayout questionsContainer = findViewById(R.id.questionsContainer);
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