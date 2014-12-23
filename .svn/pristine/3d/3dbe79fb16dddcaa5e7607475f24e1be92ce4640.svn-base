package com.chc.found.presenters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.chc.dochoo.CHCApplication;
import com.chc.dochoo.conversations.ConversationModel;
import com.chc.dochoo.conversations.GroupChatTask;
import com.chc.dochoo.conversations.FoundMessage;
import com.chc.dochoo.conversations.GroupConversation;
import com.chc.dochoo.conversations.GroupMemberModel;
import com.chc.dochoo.conversations.InstantMessage;
import com.chc.dochoo.conversations.InstantMessage.InstantMessageType;
import com.chc.dochoo.conversations.InstantMessageModel;
import com.chc.dochoo.conversations.OutgoingMessageStatus;
import com.chc.dochoo.conversations.PrivateConversation;
import com.chc.exceptions.InternalErrorException;
import com.chc.found.FoundSettings;
import com.chc.found.config.Apis;
import com.chc.found.models.AddEntityState;
import com.chc.found.models.DatabaseHelper;
import com.chc.found.models.EntityModel;
import com.chc.found.models.EntityUser;
import com.chc.found.network.NetworkRequestsUtil;
import com.chc.found.views.IInstantMessageView;
import com.chcgp.hpad.util.general.CHCGeneralUtil;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class InstantMessagePresenter implements IMessagePresenter, IEntityPresenter {

    private static final String TAG = "InstantMessagePresenter";
    public static final String ANDROID_URI_FILE_PREFIX = "file://";

    IInstantMessageView mView;
    InstantMessageModel mModel;
    EntityModel entityModel;
    private DatabaseHelper helper;

    public InstantMessagePresenter(IInstantMessageView mView, DatabaseHelper helper) {
        super();
        this.mView = mView;
        this.mModel = new InstantMessageModel(this);
        this.entityModel = new EntityModel(this);
        this.helper = helper;
    }

    @Override
    public void loadData(String userId, String targetId, String pushId) {
//        // mark read
//        EntityUser entityUser = EntityModel.getEntityById(helper, targetId);
//        assert entityUser != null : "Cannot find user using id " + targetId;
//        if (entityUser == null) return;
//        entityUser.setNumUnread(0);
//        if (entityModel != null)
//            entityModel.updateEntityUser(helper, entityUser);

        List<InstantMessage> cachedList = InstantMessageModel.getInstantMessagesByEntityId(helper, targetId);
        assert this.mView != null : "mView is null!";
        if (mView != null)
            this.mView.onMessageLoaded(cachedList);
    }

    @Override
    public void loadGroupData(String userId, String targetGroupId, String pushId) {
        List<InstantMessage> cachedList = InstantMessageModel.getInstantMessagesByGroupId(helper, targetGroupId);
        assert this.mView != null:"mView is null";
        if(mView != null)
            this.mView.onMessageLoaded(cachedList);
    }


    @Override
    @Deprecated
    public void loadServerData(String userId, String targetId, String pushId) {
        if (null != userId)
            mModel.loadMessageFromServer(this.helper, targetId, userId);
    }

    @Override
    public void onMessageLoaded(List<? extends FoundMessage> msgList) {
        mView.onMessageLoaded(msgList);
    }

    @Override
    public void writeData(FoundMessage msg) {
        if (msg instanceof InstantMessage) {
            InstantMessageModel.createInstantMessage(this.helper, (InstantMessage) msg);
        }

    }

    @Override
    public InstantMessage getMessageById(String id) {
        return InstantMessageModel.getMessageById(this.helper, id);

    }

    @Override
    public InstantMessage getGroupMessageById(String id) {
        return InstantMessageModel.getMessageById(this.helper, id);
    }

    /**
     * Post a text message
     *
     * @param content
     * @param doctorId
     * @param userId
     */
    public void postTextMessage(String content, String doctorId, String userId, String pushid, String groupId) {
        if (StringUtils.isBlank(content)) return;
        InstantMessage im = InstantMessage.newMessage(doctorId, groupId, OutgoingMessageStatus.SENDING, true, content, getLastMessageTime(doctorId, groupId));
        doPostMsg(im, content, doctorId, userId, pushid);
    }

    public void postImageMessage(Uri fileUri, String targetId, String groupId, String userId, String pushid) {
        String filename = getFileName(fileUri);
        if (filename == null) return;
        String newUri = storeOwnImageCache(fileUri, filename, FoundSettings.DOWNLOAD_PATH);
        if (newUri == null) {
            mView.onUnsupportedFormatOrSize();
            return;
        }

        // post image
        InstantMessage im = InstantMessage.newImageMsg(targetId, groupId, OutgoingMessageStatus.SENDING, true, filename, newUri, getLastMessageTime(targetId, groupId));
        doPostMsg(im, filename, targetId, userId, pushid);
    }

//    public void postImageMessage(Bitmap pic, String targetId, String userId, String pushid) {
//        String filename = randomFileName(InstantMessageType.IMAGE);
//        if (filename == null) return;
//        String newUri = storeOwnImageCache(pic, filename, FoundSettings.DOWNLOAD_PATH);
//        if (newUri == null) {
//            mView.onUnsupportedFormatOrSize();
//            return;
//        }
//
//        // post image
//        InstantMessage im = InstantMessage.newImageMsg(targetId, OutgoingMessageStatus.SENDING, true, filename, newUri);
//
//        doPostMsg(im, filename, targetId, userId, pushid);
//    }

//    private String randomFileName(InstantMessageType image) {
//        switch (image) {
//            case IMAGE:
//                return UUID.randomUUID().toString() + ".jpg";
//        }
//        return UUID.randomUUID().toString();
//    }

    /*
     * Store Dochoo's own cache of the image and encrypt it
     * returns new Uri string of cache file
     */
    private String storeOwnImageCache(Uri fileUri, String filename,
                                      String downloadPath) {
        if (Apis.DEBUG) Log.w(TAG, "writing to cache + " + fileUri);
        InputStream input = null;
        BufferedOutputStream bos = null;
        String newFilePath = downloadPath + filename;
        if (Apis.DEBUG) Log.w(TAG, "new file path: " + newFilePath);
        try {
            input = mView.getCHCApplication().getContentResolver().openInputStream(fileUri);
            if (Apis.DEBUG) Log.w(TAG, "opened inputstream");
            if (input == null) return null;
            File downloadPathFile = new File(downloadPath);
            if (!downloadPathFile.exists()) downloadPathFile.mkdirs();

            if (Apis.DEBUG) Log.w(TAG, "creating input output stream");
            Options options = CHCGeneralUtil.decodeStreamOptionsOnly(input, FoundSettings.MAX_PIXEL);
            CHCGeneralUtil.closeQuietly(input);

            // open again
            input = mView.getCHCApplication().getContentResolver().openInputStream(fileUri);

            Bitmap bitmap = BitmapFactory.decodeStream(input, null, options);
            if (bitmap == null) return null;

            // rotate the bitmap according to its orientation
            int rotate = getOrientation(mView.getCHCApplication(), fileUri);
            if (rotate != -1) {
                Matrix matrix = new Matrix();
                matrix.postRotate(rotate);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }

            File cacheFile = new File(newFilePath);
            if (cacheFile.exists()) cacheFile.delete();
            bos = new BufferedOutputStream(new FileOutputStream(cacheFile));
            bitmap.compress(CompressFormat.JPEG, 90, bos);

            if (Apis.DEBUG) Log.w(TAG, "stream written and closed");

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        } finally {
            CHCGeneralUtil.closeQuietly(input);
            CHCGeneralUtil.closeQuietly(bos);
        }
        return ANDROID_URI_FILE_PREFIX + newFilePath;
    }

    public static int getOrientation(Context context, Uri photoUri) {
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[] { MediaStore.Images.ImageColumns.ORIENTATION },
                null, null, null);

        try {
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0);
            } else {
                return -1;
            }
        } finally {
            CHCGeneralUtil.closeQuietly(cursor);
        }
    }

    private void doPostMsg(InstantMessage im, String content, String doctorId, String userId, String pushid) {

        if(StringUtils.isBlank(doctorId) && StringUtils.isBlank(im.getGroupId()))   return;
        onPrePostMsg(im);
        this.mView.onAddNewPostMessage(im);
        new PostMessageAsyncTask(im).execute(doctorId, content, userId, pushid);



    }

    private String getFileName(Uri fileUri) {
        String path = fileUri.getPath();
        if (path == null) return null;
        int index = path.lastIndexOf('/');
        int secondindex = path.lastIndexOf(':');
        index = Math.max(index, secondindex);
        if (index == -1) return "";

        return path.substring(index + 1) + ".jpg";
    }

    /**
     * check if the postfix is jpg or JPG
     *
     * @param postfix
     * @return
     */
    private boolean isNotValidImage(String postfix) {
        return !postfix.equals("jpg") && !postfix.equals("JPG");
    }

    /**
     * returns the postfix after {@code .} (dot)
     *
     * @param filename
     * @return
     */
    private String getPostfix(String filename) {
        int index = filename.lastIndexOf('.');
        if (index == -1) return "";

        return filename.substring(index + 1);
    }

    /**
     * Does preprocessing for posting a message
     *
     * @param im
     */
    @Deprecated
    private void onPrePostMsg(InstantMessage im) {
//        EntityUser entityUser = EntityModel.getEntityById(helper, im.getEntityId());
//        entityUser.setNumUnread(0);
//        entityUser.setLastMsgTime(System.currentTimeMillis());
//        entityModel.updateEntityUser(helper, entityUser);
    }

    public void onVoiceMessageDamaged(final InstantMessage item) {
        CHCGeneralUtil.delete(item.getMultimediaPath());
        item.setMultimediaPath(null);
        InstantMessageModel.updateInstantMessage(mView.getCHCApplication().getHelper(), item);
    }

    /**
     * Examines message list and sync the correct status to server
     * @param imList
     */
    public void examineInstantMessageStatus(List<InstantMessage> imList) {
        for (InstantMessage im : imList) {
            if (im.isFromPatient()) continue; // we are only interested in messages received

            switch(im.getMessageType()) {
                case TEXT:
                    if (im.getStatus() != OutgoingMessageStatus.READ) {
                        // any text msgs not read should now be read.
                        onMessageRead(im);
                    }
                    break;
                default:
                    if (!im.isUnread() && im.getStatus() != OutgoingMessageStatus.READ) {
                        // if the message is marked read locally but failed to
                        // sync to the server
                        onMessageRead(im);
                    } else if (im.isUnread() && im.getStatus() != OutgoingMessageStatus.DELIVERED) {
                        // if the message is not marked read locally but should be of delivered status
                        onMessageDelivered(im);
                    } else {
                        // where the status of the message is consistent, ignore
                    }
            }
        }
    }

    /**
     * Contacts the server to delete the message
     * Must be called in worker thread
     *
     * @return
     */
    public boolean syncDeleteMessage(String... params) {
        try {
            NetworkRequestsUtil.postDeleteSingleMessage(params);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void deleteMessageLocalDb(String msgId) {
        InstantMessage messageById = InstantMessageModel.getMessageById(helper, msgId);
        if (messageById != null) {
            List<InstantMessage> list = new ArrayList<>(1);
            list.add(messageById);
            InstantMessageModel.deleteMessages(helper, list);
        }
    }

    public Long getLastMessageTime(String entityId, String groupId){
        InstantMessage lastMsg;
        if(StringUtils.isNotBlank(groupId)){
            //groupMessage
            lastMsg = getLastGroupMessage(groupId);
        }
        else    lastMsg = getLastMessage(entityId);
        return lastMsg == null?System.currentTimeMillis():lastMsg.getTime()+1000;
    }

    public InstantMessage getLastMessage(String entityId) {
        return InstantMessageModel.getLastInstantMessageByEntityId(helper, entityId);
    }

    public InstantMessage getLastGroupMessage(String groupId){
        return InstantMessageModel.getLastGroupMessageByGroupId(helper, groupId);
    }
    private class PostMessageAsyncTask extends AsyncTask<String, Void, String> {

        private InstantMessage im;
        private int failedCode;

        public PostMessageAsyncTask(InstantMessage im) {
            super();
            this.im = im;
        }

        @Override
        /**
         * params:
         *
         * docid 0
         * content 1
         * userid 2
         * pushid 3
         * groupid 4
         */
        protected String doInBackground(String... params) {
            String result = null;
            try {
                InstantMessageType messageType = im.getMessageType();
                if (messageType == null) {
                    // if no type, fail fast
                    return null;
                }
                switch (messageType) {
                    case TEXT:
                        if(StringUtils.isBlank(im.getGroupId()))
                            //private conversation
                            return NetworkRequestsUtil.postMessage(params[0], params[1], params[2], params[3]);
                        else
                            //group conversation
                            return NetworkRequestsUtil.postGroupChatMessage(params[2],params[3],im.getGroupId(),params[1]);
                    case IMAGE:
                        InputStream input;
                        try {
                            if (Apis.DEBUG)
                                Log.w(TAG, "opening cache file stream: " + this.im.getMultimediaPath());
                            input = mView.getCHCApplication().getContentResolver().openInputStream(Uri.parse(this.im.getMultimediaPath()));
                        } catch (FileNotFoundException e1) {
                            return null;
                        }
                        if (input == null) return null;
                        if (Apis.DEBUG) Log.w(TAG, "input stream opened, uploading now");
                        return NetworkRequestsUtil.postImageMessage(input, params[0], im.getGroupId(), this.im.getContent(), params[2], params[3],
                                this.im.getMultimediaPath(), InstantMessageType.IMAGE.getTypeStr());
                    case VOICE:
                        File f = new File(im.getMultimediaPath());
                        if (f.exists() && f.isFile()) {
                            return NetworkRequestsUtil.postMobileUploadFile(params[2], params[0], im.getGroupId(), params[3], params[1],
                                    new FileInputStream(f), Apis.PARAM_TYPE_UPLOAD_VOICE);
                        }
                        return null;
                    default:
                        return null;
                }
            } catch (InternalErrorException e) {
                switch (e.getErrorCode()) {
                    case 601:
                        Log.e(TAG, "wrong doctor or patient id");
                        break;
                    case 603:
                        Log.e(TAG, "Wrong push id");
                        failedCode = 603;
                        break;
                    default:
                        Log.e(TAG, "unknown error code " + e.getErrorCode());
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onCancelled(String result) {
            if (StringUtils.isNotEmpty(result)) {
                onPostSucceed(result, im.getEntityId());
            }
            // if failed ignore since the task is already cancelled
        }

        @Override
        protected void onPostExecute(String result) {
            if (StringUtils.isNotEmpty(result)) {
                onPostSucceed(result, im.getEntityId());
            } else {
                onPostFailed(failedCode);
            }
        }

        private void onPostFailed(int failedCode) {
            // when failed display failure
            this.im.setStatus(OutgoingMessageStatus.FAILED);
            this.im.setTime(getLastMessageTime(this.im.getEntityId(), this.im.getGroupId()));
            /*InstantMessage last = InstantMessageModel.getLastInstantMessageByEntityId(helper,this.im.getEntityId());
            if(last == null){
                this.im.setTime(System.currentTimeMillis());
            }
            else{
                this.im.setTime(last.getTime()+1000);
            }*/
            if(StringUtils.isBlank(this.im.getEntityId()))
                im.setEntityId("group");
            writeData(im);
            if (failedCode != 0) {
                // TODO: need reauthenticate
            }
        }

        /**
         * @param json     InstantMessage view object json
         * @param entityId
         */
        private void onPostSucceed(String json, String entityId) {
            // when succeeded update the status
            try {
                String messageTarget = StringUtils.isBlank(entityId)?"group":entityId;
                JSONObject jo = new JSONObject(json);
                InstantMessage response = new InstantMessage(jo, messageTarget);
                InstantMessage cached = InstantMessageModel.getMessageById(helper,this.im.getId());
                if(cached != null){
                    // delete cached failed message
                    List<InstantMessage> cachedMsg = new ArrayList<>();
                    cachedMsg.add(cached);
                   InstantMessageModel.deleteMessages(helper,cachedMsg);
                }

                this.im.setEntityId(messageTarget);
                this.im.setId(response.getId());
                this.im.setTime(response.getTime());
                this.im.setStatus(OutgoingMessageStatus.SENT);

                // and write to database
                writeData(im);


            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
                onPostFailed(0);
            }
        }

    }

    public void onMessageResend(InstantMessage item, String userId, String doctorId, String pushid) {

        item.setStatus(OutgoingMessageStatus.SENDING);

        onPrePostMsg(item);

        new PostMessageAsyncTask(item).execute(doctorId, item.getContent(), userId, pushid);
    }

    /**
     * handles group push messaging
     */

    public void onNewGroupMsgReceived(final InstantMessage im){
        if(im == null || StringUtils.isBlank(im.getGroupId()))    return;
        CHCApplication chcApplication = mView.getCHCApplication();
        if(chcApplication == null)  return;
        final String groupId = im.getGroupId();
        if(StringUtils.isBlank(groupId))    return;

        GroupConversation group = ConversationModel.getGroupConversationByGroupId(helper, groupId);
        if(group == null){
            String newGroupId = GroupChatTask.updateGroupInformation(helper,chcApplication.getUserId(), chcApplication.getRegId() ,groupId);
            if(StringUtils.isBlank(newGroupId)) return;
            group = ConversationModel.getGroupConversationById(helper,newGroupId);
        }

        // download and write the message to db, if type is not text
        if (im.getMessageType() != null && im.getMessageType() != InstantMessageType.TEXT) {

            String result = null;
            boolean b = NetworkRequestsUtil.downloadFile(FoundSettings.DOWNLOAD_PATH, im.getContent(), im.getId());
            if (b) {
                String prefix =
                        im.getMessageType() == InstantMessageType.IMAGE
                                ? ANDROID_URI_FILE_PREFIX
                                : "";
                result = prefix + FoundSettings.DOWNLOAD_PATH + im.getContent();
            }
            im.setMultimediaPath(result);
        }
        updateData(im);
        group.setLastMsgId(im.getId());
        group.setNumOfUnread(group.getNumOfUnread()+1);
        ConversationModel.updateGroupConversation(helper, group);

    }
    /**
     * handles push messaging, must be called in worker thread
     *
     * @param im
     */
    public void onNewMsgReceived(final InstantMessage im) {
        if (im == null || im.getEntityId() == null) return;
        CHCApplication chcApplication = mView.getCHCApplication();
        if (chcApplication == null) return;

        String entityId = im.getEntityId();
        EntityUser entityUser = EntityModel.getEntityById(helper, entityId);
        if (entityUser == null) {
            // we do not have this entity in db, need to download
            // this can be done here blocking, because we are
            // in a worker thread
            try {
                refreshAllContactLists(chcApplication.getUserId(), chcApplication.getRegId(),
                        FoundSettings.getInstance(chcApplication).isPatient());

                if (EntityModel.getEntityById(helper, entityId) == null) return;

            } catch (Exception e) {
                e.printStackTrace();
                // failed, abort this handling
                return;
            }
        }

        // download and write the message to db, if type is not text
        if (im.getMessageType() != null && im.getMessageType() != InstantMessageType.TEXT) {

            String result = null;
            boolean b = NetworkRequestsUtil.downloadFile(FoundSettings.DOWNLOAD_PATH, im.getContent(), im.getId());
            if (b) {
                String prefix =
                        im.getMessageType() == InstantMessageType.IMAGE
                                ? ANDROID_URI_FILE_PREFIX
                                : "";
                result = prefix + FoundSettings.DOWNLOAD_PATH + im.getContent();
            }
            im.setMultimediaPath(result);
        }
        // write to db regardless of its type
        updateData(im);

        PrivateConversation privateConversation = createIfNotExistsPrivateConversation(helper, entityId, im.getId(),
                chcApplication.getUserId());
        privateConversation.setLastMsgId(im.getId());
        privateConversation.setNumOfUnread(privateConversation.getNumOfUnread()+1);
        ConversationModel.updatePrivateConversation(helper, privateConversation);
        // after data is written, acknowledge the receipt to server
        onMessageDelivered(im);
        // service can continue to work, such as notifying user
    }

    /**
     * Loads from server all contact lists and writes into database, must be called in worker thread
     * @param userId
     * @param pushId
     * @param isPatient
     * @return
     */
    private void refreshAllContactLists(String userId, String pushId, boolean isPatient) {
        if (isPatient) {
            EntityModel.loadProfessionalListForPatientOnWorkerThread(helper, userId, pushId);
        } else {
            EntityModel.loadProRelationListOnWorkerThread(helper, userId, pushId);
            EntityModel.loadPatientListOnWorkerThread(helper, userId, pushId);
            EntityModel.loadColleagueListOnWorkerThread(helper, userId, pushId);
        }
    }

    /**
     * tries to update the delivered status to server,
     * upon success writes the status to db
     * upon failure does nothing
     * @param im
     */
    public void onMessageDelivered(InstantMessage im) {

        try {
            markInstantMessageDelivered(im);

            im.setStatus(OutgoingMessageStatus.DELIVERED);
            // write to db regardless of its type
            updateData(im);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private PrivateConversation createIfNotExistsPrivateConversation(
            DatabaseHelper helper, String entityId, String msgId, String userId) {
        PrivateConversation con = ConversationModel.getPrivateConversationByEntityId(helper, entityId);
        if (con == null) {
            con = ConversationModel.createPrivateConversation(helper,
                    mView.getCHCApplication().getUserId(), entityId);
        }
        return con;
    }


    /**
     * To be called in worker thread
     * @param im
     */
    private void markInstantMessageDelivered(final InstantMessage im) throws IOException, InternalErrorException {
        CHCApplication chcApplication = mView.getCHCApplication();
        if (chcApplication != null)
            updateIncomingMessageStatus(chcApplication.getUserId(), chcApplication.getRegId(),
                    im.getId(), OutgoingMessageStatus.DELIVERED.getStatusCode());
    }

    /**
     * To be called in main thread
     * @param im
     */
    private void onIncomingMessageRead(final InstantMessage im) {
        if (im == null) return;
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {
                if (mView != null) {
                    CHCApplication chcApplication = mView.getCHCApplication();
                    if (chcApplication != null)
                        try {
                            updateIncomingMessageStatus(chcApplication.getUserId(), chcApplication.getRegId(),
                                    im.getId(), OutgoingMessageStatus.READ.getStatusCode());
                            return true;
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG,"not updating message status because: " + e.getMessage());
                            return false;
                        }
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (aBoolean == null || !aBoolean) {
                    // failed, not updating the status
                } else {
                    im.setStatus(OutgoingMessageStatus.READ);
                    InstantMessageModel.updateInstantMessage(helper, im);
                }
            }
        }.execute();
    }

    /**
     * Posts to server about the status update of the specific message. Does not handle failed requests
     * @param userId
     * @param pushId
     * @param msgId
     * @param status
     */
    private void updateIncomingMessageStatus(String userId, String pushId, String msgId, int status)
            throws IOException, InternalErrorException {
        Log.e("MessageUpdate","Update message( " +msgId+" ) status to " +status+ " response: " + NetworkRequestsUtil.postUpdateMessageStatus(userId, pushId, msgId, String.valueOf(status)));
    }

    protected void updateData(InstantMessage fm) {
        InstantMessageModel.updateInstantMessage(this.helper, fm);
    }

    @Override
    public void onEntityUserLoaded(EntityUser user) {
    }

    @Override
    public void onLoadNewEntityFailed(AddEntityState state) {
    }

    @Override
    public void addContact(boolean isPatient, String idOrPin, String userId, String pushId) {
        // ignore
    }

    @Override
    public void onEntityUserListLoaded(List<EntityUser> userlist) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onContactDeleted(EntityUser user) {
        // ignored
    }

    @Override
    public void onContactDeleteFailed() {
        // ignored
    }

    @Override
    public void deleteContact(String targetId, String userId, String pushId) {
        // ignored
    }

    @Override
    public void blockContact(String targetId, String userId, String pushId) {
        // ignored
    }

    /**
     * Get entity user from DB by its id
     *
     * @param targetId
     * @return
     */
    public EntityUser getMsgEntityUser(String targetId) {
        return EntityModel.getEntityById(this.helper, targetId);
    }

    @Override
    public EntityUser getEntityById(String id) {
        return EntityModel.getEntityById(this.helper, id);
    }

    public void downloadContent(final InstantMessage item) {
        if (item == null) return;

        // check if is present
        String path = item.getMultimediaPath();
        if (StringUtils.isNotBlank(path)
                && item.getMessageType() == InstantMessageType.IMAGE) {
            Uri uri = Uri.parse(path);
            try {
                InputStream openInputStream = mView.getCHCApplication().getContentResolver().openInputStream(uri);
                try {
                    //noinspection ConstantConditions
                    openInputStream.close();
                    mView.onDownloadFinished(item);
                    return;
                } catch (Exception ignored) {
                }
            } catch (FileNotFoundException e) {
                // no such file stream
                // proceeds to download
            }
        } else if (StringUtils.isNotBlank(path)) {
            // download finished, not image
            mView.onDownloadFinished(item);
            return;
        } else {
            // not downloaded or other condition
            // continue to below to re-download
        }

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                boolean b = NetworkRequestsUtil.downloadFile(FoundSettings.DOWNLOAD_PATH,
                        item.getContent(), item.getId());
                if (b) {
                    String prefix =
                            item.getMessageType() == InstantMessageType.IMAGE
                                    ? ANDROID_URI_FILE_PREFIX
                                    : "";
                    return prefix + FoundSettings.DOWNLOAD_PATH + item.getContent();
                } else {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                if (result == null) {
                    // failed
                    mView.onDownloadFailed();
                } else {
                    item.setMultimediaPath(result);
                    InstantMessageModel.updateInstantMessage(helper, item);
                    mView.onDownloadFinished(item);
                }
            }

        }.execute();
    }

    public void postVoiceMessage(String entityId, String userId, String groupId, String pushId, String absolutepath,
                                 String filename, long lengthMs) {
        InstantMessage newVoiceMsg = InstantMessage.newVoiceMsg(entityId, groupId,
                OutgoingMessageStatus.SENDING, true, filename, absolutepath, getLastMessageTime(entityId, groupId));
        doPostMsg(newVoiceMsg, filename, entityId, userId, pushId);
    }

    public void onMessageRead(InstantMessage im) {
        if (im == null) return; 
        im.setUnread(false);
        InstantMessageModel.updateInstantMessage(this.helper, im);
        if(StringUtils.isBlank(im.getGroupId()))
            onIncomingMessageRead(im);


    }

    private void onGroupTitleChanged(){
        mView.onUnsupportedFormatOrSize();
    }

    public void close() {
        this.mView = null;
    }

}
