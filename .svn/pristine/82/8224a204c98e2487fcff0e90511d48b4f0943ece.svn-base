package com.chcgp.hpad.util.fragment.v4;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

public class AlertDialogFragment2 extends DialogFragment
	implements android.content.DialogInterface.OnClickListener {
	
	/**
	 * Unique tag associated with the dialog
	 */
	private String tag;
	
	private static final String CANCELABLE = "cancelable";
	private static final String N_BTNS = "nBtns";
	private static final String NEGATIVE_BTN_TEXT = "negativeBtnText";
	private static final String POSITIVE_BTN_TEXT = "positiveBtnText";
	private static final String NEUTRAL_BTN_TEXT = "neutralBtnText";
	private static final String MESSAGE = "message";
	private static final String TITLE = "title";
	private static final String TAG_TAG = "tag";

	public interface AlertDialogCallback {
		void onPositiveBtnClicked(String tag);
		void onNegativeBtnClicked(String tag);
		void onNeutralBtnClicked(String tag);
	}
	
	/**
	 * show the dialog without checking enclosing activity state
	 * @param fm
	 * @param tag
	 * @param title
	 * @param message
	 * @param numOfBtns
	 * @param positiveBtnText
	 * @param negativeBtnText
	 * @param neutralBtnText
	 * @param cancelable
	 */
	public static void showImmediately(
			FragmentManager fm,
			String tag,
			String title,
			String message,
			int numOfBtns,
			String positiveBtnText,
			String negativeBtnText,
			String neutralBtnText,
			boolean cancelable
			) {
		AlertDialogFragment2 adf = AlertDialogFragment2.newInstance(
				tag,
				title,
				message,
				numOfBtns,
				positiveBtnText,
				negativeBtnText,
				neutralBtnText,
				cancelable);
		DialogFragment f = (DialogFragment) fm.findFragmentByTag(tag);
		if (f != null) f.dismiss();
		
		adf.show(fm, tag);
	}
	
	public static AlertDialogFragment2 newInstance(
			String title,
			String message,
			int numOfBtns,
			String positiveBtnText,
			String negativeBtnText
			) {
		return newInstance(message, title, message, numOfBtns, positiveBtnText, negativeBtnText, null, true);
	}

	/**
	 * returns default alert dialog, cancelable true
	 * calling activity must implement callback if action is needed upon button click
	 * @param id
	 * @param title
	 * @param message
	 * @param numOfBtns
	 * @param positiveBtnText
	 * @param negativeBtnText
	 * @return
	 */
	public static AlertDialogFragment2 newInstance(
			String tag,
			String title,
			String message,
			int numOfBtns,
			String positiveBtnText,
			String negativeBtnText
			) {
		return newInstance(tag, title, message, numOfBtns, positiveBtnText, negativeBtnText, null, true);
	}
	
	/**
	 * calling activity must implement callback if action is needed upon button click
	 * @param id
	 * @param title
	 * @param message
	 * @param numOfBtns
	 * @param positiveBtnText
	 * @param negativeBtnText
	 * 	if numOfBtns == 1 then it is not used and can be null
	 * @param cancelable
	 * @return
	 */
	public static AlertDialogFragment2 newInstance(
			String tag,
			String title,
			String message,
			int numOfBtns,
			String positiveBtnText,
			String negativeBtnText,
			String neutralBtnText,
			boolean cancelable
			) {
		
		AlertDialogFragment2 ad = new AlertDialogFragment2();
		
		Bundle args = new Bundle();
		args.putString(TAG_TAG, tag);
		args.putString(TITLE, title);
		args.putString(MESSAGE, message);
		args.putString(POSITIVE_BTN_TEXT, positiveBtnText);
		if (numOfBtns > 1)
			args.putString(NEGATIVE_BTN_TEXT, negativeBtnText);
		if (numOfBtns == 3)
			args.putString(NEUTRAL_BTN_TEXT, neutralBtnText);
		args.putInt(N_BTNS, numOfBtns);
		args.putBoolean(CANCELABLE, cancelable);
		
		ad.setArguments(args);
		return ad;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		Bundle arguments = getArguments();
		
		this.tag = arguments.getString(TAG_TAG);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		builder.setTitle(arguments.getString(TITLE));
		builder.setMessage(arguments.getString(MESSAGE));
		
		int numOfBtns = arguments.getInt(N_BTNS);
		builder.setPositiveButton(arguments.getString(POSITIVE_BTN_TEXT), this);
		
		if (numOfBtns > 1)
			builder.setNegativeButton(arguments.getString(NEGATIVE_BTN_TEXT), this);
		if (numOfBtns > 2)
			builder.setNeutralButton(arguments.getString(NEUTRAL_BTN_TEXT), this);
		
		setCancelable(arguments.getBoolean(CANCELABLE));
		
		return builder.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		Activity enclosingActivity = getActivity();
		if (enclosingActivity == null
				|| enclosingActivity instanceof AlertDialogCallback == false) {
			// if not callback activity, ignore
			return;
		}
		AlertDialogCallback callback = (AlertDialogCallback) enclosingActivity;
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:
			callback.onPositiveBtnClicked(tag);
			break;
		case DialogInterface.BUTTON_NEGATIVE:
			callback.onNegativeBtnClicked(tag);
			break;
		case DialogInterface.BUTTON_NEUTRAL:
			callback.onNeutralBtnClicked(tag);
			break;
		}
		dismiss();
	}

}
