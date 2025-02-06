package com.example.test.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.test.R;
import com.example.test.api.ApiCallback;
import com.example.test.api.ApiManager;
import com.example.test.api.ApiResponseAnswer;
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

        // Ánh xạ các view
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

        // Gọi API để lấy thông tin khóa học
        fetchCourseData();

        // Sự kiện cho nút Review
        btnReview.setOnClickListener(v ->
                Toast.makeText(PointResultActivity.this, "Review button clicked", Toast.LENGTH_SHORT).show()
        );

        // Sự kiện cho nút Next
        btnNext.setOnClickListener(v -> {
            Intent intent = new Intent(PointResultActivity.this, HomeActivity.class);
            startActivity(intent);
        });
    }

    private void fetchCourseData() {
        ApiManager apiManager = new ApiManager();
        apiManager.fetchCourseById(new ApiCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onSuccess(Question question) {

            }

            @Override
            public void onSuccess(Lesson lesson) {

            }

            @Override
            public void onSuccess(Course course) {
                runOnUiThread(() -> {
                    if (course != null) {
                        List<Integer> lessonIds = course.getLessonIds();
                        for (Integer lessonId : lessonIds) {
                            fetchLessonAndResult(lessonId);
                        }
                    } else {
                        Toast.makeText(PointResultActivity.this, "Không có khóa học nào.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onSuccess(Result result) {

            }

            @Override
            public void onSuccess(List<Answer> answer) {

            }

            @Override
            public void onSuccess(ApiResponseAnswer response) {

            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() ->
                        Toast.makeText(PointResultActivity.this, errorMessage, Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onSuccessWithOtpID(String otpID) {

            }

            @Override
            public void onSuccessWithToken(String token) {

            }
        });
    }

    private void fetchLessonAndResult(int lessonId) {
        ApiManager apiManager = new ApiManager();
        apiManager.fetchLessonById(lessonId, new ApiCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onSuccess(Question question) {

            }

            @Override
            public void onSuccess(Lesson lesson) {
                if (lesson != null) {
                    String skillType = lesson.getSkillType();
                    apiManager.fetchResultByLesson(lessonId, new ApiCallback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onSuccess(Question question) {

                        }

                        @Override
                        public void onSuccess(Lesson lesson) {

                        }

                        @Override
                        public void onSuccess(Course course) {

                        }

                        @Override
                        public void onSuccess(Result result) {
                            int totalPoints = result.getTotalPoints();
                            int stuTime = result.getStuTime();

                            // Cập nhật giao diện người dùng
                            runOnUiThread(() -> updateUI(skillType, stuTime, totalPoints));
                        }

                        @Override
                        public void onSuccess(List<Answer> answer) {

                        }

                        @Override
                        public void onSuccess(ApiResponseAnswer response) {

                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            runOnUiThread(() ->
                                    Toast.makeText(PointResultActivity.this, errorMessage, Toast.LENGTH_SHORT).show()
                            );
                        }

                        @Override
                        public void onSuccessWithOtpID(String otpID) {

                        }

                        @Override
                        public void onSuccessWithToken(String token) {

                        }
                    });
                }
            }

            @Override
            public void onSuccess(Course course) {

            }

            @Override
            public void onSuccess(Result result) {

            }

            @Override
            public void onSuccess(List<Answer> answer) {

            }

            @Override
            public void onSuccess(ApiResponseAnswer response) {

            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("PointResultActivity", errorMessage);
            }

            @Override
            public void onSuccessWithOtpID(String otpID) {

            }

            @Override
            public void onSuccessWithToken(String token) {

            }
        });
    }

    private void updateUI(String skillType, int stuTime, int totalPoints) {
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
        }
    }
}