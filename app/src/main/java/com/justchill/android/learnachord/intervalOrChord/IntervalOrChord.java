package com.justchill.android.learnachord.intervalOrChord;

// Parent class of interval and chord class
class IntervalOrChord {

    // Total range between lowest and highest tone
    int totalRange;

    // Name of the interval/chord
    String mName;


    boolean isChecked; // Is it checked in options to play
    int playableCountdown; // Countdown so get random interval method don't return same interval soon after it has been played


    public int notPlayedFor; // How long this interval/chord haven't been played


    // Public get name
    public String getName() {
        return mName;
    }

    // Public set name
    public void setName(String name) {
        mName = name;
    }

    // Public get totalRange
    public int getDifference() {
        return totalRange;
    }

    // Public get - is it checked in options to play
    public boolean getIsChecked() {
        return isChecked;
    }

    // Public set - is it checked in options to play
    public void setIsChecked(boolean newBool) {
        isChecked = newBool;
    }

    // Public set playable countdown
    public void setPlayableCountdown(int newInt) {
        playableCountdown = newInt;
    }

    // Public get - is it playable countdown finished
    public boolean isPlayableCountdownFinished() {
        return playableCountdown <= 0;
    }

    // Public set - tick playable countdown
    public void tickPlayableCountdown() {
        if(playableCountdown > 0) {
            playableCountdown--;
        }
    }

}
