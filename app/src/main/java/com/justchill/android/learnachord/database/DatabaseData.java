package com.justchill.android.learnachord.database;

import android.annotation.SuppressLint;

import com.justchill.android.learnachord.TextScaling;

import java.util.Locale;

public class DatabaseData {


    public static final double maxTonesSeparationTime = 10000.0; // 10 sec
    public static final double minTonesSeparationTime = 0.0; // 0 sec
    public static final double maxChordDurationTime = 10000.0; // 10 sec
    public static final double minChordDurationTime = 0.0; // 0 sec

    @SuppressLint("ConstantLocale")
    public static final int DEFAULT_SYSTEM_LANGUAGE = Locale.getDefault().getLanguage().equals("hr") ? DataContract.UserPrefEntry.LANGUAGE_CROATIAN :
            DataContract.UserPrefEntry.LANGUAGE_ENGLISH;
    public static int appLanguage = DEFAULT_SYSTEM_LANGUAGE;


//    public static int playingDirection = DataContract.UserPrefEntry.DIRECTION_UP;
    public static int directionsCount = 1;
    public static boolean directionUp = true;
    public static boolean directionDown = false;
    public static boolean directionSameTime = false;

    public static double tonesSeparationTime = (double) DataContract.UserPrefEntry.DEFAULT_TONES_SEPARATION_TIME; // in ms
    public static double delayBetweenChords = (double) DataContract.UserPrefEntry.DEFAULT_INTERVAL_DURATION_TIME; // in ms

    public static int downKeyBorder = 1;
    public static int upKeyBorder = DataContract.UserPrefEntry.NUMBER_OF_KEYS;

    public static boolean showProgressBar = true;
    public static boolean showWhatIntervals = true;

    public static int chordTextScalingMode = 0;

    public static int playingMode = DataContract.UserPrefEntry.PLAYING_MODE_RANDOM;

    // For custom mode - looping through modes (up, down, same time...)
    public static int directionUpViewIndex = 0;
    public static int directionDownViewIndex = 1;
    public static int directionSameTimeViewIndex = 2;

    // Tones settings
    public static boolean playWhatTone = false;
    public static boolean playWhatOctave = false;

    // Scaled density to use
    public static float scaledDensity = TextScaling.SCALED_DENSITY_NORMAL;

    // Quiz
    public static int quizModeOneHighscore = 0;
    public static int quizModeTwoHighscore = 0;
    public static int quizModeThreeHighscore = 0;



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

    public static boolean isTonesSeparationTimeValid(double d) {
        return (d <= maxTonesSeparationTime && d >= minTonesSeparationTime);
    }

    public static boolean isChordDurationTimeValid(double d) {
        return (d <= maxChordDurationTime && d >= minChordDurationTime);
    }
}
