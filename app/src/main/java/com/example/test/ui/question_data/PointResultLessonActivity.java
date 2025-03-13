package com.example.test.ui.question_data;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.test.R;
import com.example.test.api.ApiCallback;
import com.example.test.api.LessonManager;
import com.example.test.api.QuestionManager;
import com.example.test.api.ResultManager;
import com.example.test.model.Answer;
import com.example.test.model.Enrollment;
import com.example.test.model.Lesson;
import com.example.test.model.Question;
import com.example.test.model.QuestionChoice;
import com.example.test.model.Result;
import com.example.test.ui.DiscussionActivity;
import com.example.test.ui.home.HomeActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PointResultLessonActivity extends AppCompatActivity {

    Button btnDone,btnDiscuss;
    private int lessonID,courseID;
    QuestionManager quesManager = new QuestionManager(this);
    LessonManager lesManager = new LessonManager();
    ResultManager resultManager = new ResultManager(this);
    TextView point;
    ImageView star1,star2,star3;
    TableLayout tableResult;
    // Set lưu các ID đã gọi để tránh gọi trùng
    private Set<Integer> calledAnswerIds = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_point_result_lesson);
        AnhXa();

        courseID = getIntent().getIntExtra("courseId",1);
        lessonID = getIntent().getIntExtra("lessonId",1);


        Log.e("point","Lesson ID: "+ lessonID + "courseID: "+ courseID);

        fetchCourseData(courseID,lessonID);
        fetchLessonData(lessonID);
        btnDone.setOnClickListener(v -> {
                Intent intent = new Intent(PointResultLessonActivity.this, HomeActivity.class);
                startActivity(intent);
        });
        btnDiscuss.setOnClickListener(v -> {
            Intent intent = new Intent(PointResultLessonActivity.this, DiscussionActivity.class);
            startActivity(intent);
        });
    }

    public void AnhXa(){
        btnDone = findViewById(R.id.btnDone);
        point = findViewById(R.id.point);
        star1 = findViewById(R.id.star1);
        star2 = findViewById(R.id.star2);
        star3 = findViewById(R.id.star3);
        btnDiscuss=findViewById(R.id.btnDiscuss);
        tableResult= findViewById(R.id.tableResult);
    }

    private void fetchCourseData(int courseId, int lessonId) {
        resultManager.createEnrollment(courseId, new ApiCallback() {
            @Override
            public void onSuccess() {
                resultManager.getEnrollment(courseId, new ApiCallback<Enrollment>() {
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

                    @SuppressLint("UseCompatLoadingForColorStateLists")
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
                            fetchQuestionAndAnswer(questionId, tableResult);
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
                            // Tạo một hàng mới cho bảng
                            TableRow row = new TableRow(tableResult.getContext());

                            // TextView cho câu hỏi + đáp án đúng
                            TextView questionTextView = new TextView(tableResult.getContext());
                            // Tạo SpannableStringBuilder để thay đổi màu chữ trong cùng một TextView
                            SpannableStringBuilder spannable = new SpannableStringBuilder();

                            // Thêm nội dung câu hỏi với định dạng in đậm
                            spannable.append("Q: ").append(question.getQuesContent()).append("\n");

                            // Định dạng "Correct:" với màu xanh
                            int correctStart = spannable.length();
                            spannable.append("Correct: ");
                            int correctEnd = spannable.length();
                            spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#4CAF50")), correctStart, correctEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                            // Lấy danh sách đáp án đúng
                            List<QuestionChoice> choices = question.getQuestionChoices();
                            List<String> correctAnswers = (choices != null) ?
                                    choices.stream()
                                            .filter(QuestionChoice::isChoiceKey)
                                            .map(QuestionChoice::getChoiceContent)
                                            .collect(Collectors.toList())
                                    : new ArrayList<>();

                            String correctAnswerString = correctAnswers.isEmpty() ? "N/A" : String.join(", ", correctAnswers);
//                            if (question.getQuesType().equals("CHOICE")|| question.getQuesType().equals("MULTIPLE")) {
//                                correctAnswer.setText("Correct answer: " + correctAnswerString);
//                            }

                            // Định dạng đáp án đúng với màu xanh
                            int answerStart = spannable.length();
                            spannable.append(correctAnswerString);
                            int answerEnd = spannable.length();
                            spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#4CAF50")), answerStart, answerEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                            questionTextView.setText(spannable);
                            questionTextView.setPadding(10, 10, 10, 10);
                            questionTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
                            questionTextView.setTypeface(null, Typeface.BOLD);

                            // TextView cho câu trả lời của người dùng
                            TextView userAnswerTextView = new TextView(tableResult.getContext());
                            userAnswerTextView.setText(answer.getAnswerContent());
                            userAnswerTextView.setPadding(10, 10, 10, 10);
                            userAnswerTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
                            userAnswerTextView.setTypeface(null, Typeface.BOLD);

                            // TextView cho điểm số
                            TextView pointTextView = new TextView(tableResult.getContext());
                            pointTextView.setText(String.valueOf(answer.getPointAchieved()));
                            pointTextView.setPadding(10, 10, 10, 10);
                            pointTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
                            pointTextView.setGravity(Gravity.CENTER);
                            pointTextView.setTypeface(null, Typeface.BOLD);
                            // Đổi màu chữ tùy theo đúng/sai
                            if (answer.getPointAchieved() == 0) {
                                userAnswerTextView.setTextColor(ContextCompat.getColor(tableResult.getContext(), android.R.color.holo_red_dark));
                                pointTextView.setTextColor(ContextCompat.getColor(tableResult.getContext(), android.R.color.holo_red_dark));
                            } else {
                                userAnswerTextView.setTextColor(ContextCompat.getColor(tableResult.getContext(), android.R.color.holo_green_dark));
                                pointTextView.setTextColor(ContextCompat.getColor(tableResult.getContext(), android.R.color.holo_green_dark));
                            }
                            // Thêm các TextView vào hàng
                            row.addView(questionTextView);
                            row.addView(userAnswerTextView);
                            row.addView(pointTextView);

                            // Thêm hàng vào bảng
                            tableResult.addView(row);
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