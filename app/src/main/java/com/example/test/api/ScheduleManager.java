package com.example.test.api;

import android.content.Context;
import android.util.Log;

import com.example.test.NotificationManager;
import com.example.test.NotificationStorage;
import com.example.test.SharedPreferencesManager;
import com.example.test.model.Schedule;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ScheduleManager extends BaseApiManager {

    private final Context context;

    public ScheduleManager(Context context) {
        this.context = context;
    }

    public void createSchedule(Schedule schedule, ApiCallback callback) {
        String userId = SharedPreferencesManager.getInstance(context).getID();
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("userId", userId);
            requestBody.put("scheduleTime", schedule.getScheduleTime());
            requestBody.put("isDaily", schedule.isDaily());
            requestBody.put("courseId", schedule.getCourseId());

            RequestBody body = RequestBody.create(requestBody.toString(), MediaType.parse("application/json; charset=utf-8"));

            Request request = new Request.Builder()
                    .url(BASE_URL + "/api/v1/schedules")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    // Xử lý lỗi
                    callback.onFailure("Lỗi kết nối: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        Log.d("Schedule:","Tao lich hoc thanh cong");
                        String responseBody = response.body().string();
                        callback.onSuccess(); // Hoặc có thể truyền thêm dữ liệu từ response
                        try {
                            JSONObject responseJson = new JSONObject(responseBody);
                            String message = responseJson.optString("message", "Your schedule has been created.");
                            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                            String id = SharedPreferencesManager.getInstance(context).getID();
                            //  Lưu userID vào SharedPreferences
                            SharedPreferencesManager.getInstance(context).saveID(id);
                            // 📌 Lưu thông báo vào SharedPreferences theo userID
                            // Lưu thông báo vào SharedPreferences theo userID
                            NotificationStorage.getInstance(context).saveNotification(id, "Create schedule successful", message, currentDate);
                        } catch (JSONException e) {
                            callback.onFailure("Lỗi phân tích phản hồi JSON: " + e.getMessage());
                        }
                    } else {
                        // Xử lý response không thành công
                        callback.onFailure("Lỗi server: " + response.code());
                    }
                }
            });

        } catch (JSONException e) {
            // Xử lý lỗi
            callback.onFailure("Lỗi JSON: " + e.getMessage());
        }
    }
}