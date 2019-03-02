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

import static com.justchill.android.learnachord.firebase.AchievementAdapter.maxAchievementProgressScore;

public class UserProfileActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100; // Any number

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);


        View profileLinearLayoutLoginClickable = findViewById(R.id.profile_linear_layout_login_clickable);
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


        if(FirebaseHandler.user.firebaseUser == null) {
            userDisplayNameTV.setText(MyApplication.readResource(R.string.login, null));
        } else {
            if(FirebaseHandler.user.photo != null) {
                setProfilePhoto(this);
            }

            // Get user name and write it
            String name = FirebaseHandler.user.firebaseUser.getDisplayName();
            userDisplayNameTV.setText(name);

        }

        refreshAchievementProgressUI(this);

        // Initially scroll to the top
        ScrollView parentScrollView = findViewById(R.id.user_profile_activity_parent_scroll_view);
        parentScrollView.smoothScrollTo(0, 0);


        // Get and save achievement progress data from cloud database
        try {
            FirebaseHandler.updateAchievementProgress();
        } catch (Exception e) {
            e.printStackTrace();
        }

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

    public static void refreshAchievementProgressUI(Activity activity) {
        try {
            // Total progress score
            int totalAchievementProgressScore = 0;
            for(Integer i : FirebaseHandler.user.achievementProgress) {
                totalAchievementProgressScore += Math.min(i, AchievementAdapter.maxAchievementProgressScore);
            }
            // Max total progress score
            int maxTotalAchievementProgressScore = AchievementAdapter.maxAchievementProgressScore * User.numberOfAchievements;


            // Set unlocked progress indicator view size (with layout_weight) depending on total progress
            View unlockedTotalProgressIndicatorView = activity.findViewById(R.id.achievement_total_progress_unlocked_indicator);
            LinearLayout.LayoutParams unlockedProgressIndicatorParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    Math.max(0, maxTotalAchievementProgressScore-totalAchievementProgressScore)
            );
            unlockedTotalProgressIndicatorView.setLayoutParams(unlockedProgressIndicatorParams);

            // Set locked progress indicator view size (with layout_weight) depending on total progress
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

            setListViewHeightBasedOnChildren(achievementsListView);
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


    // Set options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_profile_menu, menu);

        MenuItem logoutMenuItem = menu.findItem(R.id.action_logout);
        if(FirebaseHandler.user.firebaseUser == null) {
            logoutMenuItem.setVisible(false);
        }

        return true;
    }

    // Handle options menu action (there are two of them: options and profile)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout: // Open options
                FirebaseAuth.getInstance().signOut();
                FirebaseHandler.user = new User();
                UserProfileActivity.this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
