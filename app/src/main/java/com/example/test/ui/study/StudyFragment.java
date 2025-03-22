package com.example.test.ui.study;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.example.test.api.EnrollmentManager;
import com.example.test.api.LessonManager;
import com.example.test.model.Course;

import java.util.ArrayList;
import java.util.List;

public class StudyFragment extends Fragment {
    private RecyclerView recyclerView1, recyclerView2;
    private CourseAdapter adapter;
    private List<Course> courseList;
    private LessonManager lessonManager;
    private EnrollmentManager enrollmentManager;
    AppCompatButton btnAbout, btnLesson;
    LinearLayout contentAbout, contentLes;
    String prostatus = "false";

    public StudyFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_study, container, false);
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        enrollmentManager = new EnrollmentManager(context); // Lấy context khi Fragment được gắn vào Activity
    }

    @SuppressLint("WrongViewCast")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView1 = view.findViewById(R.id.recyclerView1);
        recyclerView1.setLayoutManager(new LinearLayoutManager(getContext()));

        btnAbout= view.findViewById(R.id.btnAbout);
        btnLesson= view.findViewById(R.id.btnLesson);
        contentAbout = view.findViewById(R.id.contentAbout);
        contentLes = view.findViewById(R.id.contentLes);

        btnAbout.setOnClickListener(v -> {
            btnAbout.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_about));
            btnLesson.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_lesson));
            contentAbout.setVisibility(View.VISIBLE);
            contentLes.setVisibility(View.GONE);
        });

        btnLesson.setOnClickListener(v -> {
            btnLesson.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_about));
            btnAbout.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_lesson));
            contentAbout.setVisibility(View.GONE);
            contentLes.setVisibility(View.VISIBLE);
        });

        courseList = new ArrayList<>();
        adapter = new CourseAdapter(getContext(), courseList);
        recyclerView1.setAdapter(adapter);

        lessonManager = new LessonManager();
        fetchCourses();
    }
    private void fetchCourses() {
        enrollmentManager.fetchAllEnrolledCourseIds(prostatus, new ApiCallback<List<Integer>>() {
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
