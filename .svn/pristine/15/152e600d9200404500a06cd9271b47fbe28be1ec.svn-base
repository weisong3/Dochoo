package com.chc.found.presenters;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.chc.found.views.IVoicePlaybackView;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

public class VoicePlaybackPresenter implements IVoicePlaybackPresenter {
	
	private static final String TAG = VoicePlaybackPresenter.class.getSimpleName();
    private static final int PROGRESS_RATE_MS = 500;

    private final String absolutePath;

    private static enum PlayStatus {
        PLAYING,
        PAUSED,
        STOPPED;
    }
    private final Handler mHandler;
    private IVoicePlaybackView mView;
	private MediaPlayer lastPlayer;
	private String lastPlaying = "";
    private int maxDuration;
    private final AtomicInteger currPos = new AtomicInteger(0);
    private volatile PlayStatus playStatus;



    private Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            if (playStatus != PlayStatus.PLAYING) return;
            if (lastPlayer != null) {
                currPos.set(lastPlayer.getCurrentPosition());
            }
            if (mView != null) {
                mView.onProgressUpdate(currPos.get(), maxDuration);
            }
            if (playStatus == PlayStatus.PLAYING) {
                // if still playing, schedule next update
                if (mHandler != null) {
                    mHandler.postDelayed(this, PROGRESS_RATE_MS);
                }
            }
        }
    };

	public VoicePlaybackPresenter(Handler handler, IVoicePlaybackView mView, String absolutePath) {
		super();
		this.mView = mView;
        this.mHandler = handler;
        this.absolutePath = absolutePath;
        this.playStatus = PlayStatus.STOPPED;
	}

	@Override
	public void startPlaying() {
        if (playStatus == PlayStatus.STOPPED) {
            currPos.set(0);
            startNew(absolutePath, 0);
        } else if (playStatus == PlayStatus.PAUSED) {
            resume();
        }
	}

    @Override
    public void startPlayingAtPercentage(int per) {

        if (playStatus == PlayStatus.STOPPED) {
            startNew(absolutePath, per);
        } else if (playStatus == PlayStatus.PAUSED) {
            int msec = per * maxDuration / 100;
            if (msec >= 0 && msec < maxDuration) {
                currPos.set(msec);
                seekTo(msec);
            }
            resume();
        }
    }

    private void resume() {
        if (lastPlayer == null) {
            playStatus = PlayStatus.STOPPED;
            return;
        }
        lastPlayer.start();
        playStatus = PlayStatus.PLAYING;

        mHandler.post(progressRunnable);
    }

    private void startNew(String absolutePath, int per) {
        lastPlayer = new MediaPlayer();
        lastPlayer.setOnErrorListener(new OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                stop();
                mView.onPlaybackError();
                return true;
            }
        });
        try {
            lastPlayer.setDataSource(absolutePath);
            lastPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
            lastPlayer.setVolume(1.0f,1.0f);
            lastPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
            lastPlayer.release();
            mView.onPlaybackError();
            return;
        }
        lastPlayer.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                stop();
                mView.onPlayingFinished();
            }
        });

        lastPlaying = absolutePath;
        maxDuration = lastPlayer.getDuration();
        int pos = per * maxDuration / 100;
        if (pos >= 0 && pos < maxDuration) {
            lastPlayer.seekTo(pos);
            currPos.set(pos);
        }
        lastPlayer.start();
        playStatus = PlayStatus.PLAYING;

        mHandler.post(progressRunnable);
    }

	@Override
	public void stop() {
        if (playStatus == PlayStatus.STOPPED) return;
        playStatus = PlayStatus.STOPPED;
        currPos.set(0);
        if (lastPlayer == null) return;
        try {
			lastPlayer.setOnCompletionListener(null);
			lastPlayer.stop();
			lastPlayer.reset();
			lastPlayer.release();
            lastPlayer = null;
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

    @Override
    public void pause() {
        if (playStatus == PlayStatus.PLAYING && lastPlayer != null) {
            playStatus = PlayStatus.PAUSED;
            lastPlayer.pause();
            currPos.set(lastPlayer.getCurrentPosition());
        }
    }

    @Override
    public void seekTo(int msec) {
        if (lastPlayer != null) {
            currPos.set(msec);
            lastPlayer.seekTo(msec);
        }
    }

    @Override
    public void seekPercentage(int percent) {
        if (lastPlayer != null && this.maxDuration != -1) {
            currPos.set(percent * this.maxDuration / 100);
            lastPlayer.seekTo(currPos.get());
        }
    }

    @Override
    public int getCurrentPercentage() {
        if (maxDuration <= 0) return 0;
        return (int) (((double) currPos.get()) / maxDuration * 100);
    }

    @Override
    public int getDurationMs(Context context, String absolutePath) {
        if (org.apache.commons.lang.StringUtils.isBlank(absolutePath)) {
            return -1;
        }
        File f = new File(absolutePath);
        if (f.exists() && f.isFile()) {
            MediaPlayer player = MediaPlayer.create(context, Uri.fromFile(f));
            if (player != null) {
                int duration = player.getDuration();
                player.release();
                return duration;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    @Override
    public int getCurrentPos() {
        return currPos.get();
    }

    @Override
    public boolean isPlaying() {
        return playStatus == PlayStatus.PLAYING;
    }

    @Override
    public boolean isPaused() {
        return playStatus == PlayStatus.PAUSED;
    }

    @Override
    public boolean isStopped() {
        return playStatus == PlayStatus.STOPPED;
    }

    @Override
    public void resumePlaying() {
        if (lastPlayer != null && playStatus == PlayStatus.PAUSED) {
            resume();
        }
    }

}
