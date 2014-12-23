package com.chc.found.presenters;

import android.os.AsyncTask;
import android.util.Log;

import com.chc.dochoo.userlogin.Role;
import com.chc.exceptions.InternalErrorException;
import com.chc.dochoo.CHCApplication;
import com.chc.found.FoundSettings;
import com.chc.found.config.Apis;
import com.chc.found.models.EntityUser;
import com.chc.found.models.UniversalUserView;
import com.chc.found.network.NetworkRequestsUtil;
import com.chc.found.views.ILogInView;
import com.chcgp.hpad.util.network.RestClientUtils;
import com.google.gson.Gson;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginPresenter extends AbstractAccountPresenter {
	private static final String TAG = "AccountPresenter";

	private ILogInView mView;

	public LoginPresenter(ILogInView mView) {
		super();
		this.mView = mView;
	}

	/**
	 *
	 * @param username
	 * @param password
	 * @param pushId
	 */
	public void signInUser(String username, String password, String pushId) {

		// from user input
		String encodedPassword = RestClientUtils.encodePassword(password);

		new SignInUserTask().execute(username, encodedPassword, pushId);

	}


	private class SignInUserTask extends AsyncTask<String, Void, String> {

		private String type;

		@Override
		protected String doInBackground(String... params) {
            String responseStr = null;
			try {
                responseStr = NetworkRequestsUtil.postSignIn(params[0], params[1], params[2]);
			} catch (InternalErrorException e) {
				switch (e.getErrorCode()) {
				default:
					Log.e(TAG, e.getMessage(), e);
				}
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}
			return responseStr;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result == null) onSignInFailed();
			else {
                EntityUser res;
                try {
                    res = EntityUser.parseJson(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                    onSignInFailed();
                    return;
                }
                onSignInSucceeded(res, result);
			}
		}

	}

	public void onSignInFailed() {

//		signOut(mView.getCHCApplication());

		mView.onUserSignInFailed();

	}

	void onSignInSucceeded(EntityUser user, String data) {

		if (user == null || mView == null) {
			if (Apis.DEBUG) Log.e(TAG, "blank user id or username");
			onSignInFailed();
			return;
		}

		CHCApplication application = mView.getCHCApplication();



        FoundSettings.getInstance(application).setUserRole(application, Role.getRole(user.getClass()) == Role.PATIENT);

        preSignInOrRegisterSucceeded(application, System.currentTimeMillis());

		writeAccountInfo(application, user.getId(), user.getUsername(), user.getFullname(),
                data);

        // load user profile updates from server
        AbstractAccountPresenter.loadUserProfile(application, user.getId());

		mView.onUserSignedIn(Role.getRole(user.getClass()));
	}

	public boolean isValidInput(String username, String password) {
		return StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password) && password.length() >= MIN_PSWD_LENGTH;
	}
	
}
