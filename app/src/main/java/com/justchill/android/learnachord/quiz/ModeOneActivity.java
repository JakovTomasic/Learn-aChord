package com.justchill.android.learnachord.quiz;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.justchill.android.learnachord.LocaleHelper;
import com.justchill.android.learnachord.MainActivity;
import com.justchill.android.learnachord.MyApplication;
import com.justchill.android.learnachord.R;
import com.justchill.android.learnachord.chord.Chord;
import com.justchill.android.learnachord.chord.ChordsList;
import com.justchill.android.learnachord.chord.Interval;
import com.justchill.android.learnachord.chord.IntervalsList;

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
    private String chordNameToShow, chordNumberOneToShow, chordNumberTwoToShow;


    private final int timeLeftToPlayProgressThicknessDB = 4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_quiz_mode_one);
        setTitle(readResource(R.string.quiz) + readResource(R.string.quiz_mode_title_separator) + readResource(R.string.quiz_mode_one_title));

        rand = new Random();
        checkedIntervals = IntervalsList.getCheckedIntervalCount();
        checkedChords = ChordsList.getCheckedChordsCount();

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
                startClickableImageView.setVisibility(View.GONE);
                pauseClickableImageView.setVisibility(View.VISIBLE);

                chordTextViewLayout.setVisibility(View.VISIBLE);

                MyApplication.quizPlayingPaused = false;

                playNextThing();
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
            startClickableImageView.setVisibility(View.VISIBLE);
            pauseClickableImageView.setVisibility(View.GONE);
        } else {
            startClickableImageView.setVisibility(View.GONE);
            pauseClickableImageView.setVisibility(View.VISIBLE);
        }

        if(!MyApplication.isLoadingFinished) {
            startClickableImageView.setClickable(false);
            startClickableImageView.setFocusable(false);
        }


        pauseClickableImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startClickableImageView.setVisibility(View.VISIBLE);
                pauseClickableImageView.setVisibility(View.GONE);

                chordTextViewLayout.setVisibility(View.INVISIBLE);

                MyApplication.quizPlayingPaused = true;
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

                if(MyApplication.quizModeOneCorrectAnswer) {
                    scoreTextView.setText(String.valueOf(++MyApplication.quizScore));
                    MyApplication.setIsPlaying(false);
                    playNextThing();
                } else {
                    gameOver();
                }
            }
        });

        falseAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.waitingForQuizAnswer = false;

                if(!MyApplication.quizModeOneCorrectAnswer) {
                    scoreTextView.setText(String.valueOf(++MyApplication.quizScore));
                    MyApplication.setIsPlaying(false);
                    playNextThing();
                } else {
                    gameOver();
                }


            }
        });


        MyApplication.isQuizModePlaying = true;

    }

    // Play next interval, chord or tone
    private void playNextThing() {
        if(MyApplication.waitingForQuizAnswer) {
            return;
        }

        // Reset this
        chordNameToShow = null;
        chordNumberOneToShow = null;
        chordNumberTwoToShow = null;

        MyApplication.quizIntervalToPlay = null;
        MyApplication.quizChordToPlay = null;

        // Randomly choose if correct answer will be true or false
        MyApplication.quizModeOneCorrectAnswer = rand.nextBoolean();

        // Get random low key for playing (inside borders)
        MyApplication.quizLowestKey = getRandomKey();

        // 12 tones in one octave
        int tempRandNumb = rand.nextInt(checkedIntervals + checkedChords + ((MyApplication.playWhatTone || MyApplication.playWhatOctave) ? 12 : 0));

        if(tempRandNumb < checkedIntervals) { // Play interval
            // Don't play čista prima if tone can be played
            MyApplication.quizIntervalToPlay = IntervalsList.getRandomCheckedInterval((MyApplication.playWhatTone || MyApplication.playWhatOctave) ? IntervalsList.getInterval(0) : null);
            if(MyApplication.quizIntervalToPlay == null) {
                // TODO: handle error
                return;
            }
            MyApplication.quizChordToPlay = null;

            // If answer is correct show intervals that is playing
            if(MyApplication.quizModeOneCorrectAnswer) {
                chordNameToShow = MyApplication.quizIntervalToPlay.getIntervalName();
            } else { // If not, show any other intervals, or chord if there is no intervals (or tone)
                setupWrongThingToShow();
            }

            playCurrentThing();

        } else if(tempRandNumb < checkedIntervals+checkedChords) { // Play chord
            MyApplication.quizChordToPlay = ChordsList.getRandomCheckedChord(null);
            if(MyApplication.quizChordToPlay == null) {
                // TODO: handle error
                return;
            }
            MyApplication.quizIntervalToPlay = null;

            // If answer is correct show chord that is playing
            if(MyApplication.quizModeOneCorrectAnswer) {
                chordNameToShow = MyApplication.quizChordToPlay.getChordName();
                chordNumberOneToShow = MyApplication.quizChordToPlay.getNumberOneAsString();
                chordNumberTwoToShow = MyApplication.quizChordToPlay.getNumberTwoAsString();
            } else { // If not, show any other intervals, or chord if there is no intervals (or tone)
                setupWrongThingToShow();
            }

            playCurrentThing();

        } else if(MyApplication.playWhatTone || MyApplication.playWhatOctave) {
            MyApplication.quizIntervalToPlay = null;
            MyApplication.quizChordToPlay = null;

            if(MyApplication.quizModeOneCorrectAnswer) {
                chordNameToShow = MyApplication.getKeyName(MyApplication.quizLowestKey);
            } else {
                setupWrongThingToShow();


            }

            playCurrentThing();

        } else {
            // TODO: handle error
            return;
        }

        showChord();

        MyApplication.waitingForQuizAnswer = true;
    }

    // TODO: add direction/mode support
    private void playCurrentThing() {
        if(MyApplication.quizIntervalToPlay != null) {
            MyApplication.playChord(new Interval[]{MyApplication.quizIntervalToPlay}, MyApplication.quizLowestKey);
        } else if(MyApplication.quizChordToPlay != null) {
            MyApplication.playChord(MyApplication.quizChordToPlay.getAllIntervals(), MyApplication.quizLowestKey);
        } else { // TODO: maybe not good to just play key, check if key need to play or not
            MyApplication.playKey(MyApplication.quizLowestKey);
        }
    }

    private void showChord() {
        if(chordNameToShow == null) {
            // TODO: handle error
            return;
        }
        MyApplication.updateTextView(chordTextView, chordNameToShow, chordNumOneTextView, chordNumberOneToShow, chordNumTwoTextView, chordNumberTwoToShow);
    }

    // TODO: random key -> key border is not checking if key sound is loaded
    private int getRandomKey() {
        return rand.nextInt(MyApplication.upKeyBorder-MyApplication.downKeyBorder)+MyApplication.downKeyBorder;
    }

    // TODO: add this
    // Are two keys inside same octave
    private boolean keysInSameOctave(int key1, int key2) {
        key1--;
        key2--;

        return (key1/12 == key2/12);
    }

    // TODO: add this
    // Are two keys same tones (octave not included)
    private boolean keysAreSameTone(int key1, int key2) {
        key1--;
        key2--;

        return (key1%12 == key2%12);
    }

    private void setupWrongThingToShow() {
        if(MyApplication.quizIntervalToPlay != null) {
            try {
                chordNameToShow = IntervalsList.getRandomCheckedInterval(MyApplication.quizIntervalToPlay).getIntervalName();
            } catch (Exception e) {
                chordNameToShow = null;
            }
        }

        if(MyApplication.quizChordToPlay != null) {
            try {
                Chord tempChord = ChordsList.getRandomCheckedChord(MyApplication.quizChordToPlay);
                chordNameToShow = tempChord.getChordName();
                chordNumberOneToShow = tempChord.getNumberOneAsString();
                chordNumberTwoToShow = tempChord.getNumberTwoAsString();
            } catch (Exception e) {
                chordNameToShow = null;
            }
        }

        if(MyApplication.quizIntervalToPlay == null && MyApplication.quizChordToPlay == null && (MyApplication.playWhatTone || MyApplication.playWhatOctave)) {
            int tempRandomStartFrom = getRandomKey();

            for(int i = tempRandomStartFrom; i < MyApplication.upKeyBorder; i++) {
                // If answer is not true, show that key
                if(!((MyApplication.playWhatOctave && keysInSameOctave(MyApplication.quizLowestKey, i)) || (MyApplication.playWhatTone && keysAreSameTone(MyApplication.quizLowestKey, i)))) {
                    chordNameToShow = MyApplication.getKeyName(i);
                    break;
                }
            }

            if(chordNameToShow == null) {
                // If key was not found, try going down instead of up
                for(int i = tempRandomStartFrom-1; i > MyApplication.downKeyBorder; i--) {
                    // If answer is not true, show that key
                    if(!((MyApplication.playWhatOctave && keysInSameOctave(MyApplication.quizLowestKey, i)) || (MyApplication.playWhatTone && keysAreSameTone(MyApplication.quizLowestKey, i)))) {
                        chordNameToShow = MyApplication.getKeyName(i);
                        break;
                    }
                }
            }

            if(chordNameToShow == null) {
                // TODO: handle error
                return;
            }
        }

        if(chordNameToShow == null) {
            // Try one without looking for what is playing
            setupWrongThingToShowSecondTry();
        }

        if(chordNameToShow == null) {
            // TODO: handle error
            return;
        }

    }

    private void setupWrongThingToShowSecondTry() {
        // If MyApplication.quizIntervalToPlay there will just be no exception
        try {
            chordNameToShow = IntervalsList.getRandomCheckedInterval(MyApplication.quizIntervalToPlay).getIntervalName();
        } catch (Exception e) {
            chordNameToShow = null;
        }

        if(MyApplication.playWhatTone || MyApplication.playWhatOctave && IntervalsList.getInterval(0).getIntervalName().equals(chordNameToShow)) {
            // If tones are playing and it shows čista prima, user cannot see the difference
            chordNameToShow = null;
        }

        if(chordNameToShow != null) {
            return;
        }

        // If MyApplication.quizChordToPlay there will just be no exception
        try {
            Chord tempChord = ChordsList.getRandomCheckedChord(MyApplication.quizChordToPlay);
            chordNameToShow = tempChord.getChordName();
            chordNumberOneToShow = tempChord.getNumberOneAsString();
            chordNumberTwoToShow = tempChord.getNumberTwoAsString();
        } catch (Exception e) {
            chordNameToShow = null;
        }

        if(chordNameToShow != null) {
            return;
        }

        if((MyApplication.playWhatTone || MyApplication.playWhatOctave)) {
            int tempRandomStartFrom = getRandomKey();

            for(int i = tempRandomStartFrom; i < MyApplication.upKeyBorder; i++) {
                // If answer is not true, show that key
                if(!((MyApplication.playWhatOctave && keysInSameOctave(MyApplication.quizLowestKey, i)) || (MyApplication.playWhatTone && keysAreSameTone(MyApplication.quizLowestKey, i)))) {
                    chordNameToShow = MyApplication.getKeyName(i);
                    break;
                }
            }

            if(chordNameToShow == null) {
                // If key was not found, try going down instead of up
                for(int i = tempRandomStartFrom-1; i > MyApplication.downKeyBorder; i--) {
                    // If answer is not true, show that key
                    if(!((MyApplication.playWhatOctave && keysInSameOctave(MyApplication.quizLowestKey, i)) || (MyApplication.playWhatTone && keysAreSameTone(MyApplication.quizLowestKey, i)))) {
                        chordNameToShow = MyApplication.getKeyName(i);
                        break;
                    }
                }
            }
        }


    }

    private void gameOver() {

    }

//    private boolean isChordCorrect() {
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append(chordTextView.getText()).append(chordNumOneTextView.getText()).append(chordNumTwoTextView.getText());
//
//        return (MyApplication.quizCorrectAnswerName.equals(stringBuilder.toString()));
//    }

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

    }

    @Override
    protected void onPause() {
        super.onPause();

        MyApplication.activityPaused();

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
                MyApplication.setIsPlaying(false);

                finish();
                return true;
            case R.id.action_options:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
