package com.chcgp.hpad.util.download;

import java.util.List;

/**
 * This class can help download multiple files in background
 * If any file is present it will resume the download
 *  
 * @author henryw
 *
 */
public class MultiDownloadTask implements DownloadTaskObservable {
	private int nTasks = 0;
	private int nTasksFinshed = 0;
	private DownloadTaskObservable observer;
	private List<String> urls, fileNames;
	private int mProgress = 0;

	public MultiDownloadTask(
			DownloadTaskObservable observer,
			List<String> urls,
			List<String> fileNames) {
		
		this.nTasks = urls.size();
		this.observer = observer;
		this.urls = urls;
		this.fileNames = fileNames;
		if (this.nTasks > 0)
			downloadNext();
	}
	
	public MultiDownloadTask(
			DownloadTaskObservable observer,
			List<String> urls,
			List<String> fileNames,
			boolean isWorkerThread) {

		this.nTasks = urls.size();
		this.observer = observer;
		this.urls = urls;
		this.fileNames = fileNames;
		if (this.nTasks > 0)
			downloadNextWorkerThread();
	}
	
	private void downloadNextWorkerThread() {
		new DownloadTask(
				this, 
				this.urls.get(this.nTasksFinshed), 
				this.fileNames.get(this.nTasksFinshed)).run();
  }

	/**
	 * not recommended
	 */
	public void retry() {
		downloadNext();
	}
	
	private void downloadNext() {
		new DownloadTask(
				this, 
				this.urls.get(this.nTasksFinshed), 
				this.fileNames.get(this.nTasksFinshed)).start();
	}

	@Override
	public void onProgressUpdate(int progress) {
		int temp = (progress + this.nTasksFinshed * 100) / this.nTasks;
		if (temp > mProgress) {
			this.observer.onProgressUpdate(temp);
			mProgress = temp;
		}
	}

	@Override
	public void onFinished() {
		if (++this.nTasksFinshed < this.nTasks)
			downloadNext();
		else
			this.observer.onFinished();
	}

	@Override
	public void onDownloadFailed(int code) {
		this.observer.onDownloadFailed(code);
	}

	@Override
	public void preFinishedActions() {
		
	}
}
