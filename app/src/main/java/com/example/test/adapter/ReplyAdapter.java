package com.example.test.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.api.ApiCallback;
import com.example.test.api.UserManager;
import com.example.test.model.Discussion;
import com.example.test.model.User;

import java.util.List;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ViewHolder> {
    private List<Discussion> replies;
    private Context context;
    private UserManager userManager;

    public ReplyAdapter(Context context, List<Discussion> replies) {
        this.context = context;
        this.replies = replies;
        this.userManager= new UserManager(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reply, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Discussion reply = replies.get(position);

        int userId = reply.getUserID();
        Log.d("ReplyActivity", "UserID: "+ userId);
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
        holder.txtContent.setText(reply.getContent());
    }

    @Override
    public int getItemCount() {
        return replies.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtUser, txtContent, txtCreatedAt;

        public ViewHolder(View itemView) {
            super(itemView);
            txtUser = itemView.findViewById(R.id.txtUser);
            txtContent = itemView.findViewById(R.id.txtContent);
//            txtCreatedAt = itemView.findViewById(R.id.txtCreatedAt);
        }
    }
}
