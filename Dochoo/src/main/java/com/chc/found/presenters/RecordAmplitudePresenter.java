package com.chc.found.presenters;

import android.os.Handler;
import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by HenryW on 12/26/13.
 */
public class RecordAmplitudePresenter {
    public static interface IAmplitudeView {
        void onAmplitudeUpdate(AmplitudeLevel level, Long updateInterval);
    }

    public static enum AmplitudeLevel {
        VERY_LOW,
        LOW,
        MEDIUM,
        HIGH;
    }

    private static final double THRESHOLD_LOW = 3;
    private static final double THRESHOLD_MEDIUM = 6;
    private static final double THRESHOLD_HIGH = 9;

    private final IAmplitudeView mView;
    private final Handler mHandler;
    private final IRecordVoicePresenter voicePresenter;
    private final AtomicBoolean recording = new AtomicBoolean(false);

    public RecordAmplitudePresenter(IAmplitudeView mView, Handler mHandler, IRecordVoicePresenter voicePresenter) {
        if (mView == null || mHandler == null || voicePresenter == null) {
            throw new IllegalArgumentException();
        }
        this.mView = mView;
        this.mHandler = mHandler;
        this.voicePresenter = voicePresenter;
    }

    public void startGettingAmplitude() {
        this.recording.set(true);
        mHandler.post(new AmplitudeRunnable(recording, mHandler, voicePresenter, mView));
    }

    public void stopGettingAmplitude() {
        this.recording.set(false);
    }

    private static class AmplitudeRunnable implements Runnable {

        private static final String TAG = AmplitudeRunnable.class.getSimpleName();
        private static final long UPDATE_INTERVAL = 150; // milliseconds
        private final AtomicBoolean recording;
        private final Handler mHandler;
        private final IRecordVoicePresenter voicePresenter;
        private final IAmplitudeView mView;
        private long totalTime;

        private AmplitudeRunnable(AtomicBoolean recording, Handler mHandler,
                                  IRecordVoicePresenter voicePresenter, IAmplitudeView mView) {
            this.recording = recording;
            this.mHandler = mHandler;
            this.voicePresenter = voicePresenter;
            this.mView = mView;
            this.totalTime = 0;
        }

        @Override
        public void run() {
            if (!recording.get()) {
                // not recording, no need to pull amplitude
                return;
            }
            try {
                double amplitude = this.voicePresenter.getAmplitude();

                Log.w(TAG, String.valueOf(amplitude));
                AmplitudeLevel level;
                if (amplitude < THRESHOLD_LOW) {
                    level = AmplitudeLevel.VERY_LOW;
                } else if (amplitude < THRESHOLD_MEDIUM) {
                    level = AmplitudeLevel.LOW;
                } else if (amplitude < THRESHOLD_HIGH) {
                    level = AmplitudeLevel.MEDIUM;
                } else {
                    level = AmplitudeLevel.HIGH;
                }
                mView.onAmplitudeUpdate(level,totalTime += UPDATE_INTERVAL);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
                // if anything is wrong, stop scheduling more update
                return;
            }
            // still recoding, schedule next update
            mHandler.postDelayed(this, UPDATE_INTERVAL);
        }
    }

}
