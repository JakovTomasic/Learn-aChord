package com.justchill.android.learnachord.quiz;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.justchill.android.learnachord.LocaleHelper;
import com.justchill.android.learnachord.MyApplication;
import com.justchill.android.learnachord.ProgressBarAnimation;
import com.justchill.android.learnachord.R;
import com.justchill.android.learnachord.chord.Chord;
import com.justchill.android.learnachord.chord.ChordsList;
import com.justchill.android.learnachord.chord.Interval;
import com.justchill.android.learnachord.chord.IntervalsList;
import com.justchill.android.learnachord.database.DataContract;

import java.util.ArrayList;
import java.util.Random;

public class ModeThreeActivity extends AppCompatActivity {

    private ProgressBar timeLeftToPlayProgressBar;

    private TextView scoreTextView;

    private ImageView startClickableImageView;
    private ImageView pauseClickableImageView;

    private View parentSearchAndListView;
    private EditText searchTextInput;
    private ListView selectAnswerListView;

    private View submitAnswerButton;


    private Random rand;
    int checkedIntervals, checkedChords;

    Thread quizModeOnePlayThread;

    // Just a little delay between playing
    private final long addMS = 100;


    private final int timeLeftToPlayProgressThicknessDB = 4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_quiz_mode_three);
        setTitle(readResource(R.string.quiz) + readResource(R.string.quiz_mode_title_separator) + readResource(R.string.quiz_mode_three_title));



        rand = new Random();
        checkedIntervals = IntervalsList.getCheckedIntervalCount();
        checkedChords = ChordsList.getCheckedChordsCount();
        if((MyApplication.playWhatTone || MyApplication.playWhatOctave) && IntervalsList.getInterval(0).getIsChecked()) {
            // If tones can be played, don't count on čista prima
            checkedIntervals--;
        }

        timeLeftToPlayProgressBar = findViewById(R.id.ring_playing_progress_bar);

        scoreTextView = findViewById(R.id.quiz_score_text_view);


        startClickableImageView = (ImageView) findViewById(R.id.start_clickable_image_view);
        pauseClickableImageView = (ImageView) findViewById(R.id.pause_clickable_image_view);

        parentSearchAndListView = findViewById(R.id.quiz_mode_three_all_answers_list_parent_layout);

        selectAnswerListView = findViewById(R.id.quiz_mode_three_select_answer_list_view);
        searchTextInput = findViewById(R.id.quiz_mode_three_search_text_input);
        submitAnswerButton = findViewById(R.id.quiz_mode_three_submit_answer_clickable_layout);


        // Setup score text view text size
        scoreTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float)(MyApplication.smallerDisplayDimensionPX / 16) * MyApplication.scaledDensity);
        scoreTextView.setText(String.valueOf(MyApplication.quizScore));

        // Setup progress bar size
        ViewGroup.LayoutParams progressBarSizeRules = timeLeftToPlayProgressBar.getLayoutParams();
        progressBarSizeRules.width = MyApplication.smallerDisplayDimensionPX / 8;
        progressBarSizeRules.height = MyApplication.smallerDisplayDimensionPX / 8;
        timeLeftToPlayProgressBar.setLayoutParams(progressBarSizeRules);


        // Setup submit answer button size
        ViewGroup.LayoutParams submitAnswerViewSizeRules = submitAnswerButton.getLayoutParams();

        submitAnswerViewSizeRules.width = MyApplication.smallerDisplayDimensionPX / 6;
        submitAnswerViewSizeRules.height = MyApplication.smallerDisplayDimensionPX / 6;
        submitAnswerButton.setLayoutParams(submitAnswerViewSizeRules);


        // Setup start button size
        ViewGroup.LayoutParams startImageViewSizeRules = startClickableImageView.getLayoutParams();
        int height_width_value, padding;

        // Logic copied from MainActivity
        height_width_value = (int)(MyApplication.smallerDisplayDimensionPX * 0.75 / 2) - MyApplication.smallerDisplayDimensionPX / 120;
        padding = height_width_value/3;

        startClickableImageView.setPadding(padding, padding, padding, padding);
        startImageViewSizeRules.width = height_width_value;
        startImageViewSizeRules.height = height_width_value;
        startClickableImageView.setLayoutParams(startImageViewSizeRules);



        startClickableImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resumeQuiz();

                // If playing was stopped/paused play it again when resumed
                if(MyApplication.quizPlayingCurrentThing) {
                    playCurrentThing();
                } else {
                    playNextThing(0);
                }
            }
        });





        // Setup pause button size
        ViewGroup.LayoutParams pauseImageViewSizeRules = pauseClickableImageView.getLayoutParams();

        // Logic copied from MainActivity
        height_width_value = (int)(MyApplication.smallerDisplayDimensionPX * 0.75 / 5.3125);
        pauseImageViewSizeRules.width = height_width_value;
        pauseImageViewSizeRules.height = height_width_value;
        pauseClickableImageView.setLayoutParams(pauseImageViewSizeRules);

        if(MyApplication.quizPlayingPaused) {
            pauseQuiz();
        } else {
            resumeQuiz();
        }

        if(!MyApplication.isLoadingFinished) {
            startClickableImageView.setClickable(false);
            startClickableImageView.setFocusable(false);
        }



        pauseClickableImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseQuiz();

                stopPlaying();
            }
        });



        if(!MyApplication.isLoadingFinished) {
            MyApplication.setupPlayButtonColor(ModeThreeActivity.this, startClickableImageView, R.color.unloadedColor);
        }

        MyApplication.addActivityListener(new MyApplication.ActivityListener() {
            @Override
            public void onIsPlayingChange() {

            }

            @Override
            public void onLoadingFinished() {
                startClickableImageView.setClickable(true);
                startClickableImageView.setFocusable(true);
                MyApplication.setupPlayButtonColor(ModeThreeActivity.this, startClickableImageView, R.color.playButton);
            }
        });


        submitAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.waitingForQuizAnswer = false;
                MyApplication.quizPlayingCurrentThing = false;
                stopPlaying();

                if(MyApplication.quizModeThreeCorrectID.equals(MyApplication.quizModeThreeSelectedID)) {
                    scoreTextView.setText(String.valueOf(++MyApplication.quizScore));
                    playNextThing(0);
                } else {
                    gameOver();
                }
            }
        });



        // When search text changes, refresh list view to show just what was searched
        searchTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                refreshListView();
            }
        });


        MyApplication.isQuizModePlaying = true;

        refreshListView();

    }

    private void refreshListView() {
        MyApplication.quizModeThreeListOfPossibleAnswerIDs = new ArrayList<>();


        // Show perfect unison only if tones are not played
        if(!MyApplication.playWhatTone && !MyApplication.playWhatOctave && IntervalsList.getInterval(0).getIsChecked() && isIntervalSearchedFor(0)) {
            MyApplication.quizModeThreeListOfPossibleAnswerIDs.add(MyApplication.quizModeThreeIntervalIDAdd);
        }

        // Add intervals to list
        for(int i = 1; i < IntervalsList.getIntervalsCount(); i++) {
            // Add every checked intervals to list (it's name)
            if(IntervalsList.getInterval(i).getIsChecked() && isIntervalSearchedFor(i)) {
                MyApplication.quizModeThreeListOfPossibleAnswerIDs.add(MyApplication.quizModeThreeIntervalIDAdd + i);
            }
        }

        // Add chords to list
        for(int i = 0; i < ChordsList.getChordsCount(); i++) {
            // Add every checked chord to list (it's name)
            if(ChordsList.getChord(i).getIsChecked() && isChordSearchedFor(i)) {
                MyApplication.quizModeThreeListOfPossibleAnswerIDs.add(MyApplication.quizModeThreeChordIDAdd + i);
            }
        }

        boolean addThisTone;

        // Add tones to list
        for(int i = MyApplication.downKeyBorder; i <= MyApplication.upKeyBorder; i++) {

            addThisTone = MyApplication.playWhatTone && MyApplication.playWhatOctave;

            if(MyApplication.playWhatTone && !MyApplication.playWhatOctave && keysInSameOctave(MyApplication.quizLowestKey, i)) {
                addThisTone = true;
            }

            if(!MyApplication.playWhatTone && MyApplication.playWhatOctave && keysAreSameTone(MyApplication.quizLowestKey, i)) {
                addThisTone = true;
            }

            if(addThisTone && isToneSearchedFor(i)) {
                MyApplication.quizModeThreeListOfPossibleAnswerIDs.add(MyApplication.quizModeThreeToneIDAdd + i);
            }
        }


        // Set list view adapter with new data
        ModeThreeListAdapter modeThreeListAdapter = new ModeThreeListAdapter(ModeThreeActivity.this, MyApplication.quizModeThreeListOfPossibleAnswerIDs);

        modeThreeListAdapter.setListener(new ModeThreeListAdapter.onViewClickListener() {
            @Override
            public void onViewClick() {
                MyApplication.quizModeThreeShowSubmitButton = true;
                updateSubmitAnswerButton();
            }
        });

        selectAnswerListView.setAdapter(modeThreeListAdapter);

    }

    private boolean isIntervalSearchedFor(int intervalID) {
        return MyApplication.doesStringContainSubstring(
                MyApplication.getChordNameAsString(IntervalsList.getInterval(intervalID).getIntervalName(), null, null),
                searchTextInput.getText().toString());
    }

    private boolean isChordSearchedFor(int chordID) {
        Chord tempChord = ChordsList.getChord(chordID);

        String tempString;

        if(MyApplication.appLanguage == DataContract.UserPrefEntry.LANGUAGE_ENGLISH && chordID == MyApplication.MD9_ID) {
            // Mali durski/dominantni 9
            tempString = MyApplication.getChordNameAsString(MyApplication.MD9_ENG_TEXT, MyApplication.MD9_ENG_ONE, MyApplication.MD9_ENG_TWO);
        } else {
            tempString = MyApplication.getChordNameAsString(tempChord.getChordName(), tempChord.getNumberOneAsString(), tempChord.getNumberTwoAsString());
        }

        return MyApplication.doesStringContainSubstring(tempString, searchTextInput.getText().toString());
    }

    private boolean isToneSearchedFor(int toneID) {
        return MyApplication.doesStringContainSubstring(MyApplication.getKeyName(toneID), searchTextInput.getText().toString());
    }

    // Play next interval, chord or tone
    private void playNextThing(int numberOfRecursiveRuns) {
        if(MyApplication.waitingForQuizAnswer || numberOfRecursiveRuns > 5) {
            return;
        }

        if(checkedIntervals + checkedChords <= 1 && !MyApplication.playWhatTone && !MyApplication.playWhatOctave) {
            Toast.makeText(MyApplication.getActivity(), readResource(R.string.not_enough_selected_intervals_or_chords_error), Toast.LENGTH_SHORT).show();
            pauseQuiz();
            return;
        }

        if(MyApplication.directionsCount <= 0 && !MyApplication.playWhatTone && !MyApplication.playWhatOctave) {
            Toast.makeText(MyApplication.getActivity(), readResource(R.string.no_checked_playing_type_error), Toast.LENGTH_SHORT).show();
            pauseQuiz();
            return;
        }

        if(MyApplication.upKeyBorder - MyApplication.downKeyBorder <= 12 && MyApplication.playWhatOctave && !MyApplication.playWhatTone) {
            Toast.makeText(MyApplication.getActivity(), readResource(R.string.key_borders_are_too_small), Toast.LENGTH_SHORT).show();
            pauseQuiz();
            return;
        }

        // Reset this
        MyApplication.quizModeThreeCorrectID = null;
        MyApplication.quizModeThreeSelectedID = null;

        MyApplication.quizModeThreeShowSubmitButton = false;
        updateSubmitAnswerButton();

        // Try to remove cursor and keyboard
        searchTextInput.setText("");
        searchTextInput.clearFocus();
        MyApplication.hideKeyboardFromActivity(ModeThreeActivity.this);

        MyApplication.quizIntervalToPlay = null;
        MyApplication.quizChordToPlay = null;


        // 12 tones in one octave
        int tempRandNumb = rand.nextInt(checkedIntervals + checkedChords + ((MyApplication.playWhatTone || MyApplication.playWhatOctave) ? 12 : 0));

        if(MyApplication.directionsCount <= 0) {
            // If there is no direction to play, play tone
            tempRandNumb = rand.nextInt(12) + checkedIntervals + checkedChords;
        }

        if(tempRandNumb < checkedIntervals) { // Play interval
            // Don't play čista prima if tone can be played
            MyApplication.quizIntervalToPlay = IntervalsList.getRandomCheckedInterval((MyApplication.playWhatTone || MyApplication.playWhatOctave) ? IntervalsList.getInterval(0) : null);
            if(MyApplication.quizIntervalToPlay == null) {
                // Something went wrong, try again
                playNextThing(numberOfRecursiveRuns+1);
                return;
            }
            MyApplication.quizChordToPlay = null;

            MyApplication.quizModeThreeCorrectID = MyApplication.quizModeThreeIntervalIDAdd + MyApplication.quizIntervalToPlay.getDifference();


        } else if(tempRandNumb < checkedIntervals+checkedChords) { // Play chord
            MyApplication.quizChordToPlay = ChordsList.getRandomCheckedChord();
            if(MyApplication.quizChordToPlay == null) {
                // Something went wrong, try again
                playNextThing(numberOfRecursiveRuns+1);
                return;
            }
            MyApplication.quizIntervalToPlay = null;

            MyApplication.quizModeThreeCorrectID = MyApplication.quizModeThreeChordIDAdd + MyApplication.quizChordToPlay.getID();


        } else if(MyApplication.playWhatTone || MyApplication.playWhatOctave) {
            MyApplication.quizIntervalToPlay = null;
            MyApplication.quizChordToPlay = null;

            // Get random low key for playing (inside borders)
            MyApplication.quizLowestKey = getRandomKey();

            MyApplication.quizModeThreeCorrectID = MyApplication.quizModeThreeToneIDAdd + MyApplication.quizLowestKey;

        } else {
            // Something went wrong, try again
            playNextThing(numberOfRecursiveRuns+1);
            return;
        }

        if(MyApplication.quizIntervalToPlay != null || MyApplication.quizChordToPlay != null) {
            // Get random low key for playing (inside borders)
            MyApplication.quizLowestKey = getRandomKey();
        }


        MyApplication.waitingForQuizAnswer = true;



        refreshListView();

        playCurrentThing();

    }

    private void playCurrentThing() {
        MyApplication.quizPlayingCurrentThing = true;

        quizModeOnePlayThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int playingID = ++MyApplication.quizPlayingID;
                Integer directionToPlay = null;

                int numberOfTones = 1;
                if(MyApplication.quizIntervalToPlay != null) {
                    numberOfTones = 2;
                } else if(MyApplication.quizChordToPlay != null) {
                    numberOfTones = MyApplication.quizChordToPlay.getToneNumber();
                }
                double playingDuration = MyApplication.tonesSeparationTime * numberOfTones + MyApplication.delayBetweenChords + addMS;

                if(MyApplication.playingMode == DataContract.UserPrefEntry.PLAYING_MODE_CUSTOM) {
                    if(MyApplication.directionsCount > 0) {
                        playingDuration *= MyApplication.directionsCount;
                    }
                }

                updateProgressBarAnimation((long)playingDuration);


                // Logic copy-pasted (and changed) from ServicePlayer.Play()
                if(MyApplication.playingMode == DataContract.UserPrefEntry.PLAYING_MODE_CUSTOM) {
                    // Loop through all possibilities
                    for(int i = Math.min(Math.min(MyApplication.directionUpID, MyApplication.directionDownID), MyApplication.directionSameID); i <=
                            Math.max(Math.max(MyApplication.directionUpID, MyApplication.directionDownID), MyApplication.directionSameID); i++) {
                        directionToPlay = null;
                        if(MyApplication.directionUpViewIndex == i && MyApplication.directionUp) {
                            directionToPlay = MyApplication.directionUpID;
                        } else if(MyApplication.directionDownViewIndex == i && MyApplication.directionDown) {
                            directionToPlay = MyApplication.directionDownID;
                        } else if(MyApplication.directionSameTimeViewIndex == i && MyApplication.directionSameTime) {
                            directionToPlay = MyApplication.directionSameID;
                        }

                        if(directionToPlay == null) {
                            continue;
                        }

                        if(MyApplication.quizPlayingPaused) {
                            // If playing was paused, stop playing
                            return;
                        }

                        if(!(MyApplication.getActivity() instanceof ModeThreeActivity)) {
                            // If activity was exited, stop playing
                            MyApplication.quizPlayingCurrentThing = false;
                            return;
                        }

                        justPlayThis(directionToPlay, playingID);
                        if(MyApplication.quizIntervalToPlay == null && MyApplication.quizChordToPlay == null) {
                            // If tone is playing break the loop
                            break;
                        }
                    }

                    if(MyApplication.directionsCount <= 0) {
                        // If there is no direction to play, try to play an key
                        justPlayThis(null, playingID);
                    }
                } else if(MyApplication.playingMode == DataContract.UserPrefEntry.PLAYING_MODE_RANDOM) {
                    int randomNumb = rand.nextInt(MyApplication.directionsCount);
                    int counter = 0;
                    // Loop through IDs (and numbers in between) until you come to Id that is in place of randomNumb (in order)
                    for(int i = Math.min(Math.min(MyApplication.directionUpID, MyApplication.directionDownID), MyApplication.directionSameID); i <=
                            Math.max(Math.max(MyApplication.directionUpID, MyApplication.directionDownID), MyApplication.directionSameID); i++) {
                        if((MyApplication.directionUp && MyApplication.directionUpID == i) ||
                                (MyApplication.directionDown && MyApplication.directionDownID == i) ||
                                (MyApplication.directionSameTime && MyApplication.directionSameID == i)) {
                            if(counter >= randomNumb) {
                                directionToPlay = i;
                                break;
                            } else {
                                counter++;
                            }
                        }
                    }

                    if(directionToPlay == null) {
                        Log.e("ServicePlayer","Random algorithm is not working (ServicePlayer)");
                        // Something went wrong
                        MyApplication.quizPlayingCurrentThing = false;
                        return;
                    }

                    justPlayThis(directionToPlay, playingID);
                }

                if(!MyApplication.quizPlayingPaused && playingID == MyApplication.quizPlayingID) {
                    // If playing is not paused, it's finished
                    MyApplication.quizPlayingCurrentThing = false;
                }
            }
        });
        quizModeOnePlayThread.start();

    }

    private void justPlayThis(Integer directionToPlay, int playingID) {
        try {
            Thread.sleep(10);
        } catch (Exception e) {}

        if(playingID != MyApplication.quizPlayingID) {
            return;
        }

        if(MyApplication.quizIntervalToPlay != null && directionToPlay != null) {
            MyApplication.playChord(new Interval[]{MyApplication.quizIntervalToPlay}, MyApplication.quizLowestKey, directionToPlay);

            try {
                Thread.sleep((long)MyApplication.tonesSeparationTime * 2 + (long)MyApplication.delayBetweenChords + addMS);
            } catch (Exception e) {}
        } else if(MyApplication.quizChordToPlay != null && directionToPlay != null) {
            MyApplication.playChord(MyApplication.quizChordToPlay.getAllIntervals(), MyApplication.quizLowestKey, directionToPlay);

            try {
                Thread.sleep((long)MyApplication.tonesSeparationTime * (MyApplication.quizChordToPlay.getToneNumber()) + (long)MyApplication.delayBetweenChords + addMS);
            } catch (Exception e) {}
        } else if(MyApplication.playWhatTone || MyApplication.playWhatOctave) {
            MyApplication.playKey(MyApplication.quizLowestKey);

            try {
                Thread.sleep((long)MyApplication.tonesSeparationTime + (long)MyApplication.delayBetweenChords + addMS);
            } catch (Exception e) {}
        }
    }

    // TODO: random key uses key borders, it is not checking if key sound is loaded, check if key sound is loaded
    private int getRandomKey() {
        if(MyApplication.quizIntervalToPlay != null) {
            return rand.nextInt(MyApplication.upKeyBorder-MyApplication.downKeyBorder-MyApplication.quizIntervalToPlay.getDifference())+MyApplication.downKeyBorder;
        }

        if(MyApplication.quizChordToPlay != null) {
            return rand.nextInt(MyApplication.upKeyBorder-MyApplication.downKeyBorder-MyApplication.quizChordToPlay.getDifference())+MyApplication.downKeyBorder;
        }

        return rand.nextInt(MyApplication.upKeyBorder-MyApplication.downKeyBorder)+MyApplication.downKeyBorder;
    }

    // Are two keys inside same octave
    private boolean keysInSameOctave(int key1, int key2) {
        key1--;
        key2--;

        return (key1/12 == key2/12);
    }

    // Are two keys same tones (octave not included)
    private boolean keysAreSameTone(int key1, int key2) {
        key1--;
        key2--;

        return (key1%12 == key2%12);
    }


    private void gameOver() {
        // Save high score if greater than current
        MyApplication.refreshQuizModeThreeHighScore();
        MyApplication.quizScore = 0;


        showGameOverDialog();
    }


    private void updateProgressBarAnimation(long duration) {
        try {
            if(!MyApplication.showProgressBar && MyApplication.isLoadingFinished) {
                timeLeftToPlayProgressBar.setVisibility(View.INVISIBLE);
                return;
            } else {
                timeLeftToPlayProgressBar.setVisibility(View.VISIBLE);
            }

            ProgressBarAnimation progressBarAnimation = new ProgressBarAnimation(timeLeftToPlayProgressBar, 0, 100);
            progressBarAnimation.setDuration(duration);


            timeLeftToPlayProgressBar.startAnimation(progressBarAnimation);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private String readResource(int id) {
        return ModeThreeActivity.this.getResources().getString(id);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, null));
    }

    @Override
    protected void onResume() {
        super.onResume();

        MyApplication.activityResumed(ModeThreeActivity.this);


        // If sound is being played, don't show progress bar (animation stops after screen rotation)
        if(MyApplication.quizPlayingCurrentThing && !MyApplication.quizPlayingPaused) {
            timeLeftToPlayProgressBar.setVisibility(View.INVISIBLE);
        }

        updateSubmitAnswerButton();

    }

    @Override
    protected void onPause() {
        super.onPause();

        MyApplication.activityPaused();

        // Save high score if greater than current
        MyApplication.refreshQuizModeThreeHighScore();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.quiz_menu, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                MyApplication.quizPlayingCurrentThing = false;
                stopPlaying();

                finish();
                return true;
            case R.id.action_options:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateSubmitAnswerButton() {
        if(MyApplication.quizModeThreeShowSubmitButton) {
            submitAnswerButton.setVisibility(View.VISIBLE);
        } else {
            submitAnswerButton.setVisibility(View.GONE);
        }
    }

    private void stopPlaying() {
        MyApplication.quizPlayingID += 10;
        if(quizModeOnePlayThread != null) {
            quizModeOnePlayThread.interrupt();
            quizModeOnePlayThread = null;
        }
        MyApplication.setIsPlaying(false);

        // Set progress bar to 100 immediately
        updateProgressBarAnimation(0);
    }

    private void pauseQuiz() {
        startClickableImageView.setVisibility(View.VISIBLE);
        pauseClickableImageView.setVisibility(View.GONE);

        parentSearchAndListView.setVisibility(View.GONE);
        submitAnswerButton.setVisibility(View.GONE);


        MyApplication.quizPlayingPaused = true;
    }

    private void resumeQuiz() {
        startClickableImageView.setVisibility(View.GONE);
        pauseClickableImageView.setVisibility(View.VISIBLE);

        parentSearchAndListView.setVisibility(View.VISIBLE);
        updateSubmitAnswerButton();


        MyApplication.quizPlayingPaused = false;
    }


    private void showGameOverDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.quiz_game_over_message);
        builder.setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.setNegativeButton(R.string.try_again, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Refresh UI
                pauseQuiz();

                MyApplication.quizModeThreeShowSubmitButton = false;

                // Reset everything
                MyApplication.quizPlayingPaused = true;
                MyApplication.waitingForQuizAnswer = false;


                scoreTextView.setText(String.valueOf(MyApplication.quizScore));

                // User clicked the "try again" button, so dismiss the dialog and stay in quiz.
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
