package com.example.test.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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

public class DiscussionActivity extends AppCompatActivity implements DiscussionAdapter.OnReplyClickListener  {

    private int currentPage = 1; // Bắt đầu từ trang 1
    private boolean isLoading = false; // Để tránh tải dữ liệu nhiều lần
    private boolean hasMoreData = true; // Để biết còn dữ liệu để tải không

    private int lessonID;
    private DiscussionManager discussionManager= new DiscussionManager(this);;
    private DiscussionAdapter discussionAdapter ;
    private int currentParentId = -1;

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

        int parentDiscussionId = getIntent().getIntExtra("discussionId", -1);

//        if (parentDiscussionId != -1) {
//            editDiscussion.requestFocus(); // Focus vào EditText
//            editDiscussion.setHint("Trả lời bình luận...");
//            showKeyboard(); // Mở bàn phím
//        }
        rv_discussions.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && layoutManager.findLastVisibleItemPosition() == discussionAdapter.getItemCount() - 1) {
                    fetchDiscussions(); // Gọi API tải trang tiếp theo
                }
            }
        });


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
        if (isLoading || !hasMoreData) return; // Nếu đang tải hoặc hết dữ liệu thì không tải nữa
        isLoading = true;

        discussionManager.fetchDiscussionsByLesson(lessonID, currentPage, new ApiCallback<List<Discussion>>() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onSuccess(List<Discussion> discussions) {
                runOnUiThread(() -> {
                    if (discussions == null || discussions.isEmpty()) {
                        hasMoreData = false;
                        return;
                    }

                    if (discussionAdapter == null) {
                        discussionAdapter = new DiscussionAdapter(DiscussionActivity.this, discussions, DiscussionActivity.this);

                        rv_discussions.setLayoutManager(new LinearLayoutManager(DiscussionActivity.this));
                        rv_discussions.setAdapter(discussionAdapter);
                    } else {
                        discussionAdapter.addMoreDiscussions(discussions);
                    }

                    currentPage++;
                    isLoading = false;
                });
            }


            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    isLoading = false;
                    Toast.makeText(DiscussionActivity.this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    public void focusOnReply(int discussionId, String userName) {
        currentParentId = discussionId; // Lưu discussionId làm parentId cho bình luận
        editDiscussion.requestFocus(); // Focus vào EditText
        editDiscussion.setHint("Write a reply for "+ userName);
        showKeyboard(); // Mở bàn phím
    }



//    private void sendDiscussion() {
//        String id = SharedPreferencesManager.getInstance(this).getID();
//
//        if (id == null || id.isEmpty()) {
//            Toast.makeText(this, "Không tìm thấy user ID. Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        int userId;
//        try {
//            userId = Integer.parseInt(id);
//        } catch (NumberFormatException e) {
//            Log.e("DiscussionActivity", "Lỗi chuyển đổi user ID: " + e.getMessage(), e);
//            Toast.makeText(this, "Lỗi lấy ID người dùng!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        String discussionText = editDiscussion.getText().toString().trim();
//        if (discussionText.isEmpty()) {
//            Toast.makeText(this, "Vui lòng nhập nội dung!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        discussionManager.createDiscussion(userId, lessonID, discussionText, null, new ApiCallback<Discussion>() {
//            @Override
//            public void onSuccess() {
//
//            }
//
//            @Override
//            public void onSuccess(Discussion result) {
//                runOnUiThread(() -> {
//                    Toast.makeText(DiscussionActivity.this, "Bình luận đã gửi!", Toast.LENGTH_SHORT).show();
//                    editDiscussion.setText("");
//
//                    if (discussionAdapter != null) {
//                        discussionAdapter.addDiscussion(result);
//                        discussionAdapter.notifyDataSetChanged();
//                    } else {
//                        Log.e("DiscussionActivity", "discussionAdapter is null");
//                    }
//                });
//            }
//
//            @Override
//            public void onFailure(String errorMessage) {
//                runOnUiThread(() -> {
//                    Log.e("DiscussionActivity", "Lỗi gửi bình luận: " + errorMessage);
//                    Toast.makeText(DiscussionActivity.this, "Gửi bình luận thất bại. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
//                });
//            }
//        });
//    }


    private void sendDiscussion() {
        String id = SharedPreferencesManager.getInstance(this).getID();
        if (id == null || id.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = Integer.parseInt(id);
        String discussionText = editDiscussion.getText().toString().trim();
        if (discussionText.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập nội dung!", Toast.LENGTH_SHORT).show();
            return;
        }

        discussionManager.createDiscussion(userId, lessonID, discussionText,
                currentParentId == -1 ? null : currentParentId, new ApiCallback<Discussion>() {

                    @Override
                    public void onSuccess() {}

                    @Override
                    public void onSuccess(Discussion result) {
                        runOnUiThread(() -> {
                            Toast.makeText(DiscussionActivity.this, "Bình luận đã gửi!", Toast.LENGTH_SHORT).show();
                            editDiscussion.setText("");


                            boolean isReplyAdded = false;

                            // Duyệt danh sách discussion để tìm bài viết cha
                            for (Discussion discussion : discussionAdapter.getDiscussions()) {
                                if (discussion.getReplies() != null) {
                                    for (Discussion reply : discussion.getReplies()) {
                                        if (reply.getId() == currentParentId) {
                                            reply.getReplies().add(result); // Thêm reply vào danh sách con
                                            discussionAdapter.notifyDataSetChanged();
                                            isReplyAdded = true;
                                            break;
                                        }
                                    }
                                }
                                if (discussion.getId() == currentParentId) {
                                    discussion.getReplies().add(result); // Thêm reply vào danh sách replies của bài viết cha
                                    discussionAdapter.notifyDataSetChanged();
                                    isReplyAdded = true;
                                    break;
                                }
                            }

                            // Nếu không tìm thấy bài viết cha, thêm vào danh sách chính
                            if (!isReplyAdded) {
                                discussionAdapter.addDiscussion(result);
                                discussionAdapter.notifyDataSetChanged();
                            }
                            currentParentId = -1;// Reset parentId sau khi gửi bình luận
                            editDiscussion.setHint("Write a discussion");

                        });
                    }



                    @Override
                    public void onFailure(String errorMessage) {
                        runOnUiThread(() -> {
                            Toast.makeText(DiscussionActivity.this, "Gửi bình luận thất bại!", Toast.LENGTH_SHORT).show();
                        });
                    }
                });
    }

    private void showKeyboard() {
        editDiscussion.post(() -> {
            editDiscussion.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(editDiscussion, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }


    @Override
    public void onReplyClicked(int discussionId) {
        String userName = SharedPreferencesManager.getInstance(this).getUser().getName();
        focusOnReply(discussionId,userName);
    }
}