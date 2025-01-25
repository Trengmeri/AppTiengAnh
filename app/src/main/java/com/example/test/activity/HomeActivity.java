package com.example.test.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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

public class HomeActivity extends AppCompatActivity {

    Button continueButton;
    LinearLayout lessonsContainer; // LinearLayout để chứa các bài học
    TextView courseTitle,lessonTitle1,lessonNumber; // TextView để hiển thị tên khóa học

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        continueButton = findViewById(R.id.btn_continue);
        lessonsContainer = findViewById(R.id.lessonsContainer); // ID của LinearLayout chứa bài học
        courseTitle = findViewById(R.id.courseTitle); // ID của TextView hiển thị tên khóa học
        lessonTitle1 = findViewById(R.id.lessonTitle);
        lessonNumber = findViewById(R.id.lessonNumber);

        continueButton.setOnClickListener(v -> {
            Toast.makeText(HomeActivity.this, "Continue studying clicked!", Toast.LENGTH_SHORT).show();
        });

        // Gọi API để lấy thông tin khóa học
        ApiManager apiManager = new ApiManager();
        apiManager.fetchCourseById( new ApiCallback() {
            @Override
            public void onSuccess(Course course) {
                runOnUiThread(() -> {
                    if (course != null) {
                        // Hiển thị tên khóa học
                        courseTitle.setText(course.getName());
                        lessonTitle1.setText(course.getName());
                        lessonNumber.setText("Lesson " + course.getId());

                        // Hiển thị danh sách bài học
                        List<Integer> lessonIds = course.getLessonIds();
                        for (Integer lessonId : lessonIds) {
                            // Gọi API để lấy thông tin bài học
                            apiManager.fetchLessonById(lessonId, new ApiCallback() {
                                @Override
                                public void onSuccess(Lesson lesson) {
                                    runOnUiThread(() -> {
                                        if (lesson != null) {
                                            // Hiển thị thông tin bài học trong LinearLayout
                                            View lessonView = getLayoutInflater().inflate(R.layout.item_lesson, null);
                                            TextView lessonTitle = lessonView.findViewById(R.id.lessonTitle);
                                            lessonTitle.setText(lesson.getName());
                                            lessonsContainer.addView(lessonView);
                                        }
                                    });
                                }

                                @Override
                                public void onSuccess(Course course) {}

                                @Override
                                public void onFailure(String errorMessage) {
                                    runOnUiThread(() -> {
                                        Toast.makeText(HomeActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                    });
                                }

                                @Override
                                public void onSuccessWithOtpID(String otpID) {}

                                @Override
                                public void onSuccess() {}

                                @Override
                                public void onSuccess(Result result) {}

                                @Override
                                public void onSuccess(List<Answer> answer) {}

                                @Override
                                public void onSuccess(ApiResponseAnswer response) {

                                }

                                @Override
                                public void onSuccess(Question question) {

                                }
                            });
                        }
                    } else {
                        Toast.makeText(HomeActivity.this, "Không có khóa học nào.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    Toast.makeText(HomeActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onSuccessWithOtpID(String otpID) {}

            @Override
            public void onSuccess() {}

            @Override
            public void onSuccess(Result result) {}

            @Override
            public void onSuccess(List<Answer> answer) {}

            @Override
            public void onSuccess(ApiResponseAnswer response) {

            }

            @Override
            public void onSuccess(Question question) {}

            @Override
            public void onSuccess(Lesson lesson) {

            }
        });
    }
}