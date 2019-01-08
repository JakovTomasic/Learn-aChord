package com.justchill.android.learnachord;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.justchill.android.learnachord.chord.Interval;
import com.justchill.android.learnachord.chord.IntervalsList;

import java.util.ArrayList;

public class IntervalAdapter extends ArrayAdapter<Interval> {

    private static final String LOG_TAG = IntervalAdapter.class.getName();

    // This needs to be public so it can be accessed from settings package classes
    public IntervalAdapter(Activity context, ArrayList<Interval> intervals) {
        super(context, 0, intervals);
    }

    public interface RefreshViewListener {
        void onRefresh();
    }
    private RefreshViewListener refreshViewListener;

    public void setListener(RefreshViewListener listener) {
        refreshViewListener = listener;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View settingsView = convertView;
        if(settingsView == null) {
            settingsView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_settings, parent, false);
        }

        final Interval currentInterval = IntervalsList.getInterval(position);

        final CheckBox onOff = (CheckBox) settingsView.findViewById(R.id.settings_switch);
        if (currentInterval != null) {
            onOff.setChecked(currentInterval.getIsChecked());
        } else {
            onOff.setChecked(true);
        }

        // For this settingsView need to be in front (i think), Solution for not calling onClick(): none of child Layouts can be clickable
        settingsView.setClickable(true); // After one hour of pain i found this online, AND IT DIDN'T WORKED
        settingsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getContext(), "numb: " + position, Toast.LENGTH_SHORT).show();
                MyApplication.setDoesDbNeedUpdate(true);
                onOff.setChecked(!onOff.isChecked());

                if (currentInterval != null) {
                    currentInterval.setIsChecked(onOff.isChecked());
                }

                if(refreshViewListener != null) {
                    refreshViewListener.onRefresh();
                }
            }
        });

        ImageView playImageView = (ImageView) settingsView.findViewById(R.id.settings_interval_play_icon);

        playImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getContext(), "play: " + position, Toast.LENGTH_SHORT).show();
                // 25 == middle c
                MyApplication.playChord(new Interval[]{currentInterval}, 25);
            }
        });


        if (currentInterval != null) {
            TextView intervalLabel = (TextView) settingsView.findViewById(R.id.settings_interval_text_view);
            intervalLabel.setText(currentInterval.getIntervalName());
        }

        return settingsView;
    }
}
