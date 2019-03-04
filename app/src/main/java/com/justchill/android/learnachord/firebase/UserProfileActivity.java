package com.justchill.android.learnachord.firebase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.justchill.android.learnachord.LocaleHelper;
import com.justchill.android.learnachord.MyApplication;
import com.justchill.android.learnachord.R;
import com.justchill.android.learnachord.database.DatabaseHandler;

// Activity for user account and achievements
public class UserProfileActivity extends AppCompatActivity {

    // Request codes for activities with requests - for logging in and getting photo from gallery
    public static final int RC_SIGN_IN = 100; // Any number
    public static final int RC_GET_PICTURE = 101; // Any number

    // Saving all photos when user wants to change profile photo
    public static Bitmap googlePhoto = null;
    public static Bitmap facebookPhoto = null;
    public static Bitmap twitterPhoto = null;
    public static Bitmap fromPhonePhoto = null;

    // Parent layout of "Select new image" option
    private RelativeLayout chooseProfileParentRelativeLayout;

    // Views for select new profile photo option
    private ImageView photoOptionDefaultCircleIV;
    private ImageView photoOptionGoogleCircleIV;
    private View photoGoogleSeparationView;
    private ImageView photoOptionFacebookCircleIV;
    private View photoFacebookSeparationView;
    private ImageView photoOptionTwitterCircleIV;
    private View photoTwitterSeparationView;
    private ImageView photoOptionChooseFromPhoneCircleIV;

    // TODO: set all sizes programmatically

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Declare UI elements

        chooseProfileParentRelativeLayout = findViewById(R.id.choose_profile_photo_parent_relative_layout);

        photoOptionDefaultCircleIV = findViewById(R.id.choose_profile_photo_circle_image_view_default);
        photoOptionGoogleCircleIV = findViewById(R.id.choose_profile_photo_circle_image_view_google);
        photoGoogleSeparationView = findViewById(R.id.choose_profile_photo_separation_view_google);
        photoOptionFacebookCircleIV = findViewById(R.id.choose_profile_photo_circle_image_view_facebook);
        photoFacebookSeparationView = findViewById(R.id.choose_profile_photo_separation_view_facebook);
        photoOptionTwitterCircleIV = findViewById(R.id.choose_profile_photo_circle_image_view_twitter);
        photoTwitterSeparationView = findViewById(R.id.choose_profile_photo_separation_view_twitter);
        photoOptionChooseFromPhoneCircleIV = findViewById(R.id.choose_profile_photo_circle_image_view_from_phone);

        View profileLinearLayoutLoginClickable = findViewById(R.id.profile_linear_layout_login_clickable);
        final TextView userDisplayNameTV = findViewById(R.id.user_display_name_text_view);

        // When user clicks on picture or name, try to log him in (in case user is logged out)
        profileLinearLayoutLoginClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread handleClickOnProfilePhotoThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // Check internet connection on this thread
                        final boolean tempIsInternetAvailable = MyApplication.isInternetAvailable();
                        try {
                            // Continue on UI thread
                            UserProfileActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // If there is valid internet connection and user is not logged in, open login window
                                    if(FirebaseHandler.user.firebaseUser == null && tempIsInternetAvailable) {
                                        // If user is not logged in, log him in
                                        FirebaseHandler.showLogInScreen(UserProfileActivity.this);
                                        // TODO: don't exit from activity when sign in is cancelled
                                    } else {
                                        // User is logged in or there is not valid internet connection
                                        // If choose profile option is already opened, close it
                                        if(chooseProfileParentRelativeLayout.getHeight() > 0) {
                                            chooseProfileParentRelativeLayout.getLayoutParams().height = 0;
                                            chooseProfileParentRelativeLayout.requestLayout();
                                        } else {
                                            // If choose profile option is not opened, open it
                                            showChooseProfileMenu();
                                        }

                                    }
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                handleClickOnProfilePhotoThread.start();

            }
        });

        // On separate thread because network must be checked on separate thread
        Thread setupUserProfilePhotoAndNameThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Check internet connection on this thread
                final boolean tempIsInternetAvailable = MyApplication.isInternetAvailable();
                try {
                    // Continue on UI thread
                    UserProfileActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(FirebaseHandler.user.firebaseUser == null && tempIsInternetAvailable) {
                                /*
                                 * If user is not logged in and can log in (has valid internet connection),
                                 * instead of name write "Login" to indicate how to log in
                                 */
                                userDisplayNameTV.setText(MyApplication.readResource(R.string.login, null));
                            } else  {
                                // If profile photo is downloaded, show it
                                if(FirebaseHandler.user.photo != null) {
                                    setProfilePhoto(UserProfileActivity.this);
                                }

                                // If user is logged in, write it's name
                                if(FirebaseHandler.user.firebaseUser != null) {
                                    // Get user name and write it
                                    String name = FirebaseHandler.user.firebaseUser.getDisplayName();
                                    userDisplayNameTV.setText(name);
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        setupUserProfilePhotoAndNameThread.start();


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


        // Choose profile picture options (listener for each picture)

        photoOptionDefaultCircleIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Set default image
                FirebaseHandler.imageToSet = FirebaseHandler.IMAGE_TO_SET_DEFAULT_ID;
                FirebaseHandler.setupUserPhoto();

                // Close choose profile picture option
                chooseProfileParentRelativeLayout.getLayoutParams().height = 0;
                chooseProfileParentRelativeLayout.requestLayout();
            }
        });
        photoOptionGoogleCircleIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Set google profile image
                FirebaseHandler.imageToSet = FirebaseHandler.IMAGE_TO_SET_GOOGLE_ID;
                FirebaseHandler.setupUserPhoto();

                // Close choose profile picture option
                chooseProfileParentRelativeLayout.getLayoutParams().height = 0;
                chooseProfileParentRelativeLayout.requestLayout();
            }
        });
        photoOptionFacebookCircleIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Set facebook profile image
                FirebaseHandler.imageToSet = FirebaseHandler.IMAGE_TO_SET_FACEBOOK_ID;
                FirebaseHandler.setupUserPhoto();

                // Close choose profile picture option
                chooseProfileParentRelativeLayout.getLayoutParams().height = 0;
                chooseProfileParentRelativeLayout.requestLayout();
            }
        });
        photoOptionTwitterCircleIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Set twitter profile image
                FirebaseHandler.imageToSet = FirebaseHandler.IMAGE_TO_SET_TWITTER_ID;
                FirebaseHandler.setupUserPhoto();

                // Close choose profile picture option
                chooseProfileParentRelativeLayout.getLayoutParams().height = 0;
                chooseProfileParentRelativeLayout.requestLayout();
            }
        });
        photoOptionChooseFromPhoneCircleIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Set profile image from phone
                FirebaseHandler.imageToSet = FirebaseHandler.IMAGE_TO_SET_FROM_PHONE_ID;
                // Close choose profile picture option
                chooseProfileParentRelativeLayout.getLayoutParams().height = 0;
                chooseProfileParentRelativeLayout.requestLayout();

                // TODO: add loading animation

                // Choose profile picture from gallery
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RC_GET_PICTURE);

            }
        });

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
            if(FirebaseHandler.user.photo == null) {
                // If there is no photo, set the default one
                profilePhotoCircleIV.setImageResource(R.drawable.ic_default_user_profile_photo);
            } else {
                // Set the photos
                profilePhotoCircleIV.setImageBitmap(FirebaseHandler.user.photo);
                profilePhotoCircleIV.requestLayout();
            }
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


    // Called when activity with result is finished (when user chooses with what platform to log in or photo from gallery)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN) {
            // Activity with result was firebase sign in activity

            // Handle it (save new user)
            FirebaseHandler.handleOnActivityResult(UserProfileActivity.this, requestCode, resultCode, data);
            // Exit activity (to refresh everything and for UX reasons)
            UserProfileActivity.this.finish();
        } else if(requestCode == RC_GET_PICTURE) {
            // Activity with result was choose profile photo from gallery

            // Handle it (save and display image)
            FirebaseHandler.handleOnActivityResult(UserProfileActivity.this, requestCode, resultCode, data);
        }

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
                // Create new user (refresh / delete all user data locally)
                FirebaseAuth.getInstance().signOut();
                FirebaseHandler.user = new User();

                // Update achievements
                DatabaseHandler.setDoAchievementsNeedUpdate(true);

                // Forget user photos
                googlePhoto = null;
                facebookPhoto = null;
                twitterPhoto = null;
                fromPhonePhoto = null;

                // Exit activity (to refresh everything and for UX reasons)
                UserProfileActivity.this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // Display menu for choosing new profile photo
    private void showChooseProfileMenu() {

        // Set size of parent view (to make it visible)
        chooseProfileParentRelativeLayout.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        chooseProfileParentRelativeLayout.getLayoutParams().width = RelativeLayout.LayoutParams.MATCH_PARENT;
        // Remeasure and redraw this view (without this, nothing changes after executing code above)
        chooseProfileParentRelativeLayout.requestLayout();

        Thread chooseProfileSetupThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Logged out user cannot change profile photo if there is internet connection available (because, he will try to log in)
                if(FirebaseHandler.user.firebaseUser == null && MyApplication.isInternetAvailable()) {
                    return;
                }


                // If user is logged in, get all profile photos from cloud (if we don't have them already)
                if(FirebaseHandler.user.firebaseUser != null) {
                    // Loop through all user profiles (from all providers) and save user profile photos as bitmap
                    for(UserInfo profile : FirebaseHandler.user.firebaseUser.getProviderData()) {
                        // Get the profile photo's url
                        Uri photoUrl = profile.getPhotoUrl();

                        if(photoUrl == null) {
                            continue;
                        }

                        // Convert the Url to a String and store into a variable
                        String photoPath = photoUrl.toString();

                        // Get photo in best resolution
                        if(photoPath.toLowerCase().contains("google") && googlePhoto == null) {
                            googlePhoto = FirebaseHandler.getImageBitmap(FirebaseHandler.googleGetHighResPhotoPath(photoPath));
                        }
                        if(photoPath.toLowerCase().contains("facebook") && facebookPhoto == null) {
                            facebookPhoto = FirebaseHandler.getImageBitmap(FirebaseHandler.facebookGetHighResPhotoPath(profile.getUid()));
                        }
                        if(photoPath.toLowerCase().contains("twimg") && twitterPhoto == null) {
                            twitterPhoto = FirebaseHandler.getImageBitmap(FirebaseHandler.twitterGetHighResPhotoPath(photoPath));
                        }
                    }
                }

                // If activity is visible, change UI
                if(MyApplication.getActivity() != null) {
                    try {
                        // Try to display all photos
                        if(MyApplication.getActivity() instanceof UserProfileActivity) {
                            MyApplication.getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Get size of default photo to scale new photos the same vay
                                    int currentSize = photoOptionDefaultCircleIV.getHeight();

                                    /*
                                     * If photo does not exist hide option for setting it
                                     * If it does display it in same scale as default photo
                                     */

                                    if(googlePhoto == null) {
                                        photoOptionGoogleCircleIV.setVisibility(View.GONE);
                                        photoGoogleSeparationView.setVisibility(View.GONE);
                                    } else {
                                        photoOptionGoogleCircleIV.setImageBitmap(Bitmap.createScaledBitmap(googlePhoto, currentSize, currentSize, true));
                                    }

                                    if(facebookPhoto == null) {
                                        photoOptionFacebookCircleIV.setVisibility(View.GONE);
                                        photoFacebookSeparationView.setVisibility(View.GONE);
                                    } else {
                                        photoOptionFacebookCircleIV.setImageBitmap(Bitmap.createScaledBitmap(facebookPhoto, currentSize, currentSize, true));
                                    }

                                    if(twitterPhoto == null) {
                                        photoOptionTwitterCircleIV.setVisibility(View.GONE);
                                        photoTwitterSeparationView.setVisibility(View.GONE);
                                    } else {
                                        photoOptionTwitterCircleIV.setImageBitmap(Bitmap.createScaledBitmap(twitterPhoto, currentSize, currentSize, true));
                                    }
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }
        });
        chooseProfileSetupThread.start();
    }

}
