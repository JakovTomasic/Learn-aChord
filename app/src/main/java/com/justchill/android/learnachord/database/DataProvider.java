package com.justchill.android.learnachord.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.justchill.android.learnachord.MyApplication;
import com.justchill.android.learnachord.R;

public class DataProvider extends ContentProvider {

//    /** Tag for the log messages */
//    public static final String LOG_TAG = DataProvider.class.getSimpleName();

    private IntervalsDbHelper mIntervalsDbHelper;
    /** URI matcher code for the content URI for the userpref table */
    private static final int USERPREFS = 100;

//    /** URI matcher code for the content URI for a single pet in the pets table */
//    private static final int PET_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

//        sUriMatcher.addURI(DataContract.CONTENT_AUTHORITY, DataContract.PATH_USERPREF, USERPREFS); // whole table

        sUriMatcher.addURI(DataContract.CONTENT_AUTHORITY, DataContract.PATH_USERPREF + "/#" ,USERPREFS); // just one row (# == any number)
    }

//    /**
//     * The MIME type of a CONTENT_URI subdirectory of a single person. // Just examples from android contact app
//     */
//    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/contact";
//    /**
//     * The MIME type of CONTENT_URI providing a directory of people.
//     */
//    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/contact";


    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        // Make sure the variable is a global variable, so it can be referenced from other ContentProvider methods.
        mIntervalsDbHelper = new IntervalsDbHelper(getContext());

        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mIntervalsDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case USERPREFS:
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = DataContract.UserPrefEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(DataContract.UserPrefEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Return the cursor
        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case USERPREFS:
                return insertData(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    // This function is not needed
    private Uri insertData(Uri uri, ContentValues values) {
//        // Check that all data is acceptable
//        String[] intervalKeys = MyApplication.getAppContext().getResources().getStringArray(R.array.interval_keys);
//        for (int i = 0; i < intervalKeys.length; i++) {
//            Integer temp = values.getAsInteger(intervalKeys[i]);
//            if(temp == null) {
//                throw new IllegalArgumentException("Data cannot be null");
//            }
//            if(DataContract.UserPrefEntry.isCheckboxCheckedIntAcceptable(temp)) {
//                throw new IllegalArgumentException("Data cannot be any other number than CHECKBOX_CHECKED or CHECKBOX_NOT_CHECKED");
//            }
//        }
//
//        SQLiteDatabase database = mIntervalsDbHelper.getWritableDatabase();
//
//        long id = database.insert(DataContract.UserPrefEntry.TABLE_NAME, null, values);
//
//        if (id == -1) {
//            Log.e(LOG_TAG, "Failed to insert row for " + uri);
//            return null;
//        }
//
//        // Once we know the ID of the new row in the table,
//        // return the new URI with the ID appended to the end of it
//        return ContentUris.withAppendedId(uri, id);

        return uri;
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case USERPREFS:
                // For the _ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = DataContract.UserPrefEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateData(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateData(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // The values for the fields (that aren't present in the ContentValues object) will stay the same as before.

        // Check that all data is acceptable
        String[] intervalKeys = MyApplication.getAppContext().getResources().getStringArray(R.array.interval_keys);
        Integer temp;
        for (String intervalKey : intervalKeys) {
            temp = values.getAsInteger(intervalKey);
            if (temp == null) {
                throw new IllegalArgumentException("Data cannot be null");
            }
            if (!DataContract.UserPrefEntry.isCheckboxCheckedIntAcceptable(temp)) {
                throw new IllegalArgumentException("Data cannot be any other number than CHECKBOX_CHECKED or CHECKBOX_NOT_CHECKED: " + temp);
            }
        }

        String[] intervalKeysAboveOctave = MyApplication.getAppContext().getResources().getStringArray(R.array.interval_keys_above_octave);
        for (String key : intervalKeysAboveOctave) {
            temp = values.getAsInteger(key);
            if (temp == null) {
                throw new IllegalArgumentException("Data cannot be null");
            }
            if (!DataContract.UserPrefEntry.isCheckboxCheckedIntAcceptable(temp)) {
                throw new IllegalArgumentException("Data cannot be any other number than CHECKBOX_CHECKED or CHECKBOX_NOT_CHECKED: " + temp);
            }
        }

        String[] chordKeys = MyApplication.getAppContext().getResources().getStringArray(R.array.chord_keys);
        for (String key : chordKeys) {
            temp = values.getAsInteger(key);
            if (temp == null) {
                throw new IllegalArgumentException("Data cannot be null");
            }
            if (!DataContract.UserPrefEntry.isCheckboxCheckedIntAcceptable(temp)) {
                throw new IllegalArgumentException("Data cannot be any other number than CHECKBOX_CHECKED or CHECKBOX_NOT_CHECKED: " + temp);
            }
        }

        String[] preferenceKeys = MyApplication.getAppContext().getResources().getStringArray(R.array.preference_keys);
        temp = values.getAsInteger(preferenceKeys[0]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(!DataContract.UserPrefEntry.isCheckboxCheckedIntAcceptable(temp)) {
            throw new IllegalArgumentException("Data cannot be any other number than CHECKBOX_CHECKED or CHECKBOX_NOT_CHECKED: " + temp);
        }

        temp = values.getAsInteger(preferenceKeys[1]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(!DataContract.UserPrefEntry.isCheckboxCheckedIntAcceptable(temp)) {
            throw new IllegalArgumentException("Data cannot be any other number than CHECKBOX_CHECKED or CHECKBOX_NOT_CHECKED: " + temp);
        }

        temp = values.getAsInteger(preferenceKeys[2]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(!DataContract.UserPrefEntry.isCheckboxCheckedIntAcceptable(temp)) {
            throw new IllegalArgumentException("Data cannot be any other number than CHECKBOX_CHECKED or CHECKBOX_NOT_CHECKED: " + temp);
        }

        temp = values.getAsInteger(preferenceKeys[3]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(!MyApplication.isTonesSeparationTimeValid((double) temp)) {
            throw new IllegalArgumentException("Data " + temp + " is out of borders, min border: " + MyApplication.minTonesSeparationTime
                    + ", max border: " + MyApplication.maxTonesSeparationTime);
        }

        temp = values.getAsInteger(preferenceKeys[4]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(!MyApplication.isChordDurationTimeValid((double) temp)) {
            throw new IllegalArgumentException("Data " + temp + " is out of borders, min border: " + MyApplication.minChordDurationTime
                    + ", max border: " + MyApplication.maxChordDurationTime);
        }

        temp = values.getAsInteger(preferenceKeys[5]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(!DataContract.UserPrefEntry.isLanguageIntAcceptable(temp)) {
            throw new IllegalArgumentException("Data cannot be any other number than preselected Language numbers, integer " + temp + " is invalid");
        }

        temp = values.getAsInteger(preferenceKeys[6]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(temp < 1 || temp > DataContract.UserPrefEntry.NUMBER_OF_KEYS || temp > values.getAsInteger(preferenceKeys[7])) {
            throw new IllegalArgumentException("Integer " + temp + " out of borders");
        }

        temp = values.getAsInteger(preferenceKeys[7]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(temp < 1 || temp > 61) {
            throw new IllegalArgumentException("Integer " + temp + " out of borders");
        }

        temp = values.getAsInteger(preferenceKeys[8]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(!DataContract.UserPrefEntry.isCheckboxCheckedIntAcceptable(temp)) {
            throw new IllegalArgumentException("Data cannot be any other number than CHECKBOX_CHECKED or CHECKBOX_NOT_CHECKED: " + temp);
        }

        temp = values.getAsInteger(preferenceKeys[9]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(!DataContract.UserPrefEntry.isCheckboxCheckedIntAcceptable(temp)) {
            throw new IllegalArgumentException("Data cannot be any other number than CHECKBOX_CHECKED or CHECKBOX_NOT_CHECKED: " + temp);
        }

        temp = values.getAsInteger(preferenceKeys[10]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(!DataContract.UserPrefEntry.isChordTextScalingModeAcceptable(temp)) {
            throw new IllegalArgumentException("Data MyApplication.chordTextScalingMode is not any of preselected numbers, it is: " + temp);
        }

        temp = values.getAsInteger(preferenceKeys[11]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(!DataContract.UserPrefEntry.isPlayingModeIntAcceptable(temp)) {
            throw new IllegalArgumentException("Data MyApplication.playingMode is not any of preselected numbers, it is: " + temp);
        }

        temp = values.getAsInteger(preferenceKeys[12]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(temp < 0) {
            throw new IllegalArgumentException("Data " + temp + " is not valid.");
        }

        temp = values.getAsInteger(preferenceKeys[13]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(temp < 0) {
            throw new IllegalArgumentException("Data " + temp + " is not valid.");
        }

        temp = values.getAsInteger(preferenceKeys[14]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(temp < 0) {
            throw new IllegalArgumentException("Data " + temp + " is not valid.");
        }

        temp = values.getAsInteger(preferenceKeys[15]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(!DataContract.UserPrefEntry.isCheckboxCheckedIntAcceptable(temp)) {
            throw new IllegalArgumentException("Data cannot be any other number than CHECKBOX_CHECKED or CHECKBOX_NOT_CHECKED: " + temp);
        }

        temp = values.getAsInteger(preferenceKeys[16]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(!DataContract.UserPrefEntry.isCheckboxCheckedIntAcceptable(temp)) {
            throw new IllegalArgumentException("Data cannot be any other number than CHECKBOX_CHECKED or CHECKBOX_NOT_CHECKED: " + temp);
        }

        temp = values.getAsInteger(preferenceKeys[17]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(temp < 0) {
            throw new IllegalArgumentException("Mode one high score cannot be less than 0. High score: " + temp);
        }

        temp = values.getAsInteger(preferenceKeys[18]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(temp < 0) {
            throw new IllegalArgumentException("Mode two high score cannot be less than 0. High score: " + temp);
        }

        temp = values.getAsInteger(preferenceKeys[19]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(temp < 0) {
            throw new IllegalArgumentException("Mode three high score cannot be less than 0. High score: " + temp);
        }


        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Update the selected pets in the pets database table with the given ContentValues
        SQLiteDatabase database = mIntervalsDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(DataContract.UserPrefEntry.TABLE_NAME, values, selection, selectionArgs);

        if(rowsUpdated > 0) {
            MyApplication.setDoIntervalsNeedUpdate(true);
            MyApplication.setDoChordsNeedUpdate(true);
            MyApplication.setDoSettingsNeedUpdate(true);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
//        // Get write-able database
//        SQLiteDatabase database = mIntervalsDbHelper.getWritableDatabase();

        int rowsDeleted = 0;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case USERPREFS:
                // Set all values to default
                ContentValues values = new ContentValues();

                String[] intervalKeys = MyApplication.getAppContext().getResources().getStringArray(R.array.interval_keys);
                for (String intervalKey : intervalKeys) {
                    values.put(intervalKey, DataContract.UserPrefEntry.CHECKBOX_CHECKED);
                }

                String[] intervalKeysAboveOctave = MyApplication.getAppContext().getResources().getStringArray(R.array.interval_keys_above_octave);
                for (String key : intervalKeysAboveOctave) {
                    values.put(key, DataContract.UserPrefEntry.CHECKBOX_NOT_CHECKED);
                }

                String[] chordKeys = MyApplication.getAppContext().getResources().getStringArray(R.array.chord_keys);
                for (int i = 0; i < chordKeys.length; i++) {
                    if(i == 0 || i == 3 || i == 10) {
                        values.put(chordKeys[i], DataContract.UserPrefEntry.CHECKBOX_CHECKED);
                    } else {
                        values.put(chordKeys[i], DataContract.UserPrefEntry.CHECKBOX_NOT_CHECKED);
                    }
                }

                String[] preferenceKeys = MyApplication.getAppContext().getResources().getStringArray(R.array.preference_keys);
                values.put(preferenceKeys[0], DataContract.UserPrefEntry.CHECKBOX_CHECKED);
                values.put(preferenceKeys[1], DataContract.UserPrefEntry.CHECKBOX_NOT_CHECKED);
                values.put(preferenceKeys[2], DataContract.UserPrefEntry.CHECKBOX_NOT_CHECKED);
                values.put(preferenceKeys[3], DataContract.UserPrefEntry.DEFAULT_TONES_SEPARATION_TIME);
                values.put(preferenceKeys[4], DataContract.UserPrefEntry.DEFAULT_INTERVAL_DURATION_TIME);
                values.put(preferenceKeys[5], MyApplication.DEFAULT_SYSTEM_LANGUAGE);
                values.put(preferenceKeys[6], 1);
                values.put(preferenceKeys[7], DataContract.UserPrefEntry.NUMBER_OF_KEYS);
                values.put(preferenceKeys[8], DataContract.UserPrefEntry.CHECKBOX_CHECKED);
                values.put(preferenceKeys[9], DataContract.UserPrefEntry.CHECKBOX_CHECKED);
                values.put(preferenceKeys[10], DataContract.UserPrefEntry.CHORD_TEXT_SCALING_MODE_AUTO);
                values.put(preferenceKeys[11], DataContract.UserPrefEntry.PLAYING_MODE_RANDOM);
                values.put(preferenceKeys[12], DataContract.UserPrefEntry.DIRECTION_UP_VIEW_DEFAULT_INDEX);
                values.put(preferenceKeys[13], DataContract.UserPrefEntry.DIRECTION_DOWN_VIEW_DEFAULT_INDEX);
                values.put(preferenceKeys[14], DataContract.UserPrefEntry.DIRECTION_SAME_TIME_VIEW_DEFAULT_INDEX);
                values.put(preferenceKeys[15], DataContract.UserPrefEntry.CHECKBOX_NOT_CHECKED);
                values.put(preferenceKeys[16], DataContract.UserPrefEntry.CHECKBOX_NOT_CHECKED);
                // You cannot delete high scores
                values.put(preferenceKeys[17], MyApplication.quizModeOneHighscore);
                values.put(preferenceKeys[18], MyApplication.quizModeTwoHighscore);
                values.put(preferenceKeys[19], MyApplication.quizModeThreeHighscore);

                // Run update with default data (set data to default)
                rowsDeleted = update(ContentUris.withAppendedId(DataContract.UserPrefEntry.CONTENT_URI, 1), values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case USERPREFS:
                return DataContract.UserPrefEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
