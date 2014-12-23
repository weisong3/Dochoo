package com.chcgp.hpad.util.download;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * Background download thread supports resumable download. NOTE: please refer to
 * {@link DownloadTaskObservable} for error codes, do not retry download without
 * checking error code!
 * 
 */
public class DownloadTask extends Thread {

	private static final int BUFFER_SIZE = 8192;
	private String fileUrl = "", saveFileName = "";
	private static final int DOWN_UPDATE = 1;
	private static final int DOWN_OVER = 2;
	private static final String TAG = "CHCDownload";
	private DownloadHandler mHandler;
	private int progress = 0;
	private DownloadTaskObservable observer;
	private boolean interrupt = false;

	/**
	 * Background download thread supports resumable download. NOTE: please refer
	 * to {@link DownloadTaskObservable} for error codes, do not retry download
	 * without checking error code!
	 * 
	 * @param observer
	 *          callback object implementing the interface
	 *          {@link DownloadTaskObservable}, can be null:
	 * @param fileUrl
	 *          complete url for the file being downloaded
	 * @param saveFileName
	 *          complete path and file name for the file to be saved to
	 */
	public DownloadTask(
			DownloadTaskObservable observer,
			String fileUrl, 
			String saveFileName) {
		
		this.fileUrl = fileUrl;
		this.saveFileName = saveFileName;
		this.observer = observer;
		this.mHandler = new DownloadHandler();
	}
	
	
	
	/**
	 * If the calling thread is not UI thread or does not have
	 * looper, need to explicitly call Looper.prepare() and Looper.loop()
	 * and provide Looper.myLooper() to this constructor
	 * 
	 * @param observer
	 * @param fileUrl
	 * @param saveFileName
	 * @param looper
	 */
	public DownloadTask(
			DownloadTaskObservable observer, 
			String fileUrl, 
			String saveFileName,
			Looper looper) {
		
		this.fileUrl = fileUrl;
		this.saveFileName = saveFileName;
		this.observer = observer;
		this.mHandler = new DownloadHandler(looper);
	}

	public void interrupt() {
		this.interrupt = true;
	}

	public void run() {
		RandomAccessFile fos = null;
		BufferedInputStream bis = null;
		HttpURLConnection conn = null;
		long remoteLength = 0;

		try {
			File file = new File(saveFileName.substring(0, saveFileName.lastIndexOf("/") + 1));
			if (!file.exists()) {
				file.mkdirs();
			}
			URL url = new URL(fileUrl);

			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Referer", "http://www.chcgp.com.cn/");
			conn.connect();
			remoteLength = conn.getContentLength();
			conn.disconnect();
			
			if(remoteLength < 0){
				HttpClient httpclient = new DefaultHttpClient();
				HttpGet httpget = new HttpGet(fileUrl); 
				HttpResponse response = httpclient.execute(httpget);
				remoteLength = (long) response.getEntity().getContentLength();
			}

			long fileLength = 0;
			File checkfile = new File(saveFileName);
			fos = new RandomAccessFile(checkfile, "rw");
			if (checkfile.exists() && checkfile.isFile()) {
				// if already exists, check the size
				fileLength = checkfile.length();
				if (fileLength < remoteLength) {
					// resume download
					if (fileLength != 0) {
						fos.seek(fileLength);
					}
				} else if (fileLength > remoteLength) {
					// corrupted file, delete
					if (checkfile.delete()) {
						// delete succeeded, directly download
					} else {
						checkfile.delete();
						failed();
						return;
					}
				} else {
					// no need to download, go to install step
					progress = 100;
					mHandler.sendEmptyMessage(DOWN_UPDATE);
					mHandler.sendEmptyMessage(DOWN_OVER);
					return;
				}
			} else {
				// no file, directly download
			}
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("RANGE", "bytes=" + fileLength + "-");
			conn.setRequestProperty("Referer", "http://www.chcgp.com.cn/");
			conn.connect();
			bis = new BufferedInputStream(conn.getInputStream());
			long count = fileLength;
			byte buf[] = new byte[BUFFER_SIZE];
			Log.i(TAG, "Reading from input stream");
			int previousProgress = 0;
			do {
				int numread = bis.read(buf, 0, BUFFER_SIZE);
				if (numread <= 0) {
					mHandler.sendEmptyMessage(DOWN_OVER);
					break;
				}
				count += numread;
				progress = (int) (((double) count / remoteLength) * 100);
				if (progress > previousProgress) {
					mHandler.sendEmptyMessage(DOWN_UPDATE);
					previousProgress = progress;
				}
				fos.write(buf, 0, numread);
			} while (!interrupt);
			Log.i(TAG, count + " bytes read");
		} catch (MalformedURLException e) {
			Log.w(TAG, "Http Error", e);
			failed();
		} catch (java.io.FileNotFoundException e) {
			Log.w(TAG, "Possible file permission error", e);
			failed(DownloadTaskObservable.FILE_NOT_FOUND_ERROR);
		} catch (IOException e) {
			Log.w(TAG, "IO Error", e);
			failed();
		} catch (Exception e) {
			Log.w(TAG, "Other Error", e);
			failed(DownloadTaskObservable.OTHER_ERROR);
		} finally {
			if (conn != null)
				conn.disconnect();
			closeQuietly(fos);
			closeQuietly(bis);
		}
	}
	
	public static void closeQuietly(Closeable closeable) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (IOException ioe) {
			// ignore
		}
	}
	
	private void failed() {
		failed(DownloadTaskObservable.NETWORK_OR_IO_ERROR);
	}

	private void failed(int code) {
		if (observer != null)
			observer.onDownloadFailed(code);
	}

	private class DownloadHandler extends Handler {

		public DownloadHandler(Looper looper) {
			super(looper);
		}

		public DownloadHandler() {
			super();
		}

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWN_UPDATE:
				if (observer != null)
					observer.onProgressUpdate(progress);
				break;
			case DOWN_OVER:
				if (observer != null)
					observer.onFinished();
				break;
			default:
				break;
			}
		}

	}
}