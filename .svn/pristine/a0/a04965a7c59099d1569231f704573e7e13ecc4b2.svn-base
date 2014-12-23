package com.chc.found.presenters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.chc.dochoo.CHCApplication;
import com.chc.found.FoundSettings;
import com.chc.found.models.AddEntityState;
import com.chc.found.models.DatabaseHelper;
import com.chc.found.models.DoctorUser;
import com.chc.found.models.EntityModel;
import com.chc.found.models.EntityUser;
import com.chc.found.models.MedicalCenterUser;
import com.chc.found.network.NetworkRequestsUtil;
import com.chc.found.utils.SharedPreferenceUtil;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractAccountPresenter implements IEntityPresenter {
	static final int MIN_PSWD_LENGTH = 6;

	protected static final String TAG = AbstractAccountPresenter.class.getSimpleName();
	
	private EntityModel entityModel;
	
	final void writeAccountInfo(CHCApplication application, String userId, String username, String fullname, String data) {
		
		// record account sign in to shared preference
		SharedPreferenceUtil.writeUserId(application, userId);
		SharedPreferenceUtil.writeUserName(application, username);
		SharedPreferenceUtil.writeFullName(application, fullname);
		SharedPreferenceUtil.writeUserData(application, data);
		application.setUserId(userId);
		application.setUserName(username);
		application.setUserFullname(fullname);
		application.setUser(data);
	}
	
	public static final void loadUserProfile(final CHCApplication application, final String id) {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				
				try {
					String response = NetworkRequestsUtil.getEntityByIdOrPin(id);
					if (StringUtils.isNotBlank(response)) {
						SharedPreferenceUtil.writeUserData(application, response);
						application.setUser(response);
					}
                    if (StringUtils.isBlank(response)) {
                        return null;
                    }
                    EntityUser entityUser = EntityUser.parseJson(response);
                    Bitmap bitmap = download_Image(entityUser.getProfileIconUrl());
                    if (bitmap != null) {
                        SharedPreferenceUtil.writeUserImage(bitmap, application);
                    }
                } catch (Exception e) {
					Log.e(TAG, e.getMessage(), e);
				}
				return null;
			}
		}.execute();
	}

    private static Bitmap download_Image(String url) {

        Bitmap bmp = null;
        try{
            URL ulrn = new URL(url);
            HttpURLConnection con = (HttpURLConnection)ulrn.openConnection();
            InputStream is = con.getInputStream();
            bmp = BitmapFactory.decodeStream(is);
            if (null != bmp)
                return bmp;

        }catch(Exception e){}
        return bmp;
    }
	
	final void signOut(CHCApplication application) {
		
		// clear all data
		writeAccountInfo(application, "", "", "", "");
        SharedPreferenceUtil.wipe(application);
		
		FoundSettings foundSettings = FoundSettings.getInstance(application);
//		foundSettings.setFirstLaunch(application, true);
		foundSettings.setUserRole(application, true);

        application.onSignedOut();

        application.getHelper().clearAllTables();
		
		deleteEverythingCache();
	}

    public static void deleteEverythingCache() {
		File file = new File(FoundSettings.DOWNLOAD_PATH);
		recursiveDelete(file);
	}

    public static void recursiveDelete(File file) {
        if (file == null || !file.exists()) return;
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    recursiveDelete(f);
                }
            }
        } else if (file.isFile()) {
            file.delete();
        } else {
            // ignore
        }
    }

    @Override
	public void onEntityUserLoaded(EntityUser user) {
	}

	@Override
	public void onLoadNewEntityFailed(AddEntityState state) {
	}

	@Override
	public void onEntityUserListLoaded(List<EntityUser> userlist) {
	}

	@Override
	public EntityUser getEntityById(String id) {
		return null;
	}

    @Override
    public void addContact(boolean isPatient, String idOrPin, String userId, String pushId) {
        // ignored
    }

    @Override
    public void onContactDeleted(EntityUser user) {
        // ignored
    }

    @Override
    public void onContactDeleteFailed() {
        // ignored
    }

    @Override
    public void deleteContact(String targetId, String userId, String pushId) {
        // ignored
    }

    @Override
    public void blockContact(String targetId, String userId, String pushId) {
        //ignored
    }

    /**
     * Must be called upon successful log in or register attempt to record the login time
     * @param context
     * @param lastLogInTime
     */
    final void preSignInOrRegisterSucceeded(Context context, Long lastLogInTime) {
        FoundSettings.getInstance(context).setLastLoginTime(context, lastLogInTime);
    }
}
