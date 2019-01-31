package com.justchill.android.learnachord.intervalOrChord;

class IntervalOrChord {

    // All variables and functions are package-private (default)


    int totalRange;

    String mName;


    boolean isChecked; // Is it checked in settings to play
    int playableCountdown; // Countdown so random intervals method don't return same interval very soon as it was played



    public int notPlayedFor; // How long this intervals haven't been played



    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getDifference() {
        return totalRange;
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
