package com.justchill.android.learnachord.settings;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.justchill.android.learnachord.LocaleHelper;
import com.justchill.android.learnachord.R;
import com.justchill.android.learnachord.database.DatabaseData;
import com.justchill.android.learnachord.database.DatabaseHandler;

// Fragment for choosing to play tones or not and setting wanted tone recognition accuracy for quiz
public class TonesFragment extends Fragment {

    // View of this fragment that is displaying in UI
    private View fragmentView;

    // Tone option button / clickable view
    private View whatToneClickableView;
    // Tone option check box
    private CheckBox whatToneCheckBox;
    // Octave option button / clickable view
    private View octaveOptionClickableView;
    // Octave option check box
    private CheckBox octaveOptionCheckBox;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_tones, container, false);

        // For different languages support
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

}
