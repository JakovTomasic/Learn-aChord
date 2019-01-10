package com.justchill.android.learnachord.quiz;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

public class ChooseQuizModeActivity extends AppCompatActivity {

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

    private void resetQuizHighScoreViews() {
        quizModeDescriptionTV[0].setText(readResource(R.string.highscore) + ":\n" + String.valueOf(MyApplication.quizModeOneHighscore));
        quizModeDescriptionTV[1].setText(readResource(R.string.highscore) + ":\n" + String.valueOf(MyApplication.quizModeTwoHighscore));
        quizModeDescriptionTV[2].setText(readResource(R.string.highscore) + ":\n" + String.valueOf(MyApplication.quizModeThreeHighscore));
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

        // Stop all sounds in case user exited a quiz when sound was laying
        MyApplication.setIsPlaying(false);

        MyApplication.activityResumed(ChooseQuizModeActivity.this);
        MyApplication.quizPlayingPaused = true;

        // Set high scores
        resetQuizHighScoreViews();

        MyApplication.quizChordNameToShow = "";
        MyApplication.quizChordNumberOneToShow = "";
        MyApplication.quizChordNumberTwoToShow = "";
        MyApplication.waitingForQuizAnswer = false;
        MyApplication.quizPlayingCurrentThing = false;

        MyApplication.quizModeThreeShowSubmitButton = false;
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Update database if new high score has been set
        if(MyApplication.doesDbNeedUpdate()) {
            MyApplication.updateDatabaseOnSeparateThread();
        }

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
            case R.id.action_details:
                showQuizExplanationDialog();
                return true;
            case R.id.action_reset_mode_one:
                showQuizHighScoreDeleteDialog(1);
                return true;
            case R.id.action_reset_mode_two:
                showQuizHighScoreDeleteDialog(2);
                return true;
            case R.id.action_reset_mode_three:
                showQuizHighScoreDeleteDialog(3);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showQuizExplanationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(ChooseQuizModeActivity.this);
        builder.setMessage(R.string.quiz_explanation_dialog_text);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if(dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showQuizHighScoreDeleteDialog(final int quizMode) {
        // Create an AlertDialog.Builder and set the message, and click listeners for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(ChooseQuizModeActivity.this);
        switch (quizMode) {
            case 1:
                builder.setMessage(R.string.quiz_reset_mode_one_highscore_message);
                break;
            case 2:
                builder.setMessage(R.string.quiz_reset_mode_two_highscore_message);
                break;
            case 3:
                builder.setMessage(R.string.quiz_reset_mode_three_highscore_message);
                break;
        }
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                switch (quizMode) {
                    case 1:
                        MyApplication.quizModeOneHighscore = 0;
                        break;
                    case 2:
                        MyApplication.quizModeTwoHighscore = 0;
                        break;
                    case 3:
                        MyApplication.quizModeThreeHighscore = 0;
                        break;
                }

                MyApplication.setDoesDbNeedUpdate(true);
                resetQuizHighScoreViews();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if(dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }



}
