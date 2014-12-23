package com.chc.found.presenters;

import static com.chc.found.config.Apis.SENDER_ID;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.chc.dochoo.CHCApplication;
import com.chc.found.config.Apis;
import com.chc.found.utils.SharedPreferenceUtil;
import com.igexin.sdk.PushManager;
import com.test.found.R;

import org.apache.commons.lang.StringUtils;

import java.util.List;

public class PushServicePresenter {

    private static final String TAG = PushServicePresenter.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 10086;
    private Activity mActivity;
    private CHCApplication mApplication;
    private IPushServiceView mView;
    private PushManager geTuiManager;
    private String regid;

    public static interface IPushServiceView {
        void onRegisterPushIdFailed();

        Activity getContextActivity();

        void onRegisterPushIdSucceeded();
    }

    public PushServicePresenter(IPushServiceView view) {
        super();
        this.mView = view;
        this.mActivity = view.getContextActivity();
        this.mApplication = CHCApplication.getInstance(this.mActivity);
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and app versionCode in the application's shared
     * preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    if (geTuiManager == null) {
                        geTuiManager = PushManager.getInstance();
                    }
                    geTuiManager.initialize(mApplication);
                    regid = geTuiManager.getClientid(mActivity);
                    String msg = "Device registered, registration ID=" + regid;
                    if (Apis.DEBUG) Log.i(TAG, msg);

                    return sendRegistrationIdToBackend();
                } catch (IOException ex) {
                    Log.e(TAG, ex.getMessage(), ex);
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                // AlertDialogFragment2.showImmediately(getSupportFragmentManager(),
                // TAG, null, msg, 1, "Close", null, null, false);
                // Persist the regID - no need to register again.
                if (success && StringUtils.isNotBlank(regid)) {
                    registerIdSuccess();
                } else
                    registerIdFailed();
            }
        }.execute(null, null, null);

    }

    protected void registerIdSuccess() {
        SharedPreferenceUtil.storeRegistrationId(mActivity, regid);
        mApplication.setRegId(regid);
        mView.onRegisterPushIdSucceeded();
    }

    void registerIdFailed() {
        SharedPreferenceUtil.storeRegistrationId(mActivity, "");
        mApplication.setRegId("");
        mView.onRegisterPushIdFailed();
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * or CCS to send messages to your app. Not needed for this demo since the
     * device sends upstream messages to a server that echoes back the message
     * using the 'from' address in the message.
     * <p/>
     * Performs network request. Must be in worker thread to call
     *
     * @throws IOException
     * @throws ClientProtocolException
     */
    public boolean sendRegistrationIdToBackend() throws ClientProtocolException,
            IOException {

//    private int totalTime = 0;
//    public void checkGetuiServices()
//    {
//        attachReceiver();
//        geTuiManager = PushManager.getInstance();
//        Toast.makeText(mApplication, mApplication.getString(R.string.initializing_system), Toast.LENGTH_SHORT).show();
//        geTuiManager.initialize(mApplication);
//    }
//    return true;
//  	return NetworkRequestsUtil.postRegistrationID(mApplication.getAdminId(), regid);

        // no longer needed to send server the push id this way
        // when signing in or registering or subscribing the pushid will
        // then be recorded by server
        return true;
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If it
     * doesn't, display a dialog that allows users to download the APK from the
     * Google Play Store or enable it in the device's system settings.
     * If it does, we do our initialization afterwards
     */
    public void checkGetuiServices() {
//        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mActivity);
//        if (resultCode != ConnectionResult.SUCCESS) {
//            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
//                GooglePlayServicesUtil.getErrorDialog(resultCode, mActivity,
//                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
//            } else {
//                if (Apis.DEBUG) Log.i(TAG, "This device is not supported.");
//                mActivity.finish();
//            }
////            mView.onRegisterPushIdFailed();
//            return;
//        }

        // If this check succeeds, proceed with normal processing.
        // Otherwise, prompt user to get valid Play Services APK.
        geTuiManager = PushManager.getInstance();
        regid = this.mApplication.getRegId();

        if (StringUtils.isBlank(regid)) {
            Toast.makeText(mApplication, mApplication.getString(R.string.initializing_system), Toast.LENGTH_SHORT).show();
            registerInBackground();
        } else {
            // has push id, good to go
            registerIdSuccess();
        }
    }

}
