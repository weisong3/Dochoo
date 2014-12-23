package com.chc.dochoo.conversations;

import com.chcgp.hpad.util.general.CHCGeneralUtil;
import com.j256.ormlite.field.DatabaseField;

/**
 * Created by HenryW on 1/2/14.
 */
public abstract class Conversation {

    public static final String COLUMN_NAME_ID = "id";

    @DatabaseField(id = true, columnName = COLUMN_NAME_ID)
    private String id;
    @DatabaseField
    private String userId;
    @DatabaseField
    private String lastMsgId;
    @DatabaseField
    private String topic;
    @DatabaseField
    private int numOfUnread;

    private InstantMessage lastMsg;

    protected Conversation() {
    }

    Conversation(String userId) {
        this.id = CHCGeneralUtil.randomUuidNoDash();
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastMsgId() {
        return lastMsgId;
    }

    public void setLastMsgId(String lastMsgId) {
        this.lastMsgId = lastMsgId;
    }

    public InstantMessage getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(InstantMessage lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getNumOfUnread() {
        return numOfUnread;
    }

    public void setNumOfUnread(int numOfUnread) {
        this.numOfUnread = numOfUnread;
    }
}

