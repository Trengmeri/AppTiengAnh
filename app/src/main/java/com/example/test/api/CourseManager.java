package com.example.test.api;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.test.SharedPreferencesManager;
import com.example.test.model.Course;
import com.example.test.model.Enrollment;
import com.example.test.response.ApiResponseCourse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class CourseManager extends BaseApiManager{
    private final Context context;

    public CourseManager(Context context) {
        this.context = context;
    }

    public void fetchEnrollmentsByUser(int userId, boolean proStatus, int page, int size, ApiCallback<List<Enrollment>> callback) {
        String token = SharedPreferencesManager.getInstance(context).getAccessToken();

        // Tạo URL với tham số proStatus
        String url = BASE_URL + "/api/v1/enrollments/user/" + userId +
                "?page=" + page + "&size=" + size + "&proStatus=" + proStatus;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Content-Type", "application/json")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("API_RESPONSE", "Dữ liệu nhận được: " + responseBody);

                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        JSONObject dataObject = jsonResponse.getJSONObject("data");
                        JSONArray contentArray = dataObject.getJSONArray("content");

                        Gson gson = new Gson();
                        Type enrollmentListType = new TypeToken<List<Enrollment>>() {}.getType();
                        List<Enrollment> enrollments = gson.fromJson(contentArray.toString(), enrollmentListType);

                        new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(enrollments));
                    } catch (JSONException e) {
                        new Handler(Looper.getMainLooper()).post(() -> callback.onFailure("Lỗi parse JSON: " + e.getMessage()));
                    }
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onFailure("Lỗi: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onFailure("Lỗi kết nối: " + e.getMessage()));
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



}
