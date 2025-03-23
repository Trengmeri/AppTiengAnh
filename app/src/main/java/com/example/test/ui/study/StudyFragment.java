package com.example.test.ui.study;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
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

public class StudyFragment extends Fragment {
    private RecyclerView recyclerView;
    private CourseAdapter adapter;
    private List<Course> courseList;
    private LessonManager lessonManager = new LessonManager();
    AppCompatButton btnMyCourse, btnSuggestCourse;
    private String prostatus = "True"; // Mac dinh ban dau cho xem khoa hoc cua toi

    public StudyFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_study, container, false);
        return view;
    }

    @SuppressLint("WrongViewCast")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        btnMyCourse= view.findViewById(R.id.btnMyCourse);
        btnSuggestCourse= view.findViewById(R.id.btnSuggestCourse);

        // cau hinh recyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        courseList = new ArrayList<>();
        adapter = new CourseAdapter(getContext(), courseList);
        recyclerView.setAdapter(adapter);

        btnMyCourse.setOnClickListener(v -> {
            btnMyCourse.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_about));
            btnMyCourse.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_lesson));
            prostatus = "True";
        });

        btnSuggestCourse.setOnClickListener(v -> {
            btnSuggestCourse.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_about));
            btnSuggestCourse.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_lesson));
            prostatus = "False";
        });


        fetchCourses();
    }
    private void fetchCourses() {
        lessonManager.fetchAllCourseIds(prostatus, new ApiCallback<List<Integer>>() {
            @Override
            public void onSuccess() {}

            @Override
            public void onSuccess(List<Integer> courseIds) {
                Log.d("StudyFragment", "📌 Danh sách Course ID: " + courseIds);
                fetchLessonsForCourses(courseIds);
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("StudyFragment", "❌ Lỗi khi lấy danh sách khóa học: " + errorMessage);
            }
        });
    }

    private void fetchLessonsForCourses(List<Integer> courseIds) {
        for (Integer courseId : courseIds) {
            lessonManager.fetchCourseById(courseId, new ApiCallback<Course>() {
                @Override
                public void onSuccess() {}

                @Override
                public void onSuccess(Course course) {
                    Log.d("StudyFragment", "📌 Course ID: " + course.getId() + ", Lessons: " + course.getLessonIds());
                    courseList.add(course);

                    // Cập nhật RecyclerView
                    requireActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
                }

                @Override
                public void onFailure(String errorMessage) {
                    Log.e("StudyFragment", "❌ Lỗi khi lấy khóa học ID " + courseId + ": " + errorMessage);
                }
            });
        }
    }
}
