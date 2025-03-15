package com.example.test.ui.study;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.adapter.CourseAdapter;
import com.example.test.api.ApiCallback;
import com.example.test.api.LessonManager;
import com.example.test.model.Course;

import java.util.ArrayList;
import java.util.List;

public class StudyFragment extends Fragment {
    private RecyclerView recyclerView;
    private CourseAdapter adapter;
    private List<Course> courseList;
    private LessonManager lessonManager;

    public StudyFragment() {
        // Required empty public constructor
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

        courseList = new ArrayList<>();
        adapter = new CourseAdapter(getContext(), courseList);
        recyclerView.setAdapter(adapter);

        lessonManager = new LessonManager();
        fetchCourses();
    }
    private void fetchCourses() {
        lessonManager.fetchAllCourseIds(new ApiCallback<List<Integer>>() {
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
