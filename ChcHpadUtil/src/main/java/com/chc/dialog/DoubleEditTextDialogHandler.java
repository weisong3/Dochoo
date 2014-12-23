package com.chc.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.chc.R;

public class DoubleEditTextDialogHandler extends CHCHandler {

	public static final String EDIT_TEXT_CONTENT = "text_content";
	public static final String EDIT_TEXT_TITLE = "text_title";
	public static final String CONTENT = "secondLine";
	public static final String TITLE = "firstLine";

	private Dialog dialog;
	private Context mContext;
	private DoubleEditTextDialogResult caller;
	private int id = 0;

	public DoubleEditTextDialogHandler(Looper looper, Context context, DoubleEditTextDialogResult caller) {
		super(looper);
		this.mContext = context;
		this.caller = caller;
	}
	
	private static OnFocusChangeListener listener = new OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if(hasFocus) {
				((EditText) v).setText("");
			}
		}
	};

	@Override
	public void handleMessage(final Message msg) {
		
		String title = msg.getData().getString(TITLE);
		String content = msg.getData().getString(CONTENT);
		String title_text = msg.getData().getString(EDIT_TEXT_CONTENT);
		String content_text = msg.getData().getString(EDIT_TEXT_TITLE);
		
		switch (msg.what) {
		case DISPLAY_DIALOG:
			Log.i(getClass().getName(), "displaying dialog");
			this.id = msg.getData().getInt(DialogFactory.id);
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
			builder.setTitle(msg.getData().getString(DialogFactory.title));
			if(msg.getData().getString(DialogFactory.message) != null) 
				builder.setMessage(msg.getData().getString(DialogFactory.message));

			LayoutInflater factory = LayoutInflater.from(this.mContext);
			final View textEntryView = factory.inflate(R.layout.double_alert_dialog_text_entry, null);
			final TextView titleTextView = (TextView) textEntryView.findViewById(R.id.title_view);
			final EditText titleEditText = (EditText) textEntryView.findViewById(R.id.title_text_edit);
			final TextView contentTextView = (TextView) textEntryView.findViewById(R.id.content_view);
			final EditText contentEditText = (EditText) textEntryView.findViewById(R.id.content_text_edit);
			
			titleTextView.setText(title);
			contentTextView.setText(content);
			titleEditText.setText(title_text);
			contentEditText.setText(content_text);
			titleEditText.setOnFocusChangeListener(listener);
			contentEditText.setOnFocusChangeListener(listener);
			
			builder.setView(textEntryView);

			builder.setPositiveButton("确定", new OnClickListener() {
				public void onClick(DialogInterface dialog, int arg1) {
					String temp1 = new String();
					String temp2 = new String();
					if (titleEditText != null)
						temp1 = titleEditText.getText().toString();
					if (contentEditText != null)
						temp2 = contentEditText.getText().toString();
					dialog.dismiss();
					caller.onTextEntered(temp1, temp2, id);
				}
			});
			builder.setNegativeButton("取消", new OnClickListener() {
				public void onClick(DialogInterface dialog, int arg1) {
					if (titleEditText != null)
						titleEditText.clearComposingText();
					if (contentEditText != null)
						contentEditText.clearComposingText();
					dialog.dismiss();
					caller.onTextCancelled(id);
				}
			});
			builder.setCancelable(true);
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
}
