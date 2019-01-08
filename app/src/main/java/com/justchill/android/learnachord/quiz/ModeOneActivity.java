package com.justchill.android.learnachord.quiz;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.justchill.android.learnachord.chord.Chord;
import com.justchill.android.learnachord.chord.ChordsList;
import com.justchill.android.learnachord.chord.Interval;
import com.justchill.android.learnachord.chord.IntervalsList;
import com.justchill.android.learnachord.database.DataContract;

import java.util.Random;

public class ModeOneActivity extends AppCompatActivity {

    private ProgressBar timeLeftToPlayProgressBar;

    private TextView scoreTextView;

    private ViewGroup chordTextViewLayout;
    private TextView chordTextView;
    private TextView chordNumOneTextView;
    private TextView chordNumTwoTextView;

    private ImageView startClickableImageView;
    private ImageView pauseClickableImageView;

    private View trueAnswer, falseAnswer;


    private Random rand;
    private int checkedIntervals, checkedChords;

    Thread quizModeOnePlayThread;

    // Just a little delay between playing
    private final long addMS = 100;


    private final int timeLeftToPlayProgressThicknessDB = 4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_quiz_mode_one);
        setTitle(readResource(R.string.quiz) + readResource(R.string.quiz_mode_title_separator) + readResource(R.string.quiz_mode_one_title));

        rand = new Random();
        checkedIntervals = IntervalsList.getCheckedIntervalCount();
        checkedChords = ChordsList.getCheckedChordsCount();
        if((MyApplication.playWhatTone || MyApplication.playWhatOctave) && IntervalsList.getInterval(0).getIsChecked()) {
            // If tones can be played, don't count on čista prima
            checkedIntervals--;
        }

        timeLeftToPlayProgressBar = findViewById(R.id.ring_playing_progress_bar);

        scoreTextView = findViewById(R.id.quiz_score_text_view);

        chordTextViewLayout = (ViewGroup) findViewById(R.id.chord_text_view_linear_layout);
        chordTextView = (TextView) findViewById(R.id.chord_text_view);
        chordNumOneTextView = (TextView) findViewById(R.id.chord_number_one);
        chordNumTwoTextView = (TextView) findViewById(R.id.chord_number_two);

        startClickableImageView = (ImageView) findViewById(R.id.start_clickable_image_view);
        pauseClickableImageView = (ImageView) findViewById(R.id.pause_clickable_image_view);

        trueAnswer = findViewById(R.id.true_answer_parent_layout);
        falseAnswer = findViewById(R.id.false_answer_parent_layout);

        // Setup interval and chord text size
        MyApplication.setupIntervalAndChordTextSize(chordTextView, chordNumOneTextView, chordNumTwoTextView);

        // Setup score text view text size
        scoreTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float)(MyApplication.smallerDisplayDimensionPX / 16) * MyApplication.scaledDensity);
        scoreTextView.setText(String.valueOf(MyApplication.quizScore));

        // Setup progress bar size
        ViewGroup.LayoutParams progressBarSizeRules = timeLeftToPlayProgressBar.getLayoutParams();
        progressBarSizeRules.width = MyApplication.smallerDisplayDimensionPX / 8;
        progressBarSizeRules.height = MyApplication.smallerDisplayDimensionPX / 8;
        timeLeftToPlayProgressBar.setLayoutParams(progressBarSizeRules);

//        timeLeftToPlayProgressBar.setProgress(100);



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

//        chordTextView.setText("");
//        chordNumOneTextView.setText("");
//        chordNumTwoTextView.setText("");

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
            MyApplication.setupPlayButtonColor(ModeOneActivity.this, startClickableImageView, R.color.unloadedColor);
        }

        MyApplication.addMainActivityListener(new MyApplication.MainActivityListener() {
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
                MyApplication.waitingForQuizAnswer = false;
                MyApplication.quizPlayingCurrentThing = false;
                stopPlaying();

                if(MyApplication.quizModeOneCorrectAnswer) {
                    scoreTextView.setText(String.valueOf(++MyApplication.quizScore));
                    playNextThing(0);
                } else {
                    gameOver();
                }
            }
        });

        falseAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.waitingForQuizAnswer = false;
                MyApplication.quizPlayingCurrentThing = false;
                stopPlaying();

                if(!MyApplication.quizModeOneCorrectAnswer) {
                    scoreTextView.setText(String.valueOf(++MyApplication.quizScore));
                    playNextThing(0);
                } else {
                    gameOver();
                }


            }
        });


        MyApplication.isQuizModePlaying = true;

        showChord();

    }

    // Play next interval, chord or tone
    private void playNextThing(int numberOfRecursiveRuns) {
        if(MyApplication.waitingForQuizAnswer || numberOfRecursiveRuns > 5) {
            return;
        }

        if(checkedIntervals + checkedChords <= 0 && !MyApplication.playWhatTone && !MyApplication.playWhatOctave) {
            Toast.makeText(MyApplication.getActivity(), readResource(R.string.no_checked_intervals_error), Toast.LENGTH_SHORT).show();
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
        MyApplication.quizChordNameToShow = null;
        MyApplication.quizChordNumberOneToShow = null;
        MyApplication.quizChordNumberTwoToShow = null;

        MyApplication.quizIntervalToPlay = null;
        MyApplication.quizChordToPlay = null;

        // Randomly choose if correct answer will be true or false
        MyApplication.quizModeOneCorrectAnswer = rand.nextBoolean();

        // Get random low key for playing (inside borders)
        MyApplication.quizLowestKey = getRandomKey();

        // 12 tones in one octave
        int tempRandNumb = rand.nextInt(checkedIntervals + checkedChords + ((MyApplication.playWhatTone || MyApplication.playWhatOctave) ? 13 : 0));

        if(MyApplication.directionsCount <= 0) {
            // If there is no direction to play, play tone
            tempRandNumb = rand.nextInt(13) + checkedIntervals + checkedChords;
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

            // If answer is correct show intervals that is playing
            if(MyApplication.quizModeOneCorrectAnswer) {
                MyApplication.quizChordNameToShow = MyApplication.quizIntervalToPlay.getIntervalName();
            } else { // If not, show any other intervals, or chord if there is no intervals (or tone)
                setupWrongThingToShow();
            }

            playCurrentThing();

        } else if(tempRandNumb < checkedIntervals+checkedChords) { // Play chord
            MyApplication.quizChordToPlay = ChordsList.getRandomCheckedChord(null);
            if(MyApplication.quizChordToPlay == null) {
                // Something went wrong, try again
                playNextThing(numberOfRecursiveRuns+1);
                return;
            }
            MyApplication.quizIntervalToPlay = null;

            // If answer is correct show chord that is playing
            if(MyApplication.quizModeOneCorrectAnswer) {
                MyApplication.quizChordNameToShow = MyApplication.quizChordToPlay.getChordName();
                MyApplication.quizChordNumberOneToShow = MyApplication.quizChordToPlay.getNumberOneAsString();
                MyApplication.quizChordNumberTwoToShow = MyApplication.quizChordToPlay.getNumberTwoAsString();
            } else { // If not, show any other intervals, or chord if there is no intervals (or tone)
                setupWrongThingToShow();
            }

            playCurrentThing();

        } else if(MyApplication.playWhatTone || MyApplication.playWhatOctave) {
            MyApplication.quizIntervalToPlay = null;
            MyApplication.quizChordToPlay = null;

            if(MyApplication.quizModeOneCorrectAnswer) {
                MyApplication.quizChordNameToShow = MyApplication.getKeyName(MyApplication.quizLowestKey);
            } else {
                setupWrongThingToShow();


            }

            playCurrentThing();

        } else {
            // Something went wrong, try again
            playNextThing(numberOfRecursiveRuns+1);
            return;
        }

        showChord();

        MyApplication.waitingForQuizAnswer = true;
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

                        if(!(MyApplication.getActivity() instanceof ModeOneActivity)) {
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

    private void showChord() {
        if(MyApplication.quizChordNameToShow == null) {
            // If chord name that needs to be shown is null, show nothing (empty string)
            MyApplication.updateTextView(chordTextView, "", chordNumOneTextView, "", chordNumTwoTextView, "");
            return;
        }
        MyApplication.updateTextView(chordTextView, MyApplication.quizChordNameToShow, chordNumOneTextView, MyApplication.quizChordNumberOneToShow, chordNumTwoTextView, MyApplication.quizChordNumberTwoToShow);

    }

    // TODO: random key uses key borders, it is not checking if key sound is loaded, check if key sound is loaded
    private int getRandomKey() {
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

    private void setupWrongThingToShow() {
        if(MyApplication.quizIntervalToPlay != null) {
            try {
                MyApplication.quizChordNameToShow = IntervalsList.getRandomCheckedInterval(MyApplication.quizIntervalToPlay).getIntervalName();
            } catch (Exception e) {
                MyApplication.quizChordNameToShow = null;
            }
        }

        if(MyApplication.quizChordToPlay != null) {
            try {
                Chord tempChord = ChordsList.getRandomCheckedChord(MyApplication.quizChordToPlay);
                MyApplication.quizChordNameToShow = tempChord.getChordName();
                MyApplication.quizChordNumberOneToShow = tempChord.getNumberOneAsString();
                MyApplication.quizChordNumberTwoToShow = tempChord.getNumberTwoAsString();
            } catch (Exception e) {
                MyApplication.quizChordNameToShow = null;
            }
        }

        if(MyApplication.quizIntervalToPlay == null && MyApplication.quizChordToPlay == null && MyApplication.playWhatTone && MyApplication.playWhatOctave) {
            int tempRandomStartFrom = getRandomKey();

            for(int i = tempRandomStartFrom; i < MyApplication.upKeyBorder; i++) {
                // If answer is not true, show that key
                if(!((MyApplication.playWhatOctave && keysInSameOctave(MyApplication.quizLowestKey, i)) || (MyApplication.playWhatTone && keysAreSameTone(MyApplication.quizLowestKey, i)))) {
                    MyApplication.quizChordNameToShow = MyApplication.getKeyName(i);
                    break;
                }
            }

            if(MyApplication.quizChordNameToShow == null) {
                // If key was not found, try going down instead of up
                for(int i = tempRandomStartFrom-1; i > MyApplication.downKeyBorder; i--) {
                    // If answer is not true, show that key
                    if(!((MyApplication.playWhatOctave && keysInSameOctave(MyApplication.quizLowestKey, i)) || (MyApplication.playWhatTone && keysAreSameTone(MyApplication.quizLowestKey, i)))) {
                        MyApplication.quizChordNameToShow = MyApplication.getKeyName(i);
                        break;
                    }
                }
            }


        }

        // TODO: add min and max range support (user preference)
        if(MyApplication.quizIntervalToPlay == null && MyApplication.quizChordToPlay == null && !MyApplication.playWhatTone && MyApplication.playWhatOctave) {
            int tempOctaveNumb = (MyApplication.quizLowestKey-1)/12;

            int tempKey = (MyApplication.quizLowestKey-1)%12;

            // -1 so octave that is correct doesn't get shown
            int numberOfOctaves = DataContract.UserPrefEntry.NUMBER_OF_KEYS/12 - 1;
            int randomNumb = rand.nextInt(numberOfOctaves);
            for(int i = 0; i < numberOfOctaves-1; i++) {
                if(i != tempOctaveNumb && i >= randomNumb) {
                    // +1 to get back to normal naming scheme
                    MyApplication.quizChordNameToShow = MyApplication.getKeyName(tempKey + (12*i) + 1);
                    break;
                }
            }
        }

        if(MyApplication.quizChordNameToShow == null) {
            // Try one without looking for what is playing
            setupWrongThingToShowSecondTry();
        }

        if(MyApplication.quizChordNameToShow == null) {
            stopPlaying();
            playNextThing(0);
            return;
        }

    }

    private void setupWrongThingToShowSecondTry() {
        // If MyApplication.quizIntervalToPlay there will just be no exception
        try {
            MyApplication.quizChordNameToShow = IntervalsList.getRandomCheckedInterval(MyApplication.quizIntervalToPlay).getIntervalName();
        } catch (Exception e) {
            MyApplication.quizChordNameToShow = null;
        }

        if(MyApplication.playWhatTone || MyApplication.playWhatOctave && IntervalsList.getInterval(0).getIntervalName().equals(MyApplication.quizChordNameToShow)) {
            // If tones are playing and it shows čista prima, user cannot see the difference
            MyApplication.quizChordNameToShow = null;
        }

        if(MyApplication.quizChordNameToShow != null) {
            return;
        }

        // If MyApplication.quizChordToPlay there will just be no exception
        try {
            Chord tempChord = ChordsList.getRandomCheckedChord(MyApplication.quizChordToPlay);
            MyApplication.quizChordNameToShow = tempChord.getChordName();
            MyApplication.quizChordNumberOneToShow = tempChord.getNumberOneAsString();
            MyApplication.quizChordNumberTwoToShow = tempChord.getNumberTwoAsString();
        } catch (Exception e) {
            MyApplication.quizChordNameToShow = null;
        }

        if(MyApplication.quizChordNameToShow != null) {
            return;
        }

        // TODO: copy-pasted from function before, add min and max range support
        if(!MyApplication.playWhatTone && MyApplication.playWhatOctave) {
            int tempOctaveNumb = (MyApplication.quizLowestKey-1)/12;

            int tempKey = (MyApplication.quizLowestKey-1)%12;

            // -1 so octave that is correct doesn't get shown
            int numberOfOctaves = DataContract.UserPrefEntry.NUMBER_OF_KEYS/12 - 1;
            int randomNumb = rand.nextInt(numberOfOctaves);
            for(int i = 0; i < numberOfOctaves-1; i++) {
                if(i != tempOctaveNumb && i >= randomNumb) {
                    MyApplication.quizChordNameToShow = MyApplication.getKeyName(tempKey + (12*i));
                    break;
                }
            }
        }

        if(MyApplication.quizChordNameToShow != null) {
            return;
        }

        if((MyApplication.playWhatTone || MyApplication.playWhatOctave)) {
            int tempRandomStartFrom = getRandomKey();

            for(int i = tempRandomStartFrom; i < MyApplication.upKeyBorder; i++) {
                // If answer is not true, show that key
                if(!((MyApplication.playWhatOctave && keysInSameOctave(MyApplication.quizLowestKey, i)) || (MyApplication.playWhatTone && keysAreSameTone(MyApplication.quizLowestKey, i)))) {
                    MyApplication.quizChordNameToShow = MyApplication.getKeyName(i);
                    break;
                }
            }

            if(MyApplication.quizChordNameToShow == null) {
                // If key was not found, try going down instead of up
                for(int i = tempRandomStartFrom-1; i > MyApplication.downKeyBorder; i--) {
                    // If answer is not true, show that key
                    if(!((MyApplication.playWhatOctave && keysInSameOctave(MyApplication.quizLowestKey, i)) || (MyApplication.playWhatTone && keysAreSameTone(MyApplication.quizLowestKey, i)))) {
                        MyApplication.quizChordNameToShow = MyApplication.getKeyName(i);
                        break;
                    }
                }
            }
        }


    }

    private void gameOver() {
        // Save high score if greater than current
        MyApplication.refreshQuizModeOneHighScore();
        MyApplication.quizScore = 0;

        // Reset everything
        MyApplication.quizPlayingPaused = true;
        MyApplication.quizChordNameToShow = "";
        MyApplication.quizChordNumberOneToShow = "";
        MyApplication.quizChordNumberTwoToShow = "";
        MyApplication.waitingForQuizAnswer = false;

        showGameOverDialog();
    }



//    private boolean isChordCorrect() {
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append(chordTextView.getText()).append(chordNumOneTextView.getText()).append(chordNumTwoTextView.getText());
//
//        return (MyApplication.quizCorrectAnswerName.equals(stringBuilder.toString()));
//    }

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
        return ModeOneActivity.this.getResources().getString(id);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, null));
    }

    @Override
    protected void onResume() {
        super.onResume();

        MyApplication.activityResumed(ModeOneActivity.this);

        showChord();

        // If sound is being played, don't show progress bar (animation stops after screen rotation)
        if(MyApplication.quizPlayingCurrentThing && !MyApplication.quizPlayingPaused) {
            timeLeftToPlayProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        MyApplication.activityPaused();

        // Save high score if greater than current
        MyApplication.refreshQuizModeOneHighScore();
        MyApplication.quizScore = 0;

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
        }
        return super.onOptionsItemSelected(item);
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

        trueAnswer.setVisibility(View.GONE);
        falseAnswer.setVisibility(View.GONE);

        chordTextViewLayout.setVisibility(View.INVISIBLE);

        MyApplication.quizPlayingPaused = true;
    }

    private void resumeQuiz() {
        startClickableImageView.setVisibility(View.GONE);
        pauseClickableImageView.setVisibility(View.VISIBLE);

        trueAnswer.setVisibility(View.VISIBLE);
        falseAnswer.setVisibility(View.VISIBLE);

        chordTextViewLayout.setVisibility(View.VISIBLE);

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
