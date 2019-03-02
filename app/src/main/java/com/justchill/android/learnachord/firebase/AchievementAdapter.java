package com.justchill.android.learnachord.firebase;

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


public class AchievementAdapter extends ArrayAdapter<Boolean> {

    // This needs to be public so it can be accessed from settings package classes
    public AchievementAdapter(Activity context, ArrayList<Boolean> achievements) {
        super(context, 0, achievements);
    }

    // For getting a view, this is called every time user scrolls ListView for
    // every new view that is starting to show (and while creating ListView)
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Try to recycle other view
        View achievementView = convertView;
        if(achievementView == null) {
            achievementView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_achievement, parent, false);
        }

        final Boolean currentAchievementStatus = FirebaseHandler.user.achievements.get(position);

        // TODO: finish this

        return achievementView;
    }
}

