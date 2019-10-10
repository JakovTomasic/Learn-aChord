package com.justchill.android.learnachord.quiz;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import com.justchill.android.learnachord.firebase.AchievementChecker;
import com.justchill.android.learnachord.intervalOrChord.Chord;
import com.justchill.android.learnachord.intervalOrChord.ChordsList;
import com.justchill.android.learnachord.intervalOrChord.Interval;
import com.justchill.android.learnachord.intervalOrChord.IntervalsList;
import com.justchill.android.learnachord.database.DataContract;
import com.justchill.android.learnachord.database.DatabaseData;

import java.util.ArrayList;
import java.util.Random;

// Hard quiz activity
public class ModeThreeActivity extends AppCompatActivity {

    // Playing progress bar
    private ProgressBar timeLeftToPlayProgressBar;

    // TextView that is showing score
    private TextView scoreTextView;

    // Start/resume quiz button
    private ImageView startClickableImageView;
    // Pause quiz button
    private ImageView pauseClickableImageView;

    // Parent layout of ListView and search bar
    private View parentSearchAndListView;
    // Search EditText
    private EditText searchTextInput;
    // List of all possible answers
    private ListView selectAnswerListView;

    // Submit button view
    private View submitAnswerButton;

    // Achievement icon to show on milestone reach
    private View achievementIconView;


    // Temporary random object
    private Random rand;
    // Number of intervals and chords checked/chosen/enabled in options
    int checkedIntervals, checkedChords;

    // Thread for playing sounds
    Thread quizModeOnePlayThread;

    // Just a little delay between playing
    private static final long addMS = 100;


    // Thickness of playing progress bar in dp
    private static final int timeLeftToPlayProgressThicknessDP = 4;

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

        achievementIconView = findViewById(R.id.achievement_icon_image_view);


        setLayoutSizes();

        scoreTextView.setText(String.valueOf(QuizData.quizScore));


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




        // Set what is visible and what not
        if(QuizData.quizPlayingPaused) {
            pauseQuiz();
        } else {
            resumeQuiz();
        }

        // For app loading on startup
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

        // For app loading on startup
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
                    AchievementChecker.checkAchievements(QuizData.quizScore);
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

        // Show all possible answers in list view (having search in mind)
        refreshListView();

    }

    // Set all layout sizes that depends on screen size (for foldable phone support)
    private void setLayoutSizes() {
        // Setup score text view text size
        scoreTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float)(MyApplication.smallerDisplayDimensionPX / 16) * DatabaseData.scaledDensity);

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


        // Setup achievement milestone popup size
        achievementIconView.getLayoutParams().height = MyApplication.smallerDisplayDimensionPX / 8;
        achievementIconView.getLayoutParams().width = MyApplication.smallerDisplayDimensionPX / 8;


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


        // Setup pause button size
        ViewGroup.LayoutParams pauseImageViewSizeRules = pauseClickableImageView.getLayoutParams();

        // Logic copied from MainActivity
        height_width_value = (int)(MyApplication.smallerDisplayDimensionPX * 0.75 / 5.3125);
        pauseImageViewSizeRules.width = height_width_value;
        pauseImageViewSizeRules.height = height_width_value;
        pauseClickableImageView.setLayoutParams(pauseImageViewSizeRules);
    }

    // Show all possible answers in list that match search text
    private void refreshListView() {
        QuizData.quizModeThreeListOfPossibleAnswerIDs = new ArrayList<>();


        // Show perfect unison only if tones are not played
        if(!DatabaseData.playWhatTone && !DatabaseData.playWhatOctave && IntervalsList.getInterval(0).getIsChecked() && isIntervalSearchedFor(0)) {
            QuizData.quizModeThreeListOfPossibleAnswerIDs.add(QuizData.quizModeThreeIntervalIDAdd);
        }

        // Add intervals to list
        for(int i = 1; i < IntervalsList.getIntervalsCount(); i++) {
            // Add every checked intervals to list (it's name; if it has been searched for)
            if(IntervalsList.getInterval(i).getIsChecked() && isIntervalSearchedFor(i)) {
                QuizData.quizModeThreeListOfPossibleAnswerIDs.add(QuizData.quizModeThreeIntervalIDAdd + i);
            }
        }

        // Add chords to list
        for(int i = 0; i < ChordsList.getChordsCount(); i++) {
            // Add every checked chord to list (it's name; if it has been searched for)
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

    // Check if interval match search text (is search text substring of that interval's name)
    private boolean isIntervalSearchedFor(int intervalID) {
        return MyApplication.doesStringContainSubstring(
                MyApplication.getChordNameAsString(IntervalsList.getInterval(intervalID).getName(), null, null),
                searchTextInput.getText().toString());
    }

    // Check if chord match search text (is search text substring of that chord's name)
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

    // Check if tone match search text (is search text substring of that tone's name)
    private boolean isToneSearchedFor(int toneID) {
        return MyApplication.doesStringContainSubstring(MyApplication.getKeyName(toneID), searchTextInput.getText().toString());
    }

    // Play next interval, chord or tone
    private void playNextThing(int numberOfRecursiveRuns) {
        if(QuizData.waitingForQuizAnswer || numberOfRecursiveRuns > 10) {
            // If app is waiting for an answer (user input) or this has been tried too many times, don't play next thing
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

        // Reset temporary variables
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

            // Get random low key for playing (inside borders and with loaded sound)
            QuizData.quizLowestKey = QuizData.getRandomKey(rand);

            QuizData.quizModeThreeCorrectID = QuizData.quizModeThreeToneIDAdd + QuizData.quizLowestKey;

        } else {
            // Something went wrong, try again
            playNextThing(numberOfRecursiveRuns+1);
            return;
        }

        if(QuizData.quizIntervalToPlay != null || QuizData.quizChordToPlay != null) {
            // Get random low key for playing (inside borders and with loaded sound)
            QuizData.quizLowestKey = QuizData.getRandomKey(rand);
        }


        QuizData.waitingForQuizAnswer = true;



        // Show all possible answers in list view (having search in mind)
        refreshListView();

        // Play chosen thing
        playCurrentThing();

    }

    // Play thing that has been set to be played (interval, chord or tone) on separate thread
    private void playCurrentThing() {
        // Set this so app knows sound in quiz is reproducing (playing)
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

    // Play current interval/chord/tone in given direction
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


    // Called when user chooses false answer
    private void gameOver() {
        // Save high score if greater than current
        QuizData.refreshQuizModeThreeHighScore();

        // Show dialog and then reset score
        showGameOverDialog(QuizData.quizScore);
        QuizData.quizScore = 0;
    }


    // Start playing progress bar animation
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

    // Read string form resources
    private String readResource(int id) {
        return ModeThreeActivity.this.getResources().getString(id);
    }

    // For different languages support
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, null));
    }

    @Override
    protected void onResume() {
        super.onResume();

        MyApplication.activityResumed(ModeThreeActivity.this);

        AchievementChecker.lastPlayedQuizMode = AchievementChecker.QUIZ_MODE_THREE_ID;


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

    // Set visibility of submit button
    private void updateSubmitAnswerButton() {
        if(QuizData.quizModeThreeShowSubmitButton) {
            submitAnswerButton.setVisibility(View.VISIBLE);
        } else {
            submitAnswerButton.setVisibility(View.GONE);
        }
    }

    // Stop all sounds from playing
    public void stopPlaying() {
        QuizData.quizPlayingID += 10;
        if(quizModeOnePlayThread != null) {
            quizModeOnePlayThread.interrupt();
            quizModeOnePlayThread = null;
        }
        MyApplication.setIsPlaying(false);

        // Set progress bar to 100 immediately
        updateProgressBarAnimation(0);
    }

    // Set UI for when quiz is paused
    public void pauseQuiz() {
        startClickableImageView.setVisibility(View.VISIBLE);
        pauseClickableImageView.setVisibility(View.GONE);

        parentSearchAndListView.setVisibility(View.GONE);
        submitAnswerButton.setVisibility(View.GONE);


        QuizData.quizPlayingPaused = true;
    }

    // Set UI for when quiz is resumed (playing)
    private void resumeQuiz() {
        startClickableImageView.setVisibility(View.GONE);
        pauseClickableImageView.setVisibility(View.VISIBLE);

        parentSearchAndListView.setVisibility(View.VISIBLE);
        updateSubmitAnswerButton();


        QuizData.quizPlayingPaused = false;
    }


    // Dialog shows when user chooses a wrong answer
    private void showGameOverDialog(int score) {
        // Create an AlertDialog.Builder and set the message, and click listeners for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set view of a dialog that displays score and the correct answer
        builder.setView(MyApplication.getQuizGameOverDialogLayour(ModeThreeActivity.this, score));

        // Set title of a dialog
        builder.setTitle(R.string.quiz_game_over_message);

        // Set buttons and their onClick listeners
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

                // Reset score TextView
                scoreTextView.setText(String.valueOf(QuizData.quizScore));

                // User clicked the "try again" button, so dismiss the dialog and stay in quiz.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();

        // Dialog can't be closed on back button click or on outside click
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);

        alertDialog.show();
    }

    // Called when screen size is changed (phone unfolded)
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        MyApplication.updateSmallerDisplayDimensionPX(this);
        setLayoutSizes();
    }


}
