package com.justchill.android.learnachord.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.justchill.android.learnachord.MyApplication;
import com.justchill.android.learnachord.R;
import com.justchill.android.learnachord.intervalOrChord.ChordsList;
import com.justchill.android.learnachord.intervalOrChord.IntervalsList;

public class DatabaseHandler {

    /**
     * @param doIntervalsNeedUpdate do intervals need to update / set to data from database
     * @param doChordsNeedUpdate do they need to be set to data that is in database
     * @param doesDbNeedUpdate does database need to change / set to all current data
     * @param doSettingsNeedUpdate same with settings
     */
    public static boolean doIntervalsNeedUpdate = true;
    public static boolean doChordsNeedUpdate = true;
    public static boolean doesDbNeedUpdate = false;
    public static boolean doSettingsNeedUpdate = true;



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



    public static void updateIntervalsOnSeparateThread() {
        Thread userPrefThread2 = new Thread() {
            @Override
            public void run() {
                updateIntervals();
            }
        };
        userPrefThread2.start();
    }

    public static void updateChordsOnSeparateThread() {
        Thread userPrefThread4 = new Thread() {
            @Override
            public void run() {
                updateChords();
            }
        };
        userPrefThread4.start();
    }

    public static void updateSettingsOnSeparateThread() {
        Thread userPrefThread3 = new Thread() {
            @Override
            public void run() {
                updateSettings();
            }
        };
        userPrefThread3.start();
    }

    public static void updateDatabaseOnSeparateThread() {
        Thread userPrefThread = new Thread() {
            @Override
            public void run() {
                updateDatabase();
            }
        };
        userPrefThread.start();
    }

    public static void updateIntervals() {
        String[] projection = DataContract.concatenateTwoArrays(
                MyApplication.getAppContext().getResources().getStringArray(R.array.interval_keys),
                MyApplication.getAppContext().getResources().getStringArray(R.array.interval_keys_above_octave));
        String[] tempForID = { DataContract.UserPrefEntry._ID };
        projection = DataContract.concatenateTwoArrays(tempForID, projection);

        Cursor cursor = MyApplication.getAppContext().getContentResolver().query(DataContract.UserPrefEntry.CONTENT_URI_FIRST_ROW,
                projection, null, null, null);

        if(cursor == null) {
            return;
        }

        cursor.moveToFirst(); // Move the cursor to the first row (and only row, as it is now)

        boolean isItCheckedBool;

        try {
            // Gets all intervals by order from id.arrays.interval_keys and id.arrays.interval_keys_above_octave
            String[] intervalKeys = DataContract.concatenateTwoArrays(
                    MyApplication.getAppContext().getResources().getStringArray(R.array.interval_keys),
                    MyApplication.getAppContext().getResources().getStringArray(R.array.interval_keys_above_octave));
            for (int i = 0; i < intervalKeys.length; i++) {
                isItCheckedBool = cursor.getInt(cursor.getColumnIndex(intervalKeys[i])) == DataContract.UserPrefEntry.CHECKBOX_CHECKED;

                IntervalsList.getInterval(i).setIsChecked(isItCheckedBool);
            }
        } finally {
            cursor.close();
        }

        setDoIntervalsNeedUpdate(false);
        setDoesDbNeedUpdate(false);
    }

    public static void updateChords() {
        String[] projection = MyApplication.getAppContext().getResources().getStringArray(R.array.chord_keys);
        String[] tempForID = { DataContract.UserPrefEntry._ID };
        projection = DataContract.concatenateTwoArrays(tempForID, projection);

        Cursor cursor = MyApplication.getAppContext().getContentResolver().query(DataContract.UserPrefEntry.CONTENT_URI_FIRST_ROW,
                projection, null, null, null);

        if(cursor == null) {
            return;
        }

        cursor.moveToFirst(); // Move the cursor to the first row (and only row, as it is now)

        boolean isItCheckedBool;

        try {
            // Gets all intervals by order from id.arrays.interval_keys and id.arrays.interval_keys_above_octave
            String[] chordKeys = MyApplication.getAppContext().getResources().getStringArray(R.array.chord_keys);
            for (int i = 0; i < chordKeys.length; i++) {
                isItCheckedBool = cursor.getInt(cursor.getColumnIndex(chordKeys[i])) == DataContract.UserPrefEntry.CHECKBOX_CHECKED;

                ChordsList.getChord(i).setIsChecked(isItCheckedBool);
            }
        } finally {
            cursor.close();
        }

        setDoChordsNeedUpdate(false);
        setDoesDbNeedUpdate(false);
    }

    public static void updateSettings() {
        String[] projection = MyApplication.getAppContext().getResources().getStringArray(R.array.preference_keys);
        String[] tempForID = { DataContract.UserPrefEntry._ID };
        projection = DataContract.concatenateTwoArrays(tempForID, projection); // projection MUST have _ID

        Cursor cursor = MyApplication.getAppContext().getContentResolver().query(DataContract.UserPrefEntry.CONTENT_URI_FIRST_ROW,
                projection, null, null, null);

        if(cursor == null) {
            return;
        }

        cursor.moveToFirst(); // Move the cursor to the first row (and only row for now)

        try {
            String[] preferenceKeys = MyApplication.getAppContext().getResources().getStringArray(R.array.preference_keys);

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

        } finally {
            cursor.close();
        }

        setDoSettingsNeedUpdate(false);
        setDoesDbNeedUpdate(false);

        DatabaseData.refreshDirectionsCount();

        // If language changes translate interval and chord names, and with that, set locale language
        IntervalsList.updateAllIntervalsNames(MyApplication.getAppContext());
        ChordsList.updateAllChordsNames(MyApplication.getAppContext());
    }

    public static void updateDatabase() {
        ContentValues values = new ContentValues();

        String[] intervalKeys = DataContract.concatenateTwoArrays(
                MyApplication.getAppContext().getResources().getStringArray(R.array.interval_keys),
                MyApplication.getAppContext().getResources().getStringArray(R.array.interval_keys_above_octave));
        for (int i = 0; i < intervalKeys.length; i++) {
            values.put(intervalKeys[i], IntervalsList.getInterval(i).getIsChecked() ? DataContract.UserPrefEntry.CHECKBOX_CHECKED
                    : DataContract.UserPrefEntry.CHECKBOX_NOT_CHECKED);
        }

        String[] chordKeys = MyApplication.getAppContext().getResources().getStringArray(R.array.chord_keys);
        for (int i = 0; i < chordKeys.length; i++) {
            values.put(chordKeys[i], ChordsList.getChord(i).getIsChecked() ? DataContract.UserPrefEntry.CHECKBOX_CHECKED
                    : DataContract.UserPrefEntry.CHECKBOX_NOT_CHECKED);
        }


        String[] preferenceKeys = MyApplication.getAppContext().getResources().getStringArray(R.array.preference_keys);
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

        int newRowUri = MyApplication.getAppContext().getContentResolver().update(DataContract.UserPrefEntry.CONTENT_URI_FIRST_ROW,
                values, null, null); // returns how many rows were affected

        setDoIntervalsNeedUpdate(false);
        setDoChordsNeedUpdate(false);
        setDoSettingsNeedUpdate(false);
        setDoesDbNeedUpdate(false);

        // If some setting is changed update interval and chord names (in case it is language), and with that, set locale language
        if(newRowUri > 0) {
            IntervalsList.updateAllIntervalsNames(MyApplication.getAppContext());
            ChordsList.updateAllChordsNames(MyApplication.getAppContext());
        }
    }

}
