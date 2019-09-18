package com.justchill.android.learnachord.settings;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;

import com.justchill.android.learnachord.settings.adapters.IntervalAdapter;
import com.justchill.android.learnachord.LocaleHelper;
import com.justchill.android.learnachord.R;
import com.justchill.android.learnachord.intervalOrChord.IntervalsList;
import com.justchill.android.learnachord.database.DatabaseHandler;

// Fragment for choosing what intervals to play
public class IntervalsFragment extends Fragment {

    // List of all intervals
    private ListView intervalSettings;
    // Checkbox to check/uncheck all intervals at same time
    private CheckBox allIntervalsCB;
    // View of this fragment that is displaying in UI
    private View fragmentView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_intervals, container, false);

        intervalSettings = fragmentView.findViewById(R.id.settings_interval_list_view);
        refreshIntervalAdapter();

        allIntervalsCB = fragmentView.findViewById(R.id.all_intervals_check);
        setAllIntervalsCB();

        // For different languages support
        LocaleHelper.setLocale(getContext(), null);

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();

        allIntervalsCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHandler.setDoesDbNeedUpdate(true);
                IntervalsList.setAllIntervalsIsChecked(allIntervalsCB.isChecked());

                refreshIntervalAdapter();
            }
        });
    }

    // Set checkbox for all intervals' state depending on number of selected intervals
    private void setAllIntervalsCB() {
        allIntervalsCB.setChecked( IntervalsList.getCheckedIntervalCount() >= IntervalsList.getIntervalsCount() );
    }

    // Recreate interval list adapter
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
