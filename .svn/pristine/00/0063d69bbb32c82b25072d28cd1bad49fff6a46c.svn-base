package com.chc.dochoo.conversations;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.chc.found.presenters.IVoicePlaybackPresenter;
import com.chc.found.presenters.VoicePlaybackPresenter;
import com.chc.found.views.IVoicePlaybackView;
import com.test.found.R;

import java.lang.ref.WeakReference;

/**
 * Created by HenryW on 2/12/14.
 */
class VoiceControl implements IVoicePlaybackView {
    static final int SEEKBAR_MAX = 100;
    private final Handler handler;
    private final InstantMessageFragment mFragment;
    WeakReference<InstantMessageViewHolder> holderWeakReference;
    InstantMessage im;
    private Context mContext;
    IVoicePlaybackPresenter voicePresenter;
    int seekBarPosPercentage;
    private int maxDuration;

    VoiceControl(InstantMessageFragment fragment, Context context,
                 Handler handler, InstantMessage im, InstantMessageViewHolder holder) {
        this.mContext = context;
        this.handler = handler;
        this.im = im;
        this.holderWeakReference = new WeakReference<InstantMessageViewHolder>(holder);
        this.voicePresenter = new VoicePlaybackPresenter(handler, this, im.getMultimediaPath());
        this.mFragment = fragment;
        this.maxDuration = voicePresenter.getDurationMs(context, im.getMultimediaPath());
        updateViews();
    }

    public void replaceInstantMessage(InstantMessage im) {
        this.im = im;
        this.voicePresenter = new VoicePlaybackPresenter(handler, this, im.getMultimediaPath());
    }

    @Override
    public void onPlayingFinished() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (holderWeakReference != null && holderWeakReference.get() != null) {
                    InstantMessageViewHolder holder = holderWeakReference.get();
                    setUpButtonView(holder);
                    int progress = SEEKBAR_MAX;
                    seekBarPosPercentage = progress;
                    //noinspection ConstantConditions
                    holder.voiceSeekBar.setProgress(progress);
                    setUpTimerView(holder);
                }
                mFragment.onPlayingFinished();
            }
        });
    }

    @Override
    public void onProgressUpdate(final int currPos, final int maxDuration) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (holderWeakReference != null && holderWeakReference.get() != null) {
                    InstantMessageViewHolder holder = holderWeakReference.get();
                    //noinspection ConstantConditions
                    if (!holder.ownerId.equals(im.getId())) return;
                    int progress = (int) ((double) currPos / maxDuration * SEEKBAR_MAX + 0.5);
                    setUpButtonView(holder);
                    holder.voiceSeekBar.setProgress(progress);
                    holder.voiceTime.setText(formatVoiceTime(currPos / 1000));
                }
            }
        });
    }

    @Override
    public void onPlaybackError() {
        mFragment.onVoiceMessageDamaged(im);
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, R.string.toast_error_damaged_voice, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateViews() {
        if (holderWeakReference != null && holderWeakReference.get() != null) {
            InstantMessageViewHolder holder = holderWeakReference.get();
            //noinspection ConstantConditions
            if (!holder.ownerId.equals(im.getId())) return;
            setUpButtonView(holder);
            setUpSeekbarView(holder);
            setUpTimerView(holder);
        }
    }

    private void setUpTimerView(InstantMessageViewHolder holder) {
        if (holder == null) return;
        if (!holder.ownerId.equals(im.getId())) return;
        // set the timer text
        if (voicePresenter.isStopped()) {
            if (maxDuration != -1)
                holder.voiceTime.setText(formatVoiceTime(maxDuration / 1000));
            else
                holder.voiceTime.setText("");
        } else {
            holder.voiceTime.setText(formatVoiceTime(voicePresenter.getCurrentPos() / 1000));
        }
    }

    private void setUpSeekbarView(InstantMessageViewHolder holder) {
        if (holder == null) return;
        if (!holder.ownerId.equals(im.getId())) return;
        holder.voiceSeekBar.setMax(SEEKBAR_MAX);

        if (voicePresenter.isStopped()) {
            seekBarPosPercentage = 0;
            holder.voiceSeekBar.setProgress(0);
        } else {
            int progress = voicePresenter.getCurrentPercentage();
            seekBarPosPercentage = progress;
            holder.voiceSeekBar.setProgress(progress);
        }
    }

    void setUpButtonView(InstantMessageViewHolder holder) {
        if (holder == null) return;
        if (!holder.ownerId.equals(im.getId())) return;
        if (!voicePresenter.isPlaying()) {
            if (im.isFromPatient()) {
                holder.voiceImageBtn.setImageResource(R.drawable.btn_voice_play_out);
            } else {
                holder.voiceImageBtn.setImageResource(R.drawable.btn_voice_play_in);
            }
        } else {
            // playing
            if (im.isFromPatient()) {
                holder.voiceImageBtn.setImageResource(R.drawable.btn_pause_playing_out);
            } else {
                holder.voiceImageBtn.setImageResource(R.drawable.btn_pause_playing_in);
            }
        }
    }

    public VoiceControl setHolderWeakReference(InstantMessageViewHolder holder) {
        this.holderWeakReference = new WeakReference<InstantMessageViewHolder>(holder);
        updateViews();
        return this;
    }

    public VoiceControl setControls() {
        if (holderWeakReference != null && holderWeakReference.get() != null) {
            InstantMessageViewHolder holder = holderWeakReference.get();
            //noinspection ConstantConditions
            if (!holder.ownerId.equals(im.getId())) return this;
            holder.voiceImageBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    mFragment.onVoiceViewClicked(VoiceControl.this, im);
                }
            });
            holder.voiceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    seekBarPosPercentage = progress;
                    if (fromUser && holderWeakReference != null && holderWeakReference.get() != null
                            && maxDuration != -1) {
                        //noinspection ConstantConditions
                        holderWeakReference.get().voiceTime
                                .setText(formatVoiceTime((int) ((double) (progress) / 100 * maxDuration / 1000)));
                    }

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    if (voicePresenter.isPlaying()) {
                        voicePresenter.pause();
                        setUpButtonView(holderWeakReference.get());
                        setUpTimerView(holderWeakReference.get());
                    }
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    voicePresenter.seekPercentage(seekBarPosPercentage);
//                        voicePresenter.resumePlaying();
                }
            });
        }

        return this;
    }


    private String formatVoiceTime(int timeSec) {
        if (timeSec < 0) return "";
        int minutes = timeSec / 60;
        String result;
        if (minutes < 60) {
            int seconds = timeSec % 60;

            result = minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
        } else {
            // longer than one hour, we currently do not support this
            result = "";
        }
        return result;
    }

    public void stopPlaying() {
        if (voicePresenter.isPlaying())
            voicePresenter.stop();
        updateViews();
    }

    public void pausePlaying() {
        if (voicePresenter.isPlaying()) {
            voicePresenter.pause();
        }
        updateViews();
    }
}

