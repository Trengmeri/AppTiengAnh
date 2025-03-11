package com.example.test.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.example.test.adapter.DiscussionAdapter;
import com.example.test.api.ApiCallback;
import com.example.test.api.DiscussionManager;
import com.example.test.model.Course;
import com.example.test.model.Discussion;

import java.util.List;

public class DiscussionActivity extends AppCompatActivity {

    private int lessonID;
    private DiscussionManager discussionManager= new DiscussionManager(this);;
    private DiscussionAdapter discussionAdapter ;
    private RecyclerView rv_discussions;
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
        back = findViewById(R.id.back);
        rv_discussions = findViewById(R.id.rv_discussions);


        lessonID = getIntent().getIntExtra("lessonId",1);
        fetchDiscussions();

        back.setOnClickListener(v -> {
            finish();
        });
        
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





}