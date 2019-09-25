package com.justchill.android.learnachord.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

import com.justchill.android.learnachord.MyApplication;
import com.justchill.android.learnachord.R;


// Handles showing notifications (for now, that's only reminder notification)
public class NotificationHelper extends ContextWrapper {

    // TODO: popup notification
    // TODO: on API 29 notifications are showed inside linear layout

    /*
     * https://developer.android.com/training/scheduling/alarms#java
     * https://developer.android.com/reference/android/app/AlarmManager.html#setInexactRepeating(int,%20long,%20long,%20android.app.PendingIntent)
     */

    private static final String CHANNEL_ID = "com.justchill.android.learnachord.notification";
    private static final int NOTIFICATION_ID = 3000;
    private static final String CHANNEL_NAME = "Reminder channel";
    private static NotificationManager manager;


    public NotificationHelper(Context base) {
        super(base);

        // Create one channel all reminders will be sent to
        createChannel();
    }

    // On API 26 and above, notification channel is needed
    private static void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            // TODO: add more options
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
    public static Notification.Builder reminderNotification(String title, String body) {
        Notification.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            builder = new Notification.Builder(MyApplication.getAppContext().getApplicationContext(), CHANNEL_ID);
        else builder = new Notification.Builder(MyApplication.getAppContext().getApplicationContext());

        builder.setSmallIcon(R.drawable.ic_play_arrow)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true);

        return builder;
    }

    // Creates and displays reminder notification
    public static void showReminderNotification() {
        Notification.Builder builder = reminderNotification("title", "body");
        getManager().notify(NOTIFICATION_ID, builder.build());
    }


}
