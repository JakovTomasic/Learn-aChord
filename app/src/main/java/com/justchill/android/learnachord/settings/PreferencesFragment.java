package com.justchill.android.learnachord.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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

import com.justchill.android.learnachord.LocaleHelper;
import com.justchill.android.learnachord.MyApplication;
import com.justchill.android.learnachord.R;
import com.justchill.android.learnachord.RangeSeekBar;
import com.justchill.android.learnachord.database.DataContract;

import java.util.Locale;

public class PreferencesFragment extends Fragment {

    private View fragmentView;

    private Spinner playingModeSpinner;
    private TextView directionExplanationClickableTextView;
    private View directionUpView;
    private CheckBox directionUpCheckBox;
    private View directionDownView;
    private CheckBox directionDownCheckBox;
    private View directionSameTimeView;
    private CheckBox directionSameTimeCheckBox;
    private View tonesSeparationView;
    private EditText tonesSeparationEditText;
    private View tonesDurationView;
    private EditText tonesDurationEditText;
    private EditText uselessEditTextToRemoveFocus;
    private Spinner languageSpinner;
    private TextView rangeTextView;
    private TextView downBorderTextView;
    private TextView upBorderTextView;
    private RangeSeekBar<Integer> seekBar;
    private View showProgressBarView;
    private CheckBox showProgressBarCheckBox;
    private View showWhatIntervalsView;
    private CheckBox showWhatIntervalsCheckBox;
    private Spinner chordTextSizeSpinner;
//    private View chooseColorsView;
//    private View updateToPremiumView;

    private ViewGroup allPlayingModesParentView;
    private View contextMenuView;
//    private CheckBox contextMenuCheckBox;

    private Context context;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_preferences, container, false);
        context = getContext();

        playingModeSpinner = (Spinner) fragmentView.findViewById(R.id.playing_mode_spinner);
        directionExplanationClickableTextView = (TextView) fragmentView.findViewById(R.id.direction_explanation_clickable_text_view);

        directionUpView = fragmentView.findViewById(R.id.play_direction_up_clickable_view);
        directionUpCheckBox = fragmentView.findViewById(R.id.settings_up_check_box);

        directionDownView = fragmentView.findViewById(R.id.play_direction_down_clickable_view);
        directionDownCheckBox = fragmentView.findViewById(R.id.settings_down_check_box);

        directionSameTimeView = fragmentView.findViewById(R.id.play_direction_same_time_clickable_view);
        directionSameTimeCheckBox = fragmentView.findViewById(R.id.settings_same_time_check_box);

        tonesSeparationView = fragmentView.findViewById(R.id.tones_separation_linear_layout);
        tonesSeparationEditText = (EditText) fragmentView.findViewById(R.id.second_sound_delay_number_input);

        tonesDurationView = fragmentView.findViewById(R.id.tones_duration_linear_layout);
        tonesDurationEditText = (EditText) fragmentView.findViewById(R.id.between_chords_delay_number_input);

        uselessEditTextToRemoveFocus = (EditText) fragmentView.findViewById(R.id.useless_edit_text_to_remove_focus);

        languageSpinner = (Spinner) fragmentView.findViewById(R.id.language_spinner);

        rangeTextView = (TextView) fragmentView.findViewById(R.id.range_text_view);
        downBorderTextView = (TextView) fragmentView.findViewById(R.id.down_border_text_view);
        upBorderTextView = (TextView) fragmentView.findViewById(R.id.up_border_text_view);
        seekBar = fragmentView.findViewById(R.id.rangeSeekbar);

        showProgressBarView = fragmentView.findViewById(R.id.show_progress_bar_parent_layout);
        showProgressBarCheckBox = (CheckBox) fragmentView.findViewById(R.id.settings_show_progress_bar_check_box);

        showWhatIntervalsView = fragmentView.findViewById(R.id.show_what_intervals_parent_layout);
        showWhatIntervalsCheckBox = fragmentView.findViewById(R.id.settings_what_intervals_check_box);

        chordTextSizeSpinner = (Spinner) fragmentView.findViewById(R.id.chord_text_size_spinner);

//        chooseColorsView = fragmentView.findViewById(R.id.choose_colors_clickable_view);

//        updateToPremiumView = fragmentView.findViewById(R.id.remove_ads_clickable_view);


        allPlayingModesParentView = (ViewGroup) fragmentView.findViewById(R.id.all_playing_modes_parent_layout);
        contextMenuView = directionUpView;
//        contextMenuCheckBox = directionUpCheckBox;


        setupPlayingModeSpinner();

        registerForContextMenu(directionUpView);
        registerForContextMenu(directionDownView);
        registerForContextMenu(directionSameTimeView);

        directionExplanationClickableTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDirectionExplanationDialog();
            }
        });

        directionUpCheckBox.setChecked(MyApplication.directionUp);
        directionUpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                directionUpCheckBox.setChecked(!directionUpCheckBox.isChecked());
                MyApplication.directionUp = directionUpCheckBox.isChecked();

                MyApplication.refreshDirectionsCount();

                MyApplication.setDoesDbNeedUpdate(true);
                updateDurationViews();
            }
        });
        directionUpView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                contextMenuView = directionUpView;
//                contextMenuCheckBox = directionUpCheckBox;
                return false;
            }
        });

        directionDownCheckBox.setChecked(MyApplication.directionDown);
        directionDownView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                directionDownCheckBox.setChecked(!directionDownCheckBox.isChecked());
                MyApplication.directionDown = directionDownCheckBox.isChecked();

                MyApplication.refreshDirectionsCount();

                MyApplication.setDoesDbNeedUpdate(true);
                updateDurationViews();
            }
        });
        directionDownView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                contextMenuView = directionDownView;
//                contextMenuCheckBox = directionDownCheckBox;
                return false;
            }
        });

        directionSameTimeCheckBox.setChecked(MyApplication.directionSameTime);
        directionSameTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                directionSameTimeCheckBox.setChecked(!directionSameTimeCheckBox.isChecked());
                MyApplication.directionSameTime = directionSameTimeCheckBox.isChecked();

                MyApplication.refreshDirectionsCount();

                MyApplication.setDoesDbNeedUpdate(true);
                updateDurationViews();
            }
        });
        directionSameTimeView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                contextMenuView = directionSameTimeView;
//                contextMenuCheckBox = directionSameTimeCheckBox;
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
                MyApplication.playKey(MyApplication.downKeyBorder);
            }
        });
        upBorderTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.playKey(MyApplication.upKeyBorder);
            }
        });

        calculateRange(MyApplication.downKeyBorder, MyApplication.upKeyBorder);
        seekBar.setRangeValues(1, DataContract.UserPrefEntry.NUMBER_OF_KEYS);
        seekBar.setSelectedMinValue(MyApplication.downKeyBorder);
        seekBar.setSelectedMaxValue(MyApplication.upKeyBorder);
        seekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                calculateRange(minValue, maxValue);

                MyApplication.downKeyBorder = minValue;
                MyApplication.upKeyBorder = maxValue;
            }
        });
        seekBar.setNotifyWhileDragging(true); // I don't have idea what this is exactly doing

        showProgressBarCheckBox.setChecked(MyApplication.showProgressBar);
        showProgressBarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressBarCheckBox.setChecked(!showProgressBarCheckBox.isChecked());
                MyApplication.showProgressBar = showProgressBarCheckBox.isChecked();

                MyApplication.setDoesDbNeedUpdate(true);
            }
        });

        showWhatIntervalsCheckBox.setChecked(MyApplication.showWhatIntervals);
        showWhatIntervalsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWhatIntervalsCheckBox.setChecked(!showWhatIntervalsCheckBox.isChecked());
                MyApplication.showWhatIntervals = showWhatIntervalsCheckBox.isChecked();

                MyApplication.setDoesDbNeedUpdate(true);
            }
        });

        setupChordTextSizeSpinner();

//        chooseColorsView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getContext(), "Color", Toast.LENGTH_SHORT).show();
//                MyApplication.setDoesDbNeedUpdate(true);
//                // TODO: implement this (but nit here), choose each interval, chord and tone color
//            }
//        });

//        updateToPremiumView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getContext(), "Pro", Toast.LENGTH_SHORT).show();
//                // TODO: implement this if paid version is added
//            }
//        });

        updateRoundButtonColor(downBorderTextView.getBackground());
        updateRoundButtonColor(upBorderTextView.getBackground());

        updatePlayingModeViews();

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();

        updateDurationViews();
    }

    @Override
    public void onPause() {
        super.onPause();

        if(!MyApplication.doSettingsNeedUpdate()) {
            handleTonesSeparationEditText(null);
            handleTonesDurationEditText();
        }

        hideKeyboardFrom();
    }

    private void updateRoundButtonColor(Drawable drawable) {
        if (drawable instanceof ShapeDrawable) {
            // cast to 'ShapeDrawable'
            ShapeDrawable shapeDrawable = (ShapeDrawable) drawable;
            shapeDrawable.getPaint().setColor(ContextCompat.getColor(MyApplication.getAppContext(), R.color.playButton));
        } else if (drawable instanceof GradientDrawable) {
            // cast to 'GradientDrawable'
            GradientDrawable gradientDrawable = (GradientDrawable) drawable;
            gradientDrawable.setColor(ContextCompat.getColor(MyApplication.getAppContext(), R.color.playButton));
        } else if (drawable instanceof ColorDrawable) {
            // alpha value may need to be set again after this call
            ColorDrawable colorDrawable = (ColorDrawable) drawable;
            colorDrawable.setColor(ContextCompat.getColor(MyApplication.getAppContext(), R.color.playButton));
        }
    }

    private boolean handleTonesSeparationEditText(Integer actionId) {
        try {
            if(!tonesSeparationEditText.getText().toString().isEmpty()) {
                final double value = Double.valueOf(tonesSeparationEditText.getText().toString()) * 1000.0;
                if(value <= 0.01) {
                    if(MyApplication.directionUp || !MyApplication.directionDown) {
                        MyApplication.directionUp = false;
                        MyApplication.directionDown = false;
                        MyApplication.directionSameTime = true;

                        MyApplication.refreshDirectionsCount();
                    }
                } else {
                    if(value > MyApplication.maxTonesSeparationTime) {
                        MyApplication.tonesSeparationTime = MyApplication.maxTonesSeparationTime;
                    } else if(value < MyApplication.minTonesSeparationTime) {
                        MyApplication.tonesSeparationTime = MyApplication.minTonesSeparationTime;
                    } else {
                        MyApplication.tonesSeparationTime = value;
                    }
                }

                MyApplication.setDoesDbNeedUpdate(true);
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

    private boolean handleTonesDurationEditText() {
        hideKeyboardFrom();

        try {
            if(!tonesDurationEditText.getText().toString().isEmpty()) {
                final double value = Double.valueOf(tonesDurationEditText.getText().toString()) * 1000.0;
                if(value > MyApplication.maxChordDurationTime) {
                    MyApplication.delayBetweenChords = MyApplication.maxChordDurationTime;
                } else if(value < MyApplication.minChordDurationTime) {
                    MyApplication.delayBetweenChords = MyApplication.minChordDurationTime;
                } else {
                    MyApplication.delayBetweenChords = value;
                }

                MyApplication.setDoesDbNeedUpdate(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true; // consume the action
    }

    private void hideKeyboardFrom() {
        tonesSeparationView.clearFocus();
        tonesDurationView.clearFocus();

        uselessEditTextToRemoveFocus.requestFocus();

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(fragmentView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void updateDurationViews() {
        // These two are to save number when it is changed and check unchecked
        handleTonesSeparationEditText(null);
//        handleTonesDurationEditText();

        final String format = "%.2f"; // 2 decimal points
        tonesSeparationEditText.setText(String.format(Locale.US, format, MyApplication.tonesSeparationTime / 1000));
        tonesDurationEditText.setText(String.format(Locale.US, format, MyApplication.delayBetweenChords / 1000));

        if(!MyApplication.directionUp && !MyApplication.directionDown && MyApplication.directionSameTime) {
            tonesSeparationView.setVisibility(View.GONE);
//            tonesDurationView.setVisibility(View.GONE);
        } else {
            tonesSeparationView.setVisibility(View.VISIBLE);
//            tonesDurationView.setVisibility(View.VISIBLE);
        }
    }

    private void calculateRange(int minRange, int maxRange) {
        minRange--; // 0 to 60 (and not 1 - 61)
        maxRange--;

        String[] keys = getResources().getStringArray(R.array.key_symbols);
        StringBuilder stringBuilder = new StringBuilder();

        if(MyApplication.appLanguage == DataContract.UserPrefEntry.LANGUAGE_CROATIAN) {
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
//        menu.setHeaderTitle("Choose action");
        if(getActivity() != null) {
            getActivity().getMenuInflater().inflate(R.menu.custom_playing_mode_context_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(MyApplication.playingMode != DataContract.UserPrefEntry.PLAYING_MODE_CUSTOM) {
            MyApplication.playingMode = DataContract.UserPrefEntry.PLAYING_MODE_CUSTOM;
            setupPlayingModeSpinner();

            MyApplication.directionUpViewIndex = DataContract.UserPrefEntry.DIRECTION_UP_VIEW_DEFAULT_INDEX;
            MyApplication.directionDownViewIndex = DataContract.UserPrefEntry.DIRECTION_DOWN_VIEW_DEFAULT_INDEX;
            MyApplication.directionSameTimeViewIndex = DataContract.UserPrefEntry.DIRECTION_SAME_TIME_VIEW_DEFAULT_INDEX;
        }

        int oldIndex;
        switch (item.getItemId()) {
            case R.id.move_up_option:
//                Toast.makeText(context, "Option 1 selected", Toast.LENGTH_SHORT).show();
                if(contextMenuView == directionDownView) {
                    oldIndex = MyApplication.directionDownViewIndex;
                    if(oldIndex <= 0) {
                        return true;
                    }
                } else if (contextMenuView == directionSameTimeView) {
                    oldIndex = MyApplication.directionSameTimeViewIndex;
                    if(oldIndex <= 0) {
                        return true;
                    }
                } else { // directionUpView
                    oldIndex = MyApplication.directionUpViewIndex;
                    if(oldIndex <= 0) {
                        return true;
                    }
                }

                if(MyApplication.directionDownViewIndex == oldIndex-1) {
                    MyApplication.directionDownViewIndex++;
                } else if(MyApplication.directionSameTimeViewIndex == oldIndex-1) {
                    MyApplication.directionSameTimeViewIndex++;
                } else {
                    MyApplication.directionUpViewIndex++;
                }

                if(contextMenuView == directionDownView) {
                    MyApplication.directionDownViewIndex--;
                } else if (contextMenuView == directionSameTimeView) {
                    MyApplication.directionSameTimeViewIndex--;
                } else { // directionUpView
                    MyApplication.directionUpViewIndex--;
                }

                updatePlayingModeViews();
                return true;
            case R.id.move_down_option:
//                Toast.makeText(context, "Option 2 selected", Toast.LENGTH_SHORT).show();
                if(contextMenuView == directionDownView) {
                    oldIndex = MyApplication.directionDownViewIndex;
                    if(oldIndex >= 2) {
                        return true;
                    }
                } else if (contextMenuView == directionSameTimeView) {
                    oldIndex = MyApplication.directionSameTimeViewIndex;
                    if(oldIndex >= 2) {
                        return true;
                    }
                } else { // directionUpView
                    oldIndex = MyApplication.directionUpViewIndex;
                    if(oldIndex >= 2) {
                        return true;
                    }
                }

                if(MyApplication.directionDownViewIndex == oldIndex+1) {
                    MyApplication.directionDownViewIndex--;
                } else if(MyApplication.directionSameTimeViewIndex == oldIndex+1) {
                    MyApplication.directionSameTimeViewIndex--;
                } else {
                    MyApplication.directionUpViewIndex--;
                }

                if(contextMenuView == directionDownView) {
                    MyApplication.directionDownViewIndex++;
                } else if (contextMenuView == directionSameTimeView) {
                    MyApplication.directionSameTimeViewIndex++;
                } else { // directionUpView
                    MyApplication.directionUpViewIndex++;
                }

                updatePlayingModeViews();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void setupPlayingModeSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout

        // Apply the adapter to the spinner
        // Specify dropdown layout style - custom spinner item - to change text and background color
        playingModeSpinner.setAdapter(new ArrayAdapter<String>(context, R.layout.spinner_item,
                getResources().getStringArray(R.array.playing_mode_options)));

        playingModeSpinner.setSelection(MyApplication.playingMode);

        // Set the integer mSelected to the constant values
        playingModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.custom))) {
                        MyApplication.playingMode = DataContract.UserPrefEntry.PLAYING_MODE_CUSTOM;
                    } else { // random
                        MyApplication.playingMode = DataContract.UserPrefEntry.PLAYING_MODE_RANDOM;
                    }

                    updatePlayingModeViews();

                    MyApplication.setDoesDbNeedUpdate(true);
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                playingModeSpinner.setSelection(MyApplication.appLanguage);
            }
        });
    }

    private void setupLanguageSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout

        // Apply the adapter to the spinner
        // Specify dropdown layout style - custom spinner item - to change text and background color
        languageSpinner.setAdapter(new ArrayAdapter<String>(context, R.layout.spinner_item,
                getResources().getStringArray(R.array.language_options)));

        languageSpinner.setSelection(MyApplication.appLanguage);

        // Set the integer mSelected to the constant values
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.english))) {
                        MyApplication.appLanguage = DataContract.UserPrefEntry.LANGUAGE_ENGLISH;
                    } else {
                        MyApplication.appLanguage = DataContract.UserPrefEntry.LANGUAGE_CROATIAN;
                    }

                    MyApplication.setDoesDbNeedUpdate(true);
                }

                LocaleHelper.setLocale(context, null);

            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                languageSpinner.setSelection(MyApplication.appLanguage);
            }
        });
    }

    private void setupChordTextSizeSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout

        // Apply the adapter to the spinner
        // Specify dropdown layout style - custom spinner item - to change text and background color
        chordTextSizeSpinner.setAdapter(new ArrayAdapter<String>(context, R.layout.spinner_item,
                getResources().getStringArray(R.array.chord_text_size_options)));

        chordTextSizeSpinner.setSelection(MyApplication.chordTextScalingMode);

        // Set the integer mSelected to the constant values
        chordTextSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.small))) {
                        MyApplication.chordTextScalingMode = DataContract.UserPrefEntry.CHORD_TEXT_SCALING_MODE_SMALL;
                    } else if (selection.equals(getString(R.string.normal))) {
                        MyApplication.chordTextScalingMode = DataContract.UserPrefEntry.CHORD_TEXT_SCALING_MODE_NORMAL;
                    } else if (selection.equals(getString(R.string.large))) {
                        MyApplication.chordTextScalingMode = DataContract.UserPrefEntry.CHORD_TEXT_SCALING_MODE_LARGE;
                    } else {
                        MyApplication.chordTextScalingMode = DataContract.UserPrefEntry.CHORD_TEXT_SCALING_MODE_AUTO;
                    }
                    MyApplication.setDoesDbNeedUpdate(true);
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                chordTextSizeSpinner.setSelection(MyApplication.chordTextScalingMode);
            }
        });
    }

    private void addViewToParent(ViewGroup parent, int index) {
        if(MyApplication.directionDownViewIndex == index) {
            parent.addView(directionDownView);
        } else if(MyApplication.directionSameTimeViewIndex == index) {
            parent.addView(directionSameTimeView);
        } else { // direction up
            parent.addView(directionUpView);
        }
    }

    private void updatePlayingModeViews() {
//        Log.d("Pref", MyApplication.directionUpViewIndex + ", " + MyApplication.directionDownViewIndex + ", " +
//                MyApplication.directionSameTimeViewIndex);
        if(MyApplication.playingMode == DataContract.UserPrefEntry.PLAYING_MODE_CUSTOM) {
            allPlayingModesParentView.removeAllViews();
            for(int i = 0; i < 3; i++) {
                addViewToParent(allPlayingModesParentView, i);
            }
        } else {
            allPlayingModesParentView.removeAllViews();
            for(int i = 0; i < 3; i++) {
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

    private void showDirectionExplanationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
        alertDialog.show();
    }

}
