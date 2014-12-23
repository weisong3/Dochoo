package com.chc.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.EditText;

public class AutoFitBgEditText extends EditText {

	public AutoFitBgEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	public AutoFitBgEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public AutoFitBgEditText(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * measures according to its background
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Bitmap bitmap = ((BitmapDrawable)this.getBackground()).getBitmap();
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		
		setMeasuredDimension(width, height);
	}

}
