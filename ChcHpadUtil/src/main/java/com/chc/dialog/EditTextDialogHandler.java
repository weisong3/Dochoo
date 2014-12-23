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
import android.widget.EditText;

import com.chc.R;

public class EditTextDialogHandler extends CHCHandler {

	public static final String EDIT_TEXT_CONTENT = "text_content";

	private Dialog dialog;
	private Context mContext;
	private EditTextDialogResult caller;
	private int id = 0;

	public EditTextDialogHandler(Looper looper, Context context, EditTextDialogResult caller) {
		super(looper);
		this.mContext = context;
		this.caller = caller;
	}

	@Override
	public void handleMessage(final Message msg) {
		
		String text = msg.getData().getString(EDIT_TEXT_CONTENT);
		
		switch (msg.what) {
		case DISPLAY_DIALOG:
			Log.i(getClass().getName(), "displaying dialog");
			this.id = msg.getData().getInt(DialogFactory.id);
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
			builder.setTitle(msg.getData().getString(DialogFactory.title));
			builder.setMessage(msg.getData().getString(DialogFactory.message));

			LayoutInflater factory = LayoutInflater.from(this.mContext);
			final View textEntryView = factory.inflate(R.layout.alert_dialog_text_entry, null);
			final EditText input = (EditText) textEntryView.findViewById(R.id.alert_text_edit);
			
			input.setText(text);
			
			builder.setView(textEntryView);

			builder.setPositiveButton("Yes", new OnClickListener() {
				public void onClick(DialogInterface dialog, int arg1) {
					String temp = new String();
					if (input != null)
						temp = input.getText().toString();
					dialog.dismiss();
					caller.onTextEntered(temp, id);
				}
			});
			builder.setNegativeButton("No", new OnClickListener() {
				public void onClick(DialogInterface dialog, int arg1) {
					if (input != null)
						input.clearComposingText();
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
