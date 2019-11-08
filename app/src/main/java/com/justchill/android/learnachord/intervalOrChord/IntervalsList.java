package com.justchill.android.learnachord.intervalOrChord;

import com.justchill.android.learnachord.MyApplication;
import com.justchill.android.learnachord.R;
import com.justchill.android.learnachord.database.DatabaseData;

import java.util.ArrayList;
import java.util.Random;

// List of all intervals and methods for using that list
public final class IntervalsList {

    // List of all intervals, ordered by size (ascending)
    private static final ArrayList<Interval> ALL_INTERVALS = new ArrayList<>();

    // Static initializer. This is run the first time anything is called from this class.
    static {

        ALL_INTERVALS.add(new Interval(0, MyApplication.getStringByLocal(R.string.interval_cista_prima)));
        ALL_INTERVALS.add(new Interval(1, MyApplication.getStringByLocal(R.string.interval_mala_sekunda)));
        ALL_INTERVALS.add(new Interval(2, MyApplication.getStringByLocal(R.string.interval_velika_sekunda)));
        ALL_INTERVALS.add(new Interval(3, MyApplication.getStringByLocal(R.string.interval_mala_terca)));
        ALL_INTERVALS.add(new Interval(4, MyApplication.getStringByLocal(R.string.interval_velika_terca)));
//        ALL_INTERVALS.add(new Interval(4, readResource(R.string.interval_smanjena_kvarta, resources)));
        ALL_INTERVALS.add(new Interval(5, MyApplication.getStringByLocal(R.string.interval_cista_kvarta)));
        ALL_INTERVALS.add(new Interval(6, MyApplication.getStringByLocal(R.string.interval_povecana_kvarta) + "/" +
                MyApplication.getStringByLocal(R.string.interval_smanjena_kvinta)));
//        ALL_INTERVALS.add(new Interval(6, readResource(R.string.interval_smanjena_kvinta, resources)));
        ALL_INTERVALS.add(new Interval(7, MyApplication.getStringByLocal(R.string.interval_cista_kvinta)));
//        ALL_INTERVALS.add(new Interval(8, readResource(R.string.interval_povecana_kvinta, resources)));
        ALL_INTERVALS.add(new Interval(8, MyApplication.getStringByLocal(R.string.interval_mala_seksta)));
        ALL_INTERVALS.add(new Interval(9, MyApplication.getStringByLocal(R.string.interval_velika_seksta)));
        ALL_INTERVALS.add(new Interval(10, MyApplication.getStringByLocal(R.string.interval_mala_septima)));
        ALL_INTERVALS.add(new Interval(11, MyApplication.getStringByLocal(R.string.interval_velika_septima)));
        ALL_INTERVALS.add(new Interval(12, MyApplication.getStringByLocal(R.string.interval_cista_oktava)));

        ALL_INTERVALS.add(new Interval(13, MyApplication.getStringByLocal(R.string.interval_mala_nona)));
        ALL_INTERVALS.add(new Interval(14, MyApplication.getStringByLocal(R.string.interval_velika_nona)));
        ALL_INTERVALS.add(new Interval(15, MyApplication.getStringByLocal(R.string.interval_mala_decima)));
        ALL_INTERVALS.add(new Interval(16, MyApplication.getStringByLocal(R.string.interval_velika_decima)));
        ALL_INTERVALS.add(new Interval(17, MyApplication.getStringByLocal(R.string.interval_cista_undecima)));
        ALL_INTERVALS.add(new Interval(18, MyApplication.getStringByLocal(R.string.interval_povecana_undecima) + "/" +
                MyApplication.getStringByLocal(R.string.interval_smanjena_duodecima)));
        ALL_INTERVALS.add(new Interval(19, MyApplication.getStringByLocal(R.string.interval_cista_duodecima)));
        ALL_INTERVALS.add(new Interval(20, MyApplication.getStringByLocal(R.string.interval_mala_tercdecima)));
        ALL_INTERVALS.add(new Interval(21, MyApplication.getStringByLocal(R.string.interval_velika_tercdecima)));
        ALL_INTERVALS.add(new Interval(22, MyApplication.getStringByLocal(R.string.interval_mala_kvartdecima)));
        ALL_INTERVALS.add(new Interval(23, MyApplication.getStringByLocal(R.string.interval_velika_kvartdecima)));
        ALL_INTERVALS.add(new Interval(24, MyApplication.getStringByLocal(R.string.interval_cista_kvintdecima)));
    }

    private IntervalsList() {}

    public static int getIntervalsCount() {
        return ALL_INTERVALS.size();
    }

    // Update names depending on language and set locale language
    public static void updateAllIntervalsNames() {
        getInterval(0).setName(MyApplication.getStringByLocal(R.string.interval_cista_prima));
        getInterval(1).setName(MyApplication.getStringByLocal(R.string.interval_mala_sekunda));
        getInterval(2).setName(MyApplication.getStringByLocal(R.string.interval_velika_sekunda));
        getInterval(3).setName(MyApplication.getStringByLocal(R.string.interval_mala_terca));
        getInterval(4).setName(MyApplication.getStringByLocal(R.string.interval_velika_terca));
//        getInterval(4).setName(readResource(R.string.interval_smanjena_kvarta, resources));
        getInterval(5).setName(MyApplication.getStringByLocal(R.string.interval_cista_kvarta));
        getInterval(6).setName(MyApplication.getStringByLocal(R.string.interval_povecana_kvarta) + "/" +
                MyApplication.getStringByLocal(R.string.interval_smanjena_kvinta));
//        getInterval(6).setName(readResource(R.string.interval_smanjena_kvinta, resources));
        getInterval(7).setName(MyApplication.getStringByLocal(R.string.interval_cista_kvinta));
//        getInterval(8).setName(readResource(R.string.interval_povecana_kvinta, resources));
        getInterval(8).setName(MyApplication.getStringByLocal(R.string.interval_mala_seksta));
        getInterval(9).setName(MyApplication.getStringByLocal(R.string.interval_velika_seksta));
        getInterval(10).setName(MyApplication.getStringByLocal(R.string.interval_mala_septima));
        getInterval(11).setName(MyApplication.getStringByLocal(R.string.interval_velika_septima));
        getInterval(12).setName(MyApplication.getStringByLocal(R.string.interval_cista_oktava));

        getInterval(13).setName(MyApplication.getStringByLocal(R.string.interval_mala_nona));
        getInterval(14).setName(MyApplication.getStringByLocal(R.string.interval_velika_nona));
        getInterval(15).setName(MyApplication.getStringByLocal(R.string.interval_mala_decima));
        getInterval(16).setName(MyApplication.getStringByLocal(R.string.interval_velika_decima));
        getInterval(17).setName(MyApplication.getStringByLocal(R.string.interval_cista_undecima));
        getInterval(18).setName(MyApplication.getStringByLocal(R.string.interval_povecana_undecima) + "/" +
                MyApplication.getStringByLocal(R.string.interval_smanjena_duodecima));
        getInterval(19).setName(MyApplication.getStringByLocal(R.string.interval_cista_duodecima));
        getInterval(20).setName(MyApplication.getStringByLocal(R.string.interval_mala_tercdecima));
        getInterval(21).setName(MyApplication.getStringByLocal(R.string.interval_velika_tercdecima));
        getInterval(22).setName(MyApplication.getStringByLocal(R.string.interval_mala_kvartdecima));
        getInterval(23).setName(MyApplication.getStringByLocal(R.string.interval_velika_kvartdecima));
        getInterval(24).setName(MyApplication.getStringByLocal(R.string.interval_cista_kvintdecima));

    }

    // Get interval with given id
    public static Interval getInterval(int id) {
        if(id >= getIntervalsCount()) {
            id = getIntervalsCount()-1;
        }
        if(id < 0) {
            id = 0;
        }
        return ALL_INTERVALS.get(id);
    }

    public static ArrayList<Interval> getAllIntervals() {
        return ALL_INTERVALS;
    }

    // Returns how many intervals have been checked
    public static int getCheckedIntervalCount() {
        int counter = 0;
        for(int i = 0; i < getIntervalsCount(); i++) {
            if(getInterval(i).getIsChecked()) {
                counter++;
            }
        }
        return counter;
    }

    // Returns how many intervals have been checked and can be played in given range
    public static int getCheckedIntervalCountIncludingRange() {
        int counter = 0;
        for(int i = 0; i < getIntervalsCount(); i++) {
            // List is ordered, if intervals is too big, return counter
            if(!isIntervalInsideBorders(getInterval(i))) {
                return counter;
            }
            if(getInterval(i).getIsChecked()) {
                counter++;
            }
        }
        return counter;
    }

    // Get number of intervals that can be played
    public static int getPlayableIntervalsCount() {
        int counter = 0;
        for(int i = 0; i < getIntervalsCount(); i++) {
            // List is ordered, if intervals is too big, return counter
            if(!isIntervalInsideBorders(getInterval(i))) {
                return counter;
            }
            if(getInterval(i).isPlayableCountdownFinished() && getInterval(i).getIsChecked()) {
                counter++;
            }
        }
        return counter;
    }

    // Check if interval can be played
    public static boolean isIntervalPlayable(Interval interval) {
        return interval.isPlayableCountdownFinished() && interval.getIsChecked() && isIntervalInsideBorders(interval);
    }

    // Return interval if it has not been played for too long time
    private static Interval mustBePlayed() {
        for(int i = 0; i < getIntervalsCount(); i++) {
            // List is ordered, if intervals is too big, it's not playable (and all bigger intervals) - return null
            if(!isIntervalInsideBorders(getInterval(i))) {
                return null;
            }
            if(getInterval(i).getIsChecked() && getInterval(i).notPlayedFor > (ChordsList.getPlayableChordsCount() + getPlayableIntervalsCount()) * 2) {
                return getInterval(i);
            }
        }
        return null;
    }

    // Returns random interval that can be played
    public static Interval getRandomPlayableInterval() {
        // First checks if some interval must be played
        Interval mustBePlayedInterval = mustBePlayed();
        if(mustBePlayedInterval != null) {
            return mustBePlayedInterval;
        }

        Random random = new Random();
        int checkedIntervals = getCheckedIntervalCountIncludingRange();
        int playableIntervals = getPlayableIntervalsCount();
        int counter = 0;

        // If there is no playable intervals return random checked interval that is inside borders
        if(playableIntervals <= 0) {
            if(checkedIntervals <= 0) {
                return null;
            }
            int randNum = random.nextInt(checkedIntervals); // 0 to (number of checked intervals)-1 (including them)
            for(int i = 0; i < getIntervalsCount(); i++) {
                if(counter == randNum && getInterval(i).getIsChecked() && isIntervalInsideBorders(getInterval(i))) {
                    return getInterval(i);
                }
                if(getInterval(i).getIsChecked() && isIntervalInsideBorders(getInterval(i))) {
                    counter++;
                }
            }
            return null;
        }

//        Log.d("####", "################################################################# " + playableIntervals);

        int randNum = random.nextInt(playableIntervals); // 0 to getPlayableIntervalsCount()-1 (including them)
        for(int i = 0; i < getIntervalsCount(); i++) {
            if(counter >= randNum && isIntervalPlayable(getInterval(i))) {
                return getInterval(i);
            }
            if(isIntervalPlayable(getInterval(i))) {
                counter++;
            }
        }
        return null;
    }

    // Decrease playing delay for all intervals
    public static void tickAllPlayableCountdowns() {
        for(int i = 0; i < getIntervalsCount(); i++) {
            getInterval(i).tickPlayableCountdown();
        }
    }

    // Increase counter for how long interval hasn't been played (for all intervals)
    public static void increaseNotPlayedFor() {
        for(int i = 0; i < getIntervalsCount(); i++) {
            getInterval(i).notPlayedFor++;
        }
    }

    // Check or uncheck all intervals in options
    public static void setAllIntervalsIsChecked(boolean myBool) {
        for(int i = 0; i < getIntervalsCount(); i++) {
            getInterval(i).setIsChecked(myBool);
        }
    }

    // Get checked interval with biggest total range
    public static Interval getBiggestCheckedInterval() {
        for(int i = getIntervalsCount()-1; i >= 0; i--) {
            if(getInterval(i).getIsChecked()) {
                return getInterval(i);
            }
        }
        return null;
    }

    // Check if interval can be played inside set range
    public static boolean isIntervalInsideBorders(Interval interval) {
        return interval.getDifference() <= DatabaseData.upKeyBorder - DatabaseData.downKeyBorder;
    }

    // Uncheck all intervals that can't be played inside set range
    public static void uncheckOutOfRangeIntervals() {
        for(int i = getIntervalsCount()-1; i >= 0; i--) {
            if(!isIntervalInsideBorders(getInterval(i))) {
                getInterval(i).setIsChecked(false);
            } else {
                break;
            }
        }
    }

    // Returns random interval that is checked
    public static Interval getRandomCheckedInterval(Interval... exception) {
        int checkedIntervals = getCheckedIntervalCount();
        if(exception != null) {
            int subtract = 0;
            for(Interval ex : exception) {
                if(ex != null) {
                    subtract++;
                }
            }
            checkedIntervals -= subtract; // - number of exceptions
        }
        if(checkedIntervals <= 0) {
            return null;
        }

        // Get checked interval that is numbered as randomNumb (not counting unchecked ones)
        Random random = new Random();
        int randomNumb = random.nextInt(checkedIntervals);
        int counter = 0;
        for(int i = 0; i < getIntervalsCount(); i++) {
            if(getInterval(i).getIsChecked() && !isInside(getInterval(i), exception)) {
                if(randomNumb == counter) {
                    return getInterval(i);
                }
                counter++;
            }
        }

        // If nothing was returned, return first checked interval that is not exception
        for(int i = 0; i < getIntervalsCount(); i++) {
            if(getInterval(i).getIsChecked() && !isInside(getInterval(i), exception)) {
                return getInterval(i);
            }
        }

        // Otherwise, return null
        return null;
    }

    // Check if interval is inside list of intervals
    private static boolean isInside(Interval number, Interval... newInt) {
        if(newInt == null) {
            return false;
        }

        for (Interval aNewInt : newInt) {
            if (number == aNewInt) {
                return true;
            }
        }
        return false;
    }

}
