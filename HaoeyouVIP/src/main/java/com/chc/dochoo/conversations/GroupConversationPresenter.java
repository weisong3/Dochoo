package com.chc.dochoo.conversations;

import android.util.Log;

import com.chc.found.models.DatabaseHelper;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Lance on 5/30/14.
 */
public class GroupConversationPresenter {
    private DatabaseHelper helper;

    public GroupConversationPresenter(DatabaseHelper helper) {
        this.helper = helper;
    }

    public GroupConversation getGroupConversationByGroupId(String groupchatId){
        if(StringUtils.isBlank(groupchatId)) return null;
        return ConversationModel.getGroupConversationByGroupId(helper, groupchatId);
    }

    public GroupConversation getGroupConversationById(String conversationId){
        if(StringUtils.isBlank(conversationId)) return null;
        GroupConversation GroupConversation = ConversationModel.getGroupConversationById(helper, conversationId);
        GroupConversation.setLastMsg(InstantMessageModel.getMessageById(helper,GroupConversation.getLastMsgId()));
        return GroupConversation;
    }

    public void onActivityPause(GroupConversation conversation){
        if(conversation == null) return;
        conversation.setNumOfUnread(0);
        ConversationModel.updateGroupConversation(helper, conversation);
    }

}
