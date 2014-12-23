package com.chc.dochoo.contacts;

import com.chc.found.models.EntityUser;

/**
 * Created by HenryW on 1/13/14.
 */

public interface OnContactOptionListener {
    void onContactClicked(String id);
    void onContactImageClicked(EntityUser user);
    void onContactLongClicked(String id);
}

