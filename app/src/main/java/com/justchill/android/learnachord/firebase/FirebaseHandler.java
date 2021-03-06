package com.justchill.android.learnachord.firebase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
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
import java.io.ByteArrayOutputStream;
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


    // Constants for storing witch photo to show
    public static final int IMAGE_TO_SET_DEFAULT_ID = 0;
    static final int IMAGE_TO_SET_GOOGLE_ID = 1;
    static final int IMAGE_TO_SET_FACEBOOK_ID = 2;
    static final int IMAGE_TO_SET_TWITTER_ID = 3;
    static final int IMAGE_TO_SET_FROM_PHONE_ID = 4;

    // Stores path to the photo chosen from gallery
    public static Uri photoFromPhonePath = null;
    // Stores witch photo to show
    public static int imageToSet = IMAGE_TO_SET_DEFAULT_ID;

    public static boolean isImageToSetValueValid(int value) {
        return (value == IMAGE_TO_SET_DEFAULT_ID || value == IMAGE_TO_SET_GOOGLE_ID || value == IMAGE_TO_SET_FACEBOOK_ID ||
                value == IMAGE_TO_SET_TWITTER_ID || value == IMAGE_TO_SET_FROM_PHONE_ID);
    }



    // Max dimension of the image for bitmap scaling
    private static final int MAX_IMAGE_DIMENSION = 1920;

    // Shows login screen to choose with witch platform to login
    public static void showLogInScreen(final Activity activity) {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.TwitterBuilder().build());

        // Create and launch sign-in intent
        activity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false)
                        .setAlwaysShowSignInMethodScreen(true)
                        .setTheme(R.style.loginActivityForResultTheme)
                        .build(),
                UserProfileActivity.RC_SIGN_IN);
    }

    // After user logs in, save user data
    public static void handleOnActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == UserProfileActivity.RC_SIGN_IN) {
            // Activity with result was firebase sign in activity

            // User has just tried to log in
            if (resultCode == Activity.RESULT_OK) {
                // User logged in successfully
                Toast.makeText(activity, MyApplication.readResource(R.string.login_successful_toast_message, null), Toast.LENGTH_SHORT).show();

                // Save user (for getting some of it's data later on)
                user.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


                // Create new user (refresh / delete all user data locally)
                FirebaseHandler.createNewUser();

                // Save that to DB
                DatabaseHandler.updateDatabaseOnSeparateThread();


            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        } else if (requestCode == UserProfileActivity.RC_GET_PICTURE) {
            // Activity with result was choose profile photo from gallery

            // User has just chosen new profile photo from gallery
            if (resultCode == Activity.RESULT_OK) {
                // Photo gotten successfully
                // Stream for getting photo
                InputStream imageStream = null;

                try {
                    // Save current profile photo size for scaling the new one
                    int currentProfileSize = activity.findViewById(R.id.user_profile_photo_circle_image_view).getHeight();

                    // Get path to the image
                    Uri imageUri = data.getData();
                    // Save path for later use
                    photoFromPhonePath = imageUri;


                    // Save photo properly sized and rotated
                    UserProfileActivity.fromPhonePhoto = getPhotoFromPhone(activity, imageUri, currentProfileSize);


                    // Save what photo to show and it's path into database
                    DatabaseHandler.setDoesDbNeedUpdate(true);

                    // Show photo in UI
                    setupUserPhoto();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        // Try to close image input stream after everything has been done
                        imageStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }


    // Erases all user specific data
    public static void createNewUser() {
        // Reset firebase user data (and achievements)
        FirebaseHandler.user = new User();

        // Reset all high scores
        DatabaseData.quizModeOneHighscore = 0;
        DatabaseData.quizModeTwoHighscore = 0;
        DatabaseData.quizModeThreeHighscore = 0;

        // Forget user photos
        UserProfileActivity.googlePhoto = null;
        UserProfileActivity.facebookPhoto = null;
        UserProfileActivity.twitterPhoto = null;
        UserProfileActivity.fromPhonePhoto = null;
    }

    // Returns gallery image from given uri, properly sized and rotated (and cropped)
    private static Bitmap scaleImage(Context context, Uri photoUri) throws Exception {
        // Get size data
        InputStream is = context.getContentResolver().openInputStream(photoUri);
        BitmapFactory.Options dbo = new BitmapFactory.Options();
        dbo.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, dbo);
        is.close();

        int rotatedWidth, rotatedHeight;
        int orientation = getOrientation(context, photoUri);

        // Rotate image
        if (orientation == 90 || orientation == 270) {
            rotatedWidth = dbo.outHeight;
            rotatedHeight = dbo.outWidth;
        } else {
            rotatedWidth = dbo.outWidth;
            rotatedHeight = dbo.outHeight;
        }

        // Get image from stream
        Bitmap srcBitmap;
        is = context.getContentResolver().openInputStream(photoUri);
        if (rotatedWidth > MAX_IMAGE_DIMENSION || rotatedHeight > MAX_IMAGE_DIMENSION) {
            float widthRatio = ((float) rotatedWidth) / ((float) MAX_IMAGE_DIMENSION);
            float heightRatio = ((float) rotatedHeight) / ((float) MAX_IMAGE_DIMENSION);
            float maxRatio = Math.max(widthRatio, heightRatio);

            // Create the bitmap from file
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = (int) maxRatio;
            srcBitmap = BitmapFactory.decodeStream(is, null, options);
        } else {
            srcBitmap = BitmapFactory.decodeStream(is);
        }
        is.close();

        // Crop image in center for equal width and height
        if (srcBitmap.getWidth() >= srcBitmap.getHeight()){

            srcBitmap = Bitmap.createBitmap(
                    srcBitmap,
                    srcBitmap.getWidth()/2 - srcBitmap.getHeight()/2,
                    0,
                    srcBitmap.getHeight(),
                    srcBitmap.getHeight()
            );

        }else{

            srcBitmap = Bitmap.createBitmap(
                    srcBitmap,
                    0,
                    srcBitmap.getHeight()/2 - srcBitmap.getWidth()/2,
                    srcBitmap.getWidth(),
                    srcBitmap.getWidth()
            );
        }

        /*
         * if the orientation is not 0 (or -1, which means we don't know), we
         * have to do a rotation.
         */
        if (orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);

            srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
                    srcBitmap.getHeight(), matrix, true);
        }

        // Compress bitmap
        String type = context.getContentResolver().getType(photoUri);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (type.equals("image/png")) {
            srcBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        } else if (type.equals("image/jpg") || type.equals("image/jpeg")) {
            srcBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        }
        byte[] bMapArray = baos.toByteArray();
        baos.close();

        return BitmapFactory.decodeByteArray(bMapArray, 0, bMapArray.length);
    }

    // Get orientation of the gallery image using given image uri
    private static int getOrientation(Context context, Uri photoUri) {
        /* it's on the external media. */
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);

        // If there is error, return -1 as orientation
        if (cursor == null || cursor.getCount() != 1) {
            return -1;
        }

        // Close the cursor and return result
        cursor.moveToFirst();
        int orientation = cursor.getInt(0);
        cursor.close();
        return orientation;
    }

    // Scale photo to match display size
    private static Bitmap scaleBitmap(Bitmap bitmap) {
        if(bitmap == null) {
            return null;
        }

        try {
            int bitmapSize = (int)(MyApplication.smallerDisplayDimensionPX/2.5f);
            return Bitmap.createScaledBitmap(bitmap, bitmapSize, bitmapSize, true);
        } catch (Exception ignored) {}

        return bitmap;
    }

    // Download image from url and scale if properly
    static Bitmap getImageBitmap(String url) {
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
            Log.e("FirebaseHandler", "Error getting bitmap", e);
        }

        return scaleBitmap(bm);
    }


    // Returns photo from phone
    private static Bitmap getPhotoFromPhone(Activity activity, Uri imageUri, Integer imageScaleSize) {
        if(imageUri == null || imageUri.toString().equals("")) {
            return null;
        }

        InputStream imageStream = null;
        try {
            // Create image stream to that path - for getting the image
            imageStream = activity.getContentResolver().openInputStream(imageUri);


            // return properly rotated photo
            if(imageScaleSize == null) {
                // If size value is null, image doesn't need to be sized
                return scaleBitmap(scaleImage(activity, imageUri));
            } else {
                // return photo that is properly sized
                return Bitmap.createScaledBitmap(
                        scaleImage(activity, imageUri), imageScaleSize, imageScaleSize, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                imageStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // If something went wrong, just return null
        return null;
    }


    // Download (if needed) and show user account photo in the UI
    public static void setupUserPhoto() {
        Thread getUserPhotoThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Reset user photo so we know if when something changes
                FirebaseHandler.user.photo = null;

                // Depending on what to show, handle that (and save photo that is showing in FirebaseHandler.user.photo)
                switch (imageToSet) {
                    case IMAGE_TO_SET_DEFAULT_ID:
                        // Photo is already null
                        break;
                    case IMAGE_TO_SET_GOOGLE_ID:
                        FirebaseHandler.user.photo = UserProfileActivity.googlePhoto;
                        break;
                    case IMAGE_TO_SET_FACEBOOK_ID:
                        FirebaseHandler.user.photo = UserProfileActivity.facebookPhoto;
                        break;
                    case IMAGE_TO_SET_TWITTER_ID:
                        FirebaseHandler.user.photo = UserProfileActivity.twitterPhoto;
                        break;
                    case IMAGE_TO_SET_FROM_PHONE_ID:
                        if(UserProfileActivity.fromPhonePhoto == null) {
                            // If phone photo is not set up, try to get if from gallery
                            try {
                                // Scale image to third of the smaller dimension of the display
                                UserProfileActivity.fromPhonePhoto = getPhotoFromPhone(MyApplication.getActivity(),
                                        FirebaseHandler.photoFromPhonePath, MyApplication.smallerDisplayDimensionPX/3);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        FirebaseHandler.user.photo = UserProfileActivity.fromPhonePhoto;
                        break;
                }

                // If photo was not saved, download it (if user is logged in)
                if(FirebaseHandler.user.firebaseUser != null && FirebaseHandler.user.photo == null && imageToSet != IMAGE_TO_SET_DEFAULT_ID) {
                    // Loop through all user profiles (from all providers) wanted photo is found
                    for(UserInfo profile : FirebaseHandler.user.firebaseUser.getProviderData()) {
                        // Get the profile photo's url
                        Uri photoUrl = profile.getPhotoUrl();

                        // If there is no photo data on this profile, try with next one
                        if(photoUrl == null) {
                            continue;
                        }

                        // Convert the Url to a String and store it into a variable
                        String photoPath = photoUrl.toString();

                        // Save photo if this profile is from the wanted provider
                        if(imageToSet == IMAGE_TO_SET_GOOGLE_ID && photoPath.toLowerCase().contains("google")) {
                            FirebaseHandler.user.photo = getImageBitmap(googleGetHighResPhotoPath(photoPath));
                            break;
                        }
                        if(imageToSet == IMAGE_TO_SET_FACEBOOK_ID && photoPath.toLowerCase().contains("facebook")) {
                            FirebaseHandler.user.photo = getImageBitmap(facebookGetHighResPhotoPath(profile.getUid()));
                            break;
                        }
                        if(imageToSet == IMAGE_TO_SET_TWITTER_ID && photoPath.toLowerCase().contains("twimg")) {
                            FirebaseHandler.user.photo = getImageBitmap(twitterGetHighResPhotoPath(photoPath));
                            break;
                        }

                    }
                }


                // If activity is visible, change the UI
                if(MyApplication.getActivity() != null) {
                    try {
                        // Try to display user profile photo
                        if(MyApplication.getActivity() instanceof UserProfileActivity) {
                            MyApplication.getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((UserProfileActivity) MyApplication.getActivity()).setProfilePhoto();
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }
        });
        getUserPhotoThread.start();
    }

    /*
     * Converts photo paths to high res photo path depending on what provider is the photo from
     */
    static String googleGetHighResPhotoPath(String photoPath) {
        // Variable holding the original String portion of the url that will be replaced
        String originalPieceOfUrl = "s96-c/";

        // Variable holding the new String portion of the url that does the replacing, to improve image quality
        String newPieceOfUrlToAdd = "s400-c/";

        // Replace the original part of the Url with the new part
        return photoPath.replace(originalPieceOfUrl, newPieceOfUrlToAdd);
    }
    static String facebookGetHighResPhotoPath(String facebookUserId) {
        // construct the URL to the profile picture, with a custom height
        // alternatively, use '?height=' instead of '?type=small|medium|large'
        return "https://graph.facebook.com/" + facebookUserId + "/picture?type=large";
    }
    static String twitterGetHighResPhotoPath(String photoPath) {
        // Remove part of the photoPath that redirects to lower resolution image
        return photoPath.replace("_normal", "");
    }



    // Read achievement progress data from could database and saves it if it's better that current one
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

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

                User.updateAchievementProgressInCloud = false;
            }
        });
        firestoreUpdateAchievementProgressInCloudThread.start();
    }


    // Read quiz high score data from could database and saves it if it's better that current one
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

                                        // Does cloud database need to be updated after we get all data
                                        boolean tempUpdateHighScoresInCloud = false;
                                        // Does local database need to be updated after we get all data
                                        boolean tempUpdateHighScoresLocally = false;

                                        /*
                                         * Read each high score and save it if it's greater than current one
                                         * If the current one is greater, write it to cloud
                                         */

                                        int tempHighScoreCloudValue;
                                        try {
                                            // Get quiz mode one value
                                            tempHighScoreCloudValue = Integer.parseInt(document.getData().get(highScoreKeys[0]).toString());

                                            if(DatabaseData.quizModeOneHighscore > tempHighScoreCloudValue) {
                                                tempUpdateHighScoresInCloud = true;
                                            } else if(DatabaseData.quizModeOneHighscore < tempHighScoreCloudValue) {
                                                tempUpdateHighScoresLocally = true;
                                            }
                                            // Save greater value
                                            DatabaseData.quizModeOneHighscore = Math.max(DatabaseData.quizModeOneHighscore, tempHighScoreCloudValue);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        try {
                                            // Get quiz mode one value
                                            tempHighScoreCloudValue = Integer.parseInt(document.getData().get(highScoreKeys[1]).toString());

                                            if(DatabaseData.quizModeTwoHighscore > tempHighScoreCloudValue) {
                                                tempUpdateHighScoresInCloud = true;
                                            } else if(DatabaseData.quizModeTwoHighscore < tempHighScoreCloudValue) {
                                                tempUpdateHighScoresLocally = true;
                                            }
                                            // Save greater value
                                            DatabaseData.quizModeTwoHighscore = Math.max(DatabaseData.quizModeTwoHighscore, tempHighScoreCloudValue);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        try {
                                            // Get quiz mode one value
                                            tempHighScoreCloudValue = Integer.parseInt(document.getData().get(highScoreKeys[2]).toString());

                                            if(DatabaseData.quizModeThreeHighscore > tempHighScoreCloudValue) {
                                                tempUpdateHighScoresInCloud = true;
                                            } else if(DatabaseData.quizModeThreeHighscore < tempHighScoreCloudValue) {
                                                tempUpdateHighScoresLocally = true;
                                            }
                                            // Save greater value
                                            DatabaseData.quizModeThreeHighscore = Math.max(DatabaseData.quizModeThreeHighscore, tempHighScoreCloudValue);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }


                                        if(tempUpdateHighScoresInCloud) {
                                            // Update high scores in cloud database
                                            updateHighScoreInCloud();
                                        }

                                        if(tempUpdateHighScoresLocally) {
                                            // Update high scores in local database
                                            DatabaseHandler.updateDatabaseOnSeparateThread();
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

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

                User.updateHighScoresInCloud = false;
            }
        });
        firestoreUpdateHighScoreInCloudThread.start();
    }

}

