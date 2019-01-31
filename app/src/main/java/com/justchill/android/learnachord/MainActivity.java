package com.justchill.android.learnachord;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
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

import com.justchill.android.learnachord.database.DatabaseHandler;
import com.justchill.android.learnachord.quiz.ChooseQuizModeActivity;
import com.justchill.android.learnachord.quiz.QuizData;
import com.justchill.android.learnachord.settings.SettingsActivity;

public class MainActivity extends AppCompatActivity {

    private ImageView fabIV;
    private ViewGroup parentLayout;
    private Drawable fabIVBackground;

    private ViewGroup progressBarParentLayout;

    private ViewGroup chordTextViewLayout;
    private TextView chordTextView;
    private TextView chordNumOneTextView;
    private TextView chordNumTwoTextView;

    private ListView whatIntervalsListView;

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


        parentLayout = (ViewGroup) findViewById(R.id.main_activity_parent_layout);
        chordTextViewLayout = (ViewGroup) findViewById(R.id.chord_text_view_linear_layout);

        progressBarParentLayout = (ViewGroup) findViewById(R.id.progress_bar_ring_parent_layout);

        chordTextView = (TextView) findViewById(R.id.chord_text_view);
        chordNumOneTextView = (TextView) findViewById(R.id.chord_number_one);
        chordNumTwoTextView = (TextView) findViewById(R.id.chord_number_two);

        whatIntervalsListView = findViewById(R.id.what_intervals_list_view);
        if(!MyApplication.isPlaying()) {
            whatIntervalsListView.setVisibility(View.INVISIBLE);
        }

        fabIV = (ImageView) findViewById(R.id.fab);
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

        fabIVBackground = fabIV.getBackground(); // This must be called after fabIV is initialised

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
                        fabIV.setClickable(true);
                        fabIV.setFocusable(true);
                        updatePlayStopButton();
                    }
                });
            }
        });


        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        displayWidth = size.x;
        displayHeight = size.y;

        if(MyApplication.smallerDisplayDimensionPX == null) {
            MyApplication.smallerDisplayDimensionPX = Math.min(displayWidth, displayHeight);
        }


        if(DatabaseHandler.doIntervalsNeedUpdate()) {
            DatabaseHandler.updateIntervalsOnSeparateThread();
        }

        if(DatabaseHandler.doChordsNeedUpdate()) {
            DatabaseHandler.updateChordsOnSeparateThread();
        }

        if(DatabaseHandler.doSettingsNeedUpdate()) {
            DatabaseHandler.updateSettingsOnSeparateThread();
        }


        if(MyApplication.isPlaying()) {
            chordTextViewLayout.setVisibility(View.VISIBLE);
        } else {
            chordTextViewLayout.setVisibility(View.INVISIBLE);
        }

        QuizData.isQuizModePlaying = false;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, null));
    }

    private void updatePlayStopButton() {

        // Get smaller dimension of the screen so image doesn't go out of screen
        int smallerDisplayDimension = displayWidth;
        if(displayHeight < smallerDisplayDimension) {
            smallerDisplayDimension = displayHeight;
        }


        // Set color
        if(!MyApplication.isLoadingFinished) {
            MyApplication.setupPlayButtonColor(MainActivity.this, fabIV, R.color.unloadedColor);
        } else if(MyApplication.isPlaying()) {
            MyApplication.setupPlayButtonColor(MainActivity.this, fabIV, R.color.stopButton);
        } else {
            MyApplication.setupPlayButtonColor(MainActivity.this, fabIV, R.color.playButton);
        }


        // Setup progress bar position and size
        ViewGroup.LayoutParams progressBarSizeRules = progressBarParentLayout.getLayoutParams();
        int height_width_value_progressBarParentLayout, padding;

        height_width_value_progressBarParentLayout = (int)(smallerDisplayDimension*0.75);
        progressBarSizeRules.width = height_width_value_progressBarParentLayout;
        progressBarSizeRules.height = height_width_value_progressBarParentLayout;
        progressBarParentLayout.setLayoutParams(progressBarSizeRules);


        // Setup play/stop button position
        RelativeLayout.LayoutParams positionRules = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        if(!MyApplication.isPlaying() || !MyApplication.isLoadingFinished) {
            positionRules.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        } else {
            positionRules.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.progress_bar_ring_parent_layout);
            positionRules.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        }

        fabIV.setLayoutParams(positionRules);


        // Setup play/stop button size
        ViewGroup.LayoutParams fabIVSizeRules = fabIV.getLayoutParams();
        int height_width_value;

        // It works, don't touch it
        if(!MyApplication.isPlaying() || !MyApplication.isLoadingFinished) {
            height_width_value = (int)(height_width_value_progressBarParentLayout / 1.235) -
                    dpToPx(progressBarThicknessDB*2) - smallerDisplayDimension / 120;
            padding = height_width_value/3;
        } else {
            height_width_value = (int)(height_width_value_progressBarParentLayout / 5.3125);
            padding = height_width_value/5;
        }

        fabIV.setPadding(padding, padding, padding, padding);
        fabIVSizeRules.width = height_width_value;
        fabIVSizeRules.height = height_width_value;
        fabIV.setLayoutParams(fabIVSizeRules);


        // Setup play/stop button image
        if(MyApplication.isPlaying()) {
            fabIV.setImageResource(R.drawable.ic_pause);
        } else {
            fabIV.setImageResource(R.drawable.ic_play_arrow);
        }


        // Setup interval and chord text size
        MyApplication.setupIntervalAndChordTextSize(chordTextView, chordNumOneTextView, chordNumTwoTextView, 1);


        // Setup size for list view that shows what intervals are inside chord
        ViewGroup.LayoutParams whatIntervalsListViewLayoutParams = whatIntervalsListView.getLayoutParams();

        whatIntervalsListViewLayoutParams.width = smallerDisplayDimension / 4;
        whatIntervalsListView.setLayoutParams(whatIntervalsListViewLayoutParams);

    }

    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px)
    {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

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

        updatePlayStopButton();

        MyApplication.stopPlayingChord();
        MyApplication.stopPlayingKey();
        MyApplication.activityResumed(MainActivity.this);

        if(MyApplication.isPlaying()) {
            setDontTurnOffScreen(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_options:
                MyApplication.setIsPlaying(false);

                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) { // TODO: add better split screen support
        super.onConfigurationChanged(newConfig);
//        Toast.makeText(this, "config", Toast.LENGTH_SHORT).show();
        updatePlayStopButton();
    }

    @Override
    protected void onPause() {
        super.onPause();

        MyApplication.activityPaused();
        setDontTurnOffScreen(false);
    }

}

