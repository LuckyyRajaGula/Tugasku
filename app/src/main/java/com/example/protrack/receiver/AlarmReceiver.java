package com.example.protrack.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.example.protrack.MainActivity;
import com.example.protrack.R;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "task_reminders";
    private static final String CHANNEL_NAME = "Pengingat Tugas ProTrack";

    @Override
    public void onReceive(Context context, Intent intent) {
        String taskTitle = intent.getStringExtra("TASK_TITLE");
        int taskId = intent.getIntExtra("TASK_ID", -1);
        String taskNote = intent.getStringExtra("TASK_NOTE");

        if (taskTitle == null) {
            taskTitle = "Tugas Mendekati Deadline!";
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Buat Notification Channel untuk Android O ke atas
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Saluran notifikasi untuk pengingat deadline tugas");
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Intent ketika notifikasi diklik
        Intent clickIntent = new Intent(context, MainActivity.class);
        clickIntent.putExtra("OPEN_TASK_ID", taskId);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                taskId,
                clickIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher) // Menggunakan icon launcher app
                .setContentTitle("Deadline Tugas: " + taskTitle)
                .setContentText(taskNote != null && !taskNote.isEmpty() ? taskNote : "Tugas Anda akan segera berakhir!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        if (notificationManager != null) {
            notificationManager.notify(taskId, builder.build());
        }
    }
}
