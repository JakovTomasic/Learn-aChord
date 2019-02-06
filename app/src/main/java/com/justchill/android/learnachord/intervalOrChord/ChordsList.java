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
        // Set locale language
        Context context = LocaleHelper.setLocale(MyApplication.getAppContext(), LocaleHelper.getLanguageLabel());
        Resources resources = context.getResources();

        // Durski 5 3
        ALL_CHORDS.add(new Chord(0, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(3)}, MyApplication.readResource(R.string.chord_durski, resources), 5, 3));
        ALL_CHORDS.add(new Chord(1, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(5)}, MyApplication.readResource(R.string.chord_durski, resources), 6, 3));
        ALL_CHORDS.add(new Chord(2, new Interval[]{IntervalsList.getInterval(5), IntervalsList.getInterval(4)}, MyApplication.readResource(R.string.chord_durski, resources), 6, 4));

        // Molski 5 3
        ALL_CHORDS.add(new Chord(3, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(4)}, MyApplication.readResource(R.string.chord_molski, resources), 5, 3));
        ALL_CHORDS.add(new Chord(4, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(5)}, MyApplication.readResource(R.string.chord_molski, resources), 6, 3));
        ALL_CHORDS.add(new Chord(5, new Interval[]{IntervalsList.getInterval(5), IntervalsList.getInterval(3)}, MyApplication.readResource(R.string.chord_molski, resources), 6, 4));

        // Smanjeni 5 3
        ALL_CHORDS.add(new Chord(6, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(3)}, MyApplication.readResource(R.string.chord_smanjeni_kvintakord, resources), 5, 3));
        ALL_CHORDS.add(new Chord(7, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(6)}, MyApplication.readResource(R.string.chord_smanjeni_kvintakord, resources), 6, 3));
        ALL_CHORDS.add(new Chord(8, new Interval[]{IntervalsList.getInterval(6), IntervalsList.getInterval(3)}, MyApplication.readResource(R.string.chord_smanjeni_kvintakord, resources), 6, 4));

        // Povecani 5 3 - zvuči jednako u svim oblicima (obratima)
        ALL_CHORDS.add(new Chord(9, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(4)}, MyApplication.readResource(R.string.chord_povecani, resources), 5, 3));


        // Dominantni (mali durski) 7
        ALL_CHORDS.add(new Chord(10, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(3), IntervalsList.getInterval(3)}, MyApplication.readResource(R.string.chord_dominantni, resources), 7));
        ALL_CHORDS.add(new Chord(11, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(3), IntervalsList.getInterval(2)}, MyApplication.readResource(R.string.chord_dominantni, resources), 6, 5));
        ALL_CHORDS.add(new Chord(12, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(2), IntervalsList.getInterval(4)}, MyApplication.readResource(R.string.chord_dominantni, resources), 4, 3));
        ALL_CHORDS.add(new Chord(13, new Interval[]{IntervalsList.getInterval(2), IntervalsList.getInterval(4), IntervalsList.getInterval(3)}, MyApplication.readResource(R.string.chord_dominantni, resources), 2));


        // Mnogostranost 7 u duru

        // Veliki durski 7
        ALL_CHORDS.add(new Chord(14, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(3), IntervalsList.getInterval(4)}, MyApplication.readResource(R.string.chord_veliki_durski, resources), 7));
        ALL_CHORDS.add(new Chord(15, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(4), IntervalsList.getInterval(1)}, MyApplication.readResource(R.string.chord_veliki_durski, resources), 6, 5));
        ALL_CHORDS.add(new Chord(16, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(1), IntervalsList.getInterval(4)}, MyApplication.readResource(R.string.chord_veliki_durski, resources), 4, 3));
        ALL_CHORDS.add(new Chord(17, new Interval[]{IntervalsList.getInterval(1), IntervalsList.getInterval(4), IntervalsList.getInterval(3)}, MyApplication.readResource(R.string.chord_veliki_durski, resources), 2));

        // Mali molski 7
        ALL_CHORDS.add(new Chord(18, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(4), IntervalsList.getInterval(3)}, MyApplication.readResource(R.string.chord_mali_molski, resources), 7));
        ALL_CHORDS.add(new Chord(19, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(3), IntervalsList.getInterval(2)}, MyApplication.readResource(R.string.chord_mali_molski, resources), 6, 5));
        ALL_CHORDS.add(new Chord(20, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(2), IntervalsList.getInterval(3)}, MyApplication.readResource(R.string.chord_mali_molski, resources), 4, 3));
        ALL_CHORDS.add(new Chord(21, new Interval[]{IntervalsList.getInterval(2), IntervalsList.getInterval(3), IntervalsList.getInterval(4)}, MyApplication.readResource(R.string.chord_mali_molski, resources), 2));

        // Mali smanjeni 7
        ALL_CHORDS.add(new Chord(22, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(3), IntervalsList.getInterval(4)}, MyApplication.readResource(R.string.chord_mali_smanjeni, resources), 7));
        ALL_CHORDS.add(new Chord(23, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(4), IntervalsList.getInterval(2)}, MyApplication.readResource(R.string.chord_mali_smanjeni, resources), 6, 5));
        ALL_CHORDS.add(new Chord(24, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(2), IntervalsList.getInterval(3)}, MyApplication.readResource(R.string.chord_mali_smanjeni, resources), 4, 3));
        ALL_CHORDS.add(new Chord(25, new Interval[]{IntervalsList.getInterval(2), IntervalsList.getInterval(3), IntervalsList.getInterval(3)}, MyApplication.readResource(R.string.chord_mali_smanjeni, resources), 2));


        // Mnogostranost 7 u molu

        // Veliki molski 7
        ALL_CHORDS.add(new Chord(26, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(4), IntervalsList.getInterval(4)}, MyApplication.readResource(R.string.chord_veliki_molski, resources), 7));
        ALL_CHORDS.add(new Chord(27, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(4), IntervalsList.getInterval(1)}, MyApplication.readResource(R.string.chord_veliki_molski, resources), 6, 5));
        ALL_CHORDS.add(new Chord(28, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(1), IntervalsList.getInterval(3)}, MyApplication.readResource(R.string.chord_veliki_molski, resources), 4, 3));
        ALL_CHORDS.add(new Chord(29, new Interval[]{IntervalsList.getInterval(1), IntervalsList.getInterval(3), IntervalsList.getInterval(4)}, MyApplication.readResource(R.string.chord_veliki_molski, resources), 2));

        // Povećani (veliki povećani) 7
        ALL_CHORDS.add(new Chord(30, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(4), IntervalsList.getInterval(3)}, MyApplication.readResource(R.string.chord_veliki_povecani, resources), 7));
        ALL_CHORDS.add(new Chord(31, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(3), IntervalsList.getInterval(1)}, MyApplication.readResource(R.string.chord_veliki_povecani, resources), 6, 5));
        ALL_CHORDS.add(new Chord(32, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(1), IntervalsList.getInterval(4)}, MyApplication.readResource(R.string.chord_veliki_povecani, resources), 4, 3));
        ALL_CHORDS.add(new Chord(33, new Interval[]{IntervalsList.getInterval(1), IntervalsList.getInterval(4), IntervalsList.getInterval(4)}, MyApplication.readResource(R.string.chord_veliki_povecani, resources), 2));

        // Smanjeni (smanjeno smanjeni) 7 - zvuči jednako u svim oblicima (obratima)
        ALL_CHORDS.add(new Chord(34, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(3), IntervalsList.getInterval(3)}, MyApplication.readResource(R.string.chord_smanjeni_septakord, resources), 7));


        // Veliki dominantni 9
        ALL_CHORDS.add(new Chord(35, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(3), IntervalsList.getInterval(3), IntervalsList.getInterval(4)}, MyApplication.readResource(R.string.chord_veliki_dominantni, resources), 9));

        // Mali durski/(dominantni) 9
        ALL_CHORDS.add(new Chord(36, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(3), IntervalsList.getInterval(3), IntervalsList.getInterval(3)}, MyApplication.readResource(R.string.chord_mali_durski_nonakord, resources), 9));

        // Mali molski 9
        ALL_CHORDS.add(new Chord(37, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(4), IntervalsList.getInterval(3), IntervalsList.getInterval(4)}, MyApplication.readResource(R.string.chord_mali_molski_nonakord, resources), 9));

        // Veliki durski 9
        ALL_CHORDS.add(new Chord(38, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(3), IntervalsList.getInterval(4), IntervalsList.getInterval(3)}, MyApplication.readResource(R.string.chord_veliki_durski, resources), 9));


    }

    private ChordsList() {}

    public static int getChordsCount() {
        return ALL_CHORDS.size();
    }

    // Update names depending on language and set locale language
    public static void updateAllChordsNames(Context tempContext) {
        // Set language
        Context context = LocaleHelper.setLocale(tempContext, LocaleHelper.getLanguageLabel());
        Resources resources = context.getResources();

        getChord(0).setName(MyApplication.readResource(R.string.chord_durski, resources));
        getChord(1).setName(MyApplication.readResource(R.string.chord_durski, resources));
        getChord(2).setName(MyApplication.readResource(R.string.chord_durski, resources));

        getChord(3).setName(MyApplication.readResource(R.string.chord_molski, resources));
        getChord(4).setName(MyApplication.readResource(R.string.chord_molski, resources));
        getChord(5).setName(MyApplication.readResource(R.string.chord_molski, resources));

        getChord(6).setName(MyApplication.readResource(R.string.chord_smanjeni_kvintakord, resources));
        getChord(7).setName(MyApplication.readResource(R.string.chord_smanjeni_kvintakord, resources));
        getChord(8).setName(MyApplication.readResource(R.string.chord_smanjeni_kvintakord, resources));

        getChord(9).setName(MyApplication.readResource(R.string.chord_povecani, resources));

        getChord(10).setName(MyApplication.readResource(R.string.chord_dominantni, resources));
        getChord(11).setName(MyApplication.readResource(R.string.chord_dominantni, resources));
        getChord(12).setName(MyApplication.readResource(R.string.chord_dominantni, resources));
        getChord(13).setName(MyApplication.readResource(R.string.chord_dominantni, resources));

        getChord(14).setName(MyApplication.readResource(R.string.chord_veliki_durski, resources));
        getChord(15).setName(MyApplication.readResource(R.string.chord_veliki_durski, resources));
        getChord(16).setName(MyApplication.readResource(R.string.chord_veliki_durski, resources));
        getChord(17).setName(MyApplication.readResource(R.string.chord_veliki_durski, resources));

        getChord(18).setName(MyApplication.readResource(R.string.chord_mali_molski, resources));
        getChord(19).setName(MyApplication.readResource(R.string.chord_mali_molski, resources));
        getChord(20).setName(MyApplication.readResource(R.string.chord_mali_molski, resources));
        getChord(21).setName(MyApplication.readResource(R.string.chord_mali_molski, resources));

        getChord(22).setName(MyApplication.readResource(R.string.chord_mali_smanjeni, resources));
        getChord(23).setName(MyApplication.readResource(R.string.chord_mali_smanjeni, resources));
        getChord(24).setName(MyApplication.readResource(R.string.chord_mali_smanjeni, resources));
        getChord(25).setName(MyApplication.readResource(R.string.chord_mali_smanjeni, resources));

        getChord(26).setName(MyApplication.readResource(R.string.chord_veliki_molski, resources));
        getChord(27).setName(MyApplication.readResource(R.string.chord_veliki_molski, resources));
        getChord(28).setName(MyApplication.readResource(R.string.chord_veliki_molski, resources));
        getChord(29).setName(MyApplication.readResource(R.string.chord_veliki_molski, resources));

        getChord(30).setName(MyApplication.readResource(R.string.chord_veliki_povecani, resources));
        getChord(31).setName(MyApplication.readResource(R.string.chord_veliki_povecani, resources));
        getChord(32).setName(MyApplication.readResource(R.string.chord_veliki_povecani, resources));
        getChord(33).setName(MyApplication.readResource(R.string.chord_veliki_povecani, resources));

        getChord(34).setName(MyApplication.readResource(R.string.chord_smanjeni_septakord, resources));

        getChord(35).setName(MyApplication.readResource(R.string.chord_veliki_dominantni, resources));
        getChord(36).setName(MyApplication.readResource(R.string.chord_mali_durski_nonakord, resources));
        getChord(37).setName(MyApplication.readResource(R.string.chord_mali_molski_nonakord, resources));
        getChord(38).setName(MyApplication.readResource(R.string.chord_veliki_durski_nonakord, resources));

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
