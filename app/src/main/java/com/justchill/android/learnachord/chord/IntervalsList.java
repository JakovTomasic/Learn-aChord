package com.justchill.android.learnachord.chord;

import android.content.Context;
import android.content.res.Resources;

import com.justchill.android.learnachord.LocaleHelper;
import com.justchill.android.learnachord.MyApplication;
import com.justchill.android.learnachord.R;

import java.util.ArrayList;
import java.util.Random;

public final class IntervalsList {

    private static final ArrayList<Interval> ALL_INTERVALS = new ArrayList<>();

    static {
        Context context = LocaleHelper.setLocale(MyApplication.getAppContext(), LocaleHelper.getLanguageLabel());
        Resources resources = context.getResources();

        ALL_INTERVALS.add(new Interval(0, readResource(R.string.interval_cista_prima, resources)));
        ALL_INTERVALS.add(new Interval(1, readResource(R.string.interval_mala_sekunda, resources)));
        ALL_INTERVALS.add(new Interval(2, readResource(R.string.interval_velika_sekunda, resources)));
        ALL_INTERVALS.add(new Interval(3, readResource(R.string.interval_mala_terca, resources)));
        ALL_INTERVALS.add(new Interval(4, readResource(R.string.interval_velika_terca, resources)));
//        ALL_INTERVALS.add(new Interval(4, readResource(R.string.interval_smanjena_kvarta, resources)));
        ALL_INTERVALS.add(new Interval(5, readResource(R.string.interval_cista_kvarta, resources)));
        ALL_INTERVALS.add(new Interval(6, readResource(R.string.interval_povecana_kvarta, resources) + "/" +
                readResource(R.string.interval_smanjena_kvinta, resources)));
//        ALL_INTERVALS.add(new Interval(6, readResource(R.string.interval_smanjena_kvinta, resources)));
        ALL_INTERVALS.add(new Interval(7, readResource(R.string.interval_cista_kvinta, resources)));
//        ALL_INTERVALS.add(new Interval(8, readResource(R.string.interval_povecana_kvinta, resources)));
        ALL_INTERVALS.add(new Interval(8, readResource(R.string.interval_mala_seksta, resources)));
        ALL_INTERVALS.add(new Interval(9, readResource(R.string.interval_velika_seksta, resources)));
        ALL_INTERVALS.add(new Interval(10, readResource(R.string.interval_mala_septima, resources)));
        ALL_INTERVALS.add(new Interval(11, readResource(R.string.interval_velika_septima, resources)));
        ALL_INTERVALS.add(new Interval(12, readResource(R.string.interval_cista_oktava, resources)));

        ALL_INTERVALS.add(new Interval(13, readResource(R.string.interval_mala_nona, resources)));
        ALL_INTERVALS.add(new Interval(14, readResource(R.string.interval_velika_nona, resources)));
        ALL_INTERVALS.add(new Interval(15, readResource(R.string.interval_mala_decima, resources)));
        ALL_INTERVALS.add(new Interval(16, readResource(R.string.interval_velika_decima, resources)));
        ALL_INTERVALS.add(new Interval(17, readResource(R.string.interval_cista_undecima, resources)));
        ALL_INTERVALS.add(new Interval(18, readResource(R.string.interval_povecana_undecima, resources) + "/" +
                readResource(R.string.interval_smanjena_duodecima, resources)));
        ALL_INTERVALS.add(new Interval(19, readResource(R.string.interval_cista_duodecima, resources)));
        ALL_INTERVALS.add(new Interval(20, readResource(R.string.interval_mala_tercdecima, resources)));
        ALL_INTERVALS.add(new Interval(21, readResource(R.string.interval_velika_tercdecima, resources)));
        ALL_INTERVALS.add(new Interval(22, readResource(R.string.interval_mala_kvartdecima, resources)));
        ALL_INTERVALS.add(new Interval(23, readResource(R.string.interval_velika_kvartdecima, resources)));
        ALL_INTERVALS.add(new Interval(24, readResource(R.string.interval_cista_kvintdecima, resources)));
    }

    private IntervalsList() {}

    private static String readResource(int id, Resources resources) {
        if(resources == null) {
            resources = MyApplication.getAppContext().getResources();
        }
        return resources.getString(id);
    }

    public static int getIntervalsCount() {
        return ALL_INTERVALS.size();
    }

    public static void updateAllIntervalsNames(Context tempContext) {
        Context context = LocaleHelper.setLocale(tempContext, LocaleHelper.getLanguageLabel());
        Resources resources = context.getResources();

        getInterval(0).setIntervalName(readResource(R.string.interval_cista_prima, resources));
        getInterval(1).setIntervalName(readResource(R.string.interval_mala_sekunda, resources));
        getInterval(2).setIntervalName(readResource(R.string.interval_velika_sekunda, resources));
        getInterval(3).setIntervalName(readResource(R.string.interval_mala_terca, resources));
        getInterval(4).setIntervalName(readResource(R.string.interval_velika_terca, resources));
//        getInterval(4).setIntervalName(readResource(R.string.interval_smanjena_kvarta, resources));
        getInterval(5).setIntervalName(readResource(R.string.interval_cista_kvarta, resources));
        getInterval(6).setIntervalName(readResource(R.string.interval_povecana_kvarta, resources) + "/" +
                readResource(R.string.interval_smanjena_kvinta, resources));
//        getInterval(6).setIntervalName(readResource(R.string.interval_smanjena_kvinta, resources));
        getInterval(7).setIntervalName(readResource(R.string.interval_cista_kvinta, resources));
//        getInterval(8).setIntervalName(readResource(R.string.interval_povecana_kvinta, resources));
        getInterval(8).setIntervalName(readResource(R.string.interval_mala_seksta, resources));
        getInterval(9).setIntervalName(readResource(R.string.interval_velika_seksta, resources));
        getInterval(10).setIntervalName(readResource(R.string.interval_mala_septima, resources));
        getInterval(11).setIntervalName(readResource(R.string.interval_velika_septima, resources));
        getInterval(12).setIntervalName(readResource(R.string.interval_cista_oktava, resources));

        getInterval(13).setIntervalName(readResource(R.string.interval_mala_nona, resources));
        getInterval(14).setIntervalName(readResource(R.string.interval_velika_nona, resources));
        getInterval(15).setIntervalName(readResource(R.string.interval_mala_decima, resources));
        getInterval(16).setIntervalName(readResource(R.string.interval_velika_decima, resources));
        getInterval(17).setIntervalName(readResource(R.string.interval_cista_undecima, resources));
        getInterval(18).setIntervalName(readResource(R.string.interval_povecana_undecima, resources) + "/" +
                readResource(R.string.interval_smanjena_duodecima, resources));
        getInterval(19).setIntervalName(readResource(R.string.interval_cista_duodecima, resources));
        getInterval(20).setIntervalName(readResource(R.string.interval_mala_tercdecima, resources));
        getInterval(21).setIntervalName(readResource(R.string.interval_velika_tercdecima, resources));
        getInterval(22).setIntervalName(readResource(R.string.interval_mala_kvartdecima, resources));
        getInterval(23).setIntervalName(readResource(R.string.interval_velika_kvartdecima, resources));
        getInterval(24).setIntervalName(readResource(R.string.interval_cista_kvintdecima, resources));

    }

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

    public static int getCheckedIntervalCount() {
        int counter = 0;
        for(int i = 0; i < getIntervalsCount(); i++) {
            if(getInterval(i).getIsChecked()) {
                counter++;
            }
        }
        return counter;
    }

    public static int getCheckedIntervalCountIncludingRange() {
        int counter = 0;
        for(int i = 0; i < getIntervalsCount(); i++) {
            if(!isIntervalInsideBorders(getInterval(i))) {
                return counter;
            }
            if(getInterval(i).getIsChecked()) {
                counter++;
            }
        }
        return counter;
    }

    public static int getPlayableIntervalsCount() {
        int counter = 0;
        for(int i = 0; i < getIntervalsCount(); i++) {
            if(!isIntervalInsideBorders(getInterval(i))) {
                return counter;
            }
            if(getInterval(i).isPlayableCountdownFinished() && getInterval(i).getIsChecked()) {
                counter++;
            }
        }
        return counter;
    }

    public static boolean isIntervalPlayable(Interval interval) {
        return interval.isPlayableCountdownFinished() && interval.getIsChecked() && isIntervalInsideBorders(interval);
    }

    private static Interval mustBePlayed() {
        for(int i = 0; i < getIntervalsCount(); i++) {
            if(!isIntervalInsideBorders(getInterval(i))) {
                return null;
            }
            // TODO: maybe change this hardcoded constant
            if(getInterval(i).getIsChecked() && getInterval(i).notPlayedFor > (ChordsList.getPlayableChordsCount() + getPlayableIntervalsCount()) * 2) {
                return getInterval(i);
            }
        }
        return null;
    }

    public static Interval getRandomPlayableInterval() {
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

    public static void tickAllPlayableCountdowns() {
        for(int i = 0; i < getIntervalsCount(); i++) {
            getInterval(i).tickPlayableCountdown();
        }
    }

    public static void increaseNotPlayedFor() {
        for(int i = 0; i < getIntervalsCount(); i++) {
            getInterval(i).notPlayedFor++;
        }
    }

    public static void setAllIntervalsIsChecked(boolean myBool) {
        for(int i = 0; i < getIntervalsCount(); i++) {
            getInterval(i).setIsChecked(myBool);
        }
    }

    public static Interval getBiggestCheckedInterval() {
        for(int i = getIntervalsCount()-1; i >= 0; i--) {
            if(getInterval(i).getIsChecked()) {
                return getInterval(i);
            }
        }
        return null;
    }

    public static boolean isIntervalInsideBorders(Interval interval) {
        return interval.getDifference() <= MyApplication.upKeyBorder - MyApplication.downKeyBorder;
    }

    public static void uncheckOutOfRangeIntervals() {
        for(int i = getIntervalsCount()-1; i >= 0; i--) {
            if(!isIntervalInsideBorders(getInterval(i))) {
                getInterval(i).setIsChecked(false);
            } else {
                break;
            }
        }
    }

    public static Interval getRandomCheckedInterval(Interval exception) {
        int checkedIntervals = getCheckedIntervalCount();
        if(exception != null) {
            checkedIntervals--; // -1 for exception
        }
        if(checkedIntervals <= 0) {
            return null;
        }

        // Get checked interval that is numbered as randomNumb (not counting unchecked ones)
        Random random = new Random();
        int randomNumb = random.nextInt(checkedIntervals);
        int counter = 0;
        for(int i = 0; i < getIntervalsCount(); i++) {
            if(getInterval(i).getIsChecked() && getInterval(i) != exception) {
                if(randomNumb == counter) {
                    return getInterval(i);
                }
                counter++;
            }
        }

        // If nothing was returned, return first checked interval that is not exception
        for(int i = 0; i < getIntervalsCount(); i++) {
            if(getInterval(i).getIsChecked() && getInterval(i) != exception) {
                return getInterval(i);
            }
        }

        // Otherwise, return null
        return null;
    }

}
