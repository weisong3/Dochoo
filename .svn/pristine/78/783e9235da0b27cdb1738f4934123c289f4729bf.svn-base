package com.chc.dochoo.conversations;

/**
 * Created by Lance on 6/9/14.
 */
public class GroupStatusUpdate extends FoundMessage {
    private String operatorId;
    private String groupId;
    private GroupChatTaskType groupTaskType;
    private String targetMemberId;
    private String newTitle;
    private String noticeTimeList;

    public GroupStatusUpdate(String operatorId, String groupId, short type, String targetMemberId, String newTitle, String noticeTimeList) {
        this.operatorId = operatorId;
        this.groupId = groupId;
        this.targetMemberId = targetMemberId;
        this.newTitle = newTitle;
        this.noticeTimeList = noticeTimeList;
        if(type == CONTACT_UPDATE_ADD) groupTaskType = GroupChatTaskType.NewAdd;
        else if(type == CONTACT_UPDATE_DELETED) groupTaskType = GroupChatTaskType.NewRemove;
        else if(type == GROUP_TITLE_CHANGED) groupTaskType = GroupChatTaskType.TopicChanged;
        else{
            //...May be more type in the future
        }

    }

    public String getOperatorId() {
        return operatorId;
    }

    public String getTargetMemberId() {
        return targetMemberId;
    }

    public String getNewTitle() {
        return newTitle;
    }

    public String getGroupId() {
        return groupId;
    }

    public GroupChatTaskType getGroupTaskType() {
        return groupTaskType;
    }

    public String getNoticeTimeList() {
        return noticeTimeList;
    }
}
