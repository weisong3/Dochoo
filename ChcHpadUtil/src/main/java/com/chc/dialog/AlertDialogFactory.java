package com.chc.dialog;

import android.content.Context;
import android.os.HandlerThread;

public class AlertDialogFactory extends DialogFactory {
	

	@Override
	public CHCHandler createDialogHandler(Context context, String threadName,
			DialogResult caller) {
		HandlerThread uiThread = new HandlerThread(threadName);
		uiThread.start();
		
		if (caller instanceof AlertDialogResult)
			return new AlertDialogHandler(uiThread.getLooper(),
					context, (AlertDialogResult) caller);
		else
			return null;
	}
	
}
