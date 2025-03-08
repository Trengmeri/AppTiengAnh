package com.example.test.ui.question_data;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.test.R;
import com.example.test.model.Enrollment;
import com.example.test.ui.home.HomeActivity;
import com.example.test.api.ApiCallback;
import com.example.test.api.LessonManager;
import com.example.test.api.QuestionManager;
import com.example.test.api.ResultManager;
import com.example.test.model.Answer;
import com.example.test.model.Course;
import com.example.test.model.Lesson;
import com.example.test.model.Result;

import java.util.HashSet;
import java.util.Set;

public class PointResultCourseActivity extends AppCompatActivity {
    private LinearLayout readingSkillLayout, listeningSkillLayout, speakingSkillLayout, writingSkillLayout;
    private TextView pointTextView, totalComp;
    private Button btnReview, btnNext;
    private TextView correctRead, compRead;
    private TextView correctLis, compLis;
    private TextView correctSpeak, compSpeak;
    private TextView correctWrite, compWrite;
    private int totalPointR = 0,totalPointL = 0,totalPointS = 0,totalPointW = 0;
    private int r =0,l=0,s=0,w=0;
    private double compCourse;
    private int coursePoint;
    private double comR, comL, comS, comW;
    QuestionManager quesManager = new QuestionManager(this);
    LessonManager lesManager = new LessonManager();
    ResultManager resultManager = new ResultManager(this);
    private Set<Integer> addedResultIds = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_result);

        initializeViews();
        fetchCourseData(1);

        btnReview.setOnClickListener(v -> {
            Intent intent = new Intent(PointResultCourseActivity.this, ReviewAnswerActivity.class);
            startActivity(intent);
        });

        btnNext.setOnClickListener(v -> {
            Intent intent = new Intent(PointResultCourseActivity.this, HomeActivity.class);
            startActivity(intent);
        });
    }

    private void initializeViews() {
        pointTextView = findViewById(R.id.point);
        btnReview = findViewById(R.id.btnReview);
        btnNext = findViewById(R.id.btnNext);
        correctRead = findViewById(R.id.correct_read);
        compRead = findViewById(R.id.comp_read);
        correctLis = findViewById(R.id.correct_lis);
        compLis = findViewById(R.id.comp_lis);
        correctSpeak = findViewById(R.id.correct_speak);
        compSpeak = findViewById(R.id.comp_speak);
        correctWrite = findViewById(R.id.correct_write);
        compWrite = findViewById(R.id.comp_write);
        readingSkillLayout = findViewById(R.id.readingSkillLayout);
        listeningSkillLayout = findViewById(R.id.listeningSkillLayout);
        speakingSkillLayout = findViewById(R.id.speakingSkillLayout);
        writingSkillLayout = findViewById(R.id.writingSkillLayout);
        readingSkillLayout.setVisibility(View.GONE);
        listeningSkillLayout.setVisibility(View.GONE);
        speakingSkillLayout.setVisibility(View.GONE);
        writingSkillLayout.setVisibility(View.GONE);
    }

    private void fetchCourseData(int courseId) {
        resultManager.createEnrollment(courseId, new ApiCallback() {
            @Override
            public void onSuccess() {
                lesManager.fetchCourseById(courseId,new ApiCallback<Course>() {
                    @Override
                    public void onSuccess() {}

                    @Override
                    public void onSuccess(Course course) {
                        runOnUiThread(() -> {
                            if (course!= null && course.getLessonIds()!= null) {
                                int courseId = course.getId();
                                for (Integer lessonId: course.getLessonIds()) {
                                    fetchLessonAndCreateResult(lessonId,courseId);
                                }
                            } else {
                                Log.e("PointResultActivity","Không có khóa học nào.");
                            }
                        });
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Log.e("PointResultActivity",errorMessage);
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

    private void fetchLessonAndCreateResult(int lessonId, int courseId) {
        resultManager.getEnrollments(courseId, new ApiCallback<Enrollment>() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onSuccess(Enrollment enrollment) {
                if(enrollment != null){
                    int enrollmentId = enrollment.getId();
                    Log.e("ErollmentId: ", String.valueOf(enrollment.getId()));
                    lesManager.fetchLessonById(lessonId, new ApiCallback<Lesson>() {
                        @Override
                        public void onSuccess() {}

                        @Override
                        public void onSuccess(Lesson lesson) {
                            if (lesson!= null && lesson.getSkillType()!= null) {
                                String skillType = lesson.getSkillType();
                                if (lesson!= null && lesson.getQuestionIds()!= null) {
                                    for (Integer questionId : lesson.getQuestionIds()) {
                                        resultManager.fetchAnswerPointsByQuesId(questionId, new ApiCallback<Answer>() {
                                            @Override
                                            public void onSuccess() {
                                            }

                                            @Override
                                            public void onSuccess(Answer answer) {
                                                if (answer != null) {
                                                    createResultForLesson(lessonId, answer.getSessionId(), enrollmentId, skillType);
                                                } else {
                                                    Log.e("PointResultActivity", "Không có câu trả lời nào.");
                                                }
                                            }


                                            @Override
                                            public void onFailure(String errorMessage) {
                                                Log.e("PointResultActivity",errorMessage);
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

    private void createResultForLesson(int lessonId, int sessionId, int enrollmentId, String skillType) {
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
                            runOnUiThread(() -> updateUI(skillType, result.getComLevel(), result.getTotalPoints(), result.getId(), coursePoint, compCourse));
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

    private void updateUI(String skillType, double complete, int totalPoints, int resultId, int coursePoint, double compCourse) {
        runOnUiThread(() -> {
            if (!addedResultIds.contains(resultId)) { // Kiểm tra resultId
                addedResultIds.add(resultId); // Thêm resultId vào tập hợp

                switch (skillType) {
                    case "READING":
                        totalPointR += totalPoints;
                        comR += complete;
                        r++;
                        readingSkillLayout.setVisibility(View.VISIBLE);
                        break;
                    case "LISTENING":
                        totalPointL += totalPoints;
                        comL += complete;
                        l++;
                        listeningSkillLayout.setVisibility(View.VISIBLE);
                        break;
                    case "SPEAKING":
                        totalPointS += totalPoints;
                        comS += complete;
                        s++;
                        speakingSkillLayout.setVisibility(View.VISIBLE);
                        break;
                    case "WRITING":
                        totalPointW += totalPoints;
                        comW += complete;
                        w++;
                        writingSkillLayout.setVisibility(View.VISIBLE);
                        break;
                    default:
                        Log.e("PointResultActivity", "Skill type không hợp lệ: " + skillType);
                }
            }

            correctRead.setText("Point: " + totalPointR);
            compRead.setText("Complete: " + String.format("%.1f",comR/r));
            correctLis.setText("Point: " + totalPointL);
            compLis.setText("Complete: " + String.format("%.1f",comL/l));
            correctSpeak.setText("Point: " + totalPointS);
            compSpeak.setText("Complete: " + String.format("%.1f",comS/s));
            correctWrite.setText("Point: " + totalPointW);
            compWrite.setText("Complete: " + String.format("%.1f",comW/w));
            pointTextView.setText(String.valueOf(totalPointR+totalPointL+totalPointS+totalPointW));
        });
    }

}