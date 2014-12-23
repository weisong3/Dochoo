package com.chc.found.views;

import com.chc.dochoo.userlogin.Role;

public interface ILogInView extends IBaseView {

	void onUserSignInFailed();

	void onUserSignedIn(Role role);
	
}
