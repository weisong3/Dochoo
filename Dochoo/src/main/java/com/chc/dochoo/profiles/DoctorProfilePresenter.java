package com.chc.dochoo.profiles;

import android.os.AsyncTask;
import android.util.Log;

import com.chc.found.models.DoctorUser;
import com.chc.found.models.EntityUser;
import com.chc.found.network.NetworkRequestsUtil;
import com.chc.found.views.IProfileView;

/**
 * Created by HenryW on 1/3/14.
 */
public class DoctorProfilePresenter extends AbstractProfilePresenter {
    protected static final String TAG = DoctorProfilePresenter.class.getSimpleName();

    public DoctorProfilePresenter(IProfileView view) {
        super(view);
    }

}

