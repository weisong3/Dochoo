package com.chc.listeners;

import android.view.GestureDetector;
import android.view.MotionEvent;

public class LeftRightUpDownGestureFlingListener extends GestureDetector.SimpleOnGestureListener {
	private static final int SWIPE_MIN_DISTANCE = 40;
	private static final int SWIPE_THRESHOLD_VELOCITY = 150;
	private Callback callback;
	
	public LeftRightUpDownGestureFlingListener(Callback callback) {
		this.callback = callback;
	}
	
	public static interface Callback {
		void onLeftFling();
		void onRightFling();
		void onUpFling();
		void onDownFling();
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		
		float dx = e2.getX() - e1.getX();
		float dy = e2.getY() - e1.getY();
		
		boolean upCriterion1 = dy < -SWIPE_MIN_DISTANCE
				&& Math.abs(dy) > Math.abs(dx);
		boolean downCriterion1 = dy > SWIPE_MIN_DISTANCE
				&& Math.abs(dy) > Math.abs(dx);
		boolean leftCriterion1 = dx < -SWIPE_MIN_DISTANCE
				&& Math.abs(dx) > Math.abs(dy);
		boolean rightCriterion1 = dx > SWIPE_MIN_DISTANCE
				&& Math.abs(dx) > Math.abs(dy);
				
		boolean result = false;
		if (Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
			// left and right
			if (leftCriterion1) {
				this.callback.onLeftFling();
				result = true;
			}
			else if (rightCriterion1) {
				this.callback.onRightFling();
				result = true;
			}
		}	else if (Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
			// up and down
			if (upCriterion1) {
				this.callback.onUpFling();
				result = true;
			}
			else if (downCriterion1) {
				this.callback.onDownFling();
				result = true;
			}
		}
		if (result)
			return true;
		else
			return super.onFling(e1, e2, velocityX, velocityY);
	}
}