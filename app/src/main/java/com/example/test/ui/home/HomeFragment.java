package com.example.test.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.test.R;
import com.example.test.api.ApiCallback;
import com.example.test.api.LessonManager;
import com.example.test.api.QuestionManager;
import com.example.test.api.ResultManager;
import com.example.test.model.Course;
import com.example.test.model.Lesson;
import com.example.test.ui.NotificationActivity;
import com.example.test.NevigateQuestion;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    Button continueButton;
    LinearLayout lessonsContainer; // LinearLayout để chứa các bài học
    TextView courseTitle,lessonTitle1,lessonNumber,courseId, tv404; // TextView để hiển thị tên khóa học
    ImageView btnNoti,btnstudy,btnexplore,btnprofile, icHome, icExplore,btnmins, btnplus;
    ViewPager2 vpgMain;
    GridLayout bottomBar;
    QuestionManager quesManager = new QuestionManager(getContext());
    LessonManager lesManager = new LessonManager();
    ResultManager resultManager = new ResultManager(getContext());
    int newCourseId=1;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        continueButton = view.findViewById(R.id.btn_continue);
        lessonsContainer = view.findViewById(R.id.lessonsContainer); // ID của LinearLayout chứa bài học
        courseTitle = view.findViewById(R.id.courseTitle); // ID của TextView hiển thị tên khóa học
        lessonTitle1 = view.findViewById(R.id.lessonTitle);
        lessonNumber = view.findViewById(R.id.lessonNumber);
        btnNoti= view.findViewById(R.id.img_notification);
        btnstudy = view.findViewById(R.id.ic_study);
        btnexplore = view.findViewById(R.id.ic_explore);
        btnprofile = view.findViewById(R.id.ic_profile);
        btnplus = view.findViewById(R.id.plus);
        btnmins = view.findViewById(R.id.mins);
        courseId = view.findViewById(R.id.courseId);
        tv404 = view.findViewById(R.id.tv404);

        fetchCourseData(newCourseId);

        btnplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentCourseId = Integer.parseInt(courseId.getText().toString());
                newCourseId = currentCourseId + 1;
                courseId.setText(String.valueOf(newCourseId));
                fetchCourseData(newCourseId);
            }
        });

        btnmins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentCourseId = Integer.parseInt(courseId.getText().toString());
                if (currentCourseId > 1) {
                    newCourseId = currentCourseId - 1;
                    courseId.setText(String.valueOf(newCourseId));
                    fetchCourseData(newCourseId);
                }
            }
        });

        continueButton.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Continue studying clicked!", Toast.LENGTH_SHORT).show();
        });

        btnNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NotificationActivity.class);
                startActivity(intent);
            }
        });
    }

    private void fetchCourseData(int id){
        lessonsContainer.removeAllViews();
        tv404.setVisibility(View.GONE);
        lesManager.fetchCourseById( newCourseId , new ApiCallback<Course>() {
            @Override
            public void onSuccess(Course course) {
                getActivity().runOnUiThread(() -> {
                    if (course != null) {
                        // Hiển thị tên khóa học
                        courseTitle.setText(course.getName());
                        lessonTitle1.setText(course.getName());
                        lessonNumber.setText("Lesson " + course.getId());

                        // Hiển thị danh sách bài học
                        List<Integer> lessonIds = course.getLessonIds();
                        if (lessonIds != null) {
                            for (Integer lessonId : lessonIds) {
                                // Gọi API để lấy thông tin bài học
                                lesManager.fetchLessonById(lessonId, new ApiCallback<Lesson>() {
                                    @Override
                                    public void onSuccess(Lesson lesson) {
                                        getActivity().runOnUiThread(() -> {
                                            if (lesson != null) {
                                                // Hiển thị thông tin bài học trong LinearLayout
                                                View lessonView = getLayoutInflater().inflate(R.layout.item_lesson, null);
                                                TextView lessonTitle = lessonView.findViewById(R.id.lessonTitle);
                                                lessonTitle.setText(lesson.getName());
                                                lessonsContainer.addView(lessonView);

                                                lessonTitle.setOnClickListener(v -> {
                                                    int lessonId = lesson.getId();
                                                    lesManager.fetchLessonById(lessonId, new ApiCallback<Lesson>() {
                                                        @Override
                                                        public void onSuccess() {
                                                        }

                                                        @Override
                                                        public void onFailure(String errorMessage) {
                                                            Log.e(getActivity().toString(), errorMessage);
                                                        }

                                                        @Override
                                                        public void onSuccess(Lesson lesson) {
                                                            if (lesson != null) {
                                                                Intent intent = new Intent(getActivity(), NevigateQuestion.class);
                                                                intent.putExtra("skill", lesson.getSkillType());
                                                                intent.putExtra("courseId", newCourseId);
                                                                intent.putExtra("lessonId", lesson.getId());
                                                                intent.putExtra("questionIds", new ArrayList<>(lesson.getQuestionIds())); // Truyền danh sách câu hỏi
                                                                startActivity(intent);
                                                            }
                                                        }
                                                    });
                                                });
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(String errorMessage) {
                                        getActivity().runOnUiThread(() -> {
                                            Log.e(getActivity().toString(), errorMessage);
                                        });
                                    }

                                    @Override
                                    public void onSuccess() {
                                    }
                                });
                            }
                        }
                    } else {
                        tv404.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                getActivity().runOnUiThread(() -> {
                    tv404.setVisibility(View.VISIBLE);
                });
            }

            @Override
            public void onSuccess() {}
        });
    }
}
