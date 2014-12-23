package com.chcgp.hpad.util.download;

/**
 * Progress update called via
 * {@link DownloadTaskObservable#onProgressUpdate(int)}, upon finished will call
 * {@link DownloadTaskObservable#onFinished()}, when download failed,
 * {@link DownloadTaskObservable#onDownloadFailed(int)} will be called with
 * error code.
 *  
 * @Error_codes {@link #FILE_NOT_FOUND_ERROR} file path or name
 * error, also could be file permission error.
 * 
 */
public interface DownloadTaskObservable {

	public static final int NETWORK_OR_IO_ERROR = -1;
	public static final int FILE_NOT_FOUND_ERROR = -2;
	public static final int OTHER_ERROR = -3;

	public void onProgressUpdate(int progress);

	public void onFinished();
	
	public void preFinishedActions();

	public void onDownloadFailed(int code);
}
