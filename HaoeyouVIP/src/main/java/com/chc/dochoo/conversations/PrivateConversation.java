package com.chc.dochoo.conversations;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by HenryW on 1/10/14.
 */
@DatabaseTable(tableName = "private_conversation")
public class PrivateConversation extends Conversation {
    public static final String COLUMN_TARGET_ID = "targetId";


    @DatabaseField(columnName = COLUMN_TARGET_ID)
    private String targetId;

    public PrivateConversation() {
        // blank required
    }

    public PrivateConversation(String userId, String targetId, String topic) {
        super(userId);
        this.targetId = targetId;
        this.setTopic(topic);
    }

    public PrivateConversation(String userId, String targetId) {
        this(userId, targetId, "");
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }
}
