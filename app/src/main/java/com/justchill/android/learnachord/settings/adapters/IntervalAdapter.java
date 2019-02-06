package com.justchill.android.learnachord.settings.adapters;

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

import com.justchill.android.learnachord.MyApplication;
import com.justchill.android.learnachord.R;
import com.justchill.android.learnachord.intervalOrChord.Interval;
import com.justchill.android.learnachord.intervalOrChord.IntervalsList;
import com.justchill.android.learnachord.database.DatabaseHandler;

import java.util.ArrayList;

// ListView adapter for interval options
public class IntervalAdapter extends ArrayAdapter<Interval> {

    // This needs to be public so it can be accessed from settings package classes
    public IntervalAdapter(Activity context, ArrayList<Interval> intervals) {
        super(context, 0, intervals);
    }

    // Interface for on refresh listener
    public interface RefreshViewListener {
        void onRefresh();
    }
    // On refresh listener
    private RefreshViewListener refreshViewListener;

    // Set on refresh listener
    public void setListener(RefreshViewListener listener) {
        refreshViewListener = listener;
    }

    // For getting a view, this is called every time user scrolls ListView for
    // every new view that is starting to show (and while creating ListView)
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Try to recycle other view
        View settingsView = convertView;
        if(settingsView == null) {
            settingsView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_settings, parent, false);
        }

        final Interval currentInterval = IntervalsList.getInterval(position);

        final CheckBox onOff = settingsView.findViewById(R.id.settings_switch);
        if (currentInterval != null) {
            onOff.setChecked(currentInterval.getIsChecked());
        } else {
            onOff.setChecked(true);
        }

        // For this settingsView need to be in front, Solution for not calling onClick(): none of child Layouts can be clickable
        settingsView.setClickable(true); // After one hour of pain i found this online, AND IT DIDN'T WORKED
        settingsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHandler.setDoesDbNeedUpdate(true);
                onOff.setChecked(!onOff.isChecked());

                if (currentInterval != null) {
                    currentInterval.setIsChecked(onOff.isChecked());
                }

                if(refreshViewListener != null) {
                    refreshViewListener.onRefresh();
                }
            }
        });

        ImageView playImageView = settingsView.findViewById(R.id.settings_interval_play_icon);

        playImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 25 == middle c
                MyApplication.playChord(new Interval[]{currentInterval}, 25, MyApplication.directionUpID);
            }
        });


        if (currentInterval != null) {
            TextView intervalLabel = settingsView.findViewById(R.id.settings_interval_text_view);
            intervalLabel.setText(currentInterval.getName());
        }

        return settingsView;
    }
}
