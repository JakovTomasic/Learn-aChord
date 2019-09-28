package com.justchill.android.learnachord.database;

import android.database.Cursor;
import android.net.Uri;

import com.justchill.android.learnachord.MyApplication;
import com.justchill.android.learnachord.R;
import com.justchill.android.learnachord.firebase.FirebaseHandler;
import com.justchill.android.learnachord.intervalOrChord.ChordsList;
import com.justchill.android.learnachord.intervalOrChord.IntervalsList;

import java.util.ArrayList;
import java.util.Collections;

// Old ways to communicating to old databases, needed when DB is updating
class LegacyDatabaseHandler {

    /*
     * DB version 11
     */

    // Set data to all options that is stored in database (except intervals and chords, they are handled separately)
    static void updateSettingsDbV11() {
        // Get list of database IDs for all options (saved user preferences)
        String[] projection = MyApplication.getAppContext().getResources().getStringArray(R.array.preference_keys);

        ArrayList<String> newArr = new ArrayList<>();
        Collections.addAll(newArr, projection);
        newArr.remove(newArr.size()-1);
        newArr.remove(newArr.size()-1);
        newArr.remove(newArr.size()-1);

        projection = newArr.toArray(new String[0]);

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


        } finally {
            // At the end close connection to DB
            cursor.close();
        }

        // Refresh counter for number of checked directions (in case it has been changed)
        DataContract.UserPrefEntry.refreshDirectionsCount();

        // If language changes translate interval and chord names, and with that, set locale language
        IntervalsList.updateAllIntervalsNames(MyApplication.getAppContext());
        ChordsList.updateAllChordsNames(MyApplication.getAppContext());
    }

}
