package com.justchill.android.learnachord.firebase;

import android.graphics.Bitmap;

import com.google.firebase.auth.FirebaseUser;
import com.justchill.android.learnachord.MyApplication;
import com.justchill.android.learnachord.R;
import com.justchill.android.learnachord.database.DatabaseHandler;

import java.util.ArrayList;

// Used for storing logged in users' data
public class User {

    // Stores all user data when logged in, null when logged out
    public FirebaseUser firebaseUser;

    public static final int numberOfAchievements = MyApplication.getAppContext().getResources().getStringArray(R.array.achievement_progress_keys).length;
    // List of all achievement progress values
    public ArrayList<Integer> achievementProgress = new ArrayList<>();

    // Does achievementProgress ArrayList needs to be updated from cloud
    public static boolean updateAchievementProgress;

    // Does cloud database need to be updated
    public static boolean updateAchievementProgressInCloud;


    // Stores user profile photo that is downloaded on startup
    public Bitmap photo;

    public User() {
        firebaseUser = null;
        photo = null;

        // Size of achievementProgress must always be the same
        for(int i = 0; i < numberOfAchievements ; i++) {
            achievementProgress.add(0);
        }

        updateAchievementProgress = true;
        updateAchievementProgressInCloud = false;
    }

    // Set achievement progress if it is greater than current one
    public void setAchievementProgress(int id, Integer progress) {
        if(progress == null) {
            return;
        }

        // If progress is greater (newer) update it (everywhere)
        if(progress > achievementProgress.get(id)) {
            DatabaseHandler.setDoesDbNeedUpdate(true);
            updateAchievementProgressInCloud = true;
            achievementProgress.set(id, progress);
        }
    }

    // Checks if given progress is valid
    public static boolean isAchievementProgressValid(Integer progress) {
        if(progress == null) {
            return false;
        }
        return progress >= 0;
    }

}
