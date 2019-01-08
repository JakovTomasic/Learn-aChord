package com.justchill.android.learnachord.settings;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
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
import com.justchill.android.learnachord.chord.Chord;
import com.justchill.android.learnachord.chord.ChordsList;
import com.justchill.android.learnachord.chord.Interval;
import com.justchill.android.learnachord.chord.IntervalsList;
import com.justchill.android.learnachord.database.DataContract;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyApplication.settingActivityLoadedLanguage = MyApplication.appLanguage;

        // Set the content of the activity to use the activity_settings.xml layout file
        setContentView(R.layout.activity_settings);

        // Find the view pager that will allow the user to swipe between fragments
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        // Create an adapter that knows which fragment should be shown on each page
        SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(this, getSupportFragmentManager());

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        // Find the tab layout that shows the tabs
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);

        // Connect the tab layout with the view pager. This will
        //   1. Update the tab layout when the view pager is swiped
        //   2. Update the view pager when a tab is selected
        //   3. Set the tab layout's tab names with the view pager's adapter's titles
        //      by calling onPageTitle()
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

        if(MyApplication.doesDbNeedUpdate()) {
            Thread userPrefThread = new Thread() {
                @SuppressLint("ApplySharedPref")
                @Override
                public void run() {
                    ContentValues values = new ContentValues();

                    String[] intervalKeys = DataContract.concatenateTwoArrays(
                            getResources().getStringArray(R.array.interval_keys),
                            getResources().getStringArray(R.array.interval_keys_above_octave));
                    for (int i = 0; i < intervalKeys.length; i++) {
                        values.put(intervalKeys[i], IntervalsList.getInterval(i).getIsChecked() ? DataContract.UserPrefEntry.CHECKBOX_CHECKED
                                : DataContract.UserPrefEntry.CHECKBOX_NOT_CHECKED);
                    }

                    String[] chordKeys = getResources().getStringArray(R.array.chord_keys);
                    for (int i = 0; i < chordKeys.length; i++) {
                        values.put(chordKeys[i], ChordsList.getChord(i).getIsChecked() ? DataContract.UserPrefEntry.CHECKBOX_CHECKED
                                : DataContract.UserPrefEntry.CHECKBOX_NOT_CHECKED);
                    }


                    String[] preferenceKeys = getResources().getStringArray(R.array.preference_keys);
                    values.put(preferenceKeys[0], MyApplication.directionUp ? DataContract.UserPrefEntry.CHECKBOX_CHECKED :
                            DataContract.UserPrefEntry.CHECKBOX_NOT_CHECKED);
                    values.put(preferenceKeys[1], MyApplication.directionDown ? DataContract.UserPrefEntry.CHECKBOX_CHECKED :
                            DataContract.UserPrefEntry.CHECKBOX_NOT_CHECKED);
                    values.put(preferenceKeys[2], MyApplication.directionSameTime ? DataContract.UserPrefEntry.CHECKBOX_CHECKED :
                            DataContract.UserPrefEntry.CHECKBOX_NOT_CHECKED);
                    values.put(preferenceKeys[3], (int) MyApplication.tonesSeparationTime);
                    values.put(preferenceKeys[4], (int) MyApplication.delayBetweenChords);
                    values.put(preferenceKeys[5], MyApplication.appLanguage);
                    values.put(preferenceKeys[6], MyApplication.downKeyBorder);
                    values.put(preferenceKeys[7], MyApplication.upKeyBorder);
                    values.put(preferenceKeys[8], MyApplication.showProgressBar ? DataContract.UserPrefEntry.CHECKBOX_CHECKED :
                            DataContract.UserPrefEntry.CHECKBOX_NOT_CHECKED);
                    values.put(preferenceKeys[9], MyApplication.showWhatIntervals ? DataContract.UserPrefEntry.CHECKBOX_CHECKED :
                            DataContract.UserPrefEntry.CHECKBOX_NOT_CHECKED);
                    values.put(preferenceKeys[10], MyApplication.chordTextScalingMode);
                    values.put(preferenceKeys[11], MyApplication.playingMode);
                    values.put(preferenceKeys[12], MyApplication.directionUpViewIndex);
                    values.put(preferenceKeys[13], MyApplication.directionDownViewIndex);
                    values.put(preferenceKeys[14], MyApplication.directionSameTimeViewIndex);
                    values.put(preferenceKeys[15], MyApplication.playWhatTone ? DataContract.UserPrefEntry.CHECKBOX_CHECKED :
                            DataContract.UserPrefEntry.CHECKBOX_NOT_CHECKED);
                    values.put(preferenceKeys[16], MyApplication.playWhatOctave ? DataContract.UserPrefEntry.CHECKBOX_CHECKED :
                            DataContract.UserPrefEntry.CHECKBOX_NOT_CHECKED);

                    int newRowUri = getContentResolver().update(DataContract.UserPrefEntry.CONTENT_URI_FIRST_ROW,
                            values, null, null); // returns how many rows were affected

                    MyApplication.setDoIntervalsNeedUpdate(false);
                    MyApplication.setDoChordsNeedUpdate(false);
                    MyApplication.setDoSettingsNeedUpdate(false);
                    MyApplication.setDoesDbNeedUpdate(false);

                    // If some setting is changed update interval and chord names (in case it is language)
                    if(newRowUri > 0) {
                        IntervalsList.updateAllIntervalsNames(SettingsActivity.this);
                        ChordsList.updateAllChordsNames(SettingsActivity.this);
                    }
                }
            };
            userPrefThread.start();
        }

        MyApplication.activityPaused();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                Interval tempInterval = IntervalsList.getBiggestCheckedInterval();
                Chord tempChord = ChordsList.getBiggestCheckedChord();
                if(!MyApplication.directionUp && !MyApplication.directionDown && !MyApplication.directionSameTime && !MyApplication.playWhatTone && !MyApplication.playWhatOctave) {
                    showNoDirectionConfirmationDialog();
                } else if(IntervalsList.getCheckedIntervalCount() + ChordsList.getCheckedChordsCount() <= 0 && !MyApplication.playWhatTone && !MyApplication.playWhatOctave) {
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
                            MyApplication.updateIntervals();
                            MyApplication.updateChords();
                            MyApplication.updateSettings();

                            MyApplication.setDoSettingsNeedUpdate(true);
                            MyApplication.setDoesDbNeedUpdate(false);
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
