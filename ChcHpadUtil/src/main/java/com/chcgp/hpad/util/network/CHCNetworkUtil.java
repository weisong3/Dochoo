package com.chcgp.hpad.util.network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Log;

public class CHCNetworkUtil {
	private static final String TAG = CHCNetworkUtil.class.getSimpleName();
	private static final int BUFFER_SIZE = 1024 * 4;

	public static boolean isNetworkAvailable(Context context) {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	public static boolean isWiFiAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return mWifi.isConnected();
	}
	
  public static void goToWifiSetting(Context context) {
  	context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
  }

	public static boolean uploadFileRange(URI uri, java.io.File targetFile, long start, long end) {
		boolean result = false;

		HttpURLConnection conn = null;
		DataOutputStream os = null;
		try {
			final int bufferSize = 1024 * 64;
			byte[] buffer = new byte[bufferSize];
			long totalBytesToRead = end - start, totalBytesRead = 0;
			RandomAccessFile raf = new RandomAccessFile(targetFile, "r");
			int readSize;

			// String lineEnd = "\r\n";
			// String twoHyphens = "--";
			// String boundary = "***CHC_HPAD_UPLOAD***";

			conn = (HttpURLConnection) uri.toURL().openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);

			// Use a post method.
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "multipart/mixed");
			conn.setRequestProperty("Range", start + "-" + end);

			os = new DataOutputStream(conn.getOutputStream());

			// os.writeBytes(twoHyphens + boundary + lineEnd);
			// os.writeBytes("Content-Type: application/jpg "+ lineEnd);
			// os.writeBytes(lineEnd);
			// read file and write it into form...
			try {
				raf.seek(start);
				do {
					readSize = raf.read(buffer, 0, bufferSize);
					if (readSize > 0) {
						os.write(buffer, 0, readSize);
					}
					totalBytesRead += readSize;
					if (totalBytesRead >= totalBytesToRead) {
						break;
					}
				} while (readSize > 0);
			} catch (Exception e) {
				result = false;
				Log.e(TAG, e.getMessage(), e);
			} finally {
				raf.close();
			}
			os.flush();
			os.close();
		} catch (Exception e) {
			result = false;
			Log.e(TAG, e.getMessage(), e);
		} finally {
			try {
				if (conn.getResponseCode() / 100 == 2) {
					result = true;
				}
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
				result = false;
			}
			conn.disconnect();
		}

		return result;
	}
	

	public static boolean downloadFile(File file, URL url) throws IOException {
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
			bis = new BufferedInputStream(conn.getInputStream());
			bos = new BufferedOutputStream(new FileOutputStream(file));

			int contentLength = conn.getContentLength();
			
			byte[] buffer = new byte[BUFFER_SIZE];
			int read = 0;
			int count = 0;
			while ((read = bis.read(buffer)) != -1) {
				bos.write(buffer, 0, read);
				count += read;
			}
			if (count < contentLength) return false;
			int responseCode = conn.getResponseCode();
			if (responseCode / 100 != 2) {
				// error
				return false;
			}
			// finished
			return true;
		} finally {
			try {
				bis.close();
				bos.close();
				conn.disconnect();
			} catch (Exception e) {}
		}
	}

}
