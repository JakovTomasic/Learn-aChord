package com.justchill.android.learnachord.settings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;

import com.justchill.android.learnachord.IntervalAdapter;
import com.justchill.android.learnachord.LocaleHelper;
import com.justchill.android.learnachord.MyApplication;
import com.justchill.android.learnachord.R;
import com.justchill.android.learnachord.chord.IntervalsList;

public class IntervalsFragment extends Fragment {

    private ListView intervalSettings;
    private CheckBox allIntervalsCB;
    private View fragmentView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_intervals, container, false);

        intervalSettings = (ListView) fragmentView.findViewById(R.id.settings_interval_list_view);
        refreshIntervalAdapter();

        LocaleHelper.setLocale(getContext(), null);

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();

        allIntervalsCB = (CheckBox) fragmentView.findViewById(R.id.all_intervals_check);
        setAllIntervalsCB();

        allIntervalsCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.setDoesDbNeedUpdate(true);
                IntervalsList.setAllIntervalsIsChecked(allIntervalsCB.isChecked());

                refreshIntervalAdapter();
            }
        });
    }

    private void setAllIntervalsCB() {
        allIntervalsCB.setChecked( IntervalsList.getCheckedIntervalCount() >= IntervalsList.getIntervalsCount() );
    }

    private void refreshIntervalAdapter() {
        IntervalAdapter intervalAdapter = new IntervalAdapter(getActivity(), IntervalsList.getAllIntervals());

        intervalAdapter.setListener(new IntervalAdapter.RefreshViewListener() {
            @Override
            public void onRefresh() {
                setAllIntervalsCB();
            }
        });

        intervalSettings.setAdapter(intervalAdapter);
    }
}
