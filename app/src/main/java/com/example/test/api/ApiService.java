package com.example.test.api;

import static com.example.test.api.BaseApiManager.BASE_URL;

import android.content.Context;
import android.util.Log;

import com.example.test.SharedPreferencesManager;
import com.example.test.model.EvaluationResult;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiService {
    private final Context context;
    private final OkHttpClient client = new OkHttpClient();

    public ApiService(Context context) {
        this.context = context;
    }

    public void sendAnswerToApi(String question, String userAnswer, ApiCallback<EvaluationResult> callback) {
        String token = SharedPreferencesManager.getInstance(context).getAccessToken();

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("question", question);
            jsonBody.put("userAnswer", userAnswer);
            jsonBody.put("prompt", "short evaluate");

            RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.get("application/json; charset=utf-8"));

            Request request = new Request.Builder()
                    .url(BASE_URL + "/api/v1/perplexity/evaluate")
                    .addHeader("Authorization", "Bearer " + token)
                    .post(body)
                    .build();

            new Thread(() -> {
                try {
                    Response response = client.newCall(request).execute();
                    if (!response.isSuccessful()) {
                        callback.onFailure("API error: " + response.message());
                        return;
                    }
                    String responseBody = response.body().string(); // Lưu body vào biến trước
                    Log.d("API_RESPONSE", "JSON: " + responseBody);

                    JSONObject responseObject = new JSONObject(responseBody);
                    JSONObject dataObject = responseObject.getJSONObject("data"); // Lấy object `data`

                    double point = dataObject.getDouble("score"); // ✅ Lấy giá trị `score`
                    String evaluation = dataObject.getString("evaluation");
                    String improvements = dataObject.getString("improvements");

                    EvaluationResult result = new EvaluationResult(improvements, evaluation, point);
                    callback.onSuccess(result);

                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onFailure("Request error: " + e.getMessage());
                }
            }).start();
        } catch (Exception e) {
            callback.onFailure("JSON creation error: " + e.getMessage());
        }
    }

    public void getSuggestionFromApi(String question, ApiCallback callback) {
        String token = SharedPreferencesManager.getInstance(context).getAccessToken();

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("question", question);
            jsonBody.put("prompt", "short evaluate");

            RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.get("application/json; charset=utf-8"));

            Request request = new Request.Builder()
                    .url(BASE_URL + "/api/v1/perplexity/suggest")
                    .addHeader("Authorization", "Bearer " + token)
                    .post(body)
                    .build();

            new Thread(() -> {
                try {
                    Response response = client.newCall(request).execute();
                    if (!response.isSuccessful()) {
                        callback.onFailure("API error: " + response.message());
                        return;
                    }
                    String responseBody = response.body().string(); // Lưu body vào biến trước
                    Log.d("API_RESPONSE", "JSON: " + responseBody);

                    JSONObject responseObject = new JSONObject(responseBody);
                    JSONObject dataObject = responseObject.getJSONObject("data"); // Lấy object `data`

                    callback.onSuccess();

                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onFailure("Request error: " + e.getMessage());
                }
            }).start();
        } catch (Exception e) {
            callback.onFailure("JSON creation error: " + e.getMessage());
        }
    }
}
