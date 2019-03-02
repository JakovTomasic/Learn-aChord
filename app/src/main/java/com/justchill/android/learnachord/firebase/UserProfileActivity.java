package com.justchill.android.learnachord.firebase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.justchill.android.learnachord.LocaleHelper;
import com.justchill.android.learnachord.MyApplication;
import com.justchill.android.learnachord.R;
import com.justchill.android.learnachord.database.DatabaseHandler;

// Activity for user account and achievements
public class UserProfileActivity extends AppCompatActivity {

    // Number for validating login
    private static final int RC_SIGN_IN = 100; // Any number

    // TODO: set all sizes programmatically

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Declare UI elements

        View profileLinearLayoutLoginClickable = findViewById(R.id.profile_linear_layout_login_clickable);
        TextView userDisplayNameTV = findViewById(R.id.user_display_name_text_view);

        // When user clicks on picture or name, try to log him in (in case user is logged out)
        profileLinearLayoutLoginClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(FirebaseHandler.user.firebaseUser == null) {
                    FirebaseHandler.showLogInScreen(UserProfileActivity.this, RC_SIGN_IN);
                }
            }
        });


        if(FirebaseHandler.user.firebaseUser == null) {
            // If user is not logged in, instead of name write "Login" to indicate how to log in
            userDisplayNameTV.setText(MyApplication.readResource(R.string.login, null));
        } else {
            // If profile photo is downloaded, show it
            if(FirebaseHandler.user.photo != null) {
                setProfilePhoto(this);
            }

            // Get user name and write it
            String name = FirebaseHandler.user.firebaseUser.getDisplayName();
            userDisplayNameTV.setText(name);

        }

        // Setup achievements' UI
        refreshAchievementProgressUI(this);

        // Initially, scroll to the top
        ScrollView parentScrollView = findViewById(R.id.user_profile_activity_parent_scroll_view);
        parentScrollView.smoothScrollTo(0, 0);


        // Get and save achievement progress data from cloud database
        try {
            FirebaseHandler.updateAchievementProgress();
        } catch (Exception e) {
            e.printStackTrace();
        }


        // Check internet connection on separate thread (in case connection is slow)
        Thread checkInternetConnectionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // If internet is not available, show error message
                if(!MyApplication.isInternetAvailable()) {
                    try {
                        UserProfileActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView noInternetConnectionTV = findViewById(R.id.no_internet_connection_warning_text_view);
                                noInternetConnectionTV.setVisibility(View.VISIBLE);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        checkInternetConnectionThread.start();

    }

    /*
     * Method for Setting the Height of the ListView dynamically.
     * Hack to fix the issue of not showing all the items of the ListView
     * when placed inside a ScrollView
     */
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

    // Try to add profile photo to the UI
    public static void setProfilePhoto(Activity activity) {
        try {
            ImageView profilePhotoCircleIV = activity.findViewById(R.id.user_profile_photo_circle_image_view);
            profilePhotoCircleIV.setImageBitmap(FirebaseHandler.user.photo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Setup achievements' UI
    public static void refreshAchievementProgressUI(Activity activity) {
        try {
            // Total progress score
            int totalAchievementProgressScore = 0;
            for(Integer i : FirebaseHandler.user.achievementProgress) {
                totalAchievementProgressScore += Math.min(i, AchievementAdapter.maxAchievementProgressScore);
            }
            // Max value of total progress score
            int maxTotalAchievementProgressScore = AchievementAdapter.maxAchievementProgressScore * User.numberOfAchievements;


            // Set locked and unlocked progress indicator view size (with layout_weight) depending on total progress
            View unlockedTotalProgressIndicatorView = activity.findViewById(R.id.achievement_total_progress_unlocked_indicator);
            LinearLayout.LayoutParams unlockedProgressIndicatorParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    Math.max(0, maxTotalAchievementProgressScore-totalAchievementProgressScore)
            );
            unlockedTotalProgressIndicatorView.setLayoutParams(unlockedProgressIndicatorParams);

            View lockedTotalProgressIndicatorView = activity.findViewById(R.id.achievement_total_progress_locked_indicator);
            LinearLayout.LayoutParams lockedProgressIndicatorParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    Math.max(0, maxTotalAchievementProgressScore-Math.max(0, maxTotalAchievementProgressScore-totalAchievementProgressScore))
            );
            lockedTotalProgressIndicatorView.setLayoutParams(lockedProgressIndicatorParams);


            // Refresh list view adapter (and with that whole list view)
            ListView achievementsListView = activity.findViewById(R.id.user_achievements_list_view);
            AchievementAdapter achievementAdapter = new AchievementAdapter(activity, FirebaseHandler.user.achievementProgress);

            achievementsListView.setAdapter(achievementAdapter);

            // Set fixed height
            setListViewHeightBasedOnChildren(achievementsListView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Called when user chooses with what platform to log in
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle it (save new user)
        FirebaseHandler.handleOnActivityResult(UserProfileActivity.this, RC_SIGN_IN, requestCode, resultCode, data);
        // Exit activity (to refresh everything and for UX reasons)
        UserProfileActivity.this.finish();
    }

    // For different languages support
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, null));
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


    // Set options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_profile_menu, menu);

        // If user is not logged in don't show logout button
        MenuItem logoutMenuItem = menu.findItem(R.id.action_logout);
        if(FirebaseHandler.user.firebaseUser == null) {
            logoutMenuItem.setVisible(false);
        }

        return true;
    }

    // Handle options menu action
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout: // Logout the user
                FirebaseAuth.getInstance().signOut();
                FirebaseHandler.user = new User();
                DatabaseHandler.setDoAchievementsNeedUpdate(true);
                UserProfileActivity.this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
