package com.justchill.android.learnachord.quiz;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
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
import com.justchill.android.learnachord.intervalOrChord.Chord;
import com.justchill.android.learnachord.intervalOrChord.ChordsList;
import com.justchill.android.learnachord.intervalOrChord.Interval;
import com.justchill.android.learnachord.intervalOrChord.IntervalsList;
import com.justchill.android.learnachord.database.DataContract;
import com.justchill.android.learnachord.database.DatabaseData;

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
    private static final long addMS = 100;


    private static final int timeLeftToPlayProgressThicknessDB = 4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_quiz_mode_three);
        setTitle(readResource(R.string.quiz) + readResource(R.string.quiz_mode_title_separator) + readResource(R.string.quiz_mode_three_title));

        // Change media volume when volume buttons are pressed
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        rand = new Random();
        checkedIntervals = IntervalsList.getCheckedIntervalCount();
        checkedChords = ChordsList.getCheckedChordsCount();
        if((DatabaseData.playWhatTone || DatabaseData.playWhatOctave) && IntervalsList.getInterval(0).getIsChecked()) {
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
        scoreTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float)(MyApplication.smallerDisplayDimensionPX / 16) * DatabaseData.scaledDensity);
        scoreTextView.setText(String.valueOf(QuizData.quizScore));

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
                if(QuizData.quizPlayingCurrentThing) {
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

        if(QuizData.quizPlayingPaused) {
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
                QuizData.waitingForQuizAnswer = false;
                QuizData.quizPlayingCurrentThing = false;
                stopPlaying();

                if(QuizData.quizModeThreeCorrectID.equals(QuizData.quizModeThreeSelectedID)) {
                    scoreTextView.setText(String.valueOf(++QuizData.quizScore));
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


        QuizData.isQuizModePlaying = true;

        refreshListView();

    }

    private void refreshListView() {
        QuizData.quizModeThreeListOfPossibleAnswerIDs = new ArrayList<>();


        // Show perfect unison only if tones are not played
        if(!DatabaseData.playWhatTone && !DatabaseData.playWhatOctave && IntervalsList.getInterval(0).getIsChecked() && isIntervalSearchedFor(0)) {
            QuizData.quizModeThreeListOfPossibleAnswerIDs.add(QuizData.quizModeThreeIntervalIDAdd);
        }

        // Add intervals to list
        for(int i = 1; i < IntervalsList.getIntervalsCount(); i++) {
            // Add every checked intervals to list (it's name)
            if(IntervalsList.getInterval(i).getIsChecked() && isIntervalSearchedFor(i)) {
                QuizData.quizModeThreeListOfPossibleAnswerIDs.add(QuizData.quizModeThreeIntervalIDAdd + i);
            }
        }

        // Add chords to list
        for(int i = 0; i < ChordsList.getChordsCount(); i++) {
            // Add every checked chord to list (it's name)
            if(ChordsList.getChord(i).getIsChecked() && isChordSearchedFor(i)) {
                QuizData.quizModeThreeListOfPossibleAnswerIDs.add(QuizData.quizModeThreeChordIDAdd + i);
            }
        }

        boolean addThisTone;

        // Add tones to list
        for(int i = DatabaseData.downKeyBorder; i <= DatabaseData.upKeyBorder; i++) {

            addThisTone = DatabaseData.playWhatTone && DatabaseData.playWhatOctave;

            if(DatabaseData.playWhatTone && !DatabaseData.playWhatOctave && keysInSameOctave(QuizData.quizLowestKey, i)) {
                addThisTone = true;
            }

            if(!DatabaseData.playWhatTone && DatabaseData.playWhatOctave && keysAreSameTone(QuizData.quizLowestKey, i)) {
                addThisTone = true;
            }

            if(addThisTone && isToneSearchedFor(i)) {
                QuizData.quizModeThreeListOfPossibleAnswerIDs.add(QuizData.quizModeThreeToneIDAdd + i);
            }
        }


        // Set list view adapter with new data
        ModeThreeListAdapter modeThreeListAdapter = new ModeThreeListAdapter(ModeThreeActivity.this, QuizData.quizModeThreeListOfPossibleAnswerIDs);

        modeThreeListAdapter.setListener(new ModeThreeListAdapter.onViewClickListener() {
            @Override
            public void onViewClick() {
                QuizData.quizModeThreeShowSubmitButton = true;
                updateSubmitAnswerButton();
            }
        });

        selectAnswerListView.setAdapter(modeThreeListAdapter);

    }

    private boolean isIntervalSearchedFor(int intervalID) {
        return MyApplication.doesStringContainSubstring(
                MyApplication.getChordNameAsString(IntervalsList.getInterval(intervalID).getName(), null, null),
                searchTextInput.getText().toString());
    }

    private boolean isChordSearchedFor(int chordID) {
        Chord tempChord = ChordsList.getChord(chordID);

        String tempString;

        if(DatabaseData.appLanguage == DataContract.UserPrefEntry.LANGUAGE_ENGLISH && chordID == MyApplication.MD9_ID) {
            // Mali durski/dominantni 9
            tempString = MyApplication.getChordNameAsString(MyApplication.MD9_ENG_TEXT, MyApplication.MD9_ENG_ONE, MyApplication.MD9_ENG_TWO);
        } else {
            tempString = MyApplication.getChordNameAsString(tempChord.getName(), tempChord.getNumberOneAsString(), tempChord.getNumberTwoAsString());
        }

        return MyApplication.doesStringContainSubstring(tempString, searchTextInput.getText().toString());
    }

    private boolean isToneSearchedFor(int toneID) {
        return MyApplication.doesStringContainSubstring(MyApplication.getKeyName(toneID), searchTextInput.getText().toString());
    }

    // Play next interval, chord or tone
    private void playNextThing(int numberOfRecursiveRuns) {
        if(QuizData.waitingForQuizAnswer || numberOfRecursiveRuns > 5) {
            return;
        }

        if(checkedIntervals + checkedChords <= 1 && !DatabaseData.playWhatTone && !DatabaseData.playWhatOctave) {
            Toast.makeText(MyApplication.getActivity(), readResource(R.string.not_enough_selected_intervals_or_chords_error), Toast.LENGTH_SHORT).show();
            pauseQuiz();
            return;
        }

        if(DatabaseData.directionsCount <= 0 && !DatabaseData.playWhatTone && !DatabaseData.playWhatOctave) {
            Toast.makeText(MyApplication.getActivity(), readResource(R.string.no_checked_playing_type_error), Toast.LENGTH_SHORT).show();
            pauseQuiz();
            return;
        }

        if(DatabaseData.upKeyBorder - DatabaseData.downKeyBorder <= 12 && DatabaseData.playWhatOctave && !DatabaseData.playWhatTone) {
            Toast.makeText(MyApplication.getActivity(), readResource(R.string.key_borders_are_too_small), Toast.LENGTH_SHORT).show();
            pauseQuiz();
            return;
        }

        // Reset this
        QuizData.quizModeThreeCorrectID = null;
        QuizData.quizModeThreeSelectedID = null;

        QuizData.quizModeThreeShowSubmitButton = false;
        updateSubmitAnswerButton();

        // Try to remove cursor and keyboard
        searchTextInput.setText("");
        searchTextInput.clearFocus();
        MyApplication.hideKeyboardFromActivity(ModeThreeActivity.this);

        QuizData.quizIntervalToPlay = null;
        QuizData.quizChordToPlay = null;


        // 12 tones in one octave
        int tempRandNumb = rand.nextInt(checkedIntervals + checkedChords + ((DatabaseData.playWhatTone || DatabaseData.playWhatOctave) ? 12 : 0));

        if(DatabaseData.directionsCount <= 0) {
            // If there is no direction to play, play tone
            tempRandNumb = rand.nextInt(12) + checkedIntervals + checkedChords;
        }

        if(tempRandNumb < checkedIntervals) { // Play interval
            // Don't play čista prima if tone can be played
            QuizData.quizIntervalToPlay = IntervalsList.getRandomCheckedInterval((DatabaseData.playWhatTone || DatabaseData.playWhatOctave) ? IntervalsList.getInterval(0) : null);
            if(QuizData.quizIntervalToPlay == null) {
                // Something went wrong, try again
                playNextThing(numberOfRecursiveRuns+1);
                return;
            }
            QuizData.quizChordToPlay = null;

            QuizData.quizModeThreeCorrectID = QuizData.quizModeThreeIntervalIDAdd + QuizData.quizIntervalToPlay.getDifference();


        } else if(tempRandNumb < checkedIntervals+checkedChords) { // Play chord
            QuizData.quizChordToPlay = ChordsList.getRandomCheckedChord();
            if(QuizData.quizChordToPlay == null) {
                // Something went wrong, try again
                playNextThing(numberOfRecursiveRuns+1);
                return;
            }
            QuizData.quizIntervalToPlay = null;

            QuizData.quizModeThreeCorrectID = QuizData.quizModeThreeChordIDAdd + QuizData.quizChordToPlay.getID();


        } else if(DatabaseData.playWhatTone || DatabaseData.playWhatOctave) {
            QuizData.quizIntervalToPlay = null;
            QuizData.quizChordToPlay = null;

            // Get random low key for playing (inside borders)
            QuizData.quizLowestKey = getRandomKey();

            QuizData.quizModeThreeCorrectID = QuizData.quizModeThreeToneIDAdd + QuizData.quizLowestKey;

        } else {
            // Something went wrong, try again
            playNextThing(numberOfRecursiveRuns+1);
            return;
        }

        if(QuizData.quizIntervalToPlay != null || QuizData.quizChordToPlay != null) {
            // Get random low key for playing (inside borders)
            QuizData.quizLowestKey = getRandomKey();
        }


        QuizData.waitingForQuizAnswer = true;



        refreshListView();

        playCurrentThing();

    }

    private void playCurrentThing() {
        QuizData.quizPlayingCurrentThing = true;

        quizModeOnePlayThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int playingID = ++QuizData.quizPlayingID;
                Integer directionToPlay = null;

                int numberOfTones = 1;
                if(QuizData.quizIntervalToPlay != null) {
                    numberOfTones = 2;
                } else if(QuizData.quizChordToPlay != null) {
                    numberOfTones = QuizData.quizChordToPlay.getToneNumber();
                }
                double playingDuration = DatabaseData.tonesSeparationTime * numberOfTones + DatabaseData.delayBetweenChords + addMS;

                if(DatabaseData.playingMode == DataContract.UserPrefEntry.PLAYING_MODE_CUSTOM) {
                    if(DatabaseData.directionsCount > 0) {
                        playingDuration *= DatabaseData.directionsCount;
                    }
                }

                updateProgressBarAnimation((long)playingDuration);


                // Logic copy-pasted (and changed) from ServicePlayer.Play()
                if(DatabaseData.playingMode == DataContract.UserPrefEntry.PLAYING_MODE_CUSTOM) {
                    // Loop through all possibilities
                    for(int i = Math.min(Math.min(MyApplication.directionUpID, MyApplication.directionDownID), MyApplication.directionSameID); i <=
                            Math.max(Math.max(MyApplication.directionUpID, MyApplication.directionDownID), MyApplication.directionSameID); i++) {
                        directionToPlay = null;
                        if(DatabaseData.directionUpViewIndex == i && DatabaseData.directionUp) {
                            directionToPlay = MyApplication.directionUpID;
                        } else if(DatabaseData.directionDownViewIndex == i && DatabaseData.directionDown) {
                            directionToPlay = MyApplication.directionDownID;
                        } else if(DatabaseData.directionSameTimeViewIndex == i && DatabaseData.directionSameTime) {
                            directionToPlay = MyApplication.directionSameID;
                        }

                        if(directionToPlay == null) {
                            continue;
                        }

                        if(QuizData.quizPlayingPaused) {
                            // If playing was paused, stop playing
                            return;
                        }

                        if(!(MyApplication.getActivity() instanceof ModeThreeActivity)) {
                            // If activity was exited, stop playing
                            QuizData.quizPlayingCurrentThing = false;
                            return;
                        }

                        justPlayThis(directionToPlay, playingID);
                        if(QuizData.quizIntervalToPlay == null && QuizData.quizChordToPlay == null) {
                            // If tone is playing break the loop
                            break;
                        }
                    }

                    if(DatabaseData.directionsCount <= 0) {
                        // If there is no direction to play, try to play an key
                        justPlayThis(null, playingID);
                    }
                } else if(DatabaseData.playingMode == DataContract.UserPrefEntry.PLAYING_MODE_RANDOM) {
                    int randomNumb = rand.nextInt(DatabaseData.directionsCount);
                    int counter = 0;
                    // Loop through IDs (and numbers in between) until you come to Id that is in place of randomNumb (in order)
                    for(int i = Math.min(Math.min(MyApplication.directionUpID, MyApplication.directionDownID), MyApplication.directionSameID); i <=
                            Math.max(Math.max(MyApplication.directionUpID, MyApplication.directionDownID), MyApplication.directionSameID); i++) {
                        if((DatabaseData.directionUp && MyApplication.directionUpID == i) ||
                                (DatabaseData.directionDown && MyApplication.directionDownID == i) ||
                                (DatabaseData.directionSameTime && MyApplication.directionSameID == i)) {
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
                        QuizData.quizPlayingCurrentThing = false;
                        return;
                    }

                    justPlayThis(directionToPlay, playingID);
                }

                if(!QuizData.quizPlayingPaused && playingID == QuizData.quizPlayingID) {
                    // If playing is not paused, it's finished
                    QuizData.quizPlayingCurrentThing = false;
                }
            }
        });
        quizModeOnePlayThread.start();

    }

    private void justPlayThis(Integer directionToPlay, int playingID) {
        try {
            Thread.sleep(10);
        } catch (Exception e) {}

        if(playingID != QuizData.quizPlayingID) {
            return;
        }

        if(QuizData.quizIntervalToPlay != null && directionToPlay != null) {
            MyApplication.playChord(new Interval[]{QuizData.quizIntervalToPlay}, QuizData.quizLowestKey, directionToPlay);

            try {
                Thread.sleep((long) DatabaseData.tonesSeparationTime * 2 + (long) DatabaseData.delayBetweenChords + addMS);
            } catch (Exception e) {}
        } else if(QuizData.quizChordToPlay != null && directionToPlay != null) {
            MyApplication.playChord(QuizData.quizChordToPlay.getAllIntervals(), QuizData.quizLowestKey, directionToPlay);

            try {
                Thread.sleep((long) DatabaseData.tonesSeparationTime * (QuizData.quizChordToPlay.getToneNumber()) + (long) DatabaseData.delayBetweenChords + addMS);
            } catch (Exception e) {}
        } else if(DatabaseData.playWhatTone || DatabaseData.playWhatOctave) {
            MyApplication.playKey(QuizData.quizLowestKey);

            try {
                Thread.sleep((long) DatabaseData.tonesSeparationTime + (long) DatabaseData.delayBetweenChords + addMS);
            } catch (Exception e) {}
        }
    }

    // TODO: random key uses key borders, it is not checking if key sound is loaded, check if key sound is loaded
    private int getRandomKey() {
        if(QuizData.quizIntervalToPlay != null) {
            return rand.nextInt(DatabaseData.upKeyBorder- DatabaseData.downKeyBorder- QuizData.quizIntervalToPlay.getDifference())+ DatabaseData.downKeyBorder;
        }

        if(QuizData.quizChordToPlay != null) {
            return rand.nextInt(DatabaseData.upKeyBorder- DatabaseData.downKeyBorder- QuizData.quizChordToPlay.getDifference())+ DatabaseData.downKeyBorder;
        }

        return rand.nextInt(DatabaseData.upKeyBorder- DatabaseData.downKeyBorder)+ DatabaseData.downKeyBorder;
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
        QuizData.refreshQuizModeThreeHighScore();
        QuizData.quizScore = 0;


        showGameOverDialog();
    }


    private void updateProgressBarAnimation(long duration) {
        try {
            if(!DatabaseData.showProgressBar && MyApplication.isLoadingFinished) {
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
        if(QuizData.quizPlayingCurrentThing && !QuizData.quizPlayingPaused) {
            timeLeftToPlayProgressBar.setVisibility(View.INVISIBLE);
        }

        updateSubmitAnswerButton();

    }

    @Override
    protected void onPause() {
        super.onPause();

        MyApplication.activityPaused();

        // Save high score if greater than current
        QuizData.refreshQuizModeThreeHighScore();
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
                QuizData.quizPlayingCurrentThing = false;
                stopPlaying();

                finish();
                return true;
            case R.id.action_options:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateSubmitAnswerButton() {
        if(QuizData.quizModeThreeShowSubmitButton) {
            submitAnswerButton.setVisibility(View.VISIBLE);
        } else {
            submitAnswerButton.setVisibility(View.GONE);
        }
    }

    private void stopPlaying() {
        QuizData.quizPlayingID += 10;
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


        QuizData.quizPlayingPaused = true;
    }

    private void resumeQuiz() {
        startClickableImageView.setVisibility(View.GONE);
        pauseClickableImageView.setVisibility(View.VISIBLE);

        parentSearchAndListView.setVisibility(View.VISIBLE);
        updateSubmitAnswerButton();


        QuizData.quizPlayingPaused = false;
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

                QuizData.quizModeThreeShowSubmitButton = false;

                // Reset everything
                QuizData.quizPlayingPaused = true;
                QuizData.waitingForQuizAnswer = false;


                scoreTextView.setText(String.valueOf(QuizData.quizScore));

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
