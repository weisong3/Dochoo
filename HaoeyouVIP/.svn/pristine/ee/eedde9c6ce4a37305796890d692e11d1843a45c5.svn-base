package com.chc.dochoo.navidrawer;

import android.util.SparseArray;

import com.chc.dochoo.contacts.ContactActivity;
import com.chc.dochoo.conversations.ConversationActivity;
import com.chc.dochoo.more.AboutActivity;
import com.chc.dochoo.more.AdvancedFeaturesActivity;
import com.chc.dochoo.more.InvitationActivity;
import com.chc.dochoo.profiles.ProfileActivity;
import com.chc.dochoo.settings.SettingsActivity;
import com.chc.found.DochooCardActivity;
import com.chc.found.UserManualActivity;
import com.test.found.R;

/**
 * Created by HenryW on 12/27/13.
 */
public enum NaviItems {
    PROFILE(R.id.navi_profile, ProfileActivity.class),
    CONVERSATION_ALL(R.id.navi_text_conversation_all, ConversationActivity.class),
    CONVERSATION_SINGLE(R.id.navi_text_conversation_single, ConversationActivity.class),
    CONVERSATION_GROUP(R.id.navi_text_conversation_group, ConversationActivity.class),
    CONVERSATION_STARRED(R.id.navi_text_conversation_starred, ConversationActivity.class),
    CONTACT_PROFESSIONALS(R.id.navi_text_contact_collab, ContactActivity.class),
    CONTACT_COLLEAGUES(R.id.navi_text_contact_colleague, ContactActivity.class),
    CONTACT_PATIENTS(R.id.navi_text_contact_patients, ContactActivity.class),
    MORE_ABOUT(R.id.navi_text_more_about, AboutActivity.class),
    MORE_SETTINGS(R.id.navi_text_more_setting, SettingsActivity.class),
    /*MORE_FEATURES(R.id.navi_text_more_advanced, AdvancedFeaturesActivity.class),*/
    /*INVITAION(R.id.navi_text_invitation, InvitationActivity.class),*/     //hide for Chinese version
    MY_QR_CODE(R.id.navi_text_my_qr_code, DochooCardActivity.class),
    USER_MANUAL(R.id.navi_text_user_manual, UserManualActivity.class);

    private static final SparseArray<NaviItems> map = new SparseArray<NaviItems>(values().length);
    static {
        for (NaviItems item : values()) {
            map.put(item.getResId(), item);
        }
    }

    private final int resId;
    private final Class<?> activityClass;

    NaviItems(int resId, Class<?> activityClass) {
        this.resId = resId;
        this.activityClass = activityClass;
    }

    public int getResId() {
        return resId;
    }

    public Class<?> getActivityClass() {
        return activityClass;
    }

    public static NaviItems getItemById(int resId) {
        return map.get(resId);
    }
}
