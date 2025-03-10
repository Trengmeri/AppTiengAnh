package com.example.test.ui.schedule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.test.ui.schedule.AlarmScheduler;
import com.example.test.SharedPreferencesManager;
import com.example.test.api.ScheduleManager;
import com.example.test.model.Schedule;
import com.example.test.response.ApiResponSchedule;
import com.example.test.api.ApiCallback;

import java.util.List;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("BootReceiver", "Nhận intent: " + intent.getAction());

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d("BootReceiver", "Thiết bị đã khởi động lại!");

            int userId;
            try {
                userId = Integer.parseInt(SharedPreferencesManager.getInstance(context).getID());
            } catch (NumberFormatException e) {
                Log.e("BootReceiver", "Lỗi: userId không hợp lệ");
                return;
            }

            Log.d("BootReceiver", "Đang lấy lịch học từ API...");
            ScheduleManager scheduleManager = new ScheduleManager(context);
            scheduleManager.fetchSchedulesByUserId(userId, new ApiCallback<List<Schedule>>() {
                @Override
                public void onSuccess() {}

                @Override
                public void onSuccess(List<Schedule> schedules) {
                    Log.d("BootReceiver", "Nhận được " + schedules.size() + " lịch học");
                    for (Schedule schedule : schedules) {
                        Log.d("BootReceiver", "Lên lịch báo thức cho: " + schedule.getScheduleTime());
                        AlarmScheduler.scheduleAlarm(context, schedule.getScheduleTime());
                    }
                }

                @Override
                public void onFailure(String errorMessage) {
                    Log.e("BootReceiver", "Lỗi lấy lịch học: " + errorMessage);
                }
            });
        } else {
            Log.e("BootReceiver", "Intent không hợp lệ: " + intent.getAction());
        }
    }

}
