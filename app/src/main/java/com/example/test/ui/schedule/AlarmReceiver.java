package com.example.test.ui.schedule;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.test.R;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "scheduleChannel";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver", "🔥 Báo thức kích hoạt! Đang gửi thông báo...");

        // 🔥 Kiểm tra quyền trước khi gửi thông báo (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.e("AlarmReceiver", "❌ Không có quyền POST_NOTIFICATIONS! Không thể gửi thông báo.");
                return;
            }
        }

        // 🔥 Tạo Notification Channel (chỉ cần tạo 1 lần)
        createNotificationChannel(context);

        // 🔔 Tạo thông báo
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_alert) // Đặt icon
                .setContentTitle("Lịch học")
                .setContentText("Đến giờ học rồi!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify(1, builder.build());
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Lịch học",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Kênh thông báo lịch học");

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
