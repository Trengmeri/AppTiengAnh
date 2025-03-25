package com.example.test.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.SharedPreferencesManager;
import com.example.test.adapter.CourseAdapter;
import com.example.test.adapter.LessonAdapter;
import com.example.test.adapter.ReviewAdapter;
import com.example.test.api.ApiCallback;
import com.example.test.api.CourseManager;
import com.example.test.api.LessonManager;
import com.example.test.api.ReviewManager;
import com.example.test.model.Course;
import com.example.test.model.Enrollment;
import com.example.test.model.Lesson;
import com.example.test.model.Review;

import java.util.ArrayList;
import java.util.List;

public class CourseActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CourseManager courseManager = new CourseManager(CourseActivity.this);
    private List<Course> listCourse = new ArrayList<>();
    private final int curUserId = SharedPreferencesManager.getInstance(CourseActivity.this).getUser().getId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        recyclerView = findViewById(R.id.recyclerView);;

        // Cấu hình RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getCourseInfor();
        CourseAdapter adapter = new CourseAdapter(this,listCourse);
        recyclerView.setAdapter(adapter);

    }
    private void getCourseInfor() {
        courseManager.fetchEnrollmentsByUser(curUserId, true, 1, 4, new ApiCallback<List<Enrollment>>() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onSuccess(List<Enrollment> data) {
                for (Enrollment enrollment : data) {
                    int courseId = enrollment.getCourseId();
                    courseManager.fetchCourseById(courseId, new ApiCallback<Course>() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onSuccess(Course result) {
                            runOnUiThread(() -> {
                                listCourse.add(result);
                                recyclerView.getAdapter().notifyDataSetChanged(); // Cập nhật UI
                            });
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Log.e("API_ERROR", "Lỗi: " + errorMessage);
                        }
                    });
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("API_ERROR", "Lỗi: " + errorMessage);
            }
        });
    }



}