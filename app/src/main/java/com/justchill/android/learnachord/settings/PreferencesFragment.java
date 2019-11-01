package com.justchill.android.learnachord.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.justchill.android.learnachord.MyApplication;
import com.justchill.android.learnachord.R;
import com.justchill.android.learnachord.RangeSeekBar;
import com.justchill.android.learnachord.database.DataContract;
import com.justchill.android.learnachord.database.DatabaseData;
import com.justchill.android.learnachord.database.DatabaseHandler;

import java.util.Locale;

// Other settings fragment
public class PreferencesFragment extends Fragment {

    // View of this fragment that is displaying in UI
    private View fragmentView;

    // Spinner for selecting playing mode (random or custom)
    private Spinner playingModeSpinner;
    // Question mark icon for opening playing mode help
    private TextView directionExplanationClickableTextView;
    // Direction up option, clickable view and checkbox
    private View directionUpView;
    private CheckBox directionUpCheckBox;

    // Direction down option, clickable view and checkbox
    private View directionDownView;
    private CheckBox directionDownCheckBox;

    // Direction simultaneously option, clickable view and checkbox
    private View directionSameTimeView;
    private CheckBox directionSameTimeCheckBox;

    // View and EditText for each tone duration
    private View tonesSeparationView;
    private EditText tonesSeparationEditText;

    // View and EditText for delay between playings
    private View tonesDurationView;
    private EditText tonesDurationEditText;

    // This is used to gain focus so focus can be removed from accessed EditText, not working perfectly
    private EditText uselessEditTextToRemoveFocus;

    // Spinner for selecting app language
    private Spinner languageSpinner;

    // TextView that is showing name of range's low key
    private TextView downBorderTextView;
    // TextView that is showing name of range's high key
    private TextView upBorderTextView;

    // Double seek bar for selecting range
    private RangeSeekBar<Integer> seekBar;

    // View and checkbox for show progress circle option
    private View showProgressBarView;
    private CheckBox showProgressBarCheckBox;

    // View and checkbox for show chord's intervals option
    private View showWhatIntervalsView;
    private CheckBox showWhatIntervalsCheckBox;

    // Spinner for changing layout theme
    private Spinner darkModeSpinner;
    // Spinner for changing text size
    private Spinner chordTextSizeSpinner;

    // Spinner for changing reminder time interval
    private Spinner reminderIntervalSpinner;
    // Parent view, number input and spinner for custom reminder time interval
    private View reminderCustomView;
    private EditText reminderCustomEditText;
    private Spinner reminderCustomIntervalSpinner;

    // Clickable view to send customer support mail
    private View contactUsClickableView;

    // Parent of all playing directions list
    private ViewGroup allPlayingModesParentView;
    // Saving last playing direction that was touched - for move up and down feature
    private View contextMenuView;

    private Context context;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_preferences, container, false);
        context = getContext();

        // Get all needed elements

        playingModeSpinner = fragmentView.findViewById(R.id.playing_mode_spinner);
        directionExplanationClickableTextView = fragmentView.findViewById(R.id.direction_explanation_clickable_text_view);

        directionUpView = fragmentView.findViewById(R.id.play_direction_up_clickable_view);
        directionUpCheckBox = fragmentView.findViewById(R.id.settings_up_check_box);

        directionDownView = fragmentView.findViewById(R.id.play_direction_down_clickable_view);
        directionDownCheckBox = fragmentView.findViewById(R.id.settings_down_check_box);

        directionSameTimeView = fragmentView.findViewById(R.id.play_direction_same_time_clickable_view);
        directionSameTimeCheckBox = fragmentView.findViewById(R.id.settings_same_time_check_box);

        tonesSeparationView = fragmentView.findViewById(R.id.tones_separation_linear_layout);
        tonesSeparationEditText = fragmentView.findViewById(R.id.second_sound_delay_number_input);

        tonesDurationView = fragmentView.findViewById(R.id.tones_duration_linear_layout);
        tonesDurationEditText = fragmentView.findViewById(R.id.between_chords_delay_number_input);

        uselessEditTextToRemoveFocus = fragmentView.findViewById(R.id.useless_edit_text_to_remove_focus);

        languageSpinner = fragmentView.findViewById(R.id.language_spinner);

        downBorderTextView = fragmentView.findViewById(R.id.down_border_text_view);
        upBorderTextView = fragmentView.findViewById(R.id.up_border_text_view);
        seekBar = fragmentView.findViewById(R.id.rangeSeekbar);

        showProgressBarView = fragmentView.findViewById(R.id.show_progress_bar_parent_layout);
        showProgressBarCheckBox = fragmentView.findViewById(R.id.settings_show_progress_bar_check_box);

        showWhatIntervalsView = fragmentView.findViewById(R.id.show_what_intervals_parent_layout);
        showWhatIntervalsCheckBox = fragmentView.findViewById(R.id.settings_what_intervals_check_box);

        darkModeSpinner = fragmentView.findViewById(R.id.dark_mode_spinner);
        chordTextSizeSpinner = fragmentView.findViewById(R.id.chord_text_size_spinner);

        reminderIntervalSpinner = fragmentView.findViewById(R.id.reminder_interval_spinner);

        reminderCustomView = fragmentView.findViewById(R.id.custom_reminder_parent_view);
        reminderCustomEditText = fragmentView.findViewById(R.id.custom_reminder_time_interval_number);
        reminderCustomIntervalSpinner = fragmentView.findViewById(R.id.custom_reminder_interval_spinner);

        contactUsClickableView = fragmentView.findViewById(R.id.contact_us_clickable_text_view);


        allPlayingModesParentView = fragmentView.findViewById(R.id.all_playing_modes_parent_layout);
        contextMenuView = directionUpView;


        setupPlayingModeSpinner();

        // For move up and down feature (opening move up/down action picker on long press)
        registerForContextMenu(directionUpView);
        registerForContextMenu(directionDownView);
        registerForContextMenu(directionSameTimeView);

        directionExplanationClickableTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDirectionExplanationDialog();
            }
        });

        directionUpCheckBox.setChecked(DatabaseData.directionUp);
        directionUpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                directionUpCheckBox.setChecked(!directionUpCheckBox.isChecked());
                DatabaseData.directionUp = directionUpCheckBox.isChecked();

                DataContract.UserPrefEntry.refreshDirectionsCount();

                DatabaseHandler.setDoesDbNeedUpdate(true);
                updateDurationViews();
            }
        });
        directionUpView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                contextMenuView = directionUpView;
                return false;
            }
        });

        directionDownCheckBox.setChecked(DatabaseData.directionDown);
        directionDownView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                directionDownCheckBox.setChecked(!directionDownCheckBox.isChecked());
                DatabaseData.directionDown = directionDownCheckBox.isChecked();

                DataContract.UserPrefEntry.refreshDirectionsCount();

                DatabaseHandler.setDoesDbNeedUpdate(true);
                updateDurationViews();
            }
        });
        directionDownView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                contextMenuView = directionDownView;
                return false;
            }
        });

        directionSameTimeCheckBox.setChecked(DatabaseData.directionSameTime);
        directionSameTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                directionSameTimeCheckBox.setChecked(!directionSameTimeCheckBox.isChecked());
                DatabaseData.directionSameTime = directionSameTimeCheckBox.isChecked();

                DataContract.UserPrefEntry.refreshDirectionsCount();

                DatabaseHandler.setDoesDbNeedUpdate(true);
                updateDurationViews();
            }
        });
        directionSameTimeView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                contextMenuView = directionSameTimeView;
                return false;
            }
        });

        tonesSeparationEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                return handleTonesSeparationEditText(actionId);
            }
        });
        tonesDurationEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                return handleTonesDurationEditText();
            }
        });

        setupLanguageSpinner();

        downBorderTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.playKey(DatabaseData.downKeyBorder);
            }
        });
        upBorderTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.playKey(DatabaseData.upKeyBorder);
            }
        });

        calculateRange(DatabaseData.downKeyBorder, DatabaseData.upKeyBorder);
        seekBar.setRangeValues(1, DataContract.UserPrefEntry.NUMBER_OF_KEYS);
        seekBar.setSelectedMinValue(DatabaseData.downKeyBorder);
        seekBar.setSelectedMaxValue(DatabaseData.upKeyBorder);
        seekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                calculateRange(minValue, maxValue);

                DatabaseData.downKeyBorder = minValue;
                DatabaseData.upKeyBorder = maxValue;
            }
        });
        seekBar.setNotifyWhileDragging(true); // I don't have idea what this is exactly doing

        showProgressBarCheckBox.setChecked(DatabaseData.showProgressBar);
        showProgressBarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressBarCheckBox.setChecked(!showProgressBarCheckBox.isChecked());
                DatabaseData.showProgressBar = showProgressBarCheckBox.isChecked();

                DatabaseHandler.setDoesDbNeedUpdate(true);
            }
        });

        showWhatIntervalsCheckBox.setChecked(DatabaseData.showWhatIntervals);
        showWhatIntervalsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWhatIntervalsCheckBox.setChecked(!showWhatIntervalsCheckBox.isChecked());
                DatabaseData.showWhatIntervals = showWhatIntervalsCheckBox.isChecked();

                DatabaseHandler.setDoesDbNeedUpdate(true);
            }
        });

        setupDarkModeSpinner();
        setupChordTextSizeSpinner();

        setupReminderIntervalSpinner();

        updateCustomReminderLayout(false);
        reminderCustomEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                hideKeyboardFrom();
                setCustomReminderNumber();
                return true;
            }
        });


        // When user clicks to contact us button, open mail and send it
        contactUsClickableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendContactUsMail();
            }
        });




        // Set order of playing directions
        updatePlayingModeViews();

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();

        // On fragment resume set what duration numbers to show
        updateDurationViews();
    }

    @Override
    public void onPause() {
        super.onPause();

        // If settings are up to date (updated from database) save durations (tone and between playing)
        if(!DatabaseHandler.doSettingsNeedUpdate()) {
            handleTonesSeparationEditText(null);
            handleTonesDurationEditText();
        }

        // If custom reminder menu is visible, save it's content
        if(reminderCustomView.getVisibility() == View.VISIBLE) {
            setCustomReminderNumber();
        }

        // Close the keyboard in case
        hideKeyboardFrom();
    }

    // Saves each tone duration value, returns onEditAction's return value
    private boolean handleTonesSeparationEditText(Integer actionId) {
        try {
            if(!tonesSeparationEditText.getText().toString().isEmpty()) {
                final double value = Double.valueOf(tonesSeparationEditText.getText().toString()) * 1000.0;
                if(value <= 0.01) {
                    if(DatabaseData.directionUp || !DatabaseData.directionDown) {
                        DatabaseData.directionUp = false;
                        DatabaseData.directionDown = false;
                        DatabaseData.directionSameTime = true;

                        DataContract.UserPrefEntry.refreshDirectionsCount();
                    }
                } else {
                    // Don't save value that is outside borders
                    if(value > DataContract.UserPrefEntry.maxTonesSeparationTime) {
                        DatabaseData.tonesSeparationTime = DataContract.UserPrefEntry.maxTonesSeparationTime;
                    } else if(value < DataContract.UserPrefEntry.minTonesSeparationTime) {
                        DatabaseData.tonesSeparationTime = DataContract.UserPrefEntry.minTonesSeparationTime;
                    } else {
                        DatabaseData.tonesSeparationTime = value;
                    }
                }

                DatabaseHandler.setDoesDbNeedUpdate(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (actionId == null || actionId != EditorInfo.IME_ACTION_NEXT) {
            hideKeyboardFrom();

            return true; // consume the action
        }

        return false; // pass on to other listeners.
    }

    // Saves delay between playing value, returns onEditAction's return value
    private boolean handleTonesDurationEditText() {
        hideKeyboardFrom();

        try {
            if(!tonesDurationEditText.getText().toString().isEmpty()) {
                final double value = Double.valueOf(tonesDurationEditText.getText().toString()) * 1000.0;
                // Don't save value that is outside borders
                if(value > DataContract.UserPrefEntry.maxChordDurationTime) {
                    DatabaseData.delayBetweenChords = DataContract.UserPrefEntry.maxChordDurationTime;
                } else if(value < DataContract.UserPrefEntry.minChordDurationTime) {
                    DatabaseData.delayBetweenChords = DataContract.UserPrefEntry.minChordDurationTime;
                } else {
                    DatabaseData.delayBetweenChords = value;
                }

                DatabaseHandler.setDoesDbNeedUpdate(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true; // consume the action
    }

    // Hides soft keyboard input
    private void hideKeyboardFrom() {
        tonesSeparationView.clearFocus();
        tonesDurationView.clearFocus();
        reminderCustomView.clearFocus();

        uselessEditTextToRemoveFocus.requestFocus();

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(fragmentView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    // Set duration options GUI
    private void updateDurationViews() {
        // These two are to save number when it is changed and check unchecked
        handleTonesSeparationEditText(null);

        final String format = "%.2f"; // 2 decimal points
        tonesSeparationEditText.setText(String.format(Locale.US, format, DatabaseData.tonesSeparationTime / 1000));
        tonesDurationEditText.setText(String.format(Locale.US, format, DatabaseData.delayBetweenChords / 1000));

        // If just simultaneously is selected, hide each tone duration option
        if(!DatabaseData.directionUp && !DatabaseData.directionDown && DatabaseData.directionSameTime) {
            tonesSeparationView.setVisibility(View.GONE);
        } else {
            tonesSeparationView.setVisibility(View.VISIBLE);
        }
    }

    // Sets GUI / range border keys' names depending on set language
    private void calculateRange(int minRange, int maxRange) {
        minRange--; // 0 to 60 (and not 1 - 61)
        maxRange--;

        String[] keys = MyApplication.getStringArrayByLocal(R.array.key_symbols);
        StringBuilder stringBuilder = new StringBuilder();

        if(DatabaseData.appLanguage == DataContract.UserPrefEntry.LANGUAGE_CROATIAN) {
            int minRangeOctaveNumber = (minRange/12) - 1;

            if(minRangeOctaveNumber > 0) {
                stringBuilder.append(keys[minRange%12]);
                stringBuilder.append(minRangeOctaveNumber);
            } else if(minRangeOctaveNumber == 0) {
                stringBuilder.append(keys[minRange%12]);
            } else if(minRangeOctaveNumber == -1) {
                String str = keys[minRange%12];
                String capitalizeFirstLetter = str.substring(0, 1).toUpperCase() + str.substring(1);
                stringBuilder.append(capitalizeFirstLetter);
            }

            downBorderTextView.setText(stringBuilder.toString());

            int maxRangeOctaveNumber = (maxRange/12) - 1;

            stringBuilder = new StringBuilder();

            if(maxRangeOctaveNumber > 0) {
                stringBuilder.append(keys[maxRange%12]);
                stringBuilder.append(maxRangeOctaveNumber);
            } else if(maxRangeOctaveNumber == 0) {
                stringBuilder.append(keys[maxRange%12]);
            } else if(maxRangeOctaveNumber == -1) {
                String str = keys[maxRange%12];
                String capitalizeFirstLetter = str.substring(0, 1).toUpperCase() + str.substring(1);
                stringBuilder.append(capitalizeFirstLetter);
            }

            upBorderTextView.setText(stringBuilder.toString());
        } else {
            stringBuilder.append(keys[minRange%12]);
            stringBuilder.append((minRange/12) + 2);

            downBorderTextView.setText(stringBuilder.toString());

            stringBuilder = new StringBuilder();
            stringBuilder.append(keys[maxRange%12]);
            stringBuilder.append((maxRange/12) + 2);

            upBorderTextView.setText(stringBuilder.toString());
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if(getActivity() != null) {
            getActivity().getMenuInflater().inflate(R.menu.custom_playing_mode_context_menu, menu);
        }
    }

    // This runs when user tries to change playing directions' order
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(DatabaseData.playingMode != DataContract.UserPrefEntry.PLAYING_MODE_CUSTOM) {
            // If playing mode was random, set to custom and set directions to default (and then move selected one)
            DatabaseData.playingMode = DataContract.UserPrefEntry.PLAYING_MODE_CUSTOM;
            setupPlayingModeSpinner();

            DatabaseData.directionUpViewIndex = DataContract.UserPrefEntry.DIRECTION_UP_VIEW_DEFAULT_INDEX;
            DatabaseData.directionDownViewIndex = DataContract.UserPrefEntry.DIRECTION_DOWN_VIEW_DEFAULT_INDEX;
            DatabaseData.directionSameTimeViewIndex = DataContract.UserPrefEntry.DIRECTION_SAME_TIME_VIEW_DEFAULT_INDEX;
        }

        int oldIndex;
        switch (item.getItemId()) {
            case R.id.move_up_option:
                if(contextMenuView == directionDownView) {
                    oldIndex = DatabaseData.directionDownViewIndex;
                    if(oldIndex <= 0) {
                        return true;
                    }
                } else if (contextMenuView == directionSameTimeView) {
                    oldIndex = DatabaseData.directionSameTimeViewIndex;
                    if(oldIndex <= 0) {
                        return true;
                    }
                } else { // directionUpView
                    oldIndex = DatabaseData.directionUpViewIndex;
                    if(oldIndex <= 0) {
                        return true;
                    }
                }

                if(DatabaseData.directionDownViewIndex == oldIndex-1) {
                    DatabaseData.directionDownViewIndex++;
                } else if(DatabaseData.directionSameTimeViewIndex == oldIndex-1) {
                    DatabaseData.directionSameTimeViewIndex++;
                } else {
                    DatabaseData.directionUpViewIndex++;
                }

                if(contextMenuView == directionDownView) {
                    DatabaseData.directionDownViewIndex--;
                } else if (contextMenuView == directionSameTimeView) {
                    DatabaseData.directionSameTimeViewIndex--;
                } else { // directionUpView
                    DatabaseData.directionUpViewIndex--;
                }

                updatePlayingModeViews();
                return true;
            case R.id.move_down_option:
                if(contextMenuView == directionDownView) {
                    oldIndex = DatabaseData.directionDownViewIndex;
                    if(oldIndex >= 2) {
                        return true;
                    }
                } else if (contextMenuView == directionSameTimeView) {
                    oldIndex = DatabaseData.directionSameTimeViewIndex;
                    if(oldIndex >= 2) {
                        return true;
                    }
                } else { // directionUpView
                    oldIndex = DatabaseData.directionUpViewIndex;
                    if(oldIndex >= 2) {
                        return true;
                    }
                }

                if(DatabaseData.directionDownViewIndex == oldIndex+1) {
                    DatabaseData.directionDownViewIndex--;
                } else if(DatabaseData.directionSameTimeViewIndex == oldIndex+1) {
                    DatabaseData.directionSameTimeViewIndex--;
                } else {
                    DatabaseData.directionUpViewIndex--;
                }

                if(contextMenuView == directionDownView) {
                    DatabaseData.directionDownViewIndex++;
                } else if (contextMenuView == directionSameTimeView) {
                    DatabaseData.directionSameTimeViewIndex++;
                } else { // directionUpView
                    DatabaseData.directionUpViewIndex++;
                }

                updatePlayingModeViews();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    // Sets up spinner for choosing playing mode
    private void setupPlayingModeSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout

        // Apply the adapter to the spinner
        // Specify dropdown layout style - custom spinner item - to change text and background color
        playingModeSpinner.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_item,
                getResources().getStringArray(R.array.playing_mode_options)));

        playingModeSpinner.setSelection(DatabaseData.playingMode);

        playingModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.custom))) {
                        DatabaseData.playingMode = DataContract.UserPrefEntry.PLAYING_MODE_CUSTOM;
                    } else { // random
                        DatabaseData.playingMode = DataContract.UserPrefEntry.PLAYING_MODE_RANDOM;
                    }

                    updatePlayingModeViews();

                    DatabaseHandler.setDoesDbNeedUpdate(true);
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                playingModeSpinner.setSelection(DatabaseData.appLanguage);
            }
        });
    }

    // Sets up spinner for choosing app language
    private void setupLanguageSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout

        // Apply the adapter to the spinner
        // Specify dropdown layout style - custom spinner item - to change text and background color
        languageSpinner.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_item,
                getResources().getStringArray(R.array.language_options)));

        languageSpinner.setSelection(DatabaseData.appLanguage);

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.english))) {
                        DatabaseData.appLanguage = DataContract.UserPrefEntry.LANGUAGE_ENGLISH;
                    } else {
                        DatabaseData.appLanguage = DataContract.UserPrefEntry.LANGUAGE_CROATIAN;
                    }

                    DatabaseHandler.setDoesDbNeedUpdate(true);
                }


            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                languageSpinner.setSelection(DatabaseData.appLanguage);
            }
        });
    }

    // Return order number of selected mode in dark mode spinner
    private int getDarkModeSelection(int modeId) {
        switch (modeId) {
            case AppCompatDelegate.MODE_NIGHT_NO:
                return 1;
            case AppCompatDelegate.MODE_NIGHT_YES:
                return 2;
            default: // auto
                return 0;
        }
    }

    // Sets up spinner for choosing layout theme
    private void setupDarkModeSpinner() {
        // Apply the adapter to the spinner
        // Specify dropdown layout style - custom spinner item - to change text and background color
        darkModeSpinner.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_item,
                getResources().getStringArray(R.array.dark_mode_options)));

        darkModeSpinner.setSelection(getDarkModeSelection(DatabaseData.nightModeId));

        darkModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                int modeBeforeChange = DatabaseData.nightModeId;
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.dark_mode_light))) {
                        DatabaseData.nightModeId = AppCompatDelegate.MODE_NIGHT_NO;
                    } else if (selection.equals(getString(R.string.dark_mode_dark))) {
                        DatabaseData.nightModeId = AppCompatDelegate.MODE_NIGHT_YES;
                    } else { // automatic mode
                        DatabaseData.nightModeId = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                    }

                    // If theme is changed, change ui
                    if(modeBeforeChange != DatabaseData.nightModeId) {
                        // Set new app theme
                        AppCompatDelegate.setDefaultNightMode(DatabaseData.nightModeId);
                        DatabaseHandler.setDoesDbNeedUpdate(true);

                        try {
                            getActivity().recreate();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                darkModeSpinner.setSelection(getDarkModeSelection(DatabaseData.nightModeId));
            }
        });
    }

    // Sets up spinner for choosing text size
    private void setupChordTextSizeSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout

        // Apply the adapter to the spinner
        // Specify dropdown layout style - custom spinner item - to change text and background color
        chordTextSizeSpinner.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_item,
                getResources().getStringArray(R.array.chord_text_size_options)));

        chordTextSizeSpinner.setSelection(DatabaseData.chordTextScalingMode);

        chordTextSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.small))) {
                        DatabaseData.chordTextScalingMode = DataContract.UserPrefEntry.CHORD_TEXT_SCALING_MODE_SMALL;
                    } else if (selection.equals(getString(R.string.normal))) {
                        DatabaseData.chordTextScalingMode = DataContract.UserPrefEntry.CHORD_TEXT_SCALING_MODE_NORMAL;
                    } else if (selection.equals(getString(R.string.large))) {
                        DatabaseData.chordTextScalingMode = DataContract.UserPrefEntry.CHORD_TEXT_SCALING_MODE_LARGE;
                    } else {
                        DatabaseData.chordTextScalingMode = DataContract.UserPrefEntry.CHORD_TEXT_SCALING_MODE_AUTO;
                    }
                    DatabaseHandler.setDoesDbNeedUpdate(true);
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                chordTextSizeSpinner.setSelection(DatabaseData.chordTextScalingMode);
            }
        });
    }

    // Selects item that should be selected in the interval spinner
    private void setReminderIntervalSpinnerSelection() {
        int valueToSet;
        switch (DatabaseData.reminderTimeIntervalMode) {
            case DataContract.UserPrefEntry.REMINDER_TIME_INTERVAL_HOUR:
                valueToSet = 4;
                break;
            case DataContract.UserPrefEntry.REMINDER_TIME_INTERVAL_DAY:
                valueToSet = 1;
                break;
            case DataContract.UserPrefEntry.REMINDER_TIME_INTERVAL_WEEK:
                valueToSet = 2;
                break;
            case DataContract.UserPrefEntry.REMINDER_TIME_INTERVAL_MONTH:
                valueToSet = 3;
                break;
            default:
                valueToSet = 0;
        }

        if(DatabaseData.reminderTimeIntervalNumber == 1) reminderIntervalSpinner.setSelection(valueToSet);
        else reminderIntervalSpinner.setSelection(4); // custom
    }

    // Sets up spinner for choosing reminders' time interval
    private void setupReminderIntervalSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout

        // Apply the adapter to the spinner
        // Specify dropdown layout style - custom spinner item - to change text and background color
        reminderIntervalSpinner.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_item,
                getResources().getStringArray(R.array.reminder_time_interval_options)));

        setReminderIntervalSpinnerSelection();

        reminderIntervalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    boolean customSelected = false;
                    if (selection.equals(getString(R.string.never))) {
                        DatabaseData.reminderTimeIntervalNumber = 1;
                        DatabaseData.reminderTimeIntervalMode = DataContract.UserPrefEntry.REMINDER_TIME_INTERVAL_NEVER;
                    } else if (selection.equals(getString(R.string.day))) {
                        DatabaseData.reminderTimeIntervalNumber = 1;
                        DatabaseData.reminderTimeIntervalMode = DataContract.UserPrefEntry.REMINDER_TIME_INTERVAL_DAY;
                    } else if (selection.equals(getString(R.string.week))) {
                        DatabaseData.reminderTimeIntervalNumber = 1;
                        DatabaseData.reminderTimeIntervalMode = DataContract.UserPrefEntry.REMINDER_TIME_INTERVAL_WEEK;
                    } else if (selection.equals(getString(R.string.month))) {
                        DatabaseData.reminderTimeIntervalNumber = 1;
                        DatabaseData.reminderTimeIntervalMode = DataContract.UserPrefEntry.REMINDER_TIME_INTERVAL_MONTH;
                    } else {
                        customSelected = true;
                    }
                    // Show or hide custom reminder time layout
                    updateCustomReminderLayout(customSelected);
                    DatabaseHandler.setDoesDbNeedUpdate(true);
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                setReminderIntervalSpinnerSelection();
            }
        });
    }

    // Saves reminder time interval number based on custom text input
    private void setCustomReminderNumber() {
        try {
            int reminderNumber = Integer.parseInt(reminderCustomEditText.getText().toString());
            if(reminderNumber < 1) {
                DatabaseData.reminderTimeIntervalNumber = 1;
                DatabaseData.reminderTimeIntervalMode = DataContract.UserPrefEntry.REMINDER_TIME_INTERVAL_NEVER;
            } else {
                DatabaseData.reminderTimeIntervalNumber = reminderNumber;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Selects item that should be selected in the custom interval spinner
    private void setCustomReminderIntervalSpinnerSelection() {
        int valueToSet;
        switch (DatabaseData.reminderTimeIntervalMode) {
            case DataContract.UserPrefEntry.REMINDER_TIME_INTERVAL_HOUR:
                valueToSet = 0;
                break;
            case DataContract.UserPrefEntry.REMINDER_TIME_INTERVAL_DAY:
                valueToSet = 1;
                break;
            case DataContract.UserPrefEntry.REMINDER_TIME_INTERVAL_MONTH:
                valueToSet = 3;
                break;
            default: // Week is the default mode
                valueToSet = 2;
        }

        reminderCustomIntervalSpinner.setSelection(valueToSet);
    }

    // Sets up layout for custom reminder time options
    private void updateCustomReminderLayout(boolean makeLayoutVisible) {
        // Set view visibility
        if(DatabaseData.reminderTimeIntervalNumber != 1 || makeLayoutVisible ||
                DatabaseData.reminderTimeIntervalMode == DataContract.UserPrefEntry.REMINDER_TIME_INTERVAL_HOUR) {
            reminderCustomView.setVisibility(View.VISIBLE);
        } else {
            reminderCustomView.setVisibility(View.GONE);
        }

        // Set number text
        reminderCustomEditText.setText(String.valueOf(DatabaseData.reminderTimeIntervalNumber));


        // Setup time interval mode spinner

        reminderCustomIntervalSpinner.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_item,
                getResources().getStringArray(R.array.custom_reminder_time_interval_options)));

        setCustomReminderIntervalSpinnerSelection();

        reminderCustomIntervalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.hour))) {
                        DatabaseData.reminderTimeIntervalMode = DataContract.UserPrefEntry.REMINDER_TIME_INTERVAL_HOUR;
                    } else if (selection.equals(getString(R.string.day))) {
                        DatabaseData.reminderTimeIntervalMode = DataContract.UserPrefEntry.REMINDER_TIME_INTERVAL_DAY;
                    } else if (selection.equals(getString(R.string.month))) {
                        DatabaseData.reminderTimeIntervalMode = DataContract.UserPrefEntry.REMINDER_TIME_INTERVAL_MONTH;
                    } else { // Week is the default mode
                        DatabaseData.reminderTimeIntervalMode = DataContract.UserPrefEntry.REMINDER_TIME_INTERVAL_WEEK;
                    }
                    DatabaseHandler.setDoesDbNeedUpdate(true);
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                setCustomReminderIntervalSpinnerSelection();
            }
        });
    }

    // Adds playing direction view to playing directions parent
    private void addViewToParent(ViewGroup parent, int index) {
        if(DatabaseData.directionDownViewIndex == index) {
            parent.addView(directionDownView);
        } else if(DatabaseData.directionSameTimeViewIndex == index) {
            parent.addView(directionSameTimeView);
        } else { // direction up
            parent.addView(directionUpView);
        }
    }

    // Sets order of playing directions
    private void updatePlayingModeViews() {
        if(DatabaseData.playingMode == DataContract.UserPrefEntry.PLAYING_MODE_CUSTOM) {
            allPlayingModesParentView.removeAllViews();
            for(int i = 0; i < 3; i++) {
                addViewToParent(allPlayingModesParentView, i);
            }
        } else {
            allPlayingModesParentView.removeAllViews();
            for(int i = 0; i < 3; i++) {
                // This is written like this for future change flexibility
                if(DataContract.UserPrefEntry.DIRECTION_DOWN_VIEW_DEFAULT_INDEX == i) {
                    allPlayingModesParentView.addView(directionDownView);
                } else if(DataContract.UserPrefEntry.DIRECTION_SAME_TIME_VIEW_DEFAULT_INDEX == i) {
                    allPlayingModesParentView.addView(directionSameTimeView);
                } else { // direction up
                    allPlayingModesParentView.addView(directionUpView);
                }
            }
        }
    }

    // Open mail app and allow user to send feedback mail
    private void sendContactUsMail() {
        // New intent to send mail to the specified address
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        // Send mail to "justchill.apps@gmail.com". Only email apps should handle this
        intent.setData(Uri.parse("mailto:justchill.apps@gmail.com"));
        // Set mail title
        intent.putExtra(Intent.EXTRA_SUBJECT, MyApplication.readResource(R.string.app_name, null) +
                ": " + MyApplication.readResource(R.string.contact_us, null));
//        intent.putExtra(Intent.EXTRA_TEXT, "Here, you can add specified default text of the mail");

        try {
            // Try to send mail if possible
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // Show explanation dialog when user clicks on question mark help icon
    private void showDirectionExplanationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);
        builder.setMessage(R.string.direction_explanation_dialog);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if(dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
    }

}
