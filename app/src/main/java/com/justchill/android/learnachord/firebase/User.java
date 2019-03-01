package com.justchill.android.learnachord.firebase;

import android.graphics.Bitmap;

import com.google.firebase.auth.FirebaseUser;

public class User {

    public FirebaseUser firebaseUser;

    public boolean achievements[] = {false, false, false, false, false};

    public Bitmap photo = null;

    public String value1 = null;
    public String value2 = null;

    public User() {
        firebaseUser = null;
    }

}
