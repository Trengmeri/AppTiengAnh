package com.example.test.ui.study;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.adapter.CourseAdapter;
import com.example.test.api.ApiCallback;
import com.example.test.api.CourseManager;
import com.example.test.api.LessonManager;
import com.example.test.model.Course;

import java.util.ArrayList;
import java.util.List;

public class StudyActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CourseAdapter adapter;
    private List<Course> courseList;
    LessonManager lessonManager = new LessonManager();
    CourseManager courseManager = new CourseManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Dữ liệu giả
        courseList = new ArrayList<>();

        adapter = new CourseAdapter(this, courseList);
        recyclerView.setAdapter(adapter);

        lessonManager.fetchAllCourseIds(new ApiCallback<List<Integer>>() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onSuccess(List<Integer> courseIds) {
                Log.d("MainActivity", "📌 Danh sách Course ID: " + courseIds);
                fetchLessonsForCourses(courseIds);
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("MainActivity", "Lỗi khi lấy danh sách khóa học: " + errorMessage);
            }
        });
    }
    private void fetchLessonsForCourses(List<Integer> courseIds) {
        for (Integer courseId : courseIds) {
            courseManager.fetchCourseById(courseId, new ApiCallback<Course>() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onSuccess(Course course) {
                    Log.d("MainActivity", "📌 Course ID: " + course.getId() + ", Lessons: " + course.getLessonIds());
                }

                @Override
                public void onFailure(String errorMessage) {
                    Log.e("MainActivity", "Lỗi khi lấy khóa học ID " + courseId + ": " + errorMessage);
                }
            });
        }
    }

}

