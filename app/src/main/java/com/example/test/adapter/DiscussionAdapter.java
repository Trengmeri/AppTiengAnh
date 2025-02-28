package com.example.test.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.SharedPreferencesManager;
import com.example.test.api.ApiCallback;
import com.example.test.api.DiscussionManager;
import com.example.test.api.UserManager;
import com.example.test.model.Discussion;
import com.example.test.model.User;

import java.util.List;

public class DiscussionAdapter extends RecyclerView.Adapter<DiscussionAdapter.ViewHolder> {
    private Context context;
    private List<Discussion> discussions;
    private UserManager userManager;
    private DiscussionManager discussionManager;
    private int currentUserId;

    public DiscussionAdapter(Context context, List<Discussion> discussions) {
        this.context = context;
        this.discussions = discussions;
        this.userManager = new UserManager(context);
        this.discussionManager = new DiscussionManager(context);
        this.currentUserId = SharedPreferencesManager.getInstance(context).getUser().getId();
        if (currentUserId == -1) {
            Log.e("DiscussionAdapter", "User chưa đăng nhập, userId không hợp lệ");
        }
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


        holder.txtContent.setText(discussion.getContent());
        holder.txtUser.setText("Đang tải..."); // Trạng thái chờ

        // Gán userId vào holder để đảm bảo dữ liệu đúng
        holder.itemView.setTag(discussion.getUserID());
        Log.d("DEBUG", "UserID: " + discussion.getUserID());

        userManager.fetchUserById(discussion.getUserID(), new ApiCallback<User>() {
            @Override
            public void onSuccess() {

            }
            @Override
            public void onSuccess(User user) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    holder.txtUser.setText(user.getName());
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                new Handler(Looper.getMainLooper()).post(() -> {
                        holder.txtUser.setText("Không tải được tên");
                });
            }
        });

        if (currentUserId != -1) {
            discussionManager.fetchLikeStatus(currentUserId, discussion.getId(), new ApiCallback<DiscussionManager.LikeStatus>() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onSuccess(DiscussionManager.LikeStatus likeStatus) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        discussion.setNumLike(likeStatus.getNumLike());
                        discussion.setLiked(likeStatus.isLiked());
                        holder.txtLikeCount.setText(String.valueOf(discussion.getNumLike()));
                        holder.btnLike.setSelected(discussion.isLiked());
                    });
                }

                @Override
                public void onFailure(String errorMessage) {
                    Log.e("DiscussionAdapter", "Lỗi lấy trạng thái Like: " + errorMessage);
                }
            });
        } else {
            Log.w("DiscussionAdapter", "User chưa đăng nhập, không lấy trạng thái Like");
        }
        holder.btnLike.setOnClickListener(v -> {
            if (currentUserId == -1) {
                Log.e("DiscussionAdapter", "User chưa đăng nhập, không thể Like");
                return; // Ngăn không cho nhấn Like nếu chưa đăng nhập
            }
            boolean newLikedState = !holder.btnLike.isSelected(); // Đổi trạng thái
            holder.btnLike.setSelected(newLikedState);

            // Cập nhật số lượng Like cục bộ
            int newLikeCount = discussion.getNumLike() + (newLikedState ? 1 : -1);
            discussion.setNumLike(newLikeCount);
            discussion.setLiked(newLikedState);
            holder.txtLikeCount.setText(String.valueOf(newLikeCount));

            discussionManager.updateLike(discussion.getId(), newLikedState, new ApiCallback<Void>() {
                @Override
                public void onSuccess() {}

                @Override
                public void onSuccess(Void result) {
                    Log.d("DiscussionAdapter", "Cập nhật Like thành công");
                }

                @Override
                public void onFailure(String errorMessage) {
                    Log.e("DiscussionAdapter", "Lỗi khi cập nhật Like: " + errorMessage);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        discussion.setLiked(!newLikedState);
                        discussion.setNumLike(discussion.getNumLike() + (newLikedState ? -1 : 1));
                        holder.btnLike.setSelected(!newLikedState);
                        holder.txtLikeCount.setText(String.valueOf(discussion.getNumLike()));
                    });
                }
            });
        });

    }

    @Override
    public int getItemCount() {
        return discussions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtUser, txtContent, txtLikeCount;
        ImageView btnLike;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUser = itemView.findViewById(R.id.txtUser);
            txtContent = itemView.findViewById(R.id.txtContent);
            txtLikeCount = itemView.findViewById(R.id.txtLikeCount);
            btnLike = itemView.findViewById(R.id.btnLike);
        }
    }
}
