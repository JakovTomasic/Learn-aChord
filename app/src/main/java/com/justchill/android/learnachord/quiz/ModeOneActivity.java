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
import com.justchill.android.learnachord.MyApplication;
import com.justchill.android.learnachord.R;

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


    private final int timeLeftToPlayProgressThicknessDB = 4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_quiz_mode_one);
        setTitle(readResource(R.string.quiz) + readResource(R.string.quiz_mode_title_separator) + readResource(R.string.quiz_mode_one_title));


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

        chordTextView.setText("");
        chordNumOneTextView.setText("");
        chordNumTwoTextView.setText("");

        startClickableImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickStartButton();
            }
        });


        // Setup pause button size
        ViewGroup.LayoutParams pauseImageViewSizeRules = pauseClickableImageView.getLayoutParams();

        // Logic copied from MainActivity
        height_width_value = (int)(MyApplication.smallerDisplayDimensionPX * 0.75 / 5.3125);
        pauseImageViewSizeRules.width = height_width_value;
        pauseImageViewSizeRules.height = height_width_value;
        pauseClickableImageView.setLayoutParams(pauseImageViewSizeRules);

        if(MyApplication.isPlaying()) {
            startClickableImageView.setVisibility(View.INVISIBLE);
            startClickableImageView.setClickable(false);
            startClickableImageView.setFocusable(false);

            pauseClickableImageView.setVisibility(View.VISIBLE);
            pauseClickableImageView.setClickable(true);
            pauseClickableImageView.setFocusable(true);
        } else {
            startClickableImageView.setVisibility(View.VISIBLE);
            startClickableImageView.setClickable(true);
            startClickableImageView.setFocusable(true);

            pauseClickableImageView.setVisibility(View.INVISIBLE);
            pauseClickableImageView.setClickable(false);
            pauseClickableImageView.setFocusable(false);
        }

        if(!MyApplication.isLoadingFinished) {
            startClickableImageView.setClickable(false);
            startClickableImageView.setFocusable(false);
        }


        pauseClickableImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickPauseButton();
            }
        });


        MyApplication.addMainActivityListener(new MyApplication.MainActivityListener() {
            @Override
            public void onIsPlayingChange() {

            }

            @Override
            public void onLoadingFinished() {
                startClickableImageView.setClickable(true);
                startClickableImageView.setFocusable(true);
            }
        });




        // Setup true and false buttons listeners
        trueAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!MyApplication.waitingForQuizAnswer) {
                    return;
                }

                MyApplication.waitingForQuizAnswer = false;
                if(isChordCorrect()) {
                    scoreTextView.setText(String.valueOf(++MyApplication.quizScore));
                    MyApplication.setIsPlaying(false);
                    clickStartButton();
                } else {

                }
            }
        });

        falseAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!MyApplication.waitingForQuizAnswer) {
                    return;
                }

                MyApplication.waitingForQuizAnswer = false;
                if(!isChordCorrect()) {
                    scoreTextView.setText(String.valueOf(++MyApplication.quizScore));
                    MyApplication.setIsPlaying(false);
                    clickStartButton();
                } else {

                }
            }
        });


        MyApplication.isQuizModePlaying = true;

    }

    private void clickStartButton() {
        startClickableImageView.setVisibility(View.INVISIBLE);
        startClickableImageView.setClickable(false);
        startClickableImageView.setFocusable(false);

        pauseClickableImageView.setVisibility(View.VISIBLE);
        pauseClickableImageView.setClickable(true);
        pauseClickableImageView.setFocusable(true);


        if(!MyApplication.isPlaying()) {
            MyApplication.setIsPlaying(true);
        }
        chordTextViewLayout.setVisibility(View.VISIBLE);
    }

    private void clickPauseButton() {
        startClickableImageView.setVisibility(View.VISIBLE);
        startClickableImageView.setClickable(true);
        startClickableImageView.setFocusable(true);

        pauseClickableImageView.setVisibility(View.INVISIBLE);
        pauseClickableImageView.setClickable(false);
        pauseClickableImageView.setFocusable(false);


        chordTextViewLayout.setVisibility(View.INVISIBLE);
    }

    private boolean isChordCorrect() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(chordTextView.getText()).append(chordNumOneTextView.getText()).append(chordNumTwoTextView.getText());

        return (MyApplication.quizCorrectAnswerName.equals(stringBuilder.toString()));
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
