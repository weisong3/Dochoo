package com.chc.found.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.chc.dochoo.CHCApplication;
import com.chc.found.adapters.ActivityAdapter;
import com.chc.found.adapters.PatientMainPagerAdapter;
import com.chc.dochoo.conversations.FoundMessage;
import com.chc.dochoo.conversations.NewsMessage;
import com.chc.found.presenters.NewsPresenter;
import com.chc.found.views.IMessageView;
import com.test.found.R;

import java.util.List;

public class ActivitiesFragment extends ListFragment implements IMessageView {
	
	private NewsPresenter presenter;
	private BroadcastReceiver receiver = new MyBroadcastReceiver();
	private volatile boolean receiverRegistered = false;
	private IntentFilter intentFilter;
    private String entityId;

    public ActivitiesFragment() {
		super();

		this.presenter = new NewsPresenter(this);
	}
	
	@Override
	public CHCApplication getCHCApplication() {
        FragmentActivity activity = getActivity();
        if (activity == null) return null;
		return CHCApplication.getInstance(activity);
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized void onMessageLoaded(List<? extends FoundMessage> msgList) {
		
		ActivityAdapter adapter = (ActivityAdapter) getListAdapter();
		if (adapter == null) {
			adapter = new ActivityAdapter(getActivity(), (List<NewsMessage>) msgList);
			this.getListView().setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					presenter.onNewsClicked((NewsMessage) arg0.getItemAtPosition(arg2), getActivity());
				}
			});
			this.setListAdapter(adapter);
		} else {
			adapter.updateMessageList((List<NewsMessage>) msgList);
		}
		synchronized (this) {
			if (getActivity() != null && !receiverRegistered) {
				getActivity().registerReceiver(receiver, intentFilter);
				receiverRegistered = true;
			}
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments == null) return; // error, no argument
        entityId = arguments.getString(PatientMainPagerAdapter.KEY_ENTITY_ID);

		intentFilter = new IntentFilter(getString(R.string.news_action));
	}

	@Override
	public void onPause() {
		super.onPause();
		if (receiverRegistered) {
			getActivity().unregisterReceiver(receiver);
			synchronized (this) {
				receiverRegistered = false;
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();

        if (entityId == null) return; // error, no entity id
        CHCApplication chcApplication = getCHCApplication();
        if (chcApplication != null) {
            presenter.loadData(chcApplication.getUserId(), entityId, chcApplication.getRegId());
            presenter.loadServerData(chcApplication.getUserId(), entityId, chcApplication.getRegId());
        }
	}
	
	private class MyBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String s = intent.getStringExtra(context.getString(R.string.broadcast_intent_extra_tag_news));
			if (s != null) {
				NewsMessage msg = presenter.getMessageById(s);
				ActivityAdapter adapter = (ActivityAdapter) getListAdapter();
				if (adapter != null) {
					adapter.insertNewItem(msg);
				}
			}
		}
		
	}

}
