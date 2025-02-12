package com.example.test.api;

import android.content.Context;
import android.util.Log;

import com.example.test.SharedPreferencesManager;
import com.example.test.model.Answer;
import com.example.test.model.Result;
import com.example.test.response.ApiResponseResult;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ResultManager extends BaseApiManager {
    private final Context context;

    public ResultManager(Context context) {
        this.context = context;
    }


    public void createResult(int lessonId, int sessionId, int enrollmentId, ApiCallback callback) {
        String userId = SharedPreferencesManager.getInstance(context).getID();

        String json = "{ \"lessonId\":" + lessonId + ", \"sessionId\":" + sessionId + ", \"enrollmentId\":" + enrollmentId + "}";
        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(BASE_URL + "/api/v1/lesson-results/user/" + userId)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("Không thể tạo kết quả: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("ResultManager", "createResult: Tạo Result thành công");
                    callback.onSuccess();
                } else {
                    Log.e("ResultManager", "createResult: Lỗi " + response.code());
                    callback.onFailure("Lỗi: " + response.code());
                }
            }
        });
    }

    public void fetchAnswerPointsByQuesId(int questionId, ApiCallback callback) {
        String userId = SharedPreferencesManager.getInstance(context).getID();
        Request request = new Request.Builder()
                .url(BASE_URL + "/api/v1/answers/latest?userId="+ userId +"&questionId=" + questionId)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("ResultManager", "Phản hồi từ server: " + responseBody);

                    Gson gson = new Gson();
                    Answer answer = gson.fromJson(responseBody, Answer.class); // Thay đổi ở đây

                    if (answer != null) {
                        Log.d("ResultManager", "Answer ID: " + answer.getId() + ", Điểm đạt được: " + answer.getPointAchieved() + ", Session ID: " + answer.getSessionId());
                        callback.onSuccess(answer); // Thay đổi ở đây
                    } else {
                        callback.onFailure("Không có câu trả lời nào.");
                    }
                } else {
                    Log.e("ResultManager", "Lỗi từ server: Mã lỗi " + response.code());
                    callback.onFailure("Lỗi từ server: Mã lỗi " + response.code());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("ResultManager", "Lỗi kết nối: " + e.getMessage());
                callback.onFailure("Không thể kết nối tới API.");
            }
        });
    }

    public void fetchResultByLesson(int lessonId, ApiCallback callback) {
        String userId = SharedPreferencesManager.getInstance(context).getID();

        Request request = new Request.Builder()
                .url(BASE_URL + "/api/v1/lesson-results/user/" + userId + "/lesson/" + lessonId + "/latest")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("ResultManager", "Phản hồi từ server: " + responseBody);

                    Gson gson = new Gson();
                    ApiResponseResult apiResponse = gson.fromJson(responseBody, ApiResponseResult.class);

                    if (apiResponse.getStatusCode() == 200) {
                        List<Result> results = apiResponse.getData();
                        String jsonResults = gson.toJson(results);
                        Log.d("ResultManager", "Dữ liệu JSON: " + jsonResults);

                        if (!results.isEmpty()) {
                            Result firstAnswer = results.get(0);
                            Log.d("ResultManager", "Result ID: " + firstAnswer.getId() + ", Điểm đạt được: " + firstAnswer.getTotalPoints() + ", Complete: " + firstAnswer.getComLevel());
                            callback.onSuccess(firstAnswer);
                        } else {
                            callback.onFailure("Không có câu trả lời nào.");
                        }
                    }
                } else {
                    Log.e("ResultManager", "Lỗi từ server: Mã lỗi " + response.code());
                    callback.onFailure("Lỗi từ server: Mã lỗi " + response.code());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("ResultManager", "Lỗi kết nối: " + e.getMessage());
                callback.onFailure("Không thể kết nối tới API.");
            }
        });
    }
}