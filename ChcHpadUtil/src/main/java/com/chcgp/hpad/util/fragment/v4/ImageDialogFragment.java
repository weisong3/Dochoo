package com.chcgp.hpad.util.fragment.v4;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chc.R;

public class ImageDialogFragment extends DialogFragment{
	private String message;
	private boolean cancelable;
	private TextView messageTextView;
	private Button positiveButton;
	private Button negativeButton;
	private String positiveText;
	private String negativeText;
	private ImageView imageView;
	private Bitmap bitmap;
	private ImageDialogListener imageDialogListener;
	
	public interface ImageDialogListener {
		public void onPositiveButtonClicked();
		public void onNegativeButtonClicked();
	}
	
	public static ImageDialogFragment newInstance(String message, Bitmap bitmap, String positiveText, String negativeText, boolean cancelable, ImageDialogListener imageDialogListener) {
		ImageDialogFragment imageDialogFragment = new ImageDialogFragment(message, bitmap, positiveText, negativeText, cancelable, imageDialogListener);
		return imageDialogFragment;
	}
	
	public ImageDialogFragment(String message, Bitmap bitmap, String positiveText, String negativeText, boolean cancelable, ImageDialogListener imageDialogListener){
		this.imageDialogListener = imageDialogListener;
		this.positiveText = positiveText;
		this.negativeText = negativeText;
		this.bitmap = bitmap;
		this.message = message;
		this.cancelable = cancelable;
		if(!this.cancelable) {
			this.setCancelable(false);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View progressDialogFragment = inflater.inflate(R.layout.fragment_image_dialog, container, false);
		messageTextView = (TextView) progressDialogFragment.findViewById(R.id.icon_message);
		positiveButton = (Button) progressDialogFragment.findViewById(R.id.positive_button);
		negativeButton = (Button) progressDialogFragment.findViewById(R.id.negative_button);
		imageView = (ImageView) progressDialogFragment.findViewById(R.id.icon_preview);
		messageTextView.setText(message);
		positiveButton.setText(positiveText);
		negativeButton.setText(negativeText);
		imageView.setImageBitmap(bitmap);
		
		positiveButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ImageDialogFragment.this.dismiss();
				imageDialogListener.onPositiveButtonClicked();
			}
		});
		
		negativeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ImageDialogFragment.this.dismiss();
				imageDialogListener.onNegativeButtonClicked();
			}
		});
		return progressDialogFragment;
	}
	
	public void setText(String text) {
		messageTextView.setText(text);
	}
	
}
