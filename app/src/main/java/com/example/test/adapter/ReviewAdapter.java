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
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.SharedPreferencesManager;
import com.example.test.api.ApiCallback;
import com.example.test.api.ReviewManager;
import com.example.test.api.UserManager;
import com.example.test.model.Review;
import com.example.test.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private Context context;
    private List<Review> reviews;
    private UserManager userManager;
    private ReviewManager reviewManager; // Giả định lớp quản lý API cho Review
    private int currentUserId;
    private Map<Integer, String> userNameCache = new HashMap<>(); // Cache tên người dùng
    private Map<Integer, ReviewManager.LikeStatus> likeStatusCache = new HashMap<>(); // Cache trạng thái Like

    public ReviewAdapter(Context context, List<Review> reviews) {
        this.context = context;
        this.reviews = reviews;
        this.userManager = new UserManager(context);
        this.reviewManager = new ReviewManager(context);
        this.currentUserId = SharedPreferencesManager.getInstance(context).getUser().getId();
        if (currentUserId == -1) {
            Log.e("ReviewAdapter", "User chưa đăng nhập, userId không hợp lệ");
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false); // Giả định layout item_review
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.txtContent.setText(review.getContent());
        holder.txtUser.setText("Đang tải...");

        // Cache tên người dùng
        int userId = review.getUserID();
        if (userNameCache.containsKey(userId)) {
            holder.txtUser.setText(userNameCache.get(userId));
        } else {
            userManager.fetchUserById(userId, new ApiCallback<User>() {
                @Override
                public void onSuccess(User user) {
                    userNameCache.put(userId, user.getName());
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
        }

        // Cache trạng thái Like
        if (currentUserId != -1) {
            int reviewId = review.getId();
            if (likeStatusCache.containsKey(reviewId)) {
                ReviewManager.LikeStatus likeStatus = likeStatusCache.get(reviewId);
                review.setNumLike(likeStatus.getNumLike());
                review.setLiked(likeStatus.isLiked());
                holder.txtLikeCount.setText(String.valueOf(review.getNumLike()));
                holder.btnLike.setSelected(review.isLiked());
            } else {
                reviewManager.fetchLikeStatus(currentUserId, reviewId, new ApiCallback<ReviewManager.LikeStatus>() {
                    @Override
                    public void onSuccess(ReviewManager.LikeStatus likeStatus) {
                        likeStatusCache.put(reviewId, likeStatus);
                        new Handler(Looper.getMainLooper()).post(() -> {
                            review.setNumLike(likeStatus.getNumLike());
                            review.setLiked(likeStatus.isLiked());
                            holder.txtLikeCount.setText(String.valueOf(review.getNumLike()));
                            holder.btnLike.setSelected(review.isLiked());
                        });
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Log.e("ReviewAdapter", "Lỗi lấy trạng thái Like: " + errorMessage);
                    }
                });
            }
        }

        // Xử lý sự kiện Like
        holder.btnLike.setOnClickListener(v -> {
            if (currentUserId == -1) {
                Log.e("ReviewAdapter", "User chưa đăng nhập, không thể Like");
                Toast.makeText(context, "Vui lòng đăng nhập để thích đánh giá!", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean newLikedState = !holder.btnLike.isSelected();
            holder.btnLike.setEnabled(false); // Vô hiệu hóa nút trong lúc chờ API

            reviewManager.updateLike(review.getId(), newLikedState, new ApiCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        review.setLiked(newLikedState);
                        review.setNumLike(review.getNumLike() + (newLikedState ? 1 : -1));
                        holder.btnLike.setSelected(newLikedState);
                        holder.txtLikeCount.setText(String.valueOf(review.getNumLike()));
                        holder.btnLike.setEnabled(true);
                        likeStatusCache.put(review.getId(), new ReviewManager.LikeStatus(review.getNumLike(), newLikedState));
                    });
                    Log.d("ReviewAdapter", "Cập nhật Like thành công");
                }

                @Override
                public void onFailure(String errorMessage) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        holder.btnLike.setEnabled(true);
                        Toast.makeText(context, "Không thể cập nhật Like: " + errorMessage, Toast.LENGTH_SHORT).show();
                    });
                    Log.e("ReviewAdapter", "Lỗi khi cập nhật Like: " + errorMessage);
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    // Phương thức thêm Review mới
    public void addReview(Review review) {
        reviews.add(0, review); // Thêm vào đầu danh sách
        notifyItemInserted(0); // Thông báo adapter thêm item mới
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