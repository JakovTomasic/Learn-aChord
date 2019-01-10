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
import android.widget.LinearLayout;
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

public class ModeTwoActivity extends AppCompatActivity {


    private ProgressBar timeLeftToPlayProgressBar;

    private TextView scoreTextView;

    private ImageView startClickableImageView;
    private ImageView pauseClickableImageView;

    private Random rand;
    private int checkedIntervals, checkedChords;

    // Just a little delay between playing
    private final long addMS = 100;

    Thread quizModeTwoPlayThread;

    private final int timeLeftToPlayProgressThicknessDB = 4;



    private View[] quizOptionParentLayout;
    private View[] quizOptionLinearLayout;
    private TextView[] quizOptionTitleTV;
    private View[] quizOptionDescriptionLayout;

    private ViewGroup[] chordTextViewLayout;
    private TextView[] chordTextView;
    private TextView[] chordNumOneTextView;
    private TextView[] chordNumTwoTextView;

    private View optionsParentLayout;

    private int height, width;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_quiz_mode_two);
        setTitle(readResource(R.string.quiz) + readResource(R.string.quiz_mode_title_separator) + readResource(R.string.quiz_mode_two_title));


        optionsParentLayout = findViewById(R.id.four_options_parent_layout);

        quizOptionParentLayout = new View[] {
                findViewById(R.id.quiz_option_one_parent_layout),
                findViewById(R.id.quiz_option_two_parent_layout),
                findViewById(R.id.quiz_option_three_parent_layout),
                findViewById(R.id.quiz_option_four_parent_layout)
        };
        quizOptionLinearLayout = new View[] {
                findViewById(R.id.quiz_option_one_linear_layout),
                findViewById(R.id.quiz_option_two_linear_layout),
                findViewById(R.id.quiz_option_three_linear_layout),
                findViewById(R.id.quiz_option_four_linear_layout)
        };
        quizOptionTitleTV = new TextView[] {
                findViewById(R.id.quiz_option_one_title_text_view),
                findViewById(R.id.quiz_option_two_title_text_view),
                findViewById(R.id.quiz_option_three_title_text_view),
                findViewById(R.id.quiz_option_four_title_text_view)
        };
        quizOptionDescriptionLayout = new View[] {
                findViewById(R.id.quiz_option_one_description_relative_layout),
                findViewById(R.id.quiz_option_two_description_relative_layout),
                findViewById(R.id.quiz_option_three_description_relative_layout),
                findViewById(R.id.quiz_option_four_description_relative_layout)
        };


        chordTextViewLayout = new ViewGroup[] {
                findViewById(R.id.option_one_chord_text_view_linear_layout),
                findViewById(R.id.option_two_chord_text_view_linear_layout),
                findViewById(R.id.option_three_chord_text_view_linear_layout),
                findViewById(R.id.option_four_chord_text_view_linear_layout)
        };
        chordTextView = new TextView[] {
                findViewById(R.id.option_one_chord_text_view),
                findViewById(R.id.option_two_chord_text_view),
                findViewById(R.id.option_three_chord_text_view),
                findViewById(R.id.option_four_chord_text_view)
        };
        chordNumOneTextView = new TextView[] {
                findViewById(R.id.option_one_chord_number_one),
                findViewById(R.id.option_two_chord_number_one),
                findViewById(R.id.option_three_chord_number_one),
                findViewById(R.id.option_four_chord_number_one)
        };
        chordNumTwoTextView = new TextView[] {
                findViewById(R.id.option_one_chord_number_two),
                findViewById(R.id.option_two_chord_number_two),
                findViewById(R.id.option_three_chord_number_two),
                findViewById(R.id.option_four_chord_number_two)
        };


        height = (int)(MyApplication.smallerDisplayDimensionPX/2.5f);
        width = height/3*2;

        for(int i = 0; i < quizOptionParentLayout.length; i++) {
            setQuizModeSize(i);
        }


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





        // Setup score text view text size
        scoreTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float)(MyApplication.smallerDisplayDimensionPX / 16) * MyApplication.scaledDensity);
        scoreTextView.setText(String.valueOf(MyApplication.quizScore));

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
            MyApplication.setupPlayButtonColor(ModeTwoActivity.this, startClickableImageView, R.color.unloadedColor);
        }

        MyApplication.addActivityListener(new MyApplication.ActivityListener() {
            @Override
            public void onIsPlayingChange() {

            }

            @Override
            public void onLoadingFinished() {
                startClickableImageView.setClickable(true);
                startClickableImageView.setFocusable(true);
                MyApplication.setupPlayButtonColor(ModeTwoActivity.this, startClickableImageView, R.color.playButton);
            }
        });



        // Setup answer (options) buttons listeners
        for(int i = 0; i < quizOptionParentLayout.length; i++) {
            final int tempID = i;
            quizOptionParentLayout[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleClickOnOption(tempID);
                }
            });
        }


        MyApplication.isQuizModePlaying = true;

        showAllChords();

    }

    private void setQuizModeSize(int modeID) {
        // Setup quizModeParentLayout size and padding
        ViewGroup.LayoutParams quizOptionParentSizeRules = quizOptionParentLayout[modeID].getLayoutParams();

//        quizModeParentLayout[modeID].setPadding(width/10, width/10, width/10, width/10);
        quizOptionParentSizeRules.width = width;
        quizOptionParentSizeRules.height = height;
        quizOptionParentLayout[modeID].setLayoutParams(quizOptionParentSizeRules);

        // Set padding for that layout
        quizOptionLinearLayout[modeID].setPadding(width/10, width/10, width/10, width/10);



        // Setup quizModeTitleTV (text) size (and quizModeDescriptionTV size automatically)
        LinearLayout.LayoutParams quizModeTitleSizeRules = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        quizModeTitleSizeRules.setMargins(0, 0, 0, width/10);
        quizOptionTitleTV[modeID].setLayoutParams(quizModeTitleSizeRules);

        quizOptionTitleTV[modeID].setTextSize(TypedValue.COMPLEX_UNIT_PX, height/7);


        // Setup interval and chord (and tone) text size
        MyApplication.setupIntervalAndChordTextSize(chordTextView[modeID], chordNumOneTextView[modeID], chordNumTwoTextView[modeID], 2.6f);


    }

    private void handleClickOnOption(int id) {
        MyApplication.waitingForQuizAnswer = false;
        MyApplication.quizPlayingCurrentThing = false;
        stopPlaying();

        if(MyApplication.quizModeTwoCorrectAnswerID == id) {
            scoreTextView.setText(String.valueOf(++MyApplication.quizScore));
            playNextThing(0);
        } else {
            gameOver();
        }
    }

    private String readResource(int id) {
        return ModeTwoActivity.this.getResources().getString(id);
    }



    // Play next interval, chord or tone
    private void playNextThing(int numberOfRecursiveRuns) {
        if(MyApplication.waitingForQuizAnswer || numberOfRecursiveRuns > 5) {
            return;
        }

        if(checkedIntervals + checkedChords < 4 && !MyApplication.playWhatTone && !MyApplication.playWhatOctave) {
            Toast.makeText(MyApplication.getActivity(), readResource(R.string.not_enough_selected_intervals_or_chords_error), Toast.LENGTH_SHORT).show();
            pauseQuiz();
            return;
        }

        if(MyApplication.directionsCount <= 0 && !MyApplication.playWhatTone && !MyApplication.playWhatOctave) {
            Toast.makeText(MyApplication.getActivity(), readResource(R.string.no_checked_playing_type_error), Toast.LENGTH_SHORT).show();
            pauseQuiz();
            return;
        }

        if(MyApplication.upKeyBorder - MyApplication.downKeyBorder < 13 && MyApplication.playWhatOctave && !MyApplication.playWhatTone) {
            Toast.makeText(MyApplication.getActivity(), readResource(R.string.key_borders_are_too_small), Toast.LENGTH_SHORT).show();
            pauseQuiz();
            return;
        }

        // Randomly choose correct answer
        MyApplication.quizModeTwoCorrectAnswerID = rand.nextInt(4);

        // Reset this
        for(int i = 0; i < quizOptionParentLayout.length; i++) {
            MyApplication.quizModeTwoChordNameToShow[i] = null;
            MyApplication.quizModeTwoChordNumberOneToShow[i] = null;
            MyApplication.quizModeTwoChordNumberTwoToShow[i] = null;

            MyApplication.quizModeTwoSelectedIntervals[i] = null;
            MyApplication.quizModeTwoSelectedChords[i] = null;
            MyApplication.quizModeTwoSelectedTones[i] = null;
        }


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
            MyApplication.quizModeTwoSelectedIntervals[MyApplication.quizModeTwoCorrectAnswerID] = MyApplication.quizIntervalToPlay;

            // Set correct answer to correct id
            MyApplication.quizModeTwoChordNameToShow[MyApplication.quizModeTwoCorrectAnswerID] = MyApplication.quizIntervalToPlay.getIntervalName();

            // Set wrong answers to wrong ids, in random order
            int tempRandomStartFrom = rand.nextInt(quizOptionParentLayout.length);
            for(int i = tempRandomStartFrom; i < quizOptionParentLayout.length; i++) {
                setupWrongThingToShow(i);
            }
            for(int i = tempRandomStartFrom-1; i >= 0; i--) {
                setupWrongThingToShow(i);
            }

            // Play correct interval
            playCurrentThing();

        } else if(tempRandNumb < checkedIntervals+checkedChords) { // Play chord
            MyApplication.quizChordToPlay = ChordsList.getRandomCheckedChord();
            if(MyApplication.quizChordToPlay == null) {
                // Something went wrong, try again
                playNextThing(numberOfRecursiveRuns+1);
                return;
            }
            MyApplication.quizIntervalToPlay = null;
            MyApplication.quizModeTwoSelectedChords[MyApplication.quizModeTwoCorrectAnswerID] = MyApplication.quizChordToPlay;

            // Set correct answer to correct id
            MyApplication.quizModeTwoChordNameToShow[MyApplication.quizModeTwoCorrectAnswerID] = MyApplication.quizChordToPlay.getChordName();
            MyApplication.quizModeTwoChordNumberOneToShow[MyApplication.quizModeTwoCorrectAnswerID] = MyApplication.quizChordToPlay.getNumberOneAsString();
            MyApplication.quizModeTwoChordNumberTwoToShow[MyApplication.quizModeTwoCorrectAnswerID] = MyApplication.quizChordToPlay.getNumberTwoAsString();

            // Set wrong answers to wrong ids, in random order
            int tempRandomStartFrom = rand.nextInt(quizOptionParentLayout.length);
            for(int i = tempRandomStartFrom; i < quizOptionParentLayout.length; i++) {
                setupWrongThingToShow(i);
            }
            for(int i = tempRandomStartFrom-1; i >= 0; i--) {
                setupWrongThingToShow(i);
            }

            // Play correct chord
            playCurrentThing();

        } else if(MyApplication.playWhatTone || MyApplication.playWhatOctave) {
            MyApplication.quizIntervalToPlay = null;
            MyApplication.quizChordToPlay = null;

            // Get random low key for playing (inside borders)
            MyApplication.quizLowestKey = getRandomKey();

            MyApplication.quizModeTwoSelectedTones[MyApplication.quizModeTwoCorrectAnswerID] = MyApplication.quizLowestKey;

            // Set correct answer to correct id
            MyApplication.quizModeTwoChordNameToShow[MyApplication.quizModeTwoCorrectAnswerID] = MyApplication.getKeyName(MyApplication.quizLowestKey);

            // Set wrong answers to wrong ids, in random order
            int tempRandomStartFrom = rand.nextInt(quizOptionParentLayout.length);
            for(int i = tempRandomStartFrom; i < quizOptionParentLayout.length; i++) {
                setupWrongThingToShow(i);
            }
            for(int i = tempRandomStartFrom-1; i >= 0; i--) {
                setupWrongThingToShow(i);
            }

            // Play correct tone
            playCurrentThing();

        } else {
            // Something went wrong, try again
            playNextThing(numberOfRecursiveRuns+1);
            return;
        }


        if(MyApplication.quizIntervalToPlay != null || MyApplication.quizChordToPlay != null) {
            // Get random low key for playing (inside borders)
            MyApplication.quizLowestKey = getRandomKey();
        }



        showAllChords();

        MyApplication.waitingForQuizAnswer = true;
    }

    private void playCurrentThing() {
        MyApplication.quizPlayingCurrentThing = true;

        quizModeTwoPlayThread = new Thread(new Runnable() {
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

                        if(!(MyApplication.getActivity() instanceof ModeTwoActivity)) {
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
        quizModeTwoPlayThread.start();

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

    private void showAllChords() {
        for(int i = 0; i < quizOptionParentLayout.length; i++) {
            if(MyApplication.quizModeTwoChordNameToShow[i] == null) {
                // If chord name that needs to be shown is null, show nothing (empty string)
                MyApplication.updateTextView(chordTextView[i], "", chordNumOneTextView[i], "", chordNumTwoTextView[i], "");
                continue;
            }
            MyApplication.updateTextView(chordTextView[i], MyApplication.quizModeTwoChordNameToShow[i], chordNumOneTextView[i],
                    MyApplication.quizModeTwoChordNumberOneToShow[i], chordNumTwoTextView[i], MyApplication.quizModeTwoChordNumberTwoToShow[i]);

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

    private void setupWrongThingToShow(int id) {
        if(MyApplication.quizModeTwoCorrectAnswerID == id) {
            // Don't set wrong answer where correct answer should be
            return;
        }


        if(MyApplication.quizIntervalToPlay != null) {
            setupWrongInterval(id);
        } else if(MyApplication.quizChordToPlay != null) {
            setupWrongChord(id);
        } else if(MyApplication.quizIntervalToPlay == null && MyApplication.quizChordToPlay == null && MyApplication.playWhatTone) {
            setupWrongToneIfPlayWhatTone(id);
        } else if(MyApplication.quizIntervalToPlay == null && MyApplication.quizChordToPlay == null && !MyApplication.playWhatTone && MyApplication.playWhatOctave) {
            setupWrongToneIfJustOctave(id);
        }

        if(MyApplication.quizModeTwoChordNameToShow[id] == null) {
            // Try one without looking for what is playing
            setupWrongThingToShowSecondTry(id);
        }

        if(MyApplication.quizModeTwoChordNameToShow[id] == null) {
            stopPlaying();
            playNextThing(0);
            return;
        }

    }

    private void setupWrongThingToShowSecondTry(int id) {
        // Show any checked interval, if there is any
        setupWrongInterval(id);

        if(MyApplication.playWhatTone || MyApplication.playWhatOctave && IntervalsList.getInterval(0).getIntervalName().equals(MyApplication.quizModeTwoChordNameToShow[id])) {
            // If tones are playing and it shows čista prima, user cannot see the difference
            MyApplication.quizModeTwoChordNameToShow[id] = null;
        }

        if(MyApplication.quizModeTwoChordNameToShow[id] != null) {
            return;
        }

        // Show any checked chord, if there is any
        setupWrongChord(id);


        if(!MyApplication.playWhatTone && MyApplication.playWhatOctave) {
            setupWrongToneIfJustOctave(id);
        }

        if(MyApplication.quizModeTwoChordNameToShow[id] != null) {
            return;
        }

        if(MyApplication.playWhatTone) {
            setupWrongToneIfPlayWhatTone(id);
        }


    }

    private void setupWrongInterval(int id) {
        try {
            // Interval to show cannot be same as any other
            Interval tempInterval = IntervalsList.getRandomCheckedInterval(
                    MyApplication.quizModeTwoSelectedIntervals[0], MyApplication.quizModeTwoSelectedIntervals[1],
                    MyApplication.quizModeTwoSelectedIntervals[2], MyApplication.quizModeTwoSelectedIntervals[3]);
            MyApplication.quizModeTwoChordNameToShow[id] = tempInterval.getIntervalName();

            // Save it so it doesn't show again
            MyApplication.quizModeTwoSelectedIntervals[id] = tempInterval;
        } catch (Exception e) {
            MyApplication.quizModeTwoChordNameToShow[id] = null;
        }
    }

    private void setupWrongChord(int id) {
        try {
            // Chord to show cannot be same as any other
            Chord tempChord = ChordsList.getRandomCheckedChord(
                    MyApplication.quizModeTwoSelectedChords[0], MyApplication.quizModeTwoSelectedChords[1],
                    MyApplication.quizModeTwoSelectedChords[2], MyApplication.quizModeTwoSelectedChords[3]);
            MyApplication.quizModeTwoChordNameToShow[id] = tempChord.getChordName();
            MyApplication.quizModeTwoChordNumberOneToShow[id] = tempChord.getNumberOneAsString();
            MyApplication.quizModeTwoChordNumberTwoToShow[id] = tempChord.getNumberTwoAsString();

            // Save it so it doesn't show again
            MyApplication.quizModeTwoSelectedChords[id] = tempChord;
        } catch (Exception e) {
            MyApplication.quizModeTwoChordNameToShow[id] = null;
        }
    }

    private void setupWrongToneIfPlayWhatTone(int id) {
        int tempRandomStartFrom = rand.nextInt(MyApplication.upKeyBorder-MyApplication.downKeyBorder)+MyApplication.downKeyBorder;

        // Tone to show cannot be same as any other
        for(int i = tempRandomStartFrom; i <= MyApplication.upKeyBorder; i++) {
            boolean thisToneIsAcceptable = true;

            // Check if it is not same as any tone (in octave or as tone, depending on user preference)
            for(int j = 0; j < quizOptionParentLayout.length; j++) {
                if(MyApplication.quizModeTwoSelectedTones[j] == null) {
                    continue;
                }
                if(MyApplication.playWhatOctave && keysInSameOctave(MyApplication.quizModeTwoSelectedTones[j], i)) {
                    thisToneIsAcceptable = false;
                    break;
                }
                if(MyApplication.playWhatTone && keysAreSameTone(MyApplication.quizModeTwoSelectedTones[j], i)) {
                    thisToneIsAcceptable = false;
                    break;
                }

                // If just tone is needed, it needs to be in same octave
                if(MyApplication.playWhatTone && !MyApplication.playWhatOctave && !keysInSameOctave(MyApplication.quizModeTwoSelectedTones[j], i)) {
                    thisToneIsAcceptable = false;
                    break;
                }
            }
            if(!thisToneIsAcceptable) {
                continue;
            }

            MyApplication.quizModeTwoChordNameToShow[id] = MyApplication.getKeyName(i);

            // Save it so it doesn't show again
            MyApplication.quizModeTwoSelectedTones[id] = i;
            break;
        }

        if(MyApplication.quizModeTwoChordNameToShow[id] == null) {
            // If key was not found, try going down instead of up
            for(int i = tempRandomStartFrom-1; i >= MyApplication.downKeyBorder; i--) {
                boolean thisToneIsAcceptable = true;

                // Check if it is not same as any tone (in octave or as tone, depending on user preference)
                for(int j = 0; j < quizOptionParentLayout.length; j++) {
                    if(MyApplication.quizModeTwoSelectedTones[j] == null) {
                        continue;
                    }
                    if(MyApplication.playWhatOctave && keysInSameOctave(MyApplication.quizModeTwoSelectedTones[j], i)) {
                        thisToneIsAcceptable = false;
                        break;
                    }
                    if(MyApplication.playWhatTone && keysAreSameTone(MyApplication.quizModeTwoSelectedTones[j], i)) {
                        thisToneIsAcceptable = false;
                        break;
                    }

                    // If just tone is needed, it needs to be in same octave
                    if(MyApplication.playWhatTone && !MyApplication.playWhatOctave && !keysInSameOctave(MyApplication.quizModeTwoSelectedTones[j], i)) {
                        thisToneIsAcceptable = false;
                        break;
                    }
                }
                if(!thisToneIsAcceptable) {
                    continue;
                }

                MyApplication.quizModeTwoChordNameToShow[id] = MyApplication.getKeyName(i);

                // Save it so it doesn't show again
                MyApplication.quizModeTwoSelectedTones[id] = i;
                break;
            }
        }
    }

    private void setupWrongToneIfJustOctave(int id) {
        // TODO: add min and max range support (user preference)

        // Tone to show cannot be same as any other
        int alreadySetTonesCounter = 0;
        for(int i = 0; i < quizOptionParentLayout.length; i++) {
            if(MyApplication.quizModeTwoSelectedTones[i] != null) {
                alreadySetTonesCounter++;
            }
        }

        int tempOctaveNumb = (MyApplication.quizLowestKey-1)/12;

        int tempKey = (MyApplication.quizLowestKey-1)%12;

        int numberOfOctaves = DataContract.UserPrefEntry.NUMBER_OF_KEYS/12;
        // - alreadySetTonesCounter so octave that is correct doesn't get shown
        // Tone to show cannot be same as any other
        int randomNumb = rand.nextInt(numberOfOctaves-alreadySetTonesCounter);
        int counter = 0;
        for(int i = 0; i < numberOfOctaves; i++) {
            // If tone is not in same octave and
            if(i != tempOctaveNumb && counter >= randomNumb
                    && (MyApplication.quizModeTwoSelectedTones[0] == null || i != (MyApplication.quizModeTwoSelectedTones[0]-1)/12)
                    && (MyApplication.quizModeTwoSelectedTones[1] == null || i != (MyApplication.quizModeTwoSelectedTones[1]-1)/12)
                    && (MyApplication.quizModeTwoSelectedTones[2] == null || i != (MyApplication.quizModeTwoSelectedTones[2]-1)/12)
                    && (MyApplication.quizModeTwoSelectedTones[3] == null || i != (MyApplication.quizModeTwoSelectedTones[3]-1)/12)) {
                // +1 to get back to normal naming scheme
                MyApplication.quizModeTwoChordNameToShow[id] = MyApplication.getKeyName(tempKey + (12*i) + 1);

                // Save it so it doesn't show again
                MyApplication.quizModeTwoSelectedTones[id] = tempKey + (12*i) + 1;
                break;
            } else {
                counter++;
            }
        }
    }


    private void gameOver() {
        // Save high score if greater than current
        MyApplication.refreshQuizModeTwoHighScore();
        MyApplication.quizScore = 0;

        // Reset everything
        MyApplication.quizPlayingPaused = true;
        MyApplication.quizModeTwoChordNameToShow = new String[] {"", "", "", ""};
        MyApplication.quizModeTwoChordNumberOneToShow = new String[] {"", "", "", ""};
        MyApplication.quizModeTwoChordNumberTwoToShow = new String[] {"", "", "", ""};
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


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, null));
    }

    @Override
    protected void onResume() {
        super.onResume();

        MyApplication.activityResumed(ModeTwoActivity.this);


        showAllChords();

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
        MyApplication.refreshQuizModeTwoHighScore();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private void stopPlaying() {
        MyApplication.quizPlayingID += 10;
        if(quizModeTwoPlayThread != null) {
            quizModeTwoPlayThread.interrupt();
            quizModeTwoPlayThread = null;
        }
        MyApplication.setIsPlaying(false);

        // Set progress bar to 100 immediately
        updateProgressBarAnimation(0);
    }

    private void pauseQuiz() {
        startClickableImageView.setVisibility(View.VISIBLE);
        pauseClickableImageView.setVisibility(View.GONE);

        optionsParentLayout.setVisibility(View.GONE);

        MyApplication.quizPlayingPaused = true;
    }

    private void resumeQuiz() {
        startClickableImageView.setVisibility(View.GONE);
        pauseClickableImageView.setVisibility(View.VISIBLE);

        optionsParentLayout.setVisibility(View.VISIBLE);

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
