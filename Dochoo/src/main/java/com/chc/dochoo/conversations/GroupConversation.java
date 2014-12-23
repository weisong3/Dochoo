package com.chc.dochoo.conversations;

import com.chcgp.hpad.util.general.CHCGeneralUtil;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lance on 5/30/14.
 */
@DatabaseTable(tableName = "group_conversation")
public class GroupConversation extends Conversation{
    public static final String COLUMN_TARGET_GROUPCHAT_ID = "targetGroupchatId";
    public static final String COLUMN_STILL_IN_GROUP = "stillInGroup";

    @DatabaseField(columnName = COLUMN_TARGET_GROUPCHAT_ID)
    private String targetGroupchatId;

    @DatabaseField(columnName = "leader")
    private String leader;

    @DatabaseField(columnName = COLUMN_STILL_IN_GROUP)
    private Boolean stillInGroup;

    public String getTargetGroupchatId() {
        return targetGroupchatId;
    }

    public String getLeader() {
        return leader;
    }

    public Boolean getStillInGroup() {
        return stillInGroup;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public void setStillInGroup(Boolean stillInGroup) {
        this.stillInGroup = stillInGroup;
    }

    public GroupConversation() {
    }

    public GroupConversation(String leaderId, String groupId, String topic){
        super("group");
        this.leader = leaderId;
        this.targetGroupchatId = groupId;
        this.setTopic(topic);
        stillInGroup = true;
    }
}
