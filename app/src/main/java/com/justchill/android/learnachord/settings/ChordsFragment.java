package com.justchill.android.learnachord.settings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;

import com.justchill.android.learnachord.ChordAdapter;
import com.justchill.android.learnachord.LocaleHelper;
import com.justchill.android.learnachord.R;
import com.justchill.android.learnachord.chord.ChordsList;
import com.justchill.android.learnachord.database.DatabaseHandler;

public class ChordsFragment extends Fragment {

    private ListView chordSettings;
    private CheckBox allChordsCB;
    private View fragmentView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_chords, container, false);

        chordSettings = (ListView) fragmentView.findViewById(R.id.settings_chord_list_view);
        refreshChordAdapter();

        LocaleHelper.setLocale(getContext(), null);

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();

        allChordsCB = (CheckBox) fragmentView.findViewById(R.id.all_chords_check);
        setAllChordsCB();

        allChordsCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHandler.setDoesDbNeedUpdate(true);
                ChordsList.setAllChordsIsChecked(allChordsCB.isChecked());

                refreshChordAdapter();
            }
        });
    }

    private void setAllChordsCB() { // CB -> Check Box
        allChordsCB.setChecked( ChordsList.getCheckedChordsCount() >= ChordsList.getChordsCount() );
    }

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
