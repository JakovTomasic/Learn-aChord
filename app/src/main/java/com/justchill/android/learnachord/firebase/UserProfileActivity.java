package com.justchill.android.learnachord.firebase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.justchill.android.learnachord.LocaleHelper;
import com.justchill.android.learnachord.MyApplication;
import com.justchill.android.learnachord.R;

import java.net.InetAddress;
import java.util.List;

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
//
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

        View profileLinearLayoutLoginClickable = findViewById(R.id.profile_linear_layout_login_clickable);
        View logoutClickableImageView = findViewById(R.id.logout_clickable_view_relative_layout);
        TextView userDisplayNameTV = findViewById(R.id.user_display_name_text_view);


        profileLinearLayoutLoginClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(FirebaseHandler.user.firebaseUser == null) {
                    FirebaseHandler.showLogInScreen(UserProfileActivity.this, RC_SIGN_IN);
                } else {
//                    Log.e("#####", FirebaseHandler.user.firebaseUser.getUid());
                }
            }
        });

        logoutClickableImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                FirebaseHandler.user = new User();
                UserProfileActivity.this.finish();
            }
        });

        if(FirebaseHandler.user.firebaseUser == null) {
            logoutClickableImageView.setVisibility(View.GONE);
            userDisplayNameTV.setText(MyApplication.readResource(R.string.login, null));
        } else {
            if(FirebaseHandler.user.photo != null) {
                setProfilePhoto(this);
            }

            for (UserInfo profile : FirebaseHandler.user.firebaseUser.getProviderData()) {
//                // Id of the provider (ex: google.com)
//                String providerId = profile.getProviderId();
//
//                // UID specific to the provider
//                String uid = profile.getUid();



                // Name, email address, and profile photo Url
                String name = profile.getDisplayName();
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


        ListView achievementsListView = findViewById(R.id.user_achievements_list_view);
        AchievementAdapter achievementAdapter = new AchievementAdapter(this, FirebaseHandler.user.achievements);

        achievementsListView.setAdapter(achievementAdapter);

        setListViewHeightBasedOnChildren(achievementsListView);

        // TODO: fix start scroll position
//        ScrollView parentScrollView = findViewById(R.id.user_profile_activity_parent_scroll_view);
//        parentScrollView.scrollTo(0, 0);
//        parentScrollView.fullScroll(ScrollView.FOCUS_UP);

    }

    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public static void setProfilePhoto(Activity activity) {
        try {
            ImageView profilePhotoCircleIV = activity.findViewById(R.id.user_profile_photo_circle_image_view);
            profilePhotoCircleIV.setImageBitmap(FirebaseHandler.user.photo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO: implement no internet indicator
//    public boolean isInternetAvailable() {
//        try {
//            InetAddress ipAddr = InetAddress.getByName("google.com");
//            //You can replace it with your name
//            return !ipAddr.equals("");
//
//        } catch (Exception e) {
//            return false;
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        FirebaseHandler.handleOnActivityResult(UserProfileActivity.this, RC_SIGN_IN, requestCode, resultCode, data);
        UserProfileActivity.this.finish();
    }

    // For different languages support
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, null));
    }

    public static void refreshData(Activity activity) {
//        TextView tv1 = activity.findViewById(R.id.tv1);
//        TextView tv2 = activity.findViewById(R.id.tv2);
//
//        tv1.setText(FirebaseHandler.user.value1);
//        tv2.setText(FirebaseHandler.user.value2);
    }

    @Override
    protected void onResume() {
        super.onResume();

        MyApplication.activityResumed(UserProfileActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        MyApplication.activityPaused();
    }
}
