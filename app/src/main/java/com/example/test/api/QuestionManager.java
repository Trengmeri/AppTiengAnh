package com.example.test.api;

import android.util.Log;

import com.example.test.model.MediaFile;
import com.example.test.model.Question;
import com.example.test.model.Result;
import com.example.test.response.ApiResponseMedia;
import com.example.test.response.ApiResponseQuestion;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class QuestionManager extends BaseApiManager {

    public void fetchQuestionContentFromApi(int questionId, ApiCallback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "/api/v1/questions/" + questionId)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body()!= null? response.body().string(): null;
                    if (responseBody!= null &&!responseBody.isEmpty()) {
                        Log.d("QuestionManager", "JSON trả về: " + responseBody);
                        try {
                            Gson gson = new Gson();
                            ApiResponseQuestion apiResponse = gson.fromJson(responseBody, ApiResponseQuestion.class);
                            Question question = apiResponse.getData();
                            if (question!= null && question.getQuestionChoices()!= null) {
                                callback.onSuccess(question);
                            } else {
                                Log.e("QuestionManager", "Câu hỏi hoặc câu trả lời không hợp lệ.");
                                callback.onFailure("Dữ liệu không hợp lệ từ server.");
                            }
                        } catch (JsonSyntaxException e) {
                            Log.e("QuestionManager", "Lỗi khi parse JSON: " + e.getMessage());
                            callback.onFailure("Lỗi khi parse JSON.");
                        }
                    } else {
                        Log.e("QuestionManager", "Body trả về rỗng hoặc không hợp lệ.");
                        callback.onFailure("Dữ liệu không hợp lệ từ server.");
                    }
                } else {
                    Log.e("QuestionManager", "Lỗi từ server: Mã lỗi " + response.code());
                    callback.onFailure("Lỗi từ server: Mã lỗi " + response.code());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("QuestionManager", "Lỗi kết nối: " + e.getMessage());
                callback.onFailure("Không thể kết nối tới API.");
            }
        });
    }

    public void saveUserAnswer(int questionId, String answerContent, ApiCallback callback) {
        String[] answerParts = answerContent.split(", ");
        List<String> answerList = new ArrayList<>(Arrays.asList(answerParts));
        Gson gson = new Gson();
        String answerContentJson = gson.toJson(answerList);

        String json = "{ \"questionId\":" + questionId + ", \"answerContent\":" + answerContentJson + "}";
        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(BASE_URL + "/api/v1/answers/user/1")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("Không thể lưu câu trả lời: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onFailure("Lỗi: " + response.code());
                }
            }
        });
    }

    public static void gradeAnswer(int answerId, Callback callback) {
        String url = BASE_URL + "/api/v1/answers/grade/" + answerId;

        Request request = new Request.Builder()
                .url(url)
                .put(RequestBody.create("", MediaType.parse(""))) // Nếu không có body, có thể để trống
                .build();

        client.newCall(request).enqueue(callback);
    }

    // Trong class QuestionManager
    public void fetchMediaByQuesId(int questionId, ApiCallback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "/api/v1/material/questions/" + questionId)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body()!= null? response.body().string(): null;
                    if (responseBody!= null &&!responseBody.isEmpty()) {
                        Log.d("QuestionManager", "JSON trả về: " + responseBody);
                        try {
                            Gson gson = new Gson();
                            ApiResponseMedia apiResponse = gson.fromJson(responseBody, ApiResponseMedia.class);

                            // Lấy danh sách MediaFile từ apiResponse.getData()
                            List<MediaFile> mediaFiles = apiResponse.getData();
                            String jsonResults = gson.toJson(mediaFiles);
                            Log.d("ResultManager", "Dữ liệu JSON: " + jsonResults);

                            if (!mediaFiles.isEmpty()) {
                                MediaFile media = mediaFiles.get(0);
                                Log.d("QuestionManager","Link media: "+media.getMaterLink());
                                callback.onSuccess(media);
                            } else {
                                Log.e("QuestionManager", "Dữ liệu không hợp lệ từ server.");
                                callback.onFailure("Dữ liệu không hợp lệ từ server.");
                            }
                        } catch (JsonSyntaxException e) {
                            Log.e("QuestionManager", "Lỗi khi parse JSON: " + e.getMessage());
                            callback.onFailure("Lỗi khi parse JSON.");
                        }
                    } else {
                        Log.e("QuestionManager", "Body trả về rỗng hoặc không hợp lệ.");
                        callback.onFailure("Dữ liệu không hợp lệ từ server.");
                    }
                } else {
                    Log.e("QuestionManager", "Lỗi từ server: Mã lỗi " + response.code());
                    callback.onFailure("Lỗi từ server: Mã lỗi " + response.code());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("QuestionManager", "Lỗi kết nối: " + e.getMessage());
                callback.onFailure("Không thể kết nối tới API.");
            }
        });
    }
}