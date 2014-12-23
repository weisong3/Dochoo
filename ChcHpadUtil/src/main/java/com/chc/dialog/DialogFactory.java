package com.chc.dialog;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;

public abstract class DialogFactory {

	public static final String title = "Title";
	public static final String message = "Message";
	public static final String positiveBtn = "Positive";
	public static final String negativeBtn = "Negative";
	public static final String defaultText = "DefaultText";
	public static final String id = "Id";
	
	
	public abstract CHCHandler createDialogHandler(
			Context context, 
			String threadName, 
			DialogResult caller);
	
	
	

	/**
	 * replaced by
	 * {@link #showDialog(CHCHandler, String, String, String, int)}
	 * and {@link #dismissDialog(CHCHandler)}
	 * @param handler
	 * @param flag
	 * @param title
	 * @param message
	 * @param defaultText
	 * @param id
	 */
	@Deprecated
	public static void handleMsg(CHCHandler handler, int flag, String title,
			String message, String defaultText, int id) {
		
		Message msg = handler.obtainMessage(flag);
		Bundle data = new Bundle();
		
		data.putString(DialogFactory.title, title);
		data.putString(DialogFactory.message, message);
		data.putInt(DialogFactory.id, id);
		// ignore defaultText
		
		msg.setData(data);
		handler.sendMessage(msg);
	}
	
	
	
	public static void showDialog(
			CHCHandler handler, 
			String title, 
			String message, 
			String defaultText,
			int id) {
		
		showDialog(handler, title, message, null, null, defaultText, id);
	}
	
	public static void showDialog(
			CHCHandler handler, 
			String title, 
			String message, 
			String positiveBtn,
			String negativeBtn,
			String defaultText,
			int id) {
		
		Message msg = handler.obtainMessage(CHCHandler.DISPLAY_DIALOG);
		Bundle data = new Bundle();
		
		data.putString(DialogFactory.title, title);
		data.putString(DialogFactory.message, message);
		data.putString(DialogFactory.positiveBtn, positiveBtn);
		data.putString(DialogFactory.negativeBtn, negativeBtn);
		data.putString(DialogFactory.defaultText, defaultText);
		data.putInt(DialogFactory.id, id);
		
		msg.setData(data);
		handler.sendMessage(msg);
		
	}
	
	
	
	public static void dismissDialog(CHCHandler handler) {
		
		Message msg = handler.obtainMessage(CHCHandler.DISMISS_DIALOG);
		Bundle data = new Bundle();
		
		msg.setData(data);
		handler.sendMessage(msg);
		
	}

}
