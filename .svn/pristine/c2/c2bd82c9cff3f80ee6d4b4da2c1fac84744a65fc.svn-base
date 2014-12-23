package com.chc.dialog;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ListViewDialogHandler extends CHCHandler {

	public static final String CONTENT = "secondLine";
	public static final String TITLE = "firstLine";
	public static final String RESID = "resId";
	
	private Dialog dialog;
	private Context mContext;
	private ListViewDialogResult caller;
	private int resId = 0;
	private ListView listView;

	public ListViewDialogHandler(Looper looper, Context context, ListViewDialogResult caller) {
		super(looper);
		this.mContext = context;
		this.caller = caller;
	}
	
	
	
	public void setContext(Context mContext) {
		this.mContext = mContext;
	}
	
	
	
	public void setCaller(ListViewDialogResult caller) {
		this.caller = caller;
	}
	
	

	@Override
	public void handleMessage(final Message msg) {
		switch (msg.what) {
		case DISPLAY_DIALOG:
			Log.i(getClass().getName(), "displaying dialog");
			this.resId = msg.getData().getInt(RESID);
			
			AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
			builder.setTitle(msg.getData().getString(DialogFactory.title));
			if(msg.getData().getString(DialogFactory.message) != null) 
				builder.setMessage(msg.getData().getString(DialogFactory.message));

			listView = new ListView(getContext());
			ArrayList<String> arrayList = msg.getData().getStringArrayList(CONTENT);
			SelectionListViewAdapter adapter = new SelectionListViewAdapter(getContext(), arrayList, resId);
			listView.setBackgroundColor(00000000);
			listView.setAdapter(adapter);
			builder.setView(listView);
			builder.setCancelable(true);
			dialog = builder.create();
			dialog.show();
			break;
		case DISMISS_DIALOG:
			if (dialog != null) {
				dialog.dismiss();
			}
			break;
		default:
			break;
		}
	}
	
	private class SelectionListViewAdapter extends BaseAdapter {
		
		private Context context;
		private ArrayList<String> arrayList;
		private int resId;

		public SelectionListViewAdapter(Context context,
				ArrayList<String> arrayList, int resId) {
			super();
			this.context = context;
			this.arrayList = arrayList;
			this.resId = resId;
		}

		@Override
		public int getCount() {
			return arrayList.size();
		}

		@Override
		public String getItem(int position) {
			return arrayList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				view = new LinearLayout(context);
				view.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));
			}
			TextView subview = (TextView) LayoutInflater.from(context).inflate(resId, parent, false);
			subview.setText(getItem(position));
			((LinearLayout) view).addView(subview);
			view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					getCallback().onItemClicked(position);
					dialog.dismiss();
				}
			});
			return view;
		}
		
	}
	
	public Context getContext() {
		return this.mContext;
	}
	
	public ListView getListView() {
		return this.listView;
	}
	
	public ListViewDialogResult getCallback() {
		return this.caller;
	}
}
