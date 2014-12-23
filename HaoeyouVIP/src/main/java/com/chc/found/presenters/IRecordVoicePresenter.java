package com.chc.found.presenters;

import java.io.IOException;

public interface IRecordVoicePresenter {
	/**
	 * Starts recording
	 */
	void startRecording(String path, String filename) throws IOException;
	
	/**
	 * Cancel recording and discard 
	 */
	void cancelRecording();
	
	/**
	 * Stop recording and save the file
	 * @return the absolute path string to the saved audio file
	 */
	void stopRecordingAndSave() throws IOException;
	
	/**
	 * Release any resources held by the presenter
	 */
	void release();

    double getAmplitude();
	
}
