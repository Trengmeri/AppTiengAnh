package com.example.test.api;

import android.util.Log;

import com.example.test.model.Course;
import com.example.test.model.Lesson;
import com.example.test.response.ApiResponseAllCourse;
import com.example.test.response.ApiResponseCourse;
import com.example.test.response.ApiResponseLesson;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
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

    public void fetchCourseById(int courseId, ApiCallback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "/api/v1/courses/" + courseId ) // Thay bằng URL máy chủ của bạn
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
    public void fetchCourses(int page, int size, final ApiCallback<ApiResponseAllCourse> callback) {
        String url = BASE_URL + "/api/v1/courses?page=" + page + "&size=" + size; // Thay bằng URL máy chủ của bạn

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("LessonManager", "Phản hồi từ server: " + responseBody);

                    Gson gson = new Gson();
                    Type courseListType = new TypeToken<ApiResponseCourse>() {}.getType();
                    ApiResponseAllCourse apiResponse = gson.fromJson(responseBody, courseListType);

                    if (apiResponse.getStatusCode() == 200) {
                        callback.onSuccess(apiResponse);
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
    public void fetchAllLessonIds(ApiCallback<List<Integer>> callback) {
        List<Integer> allLessonIds = new ArrayList<>();
        fetchLessonsByPage(0, allLessonIds, callback);
    }

    private void fetchLessonsByPage(int page, List<Integer> allLessonIds, ApiCallback<List<Integer>> callback) {
        String url = BASE_URL + "/api/v1/lessons?page=" + page;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("Lỗi kết nối: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onFailure("Lỗi từ server: Mã lỗi " + response.code());
                    return;
                }

                String responseBody = response.body().string();
                Log.d("LessonManager", "Phản hồi từ server: " + responseBody);
                Gson gson = new Gson();

                try {
                    JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
                    int totalPages = jsonResponse.getAsJsonObject("data").get("totalPages").getAsInt();
                    Type listType = new TypeToken<List<Lesson>>() {}.getType();
                    List<Lesson> lessons = gson.fromJson(jsonResponse.getAsJsonObject("data").get("content"), listType);

                    for (Lesson lesson : lessons) {
                        allLessonIds.add(lesson.getId());
                    }

                    if (page + 1 < totalPages) {
                        fetchLessonsByPage(page + 1, allLessonIds, callback);
                    } else {
                        callback.onSuccess(allLessonIds);
                    }

                } catch (Exception e) {
                    callback.onFailure("Lỗi khi xử lý JSON: " + e.getMessage());
                }
            }
        });
    }
}