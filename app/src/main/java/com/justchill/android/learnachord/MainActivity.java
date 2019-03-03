package com.justchill.android.learnachord;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.justchill.android.learnachord.database.DatabaseHandler;
import com.justchill.android.learnachord.firebase.AchievementChecker;
import com.justchill.android.learnachord.firebase.FirebaseHandler;
import com.justchill.android.learnachord.firebase.User;
import com.justchill.android.learnachord.firebase.UserProfileActivity;
import com.justchill.android.learnachord.quiz.ChooseQuizModeActivity;
import com.justchill.android.learnachord.quiz.QuizData;
import com.justchill.android.learnachord.settings.SettingsActivity;

// Main activity, also main practicing mode
public class MainActivity extends AppCompatActivity {

    // For recreating activity on language change (if it is being recycled when returning from options)
    public static boolean languageChanged = false;


    // Play/pause button
    private ImageView fabIV;
    // Whole activity parent
    private ViewGroup parentLayout;

    // Parent layout of progress bars
    private ViewGroup progressBarParentLayout;

    // Layout of all interval/chord/tone names
    private ViewGroup chordTextViewLayout;
    // Interval/chord/tone name text view
    private TextView chordTextView;
    // Chord upper right number text view
    private TextView chordNumOneTextView;
    // Chord lower right number text view
    private TextView chordNumTwoTextView;

    // ListView that shows list of intervals when chord is playing
    private ListView whatIntervalsListView;

    // Open quiz button
    private ViewGroup quizClickableIcon;

    // These two are replacing each other when rotating screen
    private int displayWidth;
    private int displayHeight;

    private final int progressBarThicknessDB = 4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(R.string.app_name); // This is not needed

        // Change media volume when volume buttons are pressed
        setVolumeControlStream(AudioManager.STREAM_MUSIC);


        // Initialize UI components

        parentLayout = findViewById(R.id.main_activity_parent_layout);
        chordTextViewLayout = findViewById(R.id.chord_text_view_linear_layout);

        progressBarParentLayout = findViewById(R.id.progress_bar_ring_parent_layout);

        chordTextView = findViewById(R.id.chord_text_view);
        chordNumOneTextView = findViewById(R.id.chord_number_one);
        chordNumTwoTextView = findViewById(R.id.chord_number_two);

        whatIntervalsListView = findViewById(R.id.what_intervals_list_view);
        if(!MyApplication.isPlaying()) {
            whatIntervalsListView.setVisibility(View.INVISIBLE);
        }

        fabIV = findViewById(R.id.fab);
        fabIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.setIsPlaying(!MyApplication.isPlaying());

                chordTextView.setText("");
                chordNumOneTextView.setText("");
                chordNumTwoTextView.setText("");

                if(MyApplication.isPlaying()) {
                    chordTextViewLayout.setVisibility(View.VISIBLE);
                } else {
                    chordTextViewLayout.setVisibility(View.INVISIBLE);
                }
            }
        });


        // For app loading on startup
        if(!MyApplication.isLoadingFinished) {
            fabIV.setClickable(false);
            fabIV.setFocusable(false);
        }


        quizClickableIcon = findViewById(R.id.quiz_icon_parent_clickable_layout);
        quizClickableIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.setIsPlaying(false);

                // Start quiz activity
                Intent intent = new Intent(MainActivity.this, ChooseQuizModeActivity.class);
                startActivity(intent);
            }
        });




        // For app loading on startup
        MyApplication.addActivityListener(new MyApplication.ActivityListener() {

            @Override
            public void onIsPlayingChange() {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(MyApplication.isPlaying()) {
                            chordTextViewLayout.setVisibility(View.VISIBLE);
                            setDontTurnOffScreen(true);
                        } else {
                            chordTextViewLayout.setVisibility(View.INVISIBLE);
                            whatIntervalsListView.setVisibility(View.INVISIBLE);
                            setDontTurnOffScreen(false);
                        }

                        updatePlayStopButton();

                    }
                });
            }

            @Override
            public void onLoadingFinished() {
                MainActivity.this.runOnUiThread(new Runnable() { // This doesn't need to be here for now
                    @Override
                    public void run() {
                        // Make play button clickable and change it's color
                        fabIV.setClickable(true);
                        fabIV.setFocusable(true);
                        updatePlayStopButton();
                    }
                });
            }
        });


        // Get screen width and height in pixels
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        displayWidth = size.x;
        displayHeight = size.y;

        // Save smaller dimension of the screen so image doesn't go out of screen
        if(MyApplication.smallerDisplayDimensionPX == null) {
            MyApplication.smallerDisplayDimensionPX = Math.min(displayWidth, displayHeight);
        }



        if(MyApplication.isPlaying()) {
            chordTextViewLayout.setVisibility(View.VISIBLE);
        } else {
            chordTextViewLayout.setVisibility(View.INVISIBLE);
        }

    }

    // For different languages support
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, null));
    }

    // Set GUI appearance of the play/stop button and of list of chord's intervals
    private void updatePlayStopButton() {

        // Set color
        if(!MyApplication.isLoadingFinished) {
            MyApplication.setupPlayButtonColor(MainActivity.this, fabIV, R.color.unloadedColor);
        } else if(MyApplication.isPlaying()) {
            MyApplication.setupPlayButtonColor(MainActivity.this, fabIV, R.color.stopButton);
        } else {
            MyApplication.setupPlayButtonColor(MainActivity.this, fabIV, R.color.playButton);
        }


        // Set progress bar position and size
        ViewGroup.LayoutParams progressBarSizeRules = progressBarParentLayout.getLayoutParams();
        int height_width_value_progressBarParentLayout, padding;

        height_width_value_progressBarParentLayout = (int)(MyApplication.smallerDisplayDimensionPX*0.75);
        progressBarSizeRules.width = height_width_value_progressBarParentLayout;
        progressBarSizeRules.height = height_width_value_progressBarParentLayout;
        progressBarParentLayout.setLayoutParams(progressBarSizeRules);


        // Set play/stop button position
        RelativeLayout.LayoutParams positionRules = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        if(!MyApplication.isPlaying() || !MyApplication.isLoadingFinished) {
            positionRules.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        } else {
            positionRules.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.progress_bar_ring_parent_layout);
            positionRules.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        }

        fabIV.setLayoutParams(positionRules);


        // Set play/stop button size
        ViewGroup.LayoutParams fabIVSizeRules = fabIV.getLayoutParams();
        int height_width_value;

        // It works, don't touch it :)
        if(!MyApplication.isPlaying() || !MyApplication.isLoadingFinished) {
            height_width_value = (int)(height_width_value_progressBarParentLayout / 1.235) -
                    dpToPx(progressBarThicknessDB*2) - MyApplication.smallerDisplayDimensionPX / 120;
            padding = height_width_value/3;
        } else {
            height_width_value = (int)(height_width_value_progressBarParentLayout / 5.3125);
            padding = height_width_value/5;
        }

        fabIV.setPadding(padding, padding, padding, padding);
        fabIVSizeRules.width = height_width_value;
        fabIVSizeRules.height = height_width_value;
        fabIV.setLayoutParams(fabIVSizeRules);


        // Set play/stop button image
        if(MyApplication.isPlaying()) {
            fabIV.setImageResource(R.drawable.ic_pause);
        } else {
            fabIV.setImageResource(R.drawable.ic_play_arrow);
        }


        // Setup interval and chord text size
        MyApplication.setupIntervalAndChordTextSize(chordTextView, chordNumOneTextView, chordNumTwoTextView, 1);


        // Setup size for list view that shows what intervals are inside chord
        ViewGroup.LayoutParams whatIntervalsListViewLayoutParams = whatIntervalsListView.getLayoutParams();

        whatIntervalsListViewLayoutParams.width = MyApplication.smallerDisplayDimensionPX / 4;
        whatIntervalsListView.setLayoutParams(whatIntervalsListViewLayoutParams);

    }

    // Convert dp/dip (Density-independent Pixels) to px (pixels)
    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    // TODO: add this to quiz too
    // Set or remove flag to stop turning off screen while playing (or dimming it)
    private void setDontTurnOffScreen(boolean dontTurnOffScreen) {
        try {
            // Is change needed
            int flags = MainActivity.this.getWindow().getAttributes().flags;
            if (dontTurnOffScreen && (flags & WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) != 0) {
                // already set, change is not needed.
                return;
            } else if (!dontTurnOffScreen && (flags & WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) == 0) {
                // already cleared, change is not needed.
                return;
            }

            if(dontTurnOffScreen) {
                MainActivity.this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else {
                MainActivity.this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Recreate main activity if language has been changed
        if(languageChanged) {
            recreate();
            languageChanged = false;
        }

        // If user has previously logged in, set it again
        if(FirebaseHandler.user == null) {
            FirebaseHandler.user = new User();
        }
        FirebaseHandler.user.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // Download user photo for later use
        if(FirebaseHandler.user.photo == null) {
            try {
                FirebaseHandler.setupUserPhoto();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Get and save achievement progress data from cloud database
        if(User.updateAchievementProgress) {
            try {
                FirebaseHandler.updateAchievementProgress();
                // And by the way, get and save quiz high scores from cloud
                FirebaseHandler.firestoreUpdateHighScore();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Save current achievement progress data to cloud database
        if(User.updateAchievementProgressInCloud) {
            try {
                FirebaseHandler.firestoreUpdateAchievementProgressInCloud();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        // If intervals, chord or settings need to update / sync with database, do that.
        // This will run when app opens (and later on sometimes)

        if(DatabaseHandler.doIntervalsNeedUpdate()) {
            DatabaseHandler.updateIntervalsOnSeparateThread();
        }

        if(DatabaseHandler.doChordsNeedUpdate()) {
            DatabaseHandler.updateChordsOnSeparateThread();
        }

        if(DatabaseHandler.doSettingsNeedUpdate()) {
            DatabaseHandler.updateSettingsOnSeparateThread();
        }

        if(DatabaseHandler.doAchievementsNeedUpdate()) {
            DatabaseHandler.updateAchievementsOnSeparateThread();
        }



        updatePlayStopButton();

        MyApplication.stopPlayingChord();
        MyApplication.stopPlayingKey();
        MyApplication.activityResumed(MainActivity.this);

        if(MyApplication.isPlaying()) {
            setDontTurnOffScreen(true);
        }

        // User is not in the quiz
        QuizData.isQuizModePlaying = false;

        // Set that none of the quiz modes has been played recently (so the user can't get a quiz achievement anymore)
        AchievementChecker.lastPlayedQuizMode = AchievementChecker.NULL_QUIZ_ID;
    }

    // Set options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // Handle options menu action (there are two of them: options and profile)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_options: // Open options
                MyApplication.setIsPlaying(false);

                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_user_profile: // Open user profile
                MyApplication.setIsPlaying(false);

                Intent intent2 = new Intent(this, UserProfileActivity.class);
                startActivity(intent2);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // For split screen support
    @Override
    public void onConfigurationChanged(Configuration newConfig) { // TODO: add better split screen support
        super.onConfigurationChanged(newConfig);
        updatePlayStopButton();
    }

    @Override
    protected void onPause() {
        super.onPause();

        MyApplication.activityPaused();
        setDontTurnOffScreen(false);
    }

}

