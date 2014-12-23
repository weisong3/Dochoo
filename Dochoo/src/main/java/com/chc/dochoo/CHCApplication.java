package com.chc.dochoo;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.chc.found.models.DatabaseHelper;
import com.chc.found.models.EntityUser;
import com.chc.found.network.NetworkRequestsUtil;
import com.chc.found.utils.SharedPreferenceUtil;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.test.found.R;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;

public class CHCApplication extends Application{

    private static final String TAG = CHCApplication.class.getSimpleName();
    private volatile DatabaseHelper databaseHelper = null;
    private volatile String regId = null;
    private volatile String userId = null;
    private volatile String userName = null;
    private volatile String userFullname = null;
    private volatile EntityUser user = null;
    private volatile boolean isSignedIn = false;
    private String currentChattingId = "";
    private boolean isCenter;

    private boolean offlineMode = true;
    private NetworkStatusBroadcastReceiver receiver;

    @Override
    public void onCreate() {
//		ACRA.init(this);
//		com.chc.bugreport.ChcReportSender mSender
//		  = new com.chc.bugreport.ChcReportSender(getPackageName());
//		ErrorReporter.getInstance().setReportSender(mSender);

        super.onCreate();

        this.userId = SharedPreferenceUtil.getCachedUserId(this);
        this.userName = SharedPreferenceUtil.getCachedUserName(this);
        this.userFullname = SharedPreferenceUtil.getFullName(this);
        try {
            String json = SharedPreferenceUtil.getUserData(this);
            if (StringUtils.isNotBlank(json))
                this.user = EntityUser.parseJson(json);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        if (this.user != null && user.getUsername() != null) {
            if (!this.userName.equals(user.getUsername())) {
                this.userName = user.getUsername();
                SharedPreferenceUtil.writeUserName(this, userName);
            }
        }
        this.regId = SharedPreferenceUtil.getRegistrationId(this);
        setSignedIn(StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(userName));

        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkStatusBroadcastReceiver();
        this.registerReceiver(receiver, intentFilter);
    }

    private class NetworkStatusBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            final ConnectivityManager connMgr = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            final android.net.NetworkInfo wifi = connMgr
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            final android.net.NetworkInfo mobile = connMgr
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (wifi.isAvailable() || mobile.isAvailable()) {
                offlineMode = false;
                checkAppVersion();
//                initializePushService();
            } else {
                offlineMode = true;
            }
        }
    }

    private class checkVersionTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try{
                return NetworkRequestsUtil.getCurrentAppVersion();
            }catch (Exception e){
                Log.e(TAG, "check app version exception : " +e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if(StringUtils.isNotBlank(s)){
                try{
                    int serverVersion = Integer.parseInt(s);
                    if(serverVersion > SharedPreferenceUtil.getAppVersion(getApplicationContext())){
                        Toast.makeText(getApplicationContext(), getString(R.string.found_app_name) + getString(R.string.new_version_app_available), Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    Log.e(TAG,"check app version exception : " +e.getMessage());
                }
            }
            super.onPostExecute(s);
        }
    }

    private void checkAppVersion(){
        new checkVersionTask().execute();
    }

//    private void writeVersion() {
//        SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
//        SharedPreferences.Editor e = sp.edit();
//        e.putInt(VERSION_TAG, VERSION);
//        e.commit();
//    }
//
//    private boolean isNewVersion() {
//        SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
//        int oldVersion = sp.getInt(VERSION_TAG, 0);
//
//        return oldVersion != VERSION;
//    }

//    private void initializePushService() {
//        if (pushServicePresenter == null) {
//            pushServicePresenter = new PushServicePresenter(getApplication());
//        }
//        pushServicePresenter.checkGetuiServices();
//    }

//    @Override
//    public CHCApplication getApplication() {
//        return this;
//    }

//    @Override
//    public void onRegisterPushIdFailed() {
//    //TODO
//        /*Toast.makeText(this, R.string.common_google_play_services_unsupported_text,
//                Toast.LENGTH_LONG).show();*/
//
//        Toast.makeText(this, getString(R.string.register_push_service_fail),
//                Toast.LENGTH_SHORT).show();
////        finish();
//
//    }

//    @Override
//    public void onRegisterPushIdSucceeded() {
//        this.regId = SharedPreferenceUtil.getRegistrationId(this);
//    }

    public void showNetworkStatusToast() {
        if (offlineMode) {
            Toast.makeText(this, getString(R.string.offline), Toast.LENGTH_SHORT).show();
        } else {
//            Toast.makeText(this.getApplicationContext(), "online mode", Toast.LENGTH_SHORT).show();
        }
    }

    public static CHCApplication getInstance(Activity a) {
        return (CHCApplication) a.getApplication();
    }

    public static CHCApplication getInstance(Service service) {
        return (CHCApplication) service.getApplication();
    }

    public DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            synchronized (this) {
                if (databaseHelper == null) {
                    databaseHelper =
                            OpenHelperManager.getHelper(this, DatabaseHelper.class);
                }
            }
        }
        return databaseHelper;
    }

    public synchronized void releaseDatabaseHelper() {
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    public synchronized String getRegId() {
        return regId;
    }

    public synchronized void setRegId(String regId) {
        this.regId = regId;
    }

    public synchronized String getUserId() {
        return userId;
    }

    public synchronized void setUserId(String userId) {
        this.userId = userId;
        this.setSignedIn(StringUtils.isNotEmpty(userId));
    }

    public synchronized String getUserName() {
        return userName;
    }

    public synchronized void setUserName(String userName) {
        this.userName = userName;
        this.setSignedIn(StringUtils.isNotEmpty(userName));
    }

    public synchronized boolean isSignedIn() {
        return isSignedIn;
    }

    public synchronized void setSignedIn(boolean isSignedIn) {
        this.isSignedIn = isSignedIn;
    }

    public String getUserFullname() {
        return userFullname;
    }

    public void setUserFullname(String userFullname) {
        SharedPreferenceUtil.writeFullName(this, userFullname);
        this.userFullname = userFullname;
    }

    public boolean isShowNotification(String id) {
        return !currentChattingId.equals(id);
    }

    public void setCurrentChattingId(String id) {
        this.currentChattingId = id;
    }

    public EntityUser getUser() {
        return user;
    }

    public void setUser(EntityUser user) {
        this.user = user;
        if (user != null)
            setUserFullname(user.getFullname());
    }

    public void setUser(String userdata) {
        if (StringUtils.isBlank(userdata)) return;
        try {
            setUser(EntityUser.parseJson(userdata));
        } catch (JSONException e) {
            this.user = null;
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public void onSignedOut() {
        this.user = null;
        this.userId = null;
        this.userName = null;
        this.userFullname = null;
        this.currentChattingId = "";
        this.isSignedIn = false;
    }

    public boolean isCenter() {
        return isCenter;
    }

    public void setCenter(boolean isCenter) {
        this.isCenter = isCenter;
    }
}
