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
    public static final int quizModeThreeToneIDAdd = 0;
    public static final int quizModeThreeIntervalIDAdd = 1000;
    public static final int quizModeThreeChordIDAdd = 10000;


    public static int quizScore = 0;
    public static boolean isQuizModePlaying = false; // Is user inside quiz mode
    public static boolean waitingForQuizAnswer = false;
    // For pausing quiz
    public static boolean quizPlayingPaused = false;
    public static int quizPlayingID = 0;
    public static boolean quizModeOneCorrectAnswer = true;

    public static Interval quizIntervalToPlay;
    public static Chord quizChordToPlay;
    public static int quizLowestKey;

    public static String quizChordNameToShow = "";
    public static String quizChordNumberOneToShow = "";
    public static String quizChordNumberTwoToShow = "";

    public static boolean quizPlayingCurrentThing = false;

    public static int quizModeTwoCorrectAnswerID = -1;
    public static String[] quizModeTwoChordNameToShow = new String[] {"", "", "", ""};
    public static String[] quizModeTwoChordNumberOneToShow = new String[] {"", "", "", ""};
    public static String[] quizModeTwoChordNumberTwoToShow = new String[] {"", "", "", ""};
    public static Interval[] quizModeTwoSelectedIntervals = new Interval[] {null, null, null, null};
    public static Chord[] quizModeTwoSelectedChords = new Chord[] {null, null, null, null};
    public static Integer[] quizModeTwoSelectedTones = new Integer[] {null, null, null, null};

    public static ArrayList<Integer> quizModeThreeListOfPossibleAnswerIDs = null;
    public static Integer quizModeThreeCorrectID = null;
    public static Integer quizModeThreeSelectedID = null;
    public static View[] quizModeThreeListViews = null;
    public static boolean quizModeThreeShowSubmitButton = false;


    // If new high score is greater that old one, save new one (as variable and to DB)

    public static void refreshQuizModeOneHighScore() {
        if(quizScore > DatabaseData.quizModeOneHighscore) {
            DatabaseData.quizModeOneHighscore = quizScore;
            DatabaseHandler.updateDatabaseOnSeparateThread();
        }
    }

    public static void refreshQuizModeTwoHighScore() {
        if(quizScore > DatabaseData.quizModeTwoHighscore) {
            DatabaseData.quizModeTwoHighscore = quizScore;
            DatabaseHandler.updateDatabaseOnSeparateThread();
        }
    }

    public static void refreshQuizModeThreeHighScore() {
        if(quizScore > DatabaseData.quizModeThreeHighscore) {
            DatabaseData.quizModeThreeHighscore = quizScore;
            DatabaseHandler.updateDatabaseOnSeparateThread();
        }
    }


    public static void quizModeThreeSetDefaultBackgroundsToAllViews(View[] list, int color) {
        if(list == null) {
            return;
        }

        for(int i = 0; i < list.length; i++) {
            if(list[i] == null) {
                continue;
            }
            list[i].setBackgroundColor(MyApplication.getAppContext().getResources().getColor(color));
        }
    }

    public static void quizModeThreeOnListItemClick(int position) {
        quizModeThreeSetDefaultBackgroundsToAllViews(quizModeThreeListViews, R.color.quizModeThreeListViewUnselectedBackgroundColor);

        quizModeThreeSelectedID = quizModeThreeListOfPossibleAnswerIDs.get(position);

        quizModeThreeListViews[position].setBackgroundColor(MyApplication.getAppContext().getResources().getColor(R.color.quizModeThreeListViewSelectedBackgroundColor));

    }


}
