package com.example.test.ui.study;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.adapter.CourseAdapter;
import com.example.test.api.ApiCallback;
import com.example.test.api.EnrollmentManager;
import com.example.test.api.LessonManager;
import com.example.test.api.ResultManager;
import com.example.test.model.Course;
import com.example.test.model.Enrollment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MyCourseFragment extends Fragment {
    private RecyclerView recyclerView1, recyclerView2, recyclerView3;
    private CourseAdapter adapter1, adapter2, adapter3;
    private List<Course> courseList1, courseList2, courseList3;
    private LessonManager lessonManager;
    private EnrollmentManager enrollmentManager;
    private ResultManager resultManager;
    LinearLayout contentAbout;
    private Set<Integer> processedCourseIds = new HashSet<>();

    public MyCourseFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_course, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        enrollmentManager = new EnrollmentManager(context);
        resultManager = new ResultManager(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchCourses();
    }

    @SuppressLint("WrongViewCast")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        recyclerView1 = view.findViewById(R.id.recyclerView1);
        recyclerView1.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView2 = view.findViewById(R.id.recyclerView2);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView3 = view.findViewById(R.id.recyclerView3);
        recyclerView3.setLayoutManager(new LinearLayoutManager(getContext()));

        contentAbout = view.findViewById(R.id.mycourse);


        courseList1 = new ArrayList<>();
        adapter1 = new CourseAdapter("True", getContext(), courseList1);
        recyclerView1.setAdapter(adapter1);

        courseList2 = new ArrayList<>();
        adapter2 = new CourseAdapter("False", getContext(), courseList2);
        recyclerView2.setAdapter(adapter2);

        courseList3 = new ArrayList<>();
        adapter3 = new CourseAdapter("Done", getContext(), courseList3);
        recyclerView3.setAdapter(adapter3);

        lessonManager = new LessonManager();
        fetchCourses();
    }

    private void fetchCourses() {

        enrollmentManager.fetchAllEnrolledCourseIds(new ApiCallback<List<Integer>>() {
            @Override
            public void onSuccess() {}

            @Override
            public void onSuccess(List<Integer> courseIds) {
                Log.d("MyCourseFragment", "📌 Danh sách Course ID: " + courseIds);
                fetchLessonsForCourses(courseIds);
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("MyCourseFragment", "❌ Lỗi khi lấy danh sách khóa học: " + errorMessage);
            }
        });
    }

    private void fetchLessonsForCourses(List<Integer> courseIds) {
        for (Integer courseId : courseIds) {
            if (processedCourseIds.contains(courseId)) {
                continue;
            }
            processedCourseIds.add(courseId);

            resultManager.getEnrollment(courseId, new ApiCallback<Enrollment>() {
                @Override
                public void onSuccess() {}

                @Override
                public void onSuccess(Enrollment enrollment) {
                    String prostatus = enrollment.getProStatus();
                    int totalPoint = enrollment.getTotalPoints();

                    lessonManager.fetchCourseById(courseId, new ApiCallback<Course>() {
                        @Override
                        public void onSuccess() {}

                        @Override
                        public void onSuccess(Course course) {
                            if (course == null) return;

                            if ("true".equalsIgnoreCase(prostatus)) {
                                if (totalPoint != 0) {
                                    courseList3.add(course); // Hoàn thành
                                } else {
                                    courseList1.add(course); // Đang học
                                }
                            } else {
                                courseList2.add(course); // Chưa đăng ký
                            }

                            if (getActivity() == null) return;
                            getActivity().runOnUiThread(() -> {
                                adapter1.notifyItemRangeChanged(0, courseList1.size());
                                adapter2.notifyItemRangeChanged(0, courseList2.size());
                                adapter3.notifyItemRangeChanged(0, courseList3.size());
                            });
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Log.e("MyCourseFragment", "❌ Lỗi khi lấy khóa học ID " + courseId + ": " + errorMessage);
                        }
                    });
                }

                @Override
                public void onFailure(String errorMessage) {
                    Log.e("MyCourseFragment", "❌ Lỗi khi lấy Enrollment của Course ID " + courseId + ": " + errorMessage);
                }
            });
        }
    }

}
