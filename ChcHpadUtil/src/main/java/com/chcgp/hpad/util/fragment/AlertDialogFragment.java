package com.chcgp.hpad.util.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

public class AlertDialogFragment extends DialogFragment {

	private OnButtonsSelectedListener mCallback = null;
	private boolean showed = false;
	
	public static AlertDialogFragment newInstance(String title, String message, int numOfBtns,
			String positiveBtnText, String negativeBtnText, boolean cancelable, OnButtonsSelectedListener callback) {
		AlertDialogFragment adf = new AlertDialogFragment(callback);
		
		Bundle args = new Bundle();
		args.putString("title", title);
		args.putString("message", message);
		args.putString("positiveBtnText", positiveBtnText);
		if (numOfBtns > 1)
			args.putString("negativeBtnText", negativeBtnText);
		args.putInt("nBtns", numOfBtns);
		args.putBoolean("cancelable", cancelable);
		adf.setArguments(args);

		return adf;
	}
	
	public static AlertDialogFragment newInstance(String title, String message, int numOfBtns,
			String positiveBtnText, String negativeBtnText, OnButtonsSelectedListener callback) {
		return newInstance(
				title,
				message,
				numOfBtns,
				positiveBtnText,
				negativeBtnText,
				false,
				callback);
	}
	
	public static AlertDialogFragment newInstance(String title, String message, int numOfBtns,
			String positiveBtnText, String negativeBtnText, boolean cancelable) {
		return newInstance(
				title,
				message,
				numOfBtns,
				positiveBtnText,
				negativeBtnText,
				false,
				null);
	}
	
	public AlertDialogFragment() {
		super();
	}
	
	public AlertDialogFragment(OnButtonsSelectedListener callback) {
		super();
		this.mCallback = callback;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getArguments().getString("title"));
		builder.setMessage(getArguments().getString("message"));
		int numOfBtns = getArguments().getInt("nBtns");
		builder.setPositiveButton(getArguments().getString("positiveBtnText"),
				new OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
				if (null != mCallback)
					mCallback.onPositiveBtnPressedSelected();
				dismiss();
			}
		});
		if (numOfBtns > 1)
			builder.setNegativeButton(getArguments().getString("negativeBtnText"), 
					new OnClickListener() {
				public void onClick(DialogInterface dialog, int arg1) {
					if (null != mCallback)
						mCallback.onNegativeBtnPressedSelected();
					dismiss();
				}
			});
		if(getArguments().getBoolean("cancelable")) {
			setCancelable(true);
		} else {
			setCancelable(false);
		}
		return builder.create(); 
	}
	
	
	
  @Override
  public void onAttach(Activity activity) {
      super.onAttach(activity);
      
      if (mCallback != null) {
    	  return;
      }
      
      if (activity instanceof OnButtonsSelectedListener) {
          mCallback = (OnButtonsSelectedListener) activity;
      } else {
          mCallback = null;
      }
  }
  
  
  
  public void setCallback(OnButtonsSelectedListener callback) {
  		mCallback = callback;
  }
  
  

    @Override
  public void dismiss() {
	  if(!showed) {
		  return;
	  }
	  showed = false;
	  super.dismiss();
  }

  @Override
  public void show(FragmentManager manager, String tag) {
	  if(showed) {
		  return;
	  }
	  showed = true;
	  super.show(manager, tag);
  }



// Container Activity must implement this interface
  public interface OnButtonsSelectedListener {
	  	public void onPositiveBtnPressedSelected();
	  	public void onNegativeBtnPressedSelected();
	  }
	
}
