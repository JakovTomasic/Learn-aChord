package com.justchill.android.learnachord.firebase;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.justchill.android.learnachord.MyApplication;
import com.justchill.android.learnachord.R;
import com.justchill.android.learnachord.database.DatabaseData;
import com.justchill.android.learnachord.database.DatabaseHandler;
import com.justchill.android.learnachord.quiz.ChooseQuizModeActivity;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Handles user account and achievements
public class FirebaseHandler {

    // Stores all current user data
    public static User user = null;


    // TODO: organize layouts and drawables in packages


    // Shows login screen to choose with witch platform to login
    static void showLogInScreen(final Activity activity, int temp_RC_SIGN_IN) {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        activity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
//                        .setAlwaysShowSignInMethodScreen(true)
                        .build(),
                temp_RC_SIGN_IN);
    }

    // After user logs in, save user data
    static void handleOnActivityResult(Activity activity, int temp_RC_SIGN_IN, int requestCode, int resultCode, Intent data) {
        if (requestCode == temp_RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(activity, "Login successful", Toast.LENGTH_SHORT).show();

                // Successfully logged in
                user.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                // Get data from database
                User.updateAchievementProgress = true;

            } else {
                // TODO: handle this error
                Toast.makeText(activity, "Login failed", Toast.LENGTH_SHORT).show();
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

    // Download image from url
    private static Bitmap getImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e("###", "Error getting bitmap", e);
        }
        return bm;
    }

    // Downloads and set on UI user account photo
    public static void setupUserPhoto() {
        Thread getUserPhotoThread = new Thread(new Runnable() {
            @Override
            public void run() {
                if(FirebaseHandler.user.firebaseUser == null) {
                    return;
                }

                // Loop through all data (if there is more of them) and save user profile photo as bitmap
                for(UserInfo profile : FirebaseHandler.user.firebaseUser.getProviderData()) {
                    final Uri photoUrl = profile.getPhotoUrl();

                    if(photoUrl == null) {
                        continue;
                    }

                    FirebaseHandler.user.photo = getImageBitmap(photoUrl.toString());
                }

                // If there is no activity, don't change UI
                if(MyApplication.getActivity() == null) {
                    return;
                }

                try {
                    // Try to display user profile photo
                    if(MyApplication.getActivity() instanceof UserProfileActivity) {
                        MyApplication.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                UserProfileActivity.setProfilePhoto(MyApplication.getActivity());
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        getUserPhotoThread.start();
    }

    // Read achievement progress data from could database
    public static void updateAchievementProgress() {
        Thread updateAchievementProgressThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // If user is logged out, data cannot be read
                if(FirebaseHandler.user.firebaseUser == null) {
                    return;
                }

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // Get all achievement data that belongs to logged in user (data is saved on firestore)
                db.collection(FirebaseHandler.user.firebaseUser.getUid()).document(MyApplication.readResource(R.string.firestore_achievement_progress_document_name, null))
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document != null && document.exists()) {

                                        // Get all achievement' progress keys (column names)
                                        String[] achievementProgressKeys = MyApplication.getAppContext().getResources().getStringArray(R.array.achievement_progress_keys);

                                        // Does cloud database need to be updated after we get all data
                                        boolean tempUpdateAchievementProgressInCloud = false;
                                        // Does local database need to be updated after we get all data
                                        boolean tempUpdateAchievementProgressLocally = false;

                                        // Loop through all that data and save it
                                        for (int i = 0; i < achievementProgressKeys.length; i++) {
                                            try {
                                                Integer tempStoringValue = Integer.parseInt(document.getData().get(achievementProgressKeys[i]).toString());

                                                // If data in cloud database is smaller (older) update cloud database (after we get all data)
                                                if(tempStoringValue < FirebaseHandler.user.achievementProgress.get(i)) {
                                                    tempUpdateAchievementProgressInCloud = true;
                                                } else if(tempStoringValue > FirebaseHandler.user.achievementProgress.get(i)) {
                                                    // If the data is greater (newer) update local database
                                                    tempUpdateAchievementProgressLocally = true;
                                                }

                                                // Save data that is newer (greater)
                                                FirebaseHandler.user.setAchievementProgress(i, tempStoringValue);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        if(tempUpdateAchievementProgressInCloud) {
                                            // Update achievements in cloud database
                                            firestoreUpdateAchievementProgressInCloud();
                                        }

                                        if(tempUpdateAchievementProgressLocally) {
                                            // Update achievements in local database
                                            DatabaseHandler.updateDatabaseOnSeparateThread();
                                        }

                                        // Data reading is done
                                        User.updateAchievementProgress = false;

                                        // Try to show new data in UI
                                        try {
                                            if(MyApplication.getActivity() instanceof UserProfileActivity) {
                                                MyApplication.getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        UserProfileActivity.refreshAchievementProgressUI(MyApplication.getActivity());
                                                    }
                                                });
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }


                                    } else {
                                        Log.d("###", "No such document");
                                        // If there is no such document, create it
                                        firestoreUpdateAchievementProgressInCloud();
                                    }
                                } else {
                                    Log.d("###", "get failed with ", task.getException());
                                }
                            }
                        });

            }
        });
        updateAchievementProgressThread.start();
    }

    // Write achievement progress data to cloud database
    public static void firestoreUpdateAchievementProgressInCloud() {
        Thread firestoreUpdateAchievementProgressInCloudThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // If user is logged out, data cannot be written
                if(FirebaseHandler.user.firebaseUser == null) {
                    return;
                }

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // Set all values inside map
                Map<String, Integer> mapOfValues = new HashMap<>();

                // Get all achievement' progress keys (column names)
                String[] achievementProgressKeys = MyApplication.getAppContext().getResources().getStringArray(R.array.achievement_progress_keys);

                // Loop through all that data and add it to mapOfValues
                for (int i = 0; i < achievementProgressKeys.length; i++) {
                    mapOfValues.put(achievementProgressKeys[i], FirebaseHandler.user.achievementProgress.get(i));
                }

                // Get achievement data from firestore database
                // SetOptions.merge() -> merges new input with old data (needed for not erasing old data if unspecified)
                db.collection(FirebaseHandler.user.firebaseUser.getUid()).document(MyApplication.readResource(R.string.firestore_achievement_progress_document_name, null))
                        .set(mapOfValues, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // TODO: handle success
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // TODO: handle failure
                            }
                        });

                User.updateAchievementProgressInCloud = false;
            }
        });
        firestoreUpdateAchievementProgressInCloudThread.start();
    }


    // Read quiz high score data from could database
    public static void firestoreUpdateHighScore() {
        Thread updateHighScoreThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // If user is logged out, data cannot be read
                if(FirebaseHandler.user.firebaseUser == null) {
                    return;
                }

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // Get all high score data that belongs to logged in user (data is saved on firestore)
                db.collection(FirebaseHandler.user.firebaseUser.getUid()).document(MyApplication.readResource(R.string.firestore_high_score_document_name, null))
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document != null && document.exists()) {

                                        // Get all achievement' progress keys (column names)
                                        String[] highScoreKeys = MyApplication.getAppContext().getResources().getStringArray(R.array.high_score_database_keys);

                                        // Loop through all that data and save it
                                        for (String key : highScoreKeys) {
                                            try {
                                                // Get and save greater data
                                                DatabaseData.quizModeOneHighscore = Math.max(DatabaseData.quizModeOneHighscore,
                                                        Integer.parseInt(document.getData().get(key).toString()));
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        // Try to show new data in UI
                                        try {
                                            if(MyApplication.getActivity() instanceof ChooseQuizModeActivity) {
                                                MyApplication.getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        ((ChooseQuizModeActivity) MyApplication.getActivity()).resetQuizHighScoreViews();
                                                    }
                                                });
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }


                                    } else {
                                        Log.d("###", "No such document");
                                        // If there is no such document, create it
                                        updateHighScoreInCloud();
                                    }
                                } else {
                                    Log.d("###", "get failed with ", task.getException());
                                }
                            }
                        });

            }
        });
        updateHighScoreThread.start();
    }

    // Write quiz high score progress data to cloud database
    public static void updateHighScoreInCloud() {
        Thread firestoreUpdateHighScoreInCloudThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // If user is logged out, data cannot be written
                if(FirebaseHandler.user.firebaseUser == null) {
                    return;
                }

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // Set all values inside map
                Map<String, Integer> mapOfValues = new HashMap<>();

                // Get all achievement' progress keys (column names)
                String[] highScoreKeys = MyApplication.getAppContext().getResources().getStringArray(R.array.high_score_database_keys);

                // Add all data to mapOfValues
                mapOfValues.put(highScoreKeys[0], DatabaseData.quizModeOneHighscore);
                mapOfValues.put(highScoreKeys[1], DatabaseData.quizModeTwoHighscore);
                mapOfValues.put(highScoreKeys[2], DatabaseData.quizModeThreeHighscore);

                // Get high score data from firestore database
                // SetOptions.merge() -> merges new input with old data (needed for not erasing old data if unspecified)
                db.collection(FirebaseHandler.user.firebaseUser.getUid()).document(MyApplication.readResource(R.string.firestore_high_score_document_name, null))
                        .set(mapOfValues, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // TODO: handle success
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // TODO: handle failure
                            }
                        });

                User.updateHighScoresInCloud = false;
            }
        });
        firestoreUpdateHighScoreInCloudThread.start();
    }

}

