package com.justchill.android.learnachord.intervalOrChord;


public class Chord extends IntervalOrChord {

    // TODO: not RAM optimized
    private Interval[] mIntervals;

    private int mID;

    private Integer mNumberOne, mNumberTwo;

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







    public Integer getNumberOne() {
        return mNumberOne;
    }

    public Integer getNumberTwo() {
        return mNumberTwo;
    }


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

}
