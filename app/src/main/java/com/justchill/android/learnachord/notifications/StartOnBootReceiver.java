package com.justchill.android.learnachord.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.justchill.android.learnachord.MyApplication;
import com.justchill.android.learnachord.database.DataContract;
import com.justchill.android.learnachord.database.DatabaseData;
import com.justchill.android.learnachord.database.DatabaseHandler;


// Runs on boot and every t milliseconds to check if reminder should be sent
public class StartOnBootReceiver extends BroadcastReceiver {

    private static final AlarmManager alarmMgr = (AlarmManager)MyApplication.getAppContext().getSystemService(Context.ALARM_SERVICE);
    private static final Intent intent = new Intent(MyApplication.getAppContext(), StartOnBootReceiver.class);
    private static final PendingIntent alarmIntent = PendingIntent.getBroadcast(MyApplication.getAppContext(), 5001, intent, 0);

    private static boolean reminderVariablesLoaded = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(this.getClass().getName(), "OnReceive called");
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // Runs only on boot, schedule alarm for periodic "should it show reminder" check
            scheduleRepeatingAlarm();
        }

        // Load all static variables from DB (for reminder)
        if(!reminderVariablesLoaded) {
            Thread loadReminderVariables = new Thread(new Runnable() {
                @Override
                public void run() {
                    DatabaseHandler.updateSettings();
                    if(!MyApplication.isUIVisible()) {
                        alarmCheck();
                    }
                    reminderVariablesLoaded = true;
                }
            });
            loadReminderVariables.start();
        } else if(!MyApplication.isUIVisible()) {
            // If app is opened, something went wrong, don't show reminder
            alarmCheck();
        }


    }

    // Schedules alarm for periodic "should it show reminder" check, turns it off if reminder is turned off
    public static void scheduleRepeatingAlarm() {
        cancelRepeatingAlarm();
        try {
            // TODO: set check interval depending on chosen time interval
            // TODO: maybe change RTC to something else (if it doesn't show reminder when checked or if it wakes the screen up)
            if(DatabaseData.reminderTimeIntervalMode != DataContract.UserPrefEntry.REMINDER_TIME_INTERVAL_NEVER)
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

    // Checks if reminder should be shown and shows it
    public void alarmCheck() {
        Integer timeInterval = getRepeatingAlarmInterval();
        if(timeInterval == null) return;
        timeInterval *= DatabaseData.reminderTimeIntervalNumber;
        if(System.currentTimeMillis() > DatabaseData.lastTimeAppUsedInMillis + timeInterval) {
            NotificationHandler.showReminderNotification();
            // When alarm is shown, that time is last time app has been used (to not show reminder on every next check)
            DatabaseHandler.writeLastTimeAppUsedOnSeparateThread();
        }
    }

    // Returns repeating alarm time interval value in milliseconds
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
