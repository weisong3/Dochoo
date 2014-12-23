package com.chc.found.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chc.found.config.Apis;
import com.chc.dochoo.conversations.NewsMessage;
import com.chcgp.hpad.util.download.ImageDownloader;
import com.test.found.R;

import org.apache.commons.lang.StringUtils;

import java.util.List;

public class ActivityAdapter extends BaseAdapter {

	private Context mContext;
	private List<NewsMessage> msgList;
	
	public ActivityAdapter(Context context, List<NewsMessage> msgList) {
		super();
		mContext = context;
		this.msgList = msgList;
	}

	@Override
	public int getCount() {
		return msgList.size();
	}

	@Override
	public NewsMessage getItem(int position) {
		return msgList.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.activity_item, null);
		}
		ImageView icon = (ImageView) convertView
				.findViewById(R.id.activity_item_icon);
		TextView title = (TextView) convertView
				.findViewById(R.id.activity_item_title);
		TextView content = (TextView) convertView
				.findViewById(R.id.activity_item_content);
		final NewsMessage item = getItem(position);

		String[] imagelink = item.getImagelink();
		if (imagelink != null && imagelink.length != 0)
			ImageDownloader.getInstance().download(Apis.SERVER + imagelink[0], icon, 50, 50,
				mContext.getResources(), R.drawable.ic_launcher);
		else
			icon.setImageResource(R.drawable.ic_launcher);
		if (StringUtils.isNotBlank(item.getTitle()))
			title.setText(item.getTitle());
		else
			title.setText("");
		if (StringUtils.isNotBlank(item.getContent()))
			content.setText(item.getContent());
		else
			content.setText("");

		return convertView;
	}
	
	public void updateMessageList(List<NewsMessage> l) {
		this.msgList = l;
		this.notifyDataSetChanged();
	}
	
	public void insertNewItem(NewsMessage item) {
		this.msgList.add(0, item);
		this.notifyDataSetChanged();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

}