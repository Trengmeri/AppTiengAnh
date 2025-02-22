package com.example.test.api;

import android.util.Log;

import com.example.test.model.Flashcard;
import com.example.test.response.ApiResponseFlashcard;
import com.example.test.response.ApiResponseFlashcardGroup;
import com.example.test.response.ApiResponseOneFlashcard;
import com.example.test.response.FlashcardGroupResponse;
import com.example.test.model.WordData;
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

    public void updateFlashcardGroup(int groupId, String newName, FlashcardApiCallback callback) {
        String url = BASE_URL + "/api/v1/flashcard-groups/" + groupId + "?newName=" + newName;
        Request request = new Request.Builder()
                .url(url)
                .put(RequestBody.create("", MediaType.parse("application/json; charset=utf-8"))) // Body rỗng cho PUT
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    callback.onSuccess(new ApiResponseFlashcardGroup()); // Tạo một đối tượng thành công
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

    public void deleteFlashcardGroup(int groupId, FlashcardApiCallback callback) {
        String url = BASE_URL + "/api/v1/flashcard-groups/" + groupId;
        Request request = new Request.Builder()
                .url(url)
                .delete() // Gọi phương thức DELETE
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    callback.onSuccess(new ApiResponseFlashcardGroup()); // Tạo một đối tượng thành công
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

    public void fetchFlashcardById(int flashcardId, FlashcardApiCallback callback) {
        Log.d("FlashcardManager", "Starting API call for flashcard ID: " + flashcardId);

        String url = BASE_URL + "/api/v1/flashcards/" + flashcardId;
        Log.d("FlashcardManager", "API URL: " + url);

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("FlashcardManager", "API call failed", e);
                callback.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("FlashcardManager", "Received API response. Code: " + response.code());

                if (!response.isSuccessful()) {
                    Log.e("FlashcardManager", "API error response: " + response.code());
                    callback.onFailure("Server returned " + response.code());
                    return;
                }

                try {
                    String jsonData = response.body().string();
                    Log.d("FlashcardManager", "API response body: " + jsonData);

                    Gson gson = new Gson();
                    ApiResponseOneFlashcard apiResponse = gson.fromJson(jsonData, ApiResponseOneFlashcard.class);

                    if (apiResponse != null && apiResponse.getData() != null) {
                        Log.d("FlashcardManager", "Successfully parsed flashcard data");
                        callback.onSuccess(apiResponse);
                    } else {
                        Log.e("FlashcardManager", "API response parsing error: response or data is null");
                        callback.onFailure("Invalid response format");
                    }
                } catch (Exception e) {
                    Log.e("FlashcardManager", "Error parsing API response", e);
                    callback.onFailure("Error parsing response: " + e.getMessage());
                }
            }
        });
    }

    public void fetchWordDefinition(String word, AddFlashCardApiCallback<WordData> callback) {
        String url = BASE_URL + "/api/v1/dictionary/" + word; // URL API

        // Tạo một request
        Request request = new Request.Builder()
                .url(url)
                .build();

        // Gọi API
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.d("API Response", responseData);
                    WordData wordData = new Gson().fromJson(responseData, WordData.class);
                    callback.onSuccess(wordData);
                } else {
                    callback.onFailure("Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("Error: " + e.getMessage());
            }
        });
    }
}