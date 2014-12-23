package com.chc.found;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.Environment;

import com.chc.found.utils.SharedPreferenceUtil;

/**
 * This is the singleton class managing all the settings
 *
 */
public class FoundSettings {

    public static final String TYPEFACE_OPEN_SANS = "opensans.ttf";
    public static final String BUNDLE_KEY_NAVI_ITEM = "bundle_args_naviitem";
    public static final int MAX_PIXEL_ICON = 1024;
    public static final String TEXT_MALE = "male";
    public static final String TEXT_FEMALE = "female";
    public static final int BUFFERSIZE = 1024 * 10;
    private static volatile FoundSettings instance;

	private static final String PREF_MASTER_KEY_SETTINGS = "settings";
	private static final String PREF_KEY_NOTIFICATION = "show notification";
	private static final String PREF_KEY_IS_PATIENT = "ispatient";
	
	private boolean showNotification = true;
	private boolean isPatient = false;
	
	public static final String DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/data/chc/";

	public static final int MAX_PIXEL = 2048;

    private Long lastLoginTime, reauthenticateInterval, msgDeleteInterval;
	
	/**
	 * Private constructor used for initializing preferences
	 * After initialization, the context is discarded and only preference data are kept
	 * @param context context is needed for {@code SharedPreferences} access
	 */
	private FoundSettings(Context context) {
		init(context);
	}
	
	public static FoundSettings getInstance(Context context) {
		if (instance == null) {
			synchronized (FoundSettings.class) {
				if (instance == null) {
					instance = new FoundSettings(context);
				}
			}
		}
		return instance;
	}
	
	private void init(Context context) {
		SharedPreferences sp = context.getSharedPreferences(PREF_MASTER_KEY_SETTINGS, Context.MODE_PRIVATE);
		
		showNotification = sp.getBoolean(PREF_KEY_NOTIFICATION, true);
		isPatient = sp.getBoolean(PREF_KEY_IS_PATIENT, false);

	}
	
	private void writeSettings(Context context) {
		SharedPreferences sp = context.getSharedPreferences(PREF_MASTER_KEY_SETTINGS, Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		
		edit.putBoolean(PREF_KEY_NOTIFICATION, showNotification);
		edit.putBoolean(PREF_KEY_IS_PATIENT, isPatient);
		// put all settings into editor every time. This is 
		// to ensure android platform pertains the changes
		
		edit.clear();
		
		edit.commit();
	}
	
	public boolean isShowNotification() {
		return showNotification;
	}
	
	public void setShowNotification(Context context, boolean showNotification) {
		this.showNotification = showNotification;
		
		writeSettings(context);
	}

	public boolean isPatient() {
		return this.isPatient;
	}
	
	public boolean isProfessional() {
		return !this.isPatient();
	}
	
	public void setUserRole(Context context, boolean isPatient) {
		this.isPatient = isPatient;
		
		writeSettings(context);
	}

    /**
     *
     * @param context
     * @return null if not recorded
     */
    public Long getLastLoginTime(Context context) {
        if (lastLoginTime == null)
            lastLoginTime = SharedPreferenceUtil.getLastLogInTime(context);

        return lastLoginTime;
    }

    public void setLastLoginTime(Context context, Long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
        SharedPreferenceUtil.setLastLogInTime(context, lastLoginTime);
    }

    public Long getReauthenticateInterval(Context context) {
        if (reauthenticateInterval == null)
            reauthenticateInterval = SharedPreferenceUtil.getReauthenticateInterval(context);

        return reauthenticateInterval;
    }

    public void setReauthenticateInterval(Context context, Long reauthenticateInterval) {
        this.reauthenticateInterval = reauthenticateInterval;
        SharedPreferenceUtil.setReauthenticateInterval(context, reauthenticateInterval);
    }

    public Long getMsgDeleteInterval(Context context) {
        if (msgDeleteInterval == null)
            msgDeleteInterval = SharedPreferenceUtil.getMsgDeleteInterval(context);
        return msgDeleteInterval;
    }

    public void setMsgDeleteInterval(Context context, Long msgDeleteInterval) {
        this.msgDeleteInterval = msgDeleteInterval;
        SharedPreferenceUtil.setMessageDeleteInterval(context, msgDeleteInterval);
    }

    public static boolean isReauthenticationNeeded(Context context) {
        long currentTime = System.currentTimeMillis();
        FoundSettings foundSettings = FoundSettings.getInstance(context);
        Long interval = foundSettings.getReauthenticateInterval(context);
        Long lastLoginTime = foundSettings.getLastLoginTime(context);
        return lastLoginTime == null || currentTime > lastLoginTime + interval;
    }
}
