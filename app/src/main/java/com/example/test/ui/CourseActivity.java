package com.example.test.ui;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.example.test.adapter.DiscussionAdapter;
import com.example.test.api.ApiCallback;
import com.example.test.api.DiscussionManager;
import com.example.test.model.Answer;
import com.example.test.model.Course;
import com.example.test.model.Discussion;
import com.example.test.model.Lesson;
import com.example.test.model.MediaFile;
import com.example.test.model.Question;
import com.example.test.model.Result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;

public class CourseActivity extends AppCompatActivity {

    AppCompatButton btnAbout, btnLesson;
    ImageView btSendDiscussion;
    LinearLayout contentAbout, contentLes;
//    ImageView btnLike,btnLike1;
    private RecyclerView recyclerView;
    private DiscussionAdapter  discussionAdapter;
    private DiscussionManager discussionManager;

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
        btnAbout= findViewById(R.id.btnAbout);
        btnLesson= findViewById(R.id.btnLesson);
        contentAbout = findViewById(R.id.contentAbout);
        contentLes = findViewById(R.id.contentLes);

        btnAbout.setOnClickListener(v -> {
            btnAbout.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_about));
            btnLesson.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_lesson));
            contentAbout.setVisibility(View.VISIBLE);
            contentLes.setVisibility(View.GONE);
        });

        btnLesson.setOnClickListener(v -> {
            btnLesson.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_about));
            btnAbout.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_lesson));
            contentAbout.setVisibility(View.GONE);
            contentLes.setVisibility(View.VISIBLE);
        });

        recyclerView = findViewById(R.id.recyclerViewDiscussion);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        discussionAdapter = new DiscussionAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(discussionAdapter);
        btSendDiscussion= findViewById(R.id.btSendDiscussion);
        btSendDiscussion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDiscussion();
            }
        });




//        btnLike = findViewById(R.id.btnLike);
//        btnLike.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                v.setSelected(!v.isSelected()); // Đổi trạng thái selected
//            }
//        });
//        btnLike1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                v.setSelected(!v.isSelected()); // Đổi trạng thái selected
//            }
//        });


    }
    private int getLessonId() {
        SharedPreferences sharedPreferences = getSharedPreferences("LESSON_DATA", MODE_PRIVATE);
        return sharedPreferences.getInt("LESSON_ID", -1);
    }


    private void sendDiscussion() {
        EditText edtDiscussion = findViewById(R.id.edtDiscussion);
        String content = edtDiscussion.getText().toString().trim();

        if (content.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập nội dung!", Toast.LENGTH_SHORT).show();
            return;
        }

        int lessonId = 1;
        Integer parentId = null; // Nếu là trả lời thì truyền ID của bình luận cha

        discussionManager.createDiscussion(lessonId, content, parentId, new ApiCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(CourseActivity.this, "Bình luận đã gửi!", Toast.LENGTH_SHORT).show();
                edtDiscussion.setText(""); // Xóa nội dung sau khi gửi
                loadDiscussions(); // Cập nhật danh sách discussion
//
            }

            @Override
            public void onSuccess(Object result) {

            }


            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(CourseActivity.this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
            }

        });


    }
    private void loadDiscussions() {
        int lessonId = getLessonId(); // Lấy lessonId

        discussionManager.fetchDiscussionsByLesson(lessonId, new ApiCallback<List<Discussion>>() {
            @Override
            public void onSuccess(List<Discussion> discussion) {

                if (discussion.isEmpty()) {
                    Toast.makeText(CourseActivity.this, "Không có bình luận nào!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d("DEBUG", "Số bình luận: " + discussion.size());

                // Gán dữ liệu vào adapter
                discussionAdapter = new DiscussionAdapter(CourseActivity.this, discussion);
                recyclerView.setAdapter(discussionAdapter);
            }

            @Override
            public void onFailure(String errorMessage) {

            }

            @Override
            public void onSuccess() {

            }

        });
    }


}