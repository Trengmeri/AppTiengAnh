package com.example.test.api;

import com.example.test.response.ApiResponseFlashcardGroup;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import com.google.gson.Gson;
import java.io.IOException;

public class FlashcardManager extends BaseApiManager {
    private Gson gson;

    public FlashcardManager() {
        gson = new Gson();
    }

    public void fetchFlashcardGroups(int userId, int page, FlashcardApiCallback callback) {
        String url = BASE_URL + "/api/v1/flashcard-groups/user/" + userId + "?page=" + page + "&size=4";
        Request request = new Request.Builder()
                .url(url)
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
}