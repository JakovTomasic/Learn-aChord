package com.justchill.android.learnachord.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.justchill.android.learnachord.LocaleHelper;
import com.justchill.android.learnachord.MyApplication;
import com.justchill.android.learnachord.R;
import com.justchill.android.learnachord.intervalOrChord.Chord;
import com.justchill.android.learnachord.intervalOrChord.ChordsList;
import com.justchill.android.learnachord.intervalOrChord.Interval;
import com.justchill.android.learnachord.intervalOrChord.IntervalsList;
import com.justchill.android.learnachord.database.DataContract;
import com.justchill.android.learnachord.database.DatabaseData;
import com.justchill.android.learnachord.database.DatabaseHandler;

// Options activity
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Change media volume when volume buttons are pressed
        setVolumeControlStream(AudioManager.STREAM_MUSIC);


        // Save the current language so if language is changed, UI doesn't change until activity reset
        // This is needed because some of the UI will translate immediately, but some of them won't
        MyApplication.settingActivityLoadedLanguage = DatabaseData.appLanguage;

        // Set the content of the activity to use the activity_settings.xml layout file
        setContentView(R.layout.activity_settings);

        // Find the view pager that will allow the user to swipe between fragments
        ViewPager viewPager = findViewById(R.id.viewpager);

        // Create an adapter that knows which fragment should be shown on each page
        SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(this, getSupportFragmentManager());

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        // Find the tab layout that shows the tabs
        TabLayout tabLayout = findViewById(R.id.sliding_tabs);

        // Connect the tab layout with the view pager. This will
        //   1. Update the tab layout when the view pager is swiped
        //   2. Update the view pager when a tab is selected
        //   3. Set the tab layout's tab names with the view pager's adapter's titles by calling onPageTitle()
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {}

            @Override
            public void onPageSelected(int i) { // This method will be invoked when a new page becomes selected
                final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(findViewById(R.id.settings_activity_parent_view).getWindowToken(), 0);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {}
        });

        setTitle(R.string.options);
    }

    // For different languages support
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, null));
    }

    @Override
    protected void onResume() {
        super.onResume();

        MyApplication.activityResumed(SettingsActivity.this);

    }

    @Override
    protected void onPause() {
        super.onPause();

        // If something has changed, save options to database
        if(DatabaseHandler.doesDbNeedUpdate()) {
            DatabaseHandler.updateDatabaseOnSeparateThread();
        }

        MyApplication.activityPaused();

    }

    // Add options menu (for resetting the options)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    // Handle options menu actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                Interval tempInterval = IntervalsList.getBiggestCheckedInterval();
                Chord tempChord = ChordsList.getBiggestCheckedChord();
                if(!DatabaseData.directionUp && !DatabaseData.directionDown && !DatabaseData.directionSameTime && !DatabaseData.playWhatTone && !DatabaseData.playWhatOctave) {
                    showNoDirectionConfirmationDialog();
                } else if(IntervalsList.getCheckedIntervalCount() + ChordsList.getCheckedChordsCount() <= 0 && !DatabaseData.playWhatTone && !DatabaseData.playWhatOctave) {
                    showNoIntervalsOrChordsConfirmationDialog();
                } else if( (tempInterval != null && !IntervalsList.isIntervalInsideBorders(tempInterval)) ||
                        (tempChord != null && !ChordsList.isChordInsideBorders(tempChord)) ) {
                    showSmallRangeConfirmationDialog();
                } else {
                    finish();
                }
                return true;
            case R.id.action_reset_options:
                // returns how many rows were affected
                int rowsAffected = getContentResolver().delete(DataContract.UserPrefEntry.CONTENT_URI_FIRST_ROW, null, null);
                if(rowsAffected > 0) {
                    Thread updateIntervalAndSettingsThread = new Thread() {
                        @Override
                        public void run() {
                            DatabaseHandler.updateIntervals();
                            DatabaseHandler.updateChords();
                            DatabaseHandler.updateSettings();

                            DatabaseHandler.setDoSettingsNeedUpdate(true);
                            DatabaseHandler.setDoesDbNeedUpdate(false);
                            SettingsActivity.this.finish();
                            SettingsActivity.this.startActivity(getIntent());
                        }
                    };
                    updateIntervalAndSettingsThread.start();
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Show error if none of the playing directions is selected
    private void showNoDirectionConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.all_directions_unchecked_error);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog and stay in settings.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Show error if none of the intervals/chords/tones is selected
    private void showNoIntervalsOrChordsConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.all_intervals_and_chords_unchecked_error);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog and stay in settings.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Show error if set range is too small for some selected/checked intervals/chords
    private void showSmallRangeConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.too_small_range_error);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                IntervalsList.uncheckOutOfRangeIntervals();
                ChordsList.uncheckOutOfRangeChords();
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog and stay in settings.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
