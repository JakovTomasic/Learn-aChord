package com.justchill.android.learnachord.intervalOrChord;

import android.content.Context;
import android.content.res.Resources;

import com.justchill.android.learnachord.LocaleHelper;
import com.justchill.android.learnachord.MyApplication;
import com.justchill.android.learnachord.R;
import com.justchill.android.learnachord.database.DatabaseData;

import java.util.ArrayList;
import java.util.Random;

// List of all chords and methods for using that list
public final class ChordsList {

    // TODO: fix pov7

    /*
     * Useful for english chord abbreviations:
     * https://en.wikibooks.org/wiki/Music_Theory/Complete_List_of_Chord_Patterns
     * https://en.wikipedia.org/wiki/Inversion_(music)
     *
     * From that, rules are:
     * D53 -> M(53)
     * D63 -> M^6(3)
     * D64 -> M64
     *
     * mol isto samo m
     * SM isto samo dim
     * Pov isto samo aug
     *
     * Dom 7 je valjda samo brojevi (normalni: 7, 65, 43, (4)2)
     * veliki durski 7 -> dodaj maj
     * mali molski 7 -> dodaj min
     * Mali smanjeni 7 -> hdim
     * Veliki molski 7 -> mM
     * Povećani (VPOV) 7 -> ^7#5, ^65#3, ^2, ^#72, full name: "augmented seventh" -> aug
     * Smanjeni (SS) 7 -> dim
     *
     * VDom9 -> samo 9
     * MD9 -> ^7b9 (But it should be ^b97)
     * MM9 -> min9
     * VD9 -> maj9
     */

    // List of all chords, NOT ordered by size
    private static final ArrayList<Chord> ALL_CHORDS = new ArrayList<>(); // Osnovni akordi (5 3, 7 & 9)

    // Static initializer. This is run the first time anything is called from this class.
    static {

        // Durski 5 3
        ALL_CHORDS.add(new Chord(0, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(3)}, MyApplication.getStringByLocal(R.string.chord_durski), 5, 3));
        ALL_CHORDS.add(new Chord(1, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(5)}, MyApplication.getStringByLocal(R.string.chord_durski), 6, 3));
        ALL_CHORDS.add(new Chord(2, new Interval[]{IntervalsList.getInterval(5), IntervalsList.getInterval(4)}, MyApplication.getStringByLocal(R.string.chord_durski), 6, 4));

        // Molski 5 3
        ALL_CHORDS.add(new Chord(3, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(4)}, MyApplication.getStringByLocal(R.string.chord_molski), 5, 3));
        ALL_CHORDS.add(new Chord(4, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(5)}, MyApplication.getStringByLocal(R.string.chord_molski), 6, 3));
        ALL_CHORDS.add(new Chord(5, new Interval[]{IntervalsList.getInterval(5), IntervalsList.getInterval(3)}, MyApplication.getStringByLocal(R.string.chord_molski), 6, 4));

        // Smanjeni 5 3
        ALL_CHORDS.add(new Chord(6, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(3)}, MyApplication.getStringByLocal(R.string.chord_smanjeni_kvintakord), 5, 3));
        ALL_CHORDS.add(new Chord(7, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(6)}, MyApplication.getStringByLocal(R.string.chord_smanjeni_kvintakord), 6, 3));
        ALL_CHORDS.add(new Chord(8, new Interval[]{IntervalsList.getInterval(6), IntervalsList.getInterval(3)}, MyApplication.getStringByLocal(R.string.chord_smanjeni_kvintakord), 6, 4));

        // Povecani 5 3 - zvuči jednako u svim oblicima (obratima)
        ALL_CHORDS.add(new Chord(9, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(4)}, MyApplication.getStringByLocal(R.string.chord_povecani), 5, 3));


        // Dominantni (mali durski) 7
        ALL_CHORDS.add(new Chord(10, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(3), IntervalsList.getInterval(3)}, MyApplication.getStringByLocal(R.string.chord_dominantni), 7));
        ALL_CHORDS.add(new Chord(11, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(3), IntervalsList.getInterval(2)}, MyApplication.getStringByLocal(R.string.chord_dominantni), 6, 5));
        ALL_CHORDS.add(new Chord(12, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(2), IntervalsList.getInterval(4)}, MyApplication.getStringByLocal(R.string.chord_dominantni), 4, 3));
        ALL_CHORDS.add(new Chord(13, new Interval[]{IntervalsList.getInterval(2), IntervalsList.getInterval(4), IntervalsList.getInterval(3)}, MyApplication.getStringByLocal(R.string.chord_dominantni), 2));


        // Mnogostranost 7 u duru

        // Veliki durski 7
        ALL_CHORDS.add(new Chord(14, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(3), IntervalsList.getInterval(4)}, MyApplication.getStringByLocal(R.string.chord_veliki_durski), 7));
        ALL_CHORDS.add(new Chord(15, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(4), IntervalsList.getInterval(1)}, MyApplication.getStringByLocal(R.string.chord_veliki_durski), 6, 5));
        ALL_CHORDS.add(new Chord(16, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(1), IntervalsList.getInterval(4)}, MyApplication.getStringByLocal(R.string.chord_veliki_durski), 4, 3));
        ALL_CHORDS.add(new Chord(17, new Interval[]{IntervalsList.getInterval(1), IntervalsList.getInterval(4), IntervalsList.getInterval(3)}, MyApplication.getStringByLocal(R.string.chord_veliki_durski), 2));

        // Mali molski 7
        ALL_CHORDS.add(new Chord(18, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(4), IntervalsList.getInterval(3)}, MyApplication.getStringByLocal(R.string.chord_mali_molski), 7));
        ALL_CHORDS.add(new Chord(19, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(3), IntervalsList.getInterval(2)}, MyApplication.getStringByLocal(R.string.chord_mali_molski), 6, 5));
        ALL_CHORDS.add(new Chord(20, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(2), IntervalsList.getInterval(3)}, MyApplication.getStringByLocal(R.string.chord_mali_molski), 4, 3));
        ALL_CHORDS.add(new Chord(21, new Interval[]{IntervalsList.getInterval(2), IntervalsList.getInterval(3), IntervalsList.getInterval(4)}, MyApplication.getStringByLocal(R.string.chord_mali_molski), 2));

        // Mali smanjeni 7
        ALL_CHORDS.add(new Chord(22, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(3), IntervalsList.getInterval(4)}, MyApplication.getStringByLocal(R.string.chord_mali_smanjeni), 7));
        ALL_CHORDS.add(new Chord(23, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(4), IntervalsList.getInterval(2)}, MyApplication.getStringByLocal(R.string.chord_mali_smanjeni), 6, 5));
        ALL_CHORDS.add(new Chord(24, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(2), IntervalsList.getInterval(3)}, MyApplication.getStringByLocal(R.string.chord_mali_smanjeni), 4, 3));
        ALL_CHORDS.add(new Chord(25, new Interval[]{IntervalsList.getInterval(2), IntervalsList.getInterval(3), IntervalsList.getInterval(3)}, MyApplication.getStringByLocal(R.string.chord_mali_smanjeni), 2));


        // Mnogostranost 7 u molu

        // Veliki molski 7
        ALL_CHORDS.add(new Chord(26, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(4), IntervalsList.getInterval(4)}, MyApplication.getStringByLocal(R.string.chord_veliki_molski), 7));
        ALL_CHORDS.add(new Chord(27, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(4), IntervalsList.getInterval(1)}, MyApplication.getStringByLocal(R.string.chord_veliki_molski), 6, 5));
        ALL_CHORDS.add(new Chord(28, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(1), IntervalsList.getInterval(3)}, MyApplication.getStringByLocal(R.string.chord_veliki_molski), 4, 3));
        ALL_CHORDS.add(new Chord(29, new Interval[]{IntervalsList.getInterval(1), IntervalsList.getInterval(3), IntervalsList.getInterval(4)}, MyApplication.getStringByLocal(R.string.chord_veliki_molski), 2));

        // Povećani (veliki povećani) 7
        ALL_CHORDS.add(new Chord(30, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(4), IntervalsList.getInterval(3)}, MyApplication.getStringByLocal(R.string.chord_veliki_povecani), 7));
        ALL_CHORDS.add(new Chord(31, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(3), IntervalsList.getInterval(1)}, MyApplication.getStringByLocal(R.string.chord_veliki_povecani), 6, 5));
        ALL_CHORDS.add(new Chord(32, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(1), IntervalsList.getInterval(4)}, MyApplication.getStringByLocal(R.string.chord_veliki_povecani), 4, 3));
        ALL_CHORDS.add(new Chord(33, new Interval[]{IntervalsList.getInterval(1), IntervalsList.getInterval(4), IntervalsList.getInterval(4)}, MyApplication.getStringByLocal(R.string.chord_veliki_povecani), 2));

        // Smanjeni (smanjeno smanjeni) 7 - zvuči jednako u svim oblicima (obratima)
        ALL_CHORDS.add(new Chord(34, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(3), IntervalsList.getInterval(3)}, MyApplication.getStringByLocal(R.string.chord_smanjeni_septakord), 7));


        // Veliki dominantni 9
        ALL_CHORDS.add(new Chord(35, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(3), IntervalsList.getInterval(3), IntervalsList.getInterval(4)}, MyApplication.getStringByLocal(R.string.chord_veliki_dominantni), 9));

        // Mali durski/(dominantni) 9
        ALL_CHORDS.add(new Chord(36, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(3), IntervalsList.getInterval(3), IntervalsList.getInterval(3)}, MyApplication.getStringByLocal(R.string.chord_mali_durski_nonakord), 9));

        // Mali molski 9
        ALL_CHORDS.add(new Chord(37, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(4), IntervalsList.getInterval(3), IntervalsList.getInterval(4)}, MyApplication.getStringByLocal(R.string.chord_mali_molski_nonakord), 9));

        // Veliki durski 9
        ALL_CHORDS.add(new Chord(38, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(3), IntervalsList.getInterval(4), IntervalsList.getInterval(3)}, MyApplication.getStringByLocal(R.string.chord_veliki_durski), 9));


    }

    private ChordsList() {}

    public static int getChordsCount() {
        return ALL_CHORDS.size();
    }

    // Update names depending on language and set locale language
    public static void updateAllChordsNames() {
        getChord(0).setName(MyApplication.getStringByLocal(R.string.chord_durski));
        getChord(1).setName(MyApplication.getStringByLocal(R.string.chord_durski));
        getChord(2).setName(MyApplication.getStringByLocal(R.string.chord_durski));

        getChord(3).setName(MyApplication.getStringByLocal(R.string.chord_molski));
        getChord(4).setName(MyApplication.getStringByLocal(R.string.chord_molski));
        getChord(5).setName(MyApplication.getStringByLocal(R.string.chord_molski));

        getChord(6).setName(MyApplication.getStringByLocal(R.string.chord_smanjeni_kvintakord));
        getChord(7).setName(MyApplication.getStringByLocal(R.string.chord_smanjeni_kvintakord));
        getChord(8).setName(MyApplication.getStringByLocal(R.string.chord_smanjeni_kvintakord));

        getChord(9).setName(MyApplication.getStringByLocal(R.string.chord_povecani));

        getChord(10).setName(MyApplication.getStringByLocal(R.string.chord_dominantni));
        getChord(11).setName(MyApplication.getStringByLocal(R.string.chord_dominantni));
        getChord(12).setName(MyApplication.getStringByLocal(R.string.chord_dominantni));
        getChord(13).setName(MyApplication.getStringByLocal(R.string.chord_dominantni));

        getChord(14).setName(MyApplication.getStringByLocal(R.string.chord_veliki_durski));
        getChord(15).setName(MyApplication.getStringByLocal(R.string.chord_veliki_durski));
        getChord(16).setName(MyApplication.getStringByLocal(R.string.chord_veliki_durski));
        getChord(17).setName(MyApplication.getStringByLocal(R.string.chord_veliki_durski));

        getChord(18).setName(MyApplication.getStringByLocal(R.string.chord_mali_molski));
        getChord(19).setName(MyApplication.getStringByLocal(R.string.chord_mali_molski));
        getChord(20).setName(MyApplication.getStringByLocal(R.string.chord_mali_molski));
        getChord(21).setName(MyApplication.getStringByLocal(R.string.chord_mali_molski));

        getChord(22).setName(MyApplication.getStringByLocal(R.string.chord_mali_smanjeni));
        getChord(23).setName(MyApplication.getStringByLocal(R.string.chord_mali_smanjeni));
        getChord(24).setName(MyApplication.getStringByLocal(R.string.chord_mali_smanjeni));
        getChord(25).setName(MyApplication.getStringByLocal(R.string.chord_mali_smanjeni));

        getChord(26).setName(MyApplication.getStringByLocal(R.string.chord_veliki_molski));
        getChord(27).setName(MyApplication.getStringByLocal(R.string.chord_veliki_molski));
        getChord(28).setName(MyApplication.getStringByLocal(R.string.chord_veliki_molski));
        getChord(29).setName(MyApplication.getStringByLocal(R.string.chord_veliki_molski));

        getChord(30).setName(MyApplication.getStringByLocal(R.string.chord_veliki_povecani));
        getChord(31).setName(MyApplication.getStringByLocal(R.string.chord_veliki_povecani));
        getChord(32).setName(MyApplication.getStringByLocal(R.string.chord_veliki_povecani));
        getChord(33).setName(MyApplication.getStringByLocal(R.string.chord_veliki_povecani));

        getChord(34).setName(MyApplication.getStringByLocal(R.string.chord_smanjeni_septakord));

        getChord(35).setName(MyApplication.getStringByLocal(R.string.chord_veliki_dominantni));
        getChord(36).setName(MyApplication.getStringByLocal(R.string.chord_mali_durski_nonakord));
        getChord(37).setName(MyApplication.getStringByLocal(R.string.chord_mali_molski_nonakord));
        getChord(38).setName(MyApplication.getStringByLocal(R.string.chord_veliki_durski_nonakord));

    }

    // Get chord with given id
    public static Chord getChord(int id) {
        if(id >= getChordsCount()) {
            id = getChordsCount()-1;
        }
        if(id < 0) {
            id = 0;
        }
        return ALL_CHORDS.get(id);
    }

    public static ArrayList<Chord> getAllChords() {
        return ALL_CHORDS;
    }

    // TODO: make this more efficient, in IntervalsList too
    // Returns how many chords have been checked
    public static int getCheckedChordsCount() {
        int counter = 0;
        for(int i = 0; i < getChordsCount(); i++) {
            if(getChord(i).getIsChecked()) {
                counter++;
            }
        }
        return counter;
    }

    // Returns how many chords have been checked and can be played in given range
    public static int getCheckedChordsCountIncludingRange() {
        int counter = 0;
        for(int i = 0; i < getChordsCount(); i++) {
            if(getChord(i).getIsChecked() && isChordInsideBorders(getChord(i))) {
                counter++;
            }
        }
        return counter;
    }

    // Get number of chords that can be played
    public static int getPlayableChordsCount() {
        int counter = 0;
        for(int i = 0; i < getChordsCount(); i++) {
            if(isChordPlayable(getChord(i))) {
                counter++;
            }
        }
        return counter;
    }

    // Check if chord can be played
    public static boolean isChordPlayable(Chord chord) {
        return chord.isPlayableCountdownFinished() && chord.getIsChecked() && isChordInsideBorders(chord);
    }

    // Return chord if it has not been played for too long time
    private static Chord mustBePlayed() {
        for(int i = 0; i < getChordsCount(); i++) {
            if(getChord(i).getIsChecked() && getChord(i).notPlayedFor > (getPlayableChordsCount() + IntervalsList.getPlayableIntervalsCount()) * 2) {
                return getChord(i);
            }
        }
        return null;
    }

    // Returns random chord that can be played
    public static Chord getRandomPlayableChord() {
        Chord mustBePlayedChord = mustBePlayed();
        if(mustBePlayedChord != null) {
            return mustBePlayedChord;
        }

        Random random = new Random();
        int checkedChords = getCheckedChordsCountIncludingRange();
        int playableChords = getPlayableChordsCount();
        int counter = 0;

        if(playableChords <= 0) {
            if(checkedChords <= 0) {
                return null;
            }
            int randNum = random.nextInt(checkedChords); // 0 to (number of checked chords)-1 (including them)
            for(int i = 0; i < getChordsCount(); i++) {
                if(counter == randNum && getChord(i).getIsChecked() && isChordInsideBorders(getChord(i))) {
                    return getChord(i);
                }
                if(getChord(i).getIsChecked() && isChordInsideBorders(getChord(i))) {
                    counter++;
                }
            }
            return null;
        }

//        Log.d("####", "################################################################# " + playableIntervals);

        int randNum = random.nextInt(playableChords); // 0 to getPlayableIntervalsCount()-1 (including them)
        for(int i = 0; i < getChordsCount(); i++) {
            if(counter >= randNum && isChordPlayable(getChord(i))) {
                return getChord(i);
            }
            if(isChordPlayable(getChord(i))) {
                counter++;
            }
        }
        return null;
    }

    // Decrease playing delay for all chords
    public static void tickAllPlayableCountdowns() {
        for(int i = 0; i < getChordsCount(); i++) {
            getChord(i).tickPlayableCountdown();
        }
    }

    // Increase counter for how long chord hasn't been played (for all chords)
    public static void increaseNotPlayedFor() {
        for(int i = 0; i < getChordsCount(); i++) {
            getChord(i).notPlayedFor++;
        }
    }

    // Check or uncheck all chords in options
    public static void setAllChordsIsChecked(boolean myBool) {
        for(int i = 0; i < getChordsCount(); i++) {
            getChord(i).setIsChecked(myBool);
        }
    }

    // Get checked chord with biggest total range
    public static Chord getBiggestCheckedChord() {
        Chord biggest = getChord(0);
        for(int i = 0; i < getChordsCount(); i++) {
            if(getChord(i).getIsChecked() && getChord(i).getDifference() > biggest.getDifference()) {
                biggest = getChord(i);
            }
        }
        if(biggest.getIsChecked()) {
            return biggest;
        }
        return null;
    }

    // Check if chord can be played inside set range
    public static boolean isChordInsideBorders(Chord chord) {
        return chord.getDifference() <= DatabaseData.upKeyBorder - DatabaseData.downKeyBorder;
    }

    // Uncheck all chords that can't be played inside set range
    public static void uncheckOutOfRangeChords() {
        for(int i = 0; i < getChordsCount(); i++) {
            if(!isChordInsideBorders(getChord(i))) {
                getChord(i).setIsChecked(false);
            }
        }
    }


    // Returns random chord that is checked
    public static Chord getRandomCheckedChord(Chord... exception) {
        int checkedChords = getCheckedChordsCount();
        if(exception != null) {
            int subtract = 0;
            for(Chord ex : exception) {
                if(ex != null) {
                    subtract++;
                }
            }
            checkedChords -= subtract; // - number of exceptions
        }
        if(checkedChords <= 0) {
            return null;
        }

        // Get checked chord that is numbered as randomNumb (not counting unchecked ones)
        Random random = new Random();
        int randomNumb = random.nextInt(checkedChords);
        int counter = 0;
        for(int i = 0; i < getChordsCount(); i++) {
            if(getChord(i).getIsChecked() && !isInside(getChord(i), exception)) {
                if(randomNumb == counter) {
                    return getChord(i);
                }
                counter++;
            }
        }

        // If nothing was returned, return first checked chord that is not exception
        for(int i = 0; i < getChordsCount(); i++) {
            if(getChord(i).getIsChecked() && !isInside(getChord(i), exception)) {
                return getChord(i);
            }
        }

        // Otherwise, return null
        return null;
    }

    // Check if chord is inside list of chords
    private static boolean isInside(Chord number, Chord... newInt) {
        if(newInt == null) {
            return false;
        }

        for (Chord aNewInt : newInt) {
            if (number == aNewInt) {
                return true;
            }
        }
        return false;
    }

}
