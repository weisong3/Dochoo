package com.chc.found.models;

import com.chc.dochoo.userlogin.Role;
import com.chc.found.network.NetworkRequestsUtil;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.UUID;

/**
 * Created by Lance on 6/2/14.
 */
@DatabaseTable(tableName = "group_member")
public class GroupMember {
    public static final String COLUMN_NAME_ID = "id";
    public static final String COLUMN_GROUP_ID = "belongTo";
    public static final String COLUMN_USER_ID = "userId";
    public static final String COLUMN_INVITE_TIME = "inviteDate";


    @DatabaseField(id = true, columnName = COLUMN_NAME_ID)
    private String id;
    @DatabaseField(columnName = COLUMN_USER_ID)
    private String userId;
    @DatabaseField
    private String fullName;
    @DatabaseField(columnName = COLUMN_GROUP_ID)
    private String belongTo;
    @DatabaseField
    private String profileIconUrl;
    @DatabaseField(columnName = COLUMN_INVITE_TIME)
    private Long inviteDate;
    @DatabaseField
    private Role role;



    public GroupMember() {
    }

    public GroupMember(String userId, String fullname, Long inviteDate, String belongTo, Role role) {
        this.id = UUID.randomUUID().toString().replace("-","");
        this.userId = userId;
        this.fullName = fullname;
        this.belongTo = belongTo;
        this.inviteDate = inviteDate;
        this.profileIconUrl = NetworkRequestsUtil.getIconImageUrlString(userId);
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setInviteDate(Long inviteDate) {
        this.inviteDate = inviteDate;
    }

    public Long getInviteDate() {
        return inviteDate;
    }

    public String getProfileIconUrl() {
        return profileIconUrl;
    }

    public Role getRole() {
        return role;
    }
}
