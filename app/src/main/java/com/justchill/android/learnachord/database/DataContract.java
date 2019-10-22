package com.justchill.android.learnachord.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import androidx.appcompat.app.AppCompatDelegate;

import com.justchill.android.learnachord.MyApplication;
import com.justchill.android.learnachord.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Stores data for successful working with DB
public final class DataContract {

    // Path of the app
    static final String CONTENT_AUTHORITY = "com.justchill.android.learnachord";

    // Uri of the content provider
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Path or userPref database
    static final String PATH_USERPREF = "userpref";

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private DataContract() {}


    // List of data for DB columns in userprefs table
    public static class UserPrefEntry implements BaseColumns {

        /** The content URI to access the data in the provider */
        static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_USERPREF);

        // Content uri to access the first (and only) for in database
        public static final Uri CONTENT_URI_FIRST_ROW = ContentUris.withAppendedId(DataContract.UserPrefEntry.CONTENT_URI, 1);

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USERPREF;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single data.
         */
        static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USERPREF;

        // Name of the database table
        static final String TABLE_NAME = "userprefs";

        static final String _ID = BaseColumns._ID; // Unique ID for row, constant value "_id"

        // List of all column names (keys)
        public static final String[] COLUMN_NAMES = concatenateTwoArrays(
                concatenateTwoArrays(MyApplication.getAppContext().getResources().getStringArray(R.array.interval_keys),
                        MyApplication.getAppContext().getResources().getStringArray(R.array.interval_keys_above_octave)),
                concatenateTwoArrays(MyApplication.getAppContext().getResources().getStringArray(R.array.chord_keys),
                        MyApplication.getAppContext().getResources().getStringArray(R.array.preference_keys)));


        // Constants for check and unchecked state
        static final int CHECKBOX_NOT_CHECKED = 0;
        static final int CHECKBOX_CHECKED = 1;

        // Check if checked data match any of the constants (is it valid)
        static boolean isCheckboxCheckedIntAcceptable(int i) {
            return (i == CHECKBOX_CHECKED || i == CHECKBOX_NOT_CHECKED);
        }

        // Language constants
        public static final int LANGUAGE_ENGLISH = 0;
        public static final int LANGUAGE_CROATIAN = 1;

        // Language tags
        public static final String ENGLISH_LANGUAGE_TAG = "en";
        public static final String CROATIAN_LANGUAGE_TAG = "hr";

        // Check if language data match any of the constants (is it valid)
        static boolean isLanguageIntAcceptable(int i) {
            return (i == LANGUAGE_ENGLISH || i == LANGUAGE_CROATIAN);
        }


        // Constants for each tone duration
        public static final double maxTonesSeparationTime = 10000.0; // 10 sec
        public static final double minTonesSeparationTime = 0.0; // 0 sec
        // Constants for duration between playing
        public static final double maxChordDurationTime = 10000.0; // 10 sec
        public static final double minChordDurationTime = 0.0; // 0 sec

        // Default each tone duration
        static final int DEFAULT_TONES_SEPARATION_TIME = 1000; // 1 sec
        // Default delay between intervals/chords
        static final int DEFAULT_INTERVAL_DURATION_TIME = 1000; // 1 sec

        // Checks if each tone duration is valid (inside predefined borders)
        static boolean isTonesSeparationTimeValid(double d) {
            return (d <= maxTonesSeparationTime && d >= minTonesSeparationTime);
        }

        // Checks if delay between intervals/chords is valid (inside predefined borders)
        static boolean isChordDurationTimeValid(double d) {
            return (d <= maxChordDurationTime && d >= minChordDurationTime);
        }


        // Total number of keys
        public static final int NUMBER_OF_KEYS = 61;


        // Constant numbers for text scaling modes (also used for displaying order in spinner in settings)
        public static final int CHORD_TEXT_SCALING_MODE_AUTO = 0;
        public static final int CHORD_TEXT_SCALING_MODE_SMALL = 3;
        public static final int CHORD_TEXT_SCALING_MODE_NORMAL = 2;
        public static final int CHORD_TEXT_SCALING_MODE_LARGE = 1;

        // Check if chord text scaling mode data match any of the constants (is it valid)
        static boolean isChordTextScalingModeAcceptable(int i) {
            return (i == CHORD_TEXT_SCALING_MODE_AUTO || i == CHORD_TEXT_SCALING_MODE_SMALL
                    || i == CHORD_TEXT_SCALING_MODE_NORMAL || i == CHORD_TEXT_SCALING_MODE_LARGE);
        }

        // Constant numbers for text scaling modes (also used for displaying order in spinner in settings)
        public static final int REMINDER_TIME_INTERVAL_NEVER = 0;
        public static final int REMINDER_TIME_INTERVAL_HOUR = 1;
        public static final int REMINDER_TIME_INTERVAL_DAY = 2;
        public static final int REMINDER_TIME_INTERVAL_WEEK = 3;
        public static final int REMINDER_TIME_INTERVAL_MONTH = 4;

        // Check if reminder interval mode data match any of the constants (is it valid)
        static boolean isReminderTimeModeAcceptable(int i) {
            return (i == REMINDER_TIME_INTERVAL_NEVER || i == REMINDER_TIME_INTERVAL_HOUR || i == REMINDER_TIME_INTERVAL_DAY
                    || i == REMINDER_TIME_INTERVAL_WEEK || i == REMINDER_TIME_INTERVAL_MONTH);
        }

        // Constant for random playing mode
        public static final int PLAYING_MODE_RANDOM = 0;
        // Constant for custom playing mode
        public static final int PLAYING_MODE_CUSTOM = 1;

        // Check if playing mode data match any of the constants (is it valid)
        static boolean isPlayingModeIntAcceptable(int i) {
            return (i == PLAYING_MODE_RANDOM || i == PLAYING_MODE_CUSTOM);
        }

        // Default constants for directions order in settings
        public static final int DIRECTION_UP_VIEW_DEFAULT_INDEX = 0;
        public static final int DIRECTION_DOWN_VIEW_DEFAULT_INDEX = 1;
        public static final int DIRECTION_SAME_TIME_VIEW_DEFAULT_INDEX = 2;

        // This needs to be called when some of the directions are checked or unchecked
        // Refreshes counter for number of directions checked
        public static void refreshDirectionsCount() {
            DatabaseData.directionsCount = 0;
            if(DatabaseData.directionUp) {
                DatabaseData.directionsCount++;
            }
            if(DatabaseData.directionDown) {
                DatabaseData.directionsCount++;
            }
            if(DatabaseData.directionSameTime) {
                DatabaseData.directionsCount++;
            }
        }


        // Constants for saving is has initial help dialog box been displayed
        public static final int BOOLEAN_FALSE = 0;
        public static final int BOOLEAN_TRUE = 1;

        // Check if data that is boolean saved as int valid (initial dialog box showed values)
        static boolean isBooleanDataSavedAsIntValid(int value) {
            return (value == BOOLEAN_TRUE || value == BOOLEAN_FALSE);
        }


        static final int DEFAULT_NIGHT_MODE = AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY;

        static boolean isNightModeIdValid(int value) {
            return (value == AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY ||
                    value == AppCompatDelegate.MODE_NIGHT_YES || value == AppCompatDelegate.MODE_NIGHT_NO);
        }


    }

    // Custom method to concatenate two String arrays
    static String[] concatenateTwoArrays(String[] arrayFirst, String[] arraySecond){
        // Initialize an empty list
        List<String> both = new ArrayList<>();

        // Add first array elements to list
        Collections.addAll(both,arrayFirst);

        // Add another array elements to list
        Collections.addAll(both,arraySecond);

        // Convert list to array
        // Return the result
        return both.toArray(new String[both.size()]);
    }

}
