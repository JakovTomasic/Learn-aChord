package com.justchill.android.learnachord.quiz;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.justchill.android.learnachord.LocaleHelper;
import com.justchill.android.learnachord.MyApplication;
import com.justchill.android.learnachord.R;
import com.justchill.android.learnachord.settings.SettingsActivity;

public class ChooseQuizModeActivity extends AppCompatActivity {

    // TODO: add reset each mode's high score

    private View[] quizModeParentLayout;
    private View[] quizModeLinearLayout;
    private TextView[] quizModeTitleTV;
    private TextView[] quizModeDescriptionTV;

    private View parentLayout;

    private int height, width;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content of the activity to use the activity_choose_quiz_mode.xml layout file
        setContentView(R.layout.activity_choose_quiz_mode);
        setTitle(R.string.quiz);


        parentLayout = findViewById(R.id.activity_choose_quiz_mode_parent_layout);

        quizModeParentLayout = new View[] {
            findViewById(R.id.quiz_mode_one_parent_layout),
            findViewById(R.id.quiz_mode_two_parent_layout),
            findViewById(R.id.quiz_mode_three_parent_layout)
        };
        quizModeLinearLayout = new View[] {
            findViewById(R.id.quiz_mode_one_linear_layout),
            findViewById(R.id.quiz_mode_two_linear_layout),
            findViewById(R.id.quiz_mode_three_linear_layout),
        };
        quizModeTitleTV = new TextView[] {
            findViewById(R.id.quiz_mode_one_title_text_view),
            findViewById(R.id.quiz_mode_two_title_text_view),
            findViewById(R.id.quiz_mode_three_title_text_view)
        };
        quizModeDescriptionTV = new TextView[] {
            findViewById(R.id.quiz_mode_one_description_text_view),
            findViewById(R.id.quiz_mode_two_description_text_view),
            findViewById(R.id.quiz_mode_three_description_text_view)
        };


        height = MyApplication.smallerDisplayDimensionPX/2;
        width = height/3*2;

        for(int i = 0; i < quizModeParentLayout.length; i++) {
            setQuizModeSize(i);
        }


        quizModeParentLayout[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.quizScore = 0;
                Intent intent = new Intent(ChooseQuizModeActivity.this, ModeOneActivity.class);
                startActivity(intent);
            }
        });
        quizModeParentLayout[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.quizScore = 0;
                Intent intent = new Intent(ChooseQuizModeActivity.this, ModeTwoActivity.class);
                startActivity(intent);
            }
        });
        quizModeParentLayout[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.quizScore = 0;
                Intent intent = new Intent(ChooseQuizModeActivity.this, ModeThreeActivity.class);
                startActivity(intent);
            }
        });


        MyApplication.isQuizModePlaying = true;

    }

    private void setQuizModeSize(int modeID) {
        // Setup quizModeParentLayout size and padding
        ViewGroup.LayoutParams quizModeParentSizeRules = quizModeParentLayout[modeID].getLayoutParams();

//        quizModeParentLayout[modeID].setPadding(width/10, width/10, width/10, width/10);
        quizModeParentSizeRules.width = width;
        quizModeParentSizeRules.height = height;
        quizModeParentLayout[modeID].setLayoutParams(quizModeParentSizeRules);

        // Set padding for that layout
        quizModeLinearLayout[modeID].setPadding(width/10, width/10, width/10, width/10);



        // Setup quizModeTitleTV (text) size (and quizModeDescriptionTV size automatically)
        LinearLayout.LayoutParams quizModeTitleSizeRules = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        quizModeTitleSizeRules.setMargins(0, 0, 0, width/10);
        quizModeTitleTV[modeID].setLayoutParams(quizModeTitleSizeRules);

        quizModeTitleTV[modeID].setTextSize(TypedValue.COMPLEX_UNIT_PX, height/7);



        // Set text size for highscore
        quizModeDescriptionTV[modeID].setTextSize(TypedValue.COMPLEX_UNIT_PX, height/10);


    }

    private String readResource(int id) {
        return ChooseQuizModeActivity.this.getResources().getString(id);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, null));
    }

    @Override
    protected void onResume() {
        super.onResume();

        MyApplication.activityResumed(ChooseQuizModeActivity.this);
        MyApplication.quizPlayingPaused = true;

        // Set high scores
        quizModeDescriptionTV[0].setText(readResource(R.string.highscore) + ":\n" + String.valueOf(MyApplication.quizModeOneHighscore));
        quizModeDescriptionTV[1].setText(readResource(R.string.highscore) + ":\n" + String.valueOf(MyApplication.quizModeTwoHighscore));
        quizModeDescriptionTV[2].setText(readResource(R.string.highscore) + ":\n" + String.valueOf(MyApplication.quizModeThreeHighscore));

        MyApplication.quizChordNameToShow = "";
        MyApplication.quizChordNumberOneToShow = "";
        MyApplication.quizChordNumberTwoToShow = "";
        MyApplication.waitingForQuizAnswer = false;
    }

    @Override
    protected void onPause() {
        super.onPause();

        MyApplication.activityPaused();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.quiz_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_options:
                // TODO: handle this
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
