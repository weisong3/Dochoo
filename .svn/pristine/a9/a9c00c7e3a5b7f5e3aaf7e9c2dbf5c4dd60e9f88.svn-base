package com.chc.dochoo.contacts;

import android.graphics.Color;
import android.support.v7.appcompat.R;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
/**
 * Created by Lance on 9/19/14.
 */
public class SideBar extends View {
    private char[] l = new char[] { '#','A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
            'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
    private SectionIndexer sectionIndexter = null;
    private ListView list;
    private TextView mDialogText;
    private RelativeLayout sectionToastLayout;
    private float m_nItemHeight ;
    public SideBar(Context context) {
        super(context);
        m_nItemHeight = this.getHeight() / l.length;
    }
    public SideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        m_nItemHeight = this.getHeight() / l.length;
    }

    public SideBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        m_nItemHeight = this.getHeight() / l.length;
    }
    public void setListView(ListView _list) {
        list = _list;
        sectionIndexter = (SectionIndexer) _list.getAdapter();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        m_nItemHeight = parentHeight / l.length;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setTextView(TextView mDialogText) {
        this.mDialogText = mDialogText;
    }

    public void setSectionToastLayout(RelativeLayout sectionToastLayout) {
        this.sectionToastLayout = sectionToastLayout;
    }

    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        int i = (int) event.getY();
        int idx = (int)(i /  m_nItemHeight);
        if (idx >= l.length) {
            idx = l.length - 1;
        } else if (idx < 0) {
            idx = 0;
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            this.setBackgroundColor(Color.LTGRAY);
            mDialogText.setVisibility(View.VISIBLE);
            sectionToastLayout.setVisibility(View.VISIBLE);
            mDialogText.setText(""+l[idx]);
            if (sectionIndexter == null) {
                sectionIndexter = (SectionIndexer) list.getAdapter();
            }
            int position = sectionIndexter.getPositionForSection(l[idx]);
            if (position == -1) {
                return true;
            }
            list.setSelection(position);
        }else{
            this.setBackgroundColor(Color.TRANSPARENT);
            mDialogText.setVisibility(View.INVISIBLE);
            sectionToastLayout.setVisibility(View.INVISIBLE);
        }
        return true;
    }
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(0xff595c61);
        paint.setTextSize(getContext().getResources().getDimensionPixelSize(com.test.found.R.dimen.default_text_size_small));
        paint.setTextAlign(Paint.Align.CENTER);
        float widthCenter = getMeasuredWidth() / 2;
        for (int i = 0; i < l.length; i++) {
            canvas.drawText(String.valueOf(l[i]), widthCenter, m_nItemHeight + (i * m_nItemHeight), paint);
        }
        super.onDraw(canvas);
    }
}
