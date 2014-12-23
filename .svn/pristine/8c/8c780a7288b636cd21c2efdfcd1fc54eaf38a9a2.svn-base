package com.chc.dochoo.conversations;

import android.content.Context;

import com.chc.found.models.DatabaseHelper;

import org.apache.commons.lang.StringUtils;

/**
 * Created by HenryW on 1/13/14.
 */
public class PrivateConversationPresenter {
    private Context mContext;
    private DatabaseHelper dbhelper;

    public PrivateConversationPresenter(Context mContext, DatabaseHelper dbhelper) {
        this.mContext = mContext;
        this.dbhelper = dbhelper;
    }

    public PrivateConversation getPrivateConversationById(String id) {
        if (id == null) return null;
        PrivateConversation privateConversation = ConversationModel.getPrivateConversationById(this.dbhelper, id);
        privateConversation.setLastMsg(InstantMessageModel.getMessageById(this.dbhelper,
                privateConversation.getLastMsgId()));
        return privateConversation;
    }

    public PrivateConversation createNewPrivateConversation(String userId, String entityId) {
        return ConversationModel.createPrivateConversation(this.dbhelper, userId, entityId);
    }

    public void updatePrivateConversation(PrivateConversation con) {
        if (con == null) return;
        ConversationModel.updatePrivateConversation(this.dbhelper, con);
    }

    public void onActivityPause(PrivateConversation con) {
        if (con == null) return;
        con.setNumOfUnread(0);
        if (StringUtils.isNotBlank(con.getLastMsgId())) {
            // there is message in the conversation
            updatePrivateConversation(con);
        } else {
            deletePrivateConversationIfEmpty(con.getTargetId());
        }
    }

    private void deletePrivateConversationIfEmpty(String entityId) {
        ConversationModel.deletePrivateConversationIfEmpty(this.dbhelper, entityId);
    }

    public PrivateConversation getPrivateConversationByEntityId(String entityId) {
        return ConversationModel.getPrivateConversationByEntityId(this.dbhelper, entityId);
    }
}
