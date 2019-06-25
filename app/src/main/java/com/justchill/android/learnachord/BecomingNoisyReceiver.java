package com.justchill.android.learnachord;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import com.justchill.android.learnachord.quiz.ModeOneActivity;
import com.justchill.android.learnachord.quiz.ModeThreeActivity;
import com.justchill.android.learnachord.quiz.ModeTwoActivity;
import com.justchill.android.learnachord.quiz.QuizData;

// Stops playing all sounds (or pauses quiz) called when headphones are disconnected
public class BecomingNoisyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
            // Pause the playback
            if(QuizData.isQuizModePlaying) {
                // If user is inside quiz, try to pause it
                try {
                    if(MyApplication.getActivity() instanceof ModeOneActivity) {
                        ((ModeOneActivity) MyApplication.getActivity()).pauseQuiz();
                        ((ModeOneActivity) MyApplication.getActivity()).stopPlaying();
                    } else if(MyApplication.getActivity() instanceof ModeTwoActivity) {
                        ((ModeTwoActivity) MyApplication.getActivity()).pauseQuiz();
                        ((ModeTwoActivity) MyApplication.getActivity()).stopPlaying();
                    } else if(MyApplication.getActivity() instanceof ModeThreeActivity) {
                        ((ModeThreeActivity) MyApplication.getActivity()).pauseQuiz();
                        ((ModeThreeActivity) MyApplication.getActivity()).stopPlaying();
                    }
                } catch (Exception ignored) {}
            } else {
                // Otherwise, stop all sounds
                MyApplication.setIsPlaying(false);
            }
        }
    }

}
