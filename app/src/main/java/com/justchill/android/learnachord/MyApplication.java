package com.justchill.android.learnachord;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.multidex.MultiDex;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.justchill.android.learnachord.intervalOrChord.Interval;
import com.justchill.android.learnachord.database.DataContract;
import com.justchill.android.learnachord.database.DatabaseData;
import com.justchill.android.learnachord.quiz.QuizData;

import java.net.InetAddress;
import java.util.ArrayList;

// Main static class to get application context and other static things that don't belong anywhere else
public class MyApplication extends Application {

    // Context of the application
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    // Listener for ServicePlayer interface
    public interface ChangeListener {
        // Called when isPlaying changes
        void onIsPlayingChange();
        // Called when any of the activities resumes
        void onActivityResumed();
        // Called when playingChordOrInterval changes
        void onPlayChordChange(Interval[] interval, int lowestKey, int directionToPlay);
        // Called when playingKey changes
        void onPlayKey(Integer keyId);
    }

    // Listener for loading sound when app opens (for when loading is finished)
    public interface ActivityListener {
        // Called when isPlaying changes
        void onIsPlayingChange();
        // Called when isLoadingFinished changes
        void onLoadingFinished();
    }

    // Is UI of the app visible (is app opened)
    private static boolean UIVisible = false;
    // Is sound randomly playing (in main activity)
    private static boolean isPlaying = false;
    // Listener for ServicePlayer
    private static ChangeListener servicePlayerListener;
    // Listeners for loading sound when app opens (for when loading is finished)
    private static ArrayList<ActivityListener> activityListeners = new ArrayList<>();

    // Is just one interval/chord playing (not random looping, just one)
    private static boolean playingChordOrInterval = false;
    // Is just one key playing
    private static boolean playingKey = false;

    // Is sound loading finished
    public static boolean isLoadingFinished = false;

    // In eng language, mali durski 9 is different. Here are constants for it:
    public static final int MD9_ID = 36;
    public static final String MD9_ENG_TEXT = "";
    public static final String MD9_ENG_ONE = "b9";
    public static final String MD9_ENG_TWO = "7";

    // Physical screen min(width, height)
    public static Integer smallerDisplayDimensionPX = null;

    // To not change options' language until you restart options (close and open)
    // Otherwise, some chords (mali durski 9) will be showed differently
    public static int settingActivityLoadedLanguage = 0;


    // This is activity that is currently opened, null if UI is not visible
    @SuppressLint("StaticFieldLeak")
    private static Activity activity = null;

    // This is called when app is opened (created)
    public void onCreate() {
        super.onCreate();
        // Set app context
        MyApplication.context = getApplicationContext();

        // Start service
        Thread serviceThread = new Thread() {
            @Override
            public void run() {
                startService(new Intent(getApplicationContext(), ServicePlayer.class));
            }
        };
        serviceThread.start();
    }

    public static ChangeListener getServicePlayerListener() {
        return MyApplication.servicePlayerListener;
    }

    public static void setServicePlayerListener(ChangeListener listener) {
        MyApplication.servicePlayerListener = listener;
    }

    public static ActivityListener getActivityListener(int id) {
        return MyApplication.activityListeners.get(id);
    }

    public static void addActivityListener(ActivityListener listener) {
        MyApplication.activityListeners.add(listener);
    }

    // Get application context. Needed for some static methods.
    public static Context getAppContext() {
        return MyApplication.context;
    }

    // Called when eny activity pauses
    public static void activityPaused() {
        MyApplication.UIVisible = false;
        MyApplication.activity = null;
    }

    // Called when eny activity resumes
    public static void activityResumed(Activity mActivity) {
        MyApplication.UIVisible = true;
        MyApplication.activity = mActivity;

        if(servicePlayerListener != null) {
            servicePlayerListener.onActivityResumed();
        }
    }

    // Play just one key
    public static void playKey(int keyId) {
        MyApplication.playingKey = true;

        if(servicePlayerListener != null) {
            servicePlayerListener.onPlayKey(keyId);
        }
    }

    // Stop playing just one key
    public static void stopPlayingKey() {
        if(!MyApplication.playingKey) {
            return;
        }

        MyApplication.playingKey = false;

        if(servicePlayerListener != null) {
            servicePlayerListener.onPlayKey(null);
        }
    }

    public static boolean isUIVisible() {
        return MyApplication.UIVisible;
    }

    // If isPlaying is set to true - random playing loop will start. If it is false, all playing will stop
    public static void setIsPlaying(boolean newBool) {
        MyApplication.isPlaying = newBool;

        if(!MyApplication.isPlaying) {
            MyApplication.playingChordOrInterval = false;
        }
        if(servicePlayerListener != null) {
            servicePlayerListener.onIsPlayingChange();
        }
        if(activityListeners != null && activityListeners.size() > 0) {
            for(int i = 0; i < activityListeners.size(); i++) {
                activityListeners.get(i).onIsPlayingChange();
            }
        }
    }

    public static boolean isPlaying() {
        return MyApplication.isPlaying;
    }

    public static boolean isChordOrIntervalPlaying() {
        return MyApplication.playingChordOrInterval;
    }

    // Play just one chord or interval in given direction on given key
    public static void playChord(Interval[] interval, int lowestKey, int directionToPlay) {
        MyApplication.playingChordOrInterval = true;

        if(servicePlayerListener != null) {
            servicePlayerListener.onPlayChordChange(interval, lowestKey, directionToPlay);
        }
    }

    // Stop playing chord/interval
    public static void stopPlayingChord() {
        // This must be here or rotation will stop everything
        if(!isChordOrIntervalPlaying()) {
            return;
        }

        MyApplication.playingChordOrInterval = false;

        if(servicePlayerListener != null) {
            servicePlayerListener.onPlayChordChange(null, 0, 0);
        }
    }

    // Called when sound loading is finished (app is ready to start playing)
    public static void loadingFinished() {
        MyApplication.isLoadingFinished = true;

        if(activityListeners != null && activityListeners.size() > 0) {
            for(int i = 0; i < activityListeners.size(); i++) {
                activityListeners.get(i).onLoadingFinished();
            }
        }
    }

    public static Activity getActivity() {
        return MyApplication.activity;
    }




    // For telling in what direction to play chords/intervals, just constants
    public static final int directionUpID = 0;
    public static final int directionDownID = 1;
    public static final int directionSameID = 2;





    // Returns the name of a given key as string
    public static String getKeyName(int key) {
        key--; // 0 to 60 (and not 1 - 61)

        String[] keys = MyApplication.getAppContext().getResources().getStringArray(R.array.key_symbols);
        StringBuilder stringBuilder = new StringBuilder();

        if(DatabaseData.appLanguage == DataContract.UserPrefEntry.LANGUAGE_CROATIAN) {
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

    // Returns view to set in quiz game over dialog (showing the
    public static View getQuizGameOverDialogLayour(Activity activity, int score) {
        // Get layout
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_quiz_game_over_layout, null, false);

        // Get chord (/interval/tones) text views
        TextView chordNameTV = dialogLayout.findViewById(R.id.chord_text_view);
        TextView chordNumberOneTV = dialogLayout.findViewById(R.id.chord_number_one);
        TextView chordNumberTwoTV = dialogLayout.findViewById(R.id.chord_number_two);

        // Initialize names
        String correctName = null, correctNumberOne = null, correctNumberTwo = null;

        // Set names depending on what has been played
        if(QuizData.quizIntervalToPlay != null) {
            correctName = QuizData.quizIntervalToPlay.getName();
        }
        else if(QuizData.quizChordToPlay != null) {
            correctName = QuizData.quizChordToPlay.getName();
            correctNumberOne = QuizData.quizChordToPlay.getNumberOneAsString();
            correctNumberTwo = QuizData.quizChordToPlay.getNumberTwoAsString();
        }
        else
        {
            correctName = MyApplication.getKeyName(QuizData.quizLowestKey);
        }

        // Set that names to the TextViews
        MyApplication.updateTextView(chordNameTV, correctName, chordNumberOneTV, correctNumberOne, chordNumberTwoTV, correctNumberTwo);


        // Set score text
        TextView scoreTV = dialogLayout.findViewById(R.id.quiz_final_score_text_view);
        scoreTV.setText(MyApplication.readResource(R.string.quiz_game_over_score_text, null) + " " + String.valueOf(score));

        // return the view
        return dialogLayout;
    }

    // Set interval/chord/tone name text
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
                                chordTV.append(chordNumberOne);
                            }

                            numberOneTV.setVisibility(View.GONE);
                            numberTwoTV.setVisibility(View.GONE);
                        } else {
                            numberOneTV.setVisibility(View.VISIBLE);
                            numberTwoTV.setVisibility(View.VISIBLE);
                            numberOneTV.setText(chordNumberOne);
                            numberTwoTV.setText(chordNumberTwo);
                        }

                    } catch (Exception ignored) {}

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Returns interval/chord/tone name as string, for quiz mode three search
    public static String getChordNameAsString(String name, String numberOne, String numberTwo) {
        StringBuilder stringBuilder = new StringBuilder();

        if(name != null) {
            stringBuilder.append(name);
        }

        if(numberOne != null) {
            stringBuilder.append(numberOne);
        }

        if(numberTwo != null) {
            stringBuilder.append(numberTwo);
        }

        return stringBuilder.toString();
    }

    // Check if given string has given substring, for quiz mode three search
    public static boolean doesStringContainSubstring(String string, String substring) {
        if(string == null) {
            return false;
        }
        if(substring == null) {
            return true;
        }

        return string.toLowerCase().contains(substring.toLowerCase());
    }

    // For hiding soft input
    public static void hideKeyboardFromActivity(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }

        if(imm == null) {
            return;
        }

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    // Set interval/chord/tone name text size
    public static void setupIntervalAndChordTextSize(TextView chordTextView, TextView chordNumOneTextView, TextView chordNumTwoTextView, float divideBy) {
        TextScaling.refreshScaledDensity();

        float chordTextViewTextSizePX = (float)(MyApplication.smallerDisplayDimensionPX * 0.75 / 8) * DatabaseData.scaledDensity;

        chordTextViewTextSizePX /= divideBy;

        chordTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, chordTextViewTextSizePX);
        chordNumOneTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, chordTextViewTextSizePX/2);
        chordNumTwoTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, chordTextViewTextSizePX/2);
    }

    // Set given color of play/stop button
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


    /*
     * Returns true if internet connection is present (not just connected to network)
     * MUST BE CALLED FROM SEPARATE THREAD!!
     */
    public static boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            return !ipAddr.toString().equals("");

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    // Read string from resources
    public static String readResource(int id, Resources resources) {
        if(resources == null) {
            resources = getAppContext().getResources();
        }
        return resources.getString(id);
    }

    // For different languages support
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, null));

        // Fix for not opening app on older versions after firebase integration
        MultiDex.install(this);
    }

    // When something changes, set app language
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleHelper.setLocale(this, null);
    }


}