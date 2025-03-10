package com.example.test.ui.schedule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.test.SharedPreferencesManager;
import com.example.test.api.ApiCallback;
import com.example.test.api.ScheduleManager;
import com.example.test.model.Schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d("BootReceiver", "📥 Đang lấy lịch học từ API...");

            ScheduleManager scheduleManager = new ScheduleManager(context);// Lấy userId

            scheduleManager.fetchSchedulesByUserId(new ApiCallback<List<Schedule>>() {
                @Override
                public void onSuccess() {}

                @Override
                public void onSuccess(List<Schedule> schedules) {
                    Log.d("BootReceiver", "✅ Nhận được " + schedules.size() + " lịch học");

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                    long currentTime = System.currentTimeMillis();
                    int requestCode = 1000; // Để mỗi báo thức có ID riêng biệt

                    for (Schedule schedule : schedules) {
                        try {
                            long scheduleTime = sdf.parse(schedule.getScheduleTime()).getTime();
                            if (scheduleTime > currentTime) { // Chỉ đặt lịch chưa đến giờ
                                Log.d("BootReceiver", "📅 Lên lịch báo thức cho: " + schedule.getScheduleTime());
                                AlarmScheduler.scheduleAlarm(context, schedule.getScheduleTime(),  requestCode++);
                            } else {
                                Log.d("BootReceiver", "⏳ Bỏ qua lịch cũ: " + schedule.getScheduleTime());
                            }
                        } catch (ParseException e) {
                            Log.e("BootReceiver", "❌ Lỗi khi phân tích thời gian: " + e.getMessage());
                        }
                    }
                }

                @Override
                public void onFailure(String errorMessage) {
                    Log.e("BootReceiver", "❌ Lỗi lấy lịch học: " + errorMessage);
                }
            });
        }
    }
}
