package com.chc.found.presenters;

import com.chc.found.views.IBaseView;

public class SignOutPresenter extends AbstractAccountPresenter {
	
	private IBaseView mView;

	public SignOutPresenter(IBaseView mView) {
		super();
		this.mView = mView;
	}
	
	public void signOut() {
		signOut(mView.getCHCApplication());
		mView.getCHCApplication().getHelper().clearAllTables();
	}

}
