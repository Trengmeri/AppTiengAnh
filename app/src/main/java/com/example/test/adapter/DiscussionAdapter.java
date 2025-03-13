package com.example.test.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.SharedPreferencesManager;
import com.example.test.api.ApiCallback;
import com.example.test.api.DiscussionManager;
import com.example.test.api.UserManager;
import com.example.test.model.Discussion;
import com.example.test.model.User;
import com.example.test.ui.DiscussionActivity;

import java.util.List;

public class DiscussionAdapter extends RecyclerView.Adapter<DiscussionAdapter.ViewHolder> {
    private List<Discussion> discussions;
    private Context context;
    private ReplyAdapter replyAdapter;

    private UserManager userManager;
    private DiscussionManager discussionManager;
    private OnReplyClickListener replyClickListener;


    public DiscussionAdapter(Context context, List<Discussion> discussions,  OnReplyClickListener replyClickListener) {
        this.context = context;
        this.discussions = discussions;
        this.userManager = new UserManager(context);
        this.discussionManager= new DiscussionManager(context);
        this.replyClickListener = replyClickListener; // Lưu lại listener
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_discussion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Discussion discussion = discussions.get(position);

        int userId = discussion.getUserID();
        userManager.fetchUserById(userId, new ApiCallback<User>() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onSuccess(User user) {
                new Handler(Looper.getMainLooper()).post(() -> holder.txtUser.setText(user.getName()));
            }

            @Override
            public void onFailure(String errorMessage) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    holder.txtUser.setText("Không tải được tên");
                    Toast.makeText(context, "Lỗi tải tên người dùng: " + errorMessage, Toast.LENGTH_SHORT).show();
                });
            }
        });
        holder.txtContent.setText(discussion.getContent());

        // Kiểm tra trạng thái like
        discussionManager.isDiscussionLiked(userId, discussion.getId(), new ApiCallback<Boolean>() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onSuccess(Boolean isLiked) {
                discussion.setLiked(isLiked);
                new Handler(Looper.getMainLooper()).post(() -> {
                    holder.btnLike.setSelected(isLiked);
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("DiscussionAdapter", "Lỗi kiểm tra like: " + errorMessage);
            }
        });
        holder.txtLikeCount.setText(String.valueOf(discussion.getNumLike()));


        // Xử lý sự kiện bấm nút Like
        holder.btnLike.setOnClickListener(v -> {
            boolean isLiked = discussion.isLiked();
            int newLikeCount = isLiked ? discussion.getNumLike() - 1 : discussion.getNumLike() + 1;

            // Cập nhật UI ngay lập tức
            discussion.setNumLike(newLikeCount);
            discussion.setLiked(!isLiked);
            holder.txtLikeCount.setText(String.valueOf(newLikeCount));
            holder.btnLike.setSelected(!isLiked);

            // Gửi API like/unlike
            if (!isLiked) {
                discussionManager.likeDiscussion(userId, discussion.getId(), new ApiCallback<Void>() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onSuccess(Void response) {
                        Log.d("DiscussionAdapter", "Like thành công");
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        rollbackLike(holder, discussion, isLiked);
                    }
                });
            } else {
                discussionManager.unlikeDiscussion(userId, discussion.getId(), new ApiCallback<Void>() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onSuccess(Void response) {
                        Log.d("DiscussionAdapter", "Unlike thành công");
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        rollbackLike(holder, discussion, isLiked);
                    }
                });
            }
        });


        // Xu ly khi bam reply
        holder.txtReply.setOnClickListener(v -> {
            if (replyClickListener != null) {
                replyClickListener.onReplyClicked(discussion.getId());
            }
        });




        // Hiển thị danh sách reply
        if (holder.recyclerReply.getLayoutManager() == null) {
            holder.recyclerReply.setLayoutManager(new LinearLayoutManager(context));
        }
        replyAdapter = new ReplyAdapter(context, discussion.getReplies());
        holder.recyclerReply.setAdapter(replyAdapter);
        replyAdapter.notifyDataSetChanged();





    }

    @Override
    public int getItemCount() {
        return discussions.size();
    }
    public void addDiscussion(Discussion discussion) {
        if (discussions != null) {
            discussions.add(0, discussion); // Thêm vào đầu danh sách (hoặc `.add(discussion)` để thêm cuối)
            notifyItemInserted(0); // Cập nhật RecyclerView
        } else {
            Log.e("DiscussionAdapter", "discussionList is null");
        }
    }

    public void addMoreDiscussions(List<Discussion> newDiscussions) {
        if (newDiscussions != null && !newDiscussions.isEmpty()) {
            int startPosition = discussions.size();
            discussions.addAll(newDiscussions);
            notifyItemRangeInserted(startPosition, newDiscussions.size());
        }
    }


    private void rollbackLike(ViewHolder holder, Discussion discussion, boolean previousState) {
        discussion.setNumLike(previousState ? discussion.getNumLike() + 1 : discussion.getNumLike() - 1);
        discussion.setLiked(previousState);

        // Đảm bảo chạy trên UI Thread
        new Handler(Looper.getMainLooper()).post(() -> {
            holder.txtLikeCount.setText(String.valueOf(discussion.getNumLike()));
            holder.btnLike.setSelected(previousState);

            // Chạy Toast trên UI Thread
            if (context != null) {
                Toast.makeText(context, "Lỗi khi like/unlike", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface OnReplyClickListener {
        void onReplyClicked(int discussionId);
    }
    public List<Discussion> getDiscussions() {
        return discussions;
    }





    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtUser, txtContent, txtLikeCount, txtReply;
        RecyclerView recyclerReply;
        ImageView btnLike;

        public ViewHolder(View itemView) {
            super(itemView);
            btnLike = itemView.findViewById(R.id.btnLike);
            txtUser = itemView.findViewById(R.id.txtUser);
            txtContent = itemView.findViewById(R.id.txtContent);
            txtLikeCount = itemView.findViewById(R.id.txtLikeCount);
//            txtCreatedAt = itemView.findViewById(R.id.txtCreatedAt);
            txtReply = itemView.findViewById(R.id.txtReply);
            recyclerReply = itemView.findViewById(R.id.recyclerReply);
        }
    }
}

