package com.chc.found.presenters;

import com.chc.found.models.DatabaseHelper;
import com.chc.dochoo.conversations.NewsMessage;
import com.chc.found.views.IMessageView;

public class NewsDetailPresenter extends NewsPresenter {

	private IMessageView mView;
	
	public NewsDetailPresenter(IMessageView iView) {
		super(iView);
		this.mView = iView;
	}

	public NewsMessage getNewsById(String id) {
		DatabaseHelper helper = mView.getCHCApplication().getHelper();
		return getModel().getCachedNewsById(helper, id);
	}
}
