package com.example.test.api;

import android.content.Context;
import android.util.Log;

import com.example.test.SharedPreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserManager extends BaseApiManager {
    private final Context context;

    public UserManager(Context context) {
        this.context = context;
    }
    public void uploadAvatar(int userId, File imageFile, ApiCallback<String> callback) {
        String token = SharedPreferencesManager.getInstance(context).getAccessToken();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", imageFile.getName(),
                        RequestBody.create(imageFile, MediaType.parse("image/*")))
                .build();
        Request request = new Request.Builder()
                .url(BASE_URL + "/api/v1/users/" + userId + "/avatar")
                .addHeader("Authorization", "Bearer " + token)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("Upload failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string(); // Read response body once
                Log.d("UserManager", "Upload response: " + responseBody);

                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);
                        String avatarUrl = jsonObject.getString("avatar");
                        callback.onSuccess(avatarUrl);
                    } catch (JSONException e) {
                        callback.onFailure("Error parsing response: " + e.getMessage());
                    }
                } else {
                    callback.onFailure("Upload failed: " + response.code());
                }
                response.close();
            }
        });
    }
    public void fetchUserProfile(int userId, ApiCallback<JSONObject> callback) {
        String token = SharedPreferencesManager.getInstance(context).getAccessToken();

        Request request = new Request.Builder()
                .url(BASE_URL + "/api/v1/users/" + userId)
                .addHeader("Authorization", "Bearer " + token)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("UserManager", "Fetch failed: " + e.getMessage());
                callback.onFailure("Connection error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("UserManager", "Fetch successful: " + responseBody);
                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);
                        callback.onSuccess(jsonObject);
                    } catch (JSONException e) {
                        callback.onFailure("Error parsing response: " + e.getMessage());
                    }
                } else {
                    String error = "Server error: " + response.code();
                    Log.e("UserManager", error);
                    callback.onFailure(error);
                }
                response.close();
            }
        });
    }
    public void updateProfile(int userId, String name, String phone, String speciField,String englishlevel, ApiCallback callback) {
        String token = SharedPreferencesManager.getInstance(context).getAccessToken();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", userId);
            jsonObject.put("name", name);
            jsonObject.put("phone", phone);
            jsonObject.put("speciField", speciField);
            jsonObject.put("englishlevel", englishlevel);
        } catch (JSONException e) {

            callback.onFailure("Error creating JSON: " + e.getMessage());
            return;
        }

        RequestBody body = RequestBody.create(
                MediaType.get("application/json; charset=utf-8"),
                jsonObject.toString()
        );

        Request request = new Request.Builder()
                .url(BASE_URL + "/api/v1/users")
                .addHeader("Authorization", "Bearer " + token)
                .put(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("UserManager", "Update failed: " + e.getMessage());
                callback.onFailure("Connection error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("UserManager", "Update successful: " + responseBody);
                    callback.onSuccess();
                } else {
                    String error = "Server error: " + response.code();
                    Log.e("UserManager", error);
                    callback.onFailure(error);
                }
                response.close();
            }
        });
    }
}