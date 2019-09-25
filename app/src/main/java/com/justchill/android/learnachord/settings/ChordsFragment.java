package com.justchill.android.learnachord.settings;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;

import com.justchill.android.learnachord.settings.adapters.ChordAdapter;
import com.justchill.android.learnachord.LocaleHelper;
import com.justchill.android.learnachord.R;
import com.justchill.android.learnachord.intervalOrChord.ChordsList;
import com.justchill.android.learnachord.database.DatabaseHandler;

// Fragment for choosing what chords to play
public class ChordsFragment extends Fragment {

    // List of all chords
    private ListView chordSettings;
    // Checkbox to check/uncheck all intervals at same time
    private CheckBox allChordsCB;
    // View of this fragment that is displaying in UI
    private View fragmentView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_chords, container, false);

        chordSettings = fragmentView.findViewById(R.id.settings_chord_list_view);
        refreshChordAdapter();

        allChordsCB = fragmentView.findViewById(R.id.all_chords_check);
        setAllChordsCB();


        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();

        allChordsCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHandler.setDoesDbNeedUpdate(true);
                ChordsList.setAllChordsIsChecked(allChordsCB.isChecked());

                refreshChordAdapter();
            }
        });
    }

    // Set checkbox for all chords' state depending on number of selected chords
    private void setAllChordsCB() {
        allChordsCB.setChecked( ChordsList.getCheckedChordsCount() >= ChordsList.getChordsCount() );
    }

    // Recreate chord list adapter
    public void refreshChordAdapter() {
        ChordAdapter chordAdapter = new ChordAdapter(getActivity(), ChordsList.getAllChords());

        chordAdapter.setListener(new ChordAdapter.RefreshViewListener() {
            @Override
            public void onRefresh() {
                setAllChordsCB();
            }
        });

        chordSettings.setAdapter(chordAdapter);
    }

}
