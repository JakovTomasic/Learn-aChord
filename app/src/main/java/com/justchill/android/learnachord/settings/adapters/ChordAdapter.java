package com.justchill.android.learnachord.settings.adapters;

import android.annotation.SuppressLint;
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
import com.justchill.android.learnachord.intervalOrChord.Chord;
import com.justchill.android.learnachord.intervalOrChord.ChordsList;
import com.justchill.android.learnachord.database.DataContract;
import com.justchill.android.learnachord.database.DatabaseHandler;

import java.util.ArrayList;

// ListView adapter for chord options
public class ChordAdapter extends ArrayAdapter<Chord> {

    // This needs to be public so it can be accessed from settings package classes
    public ChordAdapter(Activity context, ArrayList<Chord> chords) {
        super(context, 0, chords);
    }

    // Interface for on refresh listener
    public interface RefreshViewListener {
        void onRefresh();
    }
    // On refresh listener
    private ChordAdapter.RefreshViewListener refreshViewListener;

    // Set on refresh listener
    public void setListener(ChordAdapter.RefreshViewListener listener) {
        refreshViewListener = listener;
    }

    // For getting a view, this is called every time user scrolls ListView for
    // every new view that is starting to show (and while creating ListView)
    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Try to recycle other view
        View settingsView = convertView;
        if(settingsView == null) {
            settingsView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_settings, parent, false);
        }

        final Chord currentChord = ChordsList.getChord(position);

        final CheckBox onOff = settingsView.findViewById(R.id.settings_switch);
        if (currentChord != null) {
            onOff.setChecked(currentChord.getIsChecked());
        } else {
            onOff.setChecked(true);
        }

        // Solution for not calling onClick(): none of child Layouts can be clickable
        settingsView.setClickable(true);
        settingsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHandler.setDoesDbNeedUpdate(true);
                onOff.setChecked(!onOff.isChecked());

                if (currentChord != null) {
                    currentChord.setIsChecked(onOff.isChecked());
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
                if(currentChord != null) {
                    // 25 == middle c
                    MyApplication.playChord(currentChord.getAllIntervals(), 25, MyApplication.directionUpID);
                }
            }
        });


        // Set name of chord. This depends on app language
        if (currentChord != null) {
            TextView chordLabel = settingsView.findViewById(R.id.settings_interval_text_view);
            chordLabel.setText(currentChord.getName());

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

            // MD9 has different naming scheme on english language
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
