package com.justchill.android.learnachord.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.justchill.android.learnachord.MyApplication;
import com.justchill.android.learnachord.R;
import com.justchill.android.learnachord.firebase.FirebaseHandler;

// Handles creating and updating (to newer version, on some app updates) database
public class IntervalsDbHelper extends SQLiteOpenHelper {

    /**
     * database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 13;
    private static final String DATABASE_NAME = "settings.db";

    /**
     * Constructs a new instance of {@link IntervalsDbHelper}.
     *
     * @param context of the app
     */
    // Was public, warning error sad to remove it
    IntervalsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create a table
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("CREATE TABLE " + DataContract.UserPrefEntry.TABLE_NAME + " (");
        stringBuilder.append(DataContract.UserPrefEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, ");

        String endString = ", ";

        // Add intervals below octave
        String[] intervalKeys = MyApplication.getAppContext().getResources().getStringArray(R.array.interval_keys);
        for (String key : intervalKeys) {
            stringBuilder.append(key).append(" INTEGER NOT NULL DEFAULT ")
                    .append(DataContract.UserPrefEntry.CHECKBOX_CHECKED).append(endString);
        }

        // Add intervals above octave
        String[] intervalKeysAboveOctave = MyApplication.getAppContext().getResources().getStringArray(R.array.interval_keys_above_octave);
        for (String key : intervalKeysAboveOctave) {
            stringBuilder.append(key).append(" INTEGER NOT NULL DEFAULT ")
                    .append(DataContract.UserPrefEntry.CHECKBOX_NOT_CHECKED).append(endString);
        }

        // Add chords
        String[] chordKeys = MyApplication.getAppContext().getResources().getStringArray(R.array.chord_keys);
        for (int i = 0; i < chordKeys.length; i++) {
            stringBuilder.append(chordKeys[i]).append(" INTEGER NOT NULL DEFAULT ");

            // Be careful if order of keys are changed
            if(i == 0 || i == 3 || i == 10) {
                stringBuilder.append(DataContract.UserPrefEntry.CHECKBOX_CHECKED);
            } else {
                stringBuilder.append(DataContract.UserPrefEntry.CHECKBOX_NOT_CHECKED);
            }

            stringBuilder.append(endString);
        }

        // Fix after removing veliki_povecani_kvintsekstakord (removing it from DB was hard, and it was late, ok?)
        stringBuilder.append("veliki_povecani_kvintsekstakord INTEGER NOT NULL DEFAULT 0").append(endString);


        // Add preferences
        String[] preferenceKeys = MyApplication.getAppContext().getResources().getStringArray(R.array.preference_keys);

        stringBuilder.append(preferenceKeys[0]).append(" INTEGER NOT NULL DEFAULT ")
                .append(DataContract.UserPrefEntry.CHECKBOX_CHECKED).append(endString);
        stringBuilder.append(preferenceKeys[1]).append(" INTEGER NOT NULL DEFAULT ")
                .append(DataContract.UserPrefEntry.CHECKBOX_NOT_CHECKED).append(endString);
        stringBuilder.append(preferenceKeys[2]).append(" INTEGER NOT NULL DEFAULT ")
                .append(DataContract.UserPrefEntry.CHECKBOX_NOT_CHECKED).append(endString);
        stringBuilder.append(preferenceKeys[3]).append(" INTEGER NOT NULL DEFAULT ")
                .append(DataContract.UserPrefEntry.DEFAULT_TONES_SEPARATION_TIME).append(endString);
        stringBuilder.append(preferenceKeys[4]).append(" INTEGER NOT NULL DEFAULT ")
                .append(DataContract.UserPrefEntry.DEFAULT_INTERVAL_DURATION_TIME).append(endString);
        stringBuilder.append(preferenceKeys[5]).append(" INTEGER NOT NULL DEFAULT ")
                .append(DataContract.UserPrefEntry.LANGUAGE_ENGLISH).append(endString);
        stringBuilder.append(preferenceKeys[6]).append(" INTEGER NOT NULL DEFAULT ")
                .append(1).append(endString);
        stringBuilder.append(preferenceKeys[7]).append(" INTEGER NOT NULL DEFAULT ")
                .append(DataContract.UserPrefEntry.NUMBER_OF_KEYS).append(endString);
        stringBuilder.append(preferenceKeys[8]).append(" INTEGER NOT NULL DEFAULT ")
                .append(DataContract.UserPrefEntry.CHECKBOX_CHECKED).append(endString);
        stringBuilder.append(preferenceKeys[9]).append(" INTEGER NOT NULL DEFAULT ")
                .append(DataContract.UserPrefEntry.CHECKBOX_CHECKED).append(endString);
        stringBuilder.append(preferenceKeys[10]).append(" INTEGER NOT NULL DEFAULT ")
                .append(DataContract.UserPrefEntry.CHORD_TEXT_SCALING_MODE_AUTO).append(endString);
        stringBuilder.append(preferenceKeys[11]).append(" INTEGER NOT NULL DEFAULT ")
                .append(DataContract.UserPrefEntry.PLAYING_MODE_RANDOM).append(endString);
        stringBuilder.append(preferenceKeys[12]).append(" INTEGER NOT NULL DEFAULT ")
                .append(DataContract.UserPrefEntry.DIRECTION_UP_VIEW_DEFAULT_INDEX).append(endString);
        stringBuilder.append(preferenceKeys[13]).append(" INTEGER NOT NULL DEFAULT ")
                .append(DataContract.UserPrefEntry.DIRECTION_DOWN_VIEW_DEFAULT_INDEX).append(endString);
        stringBuilder.append(preferenceKeys[14]).append(" INTEGER NOT NULL DEFAULT ")
                .append(DataContract.UserPrefEntry.DIRECTION_SAME_TIME_VIEW_DEFAULT_INDEX).append(endString);
        stringBuilder.append(preferenceKeys[15]).append(" INTEGER NOT NULL DEFAULT ")
                .append(DataContract.UserPrefEntry.CHECKBOX_NOT_CHECKED).append(endString);
        stringBuilder.append(preferenceKeys[16]).append(" INTEGER NOT NULL DEFAULT ")
                .append(DataContract.UserPrefEntry.CHECKBOX_NOT_CHECKED).append(endString);
        stringBuilder.append(preferenceKeys[17]).append(" INTEGER NOT NULL DEFAULT ")
                .append(0).append(endString);
        stringBuilder.append(preferenceKeys[18]).append(" INTEGER NOT NULL DEFAULT ")
                .append(0).append(endString);
        stringBuilder.append(preferenceKeys[19]).append(" INTEGER NOT NULL DEFAULT ")
                .append(0).append(endString);
        stringBuilder.append(preferenceKeys[20]).append(" INTEGER NOT NULL DEFAULT ")
                .append(FirebaseHandler.IMAGE_TO_SET_DEFAULT_ID).append(endString);
        stringBuilder.append(preferenceKeys[21]).append(" TEXT").append(endString);
        stringBuilder.append(preferenceKeys[22]).append(" INTEGER NOT NULL DEFAULT ")
                .append(DataContract.UserPrefEntry.BOOLEAN_FALSE).append(endString);
        stringBuilder.append(preferenceKeys[23]).append(" INTEGER NOT NULL DEFAULT ")
                .append(DataContract.UserPrefEntry.BOOLEAN_FALSE).append(endString);
        stringBuilder.append(preferenceKeys[24]).append(" INTEGER NOT NULL DEFAULT ")
                .append(DataContract.UserPrefEntry.BOOLEAN_FALSE).append(endString);
        stringBuilder.append(preferenceKeys[25]).append(" INTEGER NOT NULL DEFAULT ")
                .append(DataContract.UserPrefEntry.BOOLEAN_FALSE).append(endString);
        stringBuilder.append(preferenceKeys[26]).append(" INTEGER NOT NULL DEFAULT ")
                .append(DataContract.UserPrefEntry.BOOLEAN_FALSE).append(endString);
        stringBuilder.append(preferenceKeys[27]).append(" INTEGER NOT NULL DEFAULT ")
                .append(1).append(endString);
        stringBuilder.append(preferenceKeys[28]).append(" INTEGER NOT NULL DEFAULT ")
                .append(DataContract.UserPrefEntry.REMINDER_TIME_INTERVAL_WEEK).append(endString);
        stringBuilder.append(preferenceKeys[29]).append(" INTEGER NOT NULL DEFAULT ")
                .append(0).append(endString);
        stringBuilder.append(preferenceKeys[30]).append(" INTEGER NOT NULL DEFAULT ")
                .append(DataContract.UserPrefEntry.DEFAULT_NIGHT_MODE).append(endString);


        // Add achievement progress
        String[] achievementProgressKeys = MyApplication.getAppContext().getResources().getStringArray(R.array.achievement_progress_keys);
        for (int i = 0; i < achievementProgressKeys.length; i++) {
            if(i >= achievementProgressKeys.length-1) {
                // After last key set sql ending string
                endString = ");";
            }
            stringBuilder.append(achievementProgressKeys[i]).append(" INTEGER NOT NULL DEFAULT ")
                    .append(0).append(endString);
        }



        String SQL_CREATE_INTERVALS_TABLE = stringBuilder.toString();
        // Execute the SQL statement
        db.execSQL(SQL_CREATE_INTERVALS_TABLE);




        // Add first row, all interval values are default
        stringBuilder = new StringBuilder();
        stringBuilder.append("INSERT INTO " + DataContract.UserPrefEntry.TABLE_NAME + " (");

        endString = ", ";

        for (String key : intervalKeys) {
            stringBuilder.append(key).append(endString);
        }

        for (String key : intervalKeysAboveOctave) {
            stringBuilder.append(key).append(endString);
        }

        for (String key : chordKeys) {
            stringBuilder.append(key).append(endString);
        }

        // Fix after removing veliki_povecani_kvintsekstakord (removing it from DB was hard, and it was late, ok?)
        stringBuilder.append("veliki_povecani_kvintsekstakord").append(endString);


        for (int i = 0; i < preferenceKeys.length; i++) {
            // Column at 21 won't be added (is null by default)
            if(i != 21) {
                stringBuilder.append(preferenceKeys[i]).append(endString);
            }
        }

        for (int i = 0; i < achievementProgressKeys.length; i++) {
            if(i >= achievementProgressKeys.length-1) {
                endString = ")";
            }
            stringBuilder.append(achievementProgressKeys[i]).append(endString);
        }

        stringBuilder.append(" VALUES (");

        endString = ", ";

        for (String key : intervalKeys) {
            stringBuilder.append(DataContract.UserPrefEntry.CHECKBOX_CHECKED).append(endString);
        }

        for (String key : intervalKeysAboveOctave) {
            stringBuilder.append(DataContract.UserPrefEntry.CHECKBOX_NOT_CHECKED).append(endString);
        }

        for (int i = 0; i < chordKeys.length; i++) {
            // Be careful if order of keys are changed
            if(i == 0 || i == 3 || i == 10) {
                stringBuilder.append(DataContract.UserPrefEntry.CHECKBOX_CHECKED).append(endString);
            } else {
                stringBuilder.append(DataContract.UserPrefEntry.CHECKBOX_NOT_CHECKED).append(endString);
            }
        }

        // Fix after removing veliki_povecani_kvintsekstakord (removing it from DB was hard, and it was late, ok?)
        stringBuilder.append(DataContract.UserPrefEntry.CHECKBOX_NOT_CHECKED).append(endString);


        stringBuilder.append(DataContract.UserPrefEntry.CHECKBOX_CHECKED).append(endString);
        stringBuilder.append(DataContract.UserPrefEntry.CHECKBOX_NOT_CHECKED).append(endString);
        stringBuilder.append(DataContract.UserPrefEntry.CHECKBOX_NOT_CHECKED).append(endString);
        stringBuilder.append(DataContract.UserPrefEntry.DEFAULT_TONES_SEPARATION_TIME).append(endString);
        stringBuilder.append(DataContract.UserPrefEntry.DEFAULT_INTERVAL_DURATION_TIME).append(endString);
        stringBuilder.append(DatabaseData.DEFAULT_SYSTEM_LANGUAGE).append(endString);
        stringBuilder.append(1).append(endString);
        stringBuilder.append(DataContract.UserPrefEntry.NUMBER_OF_KEYS).append(endString);
        stringBuilder.append(DataContract.UserPrefEntry.CHECKBOX_CHECKED).append(endString);
        stringBuilder.append(DataContract.UserPrefEntry.CHECKBOX_CHECKED).append(endString);
        stringBuilder.append(DataContract.UserPrefEntry.CHORD_TEXT_SCALING_MODE_AUTO).append(endString);
        stringBuilder.append(DataContract.UserPrefEntry.PLAYING_MODE_RANDOM).append(endString);
        stringBuilder.append(DataContract.UserPrefEntry.DIRECTION_UP_VIEW_DEFAULT_INDEX).append(endString);
        stringBuilder.append(DataContract.UserPrefEntry.DIRECTION_DOWN_VIEW_DEFAULT_INDEX).append(endString);
        stringBuilder.append(DataContract.UserPrefEntry.DIRECTION_SAME_TIME_VIEW_DEFAULT_INDEX).append(endString);
        stringBuilder.append(DataContract.UserPrefEntry.CHECKBOX_NOT_CHECKED).append(endString);
        stringBuilder.append(DataContract.UserPrefEntry.CHECKBOX_NOT_CHECKED).append(endString);
        stringBuilder.append(0).append(endString);
        stringBuilder.append(0).append(endString);
        stringBuilder.append(0).append(endString);
        stringBuilder.append(FirebaseHandler.IMAGE_TO_SET_DEFAULT_ID).append(endString);
        // Empty row, path to profile photo from gallery is null by default
        stringBuilder.append(DataContract.UserPrefEntry.BOOLEAN_FALSE).append(endString);
        stringBuilder.append(DataContract.UserPrefEntry.BOOLEAN_FALSE).append(endString);
        stringBuilder.append(DataContract.UserPrefEntry.BOOLEAN_FALSE).append(endString);
        stringBuilder.append(DataContract.UserPrefEntry.BOOLEAN_FALSE).append(endString);
        stringBuilder.append(DataContract.UserPrefEntry.BOOLEAN_FALSE).append(endString);
        stringBuilder.append(1).append(endString);
        stringBuilder.append(DataContract.UserPrefEntry.REMINDER_TIME_INTERVAL_WEEK).append(endString);
        stringBuilder.append(System.currentTimeMillis()).append(endString);
        stringBuilder.append(DataContract.UserPrefEntry.DEFAULT_NIGHT_MODE).append(endString);

        for (int i = 0; i < achievementProgressKeys.length; i++) {
            if(i >= achievementProgressKeys.length-1) {
                // After last key set sql ending string
                endString = ");";
            }
            stringBuilder.append(0).append(endString);
        }

        String SQL_ADD_FIRST_ROW = stringBuilder.toString();
        // Execute the SQL statement
        db.execSQL(SQL_ADD_FIRST_ROW);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This deletes all userPref on DB update and then sets them to new default values

        // Get titles of all column names from settings
        String[] preferenceKeys = MyApplication.getAppContext().getResources().getStringArray(R.array.preference_keys);

        // Added reminder notification to DB version 12
        if(oldVersion < 12) {
            db.execSQL("ALTER TABLE " + DataContract.UserPrefEntry.TABLE_NAME + " ADD COLUMN " + preferenceKeys[27] + " INTEGER NOT NULL DEFAULT 1;");
            db.execSQL("ALTER TABLE " + DataContract.UserPrefEntry.TABLE_NAME + " ADD COLUMN " + preferenceKeys[28] + " INTEGER NOT NULL DEFAULT " + DataContract.UserPrefEntry.REMINDER_TIME_INTERVAL_WEEK + ";");
            db.execSQL("ALTER TABLE " + DataContract.UserPrefEntry.TABLE_NAME + " ADD COLUMN " + preferenceKeys[29] + " INTEGER NOT NULL DEFAULT 0;");
        }
        if(oldVersion < 13) {
            // In db version 13 veliki_povecani_kvintsekstakord was removed, but it is late so it's handled programmatically
            db.execSQL("ALTER TABLE " + DataContract.UserPrefEntry.TABLE_NAME + " ADD COLUMN " + preferenceKeys[30] + " INTEGER NOT NULL DEFAULT " + DataContract.UserPrefEntry.DEFAULT_NIGHT_MODE + ";");
        }

    }

}
