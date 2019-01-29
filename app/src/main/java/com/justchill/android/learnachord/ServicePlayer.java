package com.justchill.android.learnachord;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.justchill.android.learnachord.chord.Chord;
import com.justchill.android.learnachord.chord.ChordsList;
import com.justchill.android.learnachord.chord.Interval;
import com.justchill.android.learnachord.chord.IntervalsList;
import com.justchill.android.learnachord.database.DataContract;
import com.justchill.android.learnachord.database.DatabaseData;
import com.justchill.android.learnachord.quiz.QuizData;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;

public class ServicePlayer extends Service {

    // All sounds downloaded from http://theremin.music.uiowa.edu/MISpiano.html (ff sounds)

    private static SoundPool soundPool;
    private int[] keySounds = new int[DataContract.UserPrefEntry.NUMBER_OF_KEYS]; // Now, there are 61 key sounds in raw R directory
    private boolean[] keySoundLoaded = new boolean[DataContract.UserPrefEntry.NUMBER_OF_KEYS];
    private final int soundsToLoadBeforePlaying = 25; // TODO: optimize this, loading sounds
    private boolean areAllSoundsLoaded = false;

    private AudioManager audioManager;

    private Interval currentInterval;
    private Chord currentChord;

    private int intervalsPlayedFor;
    private int chordsPlayedFor;
    private int toneNotPlayedFor;

    private Thread thread = null;

    // Manages what is being played, ID of current player so all previous know they need to shut the * up
    private int playingID = 0;

    private int lastKey = -1;
    private int directionUpNotPlayedFor = 0;
    private int directionDownNotPlayedFor = 0;
    private int directionTogetherNotPlayedFor = 0;

    // This is needed for refreshing activity
    private int globalNumberOfIntervalsToShow = 0;
    private int globalDirectionToPlay = 0;
    private boolean globalShowWhatIntervals = false;

    private AudioAttributes audioAttributes;
    private AudioFocusRequest audioFocusRequest;

    private int milisecPassed = 0;
    private int maxMilisecToPass = 0;

    // For settings what to play next
    final int PLAY_INTERVAL = 0;
    final int PLAY_CHORD = 1;
    final int PLAY_TONE = 2;

    private ProgressBar progressBar = null;

    private ProgressBarAnimation progressBarAnimation;
    // max displaying value for progress bar
    private int max = 75;

    AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                // Pause playback, in this case just stop it
                stop();
                MyApplication.setIsPlaying(false);
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // Resume playback
                if(MyApplication.isPlaying()) {
                    play((int) DatabaseData.tonesSeparationTime, (int) DatabaseData.delayBetweenChords, ++playingID);
                }
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                // Stop playback
                stop();
                MyApplication.setIsPlaying(false);
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Toast.makeText(getApplicationContext(), "onStartCommand bind", Toast.LENGTH_SHORT).show();

        // Create and setup {@ling AudioManager} to request audio focus
        audioManager = (AudioManager) this.getBaseContext().getSystemService(Context.AUDIO_SERVICE);

        // TODO: 5 is number of sounds that can be played at same time, replace it with 6 when bigger chords are added (in 2 places)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // >= api 21
            audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN) // This is for later
                        .setAudioAttributes(audioAttributes)
                        .setAcceptsDelayedFocusGain(false)
                        .setWillPauseWhenDucked(true)
                        .setOnAudioFocusChangeListener(afChangeListener)
                        .build();
            }

            soundPool = new SoundPool.Builder().setMaxStreams(5).setAudioAttributes(audioAttributes).build();
        } else {
            soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0); // sryQuality = 0 is never implemented, can be any#
        }

//        Thread debug = new Thread() {
//            @Override
//            public void run() {
//                for(int i = 0; i < 1000; i++) {
//                    Log.d("####", "######################## " + MyApplication.isPlaying() + ", " + MyApplication.isChordOrIntervalPlaying());
//                    try {
//                        Thread.sleep(2000);
//                    } catch (InterruptedException e2) {
//                        e2.printStackTrace();
//                    }
//                }
//            }
//        };
//        debug.start();

        Thread soundPoolSetupThread = new Thread() {
            @Override
            public void run() {
                for(int i = 0; i < DataContract.UserPrefEntry.NUMBER_OF_KEYS; i++) {
                    keySoundLoaded[i] = false;
                }
                loadSound(DatabaseData.downKeyBorder, Math.min(DatabaseData.downKeyBorder+soundsToLoadBeforePlaying,
                        DataContract.UserPrefEntry.NUMBER_OF_KEYS), true);

                do {
                    try {
                        MyApplication.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MyApplication.loadingFinished();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {}
                } while(!MyApplication.isLoadingFinished);


                updateProgressBarAnimation(null, null);

                loadSound(1, DataContract.UserPrefEntry.NUMBER_OF_KEYS, false);
                areAllSoundsLoaded = true;
            }
        };
        soundPoolSetupThread.start();

        MyApplication.setServicePlayerListener(new MyApplication.ChangeListener() {
            @Override
            public void onIsPlayingChange() {
                stop();
                if(MyApplication.isPlaying()) {
                    play((int) DatabaseData.tonesSeparationTime, (int) DatabaseData.delayBetweenChords, ++playingID);
                } else {
                    if(!MyApplication.isChordOrIntervalPlaying() && !QuizData.quizPlayingCurrentThing) {
                        abandonAudioFocus();
                    }
                }
            }

            @Override
            public void onActivityResumed() {
                max = 100;
                if(MyApplication.getActivity() != null && MyApplication.getActivity() instanceof MainActivity) {
                    max = 75;
                }

                if(MyApplication.isPlaying()) {
                    if(currentChord != null) {
                        if(DatabaseData.appLanguage == DataContract.UserPrefEntry.LANGUAGE_ENGLISH && currentChord.getID() == MyApplication.MD9_ID) {
                            // Mali durski/dominantni 9
                            updateTextView(MyApplication.MD9_ENG_TEXT, MyApplication.MD9_ENG_ONE, MyApplication.MD9_ENG_TWO);
                        } else {
                            // String.valueOf(null) == "null"
                            updateTextView(currentChord.getName(), currentChord.getNumberOneAsString(), currentChord.getNumberTwoAsString());
                        }

                        if(globalShowWhatIntervals && DatabaseData.showWhatIntervals) {
                            setWhatIntervalsListViewVisibility(View.VISIBLE);
                        }

                        updateWhatIntervalsListView(globalNumberOfIntervalsToShow, currentChord.getAllIntervals(), globalDirectionToPlay);
                    } else {
                        setWhatIntervalsListViewVisibility(View.INVISIBLE);
                        if(currentInterval != null) {
                            String text = currentInterval.getName();
                            // This interval has unique name (two rows)
                            if(currentInterval.getDifference() == 6) {
                                text = readResource(R.string.interval_povecana_kvarta) + "\n/" + readResource(R.string.interval_smanjena_kvinta);
                            } else if(currentInterval.getDifference() == 18) {
                                text = readResource(R.string.interval_povecana_undecima) + "\n/" + readResource(R.string.interval_smanjena_duodecima);
                            }
                            updateTextView(text, null, null);
                        } else {
                            // If it is playing and it is not chord or interval, it is key (saved in lastKey)
                            updateTextView(getKeyName(lastKey), null, null);
                        }
                    }

                    updateProgressBarAnimation((int)(((double)milisecPassed/maxMilisecToPass) * ((float)max)), null);
                } else {
                    updateProgressBarAnimation(max, max);
                    setWhatIntervalsListViewVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPlayChordChange(final Interval[] interval, final int lowestKey, final int directionToPlay) {
                stop();
                if(interval != null) {
                    thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            playChord((int) DatabaseData.tonesSeparationTime, (int) DatabaseData.delayBetweenChords, interval,
                                    directionToPlay, ++playingID, lowestKey, null, null, null, false);
                        }
                    });
                    thread.start();
                } else if(!MyApplication.isPlaying()) {
                    abandonAudioFocus();
                }
            }

            @Override
            public void onPlayKey(final Integer keyId) {
                stop();
                if(keyId != null) {
                    if(MyApplication.isUIVisible()) {
                        thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                playChord((int) DatabaseData.tonesSeparationTime, (int) DatabaseData.delayBetweenChords,
                                        new Interval[]{IntervalsList.getInterval(0)}, 0, ++playingID, keyId,
                                        null, null, null, false);
                            }
                        });
                        thread.start();
                    }
                } else if(!MyApplication.isPlaying()) {
                    abandonAudioFocus();
                }
            }
        });


        intervalsPlayedFor = 0;
        chordsPlayedFor = 0;
        toneNotPlayedFor = 0;

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stop();
        abandonAudioFocus();
        soundPool.release();
        soundPool = null;
    }

    private static String readResource(int id) {
        return MyApplication.getAppContext().getResources().getString(id);
    }

    // Returns true if sound was played successfully
    public boolean playChord(final int oneKeyTime, final int betweenDelayMilisec, final Interval[] intervals, final int directionToPlay,
                             final int tempPlayID, final int lowestKey, final String chordName, final Integer chordNumberOne,
                             final Integer chordNumberTwo, final boolean showWhatIntervals) {


        globalDirectionToPlay = directionToPlay;
        globalShowWhatIntervals = showWhatIntervals;


        ArrayList<Integer> key = new ArrayList<>();

        int rangeOfChord = 0;

        int tempAddIfCistaPrima = 0;

        key.add(1);
        for(int i = 0; i < intervals.length; i++) {
            if(intervals[i].getDifference() == 0) {
                tempAddIfCistaPrima++;
                continue;
            }
            key.add(key.get(i-tempAddIfCistaPrima) + intervals[i].getDifference());
            rangeOfChord += intervals[i].getDifference();
        }


        if(rangeOfChord+lowestKey > DataContract.UserPrefEntry.NUMBER_OF_KEYS) {
            return false;
        }

        // Value to add to every key
        int toAdd = lowestKey - key.get(0);

        for(int i = 0; i < key.size(); i++) {
            key.add(i, key.get(i) + toAdd);
            key.remove(i+1);

            if(key.get(i) < 1 || key.get(i) > DataContract.UserPrefEntry.NUMBER_OF_KEYS) {
                return false;
            }
        }

        if(!areAllSoundsLoaded) {
            // Loop through every key and check if it was loaded
            for(int i = 0; i < key.size(); i++) {
                if(!keySoundLoaded[key.get(i)-1]) {
                    return false;
                }
            }
        }

        if(playingID != tempPlayID) { // BugFix if two players try to play at same time
            return false;
        }

        // Shows interval name
        if(MyApplication.isPlaying()) {
            if(currentChord != null && DatabaseData.appLanguage == DataContract.UserPrefEntry.LANGUAGE_ENGLISH && currentChord.getID() == MyApplication.MD9_ID) {
                // Mali durski/dominantni 9
                updateTextView(MyApplication.MD9_ENG_TEXT, MyApplication.MD9_ENG_ONE, MyApplication.MD9_ENG_TWO);
            } else {
                String text = chordName;
                // This interval has unique name (two rows)
                if(intervals.length == 1 && intervals[0].getDifference() == 6) {
                    text = readResource(R.string.interval_povecana_kvarta) + "\n/" + readResource(R.string.interval_smanjena_kvinta);
                } else if(intervals.length == 1 && intervals[0].getDifference() == 18) {
                    text = readResource(R.string.interval_povecana_undecima) + "\n/" + readResource(R.string.interval_smanjena_duodecima);
                }
                // String.valueOf(null) == "null"
                updateTextView(text, chordNumberOne == null ? null : String.valueOf(chordNumberOne), chordNumberTwo == null ? null : String.valueOf(chordNumberTwo));
            }
        }

        if(!MyApplication.isUIVisible()) {
            return false;
        }

        // Make list of all intervals invisible
        if(showWhatIntervals && intervals.length > 1 && DatabaseData.showWhatIntervals) {
            setWhatIntervalsListViewVisibility(View.VISIBLE);
        }


        // Play chord key by key
        for(int i = 0; i < key.size(); i++) {
            globalNumberOfIntervalsToShow = i;
            if(soundPool != null) {
                try {
                    if(directionToPlay == MyApplication.directionDownID) {
                        soundPool.play(keySounds[key.get(key.size()-i-1) - 1], 1, 1, 0, 0, 1);
                    } else {
                        soundPool.play(keySounds[key.get(i)-1], 1, 1, 0, 0, 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Show each interval of chord (if it is chord) as it is playing
            if(showWhatIntervals && intervals.length > 1) {
                updateWhatIntervalsListView(i, intervals, directionToPlay);
            }

            if(oneKeyTime > 0 && directionToPlay != MyApplication.directionSameID) {
                try {
                    Thread.sleep(oneKeyTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                milisecPassed += oneKeyTime;

                if(!MyApplication.isUIVisible()) {
                    stopSounds();
                    return false;
                }

                if(playingID != tempPlayID) {
                    return false;
                }
            }
        }

        if(betweenDelayMilisec > 0) {
            try {
                Thread.sleep(betweenDelayMilisec);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            milisecPassed += betweenDelayMilisec;

            if(directionToPlay == MyApplication.directionSameID) {
                try {
                    Thread.sleep(oneKeyTime * (intervals.length+1));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                milisecPassed += oneKeyTime * (intervals.length+1);
            }
        }

        stopSounds();

        return true;
    }

    // This function randomly plays (picks) chords/intervals and shows in UI what is playing
    public void play(final int oneKeyTime, final int betweenDelayMilisec, final int tempPlayID) {
        if(thread != null || playingID != tempPlayID || oneKeyTime + betweenDelayMilisec <= 0) { // || total duration <= 0 ms
            return;
        }

        // This is just to stop int overflow
        // playingID == tempPlayID (checked before)
        if(playingID > 10000) {
            playingID = 0;
            play(oneKeyTime, betweenDelayMilisec, 0);
            return;
        }

        final Random rand = new Random();

        thread = new Thread() {
            @Override
            public void run() {
//                Log.d(ServicePlayer.class.getSimpleName(), "run######################################################run");


                int whatToPlay;

                Interval[] intervalsToPlay;
                String nameToPlay = null;
                Integer chordNumberOneToPlay = null, chordNumberTwoToPlay = null;
                int totalChordDifference;
                int numberOfPerfectPrimas;

                Integer directionToPlay;

                directionUpNotPlayedFor = 0;
                directionDownNotPlayedFor = 0;
                directionTogetherNotPlayedFor = 0;

                intervalsPlayedFor = 0;
                chordsPlayedFor = 0;
                toneNotPlayedFor = 0;

                // Wait until all important sounds are loaded
                while(!MyApplication.isLoadingFinished) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                stopSounds();

                if(requestAudioFocus() != AudioManager.AUDIOFOCUS_REQUEST_GRANTED || playingID != tempPlayID || !MyApplication.isUIVisible()) {
                    return;
                }


                do {
                    try {
                        Thread.sleep(100); // Just little delay so everything sets down
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(tempPlayID != playingID || !MyApplication.isUIVisible()) {
                        break;
                    }


                    try {
                        whatToPlay = setupWhatToPlay(rand);
                    } catch (Exception e) {
                        // If exception is thrown stop playing
                        MyApplication.setIsPlaying(false);
                        ServicePlayer.this.stop();
                        break;
                    }


                    if(whatToPlay == PLAY_INTERVAL) {
                        currentInterval = IntervalsList.getRandomPlayableInterval();
                        if(currentInterval == null) {
                            whatToPlay = PLAY_CHORD;
                            currentChord = ChordsList.getRandomPlayableChord();
                        } else {
                            currentChord = null;
                        }
                        toneNotPlayedFor++;
                    } else if(whatToPlay == PLAY_CHORD) {
                        currentChord = ChordsList.getRandomPlayableChord();
                        if(currentChord == null) {
                            whatToPlay = PLAY_INTERVAL;
                            currentInterval = IntervalsList.getRandomPlayableInterval();
                        } else {
                            currentInterval = null;
                        }
                        toneNotPlayedFor++;
                    } else { // PLAY_TONE
                        // Make list of all intervals invisible
                        setWhatIntervalsListViewVisibility(View.INVISIBLE);
                        globalShowWhatIntervals = false;

                        currentChord = null;
                        currentInterval = null;

                        int keyToPlay = randomInt(rand, DatabaseData.downKeyBorder, DatabaseData.upKeyBorder, 0, lastKey);
                        lastKey = keyToPlay;
                        String keyName = getKeyName(keyToPlay);

                        maxMilisecToPass = oneKeyTime + betweenDelayMilisec;
                        milisecPassed = 0;
                        updateProgressBarAnimation(null, null); // Start animation

                        playChord(oneKeyTime, betweenDelayMilisec, new Interval[]{IntervalsList.getInterval(0)}, MyApplication.directionSameID, tempPlayID, keyToPlay, keyName, null, null, false);

                        IntervalsList.tickAllPlayableCountdowns();
                        ChordsList.tickAllPlayableCountdowns();

                        IntervalsList.increaseNotPlayedFor();
                        ChordsList.increaseNotPlayedFor();

                        toneNotPlayedFor = 0;
                        continue;
                    }

                    if(currentInterval == null && currentChord == null) {
                        continue;
                    }


                    IntervalsList.tickAllPlayableCountdowns();
                    ChordsList.tickAllPlayableCountdowns();

                    IntervalsList.increaseNotPlayedFor();
                    ChordsList.increaseNotPlayedFor();

                    intervalsToPlay = null;

                    if(currentInterval != null) {
                        intervalsPlayedFor++;

                        currentInterval.notPlayedFor = 0;
                        currentInterval.setPlayableCountdown((IntervalsList.getPlayableIntervalsCount() + ChordsList.getPlayableChordsCount()) / 4);
                        intervalsToPlay = new Interval[] {currentInterval};
                        nameToPlay = currentInterval.getName();
                        chordNumberOneToPlay = null;
                        chordNumberTwoToPlay = null;
                    } else if(currentChord != null) {
                        chordsPlayedFor++;

                        currentChord.notPlayedFor = 0;
                        currentChord.setPlayableCountdown((IntervalsList.getPlayableIntervalsCount() + ChordsList.getPlayableChordsCount()) / 4);
                        intervalsToPlay = currentChord.getAllIntervals();
                        nameToPlay = currentChord.getName();
                        chordNumberOneToPlay = currentChord.getNumberOne();
                        chordNumberTwoToPlay = currentChord.getNumberTwo();
                    }

                    if(intervalsToPlay == null || intervalsToPlay.length == 0) {
                        continue;
                    }

                    /*
                     * Now current chord/interval is known. Next, we need to find starting key and playing mode
                     */

                    totalChordDifference = 0;
                    numberOfPerfectPrimas = 0;

                    for(Interval i : intervalsToPlay) {
                        if(i.getDifference() == 0) {
                            numberOfPerfectPrimas++;
                            continue;
                        }
                        totalChordDifference += i.getDifference();
                    }

                    // Get random key that is loaded in SoundPool
                    if(areAllSoundsLoaded) {
                        lastKey = randomInt(rand, DatabaseData.downKeyBorder, DatabaseData.upKeyBorder-totalChordDifference, totalChordDifference, lastKey);
                    } else {
                        lastKey = randomInt(rand, DatabaseData.downKeyBorder, DatabaseData.downKeyBorder+soundsToLoadBeforePlaying, totalChordDifference, lastKey);
                    }

                    maxMilisecToPass = oneKeyTime * (intervalsToPlay.length+1-numberOfPerfectPrimas) + betweenDelayMilisec;
                    milisecPassed = 0;

                    if(DatabaseData.playingMode == DataContract.UserPrefEntry.PLAYING_MODE_CUSTOM) {
                        maxMilisecToPass *= DatabaseData.directionsCount;

                        boolean showIntervals, intervalsShown = false;

                        updateProgressBarAnimation(null, null); // Start animation

                        // Loop through all possibilities
                        for(int i = Math.min(Math.min(MyApplication.directionUpID, MyApplication.directionDownID), MyApplication.directionSameID); i <=
                                Math.max(Math.max(MyApplication.directionUpID, MyApplication.directionDownID), MyApplication.directionSameID); i++) {
                            Integer tempDirectionID = null;
                            showIntervals = true;
                            if(DatabaseData.directionUpViewIndex == i && DatabaseData.directionUp) {
                                tempDirectionID = MyApplication.directionUpID;
                            } else if(DatabaseData.directionDownViewIndex == i && DatabaseData.directionDown) {
                                tempDirectionID = MyApplication.directionDownID;
                            } else if(DatabaseData.directionSameTimeViewIndex == i && DatabaseData.directionSameTime) {
                                tempDirectionID = MyApplication.directionSameID;
                                showIntervals = false;
                            }

                            if(tempDirectionID == null) {
                                continue;
                            }

                            // play and show interval if it haven't been shown or if it needs to show (so it doesn't refresh on direction same time)
                            playChord(oneKeyTime, betweenDelayMilisec, intervalsToPlay, tempDirectionID, tempPlayID, lastKey, nameToPlay, chordNumberOneToPlay, chordNumberTwoToPlay, !intervalsShown || showIntervals);
                            intervalsShown = true;
                        }
                    } else if(DatabaseData.playingMode == DataContract.UserPrefEntry.PLAYING_MODE_RANDOM) {
                        directionToPlay = null;

                        // Warning: hardcoded 2 (3 places) and 3 (1 place) and "directionToPlay = i;"
                        // Get direction to play
                        if(DatabaseData.directionUp && directionUpNotPlayedFor >= DatabaseData.directionsCount * 2) {
                            directionToPlay = MyApplication.directionUpID;
                        } else if(DatabaseData.directionDown && directionDownNotPlayedFor >= DatabaseData.directionsCount * 2) {
                            directionToPlay = MyApplication.directionDownID;
                        } else if(DatabaseData.directionSameTime && directionTogetherNotPlayedFor >= DatabaseData.directionsCount * 2) {
                            directionToPlay = MyApplication.directionSameID;
                        } else {
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
                        }

                        if(directionToPlay == null) {
                            Log.e("ServicePlayer","Random algorithm is not working (ServicePlayer)");

//                            // remove this:
//                            showToast("Random algorithm is not working (ServicePlayer)");

                            // Something went wrong, try again
                            continue;
                        }

                        directionUpNotPlayedFor++;
                        directionDownNotPlayedFor++;
                        directionTogetherNotPlayedFor++;

                        if(directionToPlay == MyApplication.directionUpID) {
                            directionUpNotPlayedFor = 0;
                        } else if(directionToPlay == MyApplication.directionDownID) {
                            directionDownNotPlayedFor = 0;
                        } else if(directionToPlay == MyApplication.directionSameID) {
                            directionTogetherNotPlayedFor = 0;
                        } else { // Something went wrong, try again
                            continue;
                        }


                        updateProgressBarAnimation(null, null); // Start animation

                        playChord(oneKeyTime, betweenDelayMilisec, intervalsToPlay, directionToPlay, tempPlayID, lastKey, nameToPlay, chordNumberOneToPlay, chordNumberTwoToPlay, true);
                    }

                    // Make list of all intervals invisible
                    setWhatIntervalsListViewVisibility(View.INVISIBLE);
                    globalShowWhatIntervals = false;

                } while (MyApplication.isPlaying());

                // Cleanup after everything is finished
                if(tempPlayID == playingID) {
                    ServicePlayer.this.stop();
                    ServicePlayer.this.abandonAudioFocus(); // Abandon audio focus so other apps can stream audio
                    MyApplication.setIsPlaying(false);
                }
            }
        };
        thread.start();
    }

    private int setupWhatToPlay(Random rand) throws Exception {
        int whatToPlay;

        // Random sets what to play (interval or chord)
        if(DatabaseData.directionsCount <= 0) {
            if(!DatabaseData.playWhatTone && !DatabaseData.playWhatOctave) {
                showToast(readResource(R.string.no_checked_playing_type_error));
                throw new Exception();
            } else {
                whatToPlay = PLAY_TONE;
            }
        } else if(toneNotPlayedFor >= ((IntervalsList.getCheckedIntervalCount() + ChordsList.getCheckedChordsCount()) / 2 + 1) && (DatabaseData.playWhatTone || DatabaseData.playWhatOctave)) {
            whatToPlay = PLAY_TONE;
        } else if(intervalsPlayedFor-chordsPlayedFor > (IntervalsList.getCheckedIntervalCount() / (ChordsList.getCheckedChordsCount()+1) + 1) * 3) {
            whatToPlay = PLAY_CHORD;
            intervalsPlayedFor = (intervalsPlayedFor+chordsPlayedFor) / 2;
        } else if(chordsPlayedFor-intervalsPlayedFor > (ChordsList.getCheckedChordsCount() / (IntervalsList.getCheckedIntervalCount()+1) + 1) * 3) {
            whatToPlay = PLAY_INTERVAL;
            chordsPlayedFor = (intervalsPlayedFor+chordsPlayedFor) / 2;
        } else {
            // Is there any selected interval or chord
            if(IntervalsList.getCheckedIntervalCount() + ChordsList.getCheckedChordsCount() <= 0) {
                if(!DatabaseData.playWhatTone && !DatabaseData.playWhatOctave) {
                    showToast(readResource(R.string.no_checked_intervals_error));
                    throw new Exception();
                } else {
                    whatToPlay = PLAY_TONE;
                }

            } else {
                // in rand.nextInt(bound) bound is excluded
                int tempRandNumb;
                if(DatabaseData.playWhatTone || DatabaseData.playWhatOctave) {
                    // 12 tones in one octave
                    tempRandNumb = rand.nextInt(IntervalsList.getCheckedIntervalCount() + ChordsList.getCheckedChordsCount() + 12);
                } else {
                    tempRandNumb = rand.nextInt(IntervalsList.getCheckedIntervalCount() + ChordsList.getCheckedChordsCount());
                }
                if(tempRandNumb >= IntervalsList.getCheckedIntervalCount()) {
                    if(tempRandNumb >= IntervalsList.getCheckedIntervalCount() + ChordsList.getCheckedChordsCount() && (DatabaseData.playWhatTone || DatabaseData.playWhatOctave)) {
                        whatToPlay = PLAY_TONE;
                    } else {
                        whatToPlay = PLAY_CHORD;
                    }
                } else {
                    whatToPlay = PLAY_INTERVAL;
                }
            }
        }
        return whatToPlay;
    }

    public void stopSounds() {
        try {
            soundPool.autoPause();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        playingID += 10; // So playing stops immediately (then playingID != tempPlayID), 10 if cheater-clicking
        stopSounds();
        if(thread != null) {
            thread.interrupt();
            thread = null;
        }
        updateProgressBarAnimation(max, max);

    }


    public int requestAudioFocus() {
        int result;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // >= api 26
            result = audioManager.requestAudioFocus(audioFocusRequest);
        } else {
            result = audioManager.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        }
        return result;
    }

    public void abandonAudioFocus() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // >= api 26
            audioManager.abandonAudioFocusRequest(audioFocusRequest);
        } else {
            audioManager.abandonAudioFocus(afChangeListener);
        }
    }

    private void loadSound(final int from, final int to, final boolean showProgress) {
        Class res = R.raw.class;
        long startTime;
        for(int i = from; i <= to; i++) {
            if(keySoundLoaded[i-1]) {
                continue;
            }
            try {
                // Getting key mp3 sound based on key number
                startTime = System.currentTimeMillis();
                Field field = res.getField("piano" + i); // Rand key - interval
                int resID = field.getInt(null);

                // priority == 1 will be implemented later in android OS, 1 is recommended for future compatibility
                keySounds[i-1] = soundPool.load(MyApplication.getActivity(), resID, 1);

                if(showProgress) {
                    // Instant set to percentage of sounds loaded
                    updateProgressBarAnimation(null, (int)(((double)i/to) * ((float)max) )); // Update Progress Bar
                } else {
                    // If progress is not needed to show, process is running in background.
                    // Sleep for time that needed to load one sound*2, to ease off CPU
                    try {
                        Thread.sleep((System.currentTimeMillis()-startTime) * 2 + 1);
                    } catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                }
                keySoundLoaded[i-1] = true;
            }
            catch (Exception e) { // This will loop until all keys are loaded or app is completely removed
                Log.e("MyTag", "Failed to get raw id.", e);
                i--;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

    public void updateTextView(final String chordName, final String chordNumberOne, final String chordNumberTwo) {
        // Update it only in MainActivity
        if(MyApplication.getActivity() == null || !(MyApplication.getActivity() instanceof MainActivity)) {
            return;
        }
        try {
            MyApplication.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView chordTV = (TextView) MyApplication.getActivity().findViewById(R.id.chord_text_view);
                    TextView numberOneTV = MyApplication.getActivity().findViewById(R.id.chord_number_one);
                    TextView numberTwoTV = MyApplication.getActivity().findViewById(R.id.chord_number_two);

                    MyApplication.updateTextView(chordTV, chordName, numberOneTV, chordNumberOne, numberTwoTV, chordNumberTwo);

                    if(DatabaseData.showProgressBar) {
                        progressBar = (ProgressBar) MyApplication.getActivity().findViewById(R.id.ring_playing_progress_bar);
                        progressBar.setProgress(0);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateWhatIntervalsListView(final int numberOfIntervalsToShow, final Interval[] intervals, final int directionToPlay) {
        if(MyApplication.getActivity() == null || !(MyApplication.getActivity() instanceof MainActivity)) {
            return;
        }
        try {
            MyApplication.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    WhatIntervalsAdapter whatIntervalsAdapter = new WhatIntervalsAdapter(MyApplication.getActivity(), intervals, numberOfIntervalsToShow, directionToPlay);

                    ListView whatIntervalsListView = MyApplication.getActivity().findViewById(R.id.what_intervals_list_view);

                    whatIntervalsListView.setAdapter(whatIntervalsAdapter);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setWhatIntervalsListViewVisibility(final int visibility) {
        if(MyApplication.getActivity() == null || !(MyApplication.getActivity() instanceof MainActivity)) {
            return;
        }
        try {
            MyApplication.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ListView whatIntervalsListView = MyApplication.getActivity().findViewById(R.id.what_intervals_list_view);
                        whatIntervalsListView.setVisibility(visibility);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This can be written better
    // set?Percent = null -> animation 0 to 75 (100%), != null set %
    private void updateProgressBarAnimation(final Integer setFromPercent, final Integer setToPercent) {
        if(MyApplication.getActivity() == null || (MyApplication.isLoadingFinished && !(MyApplication.getActivity() instanceof MainActivity))) {
            return;
        }
        try {
            MyApplication.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        progressBar = (ProgressBar) MyApplication.getActivity().findViewById(R.id.ring_playing_progress_bar);
                        if(MyApplication.getActivity() instanceof MainActivity) {
                            // background progress bar exist only in MainActivity
                            ProgressBar backgroundProgresBar = (ProgressBar) MyApplication.getActivity().
                                    findViewById(R.id.background_ring_progress_bar);
                            if(!DatabaseData.showProgressBar && MyApplication.isLoadingFinished) {
                                backgroundProgresBar.setVisibility(View.INVISIBLE);
                            } else {
                                backgroundProgresBar.setVisibility(View.VISIBLE);
                            }
                        }

                        if(!DatabaseData.showProgressBar && MyApplication.isLoadingFinished) {
                            progressBar.setVisibility(View.INVISIBLE);
                            return;
                        } else {
                            progressBar.setVisibility(View.VISIBLE);
                        }

                        if(MyApplication.isPlaying() && setToPercent == null) {
                            if(setFromPercent != null) {
                                progressBarAnimation = new ProgressBarAnimation(progressBar, setFromPercent, max);
                            } else {
                                progressBarAnimation = new ProgressBarAnimation(progressBar, 0, max);
                            }
                            progressBarAnimation.setDuration(maxMilisecToPass-milisecPassed);
                        } else {
                            if(progressBarAnimation != null) {
                                progressBarAnimation.cancel();
                            }
                            if(setToPercent == null) {
                                progressBarAnimation = new ProgressBarAnimation(progressBar, max, max);
                            } else {
                                if(setFromPercent == null) {
                                    progressBarAnimation = new ProgressBarAnimation(progressBar, progressBar.getProgress(), setToPercent);
                                } else {
                                    progressBarAnimation = new ProgressBarAnimation(progressBar, setFromPercent, setToPercent);
                                }
                            }
                            progressBarAnimation.setDuration(100); // 100 Just to see a little transition
                        }
                        progressBar.startAnimation(progressBarAnimation);
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getKeyName(int key) {
        key--; // 0 to 60 (and not 1 - 61)

        String[] keys = MyApplication.getAppContext().getResources().getStringArray(R.array.key_symbols);
        StringBuilder stringBuilder = new StringBuilder();

        if(DatabaseData.appLanguage == DataContract.UserPrefEntry.LANGUAGE_CROATIAN) {
            int octaveNumber = (key/12) - 1;

            if(octaveNumber > 0) {
                stringBuilder.append(keys[key%12]);
                stringBuilder.append(octaveNumber);
            } else if(octaveNumber == 0) {
                stringBuilder.append(keys[key%12]);
            } else if(octaveNumber == -1) {
                String str = keys[key%12];
                String capitalizeFirstLetter = str.substring(0, 1).toUpperCase() + str.substring(1);
                stringBuilder.append(capitalizeFirstLetter);
            }

            return stringBuilder.toString();
        } else {
            stringBuilder.append(keys[key%12]);
            stringBuilder.append((key/12) + 2);

            return stringBuilder.toString();
        }
    }

    private void showToast(final String msg) {
        if(msg == null || msg.isEmpty()) {
            return;
        }
        if(MyApplication.getActivity() == null) {
            return;
        }
        try {
            MyApplication.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MyApplication.getActivity(), msg, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Generates random key inside borders excluding exceptions (for now, that is just last key)
    // And including start and end numbers and chord/interval range
    private int randomInt(Random rand, int start, int end, int chordDifference, int... exception) {
        if(end < start) {
            throw new IllegalArgumentException("ServicePlayer.randomInt(...): Range of integers to choose random integer is not valid!");
        }
        if(start < 1) {
            start = 1;
        }
        if(end > DataContract.UserPrefEntry.NUMBER_OF_KEYS) {
            end = DataContract.UserPrefEntry.NUMBER_OF_KEYS;
        }

        // For ignoring exceptions outside border
        int numberOfExceptions = 0;
        for(int ex : exception) {
            if(ex >= start && ex <= end) {
                numberOfExceptions++;
            }
        }

        // If exceptions are only possible solution, return one of them
        if(end - start - chordDifference < numberOfExceptions) {
            return rand.nextInt(end-start+1) + start;
        }

        // Get random number to get (ignoring exceptions)
        int randomNumb = rand.nextInt((end-start) - numberOfExceptions + 1);
        int counter = 0;
        for(int i = 0; i <= end-start; i++) {
            if(counter == randomNumb) {
                return start+i;
            }
            // If number is not an exception - increment counter
            if(!isInside(start+i, exception)) {
                counter++;
            }
        }

        // If something goes wrong, just return random number inside borders
        return rand.nextInt(end-start+1) + start;
    }

    private boolean isInside(int number, int... newInt) {
        if(newInt == null) {
            return false;
        }

        for (int aNewInt : newInt) {
            if (number == aNewInt) {
                return true;
            }
        }
        return false;
    }

    public int getPlayingID() {
        return playingID;
    }

    public boolean isPlaying() {
        return MyApplication.isPlaying() && MyApplication.isUIVisible();
    }
}
