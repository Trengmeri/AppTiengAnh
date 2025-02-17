package com.example.test.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.model.Discussion;

import java.util.List;

public class DiscussionAdapter extends RecyclerView.Adapter<DiscussionAdapter.ViewHolder> {
    private Context context;
    private List<Discussion> discussions;

    public DiscussionAdapter(Context context, List<Discussion> discussions) {
        this.context = context;
        this.discussions = discussions;
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
        holder.txtUser.setText(discussion.getUser().getName()); // Hiển thị tên user
        holder.txtContent.setText(discussion.getContent()); // Hiển thị nội dung discussion
    }

    @Override
    public int getItemCount() {
        return discussions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtUser, txtContent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUser = itemView.findViewById(R.id.txtUser);
            txtContent = itemView.findViewById(R.id.txtContent);
        }
    }
}
