package com.justchill.android.learnachord.chord;

import java.io.Serializable;

public class Chord {

    // TODO: not RAM optimized
    private Interval[] mIntervals;

    private String mName;
    private int mID;

    // TODO: implement inheritance, this is repeating from Interval.java
    private boolean isChecked; // Is it checked in settings to play
    private int playableCountdown; // Countdown so random intervals method don't return same interval very soon as it was played

    public int notPlayedFor; // How long this intervals haven't been played

    private int totalRange;

    private Integer mNumberOne, mNumberTwo/*, mNumberThree*/;

    public Chord(int id, Interval[] intervals, String name, Integer... numbers) {
        mIntervals = intervals;
        mName = name;
        mID = id;

        mNumberOne = null;
        mNumberTwo = null;
//        mNumberThree = null;

        if(numbers.length > 0) {
            mNumberOne = numbers[0];
        }
        if(numbers.length > 1) {
            mNumberTwo = numbers[1];
        }
//        if(numbers.length > 2) {
//            mNumberThree = numbers[2];
//        }

        isChecked = true;

        totalRange = 0;
        for(int i = 0; i < mIntervals.length; i++) {
            totalRange += mIntervals[i].getDifference();
        }
    }

    public int getID() {
        return mID;
    }

    public int getToneNumber() {
        return mIntervals.length+1;
    }

    public Interval getInterval(int id) {
        if(id >= getToneNumber()) {
            id = getToneNumber() - 1;
        }
        return mIntervals[id];
    }

    public Interval[] getAllIntervals() {
        return mIntervals;
    }

    public String getChordName() {
        return mName;
    }

    public void setChordName(String name) {
        mName = name;
    }

    public boolean getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean newBool) {
        isChecked = newBool;
    }

    public void setPlayableCountdown(int newInt) {
        playableCountdown = newInt;
    }

    public boolean isPlayableCountdownFinished() {
        return playableCountdown <= 0;
    }

    public void tickPlayableCountdown() {
        if(playableCountdown > 0) {
            playableCountdown--;
        }
    }

    public int getDifference() {
        return  totalRange;
    }

    public Integer getNumberOne() {
        return mNumberOne;
    }

    public Integer getNumberTwo() {
        return mNumberTwo;
    }

//    public Integer getNumberThree() {
//        return mNumberThree;
//    }

    public String getNumberOneAsString() {
        if(mNumberOne == null) {
            return null;
        }
        return String.valueOf(mNumberOne);
    }

    public String getNumberTwoAsString() {
        if(mNumberTwo == null) {
            return null;
        }
        return String.valueOf(mNumberTwo);
    }

//    public String getNumberThreeAsString() {
//        if(mNumberThree == null) {
//            return null;
//        }
//        return String.valueOf(mNumberThree);
//    }
}
