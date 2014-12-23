package com.chc.adaptors;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chc.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CalenderYearListViewAdaptor extends BaseAdapter {

	private Context context;
	private ArrayList<String> yearList;
	private OnItemClickedListener onItemClickedListener;
	
	public interface OnItemClickedListener {
		void onItemClicked(String year);
	}

	public CalenderYearListViewAdaptor(Activity activity, long startDateLong, OnItemClickedListener onItemClickedListener) {
		this.context = activity;
		this.onItemClickedListener = onItemClickedListener;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		Date startDate = new Date(startDateLong);
		String thisYear = sdf.format(new Date());
		int startYearInt = Integer.parseInt(sdf.format(startDate));
		yearList = new ArrayList<String>();
		int thisYearInt = Integer.parseInt(thisYear);
		for(int i = thisYearInt; i >= startYearInt; --i) {
			yearList.add(Integer.toString(i));
		}
	}

	@Override
	public int getCount() {
		return yearList.size();
	}

	@Override
	public String getItem(int position) {
		return yearList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if(view == null) {
			LayoutInflater inflater = (LayoutInflater) context.getApplicationContext().getSystemService
		              (Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.year_listview_item, parent, false);
		}
		TextView tv = (TextView) view.findViewById(R.id.yearlist_item);
		tv.setText(yearList.get(position));
		tv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onItemClickedListener.onItemClicked(yearList.get(position));
			}
		});
		return view;
	}

}
