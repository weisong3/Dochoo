package com.chc.found.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chc.dochoo.CHCApplication;
import com.chc.found.config.Apis;
import com.chc.dochoo.conversations.FoundMessage;
import com.chc.dochoo.conversations.NewsMessage;
import com.chc.found.presenters.NewsDetailPresenter;
import com.chc.found.views.IMessageView;
import com.chcgp.hpad.util.download.ImageDownloader;
import com.chcgp.hpad.util.general.DateTimeUtil;
import com.test.found.R;

import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class NewsDetailFragment extends Fragment implements IMessageView{

	private NewsDetailPresenter presenter;
	private static final String KEY_ID = "id";
	
	private ImageView photo;
	private TextView title;
	private TextView content;
	private TextView time;
	
	public static NewsDetailFragment newInstance(String id) {
		  NewsDetailFragment newsDetailFragment = new NewsDetailFragment();
		  Bundle arg = new Bundle();
		  arg.putString(KEY_ID, id);
		  newsDetailFragment.setArguments(arg);
		return newsDetailFragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_news_detail, container, false);
		photo = (ImageView) root.findViewById(R.id.news_detail_image);
		title = (TextView) root.findViewById(R.id.news_detail_title);
		content = (TextView) root.findViewById(R.id.news_detail_content);
		time = (TextView) root.findViewById(R.id.news_detail_time);
		
		presenter = new NewsDetailPresenter(this);
		return root;
	}

	@Override
	public void onResume() {
		super.onResume();
		NewsMessage news = presenter.getNewsById(getArguments().getString(KEY_ID));
		if (news != null) {
			if (StringUtils.isNotBlank(news.getTitle()))
				title.setText(news.getTitle());
			String[] imagelink = news.getImagelink();
			if (imagelink != null && imagelink.length != 0) {
				ImageDownloader.getInstance().download(Apis.SERVER + imagelink[0], photo, getResources(), R.drawable.ic_launcher);
			} else {
				photo.setVisibility(View.GONE);
			}
			
			if (news.getContent() != null && !news.getContent().equals("null")) {
				content.setText(news.getContent());
			} else {
				content.setVisibility(View.GONE);
			}
			
			Date date = new Date(news.getTime());
			time.setText(DateTimeUtil.format2(date, TimeZone.getTimeZone("GMT+8")));
		}
	}

	@Override
	public CHCApplication getCHCApplication() {
        FragmentActivity activity = getActivity();
        if (activity == null) return null;
        return CHCApplication.getInstance(activity);
	}

	@Override
	public void onMessageLoaded(List<? extends FoundMessage> msgList) {
		// TODO Auto-generated method stub
		
	}
	
	

}
