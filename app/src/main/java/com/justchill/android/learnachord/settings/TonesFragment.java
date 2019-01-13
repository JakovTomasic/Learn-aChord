package com.justchill.android.learnachord.settings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.justchill.android.learnachord.LocaleHelper;
import com.justchill.android.learnachord.R;
import com.justchill.android.learnachord.database.DatabaseData;
import com.justchill.android.learnachord.database.DatabaseHandler;


public class TonesFragment extends Fragment {

    private View fragmentView;

    private View whatToneClickableView;
    private CheckBox whatToneCheckBox;
    private View octaveOptionClickableView;
    private CheckBox octaveOptionCheckBox;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_tones, container, false);

        LocaleHelper.setLocale(getContext(), null);


        whatToneClickableView = fragmentView.findViewById(R.id.what_tone_parent_layout);
        whatToneCheckBox = fragmentView.findViewById(R.id.what_tone_check_box);
        whatToneCheckBox.setChecked(DatabaseData.playWhatTone);
        whatToneClickableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseData.playWhatTone = !DatabaseData.playWhatTone;

                whatToneCheckBox.setChecked(DatabaseData.playWhatTone);

                DatabaseHandler.setDoesDbNeedUpdate(true);
            }
        });


        octaveOptionClickableView = fragmentView.findViewById(R.id.what_octave_parent_layout);
        octaveOptionCheckBox = fragmentView.findViewById(R.id.what_octave_check_box);
        octaveOptionCheckBox.setChecked(DatabaseData.playWhatOctave);
        octaveOptionClickableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseData.playWhatOctave = !DatabaseData.playWhatOctave;

                octaveOptionCheckBox.setChecked(DatabaseData.playWhatOctave);

                DatabaseHandler.setDoesDbNeedUpdate(true);
            }
        });


        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


}
