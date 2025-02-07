package com.example.test.api;

import android.util.Log;

import com.example.test.model.Course;
import com.example.test.model.Lesson;
import com.example.test.response.ApiResponseCourse;
import com.example.test.response.ApiResponseLesson;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class LessonManager extends BaseApiManager {

    public void fetchLessonById(int lessonId, ApiCallback callback) {

        Request request = new Request.Builder()
                .url(BASE_URL + "/api/v1/lessons/" + lessonId)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("LessonManager", "Phản hồi từ server: " + responseBody);

                    Gson gson = new Gson();
                    ApiResponseLesson apiResponse = gson.fromJson(responseBody, ApiResponseLesson.class);

                    if (apiResponse.getStatusCode() == 200) {
                        Lesson lesson = apiResponse.getData();
                        callback.onSuccess(lesson);
                    } else {
                        callback.onFailure("Lỗi từ server: " + apiResponse.getMessage());
                    }
                } else {
                    Log.e("LessonManager", "Lỗi từ server: Mã lỗi " + response.code());
                    callback.onFailure("Lỗi từ server: Mã lỗi " + response.code());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("LessonManager", "Lỗi kết nối: " + e.getMessage());
                callback.onFailure("Không thể kết nối tới API.");
            }
        });
    }

    public void fetchCourseById(ApiCallback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "/api/v1/courses/1") // Thay bằng URL máy chủ của bạn
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("LessonManager", "Phản hồi từ server: " + responseBody);

                    Gson gson = new Gson();
                    ApiResponseCourse apiResponse = gson.fromJson(responseBody, ApiResponseCourse.class);

                    if (apiResponse.getStatusCode() == 200) {
                        Course course = apiResponse.getData();
                        callback.onSuccess(course);
                    } else {
                        callback.onFailure("Lỗi từ server: " + apiResponse.getMessage());
                    }
                } else {
                    Log.e("LessonManager", "Lỗi từ server: Mã lỗi " + response.code());
                    callback.onFailure("Lỗi từ server: Mã lỗi " + response.code());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("LessonManager", "Lỗi kết nối: " + e.getMessage());
                callback.onFailure("Không thể kết nối tới API.");
            }
        });
    }
}