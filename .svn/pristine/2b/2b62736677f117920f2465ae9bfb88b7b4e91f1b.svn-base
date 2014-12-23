package com.chc.dialog;

import android.content.Context;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Message;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;

public class DoubleEditTextDialogFactory extends DialogFactory {

	@Override
	public CHCHandler createDialogHandler(Context context, String threadName,
			DialogResult caller) {

		HandlerThread uiThread = new HandlerThread(threadName);
		uiThread.start();
		
		if (caller instanceof DoubleEditTextDialogResult)
			return new DoubleEditTextDialogHandler(uiThread.getLooper(),
					context, (DoubleEditTextDialogResult) caller);
		else
			return null;
		
	}

	public static void handleMsg(CHCHandler handler, int flag, String title,
			String message, String firstLineText, String secondLineText, String defaultText, int id) {
		
		Message msg = handler.obtainMessage(flag);
		Bundle data = new Bundle();
		
		data.putString(DialogFactory.title, title);
		data.putString(DialogFactory.message, message);
		data.putString(DoubleEditTextDialogHandler.TITLE, firstLineText);
		data.putString(DoubleEditTextDialogHandler.CONTENT, secondLineText);
		data.putString(DoubleEditTextDialogHandler.EDIT_TEXT_CONTENT, defaultText);
		data.putInt(DialogFactory.id, id);
		
		msg.setData(data);
		handler.sendMessage(msg);
		
	}
	
	public static void showDialog(
			CHCHandler handler, 
			String title, 
			String firstLineText, 
			String secondLineText,
			String message, 
			String defaultText,
			int id) {
		
		Message msg = handler.obtainMessage(CHCHandler.DISPLAY_DIALOG);
		Bundle data = new Bundle();
		
		data.putString(DialogFactory.title, title);
		data.putString(DialogFactory.message, message);
		data.putString(DoubleEditTextDialogHandler.TITLE, firstLineText);
		data.putString(DoubleEditTextDialogHandler.CONTENT, secondLineText);
		data.putInt(DialogFactory.id, id);
		data.putString(DoubleEditTextDialogHandler.EDIT_TEXT_CONTENT, defaultText);
		
		msg.setData(data);
		handler.sendMessage(msg);
		
	}
}
