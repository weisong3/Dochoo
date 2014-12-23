package com.chcgp.hpad.util.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.chc.R;

public class ProgressDialogFragment extends DialogFragment {

	private ProgressBar pb;

	private boolean showed = false;

	public static ProgressDialogFragment newInstance(String message,
			boolean cancelable, boolean indeterminate) {
		return ProgressDialogFragment.newInstance(null, message, cancelable,
				indeterminate, -1, -1);
	}

	public static ProgressDialogFragment newInstance(String message,
			boolean cancelable, boolean indeterminate, int textColor,
			int textSize) {
		return ProgressDialogFragment.newInstance(null, message, cancelable,
				indeterminate, textColor, textSize);
	}

	public static ProgressDialogFragment newInstance(String title,
			String message, boolean cancelable, boolean indeterminate,
			int textColor, int textSize) {
		ProgressDialogFragment progressDialogFragment = new ProgressDialogFragment();

		Bundle args = new Bundle();
		args.putString("title", title);
		args.putString("message", message);
		args.putBoolean("cancelable", cancelable);
		args.putBoolean("indeterminate", indeterminate);
		args.putInt("textColor", textColor);
		args.putInt("textSize", textSize);
		progressDialogFragment.setArguments(args);

		return progressDialogFragment;
	}

	public static ProgressDialogFragment newInstance(String message,
			boolean cancelable) {
		return ProgressDialogFragment.newInstance(null, message, cancelable,
				true, -1, -1);
	}

	public ProgressDialogFragment() {
		super();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		if (getArguments().getString("title") != null) {
			builder.setTitle(getArguments().getString("title"));
		}

		if (getArguments().getString("message") != null) {
			builder.setMessage(getArguments().getString("message"));
		}

		LayoutInflater factory = LayoutInflater.from(getActivity());
		final View view = factory.inflate(
				R.layout.fragment_progress_bar_dialog, null);
		pb = (ProgressBar) view.findViewById(R.id.progress_bar);
		if (getArguments().getBoolean("cancelable")) {
			setCancelable(true);
		} else {
			setCancelable(false);
		}
		if (getArguments().getBoolean("indeterminate")) {
			pb.setIndeterminate(true);
		} else {
			pb.setIndeterminate(false);
		}

		builder.setView(view);

		return builder.create();
	}

	public void onProgressUpdate(int progress) {
		pb.setProgress(progress);
	}

	@Override
	public void dismiss() {
		if (!showed) {
			return;
		}
		showed = false;
		super.dismiss();
	}

	@Override
	public void show(FragmentManager manager, String tag) {
		if (showed) {
			return;
		}
		showed = true;
		super.show(manager, tag);
	}
}
