package com.justchill.android.learnachord;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.justchill.android.learnachord.intervalOrChord.Interval;
import com.justchill.android.learnachord.database.DatabaseData;

public class WhatIntervalsAdapter extends ArrayAdapter<Interval> {

    private static final String LOG_TAG = IntervalAdapter.class.getName();

    private int numberOfIntervalsToShow;
    private Interval[] intervalsToShow;
    private int playingDirection;

    // This needs to be public so it can be accessed from settings package classes
    public WhatIntervalsAdapter(Activity context, Interval[] intervals, int nOfIntervalsToShow, int direction) {
        super(context, 0, intervals);
        numberOfIntervalsToShow = nOfIntervalsToShow;
        intervalsToShow = intervals;
        playingDirection = direction;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View intervalView = convertView;
        if(intervalView == null) {
            intervalView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_interval, parent, false);
        }

        Interval currentInterval = intervalsToShow[intervalsToShow.length-1 - position];

        TextView intervalTextView = intervalView.findViewById(R.id.interval_text_view);


        // textSize -> text size of chord/interval text in MainActivity
        float textSize = (MyApplication.smallerDisplayDimensionPX * 0.75f) / 8 * DatabaseData.scaledDensity;
        intervalTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize / 2.6f);

        if(currentInterval != null) {
            if(currentInterval.getDifference() == 6) {
                // Pov 4
                intervalTextView.setText(MyApplication.getAppContext().getResources().getString(R.string.interval_povecana_kvarta));
            } else {
                intervalTextView.setText(currentInterval.getName());
            }

            // When direction is ascending intervals are showing in reversed direction
            int tempForComparison = position;
            if(playingDirection == MyApplication.directionUpID) {
                tempForComparison = intervalsToShow.length - 1 - position;
            }

            if(tempForComparison < numberOfIntervalsToShow) {
                intervalTextView.setTextColor(readResource(R.color.darkTextColor));
            } else {
                intervalTextView.setTextColor(readResource(R.color.lightTextColor));
            }

        }


        return intervalView;
    }

    private int readResource(int id) {
        return ContextCompat.getColor(WhatIntervalsAdapter.this.getContext(), id);
    }

}
