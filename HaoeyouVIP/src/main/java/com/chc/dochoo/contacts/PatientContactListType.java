package com.chc.dochoo.contacts;

import com.test.found.R;

/**
 * Created by HenryW on 1/13/14.
 */
public enum PatientContactListType {
    PROFESSIONALS(R.string.contact_pro);

    private PatientContactListType(int resId) {
        this.resId = resId;
    }

    public int getResId() {
        return resId;
    }
    private final int resId;
}
