package com.chc.dialog;

import android.content.Context;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Message;

public class EditTextDialogFactory extends DialogFactory {

	@Override
	public CHCHandler createDialogHandler(Context context, String threadName,
			DialogResult caller) {

		HandlerThread uiThread = new HandlerThread(threadName);
		uiThread.start();
		
		if (caller instanceof EditTextDialogResult)
			return new EditTextDialogHandler(uiThread.getLooper(),
					context, (EditTextDialogResult) caller);
		else
			return null;
		
	}

	public static void handleMsg(CHCHandler handler, int flag, String title,
			String message, String defaultText, int id) {
		
		Message msg = handler.obtainMessage(flag);
		Bundle data = new Bundle();
		
		data.putString(DialogFactory.title, title);
		data.putString(DialogFactory.message, message);
		data.putString(EditTextDialogHandler.EDIT_TEXT_CONTENT, defaultText);
		data.putInt(DialogFactory.id, id);
		
		msg.setData(data);
		handler.sendMessage(msg);
		
	}
	
	public static void showDialog(
			CHCHandler handler, 
			String title, 
			String message, 
			String defaultText,
			int id) {
		
		Message msg = handler.obtainMessage(CHCHandler.DISPLAY_DIALOG);
		Bundle data = new Bundle();
		
		data.putString(DialogFactory.title, title);
		data.putString(DialogFactory.message, message);
		data.putInt(DialogFactory.id, id);
		data.putString(EditTextDialogHandler.EDIT_TEXT_CONTENT, defaultText);
		
		msg.setData(data);
		handler.sendMessage(msg);
		
	}
}
