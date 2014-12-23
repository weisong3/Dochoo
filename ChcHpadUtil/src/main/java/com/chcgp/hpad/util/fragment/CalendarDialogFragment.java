package com.chcgp.hpad.util.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.chc.R;
import com.chc.adaptors.CalenderYearListViewAdaptor;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CalendarDialogFragment extends DialogFragment {

	private TextView mMonthNameTextView = null;
	private OnButtonsSelectedListener mCallback = null;
	private CalendarView calendarView;
	private ListView yearListView;
	
	private String dateString;
	
	public CalendarDialogFragment() {
		super();
	}

	/**
	 * 创建一个可以用来选择日期的日历
	 * 
	 * @param title 对话框的上面显示的标题
	 * @param defaultDate 创建对话框时默认选择的日期， 如果是null则显示设备上的当前时间
	 * @param message 对话框中的文字描述内容，如果是null则不显示
	 * @param numOfBtns 对话框的选项按钮数量，可以有一个或者两个，（值是"1"或者"2"）
	 * @param positiveBtnText 对话框中表示确定的选项按钮上的文字， 如（"是" ，"确定" ）
	 * @param negativeBtnText 对话框中表示否定的选项按钮上的文字， 如（"否" ，"取消"， 当numOfBtms的值是"1"时，这个值为空字符串
	 * @param callback 点击选项按钮后的回调， 是OnButtonsSelectedListener这个Interface的实例
	 * @return
	 */
	public static CalendarDialogFragment newInstance(String title, Date defaultDate, String message, int numOfBtns,
			String positiveBtnText, String negativeBtnText, OnButtonsSelectedListener callback) {
		CalendarDialogFragment cdf = new CalendarDialogFragment();
		cdf.setCallback(callback);
		Bundle args = new Bundle();
		args.putString("title", title);
		args.putLong("defaultDate", defaultDate.getTime());
		args.putString("message", message);
		args.putString("positiveBtnText", positiveBtnText);
		if (numOfBtns > 1)
			args.putString("negativeBtnText", negativeBtnText);
		args.putInt("nBtns", numOfBtns);
		cdf.setArguments(args);
		return cdf;
	}
	
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		LayoutInflater inflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService
	              (Context.LAYOUT_INFLATER_SERVICE);
		View calendarDialogView = inflater.inflate(R.layout.fragment_calendar_dialog, null, false);
		calendarView = (CalendarView) calendarDialogView.findViewById(R.id.calendarview);
		yearListView = (ListView) calendarDialogView.findViewById(R.id.listview_year);
		yearListView.setAdapter(new CalenderYearListViewAdaptor(getActivity(), calendarView.getMinDate(), new CalenderYearListViewAdaptor.OnItemClickedListener() {
			
			@Override
			public void onItemClicked(String year) {
				Date oldDate = new Date(calendarView.getDate());
				SimpleDateFormat sdf = new SimpleDateFormat("M/d");
				String oldDateString = sdf.format(oldDate);
				String newDateString = year + "/" + oldDateString;
				sdf = new SimpleDateFormat("yyyy/M/d");
				Date newDate = null;
				try {
					newDate = sdf.parse(newDateString);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				calendarView.setDate(newDate.getTime());
				yearListView.setVisibility(View.GONE);
			}
		}));
		try
	    {
	        Class<?> cvClass = calendarView.getClass();
	        Field mMonthName = cvClass.getDeclaredField("mMonthName");
	        Field mDayNamesHeader = cvClass.getDeclaredField("mDayNamesHeader");
	        mMonthName.setAccessible(true);
	        mDayNamesHeader.setAccessible(true);
	        try
	        {
	            mMonthNameTextView = (TextView) mMonthName.get(calendarView);
	            mMonthNameTextView.setTextColor(getResources().getColor(android.R.color.black));
	            mMonthNameTextView.setTextSize((int) getActivity().getResources().getDimension(R.dimen.large_font_size));
	    		mMonthNameTextView.setGravity(Gravity.CENTER);
	    		mMonthNameTextView.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.calendar_bar));
	    		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int) getActivity().getResources().getDimension(R.dimen.calenderpicker_width),
	    				LayoutParams.WRAP_CONTENT);
	    		lp.setMargins(0, 10, 0, 10);
	    		mMonthNameTextView.setLayoutParams(lp);
	            
	            ViewGroup vg = (ViewGroup) mDayNamesHeader.get(calendarView);
	            for (int i = 1, count = vg.getChildCount(); i < count; i++) {
	                TextView label = (TextView) vg.getChildAt(i);
	                if (label.getVisibility() == View.VISIBLE) {
	                    label.setTextColor(getResources().getColor(android.R.color.black));
	                }
	            }
	        } 
	        catch (IllegalArgumentException e)
	        {
	            e.printStackTrace();
	        }
	        catch (IllegalAccessException e)
	        {
	            e.printStackTrace();
	        }
	    }
	    catch (NoSuchFieldException e)
	    {
	        e.printStackTrace();
	    }
		
		calendarView.setOnDateChangeListener(new OnDateChangeListener() {
			
			@Override
			public void onSelectedDayChange(CalendarView view, int year, int month,
					int dayOfMonth) {
				int realMonth = month + 1;
				dateString = year + "年" + realMonth + "月" + dayOfMonth + "日";
				mMonthNameTextView.setText(year + "年" + realMonth + "月");
			}
		});
		
		Long defaultDate = getArguments().getLong("defaultDate");
		if(defaultDate == null) {
			defaultDate = new Date().getTime();
		}
		calendarView.setDate(defaultDate);
		mMonthNameTextView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				yearListView.setVisibility(View.VISIBLE);
			}
		});
			
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getArguments().getString("title"));
		if(getArguments().getString("message") != null) {
			builder.setMessage(getArguments().getString("message"));
		}
		builder.setView(calendarDialogView);
		int numOfBtns = getArguments().getInt("nBtns");
		builder.setPositiveButton(getArguments().getString("positiveBtnText"),
				new OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
				if (null != mCallback)
					mCallback.onPositiveBtnPressedSelected(dateString);
				dialog.dismiss();
			}
		});
		if (numOfBtns > 1)
			builder.setNegativeButton(getArguments().getString("negativeBtnText"), 
					new OnClickListener() {
				public void onClick(DialogInterface dialog, int arg1) {
					if (null != mCallback)
						mCallback.onNegativeBtnPressedSelected();
					dialog.dismiss();
				}
			});
		this.setCancelable(false);
		return builder.create(); 
	}
  
  
  public void setCallback(OnButtonsSelectedListener callback) {
  	mCallback = callback;
  }
  
  // Container Activity must implement this interface
  /**
   * 在日历上选择日期后的回调， 
   * onPositiveBtnPressedSelected(String dateString); 这里的dateString是选择的日期对应的字符串，格式为"yyyy年M月d日"（如"2013年1月28日"）
   */
  public interface OnButtonsSelectedListener {
  	public void onPositiveBtnPressedSelected(String dateString);
  	public void onNegativeBtnPressedSelected();
  }
	
}
