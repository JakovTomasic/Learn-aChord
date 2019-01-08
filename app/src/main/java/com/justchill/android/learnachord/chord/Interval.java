package com.justchill.android.learnachord.chord;

public class Interval {

    private int mDifference;
    private String mName;

    private boolean isChecked; // Is it checked in settings to play
    private int playableCountdown; // Countdown so random intervals method don't return same interval very soon as it was played

    public int notPlayedFor; // How long this intervals haven't been played

    public Interval(int difference, String name) {
        mDifference = difference;
        mName = name;
        isChecked = true;
        playableCountdown = 0;
        notPlayedFor = 0;
    }

    public int getDifference() {
        return mDifference;
    }

    public String getIntervalName() {
        return mName;
    }

    public void setIntervalName(String newName) {
        this.mName = newName;
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
}
