package com.justchill.android.learnachord.quiz;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import com.justchill.android.learnachord.database.DataContract;
import com.justchill.android.learnachord.database.DatabaseData;
import com.justchill.android.learnachord.database.DatabaseHandler;
import com.justchill.android.learnachord.firebase.FirebaseHandler;

// Activity where user can choose what quiz he will play
public class ChooseQuizModeActivity extends AppCompatActivity {

    // Parent of quiz mode button, background is button texture
    private View[] quizModeParentLayout;
    // Linear layout for good positioning
    private View[] quizModeLinearLayout;
    // TextView for quiz name/title
    private TextView[] quizModeTitleTV;
    // TextView for quiz description (record at the moment)
    private TextView[] quizModeDescriptionTV;

    // Parent of whole activity
    private View parentLayout;

    // Dimensions of each quiz mode button
    private int height, width;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content of the activity to use the activity_choose_quiz_mode.xml layout file
        setContentView(R.layout.activity_choose_quiz_mode);
        setTitle(R.string.quiz);

        // Change media volume when volume buttons are pressed
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        parentLayout = findViewById(R.id.activity_choose_quiz_mode_parent_layout);


        // Get all elements for all quiz modes

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

        // Set size for all modes
        for(int i = 0; i < quizModeParentLayout.length; i++) {
            setQuizModeSize(i);
        }

        // On quiz mode button click, open that quiz mode
        quizModeParentLayout[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuizData.quizScore = 0;
                Intent intent = new Intent(ChooseQuizModeActivity.this, ModeOneActivity.class);
                startActivity(intent);
            }
        });
        quizModeParentLayout[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuizData.quizScore = 0;
                Intent intent = new Intent(ChooseQuizModeActivity.this, ModeTwoActivity.class);
                startActivity(intent);
            }
        });
        quizModeParentLayout[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuizData.quizScore = 0;
                Intent intent = new Intent(ChooseQuizModeActivity.this, ModeThreeActivity.class);
                startActivity(intent);
            }
        });


    }

    // Sets up sizes for quiz mode buttons (for scaling, for all device display support)
    private void setQuizModeSize(int modeID) {
        // Setup quizModeParentLayout size and padding
        ViewGroup.LayoutParams quizModeParentSizeRules = quizModeParentLayout[modeID].getLayoutParams();

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

        quizModeTitleTV[modeID].setTextSize(TypedValue.COMPLEX_UNIT_PX, height/8f);



        // Set text size for highscore
        quizModeDescriptionTV[modeID].setTextSize(TypedValue.COMPLEX_UNIT_PX, height/10f);


    }

    // Set new text to high score TextView when high score has been changed
    @SuppressLint("SetTextI18n")
    public void resetQuizHighScoreViews() {
        quizModeDescriptionTV[0].setText(readResource(R.string.highscore) + ":\n" + String.valueOf(DatabaseData.quizModeOneHighscore));
        quizModeDescriptionTV[1].setText(readResource(R.string.highscore) + ":\n" + String.valueOf(DatabaseData.quizModeTwoHighscore));
        quizModeDescriptionTV[2].setText(readResource(R.string.highscore) + ":\n" + String.valueOf(DatabaseData.quizModeThreeHighscore));
    }

    // Private method for getting string from resources
    private String readResource(int id) {
        return ChooseQuizModeActivity.this.getResources().getString(id);
    }

    // For different languages support
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, null));
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Stop all sounds in case user exited a quiz when sound was laying
        MyApplication.setIsPlaying(false);


        QuizData.isQuizModePlaying = true;

        MyApplication.activityResumed(ChooseQuizModeActivity.this);
        QuizData.quizPlayingPaused = true;

        // Set high scores
        resetQuizHighScoreViews();

        // Reset all quiz stuff in case user just exited quiz
        QuizData.quizChordNameToShow = "";
        QuizData.quizChordNumberOneToShow = "";
        QuizData.quizChordNumberTwoToShow = "";
        QuizData.waitingForQuizAnswer = false;
        QuizData.quizPlayingCurrentThing = false;

        QuizData.quizModeThreeShowSubmitButton = false;

        /*
         * Show initial help dialog for this activity if it hasn't been showed yet
         * (if this is the first time user opened this activity)
         */
        if(DatabaseData.quizActivityHelpShowed == DataContract.UserPrefEntry.BOOLEAN_FALSE) {
            showQuizActivityExplanationDialog();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Update database if new high score has been set
        if(DatabaseHandler.doesDbNeedUpdate()) {
            // Local DB
            DatabaseHandler.updateDatabaseOnSeparateThread();
            // Firestore DB
            FirebaseHandler.updateHighScoreInCloud();
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
            case R.id.action_more_info: // Open help dialog
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

    // Show dialog that is explaining all quiz modes
    private void showQuizExplanationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners for the positive (ok) button on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(ChooseQuizModeActivity.this);
        builder.setTitle(R.string.quiz_explanation_dialog_title);
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

    // Show "are you sure you want to delete high score..." dialog
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
                        DatabaseData.quizModeOneHighscore = 0;
                        break;
                    case 2:
                        DatabaseData.quizModeTwoHighscore = 0;
                        break;
                    case 3:
                        DatabaseData.quizModeThreeHighscore = 0;
                        break;
                }

                DatabaseHandler.setDoesDbNeedUpdate(true);
                FirebaseHandler.updateHighScoreInCloud();
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


    // Dialog explains what quiz activity does. It automatically opens when user starts the app for the first time
    private void showQuizActivityExplanationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listener for the positive button on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.quiz_activity_explanation_dialog_title);
        builder.setMessage(R.string.quiz_activity_explanation_dialog_text);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // When ok is clicked, close the dialog
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });


        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();


        // Save to the database (and as variable in app) that this dialog has been showed if this is the first time
        if(DatabaseData.quizActivityHelpShowed != DataContract.UserPrefEntry.BOOLEAN_TRUE) {
            DatabaseData.quizActivityHelpShowed = DataContract.UserPrefEntry.BOOLEAN_TRUE;
            DatabaseHandler.updateDatabaseOnSeparateThread();
        }

    }


}
