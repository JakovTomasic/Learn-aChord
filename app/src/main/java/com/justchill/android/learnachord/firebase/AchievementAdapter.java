package com.justchill.android.learnachord.firebase;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.justchill.android.learnachord.MyApplication;
import com.justchill.android.learnachord.R;

import java.util.ArrayList;

// Adapter for showing all achievements (their progresses)
public class AchievementAdapter extends ArrayAdapter<Integer> {

    AchievementAdapter(Activity context, ArrayList<Integer> achievements) {
        super(context, 0, achievements);
    }

    // Biggest number to witch progress is being filled
    static final int maxAchievementProgressScore = 50;

    // For getting a view, this is called every time user scrolls ListView for
    // every new view that is starting to show (and while creating ListView)
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Try to recycle other view (that is not longer needed)
        View achievementView = convertView;
        if(achievementView == null) {
            achievementView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_achievement, parent, false);
        }

        final Integer currentAchievementStatus = FirebaseHandler.user.achievementProgress.get(position);


        // Show score number
        TextView achievementScoreTV = achievementView.findViewById(R.id.achievement_score_text_view);
        achievementScoreTV.setText(FirebaseHandler.user.achievementProgress.get(position).toString());

        // Set locked and unlocked progress indicator view size (with layout_weight) depending on user progress:
        View unlockedProgressIndicatorView = achievementView.findViewById(R.id.achievement_progress_unlocked_indicator);
        LinearLayout.LayoutParams unlockedProgressIndicatorParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                Math.max(0, maxAchievementProgressScore-currentAchievementStatus)
        );
        unlockedProgressIndicatorView.setLayoutParams(unlockedProgressIndicatorParams);

        View lockedProgressIndicatorView = achievementView.findViewById(R.id.achievement_progress_locked_indicator);
        LinearLayout.LayoutParams lockedProgressIndicatorParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                Math.max(0, maxAchievementProgressScore-Math.max(0, maxAchievementProgressScore-currentAchievementStatus))
                );
        lockedProgressIndicatorView.setLayoutParams(lockedProgressIndicatorParams);


        // Set achievement description
        TextView achievementDescriptionTextView = achievementView.findViewById(R.id.achievement_description_text_view);
        achievementDescriptionTextView.setText(getAchievementDescription(position));



        return achievementView;
    }

    // Returns achievement description based on position in the list
    private String getAchievementDescription(int position) {
        switch (position) {
            case 0:
                return MyApplication.readResource(R.string.achievement_description_under_octave_easy, null);
            case 1:
                return MyApplication.readResource(R.string.achievement_description_under_octave_medium, null);
            case 2:
                return MyApplication.readResource(R.string.achievement_description_under_octave_hard, null);
            case 3:
                return MyApplication.readResource(R.string.achievement_description_all_intervals_easy, null);
            case 4:
                return MyApplication.readResource(R.string.achievement_description_all_intervals_medium, null);
            case 5:
                return MyApplication.readResource(R.string.achievement_description_all_intervals_hard, null);
            case 6:
                return MyApplication.readResource(R.string.achievement_description_triads_easy, null);
            case 7:
                return MyApplication.readResource(R.string.achievement_description_triads_medium, null);
            case 8:
                return MyApplication.readResource(R.string.achievement_description_triads_hard, null);
            case 9:
                return MyApplication.readResource(R.string.achievement_description_all_chords_easy, null);
            case 10:
                return MyApplication.readResource(R.string.achievement_description_all_chords_medium, null);
            case 11:
                return MyApplication.readResource(R.string.achievement_description_all_chords_hard, null);
            case 12:
                return MyApplication.readResource(R.string.achievement_description_all_intervals_and_chords_easy, null);
            case 13:
                return MyApplication.readResource(R.string.achievement_description_all_intervals_and_chords_medium, null);
            case 14:
                return MyApplication.readResource(R.string.achievement_description_all_intervals_and_chords_hard, null);
            default:
                return "";
        }
    }


}
