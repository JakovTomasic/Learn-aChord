package com.justchill.android.learnachord;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.justchill.android.learnachord.intervalOrChord.Interval;
import com.justchill.android.learnachord.database.DataContract;
import com.justchill.android.learnachord.database.DatabaseData;

import java.util.ArrayList;

public class MyApplication extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public interface ChangeListener {
        void onIsPlayingChange();
        void onActivityResumed();
        void onPlayChordChange(Interval[] interval, int lowestKey, int directionToPlay);
        void onPlayKey(Integer keyId);
    }

    public interface ActivityListener {
        void onIsPlayingChange();
        void onLoadingFinished();
    }

    private static boolean UIVisible = false;
    private static boolean isPlaying = false;
    private static ChangeListener servicePlayerListener;
    private static ArrayList<ActivityListener> activityListeners = new ArrayList<>();

    private static boolean playingChordOrInterval = false;
    private static boolean playingKey = false;

    public static boolean isLoadingFinished = false;

    // In eng language, mali durski 9 is different. Here are constants for it:
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

        if(servicePlayerListener != null) {
            servicePlayerListener.onActivityResumed();
        }
    }

    public static void playKey(int keyId) {
        MyApplication.playingKey = true;

        if(servicePlayerListener != null) {
            servicePlayerListener.onPlayKey(keyId);
        }
    }

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

    public static void playChord(Interval[] interval, int lowestKey, int directionToPlay) {
        MyApplication.playingChordOrInterval = true;

        if(servicePlayerListener != null) {
            servicePlayerListener.onPlayChordChange(interval, lowestKey, directionToPlay);
        }
    }

    public static void stopPlayingChord() {
        if(!isChordOrIntervalPlaying()) { // This must be here or rotation will stop everything
            return;
        }

        MyApplication.playingChordOrInterval = false;

        if(servicePlayerListener != null) {
            servicePlayerListener.onPlayChordChange(null, 0, 0);
        }
    }

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




    // Used in quiz:

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

    public static boolean doesStringContainSubstring(String string, String substring) {
        if(string == null) {
            return false;
        }
        if(substring == null) {
            return true;
        }

        return string.toLowerCase().contains(substring.toLowerCase());
    }


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


    public static void setupIntervalAndChordTextSize(TextView chordTextView, TextView chordNumOneTextView, TextView chordNumTwoTextView, float divideBy) {
        TextScaling.refreshScaledDensity();

        float chordTextViewTextSizePX = (float)(MyApplication.smallerDisplayDimensionPX * 0.75 / 8) * DatabaseData.scaledDensity;

        chordTextViewTextSizePX /= divideBy;

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


}