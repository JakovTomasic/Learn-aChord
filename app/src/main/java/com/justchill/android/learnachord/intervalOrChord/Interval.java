package com.justchill.android.learnachord.intervalOrChord;

public class Interval extends IntervalOrChord {

    public Interval(int difference, String name) {
        totalRange = difference;
        mName = name;
        isChecked = true;
        playableCountdown = 0;
        notPlayedFor = 0;
    }

}
