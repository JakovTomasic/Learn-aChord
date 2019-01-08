package com.justchill.android.learnachord;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.justchill.android.learnachord.chord.Chord;
import com.justchill.android.learnachord.chord.ChordsList;
import com.justchill.android.learnachord.chord.Interval;
import com.justchill.android.learnachord.chord.IntervalsList;
import com.justchill.android.learnachord.database.DataContract;

import java.util.ArrayList;

public class ChordAdapter extends ArrayAdapter<Chord> {

    private static final String LOG_TAG = IntervalAdapter.class.getName();

    // This needs to be public so it can be accessed from settings package classes
    public ChordAdapter(Activity context, ArrayList<Chord> chords) {
        super(context, 0, chords);
    }

    public interface RefreshViewListener {
        void onRefresh();
    }

    private ChordAdapter.RefreshViewListener refreshViewListener;

    public void setListener(ChordAdapter.RefreshViewListener listener) {
        refreshViewListener = listener;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View settingsView = convertView;
        if(settingsView == null) {
            settingsView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_settings, parent, false);
        }

        final Chord currentChord = ChordsList.getChord(position);

        final CheckBox onOff = (CheckBox) settingsView.findViewById(R.id.settings_switch);
        if (currentChord != null) {
            onOff.setChecked(currentChord.getIsChecked());
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

                if (currentChord != null) {
                    currentChord.setIsChecked(onOff.isChecked());
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
                if(currentChord != null) {
                    // 25 == middle c
                    MyApplication.playChord(currentChord.getAllIntervals(), 25);
                }
            }
        });


        if (currentChord != null) {
            TextView chordLabel = (TextView) settingsView.findViewById(R.id.settings_interval_text_view);
            chordLabel.setText(currentChord.getChordName());

            TextView numberOneTV = settingsView.findViewById(R.id.settings_chord_one_text_view);
            TextView numberTwoTV = settingsView.findViewById(R.id.settings_chord_two_text_view);

            if(currentChord.getNumberTwo() == null) {
                chordLabel.setText(chordLabel.getText() + String.valueOf(currentChord.getNumberOne()));
                numberOneTV.setVisibility(View.GONE);
                numberTwoTV.setVisibility(View.GONE);
            } else {
                numberOneTV.setVisibility(View.VISIBLE);
                numberTwoTV.setVisibility(View.VISIBLE);
                numberOneTV.setText(String.valueOf(currentChord.getNumberOne()));
                numberTwoTV.setText(String.valueOf(currentChord.getNumberTwo()));
            }

            if(MyApplication.settingActivityLoadedLanguage == DataContract.UserPrefEntry.LANGUAGE_ENGLISH) {
                // Mali durski 9
                if(currentChord.getID() == MyApplication.MD9_ID) {
                    chordLabel.setText(MyApplication.MD9_ENG_TEXT);

                    numberOneTV.setVisibility(View.VISIBLE);
                    numberTwoTV.setVisibility(View.VISIBLE);
                    numberOneTV.setText(MyApplication.MD9_ENG_ONE);
                    numberTwoTV.setText(MyApplication.MD9_ENG_TWO);
                }

            }
        }

        return settingsView;
    }

}
