package com.chc.found.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ResizableImageView extends ImageView {
	private Boolean resizeByHeight = false;

    public ResizableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public ResizableImageView(Context context) {
    	super(context);
    }
    
    public ResizableImageView(Context context, Boolean resizeByHeight) {
    	super(context);
    	this.resizeByHeight = resizeByHeight;
    }

    public void setResizeByHeight(boolean resizeByHeight) {
    	this.resizeByHeight = this.resizeByHeight;
    }
    
    @Override 
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
         Drawable d = getDrawable();

         if(d!=null){
        	 if (resizeByHeight) {
        		// ceil not round - avoid thin vertical gaps along the left/right edges
	    		 int height = MeasureSpec.getSize(heightMeasureSpec);
	    		 int width = (int) Math.ceil((float) height * (float) d.getIntrinsicWidth() /(float) d.getIntrinsicHeight());
	    		 setMeasuredDimension(width, height);
        	 } else {
	    		 // ceil not round - avoid thin vertical gaps along the left/right edges
	    		 int width = MeasureSpec.getSize(widthMeasureSpec);
	    		 int height = (int) Math.ceil((float) width * (float) d.getIntrinsicHeight() / (float) d.getIntrinsicWidth());
	    		 setMeasuredDimension(width, height);
        	 }
         }else{
                 super.onMeasure(widthMeasureSpec, heightMeasureSpec);
         }
    }

}