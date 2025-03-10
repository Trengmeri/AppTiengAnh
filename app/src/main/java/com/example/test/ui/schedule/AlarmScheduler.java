package com.example.test.ui.schedule;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AlarmScheduler {

    public static void scheduleAlarm(Context context, String scheduleTime, int requestCode) {
        Log.d("AlarmScheduler", "👉 Đặt báo thức cho: " + scheduleTime + " với requestCode: " + requestCode);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        try {
            calendar.setTime(sdf.parse(scheduleTime));
        } catch (ParseException e) {
            Log.e("AlarmScheduler", "❌ Lỗi khi phân tích thời gian: " + e.getMessage());
            return;
        }

        long triggerTime = calendar.getTimeInMillis();
        Log.d("AlarmScheduler", "⏰ Thời gian báo thức (millis): " + triggerTime);

        Intent intent = new Intent(context, AlarmReceiver.class);

        // 🔥 Quan trọng: Sử dụng requestCode để tạo PendingIntent duy nhất cho từng báo thức
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                Log.e("AlarmScheduler", "❌ Ứng dụng chưa được cấp quyền đặt báo thức chính xác!");
                return;
            }

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            Log.d("AlarmScheduler", "✅ Báo thức đã được đặt vào: " + calendar.getTime());
        } else {
            Log.e("AlarmScheduler", "❌ AlarmManager không hoạt động!");
        }
    }

}
