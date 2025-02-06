package com.example.test.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.test.R;
import com.example.test.api.ApiCallback;
import com.example.test.api.ApiManager;
import com.example.test.model.Answer;
import com.example.test.model.Course;
import com.example.test.model.Lesson;
import com.example.test.model.Question;
import com.example.test.model.Result;

import java.util.List;

public class PointResultActivity extends AppCompatActivity {
    private TextView pointTextView;
    private Button btnReview, btnNext;
    private TextView correctRead, compRead;
    private TextView correctLis, compLis;
    private TextView correctSpeak, compSpeak;
    private TextView correctWrite, compWrite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_result);

        initializeViews();
        fetchCourseData();

        btnReview.setOnClickListener(v ->
                Toast.makeText(PointResultActivity.this, "Review button clicked", Toast.LENGTH_SHORT).show()
        );

        btnNext.setOnClickListener(v -> {
            Intent intent = new Intent(PointResultActivity.this, HomeActivity.class);
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
    }

    private void fetchCourseData() {
        ApiManager apiManager = new ApiManager();
        apiManager.fetchCourseById(new ApiCallback() {
            @Override
            public void onSuccess() {}

            @Override
            public void onSuccess(Question questions) {

            }

            @Override
            public void onSuccess(Lesson lesson) {}

            @Override
            public void onSuccess(Course course) {
                runOnUiThread(() -> {
                    if (course!= null && course.getLessonIds()!= null) {
                        for (Integer lessonId: course.getLessonIds()) {
                            fetchLessonAndCreateResult(lessonId);
                        }
                    } else {
                        showToast("Không có khóa học nào.");
                    }
                });
            }

            @Override
            public void onSuccess(Result result) {}

            @Override
            public void onSuccess(Answer answer) {}

            @Override
            public void onFailure(String errorMessage) {
                showToast(errorMessage);
            }

            @Override
            public void onSuccessWithOtpID(String otpID) {}
            public void onSuccessWithOtpID(String otpID) {

            }

            @Override
            public void onSuccessWithToken(String token) {

            }
        });
    }

    private void fetchLessonAndCreateResult(int lessonId) {
        ApiManager apiManager = new ApiManager();
        apiManager.fetchLessonById(lessonId, new ApiCallback() {
            @Override
            public void onSuccess() {}

            @Override
            public void onSuccess(Question questions) {

            }

            @Override
            public void onSuccess(Lesson lesson) {
                if (lesson!= null && lesson.getSkillType()!= null) {
                    String skillType = lesson.getSkillType();
                    if (lesson!= null && lesson.getQuestionIds()!= null) {
                        for (Integer questionId : lesson.getQuestionIds()) {
                            apiManager.fetchAnswerPointsByQuesId(questionId, new ApiCallback() {
                                @Override
                                public void onSuccess() {
                                }

                                @Override
                                public void onSuccess(Question questions) {

                                }

                                @Override
                                public void onSuccess(Lesson lesson) {
                                }

                                @Override
                                public void onSuccess(Course course) {
                                }

                                @Override
                                public void onSuccess(Result result) {
                                }

                                @Override
                                public void onSuccess(Answer answer) {
                                    if (answer != null) {
                                        int sessionId = answer.getSessionId();
                                        createResultForLesson(lessonId, sessionId, skillType);
                                    } else {
                                        Log.e("PointResultActivity", "Không có câu trả lời nào.");
                                    }
                                }

                                @Override
                                public void onFailure(String errorMessage) {
                                    showToast(errorMessage);
                                }

                                @Override
                                public void onSuccessWithOtpID(String otpID) {
                                }
                            });
                        }
                    }
                } else {
                    Log.e("PointResultActivity", "Bài học hoặc skillType không hợp lệ.");
                }
            }

            @Override
            public void onSuccess(Course course) {}

            @Override
            public void onSuccess(Result result) {}

            @Override
            public void onSuccess(Answer answer) {}

            @Override
            public void onFailure(String errorMessage) {
                Log.e("PointResultActivity", errorMessage);
            }

            @Override
            public void onSuccessWithOtpID(String otpID) {}
        });
    }

    private void createResultForLesson(int lessonId, int sessionId, String skillType) {
        ApiManager apiManager = new ApiManager();
        apiManager.createResult(lessonId, sessionId, 1, new ApiCallback() {
            @Override
            public void onSuccess() {}

            @Override
            public void onSuccess(Question questions) {

            }

            @Override
            public void onSuccess(Lesson lesson) {}

            @Override
            public void onSuccess(Course course) {}

            @Override
            public void onSuccess(Result result) {
                if (result!= null) {
                    int lessonIds = result.getLessionId();
                    Log.d("PointResultActivity", "createResultForLesson: Gọi fetchResultByLesson"); // Log trước khi gọi fetchResultByLesson
                    apiManager.fetchResultByLesson(lessonIds, new ApiCallback() {
                        @Override
                        public void onSuccess() {}

                        @Override
                        public void onSuccess(Question questions) {

                        }

                        @Override
                        public void onSuccessWithToken(String token) {

                        }
                    });
                }
            }

                        @Override
                        public void onSuccess(Lesson lesson) {}

                        @Override
                        public void onSuccess(Course course) {}

                        @Override
                        public void onSuccess(Result result) {
                            if (result!= null) {
                                Log.d("PointResultActivity", "fetchResultByLesson: Lấy Result thành công");
                                runOnUiThread(() -> updateUI(skillType, result.getStuTime(), result.getTotalPoints()));
                            } else {
                                Log.e("PointResultActivity", "fetchResultByLesson: Kết quả không hợp lệ.");
                            }
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Log.e("PointResultActivity", "fetchResultByLesson: " + errorMessage);
                            showToast(errorMessage);
                        }

                        @Override
                        public void onSuccess(Answer answer) {}


                        @Override
                        public void onSuccessWithOtpID(String otpID) {}
                    });
                } else {
                    Log.e("PointResultActivity", "Result không hợp lệ.");
                }
            }

            @Override
            public void onSuccess(Answer answer) {}

            @Override
            public void onFailure(String errorMessage) {
                showToast(errorMessage);
            }

            @Override
            public void onSuccessWithOtpID(String otpID) {

            }

            @Override
            public void onSuccessWithToken(String token) {

            }
            public void onSuccessWithOtpID(String otpID) {}
        });
    }

    private void updateUI(String skillType, int stuTime, int totalPoints) {
        runOnUiThread(() -> {
            pointTextView.setText(String.valueOf(totalPoints));
            switch (skillType) {
                case "READING":
                    correctRead.setText("Correct: " + stuTime);
                    compRead.setText("Complete: " + totalPoints);
                    break;
                case "LISTENING":
                    correctLis.setText("Correct: " + stuTime);
                    compLis.setText("Complete: " + totalPoints);
                    break;
                case "SPEAKING":
                    correctSpeak.setText("Correct: " + stuTime);
                    compSpeak.setText("Complete: " + totalPoints);
                    break;
                case "WRITING":
                    correctWrite.setText("Correct: " + stuTime);
                    compWrite.setText("Complete: " + totalPoints);
                    break;
                default:
                    Log.e("PointResultActivity", "Skill type không hợp lệ: " + skillType);
            }
        });
    }

    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(PointResultActivity.this, message, Toast.LENGTH_SHORT).show());
    }
}