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

import com.bumptech.glide.Glide;
import com.example.test.R;
import com.example.test.SharedPreferencesManager;
import com.example.test.api.ApiCallback;
import com.example.test.api.ReviewManager;
import com.example.test.api.UserManager;
import com.example.test.model.Discussion;
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
    private final int currentUserId;


    public ReviewAdapter(Context context, List<Review> reviews) {
        this.context = context;
        this.reviews = reviews;
        this.userManager = new UserManager(context);
        this.reviewManager = new ReviewManager(context);
        User currentUser = SharedPreferencesManager.getInstance(context).getUser();
        if (currentUser != null) {
            this.currentUserId = currentUser.getId();
        } else {
            this.currentUserId = -1;
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
        holder.txtLikeCount.setText(String.valueOf(review.getNumLike()));
        holder.btnLike.setSelected(review.isLiked());
        holder.txtNumStar.setText(String.valueOf(review.getNumStar()));
        holder.ratingBar.setRating(review.getNumStar());

        // Cache tên người dùng
        final int reviewUserId = review.getUserId();
        final ViewHolder currentHolder = holder;
        userManager.fetchUserById(reviewUserId, new ApiCallback<User>() {
            @Override
            public void onSuccess() {
                // Do nothing, xử lý sau
            }

            @Override
            public void onSuccess(User user) {
                String avatar = user.getAvt();
                String name = user.getName();
                Log.d("UserAdapter", "onSuccess: " + name);
                String uri;
                if (avatar == null) return;
                uri = avatar.replace("0.0.0.0", "14.225.198.3");

                new Handler(Looper.getMainLooper()).post(() -> {
                        currentHolder.txtUser.setText(name); // Cập nhật tên người dùng
                        Log.d("TEST", "onSuccess: Thanh Cong");
                        Glide.with(context)
                                .load(uri)
                                .placeholder(R.drawable.icon_lesson)
                                .error(R.drawable.icon_lesson)
                                .circleCrop()
                                .override(200, 200)
                                .into(currentHolder.imgAvatar);
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    currentHolder.txtUser.setText("Không tải được tên");
                    Toast.makeText(context, "Lỗi tải tên người dùng: " + errorMessage, Toast.LENGTH_SHORT).show();
                });
            }
        });

        // Kiểm tra trạng thái like ban đầu
        reviewManager.isReviewLiked(currentUserId, review.getId(), new ApiCallback<Boolean>() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onSuccess(Boolean isLiked) {
                review.setLiked(isLiked);
                new Handler(Looper.getMainLooper()).post(() -> {
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

            boolean isLiked = review.isLiked();
            int newCount = isLiked ? review.getNumLike() - 1 : review.getNumLike() + 1;

            // Cập nhật UI ngay lập tức
            review.setNumLike(newCount);
            review.setLiked(!isLiked);
            holder.txtLikeCount.setText(String.valueOf(newCount));
            holder.btnLike.setSelected(!isLiked);

            // Gửi PI like/unlike
            if (!isLiked) {
                reviewManager.likeReview(currentUserId, review.getId(), new ApiCallback<Void>() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onSuccess(Void response) {
                        Log.d("DiscussionAdapter", "Like thành công");
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        rollbackLike(holder, review, isLiked);
                    }
                });
            }
            else {
                reviewManager.unlikeReview(currentUserId, review.getId(), new ApiCallback<Void>() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onSuccess(Void response) {
                        Log.d("DiscussionAdapter", "Unlike thành công");
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        rollbackLike(holder, review, isLiked);
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

    public void addMoreReviews(List<Review> newReviews) {
        if (newReviews != null && !newReviews.isEmpty()) {
            int startPosition = reviews.size();
            reviews.addAll(newReviews);
            notifyItemRangeInserted(startPosition, newReviews.size());
        }
    }

    private void rollbackLike(ReviewAdapter.ViewHolder holder, Review review, boolean previousState) {
        review.setNumLike(previousState ? review.getNumLike() + 1 : review.getNumLike() - 1);
        review.setLiked(previousState);

        // Đảm bảo chạy trên UI Thread
        new Handler(Looper.getMainLooper()).post(() -> {
            holder.txtLikeCount.setText(String.valueOf(review.getNumLike()));
            holder.btnLike.setSelected(previousState);

            // Chạy Toast trên UI Thread
            if (context != null) {
                Toast.makeText(context, "Lỗi khi like/unlike", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtUser, txtLikeCount , txtReContent, txtNumStar;
        ImageView btnLike,imgAvatar;
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
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
        }
    }
}