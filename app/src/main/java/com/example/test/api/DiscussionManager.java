package com.example.test.api;

import android.content.Context;
import android.util.Log;
import android.view.WindowInsets;

import com.example.test.SharedPreferencesManager;
import com.example.test.model.Discussion;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
    public void createDiscussion(int lessonId, String content, Integer parentId, ApiCallback callback) {
        String userId = SharedPreferencesManager.getInstance(context).getID(); // Lấy userId từ session

        // Tạo JSON request body
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", Integer.parseInt(userId)); // Chuyển về số nguyên
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
                if (response.isSuccessful()) {
                    Log.d("DiscussionManager", "Tạo bình luận thành công");
                    callback.onSuccess();
                } else {
                    Log.e("DiscussionManager", "Lỗi " + response.code());
                    callback.onFailure("Lỗi: " + response.code());
                }
            }
        });
    }
    public void fetchDiscussionsByLesson(int lessonId, ApiCallback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "/api/v1/discussions/lesson/" + lessonId)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("API_RESPONSE", "Dữ liệu nhận được: " + responseBody);

                    Gson gson = new Gson();
                    Type typeList= new TypeToken<List<Discussion>>(){}.getType();
                    List<Discussion> discussions = gson.fromJson(responseBody, typeList);

                    if (discussions.size() > 0) {
                        callback.onSuccess(discussions);
                    } else {
                        callback.onFailure("Không có bình luận nào.");
                    }
                } else {
                    callback.onFailure("Lỗi từ server: " + response.code());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("Lỗi kết nối: " + e.getMessage());
            }
        });
    }


}


