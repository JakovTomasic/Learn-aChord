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
import android.widget.LinearLayout;
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

// Medium quiz activity
public class ModeTwoActivity extends AppCompatActivity {

    // Playing progress bar
    private ProgressBar timeLeftToPlayProgressBar;

    // TextView that is showing score
    private TextView scoreTextView;

    // Start/resume quiz button
    private ImageView startClickableImageView;
    // Pause quiz button
    private ImageView pauseClickableImageView;

    // Temporary random object
    private Random rand;
    // Number of intervals and chords checked/chosen/enabled in options
    private int checkedIntervals, checkedChords;

    // Achievement icon to show on milestone reach
    private View achievementIconView;

    // Just a little delay between playing
    private static final long addMS = 100;

    // Thread for playing sounds
    Thread quizModeTwoPlayThread;

    // Thickness of playing progress bar in dp
    private static final int timeLeftToPlayProgressThicknessDP = 4;


    // List of all possible answers' data

    // Parent button for showing answer, background is button texture
    private View[] quizOptionParentLayout;
    // Linear layout for good positioning
    private View[] quizOptionLinearLayout;
    // TextView for answer number / title
    private TextView[] quizOptionTitleTV;
    // TextView for answer description (name)
    private View[] quizOptionDescriptionLayout;

    // Parent layout for showing answer
    private ViewGroup[] chordTextViewLayout;
    // Values to show
    private TextView[] chordTextView;
    private TextView[] chordNumOneTextView;
    private TextView[] chordNumTwoTextView;

    // Parent layout for choosing answer (contains answer buttons)
    private View optionsParentLayout;

    // Dimensions of each answer button
    private int height, width;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_quiz_mode_two);
        setTitle(readResource(R.string.quiz) + readResource(R.string.quiz_mode_title_separator) + readResource(R.string.quiz_mode_two_title));

        // Change media volume when volume buttons are pressed
        setVolumeControlStream(AudioManager.STREAM_MUSIC);


        // Set all elements for all answers

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

        achievementIconView = findViewById(R.id.achievement_icon_image_view);


        height = (int)(MyApplication.smallerDisplayDimensionPX/2.5f);
        width = height/3*2;

        for(int i = 0; i < quizOptionParentLayout.length; i++) {
            setAnswerSize(i);
        }


        rand = new Random();
        checkedIntervals = IntervalsList.getCheckedIntervalCount();
        checkedChords = ChordsList.getCheckedChordsCount();
        if((DatabaseData.playWhatTone || DatabaseData.playWhatOctave) && IntervalsList.getInterval(0).getIsChecked()) {
            // If tones can be played, don't count on čista prima
            checkedIntervals--;
        }

        timeLeftToPlayProgressBar = findViewById(R.id.ring_playing_progress_bar);

        scoreTextView = findViewById(R.id.quiz_score_text_view);

        startClickableImageView = findViewById(R.id.start_clickable_image_view);
        pauseClickableImageView = findViewById(R.id.pause_clickable_image_view);





        // Setup score text view text size
        scoreTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float)(MyApplication.smallerDisplayDimensionPX / 16) * DatabaseData.scaledDensity);
        scoreTextView.setText(String.valueOf(QuizData.quizScore));

        // Setup progress bar size
        ViewGroup.LayoutParams progressBarSizeRules = timeLeftToPlayProgressBar.getLayoutParams();
        progressBarSizeRules.width = MyApplication.smallerDisplayDimensionPX / 8;
        progressBarSizeRules.height = MyApplication.smallerDisplayDimensionPX / 8;
        timeLeftToPlayProgressBar.setLayoutParams(progressBarSizeRules);



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
            MyApplication.setupPlayButtonColor(ModeTwoActivity.this, startClickableImageView, R.color.unloadedColor);
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


        QuizData.isQuizModePlaying = true;

        // Show all possible answers
        showAllChords();

    }

    // Setup size for answer button
    private void setAnswerSize(int modeID) {
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

        quizOptionTitleTV[modeID].setTextSize(TypedValue.COMPLEX_UNIT_PX, height/7f);


        // Setup interval and chord (and tone) text size
        MyApplication.setupIntervalAndChordTextSize(chordTextView[modeID], chordNumOneTextView[modeID], chordNumTwoTextView[modeID], 2.6f);


    }

    // Handle what happens when one of the answers has been clicked
    private void handleClickOnOption(int id) {
        QuizData.waitingForQuizAnswer = false;
        QuizData.quizPlayingCurrentThing = false;
        stopPlaying();

        if(QuizData.quizModeTwoCorrectAnswerID == id) {
            scoreTextView.setText(String.valueOf(++QuizData.quizScore));
            playNextThing(0);
            AchievementChecker.checkAchievements(QuizData.quizScore);
        } else {
            gameOver();
        }
    }

    // Read string form resources
    private String readResource(int id) {
        return ModeTwoActivity.this.getResources().getString(id);
    }



    // Play next interval, chord or tone
    private void playNextThing(int numberOfRecursiveRuns) {
        if(QuizData.waitingForQuizAnswer || numberOfRecursiveRuns > 10) {
            // If app is waiting for an answer (user input) or this has been tried too many times, don't play next thing
            return;
        }

        if(checkedIntervals + checkedChords < 4 && !DatabaseData.playWhatTone && !DatabaseData.playWhatOctave) {
            Toast.makeText(MyApplication.getActivity(), readResource(R.string.not_enough_selected_intervals_or_chords_error), Toast.LENGTH_SHORT).show();
            pauseQuiz();
            return;
        }

        if(DatabaseData.directionsCount <= 0 && !DatabaseData.playWhatTone && !DatabaseData.playWhatOctave) {
            Toast.makeText(MyApplication.getActivity(), readResource(R.string.no_checked_playing_type_error), Toast.LENGTH_SHORT).show();
            pauseQuiz();
            return;
        }

        if(DatabaseData.upKeyBorder - DatabaseData.downKeyBorder < 13 && DatabaseData.playWhatOctave && !DatabaseData.playWhatTone) {
            Toast.makeText(MyApplication.getActivity(), readResource(R.string.key_borders_are_too_small), Toast.LENGTH_SHORT).show();
            pauseQuiz();
            return;
        }

        // Randomly choose correct answer
        QuizData.quizModeTwoCorrectAnswerID = rand.nextInt(4);

        // Reset temporary variables
        for(int i = 0; i < quizOptionParentLayout.length; i++) {
            QuizData.quizModeTwoChordNameToShow[i] = null;
            QuizData.quizModeTwoChordNumberOneToShow[i] = null;
            QuizData.quizModeTwoChordNumberTwoToShow[i] = null;

            QuizData.quizModeTwoSelectedIntervals[i] = null;
            QuizData.quizModeTwoSelectedChords[i] = null;
            QuizData.quizModeTwoSelectedTones[i] = null;
        }


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
            QuizData.quizModeTwoSelectedIntervals[QuizData.quizModeTwoCorrectAnswerID] = QuizData.quizIntervalToPlay;

            // Set correct answer to correct id
            QuizData.quizModeTwoChordNameToShow[QuizData.quizModeTwoCorrectAnswerID] = QuizData.quizIntervalToPlay.getName();

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
            QuizData.quizChordToPlay = ChordsList.getRandomCheckedChord();
            if(QuizData.quizChordToPlay == null) {
                // Something went wrong, try again
                playNextThing(numberOfRecursiveRuns+1);
                return;
            }
            QuizData.quizIntervalToPlay = null;
            QuizData.quizModeTwoSelectedChords[QuizData.quizModeTwoCorrectAnswerID] = QuizData.quizChordToPlay;

            // Set correct answer to correct id
            QuizData.quizModeTwoChordNameToShow[QuizData.quizModeTwoCorrectAnswerID] = QuizData.quizChordToPlay.getName();
            QuizData.quizModeTwoChordNumberOneToShow[QuizData.quizModeTwoCorrectAnswerID] = QuizData.quizChordToPlay.getNumberOneAsString();
            QuizData.quizModeTwoChordNumberTwoToShow[QuizData.quizModeTwoCorrectAnswerID] = QuizData.quizChordToPlay.getNumberTwoAsString();

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

        } else if(DatabaseData.playWhatTone || DatabaseData.playWhatOctave) {
            QuizData.quizIntervalToPlay = null;
            QuizData.quizChordToPlay = null;

            // Get random low key for playing (inside borders and with loaded sound)
            QuizData.quizLowestKey = QuizData.getRandomKey(rand);

            QuizData.quizModeTwoSelectedTones[QuizData.quizModeTwoCorrectAnswerID] = QuizData.quizLowestKey;

            // Set correct answer to correct id
            QuizData.quizModeTwoChordNameToShow[QuizData.quizModeTwoCorrectAnswerID] = MyApplication.getKeyName(QuizData.quizLowestKey);

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


        if(QuizData.quizIntervalToPlay != null || QuizData.quizChordToPlay != null) {
            // Get random low key for playing (inside borders and with loaded sound)
            QuizData.quizLowestKey = QuizData.getRandomKey(rand);
        }



        showAllChords();

        QuizData.waitingForQuizAnswer = true;
    }

    // Play current interval/chord/tone on separate thread
    private void playCurrentThing() {
        QuizData.quizPlayingCurrentThing = true;

        quizModeTwoPlayThread = new Thread(new Runnable() {
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

                        if(!(MyApplication.getActivity() instanceof ModeTwoActivity)) {
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
        quizModeTwoPlayThread.start();

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

    // Show all answers
    private void showAllChords() {
        for(int i = 0; i < quizOptionParentLayout.length; i++) {
            if(QuizData.quizModeTwoChordNameToShow[i] == null) {
                // If chord name that needs to be shown is null, show nothing (empty string)
                MyApplication.updateTextView(chordTextView[i], "", chordNumOneTextView[i], "", chordNumTwoTextView[i], "");
                continue;
            }
            MyApplication.updateTextView(chordTextView[i], QuizData.quizModeTwoChordNameToShow[i], chordNumOneTextView[i],
                    QuizData.quizModeTwoChordNumberOneToShow[i], chordNumTwoTextView[i], QuizData.quizModeTwoChordNumberTwoToShow[i]);

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

    // Randomly set wrong answer to show
    private void setupWrongThingToShow(int id) {
        if(QuizData.quizModeTwoCorrectAnswerID == id) {
            // Don't set wrong answer where correct answer should be
            return;
        }


        if(QuizData.quizIntervalToPlay != null) {
            setupWrongInterval(id);
        } else if(QuizData.quizChordToPlay != null) {
            setupWrongChord(id);
        } else if(/*QuizData.quizIntervalToPlay == null && QuizData.quizChordToPlay == null &&*/ DatabaseData.playWhatTone) {
            setupWrongToneIfPlayWhatTone(id);
        } else if(/*QuizData.quizIntervalToPlay == null && QuizData.quizChordToPlay == null && !DatabaseData.playWhatTone &&*/ DatabaseData.playWhatOctave) {
            setupWrongToneIfJustOctave(id);
        }

        if(QuizData.quizModeTwoChordNameToShow[id] == null) {
            // Try one without looking for what is playing
            setupWrongThingToShowSecondTry(id);
        }

        if(QuizData.quizModeTwoChordNameToShow[id] == null) {
            stopPlaying();
            playNextThing(0);
            return;
        }

    }

    // If first random setting was not successful, try one more time
    private void setupWrongThingToShowSecondTry(int id) {
        // Show any checked interval, if there is any
        setupWrongInterval(id);

        if(DatabaseData.playWhatTone || DatabaseData.playWhatOctave && IntervalsList.getInterval(0).getName().equals(QuizData.quizModeTwoChordNameToShow[id])) {
            // If tones are playing and it shows čista prima, user cannot see the difference
            QuizData.quizModeTwoChordNameToShow[id] = null;
        }

        if(QuizData.quizModeTwoChordNameToShow[id] != null) {
            return;
        }

        // Show any checked chord, if there is any
        setupWrongChord(id);


        if(!DatabaseData.playWhatTone && DatabaseData.playWhatOctave) {
            setupWrongToneIfJustOctave(id);
        }

        if(QuizData.quizModeTwoChordNameToShow[id] != null) {
            return;
        }

        if(DatabaseData.playWhatTone) {
            setupWrongToneIfPlayWhatTone(id);
        }


    }

    // Set random interval that is not playing (and is selected in options; and is not already set as other answer)
    private void setupWrongInterval(int id) {
        try {
            // Interval to show cannot be same as any other
            Interval tempInterval = IntervalsList.getRandomCheckedInterval(
                    QuizData.quizModeTwoSelectedIntervals[0], QuizData.quizModeTwoSelectedIntervals[1],
                    QuizData.quizModeTwoSelectedIntervals[2], QuizData.quizModeTwoSelectedIntervals[3]);
            QuizData.quizModeTwoChordNameToShow[id] = tempInterval.getName();

            // Save it so it doesn't show again
            QuizData.quizModeTwoSelectedIntervals[id] = tempInterval;
        } catch (Exception e) {
            QuizData.quizModeTwoChordNameToShow[id] = null;
        }
    }

    // Set random chord that is not playing (and is selected in options)
    private void setupWrongChord(int id) {
        try {
            // Chord to show cannot be same as any other
            Chord tempChord = ChordsList.getRandomCheckedChord(
                    QuizData.quizModeTwoSelectedChords[0], QuizData.quizModeTwoSelectedChords[1],
                    QuizData.quizModeTwoSelectedChords[2], QuizData.quizModeTwoSelectedChords[3]);
            QuizData.quizModeTwoChordNameToShow[id] = tempChord.getName();
            QuizData.quizModeTwoChordNumberOneToShow[id] = tempChord.getNumberOneAsString();
            QuizData.quizModeTwoChordNumberTwoToShow[id] = tempChord.getNumberTwoAsString();

            // Save it so it doesn't show again
            QuizData.quizModeTwoSelectedChords[id] = tempChord;
        } catch (Exception e) {
            QuizData.quizModeTwoChordNameToShow[id] = null;
        }
    }

    // Set random tone that is not playing (and is selected in options) - when only tone is selected in options
    private void setupWrongToneIfPlayWhatTone(int id) {
        int tempRandomStartFrom = rand.nextInt(DatabaseData.upKeyBorder- DatabaseData.downKeyBorder)+ DatabaseData.downKeyBorder;

        // Tone to show cannot be same as any other
        for(int i = tempRandomStartFrom; i <= DatabaseData.upKeyBorder; i++) {
            boolean thisToneIsAcceptable = true;

            // Check if it is not same as any tone (in octave or as tone, depending on user preference)
            for(int j = 0; j < quizOptionParentLayout.length; j++) {
                if(QuizData.quizModeTwoSelectedTones[j] == null) {
                    continue;
                }
                if(DatabaseData.playWhatOctave && keysInSameOctave(QuizData.quizModeTwoSelectedTones[j], i)) {
                    thisToneIsAcceptable = false;
                    break;
                }
                if(DatabaseData.playWhatTone && keysAreSameTone(QuizData.quizModeTwoSelectedTones[j], i)) {
                    thisToneIsAcceptable = false;
                    break;
                }

                // If just tone is needed, it needs to be in same octave
                if(DatabaseData.playWhatTone && !DatabaseData.playWhatOctave && !keysInSameOctave(QuizData.quizModeTwoSelectedTones[j], i)) {
                    thisToneIsAcceptable = false;
                    break;
                }
            }
            if(!thisToneIsAcceptable) {
                continue;
            }

            QuizData.quizModeTwoChordNameToShow[id] = MyApplication.getKeyName(i);

            // Save it so it doesn't show again
            QuizData.quizModeTwoSelectedTones[id] = i;
            break;
        }

        if(QuizData.quizModeTwoChordNameToShow[id] == null) {
            // If key was not found, try going down instead of up
            for(int i = tempRandomStartFrom-1; i >= DatabaseData.downKeyBorder; i--) {
                boolean thisToneIsAcceptable = true;

                // Check if it is not same as any tone (in octave or as tone, depending on user preference)
                for(int j = 0; j < quizOptionParentLayout.length; j++) {
                    if(QuizData.quizModeTwoSelectedTones[j] == null) {
                        continue;
                    }
                    if(DatabaseData.playWhatOctave && keysInSameOctave(QuizData.quizModeTwoSelectedTones[j], i)) {
                        thisToneIsAcceptable = false;
                        break;
                    }
                    if(DatabaseData.playWhatTone && keysAreSameTone(QuizData.quizModeTwoSelectedTones[j], i)) {
                        thisToneIsAcceptable = false;
                        break;
                    }

                    // If just tone is needed, it needs to be in same octave
                    if(DatabaseData.playWhatTone && !DatabaseData.playWhatOctave && !keysInSameOctave(QuizData.quizModeTwoSelectedTones[j], i)) {
                        thisToneIsAcceptable = false;
                        break;
                    }
                }
                if(!thisToneIsAcceptable) {
                    continue;
                }

                QuizData.quizModeTwoChordNameToShow[id] = MyApplication.getKeyName(i);

                // Save it so it doesn't show again
                QuizData.quizModeTwoSelectedTones[id] = i;
                break;
            }
        }
    }

    // Set random tone that is not playing (and is selected in options) - when only octave is selected in options
    private void setupWrongToneIfJustOctave(int id) {
        // TODO: add min and max range support (user preference)

        // Tone to show cannot be same as any other
        int alreadySetTonesCounter = 0;
        for(int i = 0; i < quizOptionParentLayout.length; i++) {
            if(QuizData.quizModeTwoSelectedTones[i] != null) {
                alreadySetTonesCounter++;
            }
        }

        int tempOctaveNumb = (QuizData.quizLowestKey-1)/12;

        int tempKey = (QuizData.quizLowestKey-1)%12;

        int numberOfOctaves = DataContract.UserPrefEntry.NUMBER_OF_KEYS/12;
        // - alreadySetTonesCounter so octave that is correct doesn't get shown
        // Tone to show cannot be same as any other
        int randomNumb = rand.nextInt(numberOfOctaves-alreadySetTonesCounter);
        int counter = 0;
        for(int i = 0; i < numberOfOctaves; i++) {
            // If tone is not in same octave and
            if(i != tempOctaveNumb && counter >= randomNumb
                    && (QuizData.quizModeTwoSelectedTones[0] == null || i != (QuizData.quizModeTwoSelectedTones[0]-1)/12)
                    && (QuizData.quizModeTwoSelectedTones[1] == null || i != (QuizData.quizModeTwoSelectedTones[1]-1)/12)
                    && (QuizData.quizModeTwoSelectedTones[2] == null || i != (QuizData.quizModeTwoSelectedTones[2]-1)/12)
                    && (QuizData.quizModeTwoSelectedTones[3] == null || i != (QuizData.quizModeTwoSelectedTones[3]-1)/12)) {
                // +1 to get back to normal naming scheme
                QuizData.quizModeTwoChordNameToShow[id] = MyApplication.getKeyName(tempKey + (12*i) + 1);

                // Save it so it doesn't show again
                QuizData.quizModeTwoSelectedTones[id] = tempKey + (12*i) + 1;
                break;
            } else {
                counter++;
            }
        }
    }


    // Called when user chooses false answer
    private void gameOver() {
        // Save high score if greater than current
        QuizData.refreshQuizModeTwoHighScore();

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


    // For different languages support
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, null));
    }

    @Override
    protected void onResume() {
        super.onResume();

        MyApplication.activityResumed(ModeTwoActivity.this);

        AchievementChecker.lastPlayedQuizMode = AchievementChecker.QUIZ_MODE_TWO_ID;


        showAllChords();

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
        QuizData.refreshQuizModeTwoHighScore();

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


    // Stop all sounds from playing
    private void stopPlaying() {
        QuizData.quizPlayingID += 10;
        if(quizModeTwoPlayThread != null) {
            quizModeTwoPlayThread.interrupt();
            quizModeTwoPlayThread = null;
        }
        MyApplication.setIsPlaying(false);

        // Set progress bar to 100 immediately
        updateProgressBarAnimation(0);
    }

    // Set UI for when quiz is paused
    private void pauseQuiz() {
        startClickableImageView.setVisibility(View.VISIBLE);
        pauseClickableImageView.setVisibility(View.GONE);

        optionsParentLayout.setVisibility(View.GONE);

        QuizData.quizPlayingPaused = true;
    }

    // Set UI for when quiz is resumed (playing)
    private void resumeQuiz() {
        startClickableImageView.setVisibility(View.GONE);
        pauseClickableImageView.setVisibility(View.VISIBLE);

        optionsParentLayout.setVisibility(View.VISIBLE);

        QuizData.quizPlayingPaused = false;
    }

    // Dialog shows when user chooses a wrong answer
    private void showGameOverDialog(int score) {
        // Create an AlertDialog.Builder and set the message, and click listeners for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set view of a dialog that displays score and the correct answer
        builder.setView(MyApplication.getQuizGameOverDialogLayour(ModeTwoActivity.this, score));

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
                QuizData.quizModeTwoChordNameToShow = new String[] {"", "", "", ""};
                QuizData.quizModeTwoChordNumberOneToShow = new String[] {"", "", "", ""};
                QuizData.quizModeTwoChordNumberTwoToShow = new String[] {"", "", "", ""};
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
