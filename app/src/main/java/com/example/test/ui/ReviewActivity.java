package com.example.test.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.example.test.adapter.LessonAdapter;
import com.example.test.adapter.ReviewAdapter;
import com.example.test.api.ApiCallback;
import com.example.test.api.LessonManager;
import com.example.test.api.ReviewManager;
import com.example.test.model.Course;
import com.example.test.model.Lesson;
import com.example.test.model.Review;

import java.util.ArrayList;
import java.util.List;

public class ReviewActivity extends AppCompatActivity {

    AppCompatButton btnAbout, btnLesson;
    ImageView btnSendReview;
    LinearLayout contentAbout, contentLes;
    TextView txtContentAbout, courseName, numLessons;
    Course curCourse;
    private int currentPage = 1; // Bắt đầu từ trang 1
    private boolean isLoading = false; // Để tránh tải dữ liệu nhiều lần
    private boolean hasMoreData = true; // Để biết còn dữ liệu để tải không

    private RecyclerView recyclerView, recyclerViewLesson;
    private ReviewAdapter reviewAdapter;
    private LessonAdapter lessonAdapter;
    private ReviewManager reviewManager = new ReviewManager(this);
    private LessonManager lessonManager = new LessonManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_course);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ views
        btnAbout = findViewById(R.id.btnAbout);
        numLessons = findViewById(R.id.numLessons);
        btnLesson = findViewById(R.id.btnLesson);
        contentAbout = findViewById(R.id.contentAbout);
        recyclerViewLesson = findViewById(R.id.recyclerViewLessons);
        courseName = findViewById(R.id.courseName);
        txtContentAbout = findViewById(R.id.txtContentAbout);
        recyclerView = findViewById(R.id.recyclerViewDiscussion);
        btnSendReview = findViewById(R.id.btSendReview);

        contentAbout.setVisibility(View.VISIBLE);
        recyclerViewLesson.setVisibility(View.GONE);

        // Kiểm tra null cho các view quan trọng
        if (courseName == null || txtContentAbout == null || recyclerView == null || btnSendReview == null) {
            Log.e("CourseActivity", "One or more views are null. Check activity_course.xml");
            return;
        }

        // Lấy thông tin khóa học
        getCourseInfo(1, new ApiCallback<Course>() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onSuccess(Course course) {
                runOnUiThread(() -> {
                    curCourse = course;
                    courseName.setText(course.getName());
                    txtContentAbout.setText(course.getIntro());
                    numLessons.setText(course.getLessonIds().size() + " lessons ");
                    Log.d("CourseInfo", "Name: " + course.getName() + ", Intro: " + course.getIntro());

                    // Gọi hàm lấy danh sách bài học
                    loadLessons(course.getLessonIds());

                    loadReviews(); // Tải reviews sau khi có curCourse
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> Log.e("CourseInfo", "Lỗi: " + errorMessage));
            }
        });


        // Thiết lập RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewAdapter = new ReviewAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(reviewAdapter);

        lessonAdapter = new LessonAdapter(this);
        recyclerViewLesson.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewLesson.setAdapter(lessonAdapter);


        // Sự kiện nút About và Lesson
        btnAbout.setOnClickListener(v -> {
            btnAbout.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_about));
            btnLesson.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_lesson));
            contentAbout.setVisibility(View.VISIBLE);
            recyclerViewLesson.setVisibility(View.GONE);
        });

        btnLesson.setOnClickListener(v -> {
            btnLesson.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_about));
            btnAbout.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_lesson));
            contentAbout.setVisibility(View.GONE);
            recyclerViewLesson.setVisibility(View.VISIBLE);
        });

        // Sự kiện gửi Review
        btnSendReview.setOnClickListener(v -> {
            sendReview();
        });

    }

    public void getCourseInfo(int courseId, ApiCallback<Course> callback) {
        lessonManager.fetchCourseById(courseId, new ApiCallback<Course>() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onSuccess(Course course) {
                runOnUiThread(() -> callback.onSuccess(course));

            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> callback.onFailure(errorMessage));
            }
        });
    }

    private void loadLessons(List<Integer> lessonIds) {
        List<Lesson> lessons = new ArrayList<>();

        if (lessonIds == null || lessonIds.isEmpty()) {
            Log.w("CourseActivity", "Không có bài học nào trong khóa học.");
            return;
        }

        for (int lessonId : lessonIds) {
            lessonManager.fetchLessonById(lessonId, new ApiCallback<Lesson>() {
                @Override
                public void onSuccess() {}

                @Override
                public void onSuccess(Lesson lesson) {
                    runOnUiThread(() -> {
                        lessons.add(lesson);

                        // Khi đã tải xong tất cả bài học, cập nhật adapter
                        if (lessons.size() == lessonIds.size()) {
                            lessonAdapter.setLessons(lessons);
                        }
                    });
                }

                @Override
                public void onFailure(String errorMessage) {
                    Log.e("LessonLoad", "Lỗi tải bài học: " + errorMessage);
                }
            });
        }
    }

    private void sendReview() {
        EditText edtReview = findViewById(R.id.edtReview); // Sửa từ edtReviewSubject thành edtReview

        if (edtReview == null) {
            Log.e("CourseActivity", "edtReview is null. Check R.id.edtReview in activity_course.xml");
            Toast.makeText(this, "Không tìm thấy trường nhập nội dung!", Toast.LENGTH_SHORT).show();
            return;
        }


        String reContent = edtReview.getText().toString().trim();
        String reSubject = curCourse != null ? curCourse.getName() : "Khóa học mặc định";
        if (reContent.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập nội dung đánh giá!", Toast.LENGTH_SHORT).show();
            return;
        }
        String id = SharedPreferencesManager.getInstance(this).getID();

        if (id == null || id.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy user ID. Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }
        int userId;
        try {
            userId = Integer.parseInt(id);
        }catch (NumberFormatException e){
            Log.e("CourseActivity", "Lỗi chuyển đổi user ID: " + e.getMessage());
            Toast.makeText(this, "Lỗi lấy ID người dùng!", Toast.LENGTH_SHORT).show();
            return;
        }
        String status = "CONTRIBUTING";
        int courseId = curCourse != null ? curCourse.getId() : 1;


        if (reviewManager == null) {
            Log.e("CourseActivity", "reviewManager chưa được khởi tạo.");
            Toast.makeText(this, "Lỗi hệ thống, thử lại sau!", Toast.LENGTH_SHORT).show();
            return;
        }

        reviewManager.createReview(userId, courseId, reContent, reSubject, 5, status, new ApiCallback<Review>() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onSuccess(Review newReview) {
                runOnUiThread(() -> {
                    Toast.makeText(ReviewActivity.this, "Đánh giá đã gửi!", Toast.LENGTH_SHORT).show();
                    edtReview.setText("");
                    if(reviewAdapter != null){
                        reviewAdapter.addReview(newReview);
                    }else {
                        Log.e("CourseActivity", "reviewAdapter is null");
                    }

                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    Log.w("ReviewError", "Lỗi gửi Review: " + errorMessage);
                    if (errorMessage.contains("409") || errorMessage.contains("User has already reviewed this course")) {
                        Toast.makeText(ReviewActivity.this, "Bạn đã đánh giá khóa học này!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ReviewActivity.this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }



    private void loadReviews() {
        int courseId = curCourse != null ? curCourse.getId() : 1;
        reviewManager.fetchReviewsByCourse(courseId, currentPage, new ApiCallback<List<Review>>() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onSuccess(List<Review> reviews) {
                runOnUiThread(() -> {
                    if (reviews == null || reviews.isEmpty()) {
                        hasMoreData = false;
                        return;
                    }
                    if ( reviewAdapter == null){
                        reviewAdapter = new ReviewAdapter(ReviewActivity.this, reviews);
                    }else {
                        reviewAdapter.addMoreReviews(reviews);
                    }

                    currentPage++;
                    isLoading = false;
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() ->
                        Toast.makeText(ReviewActivity.this, "Lỗi tải đánh giá: " + errorMessage, Toast.LENGTH_SHORT).show());
            }
        });
    }
}