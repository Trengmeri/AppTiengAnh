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

    public DiscussionAdapter(Context context, List<Discussion> discussions) {
        this.context = context;
        this.discussions = discussions;
        this.userManager = new UserManager(context);
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
        holder.txtLikeCount.setText(String.valueOf(discussion.getNumLike()));


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


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtUser, txtContent, txtLikeCount, txtCreatedAt, txtReply;
        RecyclerView recyclerReply;

        public ViewHolder(View itemView) {
            super(itemView);
            txtUser = itemView.findViewById(R.id.txtUser);
            txtContent = itemView.findViewById(R.id.txtContent);
            txtLikeCount = itemView.findViewById(R.id.txtLikeCount);
//            txtCreatedAt = itemView.findViewById(R.id.txtCreatedAt);
            txtReply = itemView.findViewById(R.id.txtReply);
            recyclerReply = itemView.findViewById(R.id.recyclerReply);
        }
    }
}

