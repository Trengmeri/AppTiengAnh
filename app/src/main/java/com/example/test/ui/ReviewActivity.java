package com.example.test.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.test.R;
import com.example.test.SharedPreferencesManager;
import com.example.test.adapter.ReviewAdapter;
import com.example.test.api.ApiCallback;
import com.example.test.api.ReviewManager;
import com.example.test.model.Course;
import com.example.test.model.Review;

import java.util.List;

public class ReviewActivity extends AppCompatActivity {
    ImageView btnSendReview;
    EditText edtReview;
    TextView back;
    private ReviewManager reviewManager = new ReviewManager(this);
    private ReviewAdapter reviewAdapter;
    Course curCourse;
    RatingBar ratingBar;
    private int courseID;
    private int currentPage = 1; // Bắt đầu từ trang 1
    private boolean isLoading = false; // Để tránh tải dữ liệu nhiều lần
    private boolean hasMoreData = true; // Để biết còn dữ liệu để tải không

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.review_activity);
        btnSendReview = findViewById(R.id.btnSendReview);
        back = findViewById(R.id.back);
        ratingBar= findViewById(R.id.ratingBar);
        loadReviews();
        Intent intent = getIntent();
        courseID = intent.getIntExtra("courseId", 0);


        // Sự kiện gửi Review
        btnSendReview.setOnClickListener(v -> {
            Log.d("ReviewActivity", "Course ID: "+ courseID);
            sendReview();
        });
        back.setOnClickListener(v -> {
           finish();
        });

    }

    private void loadReviews() {
        reviewManager.fetchReviewsByCourse(courseID, currentPage, new ApiCallback<List<Review>>() {
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

    private void sendReview() {
        EditText edtReview = findViewById(R.id.editReview); // Sửa từ edtReviewSubject thành edtReview

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


        if (reviewManager == null) {
            Log.e("CourseActivity", "reviewManager chưa được khởi tạo.");
            Toast.makeText(this, "Lỗi hệ thống, thử lại sau!", Toast.LENGTH_SHORT).show();
            return;
        }
        int numStar = ratingBar.getNumStars();

        reviewManager.createReview(userId, courseID, reContent, reSubject, numStar, status, new ApiCallback<Review>() {
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
}
