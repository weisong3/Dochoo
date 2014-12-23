package com.chc.found.presenters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.chc.dochoo.CHCApplication;
import com.chc.found.NewsActivity;
import com.chc.found.config.Apis;
import com.chc.found.models.DatabaseHelper;
import com.chc.dochoo.conversations.FoundMessage;
import com.chc.dochoo.conversations.NewsMessage;
import com.chc.dochoo.conversations.NewsModel;
import com.chc.found.views.IMessageView;


public class NewsPresenter implements IMessagePresenter {
	
	private IMessageView mView;
	private NewsModel mModel;
	private static final String TAG = "NewsPresenter";
	
	public NewsPresenter(IMessageView iView) {
		this.mView = iView;
		this.mModel = new NewsModel(this);
	}

	@Override
	public void loadData(String userId, String targetId, String pushId) {
		CHCApplication chcApplication = mView.getCHCApplication();
    DatabaseHelper helper = chcApplication.getHelper();
    
		List<NewsMessage> cachedList = mModel.getCachedNewsList(helper, targetId);
		this.mView.onMessageLoaded(cachedList);
		
	}

    @Override
    public void loadGroupData(String userId, String targetGroupId, String pushId) {
        //ignore
    }

    @Override
    public FoundMessage getGroupMessageById(String id) {
        //ignore
        return null;
    }

    @Override
	public void onMessageLoaded(List<? extends FoundMessage> msgList) {
		mView.onMessageLoaded(msgList);
	}

	public void onNewsClicked(NewsMessage msg, Context context) {
		NewsActivity.startActivity(context, msg.getId());
	}
	
	NewsModel getModel() {
		return mModel;
	}

	@Override
	public void writeData(FoundMessage msg) {
		if (msg instanceof NewsMessage) {
			List<NewsMessage> l = new ArrayList<NewsMessage>(1);
			l.add((NewsMessage) msg);
			DatabaseHelper helper = mView.getCHCApplication().getHelper();
			mModel.writeToDB(helper, l);
		}
	}

	@Override
	public NewsMessage getMessageById(String id) {
		return mModel.getCachedNewsById(mView.getCHCApplication().getHelper(), id);
	}

	@Override
	public void loadServerData(String userId, String targetId, String pushId) {
		if (null != pushId) {
			mModel.loadNewsFromServer(mView.getCHCApplication().getHelper(), targetId, pushId);
		} else {
			if (Apis.DEBUG) Log.e(TAG, "pushId is null");
		}
	}
	
}
