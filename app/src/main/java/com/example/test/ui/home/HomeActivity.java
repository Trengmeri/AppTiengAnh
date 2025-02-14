package com.example.test.ui.home;

import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.test.R;
import com.example.test.ui.home.adapter.MainAdapter;
import com.example.test.api.LessonManager;
import com.example.test.api.QuestionManager;
import com.example.test.api.ResultManager;

public class HomeActivity extends AppCompatActivity {
    Button continueButton;
    LinearLayout lessonsContainer; // LinearLayout để chứa các bài học
    TextView courseTitle,lessonTitle1,lessonNumber; // TextView để hiển thị tên khóa học
    ImageView btnNoti,btnstudy,btnexplore,btnprofile, icHome, icExplore;
    ViewPager2 vpgMain;
    GridLayout bottomBar;
    QuestionManager quesManager = new QuestionManager(this);
    LessonManager lesManager = new LessonManager();
    ResultManager resultManager = new ResultManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

//        continueButton = findViewById(R.id.btn_continue);
//        lessonsContainer = findViewById(R.id.lessonsContainer); // ID của LinearLayout chứa bài học
//        courseTitle = findViewById(R.id.courseTitle); // ID của TextView hiển thị tên khóa học
//        lessonTitle1 = findViewById(R.id.lessonTitle);
//        lessonNumber = findViewById(R.id.lessonNumber);
//        btnNoti= findViewById(R.id.img_notification);
//        btnstudy = findViewById(R.id.ic_study);
//        btnexplore = findViewById(R.id.ic_explore);
//        btnprofile = findViewById(R.id.ic_profile);
        vpgMain = findViewById(R.id.vpg_main);
        bottomBar = findViewById(R.id.bottom_bar);

        // Gán Adapter cho ViewPager2
        vpgMain.setAdapter(new MainAdapter(this));
        vpgMain.setCurrentItem(0);

        icHome= bottomBar.findViewById(R.id.ic_home);
        icExplore= bottomBar.findViewById(R.id.ic_explore);

        icHome.setOnClickListener(v -> {
            vpgMain.setCurrentItem(0);
        });

        icExplore.setOnClickListener(v -> {
            vpgMain.setCurrentItem(1);
        });


//        continueButton.setOnClickListener(v -> {
//            Toast.makeText(HomeActivity.this, "Continue studying clicked!", Toast.LENGTH_SHORT).show();
//        });
//
//        btnNoti.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(HomeActivity.this, NotificationActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        btnexplore.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(HomeActivity.this, ExploreActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        // Gọi API để lấy thông tin khóa học
//        lesManager.fetchCourseById( new ApiCallback() {
//            @Override
//            public void onSuccess(Course course) {
//                runOnUiThread(() -> {
//                    if (course != null) {
//                        // Hiển thị tên khóa học
//                        courseTitle.setText(course.getName());
//                        lessonTitle1.setText(course.getName());
//                        lessonNumber.setText("Lesson " + course.getId());
//
//                        // Hiển thị danh sách bài học
//                        List<Integer> lessonIds = course.getLessonIds();
//                        for (Integer lessonId : lessonIds) {
//                            // Gọi API để lấy thông tin bài học
//                            lesManager.fetchLessonById(lessonId, new ApiCallback() {
//                                @Override
//                                public void onSuccess(Lesson lesson) {
//                                    runOnUiThread(() -> {
//                                        if (lesson != null) {
//                                            // Hiển thị thông tin bài học trong LinearLayout
//                                            View lessonView = getLayoutInflater().inflate(R.layout.item_lesson, null);
//                                            TextView lessonTitle = lessonView.findViewById(R.id.lessonTitle);
//                                            lessonTitle.setText(lesson.getName());
//                                            lessonsContainer.addView(lessonView);
//
//                                            lessonTitle.setOnClickListener(v -> {
//                                                Intent intent = new Intent(HomeActivity.this, QuestionActivity.class);
//                                                Bundle bundle = new Bundle();
//                                                bundle.putInt("lessonId", lessonId);
//                                                bundle.putInt("questionId", lesson.getQuestionIds().get(0)); // Truyền questionId đầu tiên
//                                                intent.putExtras(bundle);
//                                                startActivity(intent);
//                                            });
//                                        }
//                                    });
//                                }
//
//                                @Override
//                                public void onSuccess(Course course) {}
//
//                                @Override
//                                public void onFailure(String errorMessage) {
//                                    runOnUiThread(() -> {
//                                        Toast.makeText(HomeActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
//                                    });
//                                }
//
//                                @Override
//                                public void onSuccessWithOtpID(String otpID) {}
//
//                                @Override
//                                public void onSuccessWithToken(String token) {
//
//                                }
//
//                                @Override
//                                public void onSuccess() {}
//
//                                @Override
//                                public void onSuccess(Result result) {}
//
//                                @Override
//                                public void onSuccess(Answer answer) {}
//
//                                @Override
//                                public void onSuccess(MediaFile mediaFile) {
//
//                                }
//
//                                @Override
//                                public void onSuccess(Question question) {
//
//                                }
//                            });
//                        }
//                    } else {
//                        Toast.makeText(HomeActivity.this, "Không có khóa học nào.", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//
//            @Override
//            public void onFailure(String errorMessage) {
//                runOnUiThread(() -> {
//                    Toast.makeText(HomeActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
//                });
//            }
//
//            @Override
//            public void onSuccessWithOtpID(String otpID) {}
//
//            @Override
//            public void onSuccessWithToken(String token) {
//
//            }
//
//            @Override
//            public void onSuccess() {}
//
//            @Override
//            public void onSuccess(Result result) {}
//
//            @Override
//            public void onSuccess(Answer answer) {}
//
//            @Override
//            public void onSuccess(MediaFile mediaFile) {
//
//            }
//
//            @Override
//            public void onSuccess(Question question) {}
//
//            @Override
//            public void onSuccess(Lesson lesson) {
//
//            }
//        });
    }
}