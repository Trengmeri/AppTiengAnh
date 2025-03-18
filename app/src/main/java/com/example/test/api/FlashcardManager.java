package com.example.test.api;

import android.content.SharedPreferences;
import android.util.Log;

import com.example.test.response.ApiResponseFlashcard;
import com.example.test.response.ApiResponseFlashcardGroup;
import com.example.test.response.ApiResponseOneFlashcard;
import com.example.test.response.FlashcardGroupResponse;
import com.example.test.model.WordData;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import org.json.JSONException;
import org.json.JSONObject;

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
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseJson = new JSONObject(responseBody);
                        JSONObject data = responseJson.getJSONObject("data");
                        String groupID = data.optString("id");
                        callback.onSuccess(groupID);
                    } catch (JSONException e) {
                        callback.onFailure("Lỗi phân tích phản hồi JSON: " + e.getMessage());
                    }
                } else {
                    callback.onFailure("Thất bại: " + response.message());
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
        String url = BASE_URL + "/api/v1/dictionary/" + word;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.d("API Response", responseData);

                    // Phân tích JSON
                    JsonObject jsonObject = new Gson().fromJson(responseData, JsonObject.class);
                    JsonArray dataArray = jsonObject.getAsJsonArray("data");

                    if (dataArray != null && dataArray.size() > 0) {
                        WordData wordData = new Gson().fromJson(dataArray.get(0), WordData.class);
                        callback.onSuccess(wordData);
                    } else {
                        callback.onFailure("No data found");
                    }
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

    // Phương thức để dịch nghĩa
    public void translateDefinition(String definition, AddFlashCardApiCallback<String> callback) throws UnsupportedEncodingException {
        try {
        String sanitizedDefinition = definition.replace(";", " ");
        String encodedText = URLEncoder.encode(sanitizedDefinition, StandardCharsets.UTF_8.toString());
        String url = BASE_URL + "/api/v1/dictionary/translate/vi/" + encodedText;

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            String responseBody = response.body().string();
                            Log.d("API_RESPONSE", "Response JSON: " + responseBody);
                            String vietnameseMeaning = parseJson(responseBody)
                                    .replaceAll("\\+\\+", ",") // Thay "++" bằng ","
                                    .replaceAll("\\+\\s*", " ") // Thay "+" còn lại bằng khoảng trắng
                                    .trim();
                            callback.onSuccess(vietnameseMeaning);
                        } else {
                            callback.onFailure("Error: " + response.code() + " - " + response.message());
                        }
                    } catch (IOException e) {
                        callback.onFailure("Response parsing error: " + e.getMessage());
                    } finally {
                        response.close(); // Đóng Response để tránh rò rỉ bộ nhớ
                    }
                }
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onFailure("Network error: " + e.getMessage());
                    e.printStackTrace(); // In lỗi ra console để debug
                }
            });
        } catch (Exception e) {
            callback.onFailure("Encoding error: " + e.getMessage());
        }
    }
    public void createFlashcard(String word, List<Integer> definitionIndices, int partOfSpeechIndex, int userId, AddFlashCardApiCallback<String> callback) {
        String url = BASE_URL + "/api/v1/flashcards";
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        // Chuyển đổi danh sách definitionIndices thành chuỗi JSON
        Gson gson = new Gson();
        String definitionIndicesJson = gson.toJson(definitionIndices);

        // Tạo JSON body
        String jsonBody = "{"
                + "\"word\":\"" + word + "\","
                + "\"definitionIndices\":" + definitionIndicesJson + ","
                + "\"partOfSpeechIndex\":" + partOfSpeechIndex + ","
                + "\"userId\":\"" + userId + "\""
                + "}";

        RequestBody body = RequestBody.create(jsonBody, JSON);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d("DEBUG", "Response Code: " + response.code());
                Log.d("DEBUG", "Response Body: " + responseBody);

                if (response.isSuccessful()) {
                    try {
                        JSONObject responseJson = new JSONObject(responseBody);
                        JSONObject data = responseJson.getJSONObject("data");
                        String flashcardID = data.optString("id");
                        callback.onSuccess(flashcardID);
                    } catch (JSONException e) {
                        callback.onFailure("Lỗi phân tích phản hồi JSON: " + e.getMessage());
                    }
                } else {
                    callback.onFailure("Thất bại: " + response.message());
                }
            }


            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("Network error: " + e.getMessage());
            }
        });
    }
    public void addFlashcardToGroup(int flashcardId, int groupId, AddFlashCardApiCallback<String> callback) {
        // URL của API với flashcardId và groupId
        String url = BASE_URL + "/api/v1/flashcard-groups/" + groupId + "/group/" + flashcardId;

        // Yêu cầu POST với body rỗng vì API có thể chỉ cần ID trong URL
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create("", MediaType.parse("application/json; charset=utf-8")))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("FlashcardManager", "Kết nối thất bại: " + e.getMessage());
                callback.onFailure("Không thể kết nối đến API.");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d("FlashcardManager", "Phản hồi từ server: " + responseBody);
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseJson = new JSONObject(responseBody);
                        String message = responseJson.optString("message", "Thêm flashcard vào group thành công!");
                        callback.onSuccess(message);
                    } catch (JSONException e) {
                        callback.onFailure("Lỗi xử lý JSON: " + e.getMessage());
                    }
                } else {
                    callback.onFailure("Thêm flashcard vào group thất bại! " + response.message());
                }
            }
        });
    }


    // Phương thức phân tích JSON
    private String parseJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.getString("data");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public interface TranslateCallback {
        void onSuccess(String vietnameseMeaning);

        void onFailure(String errorMessage);
    }
}