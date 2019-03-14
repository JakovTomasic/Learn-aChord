package com.justchill.android.learnachord.quiz;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import java.util.Random;

// Easy quiz activity
public class ModeOneActivity extends AppCompatActivity {

    // Playing progress bar
    private ProgressBar timeLeftToPlayProgressBar;

    // TextView that is showing score
    private TextView scoreTextView;

    // Parent layout for showing interval/chord/tone
    private ViewGroup chordTextViewLayout;
    // Values to show
    private TextView chordTextView;
    private TextView chordNumOneTextView;
    private TextView chordNumTwoTextView;

    // Start/resume quiz button
    private ImageView startClickableImageView;
    // Pause quiz button
    private ImageView pauseClickableImageView;

    // Correct/true and wrong/false answer button
    private View trueAnswer, falseAnswer;

    // Achievement icon to show on milestone reach
    private View achievementIconView;


    // Temporary random object
    private Random rand;
    // Number of intervals and chords checked/chosen/enabled in options
    private int checkedIntervals, checkedChords;

    // Thread for playing sounds
    Thread quizModeOnePlayThread;

    // Just a little delay between playing
    private static final long addMS = 100;


    // Thickness of playing progress bar in dp
    private static final int timeLeftToPlayProgressThicknessDP = 4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_quiz_mode_one);
        setTitle(readResource(R.string.quiz) + readResource(R.string.quiz_mode_title_separator) + readResource(R.string.quiz_mode_one_title));

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

        chordTextViewLayout = findViewById(R.id.chord_text_view_linear_layout);
        chordTextView = findViewById(R.id.chord_text_view);
        chordNumOneTextView = findViewById(R.id.chord_number_one);
        chordNumTwoTextView = findViewById(R.id.chord_number_two);

        startClickableImageView = findViewById(R.id.start_clickable_image_view);
        pauseClickableImageView = findViewById(R.id.pause_clickable_image_view);

        trueAnswer = findViewById(R.id.true_answer_parent_layout);
        falseAnswer = findViewById(R.id.false_answer_parent_layout);

        achievementIconView = findViewById(R.id.achievement_icon_image_view);


        // Setup interval and chord text size
        MyApplication.setupIntervalAndChordTextSize(chordTextView, chordNumOneTextView, chordNumTwoTextView, 1);

        // Setup score text view text size
        scoreTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float)(MyApplication.smallerDisplayDimensionPX / 16) * DatabaseData.scaledDensity);
        scoreTextView.setText(String.valueOf(QuizData.quizScore));

        // Setup achievement milestone popup size
        achievementIconView.getLayoutParams().height = MyApplication.smallerDisplayDimensionPX / 8;
        achievementIconView.getLayoutParams().width = MyApplication.smallerDisplayDimensionPX / 8;

        // Setup progress bar size
        ViewGroup.LayoutParams progressBarSizeRules = timeLeftToPlayProgressBar.getLayoutParams();
        progressBarSizeRules.width = MyApplication.smallerDisplayDimensionPX / 8;
        progressBarSizeRules.height = MyApplication.smallerDisplayDimensionPX / 8;
        timeLeftToPlayProgressBar.setLayoutParams(progressBarSizeRules);


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


        // Setup true answer button size
        ViewGroup.LayoutParams trueAnswerViewSizeRules = trueAnswer.getLayoutParams();

        trueAnswerViewSizeRules.width = MyApplication.smallerDisplayDimensionPX / 6;
        trueAnswerViewSizeRules.height = MyApplication.smallerDisplayDimensionPX / 6;
        trueAnswer.setLayoutParams(trueAnswerViewSizeRules);


        // Setup true answer button size
        ViewGroup.LayoutParams falseAnswerViewSizeRules = falseAnswer.getLayoutParams();

        falseAnswerViewSizeRules.width = MyApplication.smallerDisplayDimensionPX / 6;
        falseAnswerViewSizeRules.height = MyApplication.smallerDisplayDimensionPX / 6;
        falseAnswer.setLayoutParams(falseAnswerViewSizeRules);


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
            MyApplication.setupPlayButtonColor(ModeOneActivity.this, startClickableImageView, R.color.unloadedColor);
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
                MyApplication.setupPlayButtonColor(ModeOneActivity.this, startClickableImageView, R.color.playButton);
            }
        });




        // Setup true and false buttons listeners
        trueAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuizData.waitingForQuizAnswer = false;
                QuizData.quizPlayingCurrentThing = false;
                stopPlaying();

                if(QuizData.quizModeOneCorrectAnswer) {
                    scoreTextView.setText(String.valueOf(++QuizData.quizScore));
                    playNextThing(0);
                    AchievementChecker.checkAchievements(QuizData.quizScore);
                } else {
                    gameOver();
                }
            }
        });

        falseAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuizData.waitingForQuizAnswer = false;
                QuizData.quizPlayingCurrentThing = false;
                stopPlaying();

                if(!QuizData.quizModeOneCorrectAnswer) {
                    scoreTextView.setText(String.valueOf(++QuizData.quizScore));
                    playNextThing(0);
                    AchievementChecker.checkAchievements(QuizData.quizScore);
                } else {
                    gameOver();
                }


            }
        });


        QuizData.isQuizModePlaying = true;

        // Show interval/chord/tone that needs to be shown
        showChord();

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
        QuizData.quizChordNameToShow = null;
        QuizData.quizChordNumberOneToShow = null;
        QuizData.quizChordNumberTwoToShow = null;

        QuizData.quizIntervalToPlay = null;
        QuizData.quizChordToPlay = null;

        // Randomly choose if correct answer will be true or false
        QuizData.quizModeOneCorrectAnswer = rand.nextBoolean();


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

            // If answer is correct show intervals that is playing
            if(QuizData.quizModeOneCorrectAnswer) {
                QuizData.quizChordNameToShow = QuizData.quizIntervalToPlay.getName();
            } else { // If not, show any other intervals, or chord if there is no intervals (or tone)
                setupWrongThingToShow();
            }

            playCurrentThing();

        } else if(tempRandNumb < checkedIntervals+checkedChords) { // Play chord
            QuizData.quizChordToPlay = ChordsList.getRandomCheckedChord();
            if(QuizData.quizChordToPlay == null) {
                // Something went wrong, try again
                playNextThing(numberOfRecursiveRuns+1);
                return;
            }
            QuizData.quizIntervalToPlay = null;

            // If answer is correct show chord that is playing
            if(QuizData.quizModeOneCorrectAnswer) {
                QuizData.quizChordNameToShow = QuizData.quizChordToPlay.getName();
                QuizData.quizChordNumberOneToShow = QuizData.quizChordToPlay.getNumberOneAsString();
                QuizData.quizChordNumberTwoToShow = QuizData.quizChordToPlay.getNumberTwoAsString();
            } else { // If not, show any other intervals, or chord if there is no intervals (or tone)
                setupWrongThingToShow();
            }

            playCurrentThing();

        } else if(DatabaseData.playWhatTone || DatabaseData.playWhatOctave) { // Play tone
            QuizData.quizIntervalToPlay = null;
            QuizData.quizChordToPlay = null;

            // Get random low key for playing (inside borders and with loaded sound)
            QuizData.quizLowestKey = QuizData.getRandomKey(rand);

            if(QuizData.quizModeOneCorrectAnswer) {
                QuizData.quizChordNameToShow = MyApplication.getKeyName(QuizData.quizLowestKey);
            } else {
                setupWrongThingToShow();


            }

            playCurrentThing();

        } else {
            // Something went wrong, try again
            playNextThing(numberOfRecursiveRuns+1);
            return;
        }

        if(QuizData.quizIntervalToPlay != null || QuizData.quizChordToPlay != null) {
            // Get random low key for playing (inside borders and with loaded sound)
            QuizData.quizLowestKey = QuizData.getRandomKey(rand);
        }


        showChord();

        QuizData.waitingForQuizAnswer = true;
    }

    // Play current interval/chord/tone on separate thread
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

                        if(!(MyApplication.getActivity() instanceof ModeOneActivity)) {
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
        } catch (Exception ignored) {}

        if(playingID != QuizData.quizPlayingID) {
            return;
        }

        if(QuizData.quizIntervalToPlay != null && directionToPlay != null) {
            MyApplication.playChord(new Interval[]{QuizData.quizIntervalToPlay}, QuizData.quizLowestKey, directionToPlay);

            try {
                Thread.sleep((long) DatabaseData.tonesSeparationTime * 2 + (long) DatabaseData.delayBetweenChords + addMS);
            } catch (Exception ignored) {}
        } else if(QuizData.quizChordToPlay != null && directionToPlay != null) {
            MyApplication.playChord(QuizData.quizChordToPlay.getAllIntervals(), QuizData.quizLowestKey, directionToPlay);

            try {
                Thread.sleep((long) DatabaseData.tonesSeparationTime * (QuizData.quizChordToPlay.getToneNumber()) + (long) DatabaseData.delayBetweenChords + addMS);
            } catch (Exception ignored) {}
        } else if(DatabaseData.playWhatTone || DatabaseData.playWhatOctave) {
            MyApplication.playKey(QuizData.quizLowestKey);

            try {
                Thread.sleep((long) DatabaseData.tonesSeparationTime + (long) DatabaseData.delayBetweenChords + addMS);
            } catch (Exception ignored) {}
        }
    }

    // Show interval/chord/tone that is playing or show false answer
    private void showChord() {
        if(QuizData.quizChordNameToShow == null) {
            // If chord name that needs to be shown is null, show nothing (empty string)
            MyApplication.updateTextView(chordTextView, "", chordNumOneTextView, "", chordNumTwoTextView, "");
            return;
        }
        MyApplication.updateTextView(chordTextView, QuizData.quizChordNameToShow, chordNumOneTextView, QuizData.quizChordNumberOneToShow, chordNumTwoTextView, QuizData.quizChordNumberTwoToShow);

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

    // Randomly set wrong answer to show
    private void setupWrongThingToShow() {
        if(QuizData.quizIntervalToPlay != null) {
            setupWrongInterval();
        }

        if(QuizData.quizChordToPlay != null) {
            setupWrongChord();
        }

        if(QuizData.quizIntervalToPlay == null && QuizData.quizChordToPlay == null && DatabaseData.playWhatTone/* && MyApplication.playWhatOctave*/) {
            setupWrongToneIfPlayWhatTone();
        }

        if(QuizData.quizIntervalToPlay == null && QuizData.quizChordToPlay == null && !DatabaseData.playWhatTone && DatabaseData.playWhatOctave) {
            setupWrongToneIfJustOctave();
        }

        if(QuizData.quizChordNameToShow == null) {
            // Try one without looking for what is playing
            setupWrongThingToShowSecondTry();
        }

        if(QuizData.quizChordNameToShow == null) {
            stopPlaying();
            playNextThing(0);
        }

    }

    // If first random setting was not successful, try one more time
    private void setupWrongThingToShowSecondTry() {
        // Show any checked interval, if there is any
        // If MyApplication.quizIntervalToPlay == null there will just be no exception
        setupWrongInterval();

        if(DatabaseData.playWhatTone || DatabaseData.playWhatOctave && IntervalsList.getInterval(0).getName().equals(QuizData.quizChordNameToShow)) {
            // If tones are playing and it shows čista prima, user cannot see the difference
            QuizData.quizChordNameToShow = null;
        }

        if(QuizData.quizChordNameToShow != null) {
            return;
        }

        // Show any checked chord, if there is any
        // If MyApplication.quizChordToPlay == null there will just be no exception
        setupWrongChord();

        if(QuizData.quizChordNameToShow != null) {
            return;
        }

        if(!DatabaseData.playWhatTone && DatabaseData.playWhatOctave) {
            setupWrongToneIfJustOctave();
        }

        if(QuizData.quizChordNameToShow != null) {
            return;
        }

        if(DatabaseData.playWhatTone) {
            setupWrongToneIfPlayWhatTone();
        }


    }

    // Set random interval that is not playing (and is selected in options)
    private void setupWrongInterval() {
        try {
            QuizData.quizChordNameToShow = IntervalsList.getRandomCheckedInterval(QuizData.quizIntervalToPlay).getName();
        } catch (Exception e) {
            QuizData.quizChordNameToShow = null;
        }
    }

    // Set random chord that is not playing (and is selected in options)
    private void setupWrongChord() {
        try {
            Chord tempChord = ChordsList.getRandomCheckedChord(QuizData.quizChordToPlay);
            QuizData.quizChordNameToShow = tempChord.getName();
            QuizData.quizChordNumberOneToShow = tempChord.getNumberOneAsString();
            QuizData.quizChordNumberTwoToShow = tempChord.getNumberTwoAsString();
        } catch (Exception e) {
            QuizData.quizChordNameToShow = null;
        }
    }

    // Set random tone that is not playing (and is selected in options) - when only tone is selected in options
    private void setupWrongToneIfPlayWhatTone() {
        int tempRandomStartFrom = rand.nextInt(DatabaseData.upKeyBorder- DatabaseData.downKeyBorder)+ DatabaseData.downKeyBorder;

        for(int i = tempRandomStartFrom; i < DatabaseData.upKeyBorder; i++) {
            // If answer is not true, show that key
            if(!((DatabaseData.playWhatOctave && keysInSameOctave(QuizData.quizLowestKey, i)) || (DatabaseData.playWhatTone && keysAreSameTone(QuizData.quizLowestKey, i)))) {
                QuizData.quizChordNameToShow = MyApplication.getKeyName(i);
                break;
            }
        }

        if(QuizData.quizChordNameToShow == null) {
            // If key was not found, try going down instead of up
            for(int i = tempRandomStartFrom-1; i > DatabaseData.downKeyBorder; i--) {
                // If answer is not true, show that key
                if(!((DatabaseData.playWhatOctave && keysInSameOctave(QuizData.quizLowestKey, i)) || (DatabaseData.playWhatTone && keysAreSameTone(QuizData.quizLowestKey, i)))) {
                    QuizData.quizChordNameToShow = MyApplication.getKeyName(i);
                    break;
                }
            }
        }
    }

    // TODO: add min and max range support (user preference)
    // Set random tone that is not playing (and is selected in options) - when only octave is selected in options
    private void setupWrongToneIfJustOctave() {
        int tempOctaveNumb = (QuizData.quizLowestKey-1)/12;

        int tempKey = (QuizData.quizLowestKey-1)%12;

        int numberOfOctaves = DataContract.UserPrefEntry.NUMBER_OF_KEYS/12;
        // -1 so octave that is correct doesn't get shown
        int randomNumb = rand.nextInt(numberOfOctaves - 1);
        for(int i = 0; i < numberOfOctaves; i++) {
            if(i != tempOctaveNumb && i >= randomNumb) {
                // +1 to get back to normal naming scheme
                QuizData.quizChordNameToShow = MyApplication.getKeyName(tempKey + (12*i) + 1);
                break;
            }
        }
    }


    // Called when user chooses false answer
    private void gameOver() {
        // Save high score if greater than current
        QuizData.refreshQuizModeOneHighScore();

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
        return ModeOneActivity.this.getResources().getString(id);
    }

    // For different languages support
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, null));
    }

    @Override
    protected void onResume() {
        super.onResume();

        MyApplication.activityResumed(ModeOneActivity.this);

        AchievementChecker.lastPlayedQuizMode = AchievementChecker.QUIZ_MODE_ONE_ID;

        showChord();

        // If sound is being played, don't show progress bar (animation stops after screen rotation)
        if(QuizData.quizPlayingCurrentThing && !QuizData.quizPlayingPaused) {
            timeLeftToPlayProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        MyApplication.activityPaused();

        // Save high score if greater than current
        QuizData.refreshQuizModeOneHighScore();

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
        }
        return super.onOptionsItemSelected(item);
    }

    // Stop all sounds from playing
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

    // Set UI for when quiz is paused
    private void pauseQuiz() {
        startClickableImageView.setVisibility(View.VISIBLE);
        pauseClickableImageView.setVisibility(View.GONE);

        trueAnswer.setVisibility(View.GONE);
        falseAnswer.setVisibility(View.GONE);

        chordTextViewLayout.setVisibility(View.INVISIBLE);

        QuizData.quizPlayingPaused = true;
    }

    // Set UI for when quiz is resumed (playing)
    private void resumeQuiz() {
        startClickableImageView.setVisibility(View.GONE);
        pauseClickableImageView.setVisibility(View.VISIBLE);

        trueAnswer.setVisibility(View.VISIBLE);
        falseAnswer.setVisibility(View.VISIBLE);

        chordTextViewLayout.setVisibility(View.VISIBLE);

        QuizData.quizPlayingPaused = false;
    }

    // Dialog shows when user chooses a wrong answer
    private void showGameOverDialog(int score) {
        // Create an AlertDialog.Builder and set the message, and click listeners for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set view of a dialog that displays score and the correct answer
        builder.setView(MyApplication.getQuizGameOverDialogLayour(ModeOneActivity.this, score));

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

                // Reset everything
                QuizData.quizPlayingPaused = true;
                QuizData.quizChordNameToShow = "";
                QuizData.quizChordNumberOneToShow = "";
                QuizData.quizChordNumberTwoToShow = "";
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



}
