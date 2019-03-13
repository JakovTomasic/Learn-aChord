package com.justchill.android.learnachord.firebase;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.justchill.android.learnachord.MyApplication;
import com.justchill.android.learnachord.R;
import com.justchill.android.learnachord.intervalOrChord.ChordsList;
import com.justchill.android.learnachord.intervalOrChord.IntervalsList;


// Checks and saves achievements if accomplished
public class AchievementChecker {

    // Constants for identifying witch quiz has been played recently
    public static final int NULL_QUIZ_ID = 0;
    public static final int QUIZ_MODE_ONE_ID = 1;
    public static final int QUIZ_MODE_TWO_ID = 2;
    public static final int QUIZ_MODE_THREE_ID = 3;

    // What quiz has been played recently (becomes NULL_QUIZ_ID if none is recent)
    public static int lastPlayedQuizMode = NULL_QUIZ_ID;

    // Thread that will show achievement when milestone is reached
    private static Thread showPopupThread;

    // Set values to all accomplished achievements
    public static void checkAchievements(int quizScore) {
        for(int achievementId = 0; achievementId < User.numberOfAchievements; achievementId++) {
            if(isAchievementAccomplished(achievementId) && quizScore > FirebaseHandler.user.achievementProgress.get(achievementId)) {
                // Set score if all conditions are positive and if current score is greater that previous one
                if(quizScore % 5 == 0) {
                    // If new score is dividable by 5, show popup notification
                    showPopup(MyApplication.getActivity());
                }
                FirebaseHandler.user.setAchievementProgress(achievementId, quizScore);
            }
        }
    }

    // Checks if the achievement is accomplished / is everything as it needs to be for that achievement
    private static boolean isAchievementAccomplished(int achievementId) {
        // We must know witch quiz has been played
        if(lastPlayedQuizMode == NULL_QUIZ_ID) {
            return false;
        }

        switch(achievementId) {
            case 0:
                return (lastPlayedQuizMode == QUIZ_MODE_ONE_ID && allIntervalsInsideOneOctaveChecked());
            case 1:
                return (lastPlayedQuizMode == QUIZ_MODE_TWO_ID && allIntervalsInsideOneOctaveChecked());
            case 2:
                return (lastPlayedQuizMode == QUIZ_MODE_THREE_ID && allIntervalsInsideOneOctaveChecked());
            case 3:
                return (lastPlayedQuizMode == QUIZ_MODE_ONE_ID && allIntervalsChecked());
            case 4:
                return (lastPlayedQuizMode == QUIZ_MODE_TWO_ID && allIntervalsChecked());
            case 5:
                return (lastPlayedQuizMode == QUIZ_MODE_THREE_ID && allIntervalsChecked());
            case 6:
                return (lastPlayedQuizMode == QUIZ_MODE_ONE_ID && allTriadsChecked());
            case 7:
                return (lastPlayedQuizMode == QUIZ_MODE_TWO_ID && allTriadsChecked());
            case 8:
                return (lastPlayedQuizMode == QUIZ_MODE_THREE_ID && allTriadsChecked());
            case 9:
                return (lastPlayedQuizMode == QUIZ_MODE_ONE_ID && allChordsChecked());
            case 10:
                return (lastPlayedQuizMode == QUIZ_MODE_TWO_ID && allChordsChecked());
            case 11:
                return (lastPlayedQuizMode == QUIZ_MODE_THREE_ID && allChordsChecked());
            case 12:
                return (lastPlayedQuizMode == QUIZ_MODE_ONE_ID && allIntervalsChecked() && allChordsChecked());
            case 13:
                return (lastPlayedQuizMode == QUIZ_MODE_TWO_ID && allIntervalsChecked() && allChordsChecked());
            case 14:
                return (lastPlayedQuizMode == QUIZ_MODE_THREE_ID && allIntervalsChecked() && allChordsChecked());
            default:
                Log.e("AchievementChecker", "Something went wrong. Id: " + achievementId);
        }

        return false;
    }

    // Returns true if all intervals smaller (and equal to) one octave are selected
    private static boolean allIntervalsInsideOneOctaveChecked() {
        for(int i = 0; i <= 12; i++) {
            if(!IntervalsList.getInterval(i).getIsChecked() || !IntervalsList.isIntervalInsideBorders(IntervalsList.getInterval(i))) {
                return false;
            }
        }
        return true;
    }

    // Returns true if all intervals are checked and can be played (are inside borders)
    private static boolean allIntervalsChecked() {
        return IntervalsList.getCheckedIntervalCountIncludingRange() >= IntervalsList.getIntervalsCount();
    }

    // Returns true if all triads are checked and can be played (are inside borders)
    private static boolean allTriadsChecked() {
        for(int i = 0; i <= 9; i++) {
            if(!ChordsList.getChord(i).getIsChecked() || !ChordsList.isChordInsideBorders(ChordsList.getChord(i))) {
                return false;
            }
        }
        return true;
    }

    // Returns true if all chords are checked and can be played (are inside borders)
    private static boolean allChordsChecked() {
        return ChordsList.getCheckedChordsCountIncludingRange() >= ChordsList.getChordsCount();
    }


    // TODO: implement this
    // Shows popup (notification, just information) when new achievement milestone is reached.
    private static void showPopup(final Activity activity) {
        if(activity == null) {
            // If there is no activity, UI cannot be changed
            return;
        }

        // If achievement icon is already showing, finish that first
        try {
            showPopupThread.interrupt();
        } catch (Exception ignored) {}


        showPopupThread = new Thread(new Runnable() {
            @Override
            public void run() {
//                try {
//                    activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                View popup = activity.findViewById(R.id.popup_included_layout);
//                                TextView scoreTV = popup.findViewById(R.id.achievement_score_text_view);
//
//                                scoreTV.setText(String.valueOf(score));
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                // Show notification
                setPopupVisibility(activity, View.VISIBLE);

                // Wait for 3 sec
                try {
                    Thread.sleep(3000);
                } catch (Exception ignored) {}

                // Hide notification
                setPopupVisibility(activity, View.GONE);

            }
        });
        showPopupThread.start();
    }

    // Sets visibility of the achievement milestone popup (/notification)
    private static void setPopupVisibility(final Activity activity, final int visibility) {
        try {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
//                        View popup = activity.findViewById(R.id.popup_parent_layout);
                        View popup = activity.findViewById(R.id.achievement_icon_image_view);
                        popup.setVisibility(visibility);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
