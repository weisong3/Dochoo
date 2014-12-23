package com.chc.dochoo.conversations;

import android.provider.ContactsContract;

import com.chc.dochoo.userlogin.Role;
import com.chc.found.FoundSettings;
import com.chc.found.models.DatabaseHelper;
import com.chc.found.models.EntityUser;
import com.chc.found.models.GroupMember;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Lance on 6/5/14.
 */
public class GroupMemberModel {
    public static void CreateOrUpdateGroupMember(DatabaseHelper dbhelper, String groupId, JSONObject json) throws JSONException {
        JSONArray memberJsons = json.getJSONArray("GroupChatMembers");
        RuntimeExceptionDao<GroupMember, Long> groupMemberDao = dbhelper.getGroupMemberDao();
        //do json parse first, can not do delete current members first in case there is exception when parse the returned json data
        List<GroupMember> cachedMembers = GetMemberByGroupId(dbhelper,groupId);
        for(int i=0;i<memberJsons.length();i++){
            JSONObject js = memberJsons.getJSONObject(i);
            String curUserId = js.optString("id");
            String curFullName = js.optString("fullname");
            Long curInviteTime = js.optLong("inputdate");
            Role role = Role.getRole(js.optString("role"));
            GroupMember curMember = new GroupMember(curUserId, curFullName, curInviteTime, groupId, role);
            groupMemberDao.createOrUpdate(curMember);
            //FoundSettings.getInstance().isPatient()
        }
        if(cachedMembers != null && cachedMembers.size()>0)   groupMemberDao.delete(cachedMembers);
    }

    public static List<GroupMember> GetMemberByGroupId(DatabaseHelper dbhelper, String groupId){
        RuntimeExceptionDao<GroupMember, Long> groupMemberDao = dbhelper.getGroupMemberDao();
        QueryBuilder<GroupMember, Long> groupMemberQueryBuilder = groupMemberDao.queryBuilder();
        try{
            PreparedQuery<GroupMember> preparedQuery = groupMemberQueryBuilder
                                                                .orderBy(GroupMember.COLUMN_INVITE_TIME,true)
                                                                .where().eq(GroupMember.COLUMN_GROUP_ID, groupId).prepare();
            List<GroupMember> result = groupMemberDao.query(preparedQuery);
            return result;
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void deleteGroupMemberByGroupId(DatabaseHelper dbhelper, String groupId){
        List<GroupMember> members = GetMemberByGroupId(dbhelper, groupId);
        if(null != members && members.size()>0){
            RuntimeExceptionDao<GroupMember, Long> groupMemberDao = dbhelper.getGroupMemberDao();
            for(GroupMember member: members)    groupMemberDao.delete(member);
        }
    }

    public static void deleteGroupMemberById(DatabaseHelper dbhelper, String groupMemberId){
        GroupMember member = GetGroupMemberById(dbhelper, groupMemberId);
        if(null != member){
            RuntimeExceptionDao<GroupMember, Long> groupMemberDao = dbhelper.getGroupMemberDao();
            groupMemberDao.delete(member);
        }
    }

    public static GroupMember GetGroupMemberById(DatabaseHelper dbhelper, String groupMemberId){
        RuntimeExceptionDao<GroupMember, Long> groupMemberDao = dbhelper.getGroupMemberDao();
        QueryBuilder<GroupMember, Long> groupMemberQueryBuilder = groupMemberDao.queryBuilder();
        try{
            PreparedQuery<GroupMember> preparedQuery = groupMemberQueryBuilder
                    .where().eq(GroupMember.COLUMN_NAME_ID, groupMemberId).prepare();
            List<GroupMember> result = groupMemberDao.query(preparedQuery);
            if (result != null && !result.isEmpty())
            return result.get(0);
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static GroupMember GetGroupMemberByGroupIdAndId(DatabaseHelper dbhelper, String groupId, String userId){
        RuntimeExceptionDao<GroupMember, Long> groupMemberDao = dbhelper.getGroupMemberDao();
        QueryBuilder<GroupMember, Long> groupMemberQueryBuilder = groupMemberDao.queryBuilder();
        try{
            PreparedQuery<GroupMember> preparedQuery = groupMemberQueryBuilder
                    .where()
                    .eq(GroupMember.COLUMN_GROUP_ID, groupId)
                    .and()
                    .eq(GroupMember.COLUMN_USER_ID, userId)
                    .prepare();
            List<GroupMember> result = groupMemberDao.query(preparedQuery);
            if (result != null && !result.isEmpty())
                return result.get(0);
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<GroupMember> GetGroupMembersInGroup(DatabaseHelper dbhelper, String groupId, List<String> userIdList){
        RuntimeExceptionDao<GroupMember, Long> groupMemberDao = dbhelper.getGroupMemberDao();
        QueryBuilder<GroupMember, Long> groupMemberQueryBuilder = groupMemberDao.queryBuilder();
        try{
            PreparedQuery<GroupMember> preparedQuery = groupMemberQueryBuilder
                    .where()
                    .eq(GroupMember.COLUMN_GROUP_ID, groupId)
                    .and()
                    .in(GroupMember.COLUMN_USER_ID, userIdList)
                    .prepare();
            List<GroupMember> result = groupMemberDao.query(preparedQuery);
            if (result != null && !result.isEmpty())
                return result;
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
