package com.chc.dochoo.profiles;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.chc.found.models.EntityUser;
import com.chc.found.network.NetworkRequestsUtil;
import com.chc.found.views.IProfileView;

public abstract class AbstractProfilePresenter implements IProfilePresenter {
	
	private IProfileView mView;

	public AbstractProfilePresenter(IProfileView view) {
		this.mView = view;
	}

	protected static final String TAG = AbstractProfilePresenter.class.getSimpleName();

    /**
     * Default implementation of profile icon upload
     * @param bitmap
     * @param userid
     * @param pushid
     * @param filename
     */
	@Override
	public void uploadProfileIcon(final Bitmap bitmap, final String userid, final String pushid,
			final String filename) {
		new AsyncTask<Void, Void, Boolean>() {
			
			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					return NetworkRequestsUtil.postUploadHeadIcon(userid, pushid, filename, bitmap);
				} catch (Exception e) {
					Log.e(TAG, e.getMessage(), e);
				}
				return false;
			}
			
			@Override
			protected void onPostExecute(Boolean result) {
				if (result == null || !result) mView.onUploadProfileIconFailed();
				else mView.onUploadProfileIconFinished();
			}
			
		}.execute();
		
	}

    /**
     * Default implementation of update profile
     * @param profile
     */
    @Override
    public void updateProfile(final EntityUser profile) {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                boolean result = false;
                try {
                    result = NetworkRequestsUtil.postUpdateProfile(profile);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                    result = false;
                }
                return result;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result == null || !result) {
                    // failed
                    getmView().onUpdateProfileFailed();
                } else {
                    // success
                    getmView().onUpdateProfileFinished(profile);
                }
            }

        }.execute();
    }

	public IProfileView getmView() {
		return mView;
	}

}
