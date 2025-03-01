package com.example.test.api;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.test.SharedPreferencesManager;
import com.example.test.model.User;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class UserManager extends BaseApiManager{
    private final Context context;

    public UserManager(Context context) {
        this.context = context;
    }


    // API lấy thông tin User theo ID
    public void fetchUserById(int userId, ApiCallback<User> callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "/api/v1/users/" + userId)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("API_RESPONSE", "User nhận được: " + responseBody);

                    try {
                        // Parse trực tiếp JSON thành User
                        Gson gson = new Gson();
                        User user = gson.fromJson(responseBody, User.class);

                        // Chuyển về luồng chính để cập nhật UI
                        new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(user));
                    } catch (Exception e) {
                        new Handler(Looper.getMainLooper()).post(() -> callback.onFailure("Lỗi parse JSON: " + e.getMessage()));
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



}
