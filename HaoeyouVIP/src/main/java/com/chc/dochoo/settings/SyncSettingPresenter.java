package com.chc.dochoo.settings;

import android.os.AsyncTask;
import android.util.Log;

import com.chc.found.network.NetworkRequestsUtil;

import java.io.IOException;

/**
 * Created by HenryW on 2/4/14.
 */
public class SyncSettingPresenter {
    private static final String TAG = SyncSettingPresenter.class.getSimpleName();

    public void syncMessageLifeSetting(final String userId, final String messageLifeString) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    NetworkRequestsUtil.putMessageLifeSetting(userId, messageLifeString);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                    // ignored
                }
                return null;
            }
        }.execute();
    }
}
