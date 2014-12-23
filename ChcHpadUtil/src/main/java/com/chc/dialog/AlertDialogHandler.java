package com.chc.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;

public class AlertDialogHandler extends CHCHandler {

	private Dialog dialog;
	private Context mContext;
	private AlertDialogResult caller;
	private int id = 0;

	public AlertDialogHandler(Looper looper, Context context, AlertDialogResult caller) {
		super(looper);
		this.mContext = context;
		this.caller = caller;
	}
	
	
	
	public void setContext(Context mContext) {
		this.mContext = mContext;
	}
	
	
	
	public void setCaller(AlertDialogResult caller) {
		this.caller = caller;
	}
	
	

	@Override
	public void handleMessage(final Message msg) {
		Bundle data = msg.getData();
		switch (msg.what) {
		case DISPLAY_DIALOG:
			Log.i(getClass().getName(), "displaying dialog");
			AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
			builder.setTitle(data.getString(DialogFactory.title));
			builder.setMessage(data.getString(DialogFactory.message));
			this.id = data.getInt(DialogFactory.id);
			String positiveText = data.getString(DialogFactory.positiveBtn);
			if (positiveText == null) {
				positiveText = "Yes"; 
			}
			builder.setPositiveButton(positiveText, new OnClickListener() {
				public void onClick(DialogInterface dialog, int arg1) {
					dialog.dismiss();
					caller.onConfirmed(id);
				}
			});
			String negativeText = data.getString(DialogFactory.negativeBtn);
			if (null == negativeText) {
				negativeText = "No";
			}
			builder.setNegativeButton(negativeText, new OnClickListener() {
				public void onClick(DialogInterface dialog, int arg1) {
					dialog.dismiss();
					caller.onCancelled(id);
				}
			});
			builder.setCancelable(false);
			dialog = builder.create();
			dialog.show();
			break;
		case DISMISS_DIALOG:
			if (dialog != null) {
				dialog.dismiss();
			}
			break;
		default:
			break;
		}
	}
	
	
	
	public static class NoNetworkAlertCallback implements AlertDialogResult {
		
		private Context mContext;
		
		public NoNetworkAlertCallback(Context mContext) {
			this.mContext = mContext;
		}

		@Override
		public void onConfirmed(int id) {
			WifiManager wm = (WifiManager) mContext.
					getSystemService(Context.WIFI_SERVICE);
			wm.setWifiEnabled(true);
			mContext.startActivity(
					new Intent(Settings.ACTION_WIFI_SETTINGS));
		}

		@Override
		public void onCancelled(int id) {
		}
		
	}



	public Context getContext() {
		return this.mContext;
	}
	
	
	
	public AlertDialogResult getCallback() {
		return this.caller;
	}
}
