package com.justchill.android.learnachord.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.justchill.android.learnachord.MainActivity;
import com.justchill.android.learnachord.MyApplication;
import com.justchill.android.learnachord.R;


// Handles showing notifications (for now, that's only reminder notification)
public class NotificationHandler extends ContextWrapper {

    private static final String CHANNEL_ID = "com.justchill.android.learnachord.notification";
    private static final int NOTIFICATION_ID = 3000;
    private static final String CHANNEL_NAME = "Reminder channel";
    private static NotificationManager manager;


    public NotificationHandler(Context base) {
        super(base);
    }

    // On API 26 and above, notification channel is needed
    private static void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableVibration(true);
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            getManager().createNotificationChannel(channel);
        }
    }

    // Return notification manager (system service)
    public static NotificationManager getManager() {
        if(manager == null)
            manager = (NotificationManager) MyApplication.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }

    // Creates new reminder notification
    public static NotificationCompat.Builder reminderNotification(String title, String body) {
        NotificationCompat.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            builder = new NotificationCompat.Builder(MyApplication.getAppContext(), CHANNEL_ID);
        else builder = new NotificationCompat.Builder(MyApplication.getAppContext());

        // Setup intent for opening MainActivity on reminder notification click
        Intent notificationIntent = new Intent(MyApplication.getAppContext(), MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent openAppIntent = PendingIntent.getActivity(MyApplication.getAppContext(), 5002, notificationIntent, 0);

        builder.setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.learn_achord_icon)
                .setLargeIcon(BitmapFactory.decodeResource(MyApplication.getAppContext().getResources(), R.mipmap.learn_achord_icon))
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(openAppIntent)
                .setAutoCancel(true);

        return builder;
    }

    // Creates and displays reminder notification
    public static void showReminderNotification() {
        NotificationCompat.Builder builder = reminderNotification(MyApplication.readResource(R.string.app_name, null), "Text");
        // Create one channel all reminders will be sent to
        createChannel();
        getManager().notify(NOTIFICATION_ID, builder.build());
    }


}
