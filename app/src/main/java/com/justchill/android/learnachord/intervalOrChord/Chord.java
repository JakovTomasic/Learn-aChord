package com.justchill.android.learnachord.intervalOrChord;


public class Chord extends IntervalOrChord {

    // TODO: not RAM optimized
    // List of intervals inside chord
    private Interval[] mIntervals;

    // Chord's ID
    private int mID;

    // Chord's numbers to show (right up and right down)
    private Integer mNumberOne, mNumberTwo;

    // Constructor
    public Chord(int id, Interval[] intervals, String name, Integer... numbers) {
        mIntervals = intervals;
        mName = name;
        mID = id;

        mNumberOne = null;
        mNumberTwo = null;

        if(numbers.length > 0) {
            mNumberOne = numbers[0];
        }
        if(numbers.length > 1) {
            mNumberTwo = numbers[1];
        }

        isChecked = true;

        totalRange = 0;
        for (Interval mInterval : mIntervals) {
            totalRange += mInterval.getDifference();
        }
    }

    // Public get ID
    public int getID() {
        return mID;
    }

    // Public get total number of tones
    public int getToneNumber() {
        return mIntervals.length+1;
    }

    // Public get interval that has id
    public Interval getInterval(int id) {
        if(id >= getToneNumber()) {
            id = getToneNumber() - 1;
        }
        return mIntervals[id];
    }

    // Public get list (array) of all intervals
    public Interval[] getAllIntervals() {
        return mIntervals;
    }


    // Public get number one
    public Integer getNumberOne() {
        return mNumberOne;
    }

    // Public get number two
    public Integer getNumberTwo() {
        return mNumberTwo;
    }


    // Public get number one as string
    public String getNumberOneAsString() {
        if(mNumberOne == null) {
            // If number one is not defined (null) return null (instead of "null")
            return null;
        }
        return String.valueOf(mNumberOne);
    }

    // Public get number two as string
    public String getNumberTwoAsString() {
        if(mNumberTwo == null) {
            // If number two is not defined (null) return null (instead of "null")
            return null;
        }
        return String.valueOf(mNumberTwo);
    }

}
