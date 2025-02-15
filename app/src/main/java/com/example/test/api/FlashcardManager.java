package com.example.test.api;

import android.util.Log;

import com.example.test.model.Flashcard;
import com.example.test.response.ApiResponseFlashcard;
import com.example.test.response.ApiResponseFlashcardGroup;
import com.example.test.response.FlashcardGroupResponse;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.List;

public class FlashcardManager extends BaseApiManager {
    private Gson gson;

    public FlashcardManager() {
        gson = new Gson();
    }

    public void fetchFlashcardGroups(int userId, int page, FlashcardApiCallback callback) {
        String url = BASE_URL + "/api/v1/flashcard-groups/user/" + userId + "?page=" + page + "&size=6";
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    FlashcardGroupResponse apiResponse = gson.fromJson(responseBody, FlashcardGroupResponse.class);
                    callback.onSuccess(apiResponse);
                } else {
                    callback.onFailure("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("Network error: " + e.getMessage());
            }
        });
    }

    public void createFlashcardGroup(String groupName, int userId, FlashcardApiCallback callback) {
        String url = BASE_URL + "/api/v1/flashcard-groups";
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        // Tạo JSON body
        String jsonBody = "{\"name\":\"" + groupName + "\", \"userId\":" + userId + "}";
        RequestBody body = RequestBody.create(jsonBody, JSON);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    ApiResponseFlashcardGroup apiResponse = gson.fromJson(responseBody,
                            ApiResponseFlashcardGroup.class);
                    callback.onSuccess(apiResponse);
                } else {
                    callback.onFailure("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("Network error: " + e.getMessage());
            }
        });
    }

    public void fetchFlashcardsInGroup(int groupId, FlashcardApiCallback callback) {
        String url = BASE_URL + "/api/v1/flashcard-groups/" + groupId;
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    ApiResponseFlashcard apiResponse = gson.fromJson(responseBody, ApiResponseFlashcard.class);
                    callback.onSuccess(apiResponse);
                } else {
                    callback.onFailure("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("Network error: " + e.getMessage());
            }
        });
    }
}