package com.chcgp.hpad.util.fragment.v4;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.ListView;
import android.widget.TextView;

import com.chc.R;
import com.chc.adaptors.CalenderYearListViewAdaptor;
import com.chc.adaptors.CalenderYearListViewAdaptor.OnItemClickedListener;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CalendarDialogFragment extends DialogFragment {

	private TextView mMonthNameTextView = null;
	private OnButtonsSelectedListener mCallback = null;
	private CalendarView calendarView;
	private ListView yearListView;
	protected String dateString;
	
	public CalendarDialogFragment() {
		super();
	}

	public static CalendarDialogFragment newInstance(String title, Date defaultDate, String message, int numOfBtns,
			String positiveBtnText, String negativeBtnText, OnButtonsSelectedListener callback) {
		CalendarDialogFragment cdf = new CalendarDialogFragment();
		cdf.setCallback(callback);
		Bundle args = new Bundle();
		args.putString("title", title);
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
		yearListView.setAdapter(new CalenderYearListViewAdaptor(getActivity(), calendarView.getMinDate(), new OnItemClickedListener() {
			
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
	            mMonthNameTextView.setTextSize(30);
	            
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
				// TODO Auto-generated method stub
				int realMonth = month + 1;
				dateString = year + "年" + realMonth + "月" + dayOfMonth + "日";
				mMonthNameTextView.setText(year + "年" + realMonth + "月");
			}
		});
		
		Date now = new Date();
		calendarView.setDate(now.getTime());
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
//		this.setCancelable(false);
		return builder.create(); 
	}
  
  
  public void setCallback(OnButtonsSelectedListener callback) {
  	mCallback = callback;
  }
  
  // Container Activity must implement this interface
  public interface OnButtonsSelectedListener {
  	public void onPositiveBtnPressedSelected(String dateString);
  	public void onNegativeBtnPressedSelected();
  }
	
}
