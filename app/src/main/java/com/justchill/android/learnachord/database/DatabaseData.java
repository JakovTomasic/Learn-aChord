package com.justchill.android.learnachord.database;

import android.annotation.SuppressLint;

import com.justchill.android.learnachord.TextScaling;

import java.util.Locale;

// Stores all database (option, user preference) data
public class DatabaseData {

    // Constants for each tone duration
    public static final double maxTonesSeparationTime = 10000.0; // 10 sec
    public static final double minTonesSeparationTime = 0.0; // 0 sec
    // Constants for duration between playing
    public static final double maxChordDurationTime = 10000.0; // 10 sec
    public static final double minChordDurationTime = 0.0; // 0 sec

    // Get default device language. If language of the device is croatian, set croatian. Otherwise, set english.
    @SuppressLint("ConstantLocale")
    static final int DEFAULT_SYSTEM_LANGUAGE = Locale.getDefault().getLanguage().equals("hr") ? DataContract.UserPrefEntry.LANGUAGE_CROATIAN :
            DataContract.UserPrefEntry.LANGUAGE_ENGLISH;
    // Language of the app, equals to one of the languages constants in DataContract.UserPrefEntry
    public static int appLanguage = DEFAULT_SYSTEM_LANGUAGE;


    // Counter for how many directions have been selected
    public static int directionsCount = 1;
    // Is direction selected
    public static boolean directionUp = true;
    public static boolean directionDown = false;
    public static boolean directionSameTime = false;

    // Each tone duration, initially set to default, equals to one of the constants in DataContract.UserPrefEntry
    public static double tonesSeparationTime = (double) DataContract.UserPrefEntry.DEFAULT_TONES_SEPARATION_TIME; // in ms
    // Delay between intervals/chords, initially set to default, equals to one of the constants in DataContract.UserPrefEntry
    public static double delayBetweenChords = (double) DataContract.UserPrefEntry.DEFAULT_INTERVAL_DURATION_TIME; // in ms

    // Lowest key in range
    public static int downKeyBorder = 1;
    // Highest key in range
    public static int upKeyBorder = DataContract.UserPrefEntry.NUMBER_OF_KEYS;

    // should app show progress bar while playing
    public static boolean showProgressBar = true;
    // should app show list of intervals in chord while playing
    public static boolean showWhatIntervals = true;

    // Chord/Interval/Tone text size, equals to one of the constants in DataContract.UserPrefEntry
    public static int chordTextScalingMode = DataContract.UserPrefEntry.CHORD_TEXT_SCALING_MODE_AUTO;

    // Playing mode, equals to one of the constants in DataContract.UserPrefEntry
    public static int playingMode = DataContract.UserPrefEntry.PLAYING_MODE_RANDOM;

    // Order number of playing directions
    // For custom mode - looping through modes (up, down, same time...)
    public static int directionUpViewIndex = 0;
    public static int directionDownViewIndex = 1;
    public static int directionSameTimeViewIndex = 2;

    // Tones section in options
    public static boolean playWhatTone = false;
    public static boolean playWhatOctave = false;

    // Float number to multiply text size with
    public static float scaledDensity = TextScaling.SCALED_DENSITY_NORMAL;

    // Quiz high scores
    public static int quizModeOneHighscore = 0;
    public static int quizModeTwoHighscore = 0;
    public static int quizModeThreeHighscore = 0;



    // This needs to be called when some of the directions are checked or unchecked
    // Refreshes counter for number of directions checked
    public static void refreshDirectionsCount() {
        directionsCount = 0;
        if(directionUp) {
            directionsCount++;
        }
        if(directionDown) {
            directionsCount++;
        }
        if(directionSameTime) {
            directionsCount++;
        }
    }

    // Checks if each tone duration is valid (inside predefined borders)
    static boolean isTonesSeparationTimeValid(double d) {
        return (d <= maxTonesSeparationTime && d >= minTonesSeparationTime);
    }

    // Checks if delay between intervals/chords is valid (inside predefined borders)
    static boolean isChordDurationTimeValid(double d) {
        return (d <= maxChordDurationTime && d >= minChordDurationTime);
    }




    // Constants for saving is has initial help dialog box been displayed
    public static final int BOOLEAN_FALSE = 0;
    public static final int BOOLEAN_TRUE = 1;

    // Has login initial dialog box been showed (only first time when there is internet connection)
    public static int logInHelpShowed = BOOLEAN_FALSE;
    // Has each activity initial dialog box been showed (only first time)
    public static int mainActivityHelpShowed = BOOLEAN_FALSE;
    public static int settingsActivityHelpShowed = BOOLEAN_FALSE;
    public static int quizActivityHelpShowed = BOOLEAN_FALSE;
    public static int userProfileActivityHelpShowed = BOOLEAN_FALSE;

    // Should log in help dialog be showed. For "ask me later" button to work (don't show after it was pressed)
    public static boolean dontShowLogInHelp = false;

    // Check if data that is boolean saved as int valid (initial dialog box showed values)
    public static boolean isBooleanDataSavedAsIntValid(int value) {
        return (value == BOOLEAN_TRUE || value == BOOLEAN_FALSE);
    }

}
