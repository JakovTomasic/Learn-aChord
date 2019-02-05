package com.justchill.android.learnachord.quiz;

import android.view.View;

import com.justchill.android.learnachord.MyApplication;
import com.justchill.android.learnachord.R;
import com.justchill.android.learnachord.intervalOrChord.Chord;
import com.justchill.android.learnachord.intervalOrChord.Interval;
import com.justchill.android.learnachord.database.DatabaseData;
import com.justchill.android.learnachord.database.DatabaseHandler;

import java.util.ArrayList;

public class QuizData {


    // Don't change this constants (they need to be in same order when sorted)
    // Constants for calculating id of thing that is playing, final ID will be one of these + id of that object
    static final int quizModeThreeToneIDAdd = 0;
    static final int quizModeThreeIntervalIDAdd = 1000;
    static final int quizModeThreeChordIDAdd = 10000;


    // Score in quiz right now
    static int quizScore = 0;
    // Is user inside quiz mode (playing quiz)
    public static boolean isQuizModePlaying = false;
    // Is quiz playing stopped and waiting for user to choose answer
    static boolean waitingForQuizAnswer = false;
    // For pausing quiz, is quiz paused
    static boolean quizPlayingPaused = false;
    /* Quiz will play (reproduce sound) only if method that is playing sound has same ID as this one, this is used for
        fixing bugs if multiple method try to play at same time (new method is called before previous has benn finished*/
    static int quizPlayingID = 0;
    // Is the chord that is playing also showing, is correct answer == true
    static boolean quizModeOneCorrectAnswer = true;

    // Temporary interval that is playing, null if chord or tone is playing
    static Interval quizIntervalToPlay;
    // Temporary chord that is playing, null if interval or tone is playing
    static Chord quizChordToPlay;
    // Temporary tone that is playing, null if interval or chord is playing
    static int quizLowestKey;

    // Only for quiz mode one, temporary storing what to show
    static String quizChordNameToShow = "";
    static String quizChordNumberOneToShow = "";
    static String quizChordNumberTwoToShow = "";

    // Is quiz playing / reproducing sound right now (or if it should play like when quiz is paused during playing)
    public static boolean quizPlayingCurrentThing = false;


    // Correct answer for quiz mode two (ranging from 0 to 3, including)
    static int quizModeTwoCorrectAnswerID = -1;

    // List of interval/chord/tone names  to show as possible answer
    static String[] quizModeTwoChordNameToShow = new String[] {"", "", "", ""};
    static String[] quizModeTwoChordNumberOneToShow = new String[] {"", "", "", ""};
    static String[] quizModeTwoChordNumberTwoToShow = new String[] {"", "", "", ""};

    // List of intervals/chords/tones that is shown as possible answer in quiz mode two
    static Interval[] quizModeTwoSelectedIntervals = new Interval[] {null, null, null, null};
    static Chord[] quizModeTwoSelectedChords = new Chord[] {null, null, null, null};
    static Integer[] quizModeTwoSelectedTones = new Integer[] {null, null, null, null};

    // List of all possible answers, later used in ListView
    static ArrayList<Integer> quizModeThreeListOfPossibleAnswerIDs = null;
    // Correct ID in this list view
    static Integer quizModeThreeCorrectID = null;
    // ID of thing that is being selected
    static Integer quizModeThreeSelectedID = null;
    // Temporary list of ListView items (to change background color on selection)
    static View[] quizModeThreeListViews = null;
    // Is submit button showing
    static boolean quizModeThreeShowSubmitButton = false;


    // If new high score is greater that old one, save new one (as variable and to DB): 3 methods below:

    static void refreshQuizModeOneHighScore() {
        if(quizScore > DatabaseData.quizModeOneHighscore) {
            DatabaseData.quizModeOneHighscore = quizScore;
            DatabaseHandler.updateDatabaseOnSeparateThread();
        }
    }
    static void refreshQuizModeTwoHighScore() {
        if(quizScore > DatabaseData.quizModeTwoHighscore) {
            DatabaseData.quizModeTwoHighscore = quizScore;
            DatabaseHandler.updateDatabaseOnSeparateThread();
        }
    }
    static void refreshQuizModeThreeHighScore() {
        if(quizScore > DatabaseData.quizModeThreeHighscore) {
            DatabaseData.quizModeThreeHighscore = quizScore;
            DatabaseHandler.updateDatabaseOnSeparateThread();
        }
    }


    // Set background of every item in list with given color
    private static void quizModeThreeSetDefaultBackgroundsToAllViews(View[] list, int color) {
        if(list == null) {
            return;
        }

        for (View aList : list) {
            if (aList == null) {
                continue;
            }
            aList.setBackgroundColor(MyApplication.getAppContext().getResources().getColor(color));
        }
    }

    // Called when item is clicked, resets all backgrounds and handles click
    static void quizModeThreeOnListItemClick(int position) {
        quizModeThreeSetDefaultBackgroundsToAllViews(quizModeThreeListViews, R.color.quizModeThreeListViewUnselectedBackgroundColor);

        // Update selected ID variable
        quizModeThreeSelectedID = quizModeThreeListOfPossibleAnswerIDs.get(position);

        // Set selected background
        quizModeThreeListViews[position].setBackgroundColor(MyApplication.getAppContext().getResources().getColor(R.color.quizModeThreeListViewSelectedBackgroundColor));

    }


}
