package com.justchill.android.learnachord.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.justchill.android.learnachord.MyApplication;
import com.justchill.android.learnachord.database.DataContract;
import com.justchill.android.learnachord.database.DatabaseData;


// Runs on boot and every t milliseconds (and on boot) to check if reminder should be sent
public class StartOnBootReceiver extends BroadcastReceiver {

    private static final AlarmManager alarmMgr = (AlarmManager)MyApplication.getAppContext().getSystemService(Context.ALARM_SERVICE);
    private static final Intent intent = new Intent(MyApplication.getAppContext(), StartOnBootReceiver.class);
    private static final PendingIntent alarmIntent = PendingIntent.getBroadcast(MyApplication.getAppContext(), 5001, intent, 0);

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // Runs only on boot, schedule alarm for periodic "should it show reminder" check
//            scheduleRepeatingAlarm(); // TODO: add this
        }

        // If app is opened, something went wrong, don't show reminder
        if(!MyApplication.isUIVisible()) {

        }

        NotificationHandler.showReminderNotification();
    }

    // Schedules alarm for periodic "should it show reminder" check
    public static void scheduleRepeatingAlarm() {
        cancelRepeatingAlarm();
        try {
            alarmMgr.setInexactRepeating(AlarmManager.RTC, SystemClock.elapsedRealtime() +
                    AlarmManager.INTERVAL_HALF_HOUR, AlarmManager.INTERVAL_HALF_HOUR, alarmIntent);
        } catch (Exception ignored) {}
    }

    // Cancels alarm for checking of reminder should be shown
    public static void cancelRepeatingAlarm() {
        if (alarmMgr != null) {
            alarmMgr.cancel(alarmIntent);
        }
    }

    // TODO: this may not be needed
    // Returns repeating alarm interval value in milliseconds
    private static Integer getRepeatingAlarmInterval() {
        int valueToReturn = 1000;
        switch (DatabaseData.reminderTimeIntervalMode) {
            case DataContract.UserPrefEntry.REMINDER_TIME_INTERVAL_HOUR:
                valueToReturn *= 60 * 60;
                break;
            case DataContract.UserPrefEntry.REMINDER_TIME_INTERVAL_DAY:
                valueToReturn *= 60 * 60 * 24;
                break;
            case DataContract.UserPrefEntry.REMINDER_TIME_INTERVAL_WEEK:
                valueToReturn *= 60 * 60 * 24 * 7;
                break;
            case DataContract.UserPrefEntry.REMINDER_TIME_INTERVAL_MONTH:
                valueToReturn *= 60 * 60 * 24 * 30;
                break;
            default:
                return null;
        }
        return valueToReturn;
    }


}
