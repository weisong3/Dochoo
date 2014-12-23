package com.chc.found.views;


import com.devsmart.android.ui.HorizontalListView;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomViewPager extends ViewPager {
    private int childId;

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
     @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (childId > 0 && findViewById(childId) instanceof HorizontalListView) {          
            HorizontalListView horizontalListView = (HorizontalListView)findViewById(childId);

            if (horizontalListView != null) {           
            	horizontalListView.requestDisallowInterceptTouchEvent(true);
            }

        }

        return super.onInterceptTouchEvent(event);
    }

    public void setChildId(int id) {
        this.childId = id;
    }
}