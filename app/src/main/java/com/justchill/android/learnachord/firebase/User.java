package com.justchill.android.learnachord.firebase;

import android.graphics.Bitmap;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class User {

    public FirebaseUser firebaseUser;

    public static final int numberOfAchievements = 12;
    public ArrayList<Boolean> achievements = new ArrayList<>();

    public Bitmap photo;

    // TODO: remove this
    public String value1 = null;
    public String value2 = null;

    public User() {
        firebaseUser = null;
        photo = null;

        for(int i = 0; i < numberOfAchievements ; i++) {
            achievements.add(false);
        }
    }

}
