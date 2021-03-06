package com.justchill.android.learnachord.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import androidx.appcompat.app.AppCompatDelegate;

import com.justchill.android.learnachord.MyApplication;
import com.justchill.android.learnachord.R;
import com.justchill.android.learnachord.firebase.FirebaseHandler;
import com.justchill.android.learnachord.intervalOrChord.ChordsList;
import com.justchill.android.learnachord.intervalOrChord.IntervalsList;
import com.justchill.android.learnachord.notifications.StartOnBootReceiver;

// Handles reading/writing to database
public class DatabaseHandler {


    // do intervals need to update / set to data from database
    private static boolean doIntervalsNeedUpdate = true;
    // do chords need to be set to data that is in database
    private static boolean doChordsNeedUpdate = true;
    // does database need to change / set to all current data / save all data to DB
    private static boolean doesDbNeedUpdate = false;
    // do options need to change / set to data from database
    private static boolean doSettingsNeedUpdate = true;
    // does achievement progress need to update / set to data from database
    private static boolean doAchievementsNeedUpdate = true;


    // Public get and set methods
    public static boolean doIntervalsNeedUpdate() {
        return doIntervalsNeedUpdate;
    }
    public static void setDoIntervalsNeedUpdate(boolean newBool) {
        doIntervalsNeedUpdate = newBool;
    }

    public static boolean doChordsNeedUpdate() {
        return doChordsNeedUpdate;
    }
    public static void setDoChordsNeedUpdate(boolean newBool) {
        doChordsNeedUpdate = newBool;
    }

    public static boolean doesDbNeedUpdate() {
        return doesDbNeedUpdate;
    }
    public static void setDoesDbNeedUpdate(boolean newBool) {
        doesDbNeedUpdate = newBool;
    }

    public static boolean doSettingsNeedUpdate() {
        return doSettingsNeedUpdate;
    }
    public static void setDoSettingsNeedUpdate(boolean newBool) {
        doSettingsNeedUpdate = newBool;
    }

    public static boolean doAchievementsNeedUpdate() {
        return doAchievementsNeedUpdate;
    }
    public static void setDoAchievementsNeedUpdate(boolean newBool) {
        doAchievementsNeedUpdate = newBool;
    }



    // Calls method to update intervals on separate thread
    public static void updateIntervalsOnSeparateThread() {
        Thread userPrefThread2 = new Thread() {
            @Override
            public void run() {
                updateIntervals();
            }
        };
        userPrefThread2.start();
    }

    // Calls method to update chords on separate thread
    public static void updateChordsOnSeparateThread() {
        Thread userPrefThread4 = new Thread() {
            @Override
            public void run() {
                updateChords();
            }
        };
        userPrefThread4.start();
    }

    // Calls method to update options on separate thread
    public static void updateSettingsOnSeparateThread() {
        Thread userPrefThread3 = new Thread() {
            @Override
            public void run() {
                updateSettings();
            }
        };
        userPrefThread3.start();
    }

    public static void updateAchievementsOnSeparateThread() {
        Thread achievementsDatabaseThread = new Thread() {
            @Override
            public void run() {
                updateAchievements();
            }
        };
        achievementsDatabaseThread.start();
    }

    // Calls method to update database / save to database on separate thread
    public static void updateDatabaseOnSeparateThread() {
        Thread userPrefThread = new Thread() {
            @Override
            public void run() {
                updateDatabase();
            }
        };
        userPrefThread.start();
    }

    // Calls method to write last time app has been used in milliseconds to database on separate thread
    public static void writeLastTimeAppUsedOnSeparateThread() {
        Thread lastTimeUsedThread = new Thread() {
            @Override
            public void run() {
                writeLastTimeAppUsed();
            }
        };
        lastTimeUsedThread.start();
    }


    // Set data for all intervals that is stored in database (is interval checked)
    public static void updateIntervals() {
        // Get list of database IDs for all intervals
        String[] projection = DataContract.concatenateTwoArrays(
                MyApplication.getAppContext().getResources().getStringArray(R.array.interval_keys),
                MyApplication.getAppContext().getResources().getStringArray(R.array.interval_keys_above_octave));
        // Get ID of a row in DB (for now, DB has only one row)
        String[] tempForID = { DataContract.UserPrefEntry._ID };
        // Setup projection for database query (add ID to beginning)
        projection = DataContract.concatenateTwoArrays(tempForID, projection);

        // Get data from the database
        Cursor cursor = MyApplication.getAppContext().getContentResolver().query(DataContract.UserPrefEntry.CONTENT_URI_FIRST_ROW,
                projection, null, null, null);

        // If there is no data, don't do nothing
        if(cursor == null) {
            return;
        }

        cursor.moveToFirst(); // Move the cursor to the first row (and only row, as it is for now)

        // Temp variable
        boolean isItCheckedBool;

        try {
            // Gets all DB intervals' keys by order from R.arrays.interval_keys and R.arrays.interval_keys_above_octave
            String[] intervalKeys = DataContract.concatenateTwoArrays(
                    MyApplication.getAppContext().getResources().getStringArray(R.array.interval_keys),
                    MyApplication.getAppContext().getResources().getStringArray(R.array.interval_keys_above_octave));
            // Loop through all data
            for (int i = 0; i < intervalKeys.length; i++) {
                // Get data for every intervals that is in database
                isItCheckedBool = cursor.getInt(cursor.getColumnIndex(intervalKeys[i])) == DataContract.UserPrefEntry.CHECKBOX_CHECKED;

                // Set data to that interval
                IntervalsList.getInterval(i).setIsChecked(isItCheckedBool);
            }
        } finally {
            // At the end close connection to DB
            cursor.close();
        }

        // Intervals' data has just been got from database, no need to update intervals (anymore)
        setDoIntervalsNeedUpdate(false);
    }


    // Set data for all chords that is stored in database (is chord checked)
    public static void updateChords() {
        // Get list of database IDs for all chords
        String[] projection = MyApplication.getAppContext().getResources().getStringArray(R.array.chord_keys);
        // Get ID of a row in DB (for now, DB has only one row)
        String[] tempForID = { DataContract.UserPrefEntry._ID };
        // Setup projection for database query (add ID to beginning)
        projection = DataContract.concatenateTwoArrays(tempForID, projection);

        // Get data from the database
        Cursor cursor = MyApplication.getAppContext().getContentResolver().query(DataContract.UserPrefEntry.CONTENT_URI_FIRST_ROW,
                projection, null, null, null);

        // If there is no data, don't do nothing
        if(cursor == null) {
            return;
        }

        cursor.moveToFirst(); // Move the cursor to the first row (and only row, as it is now)

        // Temp variable
        boolean isItCheckedBool;

        try {
            // Gets all DB chords' keys by order from R.array.chord_keys
            String[] chordKeys = MyApplication.getAppContext().getResources().getStringArray(R.array.chord_keys);
            // Loop through all data
            for (int i = 0; i < chordKeys.length; i++) {
                // Get data for every chord that is in database
                isItCheckedBool = cursor.getInt(cursor.getColumnIndex(chordKeys[i])) == DataContract.UserPrefEntry.CHECKBOX_CHECKED;

                // Set data to that chord
                ChordsList.getChord(i).setIsChecked(isItCheckedBool);
            }
        } finally {
            // At the end close connection to DB
            cursor.close();
        }

        // Chords' data has just been got from database, no need to update chords (anymore)
        setDoChordsNeedUpdate(false);
    }


    // Set data to all options that is stored in database (except intervals and chords, they are handled separately)
    public static void updateSettings() {
        // Get list of database IDs for all options (saved user preferences)
        String[] projection = MyApplication.getAppContext().getResources().getStringArray(R.array.preference_keys);
        // Get ID of a row in DB (for now, DB has only one row)
        String[] tempForID = { DataContract.UserPrefEntry._ID };
        // Setup projection for database query (add ID to beginning)
        projection = DataContract.concatenateTwoArrays(tempForID, projection); // projection MUST have _ID

        // Get data from the database
        Cursor cursor = MyApplication.getAppContext().getContentResolver().query(DataContract.UserPrefEntry.CONTENT_URI_FIRST_ROW,
                projection, null, null, null);

        // If there is no data, don't do nothing
        if(cursor == null) {
            return;
        }

        cursor.moveToFirst(); // Move the cursor to the first row (and only row for now)

        try {
            // Gets all preferences' keys by order from R.array.preference_keys
            String[] preferenceKeys = MyApplication.getAppContext().getResources().getStringArray(R.array.preference_keys);

            // Set all data to match data from DB
            DatabaseData.directionUp = cursor.getInt(cursor.getColumnIndex(preferenceKeys[0])) ==
                    DataContract.UserPrefEntry.CHECKBOX_CHECKED;
            DatabaseData.directionDown = cursor.getInt(cursor.getColumnIndex(preferenceKeys[1])) ==
                    DataContract.UserPrefEntry.CHECKBOX_CHECKED;
            DatabaseData.directionSameTime = cursor.getInt(cursor.getColumnIndex(preferenceKeys[2])) ==
                    DataContract.UserPrefEntry.CHECKBOX_CHECKED;
            DatabaseData.tonesSeparationTime = (double) cursor.getInt(cursor.getColumnIndex(preferenceKeys[3]));
            DatabaseData.delayBetweenChords = (double) cursor.getInt(cursor.getColumnIndex(preferenceKeys[4]));
            DatabaseData.appLanguage = cursor.getInt(cursor.getColumnIndex(preferenceKeys[5]));
            DatabaseData.downKeyBorder = cursor.getInt(cursor.getColumnIndex(preferenceKeys[6]));
            DatabaseData.upKeyBorder = cursor.getInt(cursor.getColumnIndex(preferenceKeys[7]));
            DatabaseData.showProgressBar = cursor.getInt(cursor.getColumnIndex(preferenceKeys[8])) ==
                    DataContract.UserPrefEntry.CHECKBOX_CHECKED;
            DatabaseData.showWhatIntervals = cursor.getInt(cursor.getColumnIndex(preferenceKeys[9])) ==
                    DataContract.UserPrefEntry.CHECKBOX_CHECKED;
            DatabaseData.chordTextScalingMode = cursor.getInt(cursor.getColumnIndex(preferenceKeys[10]));
            DatabaseData.playingMode = cursor.getInt(cursor.getColumnIndex(preferenceKeys[11]));
            DatabaseData.directionUpViewIndex = cursor.getInt(cursor.getColumnIndex(preferenceKeys[12]));
            DatabaseData.directionDownViewIndex = cursor.getInt(cursor.getColumnIndex(preferenceKeys[13]));
            DatabaseData.directionSameTimeViewIndex = cursor.getInt(cursor.getColumnIndex(preferenceKeys[14]));
            DatabaseData.playWhatTone = cursor.getInt(cursor.getColumnIndex(preferenceKeys[15])) ==
                    DataContract.UserPrefEntry.CHECKBOX_CHECKED;
            DatabaseData.playWhatOctave = cursor.getInt(cursor.getColumnIndex(preferenceKeys[16])) ==
                    DataContract.UserPrefEntry.CHECKBOX_CHECKED;
            DatabaseData.quizModeOneHighscore = cursor.getInt(cursor.getColumnIndex(preferenceKeys[17]));
            DatabaseData.quizModeTwoHighscore = cursor.getInt(cursor.getColumnIndex(preferenceKeys[18]));
            DatabaseData.quizModeThreeHighscore = cursor.getInt(cursor.getColumnIndex(preferenceKeys[19]));
            FirebaseHandler.imageToSet = cursor.getInt(cursor.getColumnIndex(preferenceKeys[20]));
            try {
                // Try to read this value in case it's not null
                FirebaseHandler.photoFromPhonePath = Uri.parse(cursor.getString(cursor.getColumnIndex(preferenceKeys[21])));
            } catch (NullPointerException e) {
                FirebaseHandler.photoFromPhonePath = null;
            }
            DatabaseData.logInHelpShowed = cursor.getInt(cursor.getColumnIndex(preferenceKeys[22]));
            DatabaseData.mainActivityHelpShowed = cursor.getInt(cursor.getColumnIndex(preferenceKeys[23]));
            DatabaseData.settingsActivityHelpShowed = cursor.getInt(cursor.getColumnIndex(preferenceKeys[24]));
            DatabaseData.quizActivityHelpShowed = cursor.getInt(cursor.getColumnIndex(preferenceKeys[25]));
            DatabaseData.userProfileActivityHelpShowed = cursor.getInt(cursor.getColumnIndex(preferenceKeys[26]));
            DatabaseData.reminderTimeIntervalNumber = cursor.getInt(cursor.getColumnIndex(preferenceKeys[27]));
            DatabaseData.reminderTimeIntervalMode = cursor.getInt(cursor.getColumnIndex(preferenceKeys[28]));
            DatabaseData.lastTimeAppUsedInMillis = cursor.getLong(cursor.getColumnIndex(preferenceKeys[29]));
            DatabaseData.nightModeId = cursor.getInt(cursor.getColumnIndex(preferenceKeys[30]));


        } finally {
            // At the end close connection to DB
            cursor.close();
        }


        // Sets new app theme in case it has been changed
        try {
            MyApplication.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AppCompatDelegate.setDefaultNightMode(DatabaseData.nightModeId);
                    // Set this to false only after mode is changed, so logInDialog doesn't show twice (light and dark version)
                    setDoSettingsNeedUpdate(false);
                }
            });
        } catch (Exception e) {
            // User preferences' data has just been got from database, no need to update it (anymore)
            setDoSettingsNeedUpdate(false);
        }


        // Refresh counter for number of checked directions (in case it has been changed)
        DataContract.UserPrefEntry.refreshDirectionsCount();

        // If language changes translate interval and chord names, and with that, set locale language
        IntervalsList.updateAllIntervalsNames();
        ChordsList.updateAllChordsNames();
    }

    // Set data for all achievements that is stored in database (achievement progress)
    public static void updateAchievements() {
        // Get list of database IDs for all achievements
        String[] projection = MyApplication.getAppContext().getResources().getStringArray(R.array.achievement_progress_keys);
        // Get ID of a row in DB (for now, DB has only one row)
        String[] tempForID = { DataContract.UserPrefEntry._ID };
        // Setup projection for database query (add ID to beginning)
        projection = DataContract.concatenateTwoArrays(tempForID, projection);

        // Get data from the database
        Cursor cursor = MyApplication.getAppContext().getContentResolver().query(DataContract.UserPrefEntry.CONTENT_URI_FIRST_ROW,
                projection, null, null, null);

        // If there is no data, don't do nothing
        if(cursor == null) {
            return;
        }

        cursor.moveToFirst(); // Move the cursor to the first row (and only row, as it is now)

        try {
            // Gets all DB achievements' keys by order from R.array.achievement_progress_keys
            String[] achievementKeys = MyApplication.getAppContext().getResources().getStringArray(R.array.achievement_progress_keys);
            // Loop through all data
            for (int i = 0; i < achievementKeys.length; i++) {
                // Get and set data for every achievement that is in database
                FirebaseHandler.user.setAchievementProgress(i, cursor.getInt(cursor.getColumnIndex(achievementKeys[i])));
            }
        } finally {
            // At the end close connection to DB
            cursor.close();
        }

        // Achievements' data has just been gotten from database, no need to update achievements (anymore)
        setDoAchievementsNeedUpdate(false);
    }


    public static void updateDatabase() {
        // Create new content values that will be written to the DB
        ContentValues values = new ContentValues();

        // Get all intervals' DB keys
        String[] intervalKeys = DataContract.concatenateTwoArrays(
                MyApplication.getAppContext().getResources().getStringArray(R.array.interval_keys),
                MyApplication.getAppContext().getResources().getStringArray(R.array.interval_keys_above_octave));
        // Put all intervals' data to ContentValues
        for (int i = 0; i < intervalKeys.length; i++) {
            values.put(intervalKeys[i], IntervalsList.getInterval(i).getIsChecked() ? DataContract.UserPrefEntry.CHECKBOX_CHECKED
                    : DataContract.UserPrefEntry.CHECKBOX_NOT_CHECKED);
        }

        // Get all chords' DB keys
        String[] chordKeys = MyApplication.getAppContext().getResources().getStringArray(R.array.chord_keys);
        // Put all intervals' data to ContentValues
        for (int i = 0; i < chordKeys.length; i++) {
            values.put(chordKeys[i], ChordsList.getChord(i).getIsChecked() ? DataContract.UserPrefEntry.CHECKBOX_CHECKED
                    : DataContract.UserPrefEntry.CHECKBOX_NOT_CHECKED);
        }

        // Fix after removing veliki_povecani_kvintsekstakord (removing it from DB was hard, and it was late, ok?)
        values.put("veliki_povecani_kvintsekstakord", DataContract.UserPrefEntry.CHECKBOX_NOT_CHECKED);


        // Get all preferences' DB keys
        String[] preferenceKeys = MyApplication.getAppContext().getResources().getStringArray(R.array.preference_keys);
        // Put all preferences' data to ContentValues
        values.put(preferenceKeys[0], DatabaseData.directionUp ? DataContract.UserPrefEntry.CHECKBOX_CHECKED :
                DataContract.UserPrefEntry.CHECKBOX_NOT_CHECKED);
        values.put(preferenceKeys[1], DatabaseData.directionDown ? DataContract.UserPrefEntry.CHECKBOX_CHECKED :
                DataContract.UserPrefEntry.CHECKBOX_NOT_CHECKED);
        values.put(preferenceKeys[2], DatabaseData.directionSameTime ? DataContract.UserPrefEntry.CHECKBOX_CHECKED :
                DataContract.UserPrefEntry.CHECKBOX_NOT_CHECKED);
        values.put(preferenceKeys[3], (int) DatabaseData.tonesSeparationTime);
        values.put(preferenceKeys[4], (int) DatabaseData.delayBetweenChords);
        values.put(preferenceKeys[5], DatabaseData.appLanguage);
        values.put(preferenceKeys[6], DatabaseData.downKeyBorder);
        values.put(preferenceKeys[7], DatabaseData.upKeyBorder);
        values.put(preferenceKeys[8], DatabaseData.showProgressBar ? DataContract.UserPrefEntry.CHECKBOX_CHECKED :
                DataContract.UserPrefEntry.CHECKBOX_NOT_CHECKED);
        values.put(preferenceKeys[9], DatabaseData.showWhatIntervals ? DataContract.UserPrefEntry.CHECKBOX_CHECKED :
                DataContract.UserPrefEntry.CHECKBOX_NOT_CHECKED);
        values.put(preferenceKeys[10], DatabaseData.chordTextScalingMode);
        values.put(preferenceKeys[11], DatabaseData.playingMode);
        values.put(preferenceKeys[12], DatabaseData.directionUpViewIndex);
        values.put(preferenceKeys[13], DatabaseData.directionDownViewIndex);
        values.put(preferenceKeys[14], DatabaseData.directionSameTimeViewIndex);
        values.put(preferenceKeys[15], DatabaseData.playWhatTone ? DataContract.UserPrefEntry.CHECKBOX_CHECKED :
                DataContract.UserPrefEntry.CHECKBOX_NOT_CHECKED);
        values.put(preferenceKeys[16], DatabaseData.playWhatOctave ? DataContract.UserPrefEntry.CHECKBOX_CHECKED :
                DataContract.UserPrefEntry.CHECKBOX_NOT_CHECKED);
        values.put(preferenceKeys[17], DatabaseData.quizModeOneHighscore);
        values.put(preferenceKeys[18], DatabaseData.quizModeTwoHighscore);
        values.put(preferenceKeys[19], DatabaseData.quizModeThreeHighscore);
        values.put(preferenceKeys[20], FirebaseHandler.imageToSet);
        String photoFromGalleryPathValue = "";
        try {
            photoFromGalleryPathValue = FirebaseHandler.photoFromPhonePath.toString();
        } catch (Exception ignored) {}
        values.put(preferenceKeys[21], photoFromGalleryPathValue);

        String[] achievementProgressKeys = MyApplication.getAppContext().getResources().getStringArray(R.array.achievement_progress_keys);
        for (int i = 0; i < achievementProgressKeys.length; i++) {
            values.put(achievementProgressKeys[i], FirebaseHandler.user.achievementProgress.get(i));
        }
        values.put(preferenceKeys[22], DatabaseData.logInHelpShowed);
        values.put(preferenceKeys[23], DatabaseData.mainActivityHelpShowed);
        values.put(preferenceKeys[24], DatabaseData.settingsActivityHelpShowed);
        values.put(preferenceKeys[25], DatabaseData.quizActivityHelpShowed);
        values.put(preferenceKeys[26], DatabaseData.userProfileActivityHelpShowed);
        values.put(preferenceKeys[27], DatabaseData.reminderTimeIntervalNumber);
        values.put(preferenceKeys[28], DatabaseData.reminderTimeIntervalMode);
        values.put(preferenceKeys[29], DatabaseData.lastTimeAppUsedInMillis = System.currentTimeMillis());
        values.put(preferenceKeys[30], DatabaseData.nightModeId);

        // Update the database with ContentValues data, returns how many rows were affected
        int newRowUri = MyApplication.getAppContext().getContentResolver().update(DataContract.UserPrefEntry.CONTENT_URI_FIRST_ROW,
                values, null, null);

        // Database has just been updated, no need to update anything anymore (as we already have same data)
        setDoIntervalsNeedUpdate(false);
        setDoChordsNeedUpdate(false);
        setDoSettingsNeedUpdate(false);
        setDoAchievementsNeedUpdate(false);
        setDoesDbNeedUpdate(false);

        // If some setting is changed update interval and chord names (in case it is language)
        if(newRowUri > 0) {
            IntervalsList.updateAllIntervalsNames();
            ChordsList.updateAllChordsNames();
            // In case reminder settings have been changed, reschedule reminder alarm
            StartOnBootReceiver.scheduleRepeatingAlarm();
        }
    }

    // Writes last time app has been used in milliseconds to database
    public static void writeLastTimeAppUsed() {
        // Create new content values that will be written to the DB
        ContentValues values = new ContentValues();

        // Get all preferences' DB keys
        String[] preferenceKeys = MyApplication.getAppContext().getResources().getStringArray(R.array.preference_keys);

        // Set last Time app has been used in millis
        values.put(preferenceKeys[29], DatabaseData.lastTimeAppUsedInMillis = System.currentTimeMillis());

        // Update the database with ContentValues data, returns how many rows were affected
        int newRowUri = MyApplication.getAppContext().getContentResolver().update(DataContract.UserPrefEntry.CONTENT_URI_FIRST_ROW,
                values, null, null);

    }

}
