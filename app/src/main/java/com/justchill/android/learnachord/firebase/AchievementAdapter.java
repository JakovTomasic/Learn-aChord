package com.justchill.android.learnachord.firebase;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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


        de.hdodenhof.circleimageview.CircleImageView iconIV = achievementView.findViewById(R.id.achievement_icon_image_view);
        // Read drawable from resources and create it's bitmap
        Drawable dr = getContext().getResources().getDrawable(getAchievementIconResource(position));
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();

        // Size to scale drawable to, it's minimum of default size and display size
        int displaySize = Math.min(((BitmapDrawable) dr).getBitmap().getWidth(), getAchievementIconSize());

        // Set achievement icon resource (properly scaled) and color
        iconIV.setImageBitmap(Bitmap.createScaledBitmap(bitmap, displaySize, displaySize, true));
        iconIV.setBorderColor(getAchievementBorderColorResource(position));


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

    // Returns achievement icon based on position in the list
    static int getAchievementIconResource(int position) {
        switch (position) {
            case 0:
                return R.drawable.ach_ic_one_green;
            case 1:
                return R.drawable.ach_ic_one_yellow;
            case 2:
                return R.drawable.ach_ic_one_red;
            case 3:
                return R.drawable.ach_ic_two_green;
            case 4:
                return R.drawable.ach_ic_two_yellow;
            case 5:
                return R.drawable.ach_ic_two_red;
            case 6:
                return R.drawable.ach_ic_three_green;
            case 7:
                return R.drawable.ach_ic_three_yellow;
            case 8:
                return R.drawable.ach_ic_three_red;
            case 9:
                return R.drawable.ach_ic_four_green;
            case 10:
                return R.drawable.ach_ic_four_yellow;
            case 11:
                return R.drawable.ach_ic_four_red;
            case 12:
                return R.drawable.ach_ic_five_green;
            case 13:
                return R.drawable.ach_ic_five_yellow;
            case 14:
                return R.drawable.ach_ic_five_red;
            default:
                return R.drawable.ach_ic_one_green;
        }
    }

    // Returns achievement border color based on position in the list
    static int getAchievementBorderColorResource(int position) {
        switch (position) {
            case 0:
                return getColor(R.color.achievementColorGreen);
            case 1:
                return getColor(R.color.achievementColorYellow);
            case 2:
                return getColor(R.color.achievementColorRed);
            case 3:
                return getColor(R.color.achievementColorGreen);
            case 4:
                return getColor(R.color.achievementColorYellow);
            case 5:
                return getColor(R.color.achievementColorRed);
            case 6:
                return getColor(R.color.achievementColorGreen);
            case 7:
                return getColor(R.color.achievementColorYellow);
            case 8:
                return getColor(R.color.achievementColorRed);
            case 9:
                return getColor(R.color.achievementColorGreen);
            case 10:
                return getColor(R.color.achievementColorYellow);
            case 11:
                return getColor(R.color.achievementColorRed);
            case 12:
                return getColor(R.color.achievementColorGreen);
            case 13:
                return getColor(R.color.achievementColorYellow);
            case 14:
                return getColor(R.color.achievementColorRed);
            default:
                return getColor(R.color.achievementColorGreen);
        }
    }

    // Returns color number resources based on it's id
    private static int getColor(int resId) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return MyApplication.getAppContext().getResources().getColor(resId, null);
        } else {
            return MyApplication.getAppContext().getResources().getColor(resId);
        }
    }

    // Returns px size in witch will achievement icon be displayed
    private int getAchievementIconSize() {
        TypedValue value = new TypedValue();
        DisplayMetrics metrics = new DisplayMetrics();

        getContext().getTheme().resolveAttribute(android.R.attr.listPreferredItemHeight, value, true);
        ((WindowManager) (getContext().getSystemService(Context.WINDOW_SERVICE))).getDefaultDisplay().getMetrics(metrics);

        return (int)TypedValue.complexToDimension(value.data, metrics);
    }


}
