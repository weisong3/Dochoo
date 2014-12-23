package com.chc.found.presenters;

import android.content.Context;

public interface IVoicePlaybackPresenter {
	void startPlaying();
    void startPlayingAtPercentage(int per);
	void stop();
    void pause();
    void seekTo(int msec);
    void seekPercentage(int percent);
    int getCurrentPercentage();
    int getDurationMs(Context context, String absolutePath);
    int getCurrentPos();
    boolean isPlaying();
    boolean isPaused();
    boolean isStopped();

    void resumePlaying();
}
