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


    private String getValidToken() {
        String token = SharedPreferencesManager.getInstance(context).getAccessToken();
        if (token == null || token.isEmpty()) {
            return null;
        }
        return token;
    }

    public void fetchUserById(int userId, ApiCallback<User> callback) {
        String token = getValidToken();
        if (token == null) {
            callback.onFailure("Token không hợp lệ. Vui lòng đăng nhập lại.");
            return;
        }
        Request request = new Request.Builder()
                .url(BASE_URL + "/api/v1/users/" + userId)
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Content-Type", "application/json")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("UserManager", "Response code: " + response.code() + ", Body: " + responseBody);

                    try {
                        // Parse trực tiếp JSON thành User
                        Gson gson = new Gson();
                        User user = gson.fromJson(responseBody, User.class);

                        if (user != null && user.getName() != null) { // Kiểm tra name không null
                            new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(user));
                        } else {
                            new Handler(Looper.getMainLooper()).post(() -> callback.onFailure("Dữ liệu user không hợp lệ"));
                        }
                    } catch (Exception e) {
                        new Handler(Looper.getMainLooper()).post(() -> callback.onFailure("Lỗi parse JSON: " + e.getMessage()));
                    }
                } else {
                    String errorBody = response.body().string();
                    Log.e("UserManager", "Lỗi từ server: " + response.code() + " - " + errorBody);
                    new Handler(Looper.getMainLooper()).post(() -> callback.onFailure("Lỗi từ server: " + response.code() + " - " + errorBody));
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("UserManager", "Lỗi kết nối: " + e.getMessage());
                new Handler(Looper.getMainLooper()).post(() -> callback.onFailure("Lỗi kết nối: " + e.getMessage()));
            }
        });
    }



}
