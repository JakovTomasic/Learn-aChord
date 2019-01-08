package com.justchill.android.learnachord.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import com.justchill.android.learnachord.MyApplication;
import com.justchill.android.learnachord.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DataContract {

    public static final String CONTENT_AUTHORITY = "com.justchill.android.learnachord";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_USERPREF = "userpref";

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private DataContract() {}


    public static class UserPrefEntry implements BaseColumns {

        /** The content URI to access the data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_USERPREF);

        public static final Uri CONTENT_URI_FIRST_ROW = ContentUris.withAppendedId(DataContract.UserPrefEntry.CONTENT_URI, 1);

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USERPREF;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single data.
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USERPREF;

        public static final String TABLE_NAME = "userprefs";

        public static final String _ID = BaseColumns._ID; // Unique ID for row, constant value "_id"

        public static final String[] COLUMN_NAMES = concatenateTwoArrays(
                concatenateTwoArrays(MyApplication.getAppContext().getResources().getStringArray(R.array.interval_keys),
                        MyApplication.getAppContext().getResources().getStringArray(R.array.interval_keys_above_octave)),
                concatenateTwoArrays(MyApplication.getAppContext().getResources().getStringArray(R.array.chord_keys),
                        MyApplication.getAppContext().getResources().getStringArray(R.array.preference_keys)));

        /**
         * Possible values for the interval.
         */
        public static final int CHECKBOX_NOT_CHECKED = 0;
        public static final int CHECKBOX_CHECKED = 1;

        public static boolean isCheckboxCheckedIntAcceptable(int i) {
            return (i == CHECKBOX_CHECKED || i == CHECKBOX_NOT_CHECKED);
        }

        public static final int LANGUAGE_ENGLISH = 0;
        public static final int LANGUAGE_CROATIAN = 1;

        public static final String ENGLISH_LANGUAGE_TAG = "en";
        public static final String CROATIAN_LANGUAGE_TAG = "hr";

        public static boolean isLanguageIntAcceptable(int i) {
            return (i == LANGUAGE_ENGLISH || i == LANGUAGE_CROATIAN);
        }

        public static final int DEFAULT_TONES_SEPARATION_TIME = 1000;
        public static final int DEFAULT_INTERVAL_DURATION_TIME = 1000;

        public static final int NUMBER_OF_KEYS = 61;


        public static final int CHORD_TEXT_SCALING_MODE_AUTO = 0;
        public static final int CHORD_TEXT_SCALING_MODE_SMALL = 3;
        public static final int CHORD_TEXT_SCALING_MODE_NORMAL = 2;
        public static final int CHORD_TEXT_SCALING_MODE_LARGE = 1;

        public static boolean isChordTextScalingModeAcceptable(int i) {
            return (i == CHORD_TEXT_SCALING_MODE_AUTO || i == CHORD_TEXT_SCALING_MODE_SMALL
                    || i == CHORD_TEXT_SCALING_MODE_NORMAL || i == CHORD_TEXT_SCALING_MODE_LARGE);
        }

        public static final int PLAYING_MODE_RANDOM = 0;
        public static final int PLAYING_MODE_CUSTOM = 1;

        public static boolean isPlayingModeIntAcceptable(int i) {
            return (i == PLAYING_MODE_RANDOM || i == PLAYING_MODE_CUSTOM);
        }

        public static final int DIRECTION_UP_VIEW_DEFAULT_INDEX = 0;
        public static final int DIRECTION_DOWN_VIEW_DEFAULT_INDEX = 1;
        public static final int DIRECTION_SAME_TIME_VIEW_DEFAULT_INDEX = 2;

    }

    // Custom method to concatenate two String arrays
    public static String[] concatenateTwoArrays(String[] arrayFirst,String[] arraySecond){
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
