package com.example.test.api;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.test.SharedPreferencesManager;
import com.example.test.model.Review; // Giả định có lớp Review
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReviewManager extends BaseApiManager {
    private final Context context;

    public ReviewManager(Context context) {
        this.context = context;
    }

    // Kiểm tra token hợp lệ
    private String getValidToken() {
        String token = SharedPreferencesManager.getInstance(context).getAccessToken();
        if (token == null || token.isEmpty()) {
            return null;
        }
        return token;
    }

    // API tạo Review (Cập nhật theo body mới)
    public void createReview(int userId, int courseId, String reContent, String reSubject, int numStar, String status, ApiCallback<Review> callback) {
        String token = getValidToken();
        if (token == null) {
            callback.onFailure("Token không hợp lệ. Vui lòng đăng nhập lại.");
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", userId);
            jsonObject.put("courseId", courseId);
            jsonObject.put("reContent", reContent);
            jsonObject.put("reSubject", reSubject);
            jsonObject.put("numStar", numStar);
            jsonObject.put("status", status);
        } catch (JSONException e) {
            callback.onFailure("Lỗi tạo JSON: " + e.getMessage());
            return;
        }

        RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(BASE_URL + "/api/v1/reviews")
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("Không thể tạo đánh giá: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("ReviewManager", "Tạo đánh giá thành công: " + responseBody);
                    Gson gson = new Gson();
                    Review newReview = gson.fromJson(responseBody, Review.class);
                    new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(newReview));
                } else {
                    String errorBody = response.body().string();
                    callback.onFailure("Lỗi: " + response.code() + " - " + errorBody);
                }
            }
        });
    }

    // API lấy danh sách Review theo courseId (Giữ nguyên)
    public void fetchReviewsByCourse(int courseId, ApiCallback<List<Review>> callback) {
        String token = getValidToken();
        if (token == null) {
            callback.onFailure("Token không hợp lệ. Vui lòng đăng nhập lại.");
            return;
        }

        Request request = new Request.Builder()
                .url(BASE_URL + "/api/v1/reviews/course/" + courseId)
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Content-Type", "application/json")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("API_RESPONSE", "Dữ liệu nhận được: " + responseBody);

                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        JSONObject dataObject = jsonResponse.getJSONObject("data");
                        JSONArray contentArray = dataObject.getJSONArray("content");

                        Gson gson = new Gson();
                        Type reviewListType = new TypeToken<List<Review>>() {}.getType();
                        List<Review> reviews = gson.fromJson(contentArray.toString(), reviewListType);
                        new Handler(Looper.getMainLooper()).post(() -> {
                            if (reviews != null && !reviews.isEmpty()) {
                                callback.onSuccess(reviews);
                            } else {
                                callback.onFailure("Không có đánh giá nào");
                            }
                        });
                    } catch (JSONException e) {
                        new Handler(Looper.getMainLooper()).post(() ->
                                callback.onFailure("Lỗi phân tích JSON: " + e.getMessage()));
                    }
                } else {
                    String errorBody = response.body().string();
                    callback.onFailure("Lỗi từ server: " + response.code() + " - " + errorBody);
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("Lỗi kết nối: " + e.getMessage());
            }
        });
    }

    // API kiểm tra trạng thái Like theo reviewId và userId (Giữ nguyên)
    public void fetchLikeStatus(int userId, int reviewId, ApiCallback<LikeStatus> callback) {
        String token = getValidToken();
        if (token == null) {
            callback.onFailure("Token không hợp lệ. Vui lòng đăng nhập lại.");
            return;
        }

        String url = BASE_URL + "/api/v1/likes/review?userId=" + userId + "&reviewId=" + reviewId;
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Content-Type", "application/json")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("API_RESPONSE", "Like status: " + responseBody);
                    Gson gson = new Gson();
                    LikeStatus likeStatus = gson.fromJson(responseBody, LikeStatus.class);
                    new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(likeStatus));
                } else {
                    String errorBody = response.body().string();
                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onFailure("Lỗi từ server: " + response.code() + " - " + errorBody));
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onFailure("Lỗi kết nối: " + e.getMessage()));
            }
        });
    }

    // API cập nhật trạng thái Like (Giữ nguyên)
    public void updateLike(int reviewId, boolean isLiked, ApiCallback<Void> callback) {
        String token = getValidToken();
        if (token == null) {
            callback.onFailure("Token không hợp lệ. Vui lòng đăng nhập lại.");
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("reviewId", reviewId);
            jsonObject.put("isLiked", isLiked);
        } catch (JSONException e) {
            callback.onFailure("Lỗi tạo JSON: " + e.getMessage());
            return;
        }

        RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(BASE_URL + "/api/v1/likes/review")
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("Lỗi kết nối: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("ReviewManager", "Cập nhật Like thành công");
                    new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(null));
                } else {
                    String errorBody = response.body().string();
                    callback.onFailure("Lỗi từ server: " + response.code() + " - " + errorBody);
                }
            }
        });
    }

    // Class để parse dữ liệu LikeStatus từ API (Giữ nguyên)
    public static class LikeStatus {
        private int numLike;
        private boolean isLiked;

        public LikeStatus() {}

        public LikeStatus(int numLike, boolean isLiked) {
            this.numLike = numLike;
            this.isLiked = isLiked;
        }

        public int getNumLike() { return numLike; }
        public boolean isLiked() { return isLiked; }
    }
}