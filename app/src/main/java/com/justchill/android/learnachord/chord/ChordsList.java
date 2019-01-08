package com.justchill.android.learnachord.chord;

import android.content.Context;
import android.content.res.Resources;

import com.justchill.android.learnachord.LocaleHelper;
import com.justchill.android.learnachord.MyApplication;
import com.justchill.android.learnachord.R;

import java.util.ArrayList;
import java.util.Random;

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

    private static final ArrayList<Chord> ALL_CHORDS = new ArrayList<>(); // Osnovni akordi (5 3, 7 & 9)

    static {
        Context context = LocaleHelper.setLocale(MyApplication.getAppContext(), LocaleHelper.getLanguageLabel());
        Resources resources = context.getResources();

        // Durski 5 3
        ALL_CHORDS.add(new Chord(0, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(3)}, readResource(R.string.chord_durski, resources), 5, 3));
        ALL_CHORDS.add(new Chord(1, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(5)}, readResource(R.string.chord_durski, resources), 6, 3));
        ALL_CHORDS.add(new Chord(2, new Interval[]{IntervalsList.getInterval(5), IntervalsList.getInterval(4)}, readResource(R.string.chord_durski, resources), 6, 4));

        // Molski 5 3
        ALL_CHORDS.add(new Chord(3, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(4)}, readResource(R.string.chord_molski, resources), 5, 3));
        ALL_CHORDS.add(new Chord(4, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(5)}, readResource(R.string.chord_molski, resources), 6, 3));
        ALL_CHORDS.add(new Chord(5, new Interval[]{IntervalsList.getInterval(5), IntervalsList.getInterval(3)}, readResource(R.string.chord_molski, resources), 6, 4));

        // Smanjeni 5 3
        ALL_CHORDS.add(new Chord(6, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(3)}, readResource(R.string.chord_smanjeni_kvintakord, resources), 5, 3));
        ALL_CHORDS.add(new Chord(7, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(6)}, readResource(R.string.chord_smanjeni_kvintakord, resources), 6, 3));
        ALL_CHORDS.add(new Chord(8, new Interval[]{IntervalsList.getInterval(6), IntervalsList.getInterval(3)}, readResource(R.string.chord_smanjeni_kvintakord, resources), 6, 4));

        // Povecani 5 3 - zvuči jednako u svim oblicima (obratima)
        ALL_CHORDS.add(new Chord(9, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(4)}, readResource(R.string.chord_povecani, resources), 5, 3));


        // Dominantni (mali durski) 7
        ALL_CHORDS.add(new Chord(10, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(3), IntervalsList.getInterval(3)}, readResource(R.string.chord_dominantni, resources), 7));
        ALL_CHORDS.add(new Chord(11, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(3), IntervalsList.getInterval(2)}, readResource(R.string.chord_dominantni, resources), 6, 5));
        ALL_CHORDS.add(new Chord(12, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(2), IntervalsList.getInterval(4)}, readResource(R.string.chord_dominantni, resources), 4, 3));
        ALL_CHORDS.add(new Chord(13, new Interval[]{IntervalsList.getInterval(2), IntervalsList.getInterval(4), IntervalsList.getInterval(3)}, readResource(R.string.chord_dominantni, resources), 2));


        // Mnogostranost 7 u duru

        // Veliki durski 7
        ALL_CHORDS.add(new Chord(14, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(3), IntervalsList.getInterval(4)}, readResource(R.string.chord_veliki_durski, resources), 7));
        ALL_CHORDS.add(new Chord(15, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(4), IntervalsList.getInterval(1)}, readResource(R.string.chord_veliki_durski, resources), 6, 5));
        ALL_CHORDS.add(new Chord(16, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(1), IntervalsList.getInterval(4)}, readResource(R.string.chord_veliki_durski, resources), 4, 3));
        ALL_CHORDS.add(new Chord(17, new Interval[]{IntervalsList.getInterval(1), IntervalsList.getInterval(4), IntervalsList.getInterval(3)}, readResource(R.string.chord_veliki_durski, resources), 2));

        // Mali molski 7
        ALL_CHORDS.add(new Chord(18, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(4), IntervalsList.getInterval(3)}, readResource(R.string.chord_mali_molski, resources), 7));
        ALL_CHORDS.add(new Chord(19, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(3), IntervalsList.getInterval(2)}, readResource(R.string.chord_mali_molski, resources), 6, 5));
        ALL_CHORDS.add(new Chord(20, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(2), IntervalsList.getInterval(3)}, readResource(R.string.chord_mali_molski, resources), 4, 3));
        ALL_CHORDS.add(new Chord(21, new Interval[]{IntervalsList.getInterval(2), IntervalsList.getInterval(3), IntervalsList.getInterval(4)}, readResource(R.string.chord_mali_molski, resources), 2));

        // Mali smanjeni 7
        ALL_CHORDS.add(new Chord(22, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(3), IntervalsList.getInterval(4)}, readResource(R.string.chord_mali_smanjeni, resources), 7));
        ALL_CHORDS.add(new Chord(23, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(4), IntervalsList.getInterval(2)}, readResource(R.string.chord_mali_smanjeni, resources), 6, 5));
        ALL_CHORDS.add(new Chord(24, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(2), IntervalsList.getInterval(3)}, readResource(R.string.chord_mali_smanjeni, resources), 4, 3));
        ALL_CHORDS.add(new Chord(25, new Interval[]{IntervalsList.getInterval(2), IntervalsList.getInterval(3), IntervalsList.getInterval(3)}, readResource(R.string.chord_mali_smanjeni, resources), 2));


        // Mnogostranost 7 u molu

        // Veliki molski 7
        ALL_CHORDS.add(new Chord(26, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(4), IntervalsList.getInterval(4)}, readResource(R.string.chord_veliki_molski, resources), 7));
        ALL_CHORDS.add(new Chord(27, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(4), IntervalsList.getInterval(1)}, readResource(R.string.chord_veliki_molski, resources), 6, 5));
        ALL_CHORDS.add(new Chord(28, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(1), IntervalsList.getInterval(3)}, readResource(R.string.chord_veliki_molski, resources), 4, 3));
        ALL_CHORDS.add(new Chord(29, new Interval[]{IntervalsList.getInterval(1), IntervalsList.getInterval(3), IntervalsList.getInterval(4)}, readResource(R.string.chord_veliki_molski, resources), 2));

        // Povećani (veliki povećani) 7
        ALL_CHORDS.add(new Chord(30, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(4), IntervalsList.getInterval(3)}, readResource(R.string.chord_veliki_povecani, resources), 7));
        ALL_CHORDS.add(new Chord(31, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(3), IntervalsList.getInterval(1)}, readResource(R.string.chord_veliki_povecani, resources), 6, 5));
        ALL_CHORDS.add(new Chord(32, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(1), IntervalsList.getInterval(4)}, readResource(R.string.chord_veliki_povecani, resources), 4, 3));
        ALL_CHORDS.add(new Chord(33, new Interval[]{IntervalsList.getInterval(1), IntervalsList.getInterval(4), IntervalsList.getInterval(4)}, readResource(R.string.chord_veliki_povecani, resources), 2));

        // Smanjeni (smanjeno smanjeni) 7 - zvuči jednako u svim oblicima (obratima)
        ALL_CHORDS.add(new Chord(34, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(3), IntervalsList.getInterval(3)}, readResource(R.string.chord_smanjeni_septakord, resources), 7));


        // Veliki dominantni 9
        ALL_CHORDS.add(new Chord(35, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(3), IntervalsList.getInterval(3), IntervalsList.getInterval(4)}, readResource(R.string.chord_veliki_dominantni, resources), 9));

        // Mali durski/(dominantni) 9
        ALL_CHORDS.add(new Chord(36, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(3), IntervalsList.getInterval(3), IntervalsList.getInterval(3)}, readResource(R.string.chord_mali_durski_nonakord, resources), 9));

        // Mali molski 9
        ALL_CHORDS.add(new Chord(37, new Interval[]{IntervalsList.getInterval(3), IntervalsList.getInterval(4), IntervalsList.getInterval(3), IntervalsList.getInterval(4)}, readResource(R.string.chord_mali_molski_nonakord, resources), 9));

        // Veliki durski 9
        ALL_CHORDS.add(new Chord(38, new Interval[]{IntervalsList.getInterval(4), IntervalsList.getInterval(3), IntervalsList.getInterval(4), IntervalsList.getInterval(3)}, readResource(R.string.chord_veliki_durski, resources), 9));


    }

    private ChordsList() {}

    private static String readResource(int id, Resources resources) {
        if(resources == null) {
            resources = MyApplication.getAppContext().getResources();
        }
        return resources.getString(id);
    }

    public static int getChordsCount() {
        return ALL_CHORDS.size();
    }

    public static void updateAllChordsNames(Context tempContext) {
        Context context = LocaleHelper.setLocale(tempContext, LocaleHelper.getLanguageLabel());
        Resources resources = context.getResources();

        getChord(0).setChordName(readResource(R.string.chord_durski, resources));
        getChord(1).setChordName(readResource(R.string.chord_durski, resources));
        getChord(2).setChordName(readResource(R.string.chord_durski, resources));

        getChord(3).setChordName(readResource(R.string.chord_molski, resources));
        getChord(4).setChordName(readResource(R.string.chord_molski, resources));
        getChord(5).setChordName(readResource(R.string.chord_molski, resources));

        getChord(6).setChordName(readResource(R.string.chord_smanjeni_kvintakord, resources));
        getChord(7).setChordName(readResource(R.string.chord_smanjeni_kvintakord, resources));
        getChord(8).setChordName(readResource(R.string.chord_smanjeni_kvintakord, resources));

        getChord(9).setChordName(readResource(R.string.chord_povecani, resources));

        getChord(10).setChordName(readResource(R.string.chord_dominantni, resources));
        getChord(11).setChordName(readResource(R.string.chord_dominantni, resources));
        getChord(12).setChordName(readResource(R.string.chord_dominantni, resources));
        getChord(13).setChordName(readResource(R.string.chord_dominantni, resources));

        getChord(14).setChordName(readResource(R.string.chord_veliki_durski, resources));
        getChord(15).setChordName(readResource(R.string.chord_veliki_durski, resources));
        getChord(16).setChordName(readResource(R.string.chord_veliki_durski, resources));
        getChord(17).setChordName(readResource(R.string.chord_veliki_durski, resources));

        getChord(18).setChordName(readResource(R.string.chord_mali_molski, resources));
        getChord(19).setChordName(readResource(R.string.chord_mali_molski, resources));
        getChord(20).setChordName(readResource(R.string.chord_mali_molski, resources));
        getChord(21).setChordName(readResource(R.string.chord_mali_molski, resources));

        getChord(22).setChordName(readResource(R.string.chord_mali_smanjeni, resources));
        getChord(23).setChordName(readResource(R.string.chord_mali_smanjeni, resources));
        getChord(24).setChordName(readResource(R.string.chord_mali_smanjeni, resources));
        getChord(25).setChordName(readResource(R.string.chord_mali_smanjeni, resources));

        getChord(26).setChordName(readResource(R.string.chord_veliki_molski, resources));
        getChord(27).setChordName(readResource(R.string.chord_veliki_molski, resources));
        getChord(28).setChordName(readResource(R.string.chord_veliki_molski, resources));
        getChord(29).setChordName(readResource(R.string.chord_veliki_molski, resources));

        getChord(30).setChordName(readResource(R.string.chord_veliki_povecani, resources));
        getChord(31).setChordName(readResource(R.string.chord_veliki_povecani, resources));
        getChord(32).setChordName(readResource(R.string.chord_veliki_povecani, resources));
        getChord(33).setChordName(readResource(R.string.chord_veliki_povecani, resources));

        getChord(34).setChordName(readResource(R.string.chord_smanjeni_septakord, resources));

        getChord(35).setChordName(readResource(R.string.chord_veliki_dominantni, resources));
        getChord(36).setChordName(readResource(R.string.chord_mali_durski_nonakord, resources));
        getChord(37).setChordName(readResource(R.string.chord_mali_molski_nonakord, resources));
        getChord(38).setChordName(readResource(R.string.chord_veliki_durski_nonakord, resources));

    }

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

    // TODO: smarten this, in IntervalsList too
    public static int getCheckedChordsCount() {
        int counter = 0;
        for(int i = 0; i < getChordsCount(); i++) {
            if(getChord(i).getIsChecked()) {
                counter++;
            }
        }
        return counter;
    }

    public static int getCheckedChordsCountIncludingRange() {
        int counter = 0;
        for(int i = 0; i < getChordsCount(); i++) {
            if(!isChordInsideBorders(getChord(i))) {
                return counter;
            }
            if(getChord(i).getIsChecked()) {
                counter++;
            }
        }
        return counter;
    }

    public static int getPlayableChordsCount() {
        int counter = 0;
        for(int i = 0; i < getChordsCount(); i++) {
            if(!isChordInsideBorders(getChord(i))) {
                return counter;
            }
            if(getChord(i).isPlayableCountdownFinished() && getChord(i).getIsChecked()) {
                counter++;
            }
        }
        return counter;
    }

    public static boolean isChordPlayable(Chord chord) {
        return chord.isPlayableCountdownFinished() && chord.getIsChecked() && isChordInsideBorders(chord);
    }

    private static Chord mustBePlayed() {
        for(int i = 0; i < getChordsCount(); i++) {
            if(!isChordInsideBorders(getChord(i))) {
                return null;
            }
            // TODO: maybe change this hardcoded constant
            if(getChord(i).getIsChecked() && getChord(i).notPlayedFor > (getPlayableChordsCount() + IntervalsList.getPlayableIntervalsCount()) * 2) {
                return getChord(i);
            }
        }
        return null;
    }

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

    // TODO: merge this with intervals
    public static void tickAllPlayableCountdowns() {
        for(int i = 0; i < getChordsCount(); i++) {
            getChord(i).tickPlayableCountdown();
        }
    }

    // TODO: merge this with intervals
    public static void increaseNotPlayedFor() {
        for(int i = 0; i < getChordsCount(); i++) {
            getChord(i).notPlayedFor++;
        }
    }

    public static void setAllChordsIsChecked(boolean myBool) {
        for(int i = 0; i < getChordsCount(); i++) {
            getChord(i).setIsChecked(myBool);
        }
    }

    // TODO: merge this with intervals, maybe
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

    public static boolean isChordInsideBorders(Chord chord) {
        return chord.getDifference() <= MyApplication.upKeyBorder - MyApplication.downKeyBorder;
    }

    public static void uncheckOutOfRangeChords() {
        for(int i = 0; i < getChordsCount(); i++) {
            if(!isChordInsideBorders(getChord(i))) {
                getChord(i).setIsChecked(false);
            }
        }
    }

}
