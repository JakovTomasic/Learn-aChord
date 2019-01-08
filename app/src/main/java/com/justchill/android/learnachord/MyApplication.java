package com.justchill.android.learnachord;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.justchill.android.learnachord.chord.Chord;
import com.justchill.android.learnachord.chord.ChordsList;
import com.justchill.android.learnachord.chord.Interval;
import com.justchill.android.learnachord.chord.IntervalsList;
import com.justchill.android.learnachord.database.DataContract;

import java.util.ArrayList;
import java.util.Locale;

public class MyApplication extends Application {

    // TODO: test builtin android back button support

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public interface ChangeListener {
        void onIsPlayingChange();
        void onActivityResumed();
        void onPlayChordChange(Interval[] interval, int lowestKey, int directionToPlay);
        void onPlayKey(Integer keyId);
    }

    // TODO: rename this
    public interface MainActivityListener {
        void onIsPlayingChange();
        void onLoadingFinished();
    }

    private static boolean UIVisible = false;
    private static boolean isPlaying = false;
    private static ChangeListener listener;
    private static ArrayList<MainActivityListener> mainActivityListener = new ArrayList<>();

    /**
     * @param doIntervalsNeedUpdate do intervals need to update / set to data from database
     * @param doChordsNeedUpdate do they need to be set to data that is in database
     * @param doesDbNeedUpdate does database need to change / set to all current data
     * @param doSettingsNeedUpdate same with settings
     */
    private static boolean doIntervalsNeedUpdate = true;
    private static boolean doChordsNeedUpdate = true;
    private static boolean doesDbNeedUpdate = false;
    private static boolean doSettingsNeedUpdate = true;

    private static boolean playingChordOrInterval = false;
    private static boolean playingKey = false;

    public static boolean isLoadingFinished = false;

    // In eng language, mali durski 9 is different. There are constants for it:
    public static final int MD9_ID = 36;
    public static final String MD9_ENG_TEXT = "";
    public static final String MD9_ENG_ONE = "b9";
    public static final String MD9_ENG_TWO = "7";

    // Physical screen min(width, height)
    public static Integer smallerDisplayDimensionPX = null;

    // To not change settings language until you restart settings (close and open)
    // Otherwise, some chords (mali durski 9) are showed differently
    public static int settingActivityLoadedLanguage = 0;


    @SuppressLint("StaticFieldLeak")
    private static Activity activity = null;

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();

        Thread serviceThread = new Thread() {
            @Override
            public void run() {
                startService(new Intent(getApplicationContext(), ServicePlayer.class));
            }
        };
        serviceThread.start();
    }

    public static ChangeListener getListener() {
        return MyApplication.listener;
    }

    public static void setListener(ChangeListener listener) {
        MyApplication.listener = listener;
    }

    public static MainActivityListener getMainActivityListener(int id) {
        return MyApplication.mainActivityListener.get(id);
    }

    public static void addMainActivityListener(MainActivityListener listener) {
        MyApplication.mainActivityListener.add(listener);
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }

    public static void activityPaused() {
        MyApplication.UIVisible = false;
        MyApplication.activity = null;
    }

    public static void activityResumed(Activity mActivity) {
        MyApplication.UIVisible = true;
        MyApplication.activity = mActivity;

        if(listener != null) {
            listener.onActivityResumed();
        }
    }

    public static void playKey(int keyId) {
        MyApplication.playingKey = true;

        if(listener != null) {
            listener.onPlayKey(keyId);
        }
    }

    public static void stopPlayingKey() {
        if(!MyApplication.playingKey) {
            return;
        }

        MyApplication.playingKey = false;

        if(listener != null) {
            listener.onPlayKey(null);
        }
    }

    public static boolean isUIVisible() {
        return MyApplication.UIVisible;
    }

    public static void setIsPlaying(boolean newBool) {
        MyApplication.isPlaying = newBool;

        if(!MyApplication.isPlaying) {
            MyApplication.playingChordOrInterval = false;
        }
        if(listener != null) {
            listener.onIsPlayingChange();
        }
        if(mainActivityListener != null && mainActivityListener.size() > 0) {
            for(int i = 0; i < mainActivityListener.size(); i++) {
                mainActivityListener.get(i).onIsPlayingChange();
            }
        }
    }

    public static boolean isPlaying() {
        return MyApplication.isPlaying;
    }

    public static boolean isChordOrIntervalPlaying() {
        return MyApplication.playingChordOrInterval;
    }

    public static void playChord(Interval[] interval, int lowestKey, int directionToPlay) {
        MyApplication.playingChordOrInterval = true;

        if(listener != null) {
            listener.onPlayChordChange(interval, lowestKey, directionToPlay);
        }
    }

    public static void stopPlayingChord() {
        if(!isChordOrIntervalPlaying()) { // This must be here or rotation will stop everything
            return;
        }

        MyApplication.playingChordOrInterval = false;

        if(listener != null) {
            listener.onPlayChordChange(null, 0, 0);
        }
    }

    public static void loadingFinished() {
        MyApplication.isLoadingFinished = true;

        if(mainActivityListener != null && mainActivityListener.size() > 0) {
            for(int i = 0; i < mainActivityListener.size(); i++) {
                mainActivityListener.get(i).onLoadingFinished();
            }
        }
    }

    public static boolean doIntervalsNeedUpdate() {
        return MyApplication.doIntervalsNeedUpdate;
    }

    public static void setDoIntervalsNeedUpdate(boolean newBool) {
        MyApplication.doIntervalsNeedUpdate = newBool;
    }

    public static boolean doChordsNeedUpdate() {
        return MyApplication.doChordsNeedUpdate;
    }

    public static void setDoChordsNeedUpdate(boolean newBool) {
        MyApplication.doChordsNeedUpdate = newBool;
    }

    public static boolean doesDbNeedUpdate() {
        return MyApplication.doesDbNeedUpdate;
    }

    public static void setDoesDbNeedUpdate(boolean newBool) {
        MyApplication.doesDbNeedUpdate = newBool;
    }

    public static boolean doSettingsNeedUpdate() {
        return MyApplication.doSettingsNeedUpdate;
    }

    public static void setDoSettingsNeedUpdate(boolean newBool) {
        MyApplication.doSettingsNeedUpdate = newBool;
    }

    public static Activity getActivity() {
        return MyApplication.activity;
    }


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

    // <Settings
//    public static int playingDirection = DataContract.UserPrefEntry.DIRECTION_UP;
    public static int directionsCount = 1;
    public static boolean directionUp = true;
    public static boolean directionDown = false;
    public static boolean directionSameTime = false;

    public static double tonesSeparationTime = (double) DataContract.UserPrefEntry.DEFAULT_TONES_SEPARATION_TIME; // in ms
    public static final double maxTonesSeparationTime = 10000.0; // 10 sec
    public static final double minTonesSeparationTime = 0.0; // 0 sec

    public static boolean isTonesSeparationTimeValid(double d) {
        return (d <= maxTonesSeparationTime && d >= minTonesSeparationTime);
    }

    public static double delayBetweenChords = (double) DataContract.UserPrefEntry.DEFAULT_INTERVAL_DURATION_TIME; // in ms
    public static final double maxChordDurationTime = 10000.0; // 10 sec
    public static final double minChordDurationTime = 0.0; // 0 sec

    public static boolean isChordDurationTimeValid(double d) {
        return (d <= maxChordDurationTime && d >= minChordDurationTime);
    }

    public static final int DEFAULT_SYSTEM_LANGUAGE = Locale.getDefault().getLanguage().equals("hr") ? DataContract.UserPrefEntry.LANGUAGE_CROATIAN :
            DataContract.UserPrefEntry.LANGUAGE_ENGLISH;

    public static int appLanguage = DEFAULT_SYSTEM_LANGUAGE;
//    public static int settingsLoadedAt = DEFAULT_SYSTEM_LANGUAGE; // Just not to change chord names until settings restart

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


    // Quiz
    public static int quizModeOneHighscore = 0;
    public static int quizModeTwoHighscore = 0;
    public static int quizModeThreeHighscore = 0;

    // Settings/>




    // For telling in what direction to play chords/intervals
    public static final int directionUpID = 0;
    public static final int directionDownID = 1;
    public static final int directionSameID = 2;


    // For MainActivity interval/chord text size
    public static final float SCALED_DENSITY_SMALL = 1.1f;
    public static final float SCALED_DENSITY_NORMAL = 1.5f;
    public static final float SCALED_DENSITY_LARGE = 1.75f;

    // Scaled density to use
    public static float scaledDensity = SCALED_DENSITY_NORMAL;

    // Adjust density if out of borders
    public static float autoAdjustScalingDensity(float density) {
        if(density < SCALED_DENSITY_SMALL) {
            return SCALED_DENSITY_SMALL;
        }
        if(density > SCALED_DENSITY_LARGE) {
            return SCALED_DENSITY_LARGE;
        }
        return density;
    }

    // Check of new value for scaled density is available
    public static void refreshScaledDensity() {
        switch (MyApplication.chordTextScalingMode) {
            case DataContract.UserPrefEntry.CHORD_TEXT_SCALING_MODE_SMALL:
                scaledDensity = MyApplication.SCALED_DENSITY_SMALL;
                break;
            case DataContract.UserPrefEntry.CHORD_TEXT_SCALING_MODE_NORMAL:
                scaledDensity = MyApplication.SCALED_DENSITY_NORMAL;
                break;
            case DataContract.UserPrefEntry.CHORD_TEXT_SCALING_MODE_LARGE:
                scaledDensity = MyApplication.SCALED_DENSITY_LARGE;
                break;
            default: // CHORD_TEXT_SCALING_MODE_AUTO
                scaledDensity = MyApplication.autoAdjustScalingDensity(MyApplication.getAppContext().getResources().getDisplayMetrics().scaledDensity);
        }
    }



    // Quiz
    public static int quizScore = 0;
    public static String quizCorrectAnswerName = "";
    public static boolean isQuizModePlaying = false; // Is user inside quiz mode
    public static boolean waitingForQuizAnswer = false;
    // For pausing quiz
    public static boolean quizPlayingPaused = false;

    public static boolean quizModeOneCorrectAnswer = true;
    public static Interval quizIntervalToPlay;
    public static Chord quizChordToPlay;
    public static int quizLowestKey;
    public static String quizChordNameToShow = "", quizChordNumberOneToShow = "", quizChordNumberTwoToShow = "";
    public static boolean quizPlayingCurrentThing = false;

    public static String getKeyName(int key) {
        key--; // 0 to 60 (and not 1 - 61)

        String[] keys = MyApplication.getAppContext().getResources().getStringArray(R.array.key_symbols);
        StringBuilder stringBuilder = new StringBuilder();

        if(MyApplication.appLanguage == DataContract.UserPrefEntry.LANGUAGE_CROATIAN) {
            int octaveNumber = (key/12) - 1;

            if(octaveNumber > 0) {
                stringBuilder.append(keys[key%12]);
                stringBuilder.append(octaveNumber);
            } else if(octaveNumber == 0) {
                stringBuilder.append(keys[key%12]);
            } else if(octaveNumber == -1) {
                String str = keys[key%12];
                String capitalizeFirstLetter = str.substring(0, 1).toUpperCase() + str.substring(1);
                stringBuilder.append(capitalizeFirstLetter);
            }

            return stringBuilder.toString();
        } else {
            stringBuilder.append(keys[key%12]);
            stringBuilder.append((key/12) + 2);

            return stringBuilder.toString();
        }
    }

    public static void updateTextView(final TextView chordTV, final String chordName, final TextView numberOneTV,
                               final String chordNumberOne, final TextView numberTwoTV, final String chordNumberTwo) {
        if(MyApplication.getActivity() == null) {
            return;
        }
        try {
            MyApplication.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        chordTV.setText(chordName);

                        // Copy-pasted from ChordAdapter.GetView
                        if(chordNumberTwo == null) {
                            if(chordNumberOne != null) {
                                chordTV.setText(chordTV.getText() + chordNumberOne);
                            }

                            numberOneTV.setVisibility(View.GONE);
                            numberTwoTV.setVisibility(View.GONE);
                        } else {
                            numberOneTV.setVisibility(View.VISIBLE);
                            numberTwoTV.setVisibility(View.VISIBLE);
                            numberOneTV.setText(chordNumberOne);
                            numberTwoTV.setText(chordNumberTwo);
                        }
                    } catch (Exception e) {}

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void setupIntervalAndChordTextSize(TextView chordTextView, TextView chordNumOneTextView, TextView chordNumTwoTextView) {
        MyApplication.refreshScaledDensity();

        float chordTextViewTextSizePX = (float)(MyApplication.smallerDisplayDimensionPX * 0.75 / 8) * MyApplication.scaledDensity;

        chordTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, chordTextViewTextSizePX);
        chordNumOneTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, chordTextViewTextSizePX/2);
        chordNumTwoTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, chordTextViewTextSizePX/2);
    }

    public static void setupPlayButtonColor(Context context, View playButtonView, int color) {
        Drawable playButtonBackground = playButtonView.getBackground();
        if (playButtonBackground instanceof ShapeDrawable) { // This can be removed
            // cast to 'ShapeDrawable'
            ShapeDrawable shapeDrawable = (ShapeDrawable) playButtonBackground;
            shapeDrawable.getPaint().setColor(ContextCompat.getColor(context, color));
        } else if (playButtonBackground instanceof GradientDrawable) {
            // cast to 'GradientDrawable'
            GradientDrawable gradientDrawable = (GradientDrawable) playButtonBackground;
            gradientDrawable.setColor(ContextCompat.getColor(context, color));
        } else if (playButtonBackground instanceof ColorDrawable) { // This can be removed too
            // alpha value may need to be set again after this call
            ColorDrawable colorDrawable = (ColorDrawable) playButtonBackground;
            colorDrawable.setColor(ContextCompat.getColor(context, color));
        }
    }



//    public static Context updateResources(Context tempContext, String language) {
//        if(language == null) {
//            if(MyApplication.appLanguage == DataContract.UserPrefEntry.LANGUAGE_CROATIAN) {
//                language = DataContract.UserPrefEntry.CROATIAN_LANGUAGE_TAG;
//            } else {
//                language = DataContract.UserPrefEntry.ENGLISH_LANGUAGE_TAG;
//            }
//        }
//
//        Locale locale = new Locale(language);
//        Locale.setDefault(locale);
//
//        Resources res = tempContext.getResources();
//        Configuration config = new Configuration(res.getConfiguration());
//        if (Build.VERSION.SDK_INT >= 17) {
//            config.setLocale(locale);
//            tempContext = tempContext.createConfigurationContext(config);
//        } else {
//            config.locale = locale;
//            res.updateConfiguration(config, res.getDisplayMetrics());
//        }
//
//        return tempContext;
//    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, null));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleHelper.setLocale(this, null);
    }

    public static void updateIntervalsOnSeparateThread() {
        Thread userPrefThread2 = new Thread() {
            @Override
            public void run() {
                MyApplication.updateIntervals();
            }
        };
        userPrefThread2.start();
    }

    public static void updateChordsOnSeparateThread() {
        Thread userPrefThread4 = new Thread() {
            @Override
            public void run() {
                MyApplication.updateChords();
            }
        };
        userPrefThread4.start();
    }

    public static void updateSettingsOnSeparateThread() {
        Thread userPrefThread3 = new Thread() {
            @Override
            public void run() {
                MyApplication.updateSettings();
            }
        };
        userPrefThread3.start();
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

        MyApplication.setDoIntervalsNeedUpdate(false);
        MyApplication.setDoesDbNeedUpdate(false);
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

        MyApplication.setDoChordsNeedUpdate(false);
        MyApplication.setDoesDbNeedUpdate(false);
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

            MyApplication.directionUp = cursor.getInt(cursor.getColumnIndex(preferenceKeys[0])) ==
                    DataContract.UserPrefEntry.CHECKBOX_CHECKED;
            MyApplication.directionDown = cursor.getInt(cursor.getColumnIndex(preferenceKeys[1])) ==
                    DataContract.UserPrefEntry.CHECKBOX_CHECKED;
            MyApplication.directionSameTime = cursor.getInt(cursor.getColumnIndex(preferenceKeys[2])) ==
                    DataContract.UserPrefEntry.CHECKBOX_CHECKED;
            MyApplication.tonesSeparationTime = (double) cursor.getInt(cursor.getColumnIndex(preferenceKeys[3]));
            MyApplication.delayBetweenChords = (double) cursor.getInt(cursor.getColumnIndex(preferenceKeys[4]));
            MyApplication.appLanguage = cursor.getInt(cursor.getColumnIndex(preferenceKeys[5]));
            MyApplication.downKeyBorder = cursor.getInt(cursor.getColumnIndex(preferenceKeys[6]));
            MyApplication.upKeyBorder = cursor.getInt(cursor.getColumnIndex(preferenceKeys[7]));
            MyApplication.showProgressBar = cursor.getInt(cursor.getColumnIndex(preferenceKeys[8])) ==
                    DataContract.UserPrefEntry.CHECKBOX_CHECKED;
            MyApplication.showWhatIntervals = cursor.getInt(cursor.getColumnIndex(preferenceKeys[9])) ==
                    DataContract.UserPrefEntry.CHECKBOX_CHECKED;
            MyApplication.chordTextScalingMode = cursor.getInt(cursor.getColumnIndex(preferenceKeys[10]));
            MyApplication.playingMode = cursor.getInt(cursor.getColumnIndex(preferenceKeys[11]));
            MyApplication.directionUpViewIndex = cursor.getInt(cursor.getColumnIndex(preferenceKeys[12]));
            MyApplication.directionDownViewIndex = cursor.getInt(cursor.getColumnIndex(preferenceKeys[13]));
            MyApplication.directionSameTimeViewIndex = cursor.getInt(cursor.getColumnIndex(preferenceKeys[14]));
            MyApplication.playWhatTone = cursor.getInt(cursor.getColumnIndex(preferenceKeys[15])) ==
                    DataContract.UserPrefEntry.CHECKBOX_CHECKED;
            MyApplication.playWhatOctave = cursor.getInt(cursor.getColumnIndex(preferenceKeys[16])) ==
                    DataContract.UserPrefEntry.CHECKBOX_CHECKED;

        } finally {
            cursor.close();
        }

        MyApplication.setDoSettingsNeedUpdate(false);
        MyApplication.setDoesDbNeedUpdate(false);

        refreshDirectionsCount();

        // If language changes translate interval and chord names
        IntervalsList.updateAllIntervalsNames(MyApplication.getAppContext());
        ChordsList.updateAllChordsNames(MyApplication.getAppContext());
    }


}