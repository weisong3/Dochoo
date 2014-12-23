package com.chc.dochoo.conversations;

import android.content.Context;
import android.util.Log;

import com.chc.found.models.DatabaseHelper;
import com.chc.found.models.EntityModel;
import com.chc.found.models.EntityUser;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HenryW on 1/13/14.
 */
public class ConversationPresenter {
    private Context mContext;
    private DatabaseHelper dbhelper;

    public ConversationPresenter(Context mContext, DatabaseHelper dbhelper) {
        if (mContext == null || dbhelper == null)
            throw new IllegalArgumentException("Null arguments");
        this.mContext = mContext;
        this.dbhelper = dbhelper;
    }

    public List<PrivateConversation> getAllPrivateConversations() {
        List<PrivateConversation> list = ConversationModel.getAllPrivateConversations(this.dbhelper);
        for (PrivateConversation con : list) {
            if (con.getLastMsgId() != null) {
                con.setLastMsg(InstantMessageModel.getMessageById(this.dbhelper, con.getLastMsgId()));
            }
        }

        return list;
    }

    public List<GroupConversation> getALlGroupConversations(){
        List<GroupConversation> list = ConversationModel.getAllGroupConversation(this.dbhelper);
        for(GroupConversation con:list){
            if(con.getLastMsgId() != null){
                con.setLastMsg(InstantMessageModel.getMessageById(this.dbhelper, con.getLastMsgId()));
            }
        }
        return list;
    }

    public List<Conversation> getAllConversations() {
        List<PrivateConversation> privateList = getAllPrivateConversations();
        List<GroupConversation> groupList = getALlGroupConversations();
        List<Conversation> result = new ArrayList<Conversation>(privateList.size()+groupList.size());
        result.addAll(privateList);
        result.addAll(groupList);
        return result;
    }

    /**
     * Deletes all messages within a conversation and the conversation itself from db
     * @param con
     */
    public void deletePrivateConversation(PrivateConversation con) {
        if (con != null && con.getTargetId() != null) {
            List<InstantMessage> msgsToDelete = InstantMessageModel.getInstantMessagesByEntityId(
                    this.dbhelper, con.getTargetId());
            InstantMessageModel.deleteMultimediaContentFromMsg(msgsToDelete);
            InstantMessageModel.deleteMessages(dbhelper, msgsToDelete);
            ConversationModel.deletePrivateConversation(this.dbhelper, con);
        }
    }

    public void deleteGroupConversation(GroupConversation con){
        List<InstantMessage> msgsToDelete = InstantMessageModel.getInstantMessagesByGroupId(this.dbhelper, con.getTargetGroupchatId());
        InstantMessageModel.deleteMultimediaContentFromMsg(msgsToDelete);
        GroupMemberModel.deleteGroupMemberByGroupId(dbhelper, con.getTargetGroupchatId());
        InstantMessageModel.deleteMessages(dbhelper, msgsToDelete);
        ConversationModel.deleteGroupConversation(this.dbhelper,con);
    }
    public EntityUser getEntityUserFromPrivateConversation(PrivateConversation con) {
        return EntityModel.getEntityById(this.dbhelper, con.getTargetId());
    }

}
