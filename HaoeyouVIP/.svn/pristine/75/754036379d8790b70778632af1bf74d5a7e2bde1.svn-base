package com.chc.dochoo.conversations;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONObject;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.chc.exceptions.InternalErrorException;
import com.chc.found.config.Apis;
import com.chc.found.models.DatabaseHelper;
import com.chc.found.network.NetworkRequestsUtil;
import com.chc.found.presenters.IMessagePresenter;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;

public class InstantMessageModel {

    private static final String TAG = "InstantMessageModel";
    private static final Long PAGE_LIMIT = 100l;
    private IMessagePresenter mPresenter;

    public InstantMessageModel(IMessagePresenter p) {
        super();
        mPresenter = p;
    }

    /**
     * select 100 latest cached messages having the same entity id
     *
     * @return cached messages from memory or empty list if not any
     */
    public static List<InstantMessage> getInstantMessagesByEntityId(DatabaseHelper dbhelper, String entityId) {
        List<InstantMessage> msgList;
        RuntimeExceptionDao<InstantMessage, Long> dao = dbhelper
                .getInstantMsgRuntimeDao();
        QueryBuilder<InstantMessage, Long> queryBuilder
                = dao.queryBuilder();
        try {
            queryBuilder
                    .orderBy(InstantMessage.COLUMN_NAME_TIME, false) // latest first order
                    .where().eq(InstantMessage.COLUMN_NAME_ENTITY_ID, entityId)
                    .and()
                    .isNull(InstantMessage.COLUMN_NAME_GROUP_ID);
            queryBuilder.limit(PAGE_LIMIT); // only get PAGE_LIMIT number of latest msgs
            msgList = dao.query(queryBuilder.prepare());
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
            msgList = new ArrayList<InstantMessage>(0);
        }
        Collections.reverse(msgList); // reverse to ascending order of msg time
        return msgList;
    }

    /**
     * select 100 latest cached messages having the same group id
     *
     * @return cached messages from memory or empty list if not any
     */

    public static List<InstantMessage> getInstantMessagesByGroupId(DatabaseHelper helper, String groupId){
        List<InstantMessage> msgList;
        RuntimeExceptionDao<InstantMessage, Long> dao = helper.getInstantMsgRuntimeDao();
        QueryBuilder<InstantMessage, Long> queryBuilder
                = dao.queryBuilder();
        try {
            queryBuilder
                    .orderBy(InstantMessage.COLUMN_NAME_TIME, false) // latest first order
                    .where().eq(InstantMessage.COLUMN_NAME_GROUP_ID, groupId);
            queryBuilder.limit(PAGE_LIMIT); // only get PAGE_LIMIT number of latest msgs
            msgList = dao.query(queryBuilder.prepare());
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
            msgList = new ArrayList<InstantMessage>(0);
        }
        Collections.reverse(msgList); // reverse to ascending order of msg time
        return msgList;

    }

    public static InstantMessage getLastGroupMessageByGroupId(DatabaseHelper dbhelper, String groupId) {
        RuntimeExceptionDao<InstantMessage, Long> dao = dbhelper
                .getInstantMsgRuntimeDao();
        QueryBuilder<InstantMessage, Long> queryBuilder
                = dao.queryBuilder();
        try {
            queryBuilder
                    .orderBy(InstantMessage.COLUMN_NAME_TIME, false) // latest first order
                    .where().eq(InstantMessage.COLUMN_NAME_GROUP_ID, groupId);
            queryBuilder.limit(1l); // only get PAGE_LIMIT number of latest msgs
            List<InstantMessage> query = dao.query(queryBuilder.prepare());
            return query.isEmpty() ? null : query.get(0);
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }

    public static InstantMessage getLastInstantMessageByEntityId(DatabaseHelper dbhelper, String entityId) {
        RuntimeExceptionDao<InstantMessage, Long> dao = dbhelper
                .getInstantMsgRuntimeDao();
        QueryBuilder<InstantMessage, Long> queryBuilder
                = dao.queryBuilder();
        try {
            queryBuilder
                    .orderBy(InstantMessage.COLUMN_NAME_TIME, false) // latest first order
                    .where().eq(InstantMessage.COLUMN_NAME_ENTITY_ID, entityId);
            queryBuilder.limit(1l); // only get PAGE_LIMIT number of latest msgs
            List<InstantMessage> query = dao.query(queryBuilder.prepare());
            return query.isEmpty() ? null : query.get(0);
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }

    public void loadMessageFromServer(DatabaseHelper dbhelper, String docId, String userId) {
        new LoadMessageTask(dbhelper).execute(docId, userId);
    }

    public static void createInstantMessage(DatabaseHelper dbhelper,
                                            List<InstantMessage> msgList) {
        RuntimeExceptionDao<InstantMessage, Long> dao = dbhelper
                .getInstantMsgRuntimeDao();

        for (InstantMessage msg : msgList) {
            dao.createIfNotExists(msg);
        }
    }

    public static void createInstantMessage(DatabaseHelper dbhelper,
                                            InstantMessage msg) {
        RuntimeExceptionDao<InstantMessage, Long> dao = dbhelper
                .getInstantMsgRuntimeDao();

        dao.createIfNotExists(msg);
    }

    /**
     * select a message from database by its unique id
     *
     * @param dbhelper
     * @param id
     * @return
     */
    public static InstantMessage getMessageById(DatabaseHelper dbhelper, String id) {
        RuntimeExceptionDao<InstantMessage, Long> instantMsgRuntimeDao = dbhelper
                .getInstantMsgRuntimeDao();
        QueryBuilder<InstantMessage, Long> queryBuilder = instantMsgRuntimeDao
                .queryBuilder();
        InstantMessage res = null;
        try {
            // WHERE _id = id
            queryBuilder.where()
                    .eq(InstantMessage.COLUMN_NAME_ID, id);
            List<InstantMessage> resList = instantMsgRuntimeDao.query(queryBuilder
                    .prepare());
            if (resList != null && !resList.isEmpty())
                res = resList.get(0);
        } catch (SQLException e) {
            // ignore
        }

        return res;
    }

    public static List<InstantMessage> getInstantMessagesBeforeTime(DatabaseHelper helper, long time) {
        RuntimeExceptionDao<InstantMessage, Long> dao = helper
                .getInstantMsgRuntimeDao();
        QueryBuilder<InstantMessage, Long> queryBuilder
                = dao.queryBuilder();
        try {
            queryBuilder.where().not().ge(FoundMessage.COLUMN_NAME_TIME, time);
            return dao.query(queryBuilder.prepare());
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
            return new ArrayList<InstantMessage>(0);
        }
    }

    /**
     * get message list server, will do nothing if the params number not equals to 2
     * params params[0] is docId, params[1] is userId
     *
     * @return
     */
    private class LoadMessageTask extends AsyncTask<String, Void, String> {

        private DatabaseHelper dbhelper;
        private String docId;

        public LoadMessageTask(DatabaseHelper dbhelper) {
            super();
            this.dbhelper = dbhelper;
        }

        @Override
        protected String doInBackground(String... params) {
            String msg = null;
            if (params.length != 2)
                return null;

            docId = params[0];
            String userId = params[1];

            try {
                msg = NetworkRequestsUtil.getMessages(docId, userId);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            } catch (InternalErrorException e) {
                Log.e(TAG, e.getMessage(), e);
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            List<InstantMessage> messageList = null;
            if (result == null) {
                if (Apis.DEBUG) Log.w(TAG, "instant message result is null!");
            } else {
                messageList = new ArrayList<InstantMessage>();
//				if (Apis.DEBUG) Log.w(TAG, result);
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        JSONObject jsonObject = jsonArray.optJSONObject(i);
                        InstantMessage im = new InstantMessage(jsonObject, docId);
                        messageList.add(im);
                    }
                    createInstantMessage(dbhelper, messageList);
                } catch (Exception e) {
                    messageList = null;
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            mPresenter.onMessageLoaded(messageList);
        }

    }

    public static InstantMessage updateInstantMessage(DatabaseHelper dbhelper, InstantMessage im) {
        RuntimeExceptionDao<InstantMessage, Long> dao = dbhelper
                .getInstantMsgRuntimeDao();
        dao.createOrUpdate(im);
        return im;
    }

    public static InstantMessage updateStatusById(DatabaseHelper helper, String id, OutgoingMessageStatus newStatus) {
        InstantMessage msg = getMessageById(helper, id);
        if (msg != null) {
            msg.setStatus(newStatus);
            return updateInstantMessage(helper, msg);
        }
        return null;
    }

    public static void deleteMessages(DatabaseHelper helper, List<InstantMessage> imList) {
        if (imList == null || helper == null) return;
        RuntimeExceptionDao<InstantMessage, Long> dao = helper.getInstantMsgRuntimeDao();
        for (InstantMessage msg : imList) {
            dao.delete(msg);
        }
    }

    public static void deleteMultimediaContentFromMsg(List<InstantMessage> messageList) {
        for (InstantMessage im : messageList) {
            if (im.getMessageType() != InstantMessage.InstantMessageType.TEXT) {
                // might have multi-media path
                tryDelete(im.getMultimediaPath());
            }
        }
    }


    private static boolean tryDelete(String path) {
        if (StringUtils.isBlank(path)) return false;
        if (path.contains("file://")) {
            path = Uri.parse(path).getPath();
        }
        File f = new File(path);
        if (f.exists() && f.isFile()) {
            return f.delete();
        }
        return false;
    }

}