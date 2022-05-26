package com.example.timer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Timer extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar calendar;
        SimpleDateFormat dateFormat;
        String date;
        String interval = "null";

        // Get interval from extras
        Bundle extras = intent.getExtras();
        if (extras != null) {
            interval = extras.getString("Interval");
        }

        // Get current date&time in specific format
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        date = dateFormat.format(calendar.getTime());
        String contentTitle = "Current date " + date + ". ";
        String contentText = "Next notification in " + interval + ".";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "timerNotification")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Log.d("TIMER", "New notification: " + contentTitle + contentText);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        notificationManagerCompat.notify(200, builder.build());
    }
}
