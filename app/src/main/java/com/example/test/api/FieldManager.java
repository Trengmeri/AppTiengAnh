package com.example.test.api;

import android.content.Context;
import android.util.Log;

import com.example.test.SharedPreferencesManager;
import com.example.test.model.EnglishLevel;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.example.test.model.Field;

public class FieldManager extends BaseApiManager {
    private final Context context;

    public FieldManager(Context context) {
        this.context = context;
    }

    public void fetchFields(ApiCallback<List<Field>> callback) {
        String token = SharedPreferencesManager.getInstance(context).getAccessToken();
        String url = BASE_URL + "/api/v1/courses/special-fields";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("Connection error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    Gson gson = new Gson();
                    JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
                    JsonArray dataArray = jsonObject.getAsJsonArray("data");

                    List<Field> fields = new ArrayList<>();
                    for (int i = 0; i < dataArray.size(); i++) {
                        String fieldName = dataArray.get(i).getAsString();
                        fields.add(new Field(fieldName));
                    }
                    callback.onSuccess(fields);
                } else {
                    callback.onFailure("Request failed: " + response.code());
                }
            }
        });
    }
    public void updateUserField(String field, ApiCallback callback) {
        String userId = SharedPreferencesManager.getInstance(context).getID();
        String token = SharedPreferencesManager.getInstance(context).getAccessToken();
        String url = BASE_URL + "/api/v1/users/" + userId;
        String urlUp = BASE_URL + "/api/v1/users";
        Log.d("FieldManager", "UserID: " + userId);
        Log.d("FieldManager", "Token: " + token);
        Log.d("FieldManager", "Field to update: " + field);

        Request getRequest = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(getRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("FieldManager", "Get user failed", e);
                callback.onFailure("Lỗi kết nối: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonResponse = response.body().string();
                Log.d("FieldManager", "Get user response: " + jsonResponse);

                if (response.isSuccessful()) {
                    try {
                        Gson gson = new Gson();
                        JsonObject responseObj = gson.fromJson(jsonResponse, JsonObject.class);
                        Log.d("FieldManager", "Parsed response: " + responseObj);

                        JsonObject requestBody = new JsonObject();
                        requestBody.addProperty("id", Integer.parseInt(userId));
                        // Lấy name từ response trực tiếp
                        if (responseObj.has("name")) {
                            String name = responseObj.get("name").getAsString();
                            requestBody.addProperty("name", name);
                        }
                        requestBody.addProperty("speciField", field);

                        Log.d("FieldManager", "Update request body: " + requestBody);
                        updateUserData(urlUp, token, requestBody, callback); // Sử dụng URL với userId

                    } catch (Exception e) {
                        Log.e("FieldManager", "Parse error", e);
                        callback.onFailure("Lỗi xử lý dữ liệu: " + e.getMessage());
                    }
                } else {
                    Log.e("FieldManager", "Get user failed: " + response.code());
                    callback.onFailure("Lỗi lấy thông tin user: " + response.code());
                }
            }
        });
    }

    private void updateUserData(String url, String token, JsonObject requestBody, ApiCallback callback) {
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), requestBody.toString());

        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        // Log update request
        Log.d("FieldManager", "Update request URL: " + url);
        Log.d("FieldManager", "Update request body: " + requestBody);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("FieldManager", "Update failed", e);
                callback.onFailure("Lỗi kết nối: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d("FieldManager", "Update response code: " + response.code());
                Log.d("FieldManager", "Update response body: " + responseBody);

                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onFailure("Lỗi cập nhật field: " + response.code());
                }
            }
        });
    }

}