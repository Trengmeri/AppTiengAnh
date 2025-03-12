package com.example.test.api;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.WindowInsets;

import com.example.test.SharedPreferencesManager;
import com.example.test.model.Discussion;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Proxy;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DiscussionManager extends BaseApiManager {
    private final Context context;

    public DiscussionManager(Context context) {
        this.context = context;
    }
    // API tạo Discussion
    public void createDiscussion( int userID, int lessonId, String content, Integer parentId, ApiCallback callback) {
        String token = SharedPreferencesManager.getInstance(context).getAccessToken();

        // Tạo JSON request body
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", userID); // Chuyển về số nguyên
            jsonObject.put("lessonId", lessonId);
            jsonObject.put("content", content);
            if (parentId != null) {
                jsonObject.put("parentId", parentId);
            }
        } catch (JSONException e) {
            callback.onFailure("Lỗi tạo JSON: " + e.getMessage());
            return;
        }

        RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.get("application/json; charset=utf-8"));

        // Tạo request
        Request request = new Request.Builder()
                .url(BASE_URL + "/api/v1/discussions")
                .addHeader("Authorization", "Bearer " + token)  //  Thêm token vào header
                .addHeader("Content-Type", "application/json")  //  Đảm bảo Content-Type đúng
                .post(body)
                .build();

        // Gửi request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("Không thể tạo bình luận: " + e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d("DiscussionManager", "API Response: " + responseBody);

                if (response.isSuccessful()) {
                    Gson gson = new Gson();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        JSONObject dataObject = jsonResponse.getJSONObject("data");  // Lấy phần "data"
                        Discussion newDiscussion = gson.fromJson(dataObject.toString(), Discussion.class);

                        // Gọi callback trên UI thread
                        new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(newDiscussion));

                    } catch (JSONException e) {
                        callback.onFailure("Lỗi parse JSON: " + e.getMessage());
                    }
                } else {
                    callback.onFailure("Lỗi: " + response.code());
                }
            }
        });
    }

    /*



     */

    public void fetchDiscussionsByLesson(int lessonId, ApiCallback<List<Discussion>> callback) {
        String token = SharedPreferencesManager.getInstance(context).getAccessToken();

        Request request = new Request.Builder()
                .url(BASE_URL + "/api/v1/discussions/lesson/" + lessonId + "?page=1&size=4")
                .addHeader("Authorization", "Bearer " + token)  //  Truyền token đúng
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
                        Type discussionListType = new TypeToken<List<Discussion>>() {}.getType();
                        List<Discussion> discussions = gson.fromJson(contentArray.toString(), discussionListType);

                        new Handler(Looper.getMainLooper()).post(() -> {
                            if (discussions != null && !discussions.isEmpty()) {
                                callback.onSuccess(discussions);
                            } else {
                                callback.onFailure("Không có thảo luận nào");
                            }
                        });
                    } catch (JSONException e) {
                        new Handler(Looper.getMainLooper()).post(() ->
                                callback.onFailure("Lỗi phân tích JSON: " + e.getMessage()));
                    }
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

//    public void fetchDiscussionsByLesson(int lessonId, ApiCallback<List<Discussion>> callback) {
//        String token = SharedPreferencesManager.getInstance(context).getAccessToken();
//        Request request = new Request.Builder()
//                .url(BASE_URL + "/api/v1/discussions/lesson/" + lessonId)
//                .addHeader("Authorization", "Bearer " + token)  //  Truyền token đúng
//                .addHeader("Content-Type", "application/json")
//                .get()
//                .build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    String responseBody = response.body().string();
//                    Log.d("API_RESPONSE", "Dữ liệu nhận được: " + responseBody);
//
//                    try {
//                        JSONObject jsonResponse= new JSONObject(responseBody);
//                        JSONObject dataObject = jsonResponse.getJSONObject("data");
//                        JSONArray contentArray= dataObject.getJSONArray("content");
//
//                        Gson gson = new Gson();
//                        List<Discussion> discussions= gson.fromJson(contentArray.toString(), new TypeToken<List<Discussion>>(){}.getType());
//                        new  Handler(Looper.getMainLooper()).post(()->{
//                            if (discussions != null && !discussions.isEmpty()){
//                                callback.onSuccess(discussions);
//                            }else {
//                                callback.onFailure("Discussion empty");
//                            }
//                        });
//                    }catch (JSONException e){
//                        new Handler(Looper.getMainLooper()).post(() -> callback.onFailure("Error parse Json: "+ e.getMessage()));
//                    }
//                } else {
//                    callback.onFailure("Lỗi từ server: " + response.code());
//                }
//            }
//
//            @Override
//            public void onFailure(Call call, IOException e) {
//                callback.onFailure("Lỗi kết nối: " + e.getMessage());
//            }
//        });
//    }

    // API kiểm tra trạng thái Like theo discussionId và userId
    public void fetchLikeStatus(int userId, int discussionId, ApiCallback<LikeStatus> callback) {
        String token = SharedPreferencesManager.getInstance(context).getAccessToken();
        String url = BASE_URL + "/api/v1/likes/discussion?userId=" + userId + "&discussionId=" + discussionId;

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

                    try {
                        Gson gson = new Gson();
                        LikeStatus likeStatus = gson.fromJson(responseBody, LikeStatus.class);
                        new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(likeStatus));
                    } catch (Exception e) {
                        new Handler(Looper.getMainLooper()).post(() -> callback.onFailure("Error parsing Like status: " + e.getMessage()));
                    }
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onFailure("Lỗi từ server: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onFailure("Lỗi kết nối: " + e.getMessage()));
            }
        });
    }

    // Class để parse dữ liệu LikeStatus từ API
    public static class LikeStatus {
        private int numLike;
        private boolean isLiked;

        public int getNumLike() { return numLike; }
        public boolean isLiked() { return isLiked; }
    }



}


