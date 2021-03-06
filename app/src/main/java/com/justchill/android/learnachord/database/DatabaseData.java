package com.justchill.android.learnachord.database;

import android.annotation.SuppressLint;

import com.justchill.android.learnachord.TextScaling;

import java.util.Locale;

// Stores all database (option, user preference) data
public class DatabaseData {

    // Get default device language. If language of the device is croatian, set croatian. Otherwise, set english.
    @SuppressLint("ConstantLocale")
    public static final int DEFAULT_SYSTEM_LANGUAGE = Locale.getDefault().getLanguage().equals("hr") ? DataContract.UserPrefEntry.LANGUAGE_CROATIAN :
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

    // Number of hours/days/weeks/months after witch to remind user to use app
    public static int reminderTimeIntervalNumber = 1;
    // Reminder interval number multiplier, to show reminders every n hours/days/weeks/months
    public static int reminderTimeIntervalMode = DataContract.UserPrefEntry.REMINDER_TIME_INTERVAL_WEEK;

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


    // Has login initial dialog box been showed (only first time when there is internet connection)
    public static int logInHelpShowed = DataContract.UserPrefEntry.BOOLEAN_FALSE;
    // Has each activity initial dialog box been showed (only first time)
    public static int mainActivityHelpShowed = DataContract.UserPrefEntry.BOOLEAN_FALSE;
    public static int settingsActivityHelpShowed = DataContract.UserPrefEntry.BOOLEAN_FALSE;
    public static int quizActivityHelpShowed = DataContract.UserPrefEntry.BOOLEAN_FALSE;
    public static int userProfileActivityHelpShowed = DataContract.UserPrefEntry.BOOLEAN_FALSE;

    // Should log in help dialog be showed. For "ask me later" button to work (don't show after it was pressed)
    public static boolean dontShowLogInHelp = false;

    // Stores id for night mode (Google employees doesn't know difference between dark and night mode)
    public static int nightModeId = DataContract.UserPrefEntry.DEFAULT_NIGHT_MODE;

    // Stores time in milliseconds of last time user has used he app, for showing reminders to use the app
    public static long lastTimeAppUsedInMillis = System.currentTimeMillis();

}
