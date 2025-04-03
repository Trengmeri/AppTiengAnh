package com.example.test.ui.study;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.test.R;
import com.example.test.adapter.CourseAdapter;
import com.example.test.api.ApiCallback;
import com.example.test.api.EnrollmentManager;
import com.example.test.api.ResultManager;
import com.example.test.model.Course;
import com.example.test.model.Enrollment;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CourseListFragment extends Fragment {
    private RecyclerView recyclerView;
    private CourseAdapter adapter;
    private List<Course> courseList;
    private FrameLayout join;
    private ResultManager resultManager;

    public static CourseListFragment newInstance(List<Course> courses) {
        CourseListFragment fragment = new CourseListFragment();
        Bundle args = new Bundle();
        args.putSerializable("courses", (Serializable) courses);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_course_list, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        resultManager = new ResultManager(context);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);
        join = view.findViewById(R.id.join);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() != null) {
            courseList = (List<Course>) getArguments().getSerializable("courses");
            adapter = new CourseAdapter("None",getContext(), courseList);
            recyclerView.setAdapter(adapter);
            int minCourseId = Collections.min(courseList, Comparator.comparingInt(Course::getId)).getId();
            resultManager.getEnrollment(minCourseId, new ApiCallback<Enrollment>() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onSuccess(Enrollment result) {
                }


                @Override
                public void onFailure(String errorMessage) {
                    getActivity().runOnUiThread(() -> {
                        if (errorMessage.contains("404")) { // Chỉ hiển thị join nếu lỗi 404
                            join.setVisibility(View.VISIBLE);
                            join.setOnClickListener(v -> {
                                resultManager.createEnrollment(minCourseId, new ApiCallback() {
                                    @Override
                                    public void onSuccess() {
                                        getActivity().runOnUiThread(() -> {
                                            join.setVisibility(View.GONE); // Ẩn nút Join
                                            // Hiển thị nền tối
                                            View darkOverlay = view.findViewById(R.id.darkOverlay);
                                            darkOverlay.setVisibility(View.VISIBLE);

                                            // Hiển thị GIF và thông báo
                                            ImageView imgSuccessGif = view.findViewById(R.id.imgSuccessGif);
                                            TextView tvSuccessMessage = view.findViewById(R.id.tvSuccessMessage);

                                            imgSuccessGif.setVisibility(View.VISIBLE);
                                            tvSuccessMessage.setVisibility(View.VISIBLE);

                                            // Load GIF bằng Glide
                                            Glide.with(CourseListFragment.this)
                                                    .asGif()
                                                    .load(R.raw.like)
                                                    .into(imgSuccessGif);

                                            // Tự động chuyển đến Study sau vài giây
                                            new Handler().postDelayed(() -> {
                                                ViewPager2 viewPager = requireActivity().findViewById(R.id.vpg_main);
                                                viewPager.setCurrentItem(1, true);
                                            }, 3000);
                                        });
                                    }

                                    @Override
                                    public void onSuccess(Object result) {

                                    }

                                    @Override
                                    public void onFailure(String errorMessage) {
                                        Log.e("EnrollmentError", "Lỗi khi đăng ký khóa học: " + errorMessage);
                                    }
                                });
                            });
                        } else {
                            Log.e("API_ERROR", "Lỗi khác: " + errorMessage);
                        }
                    });
                }

            });
        }
    }
}
