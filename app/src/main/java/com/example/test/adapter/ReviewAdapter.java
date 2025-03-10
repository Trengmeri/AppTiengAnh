package com.example.test.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
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
    private ReviewManager reviewManager;
    private int currentUserId;
    private Map<Integer, String> userNameCache = new HashMap<>();


    public ReviewAdapter(Context context, List<Review> reviews) {
        this.context = context;
        this.reviews = reviews;
        this.userManager = new UserManager(context);
        this.reviewManager = new ReviewManager(context);
        this.currentUserId = Integer.parseInt(SharedPreferencesManager.getInstance(context).getID());
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

        // Hiển thị nội dung đánh giá
        holder.txtReContent.setText(review.getReContent());
//        holder.txtReSubject.setText(review.getReSubject());
        holder.txtUser.setText("Đang tải...");
        holder.txtLikeCount.setText(String.valueOf(review.getNumLike()));
        holder.btnLike.setSelected(review.isLiked());
        holder.txtNumStar.setText(String.valueOf(review.getNumStar()));
        holder.ratingBar.setRating(review.getNumStar());

        // Cache tên người dùng
        int userId = review.getUserId();
        if (userNameCache.containsKey(userId)) {
            holder.txtUser.setText(userNameCache.get(userId));
        } else {
            userManager.fetchUserById(userId, new ApiCallback<User>() {
                @Override
                public void onSuccess() {

                }

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

        // Kiểm tra trạng thái like ban đầu
        reviewManager.isReviewLiked(currentUserId, review.getId(), new ApiCallback<Boolean>() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onSuccess(Boolean isLiked) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    review.setLiked(isLiked);
                    holder.btnLike.setSelected(isLiked);
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("ReviewAdapter", "Lỗi kiểm tra like: " + errorMessage);
            }
        });

        // Xử lý sự kiện Like


        holder.btnLike.setOnClickListener(v -> {
            if (currentUserId == -1) {
                Toast.makeText(context, "Vui lòng đăng nhập để thích đánh giá!", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isCurrentlyLiked = review.isLiked();
            holder.btnLike.setEnabled(false);

            if (isCurrentlyLiked) {
                reviewManager.unlikeReview(currentUserId, review.getId(), new ApiCallback<Boolean>() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        new Handler(Looper.getMainLooper()).post(() -> {
                            review.setLiked(false);
                            review.setNumLike(review.getNumLike() - 1);
                            holder.btnLike.setSelected(false);
                            holder.txtLikeCount.setText(String.valueOf(review.getNumLike()));
                            holder.btnLike.setEnabled(true);
                        });
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Log.e("ReviewAdapter", "Lỗi bỏ like: " + errorMessage);
                        holder.btnLike.setEnabled(true);
                    }
                });
            } else {
                reviewManager.likeReview(currentUserId, review.getId(), new ApiCallback<Boolean>() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        new Handler(Looper.getMainLooper()).post(() -> {
                            review.setLiked(true);
                            review.setNumLike(review.getNumLike() + 1);
                            holder.btnLike.setSelected(true);
                            holder.txtLikeCount.setText(String.valueOf(review.getNumLike()));
                            holder.btnLike.setEnabled(true);
                        });
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Log.e("ReviewAdapter", "Lỗi like: " + errorMessage);
                        holder.btnLike.setEnabled(true);
                    }
                });
            }
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
        TextView txtUser, txtLikeCount , txtReContent, txtNumStar;
        ImageView btnLike;
        RatingBar ratingBar;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUser = itemView.findViewById(R.id.txtUser);
            txtReContent= itemView.findViewById(R.id.txtReContent);
//            txtReSubject = itemView.findViewById(R.id.txtReSubject);
            txtLikeCount = itemView.findViewById(R.id.txtLikeCount);
            btnLike = itemView.findViewById(R.id.btnLike);
            ratingBar = itemView.findViewById(R.id.ratingBarReview);
            txtNumStar = itemView.findViewById(R.id.txtNumStar);
        }
    }
}