package com.chc.dialog;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Message;

public class ListViewDialogFactory extends DialogFactory {

	@Override
	public CHCHandler createDialogHandler(Context context, String threadName,
			DialogResult caller) {
		HandlerThread uiThread = new HandlerThread(threadName);
		uiThread.start();
		
		if (caller instanceof ListViewDialogResult)
			return new ListViewDialogHandler(uiThread.getLooper(),
					context, (ListViewDialogResult) caller);
		else
			return null;
	}
	
	public static void handleMsg(CHCHandler handler, int flag, String title,
			String message, ArrayList<String> selectionList, int ResId) {
		
		Message msg = handler.obtainMessage(flag);
		Bundle data = new Bundle();
		
		data.putString(DialogFactory.title, title);
		data.putString(DialogFactory.message, message);
		data.putStringArrayList(ListViewDialogHandler.CONTENT, selectionList);
		data.putInt(ListViewDialogHandler.RESID, ResId);
		
		msg.setData(data);
		handler.sendMessage(msg);
		
	}
	
	public static void showDialog(
			CHCHandler handler, 
			String title,
			String message, ArrayList<String> selectionList,
			int ResId) {
		
		Message msg = handler.obtainMessage(CHCHandler.DISPLAY_DIALOG);
		Bundle data = new Bundle();
		
		data.putString(DialogFactory.title, title);
		data.putString(DialogFactory.message, message);
		data.putStringArrayList(ListViewDialogHandler.CONTENT, selectionList);
		data.putInt(ListViewDialogHandler.RESID, ResId);
		
		msg.setData(data);
		handler.sendMessage(msg);
		
	}
}
