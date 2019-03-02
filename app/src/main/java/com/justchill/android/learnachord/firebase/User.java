package com.justchill.android.learnachord.firebase;

import android.graphics.Bitmap;

import com.google.firebase.auth.FirebaseUser;
import com.justchill.android.learnachord.MyApplication;
import com.justchill.android.learnachord.R;

import java.util.ArrayList;

public class User {

    public FirebaseUser firebaseUser;

    public static final int numberOfAchievements = MyApplication.getAppContext().getResources().getStringArray(R.array.achievement_progress_keys).length;
    public ArrayList<Integer> achievementProgress = new ArrayList<>();

    // Does achievementProgress ArrayList needs to be updated
    public boolean updateAchievementProgress;

    public boolean updateAchievementProgressInCloud;


    public Bitmap photo;

    // TODO: remove this
    public String value1 = null;
    public String value2 = null;

    public User() {
        firebaseUser = null;
        photo = null;

        for(int i = 0; i < numberOfAchievements ; i++) {
            achievementProgress.add(0);
        }

        updateAchievementProgress = true;
        updateAchievementProgressInCloud = false;
    }

}
