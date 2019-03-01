package com.justchill.android.learnachord.firebase;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.justchill.android.learnachord.R;

public class UserProfileActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100; // Any number

    EditText textInput;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
//
////        if(FirebaseHandler.user != null) {
////            Log.e("#####", FirebaseHandler.user.firebaseUser.getUid());
////        }
//
//        Button loginButton = findViewById(R.id.login_button);
//        loginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(FirebaseHandler.user.firebaseUser == null) {
//                    FirebaseHandler.showLogInScreen(UserProfileActivity.this, RC_SIGN_IN);
//                } else {
//                    Log.e("#####", FirebaseHandler.user.firebaseUser.getUid());
//                }
//            }
//        });
//
//        Button logoutButton = findViewById(R.id.logout_button);
//        logoutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FirebaseAuth.getInstance().signOut();
//                FirebaseHandler.user.firebaseUser = null;
//            }
//        });
//
//
//        textInput = findViewById(R.id.text_input);
//
//
//        Button sendDataOneBtn = findViewById(R.id.send_data_one_btn);
//        sendDataOneBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FirebaseHandler.writeData(FirebaseHandler.user.firebaseUser.getUid(), "Data",
//                        "dataValue1", textInput.getText().toString());
//                FirebaseHandler.readData(FirebaseHandler.user.firebaseUser.getUid(), "Data", "dataValue1", UserProfileActivity.this);
//            }
//        });
//
//        Button sendDataTwoBtn = findViewById(R.id.send_data_two_btn);
//        sendDataTwoBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FirebaseHandler.writeData(FirebaseHandler.user.firebaseUser.getUid(), "Data",
//                        "dataValue2", textInput.getText().toString());
//                FirebaseHandler.readData(FirebaseHandler.user.firebaseUser.getUid(), "Data", "dataValue1", UserProfileActivity.this);
//            }
//        });
//
////        refreshData();

        ImageView profilePhotoCircleIV = findViewById(R.id.user_profile_photo_circle_image_view);
        profilePhotoCircleIV.setImageBitmap(FirebaseHandler.user.photo);

        if(FirebaseHandler.user.firebaseUser != null) {
            for (UserInfo profile : FirebaseHandler.user.firebaseUser.getProviderData()) {
//                // Id of the provider (ex: google.com)
//                String providerId = profile.getProviderId();
//
//                // UID specific to the provider
//                String uid = profile.getUid();



                // Name, email address, and profile photo Url
                String name = profile.getDisplayName();
                TextView userDisplayNameTV = findViewById(R.id.user_display_name_text_view);
                userDisplayNameTV.setText(name);


//                String email = profile.getEmail();
//                final Uri photoUrl = profile.getPhotoUrl();
//
//                Thread temp = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        final Bitmap bitmap = getImageBitmap(photoUrl.toString());
//
//                        UserProfileActivity.this.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                profileIV.setImageBitmap(bitmap);
//                            }
//                        });
//
//                    }
//                });
//                temp.start();


            }
        }

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        FirebaseHandler.handleOnActivityResult(UserProfileActivity.this, RC_SIGN_IN, requestCode, resultCode, data);
    }

    public static void refreshData(Activity activity) {
//        TextView tv1 = activity.findViewById(R.id.tv1);
//        TextView tv2 = activity.findViewById(R.id.tv2);
//
//        tv1.setText(FirebaseHandler.user.value1);
//        tv2.setText(FirebaseHandler.user.value2);
    }

}
