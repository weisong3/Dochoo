package com.chc.found.presenters;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;

import com.chc.found.views.IRecordVoiceView;

public class RecordVoicePresenter implements IRecordVoicePresenter {

    private static final String VOICE_FILE_POSTFIX = ".mp4";
	//private static final int MAX_DURATION_MS = 5000; // 59 seconds
    private static final long VOICE_MIN_LENGTH = 1000;
    public static final double MAX_AMPLITUDE_DIVIDER = 2700.0;
    /**
	 * Recorder must be released via calling the close method
	 * when each presenter instance is no longer used
	 */
	private MediaRecorder mRecorder;
	private String lastRecordPath;
	private IRecordVoiceView mView;
	//private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
	//private ScheduledFuture<?> lastRecordFuture;
	private String lastFileName;
	private volatile AtomicBoolean recording = new AtomicBoolean(false);
	private String lastPath;
	private long lastStartTime;
	
	public RecordVoicePresenter(IRecordVoiceView view) {
		this.mView = view;
		mRecorder = new MediaRecorder(); // we will use a single recorder for each instance of presenter
	}

	@Override
	public synchronized void startRecording(String path, String filename) throws IOException {
		if (recording.get()) {
			// already recording
            return;
		}
        recording.set(true);

		lastRecordPath = path + filename + VOICE_FILE_POSTFIX;
		lastPath = path;
		lastFileName = filename;
		lastStartTime = System.currentTimeMillis();
        /*if (lastRecordFuture != null) {
            lastRecordFuture.cancel(true);
            lastRecordFuture = null;
        }*/

		checkSDstateAndParentFolder(lastRecordPath);

		mRecorder.setOnErrorListener(new OnErrorListener() {

			@Override
			public void onError(MediaRecorder mr, int what, int extra) {
				recordingFailed();
			}
		});

		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
		mRecorder.setOutputFile(lastRecordPath);
		mRecorder.prepare();
		mRecorder.start();

		//lastRecordFuture = scheduledExecutorService.schedule(new MaxDurationRunnable(), MAX_DURATION_MS, TimeUnit.MILLISECONDS);
	}

	protected void recordingFailed() {
		mView.onRecordFailed();
		cancelRecording();
	}

	private void checkSDstateAndParentFolder(String path) throws IOException {
		String state = android.os.Environment.getExternalStorageState();
		if(!state.equals(android.os.Environment.MEDIA_MOUNTED)){
			throw new IOException("SD Card is not mounted,It is  " + state + ".");
		}
		File directory = new File(path).getParentFile();
		if(directory == null || !directory.exists() && !directory.mkdirs()){
			throw new IOException("Path to file could not be created");
		}
	}

	@Override
	public synchronized void cancelRecording() {
		if (!recording.get()) {
			// not recording, ignore
			return;
		}
        recording.set(false);
		/*if (lastRecordFuture != null) {
			lastRecordFuture.cancel(true);
			lastRecordFuture = null;
		}*/

		try {
			mRecorder.stop();
		} catch (Exception e) {
            // ignored
		}
		mRecorder.reset();

		// delete last file
		if (lastRecordPath == null) return;

		File f = new File(lastRecordPath);
		if (f.exists() && f.isFile()) {
			f.delete();
		}
	}

	@Override
	public synchronized void stopRecordingAndSave() {
		long currentTimeMillis = System.currentTimeMillis();
		if (!recording.get()) {
			// not recording, ignore
			return;
		}
        recording.set(false);

		/*if (lastRecordFuture != null) {
			lastRecordFuture.cancel(false);
			lastRecordFuture = null;
		}*/
		try {
			mRecorder.stop();
		} catch (Exception e) {
            recordingFailed();
		}
		mRecorder.reset();

		saveAndReturn(currentTimeMillis - lastStartTime);
    }

	private void saveAndReturn(long lengthMs) {
        if (lengthMs < VOICE_MIN_LENGTH) {
            mView.onRecordTooShort();
        } else {
            mView.onRecordSaved(lastRecordPath, lastFileName + VOICE_FILE_POSTFIX, lengthMs);
        }
	}

	@Override
	public synchronized void release() {
        if (mRecorder == null) return;
		mRecorder.reset();
		mRecorder.release();
		mRecorder = null;
        recording.set(false);
	}
	
	private class MaxDurationRunnable implements Runnable {

		@Override
		public void run() {
			onMaxDuration();
		}
		
	}

	public void onMaxDuration() {
		stopRecordingAndSave();
		try {
			startRecording(lastPath, lastFileName + "+");
		} catch (IOException e) {
			e.printStackTrace();
			recordingFailed();
		}
	}

    @Override
    public double getAmplitude() throws IllegalStateException {
        if (mRecorder != null)
            return mRecorder.getMaxAmplitude() / MAX_AMPLITUDE_DIVIDER;
        else
            return 0;
    }

//    public double getAmplitudeEMA() {
//        double amp = getAmplitude();
//        mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
//        return mEMA;
//    }
}
