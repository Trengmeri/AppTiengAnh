package com.example.test.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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
import com.example.test.model.Course;
import com.example.test.model.Discussion;

import java.util.List;

public class DiscussionActivity extends AppCompatActivity {

    private int currentPage = 1; // Bắt đầu từ trang 1
    private boolean isLoading = false; // Để tránh tải dữ liệu nhiều lần
    private boolean hasMoreData = true; // Để biết còn dữ liệu để tải không

    private int lessonID;
    private DiscussionManager discussionManager= new DiscussionManager(this);;
    private DiscussionAdapter discussionAdapter ;

    RecyclerView rv_discussions;
    EditText editDiscussion;
    ImageView btSendDisussion;
    TextView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dicussion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        khaiBao();

        btSendDisussion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendDiscussion();
            }
        });


        lessonID = getIntent().getIntExtra("lessonId",1);
        fetchDiscussions();


        back.setOnClickListener(v -> {
            finish();
        });
        
    }
    private void khaiBao(){
        btSendDisussion = findViewById(R.id.btSendDiscussion);
        editDiscussion = findViewById(R.id.editDiscussion);
        back = findViewById(R.id.back);
        rv_discussions = findViewById(R.id.rv_discussions);
    }
    private void fetchDiscussions() {
        discussionManager.fetchDiscussionsByLesson(lessonID, new ApiCallback<List<Discussion>>() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onSuccess(List<Discussion> discussions) {
                runOnUiThread(() -> {
                    if (discussions == null || discussions.isEmpty()) {
                        Toast.makeText(DiscussionActivity.this, "Không có thảo luận nào.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Log để kiểm tra dữ liệu
                    for (Discussion discussion : discussions) {
                        Log.d("DiscussionActivity", "Discussion: " + discussion.getContent());
                    }

                    // Gán LayoutManager nếu chưa có
                    if (rv_discussions.getLayoutManager() == null) {
                        rv_discussions.setLayoutManager(new LinearLayoutManager(DiscussionActivity.this));
                    }

                    // Gán Adapter
                    discussionAdapter = new DiscussionAdapter(DiscussionActivity.this, discussions);
                    rv_discussions.setAdapter(discussionAdapter);

                    Toast.makeText(DiscussionActivity.this, "Tải dữ liệu thành công!", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> Toast.makeText(DiscussionActivity.this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show());
            }
        });
    }


    private void sendDiscussion() {
        String id = SharedPreferencesManager.getInstance(this).getID();

        if (id == null || id.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy user ID. Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId;
        try {
            userId = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            Log.e("DiscussionActivity", "Lỗi chuyển đổi user ID: " + e.getMessage(), e);
            Toast.makeText(this, "Lỗi lấy ID người dùng!", Toast.LENGTH_SHORT).show();
            return;
        }

        String discussionText = editDiscussion.getText().toString().trim();
        if (discussionText.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập nội dung!", Toast.LENGTH_SHORT).show();
            return;
        }

        discussionManager.createDiscussion(userId, lessonID, discussionText, null, new ApiCallback<Discussion>() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onSuccess(Discussion result) {
                runOnUiThread(() -> {
                    Toast.makeText(DiscussionActivity.this, "Bình luận đã gửi!", Toast.LENGTH_SHORT).show();
                    editDiscussion.setText("");

                    if (discussionAdapter != null) {
                        discussionAdapter.addDiscussion(result);
                        discussionAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("DiscussionActivity", "discussionAdapter is null");
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    Log.e("DiscussionActivity", "Lỗi gửi bình luận: " + errorMessage);
                    Toast.makeText(DiscussionActivity.this, "Gửi bình luận thất bại. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }





}