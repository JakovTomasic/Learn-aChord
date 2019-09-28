package com.justchill.android.learnachord.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.justchill.android.learnachord.MyApplication;
import com.justchill.android.learnachord.R;
import com.justchill.android.learnachord.firebase.FirebaseHandler;
import com.justchill.android.learnachord.firebase.User;


// Data provider - declares all actions that can be called through provider
public class DataProvider extends ContentProvider {

//    /** Tag for the log messages */
//    public static final String LOG_TAG = DataProvider.class.getSimpleName();

    // Database helper
    private IntervalsDbHelper mIntervalsDbHelper;
    /** URI matcher code for the content URI for the userpref table */
    private static final int USERPREFS = 100; // Constant to get just one row of table

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

                // Returns a cursor containing that row of the table.
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
     * This is not used because DB table has only one row.
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
                if(contentValues.size() > 1) return updateData(uri, contentValues, selection, selectionArgs);
                else return setLastTimeAppUsed(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    // Writes to database just last time app has been used in millis
    private int setLastTimeAppUsed(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // Get all options' / user preferences' keys (column names)
        String[] preferenceKeys = MyApplication.getAppContext().getResources().getStringArray(R.array.preference_keys);

        // Stores time in milliseconds of last time user has used he app
        Integer temp = values.getAsInteger(preferenceKeys[29]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(temp < 1000) {
            throw new IllegalArgumentException("Last time app has been used in millis data not valid: " + temp);
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Get database to update it
        SQLiteDatabase database = mIntervalsDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        // Return the number of rows updated
        return database.update(DataContract.UserPrefEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    // Writes all data to database
    private int updateData(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // The values for the fields (that aren't present in the ContentValues object) will stay the same as before.

        /*
         * Check that all data is acceptable (valid)
         */

        // Get all intervals' keys (all intervals under the octave, including octave) (column names)
        String[] intervalKeys = MyApplication.getAppContext().getResources().getStringArray(R.array.interval_keys);
        Integer temp;
        // Loop through all that data in ContentValues and check if it is valid
        for (String intervalKey : intervalKeys) {
            temp = values.getAsInteger(intervalKey);
            if (temp == null) {
                throw new IllegalArgumentException("Data cannot be null");
            }
            if (!DataContract.UserPrefEntry.isCheckboxCheckedIntAcceptable(temp)) {
                throw new IllegalArgumentException("Data cannot be any other number than CHECKBOX_CHECKED or CHECKBOX_NOT_CHECKED: " + temp);
            }
        }

        // Get all intervals' keys (all intervals above the octave) (column names)
        String[] intervalKeysAboveOctave = MyApplication.getAppContext().getResources().getStringArray(R.array.interval_keys_above_octave);
        // Loop through all that data in ContentValues and check if it is valid
        for (String key : intervalKeysAboveOctave) {
            temp = values.getAsInteger(key);
            if (temp == null) {
                throw new IllegalArgumentException("Data cannot be null");
            }
            if (!DataContract.UserPrefEntry.isCheckboxCheckedIntAcceptable(temp)) {
                throw new IllegalArgumentException("Data cannot be any other number than CHECKBOX_CHECKED or CHECKBOX_NOT_CHECKED: " + temp);
            }
        }

        // Get all chords' keys (column names)
        String[] chordKeys = MyApplication.getAppContext().getResources().getStringArray(R.array.chord_keys);
        // Loop through all chords' data in ContentValues and check if it is valid
        for (String key : chordKeys) {
            temp = values.getAsInteger(key);
            if (temp == null) {
                throw new IllegalArgumentException("Data cannot be null");
            }
            if (!DataContract.UserPrefEntry.isCheckboxCheckedIntAcceptable(temp)) {
                throw new IllegalArgumentException("Data cannot be any other number than CHECKBOX_CHECKED or CHECKBOX_NOT_CHECKED: " + temp);
            }
        }

        // Get all options' / user preferences' keys (column names)
        String[] preferenceKeys = MyApplication.getAppContext().getResources().getStringArray(R.array.preference_keys);

        /*
         * Go through all that data in ContentValues and check if it is valid
         */

        // Direction up
        temp = values.getAsInteger(preferenceKeys[0]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(!DataContract.UserPrefEntry.isCheckboxCheckedIntAcceptable(temp)) {
            throw new IllegalArgumentException("Data cannot be any other number than CHECKBOX_CHECKED or CHECKBOX_NOT_CHECKED: " + temp);
        }

        // Direction down
        temp = values.getAsInteger(preferenceKeys[1]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(!DataContract.UserPrefEntry.isCheckboxCheckedIntAcceptable(temp)) {
            throw new IllegalArgumentException("Data cannot be any other number than CHECKBOX_CHECKED or CHECKBOX_NOT_CHECKED: " + temp);
        }

        // Direction same time
        temp = values.getAsInteger(preferenceKeys[2]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(!DataContract.UserPrefEntry.isCheckboxCheckedIntAcceptable(temp)) {
            throw new IllegalArgumentException("Data cannot be any other number than CHECKBOX_CHECKED or CHECKBOX_NOT_CHECKED: " + temp);
        }

        // Each tone duration
        temp = values.getAsInteger(preferenceKeys[3]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(!DataContract.UserPrefEntry.isTonesSeparationTimeValid((double) temp)) {
            throw new IllegalArgumentException("Data " + temp + " is out of borders, min border: " + DataContract.UserPrefEntry.minTonesSeparationTime
                    + ", max border: " + DataContract.UserPrefEntry.maxTonesSeparationTime);
        }

        // Delay between intervals/chords
        temp = values.getAsInteger(preferenceKeys[4]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(!DataContract.UserPrefEntry.isChordDurationTimeValid((double) temp)) {
            throw new IllegalArgumentException("Data " + temp + " is out of borders, min border: " + DataContract.UserPrefEntry.minChordDurationTime
                    + ", max border: " + DataContract.UserPrefEntry.maxChordDurationTime);
        }

        // Language
        temp = values.getAsInteger(preferenceKeys[5]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(!DataContract.UserPrefEntry.isLanguageIntAcceptable(temp)) {
            throw new IllegalArgumentException("Data cannot be any other number than preselected Language numbers, integer " + temp + " is invalid");
        }

        // Down key border
        temp = values.getAsInteger(preferenceKeys[6]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(temp < 1 || temp > DataContract.UserPrefEntry.NUMBER_OF_KEYS || temp > values.getAsInteger(preferenceKeys[7])) {
            throw new IllegalArgumentException("Integer " + temp + " out of borders");
        }

        // Up key border
        temp = values.getAsInteger(preferenceKeys[7]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(temp < 1 || temp > 61) {
            throw new IllegalArgumentException("Integer " + temp + " out of borders");
        }

        // Show progress bar
        temp = values.getAsInteger(preferenceKeys[8]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(!DataContract.UserPrefEntry.isCheckboxCheckedIntAcceptable(temp)) {
            throw new IllegalArgumentException("Data cannot be any other number than CHECKBOX_CHECKED or CHECKBOX_NOT_CHECKED: " + temp);
        }

        // Show list of intervals in playing chord
        temp = values.getAsInteger(preferenceKeys[9]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(!DataContract.UserPrefEntry.isCheckboxCheckedIntAcceptable(temp)) {
            throw new IllegalArgumentException("Data cannot be any other number than CHECKBOX_CHECKED or CHECKBOX_NOT_CHECKED: " + temp);
        }

        // Interval/Chord/Tone text scaling mode
        temp = values.getAsInteger(preferenceKeys[10]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(!DataContract.UserPrefEntry.isChordTextScalingModeAcceptable(temp)) {
            throw new IllegalArgumentException("Data MyApplication.chordTextScalingMode is not any of preselected numbers, it is: " + temp);
        }

        // Playing mode
        temp = values.getAsInteger(preferenceKeys[11]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(!DataContract.UserPrefEntry.isPlayingModeIntAcceptable(temp)) {
            throw new IllegalArgumentException("Data MyApplication.playingMode is not any of preselected numbers, it is: " + temp);
        }

        // Direction up order index
        temp = values.getAsInteger(preferenceKeys[12]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(temp < 0) {
            throw new IllegalArgumentException("Data " + temp + " is not valid.");
        }

        // Direction down order index
        temp = values.getAsInteger(preferenceKeys[13]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(temp < 0) {
            throw new IllegalArgumentException("Data " + temp + " is not valid.");
        }

        // Direction same time order index
        temp = values.getAsInteger(preferenceKeys[14]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(temp < 0) {
            throw new IllegalArgumentException("Data " + temp + " is not valid.");
        }

        // Play what tone
        temp = values.getAsInteger(preferenceKeys[15]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(!DataContract.UserPrefEntry.isCheckboxCheckedIntAcceptable(temp)) {
            throw new IllegalArgumentException("Data cannot be any other number than CHECKBOX_CHECKED or CHECKBOX_NOT_CHECKED: " + temp);
        }

        // Play what octave
        temp = values.getAsInteger(preferenceKeys[16]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(!DataContract.UserPrefEntry.isCheckboxCheckedIntAcceptable(temp)) {
            throw new IllegalArgumentException("Data cannot be any other number than CHECKBOX_CHECKED or CHECKBOX_NOT_CHECKED: " + temp);
        }

        // Quiz mode one high score
        temp = values.getAsInteger(preferenceKeys[17]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(temp < 0) {
            throw new IllegalArgumentException("Mode one high score cannot be less than 0. High score: " + temp);
        }

        // Quiz mode two high score
        temp = values.getAsInteger(preferenceKeys[18]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(temp < 0) {
            throw new IllegalArgumentException("Mode two high score cannot be less than 0. High score: " + temp);
        }

        // Quiz mode three high score
        temp = values.getAsInteger(preferenceKeys[19]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(temp < 0) {
            throw new IllegalArgumentException("Mode three high score cannot be less than 0. High score: " + temp);
        }

        // Number of profile image to show
        temp = values.getAsInteger(preferenceKeys[20]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(!FirebaseHandler.isImageToSetValueValid(temp)) {
            throw new IllegalArgumentException("Given data not valid for variable FirebaseHandler.imageToSet: " + temp);
        }

        // At index 21 - gallery path to profile photo - don't need to be checked

        // Has login help dialog been displayed
        temp = values.getAsInteger(preferenceKeys[22]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(!DataContract.UserPrefEntry.isBooleanDataSavedAsIntValid(temp)) {
            throw new IllegalArgumentException("Given data not valid for DatabaseData BOOLEAN data saved as int: " + temp);
        }

        // Has main activity help dialog been displayed
        temp = values.getAsInteger(preferenceKeys[23]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(!DataContract.UserPrefEntry.isBooleanDataSavedAsIntValid(temp)) {
            throw new IllegalArgumentException("Given data not valid for DatabaseData BOOLEAN data saved as int: " + temp);
        }

        // Has settings activity help dialog been displayed
        temp = values.getAsInteger(preferenceKeys[24]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(!DataContract.UserPrefEntry.isBooleanDataSavedAsIntValid(temp)) {
            throw new IllegalArgumentException("Given data not valid for DatabaseData BOOLEAN data saved as int: " + temp);
        }

        // Has quiz activity help dialog been displayed
        temp = values.getAsInteger(preferenceKeys[25]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(!DataContract.UserPrefEntry.isBooleanDataSavedAsIntValid(temp)) {
            throw new IllegalArgumentException("Given data not valid for DatabaseData BOOLEAN data saved as int: " + temp);
        }

        // Has user profile activity help dialog been displayed
        temp = values.getAsInteger(preferenceKeys[26]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(!DataContract.UserPrefEntry.isBooleanDataSavedAsIntValid(temp)) {
            throw new IllegalArgumentException("Given data not valid for DatabaseData BOOLEAN data saved as int: " + temp);
        }

        // Number of hours/days/weeks/months after witch to remind user to use app
        temp = values.getAsInteger(preferenceKeys[27]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(temp < 1) {
            throw new IllegalArgumentException("Reminder time interval number data invalid: " + temp);
        }

        // Reminder interval number multiplier, to show reminders every n hours/days/weeks/months
        temp = values.getAsInteger(preferenceKeys[28]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(!DataContract.UserPrefEntry.isReminderTimeModeAcceptable(temp)) {
            throw new IllegalArgumentException("Reminder time interval mode data invalid: " + temp);
        }

        // Stores time in milliseconds of last time user has used he app
        temp = values.getAsInteger(preferenceKeys[29]);
        if(temp == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if(temp < 1000) {
            throw new IllegalArgumentException("Last time app has been used in millis data not valid: " + temp);
        }



        // Get all achievement' progress keys (column names)
        String[] achievementProgressKeys = MyApplication.getAppContext().getResources().getStringArray(R.array.achievement_progress_keys);
        // Loop through all that data in ContentValues and check if it is valid
        for (String progressKey : achievementProgressKeys) {
            temp = values.getAsInteger(progressKey);
            if (temp == null) {
                throw new IllegalArgumentException("Data cannot be null");
            }
            if (!User.isAchievementProgressValid(temp)) {
                throw new IllegalArgumentException("Data not valid: " + temp);
            }
        }


        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Get database to update it
        SQLiteDatabase database = mIntervalsDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        // Return the number of rows updated
        return database.update(DataContract.UserPrefEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
//        // Get write-able database
//        SQLiteDatabase database = mIntervalsDbHelper.getWritableDatabase();

        int rowsDeleted = 0;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case USERPREFS:

                /*
                 * Set all values to default
                 */

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
                values.put(preferenceKeys[5], DatabaseData.DEFAULT_SYSTEM_LANGUAGE);
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
                // Don't delete high scores and order number of user image to show
                values.put(preferenceKeys[17], DatabaseData.quizModeOneHighscore);
                values.put(preferenceKeys[18], DatabaseData.quizModeTwoHighscore);
                values.put(preferenceKeys[19], DatabaseData.quizModeThreeHighscore);
                values.put(preferenceKeys[20], FirebaseHandler.imageToSet);
                // Empty row, path to profile photo from gallery is null by default
                values.put(preferenceKeys[22], DatabaseData.logInHelpShowed);
                values.put(preferenceKeys[23], DatabaseData.mainActivityHelpShowed);
                values.put(preferenceKeys[24], DatabaseData.settingsActivityHelpShowed);
                values.put(preferenceKeys[25], DatabaseData.quizActivityHelpShowed);
                values.put(preferenceKeys[26], DatabaseData.userProfileActivityHelpShowed);
                values.put(preferenceKeys[27], 1);
                values.put(preferenceKeys[28], DataContract.UserPrefEntry.REMINDER_TIME_INTERVAL_WEEK);
                values.put(preferenceKeys[29], DatabaseData.lastTimeAppUsedInMillis = System.currentTimeMillis());

                String[] achievementProgressKeys = MyApplication.getAppContext().getResources().getStringArray(R.array.achievement_progress_keys);
                for (int i = 0; i < achievementProgressKeys.length; i++) {
                    values.put(achievementProgressKeys[i], FirebaseHandler.user.achievementProgress.get(i));
                }

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
