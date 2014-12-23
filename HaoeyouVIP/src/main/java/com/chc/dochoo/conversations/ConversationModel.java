package com.chc.dochoo.conversations;

import android.provider.ContactsContract;
import android.util.Log;

import com.chc.found.models.DatabaseHelper;
import com.chc.found.models.GroupMember;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HenryW on 1/10/14.
 */
public abstract class ConversationModel {
    private static final List<PrivateConversation> emptyPrivateList = new ArrayList<PrivateConversation>(0);

    public static boolean updatePrivateConversationLastMessageId(DatabaseHelper helper, String entityId, String msgId) {
        RuntimeExceptionDao<PrivateConversation, Long> privateConversationDao = getPrivateConversationDao(helper);
        QueryBuilder<PrivateConversation,Long> privateConversationLongQueryBuilder = privateConversationDao.queryBuilder();
        try {
            PreparedQuery<PrivateConversation> preparedQuery
                    = privateConversationLongQueryBuilder.where().eq(PrivateConversation.COLUMN_TARGET_ID, entityId).prepare();
            List<PrivateConversation> result = privateConversationDao.query(preparedQuery);
            if (!result.isEmpty()) {
                PrivateConversation con = result.get(0);
                con.setLastMsgId(msgId);
                updatePrivateConversation(helper, con);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static synchronized PrivateConversation getPrivateConversationByEntityId(DatabaseHelper helper, String entityId) {
        RuntimeExceptionDao<PrivateConversation, Long> privateConversationDao = getPrivateConversationDao(helper);
        QueryBuilder<PrivateConversation,Long> privateConversationLongQueryBuilder = privateConversationDao.queryBuilder();
        try {
            PreparedQuery<PrivateConversation> preparedQuery
                    = privateConversationLongQueryBuilder.where().eq(PrivateConversation.COLUMN_TARGET_ID, entityId).prepare();
            List<PrivateConversation> result = privateConversationDao.query(preparedQuery);
            if (!result.isEmpty()) {
                return result.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PrivateConversation getPrivateConversationById(DatabaseHelper helper, String id) {
        RuntimeExceptionDao<PrivateConversation, Long> privateConversationDao = getPrivateConversationDao(helper);
        QueryBuilder<PrivateConversation,Long> privateConversationLongQueryBuilder = privateConversationDao.queryBuilder();
        try {
            PreparedQuery<PrivateConversation> preparedQuery
                    = privateConversationLongQueryBuilder.where().eq(PrivateConversation.COLUMN_NAME_ID, id).prepare();
            List<PrivateConversation> result = privateConversationDao.query(preparedQuery);
            if (!result.isEmpty()) {
                return result.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<PrivateConversation> getAllPrivateConversations(DatabaseHelper helper) {
        RuntimeExceptionDao<PrivateConversation, Long> privateConversationDao = getPrivateConversationDao(helper);

        return privateConversationDao.queryForAll();
    }

    public static List<GroupConversation> getAllGroupConversation(DatabaseHelper helper){
        RuntimeExceptionDao<GroupConversation, Long> groupConversationDao = getGroupConversationDao(helper);
        return groupConversationDao.queryForAll();
    }

    public static synchronized PrivateConversation createPrivateConversation(DatabaseHelper helper, String userId, String entityId) {
        PrivateConversation conversation = getPrivateConversationByEntityId(helper, entityId);
        if (conversation != null) return conversation;
        RuntimeExceptionDao<PrivateConversation, Long> privateConversationDao = getPrivateConversationDao(helper);
        PrivateConversation con = new PrivateConversation(userId, entityId);
        privateConversationDao.createIfNotExists(con);
        return con;
    }

    public static void updatePrivateConversation(DatabaseHelper helper, PrivateConversation con) {
        RuntimeExceptionDao<PrivateConversation, Long> privateConversationDao = getPrivateConversationDao(helper);
        privateConversationDao.createOrUpdate(con);
    }

    public static void updateGroupConversation(DatabaseHelper helper, GroupConversation con) {
        RuntimeExceptionDao<GroupConversation, Long> groupchatConversation = getGroupConversationDao(helper);
        groupchatConversation.createOrUpdate(con);
    }

    public static void deletePrivateConversation(DatabaseHelper helper, PrivateConversation con) {
        RuntimeExceptionDao<PrivateConversation, Long> privateConversationDao = getPrivateConversationDao(helper);
        privateConversationDao.delete(con);
    }

    public static void deleteGroupConversation(DatabaseHelper dbhelper, GroupConversation con){
        RuntimeExceptionDao<GroupConversation, Long> groupchatConversationDao = getGroupConversationDao(dbhelper);
        groupchatConversationDao.delete(con);
    }

    private static RuntimeExceptionDao<PrivateConversation, Long> getPrivateConversationDao(DatabaseHelper helper) {
        return helper.getPrivateConversationRuntimeDao();
    }

    public static synchronized void deletePrivateConversationIfEmpty(DatabaseHelper dbhelper, String entityId) {
        PrivateConversation conversation = getPrivateConversationByEntityId(dbhelper, entityId);
        if (conversation != null && StringUtils.isNotBlank(conversation.getLastMsgId())) {
            deletePrivateConversation(dbhelper, conversation);
        }
    }

    private static RuntimeExceptionDao<GroupConversation, Long> getGroupConversationDao(DatabaseHelper helper){
        return helper.getGroupConversationRuntimeDao();
    }

    public static synchronized GroupConversation getGroupConversationByGroupId(DatabaseHelper helper, String groupchatId){
        RuntimeExceptionDao<GroupConversation, Long> groupchatConversationDao = getGroupConversationDao(helper);
        QueryBuilder<GroupConversation,Long> groupchatConversationLongQueryBuilder = groupchatConversationDao.queryBuilder();
        try {
            PreparedQuery<GroupConversation> preparedQuery
                    = groupchatConversationLongQueryBuilder.where().eq(GroupConversation.COLUMN_TARGET_GROUPCHAT_ID, groupchatId).prepare();
            List<GroupConversation> result = groupchatConversationDao.query(preparedQuery);
            if (!result.isEmpty()) {
                return result.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static GroupConversation getGroupConversationById(DatabaseHelper helper, String conversationId){
        RuntimeExceptionDao<GroupConversation, Long> groupchatConversationDao = getGroupConversationDao(helper);
        QueryBuilder<GroupConversation,Long> groupchatConversationLongQueryBuilder = groupchatConversationDao.queryBuilder();
        try {
            PreparedQuery<GroupConversation> preparedQuery
                    = groupchatConversationLongQueryBuilder.where().eq(GroupConversation.COLUMN_NAME_ID, conversationId).prepare();
            List<GroupConversation> result = groupchatConversationDao.query(preparedQuery);
            if (!result.isEmpty()) {
                return result.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static synchronized GroupConversation createGroupConversationIfNotExist(DatabaseHelper helper, String leaderId,  String groupId, String topic){
        GroupConversation conversation = getGroupConversationByGroupId(helper, groupId);
        if (conversation != null) return conversation;
        RuntimeExceptionDao<GroupConversation, Long> groupchatConversationDao = getGroupConversationDao(helper);
        GroupConversation con = new GroupConversation(leaderId, groupId, topic);
        groupchatConversationDao.createIfNotExists(con);
        return con;
    }

    public static synchronized GroupConversation createGroupConversationIfNotExist(DatabaseHelper helper, JSONObject json) throws JSONException{
        JSONObject groupJson = json.getJSONObject("GroupChat");
        String leaderId = groupJson.optString("leader");
        String groupId = groupJson.optString("id");
        String topic = groupJson.optString("title");
        GroupConversation group = createGroupConversationIfNotExist(helper, leaderId, groupId, topic);
        return group;
    }
}
