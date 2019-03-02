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
import com.justchill.android.learnachord.database.DataContract;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseHandler {

    public static User user = null;


    // TODO: organize layouts and drawables in packages


    static void showLogInScreen(final Activity activity, int temp_RC_SIGN_IN) {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        activity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
//                        .setIsSmartLockEnabled(false)
//                        .setAlwaysShowSignInMethodScreen(true)
                        .build(),
                temp_RC_SIGN_IN);
    }

    static void handleOnActivityResult(Activity activity, int temp_RC_SIGN_IN, int requestCode, int resultCode, Intent data) {
        if (requestCode == temp_RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(activity, "Login successful", Toast.LENGTH_SHORT).show();

                // Successfully signed in
                user.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                // ...

            } else {
                Toast.makeText(activity, "Login failed", Toast.LENGTH_SHORT).show();
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }


    // TODO: rename variables
    static void readData(final String firstChild, final String secondChild, final String valueName, final Activity activity) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(firstChild).document(secondChild)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                Log.d("###", "DocumentSnapshot data: " + document.getData().get(valueName));
                                try {
                                    user.value1 = document.getData().get("dataValue1").toString();
                                    user.value2 = document.getData().get("dataValue2").toString();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Log.d("###", "No such document");
                            }
                        } else {
                            Log.d("###", "get failed with ", task.getException());
                        }

                        try {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    UserProfileActivity.refreshData(activity);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

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

    public static void setupUserPhoto() {
        Thread getUserPhotoThread = new Thread(new Runnable() {
            @Override
            public void run() {
                if(FirebaseHandler.user.firebaseUser == null) {
                    return;
                }

                for(UserInfo profile : FirebaseHandler.user.firebaseUser.getProviderData()) {
                    final Uri photoUrl = profile.getPhotoUrl();

                    if(photoUrl == null) {
                        continue;
                    }

                    FirebaseHandler.user.photo = getImageBitmap(photoUrl.toString());
                }

                if(MyApplication.getActivity() == null) {
                    return;
                }

                try {
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
                if(FirebaseHandler.user.firebaseUser == null) {
                    return;
                }

                // TODO: finish
                FirebaseFirestore db = FirebaseFirestore.getInstance();

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

                                        // Loop through all that data and save it
                                        for (int i = 0; i < achievementProgressKeys.length; i++) {
                                            try {
                                                FirebaseHandler.user.achievementProgress.set(i,
                                                        Math.max(FirebaseHandler.user.achievementProgress.get(i),
                                                                Integer.parseInt(document.getData().get(achievementProgressKeys[i]).toString())));
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        FirebaseHandler.user.updateAchievementProgress = false;

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
                                        // If there is no suck document, create it
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

    // Write achievement progress data to could database
    public static void firestoreUpdateAchievementProgressInCloud() {
        Thread firestoreUpdateAchievementProgressInCloudThread = new Thread(new Runnable() {
            @Override
            public void run() {
                if(FirebaseHandler.user.firebaseUser == null) {
                    return;
                }

                // TODO: finish
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                Map<String, Integer> mapOfValues = new HashMap<>();

                // Get all achievement' progress keys (column names)
                String[] achievementProgressKeys = MyApplication.getAppContext().getResources().getStringArray(R.array.achievement_progress_keys);

                // Loop through all that data and add it to mapOfValues
                for (int i = 0; i < achievementProgressKeys.length; i++) {
                    mapOfValues.put(achievementProgressKeys[i], FirebaseHandler.user.achievementProgress.get(i));
                }

                // SetOptions.merge() -> merges new input with old data (needed for not erasing old unspecified data)
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
            }
        });
        firestoreUpdateAchievementProgressInCloudThread.start();
    }


    // TODO: rename variables
    static void writeData(final String firstChild, final String secondChild, final String valueName, String value) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> mapOfValues = new HashMap<>();
        mapOfValues.put(valueName, value);

        // SetOptions.merge() -> merges new input with old data (needed for not erasing old unspecified data)
        db.collection(firstChild).document(secondChild)
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
    }


}

