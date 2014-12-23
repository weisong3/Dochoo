package com.chc.found.utils;

import java.io.ByteArrayOutputStream;

import org.apache.commons.lang.StringUtils;

import com.chc.dochoo.CHCApplication;
import com.chc.found.FoundConstants;
import com.chc.found.FoundSettings;
import com.chc.found.config.Apis;
import com.chcgp.hpad.util.general.CHCGeneralUtil;
import com.test.found.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

public class SharedPreferenceUtil {
	private static final String TAG = "SharedPreferenceUtil";
	private static final String FILE_NAME_PUSH_ID = "push_id";
    private static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String PROPERTY_LAST_LOGIN = "lastLogIn";
    private static final String PROPERTY_REAUTHENTICATE_INTERVAL = "reauthenticateInterval";
    private static final String PROPERTY_DELETE_MSG_INTERVAL = "deleteMsgInterval";
    
	private static final String FILE_NAME_SIGNIN = "sign_in";
	private static final String PROPERTY_USER_ID = "userid";
	private static final String PROPERTY_USER_NAME = "username";
	private static final String PROPERTY_USER_FULL_NAME = "fullname";
	private static final String PROPERTY_USER_DATA = "json";
	private static final String PROPERTY_USER_IMAGE = "userImage";
	
	private static final String FILE_NAME_CURRENT_ENTITY = "current_entity";
	private static final String PROPERTY_ENTITY_ID = "entityid";
	
	private static final String FILE_NAME_GENERAL_PROP = "general";

    private static final String REFRESH_CONTACTS_IDLE = "refreshIdleNeeded";

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	public static int getAppVersion(Context context) {
	    try {
	        PackageInfo packageInfo = context.getPackageManager()
	                .getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	        // should never happen
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}

	/**
	 * Retrieves the stored userid, or empty string if no id is stored
	 * @param context
	 * @return userid or empty string if not found
	 */
	public static String getCachedUserId(Context context) {
		return getSignInPreferences(context).getString(PROPERTY_USER_ID, "");
	}

	/**
	 * Retrieves stored username
	 * @param context
	 * @return username or empty string if not found
	 */
	public static String getCachedUserName(Context context) {
		return getSignInPreferences(context).getString(PROPERTY_USER_NAME, "");
	}

	private static SharedPreferences getCurrentEntityPreferences(Context context) {
	    return context.getSharedPreferences(FILE_NAME_CURRENT_ENTITY,
	            Context.MODE_PRIVATE);
	}
	
	public static String getFullName(Context context) {
		return getGeneralPreferences(context).getString(PROPERTY_USER_FULL_NAME, "");
	}
	
	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private static SharedPreferences getGCMPreferences(Context context) {
	    // This sample app persists the registration ID in shared preferences, but
	    // how you store the regID in your app is up to you.
	    return context.getSharedPreferences(FILE_NAME_PUSH_ID,
	            Context.MODE_PRIVATE);
	}
	
	private static SharedPreferences getGeneralPreferences(Context context) {
		return context.getSharedPreferences(FILE_NAME_GENERAL_PROP, Context.MODE_PRIVATE);
	}

	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 *
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	public static String getRegistrationId(Context context) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	    if (StringUtils.isBlank(registrationId)) {
	    	if (Apis.DEBUG) Log.i(TAG, "Registration not found.");
	        return "";
	    }
	    // Check if app was updated; if so, it must clear the registration ID
	    // since the existing regID is not guaranteed to work with the new
	    // app version.
	    int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	    int currentVersion = getAppVersion(context);
	    if (registeredVersion != currentVersion) {
	    	if (Apis.DEBUG) Log.i(TAG, "App version changed.");
	        return "";
	    }
	    return registrationId;
	}

	private static SharedPreferences getSignInPreferences(Context context) {
	    return context.getSharedPreferences(FILE_NAME_SIGNIN,
	            Context.MODE_PRIVATE);
	}
	
	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}.
	 *
	 * @param context application's context.
	 * @param regId registration ID
	 */
	public static void storeRegistrationId(Context context, String regId) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    int appVersion = getAppVersion(context);
	    if (Apis.DEBUG) Log.i(TAG, "Saving regId on app version " + appVersion);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(PROPERTY_REG_ID, regId);
	    editor.putInt(PROPERTY_APP_VERSION, appVersion);
	    editor.commit();
	}
	
	/**
	 * Stores the current entity's id
	 * @param context
	 * @param id the id string from current {@code EntityUser} 
	 */
	public static void writeEntityId(Context context, String id) {
		final SharedPreferences prefs = getCurrentEntityPreferences(context);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(PROPERTY_ENTITY_ID, id);
	    editor.commit();
	}
	
	public static void writeFullName(Context context, String fullname) {
		final SharedPreferences prefs = getGeneralPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_USER_FULL_NAME, fullname);
		editor.commit();
	}
	
	/**
	 * Stores the userid associated with the current user
	 * @param context
	 * @param userId
	 */
	public static void writeUserId(Context context, String userId) {
		final SharedPreferences prefs = getSignInPreferences(context);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(PROPERTY_USER_ID, userId);
	    editor.commit();
	}
	
	
	/**
	 * Stores username of current user
	 * @param context
	 * @param userName
	 */
	public static void writeUserName(Context context, String userName) {
		final SharedPreferences prefs = getSignInPreferences(context);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(PROPERTY_USER_NAME, userName);
	    editor.commit();
	}
	
	public static void writeUserData(Context context, String content) {
		final SharedPreferences prefs = getSignInPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_USER_DATA, content);
		editor.commit();
	}
	
	public static String getUserData(Context context) {
		final SharedPreferences prefs = getSignInPreferences(context);
		return prefs.getString(PROPERTY_USER_DATA, "");
	}
	
	public static void writeUserImage(Bitmap bitmap, Context context) {

        if (bitmap.getWidth() > FoundSettings.MAX_PIXEL_ICON
                || bitmap.getHeight() > FoundSettings.MAX_PIXEL_ICON) {
            bitmap = CHCGeneralUtil.scaleBitmapPerMaxSize(bitmap, FoundSettings.MAX_PIXEL_ICON);
        }

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos);
		byte[] b = baos.toByteArray();
		
		final SharedPreferences prefs = getSignInPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_USER_IMAGE, Base64.encodeToString(b, Base64.DEFAULT));
		editor.commit();
	}
	
	public static Bitmap readUserImage(Context context) {
		final SharedPreferences prefs = getSignInPreferences(context);
		String previouslyEncodedImage  = prefs.getString(PROPERTY_USER_IMAGE, "");
		if( !previouslyEncodedImage.equalsIgnoreCase("") ){
		    byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
		    Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            if (bitmap.getWidth() > FoundSettings.MAX_PIXEL_ICON
                || bitmap.getHeight() > FoundSettings.MAX_PIXEL_ICON) {
                // too large
                bitmap = CHCGeneralUtil.scaleBitmapPerMaxSize(bitmap, FoundSettings.MAX_PIXEL_ICON);
                writeUserImage(bitmap, context);
            }
		    return bitmap;
		}
		return null;
	}

    public static void wipe(Context context) {
        SharedPreferences.Editor editor = getSignInPreferences(context).edit();
        editor.clear();
        editor.apply();

        editor = getGCMPreferences(context).edit();
        editor.clear();
        editor.apply();

        editor = getGeneralPreferences(context).edit();
        editor.clear();
        editor.apply();

    }

    public static Long getLastLogInTime(Context context) {
        SharedPreferences sharedPreferences = getGeneralPreferences(context);
        if (sharedPreferences.contains(PROPERTY_LAST_LOGIN)) {
            return sharedPreferences.getLong(PROPERTY_LAST_LOGIN, 0);
        } else {
            return null;
        }
    }

    public static void setLastLogInTime(Context context, Long time) {
        if (time == null) return;
        SharedPreferences sharedPreferences = getGeneralPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(PROPERTY_LAST_LOGIN, time);
        editor.apply();
    }

    public static Long getReauthenticateInterval(Context context) {
        SharedPreferences sharedPreferences = getGeneralPreferences(context);
        return sharedPreferences.getLong(PROPERTY_REAUTHENTICATE_INTERVAL,
                Long.parseLong(context.getString(R.string.default_reauthentication_interval)));
    }

    public static void setReauthenticateInterval(Context context, Long time) {
        if (time == null) return;
        SharedPreferences sharedPreferences = getGeneralPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(PROPERTY_REAUTHENTICATE_INTERVAL, time);
        editor.apply();
    }

    public static Long getMsgDeleteInterval(Context context) {
        SharedPreferences sharedPreferences = getGeneralPreferences(context);
        return sharedPreferences.getLong(PROPERTY_DELETE_MSG_INTERVAL,
                Long.parseLong(context.getString(R.string.default_msg_delete_interval)));
    }

    public static void setMessageDeleteInterval(Context context, Long time) {
        if (time == null) return;
        SharedPreferences sharedPreferences = getGeneralPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(PROPERTY_DELETE_MSG_INTERVAL, time);
        editor.apply();
    }

    public static void setRefreshIdle(Context context, boolean needed){
        SharedPreferences sharedPreferences = getGeneralPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(needed){
            editor.putBoolean(REFRESH_CONTACTS_IDLE,needed);
        }
        else{
            editor.remove(REFRESH_CONTACTS_IDLE);
        }
        editor.apply();
    }

    public static Boolean getRefreshIdle(Context context){
        SharedPreferences sharedPreferences = getGeneralPreferences(context);
        if (sharedPreferences.contains(REFRESH_CONTACTS_IDLE)) {
            return sharedPreferences.getBoolean(REFRESH_CONTACTS_IDLE,false);
        } else {
            return false;
        }
    }
}
