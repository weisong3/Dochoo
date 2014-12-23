package com.chc.found.presenters;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.google.zxing.client.android.encode.QRCodeEncoder;
import com.google.zxing.BarcodeFormat;


public class QrcodePresenter {
	
	private IQrcodeCallback callback;
  
  public interface IQrcodeCallback {
  	void onEncodeSuccess(Bitmap bitmap);
  	void onEncodeFailed();
  }
  
	public QrcodePresenter(IQrcodeCallback callback) {
		this.callback = callback;
	}
	
	public void encode(final String contents, final int widthAndHeight) {
		new AsyncTask<Void, Void, Bitmap>() {

			@Override
			protected Bitmap doInBackground(Void... params) {
				try {
					return QRCodeEncoder.encodeAsBitmap(contents, BarcodeFormat.QR_CODE, widthAndHeight);
				} catch (Exception e) {
					return null;
				}
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				if (callback != null) {
					if (result != null) callback.onEncodeSuccess(result);
					else callback.onEncodeFailed();
				}
			}
			
			
		}.execute();
	}
}
