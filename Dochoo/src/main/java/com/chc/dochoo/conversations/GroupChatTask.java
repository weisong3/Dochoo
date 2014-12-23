package com.chc.dochoo.conversations;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.chc.found.FoundSettings;
import com.chc.found.models.DatabaseHelper;
import com.chc.found.models.EntityModel;
import com.chc.found.models.EntityUser;
import com.chc.found.models.GroupMember;
import com.chc.found.network.NetworkRequestsUtil;
import com.test.found.R;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


/**
 * Created by Lance on 5/30/14.
 */
public class GroupChatTask extends AsyncTask<List<String>, Void, String>{
    private static final String TAG = "GroupChatTask";
    private ProgressDialog dialog;
    private Context context;
    private String userId;
    private String pushId;
    private String groupId;
    private DatabaseHelper dbhelper;
    private GroupChatTaskType taskType;
    private Handler myHandler = null;

    /**
     * isSHowProgress means if need to show process dialog when dealing with http request.
     * Using handler for main thread to wait for asnyc task finish
     */


    public GroupChatTask(Context context, DatabaseHelper dbhelper, GroupChatTaskType type, String userId, String pushId, String groupId, Boolean isShowProgress) {
        this.context = context;
        if(isShowProgress)
            this.dialog = new ProgressDialog(this.context);
        else
            this.dialog = null;

        this.taskType = type;
        this.userId = userId;
        this.pushId = pushId;
        this.groupId = groupId;
        this.dbhelper = dbhelper;

    }

    public void setMyHandler(Handler myHandler) {
        this.myHandler = myHandler;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(dialog != null){
            String progressMessage = "";
            switch (taskType){
                case Create://create
                    progressMessage = context.getString(R.string.group_task_loadingMessage_create_group);
                    break;
                case Add://add
                    progressMessage = context.getString(R.string.group_task_loadingMessage_add_group_member);
                    break;
                case Remove://remove
                    progressMessage = context.getString(R.string.group_task_loadingMessage_remove_group_member);
                    break;
                case ChangeTopic:
                    progressMessage = context.getString(R.string.group_task_loadingMessage_change_group_topic);
                    break;
                case Quit:
                    progressMessage = context.getString(R.string.group_task_loadingMessage_quit_group);
                    break;
                case ExportHistory:
                    progressMessage = context.getString(R.string.group_task_loadingMessage_export_group_history);
                    break;
                case AddGroupMember:
                    progressMessage = context.getString(R.string.group_task_loadingMessage_add_member_to_contact);
                    break;
                default:
                    progressMessage = "Please wait...";
                    break;
            }
            this.dialog.setMessage(progressMessage);
            this.dialog.show();
        }


    }

    @Override
    protected String doInBackground(List<String>... param) {
        JSONObject json;
        String response, responseGroupId;
        String newGroupChatId = "";
        try{
            String result = "";
            String strIds = "";
            switch (taskType){
                case Create:
                    for(String uid:param[0]) strIds += uid + ",";
                    response = NetworkRequestsUtil.postAddGroupChatMember(userId,pushId,strIds);
                    json = new JSONObject(response);
                    if(StringUtils.isNotBlank(json.optString("success"))){
                    responseGroupId = json.optString("group");
                    newGroupChatId = updateGroupInformation(dbhelper, userId, pushId, responseGroupId);
                    List<GroupMember> members = GroupMemberModel.GetMemberByGroupId(dbhelper, responseGroupId);
                    if(members != null && members.size()>0){
                        String newMemberName = "  ";
                        for(GroupMember member:members)
                            newMemberName += member.getFullName() + " , ";
                        newMemberName = newMemberName.substring(0,newMemberName.length()-2);

                        InstantMessage groupCreateMessage = InstantMessage.newNoticeMsg(userId,
                                                                                        responseGroupId,
                                                                                        context.getString(R.string.group_self_add_member_notice) + " " + context.getString(R.string.group_created_notice) + newMemberName,
                                                                                        getFirstNoticeTime(json.optString("inputdates")));
                        InstantMessageModel.createInstantMessage(dbhelper, groupCreateMessage);
                        GroupConversationActivity.startActivity((Activity) context, responseGroupId, newGroupChatId, groupCreateMessage.getId());
                        result = "success";
                    }

            }
            break;
            case Add:
                for(String uid:param[0]) strIds += uid + ",";
                response = NetworkRequestsUtil.postAddGroupChatMember(userId, pushId, strIds, groupId);
                json = new JSONObject(response);
                if(StringUtils.isNotBlank(json.optString("success"))){
                    updateGroupInformation(dbhelper, userId, pushId, groupId);
                    List<GroupMember> members = GroupMemberModel.GetGroupMembersInGroup(dbhelper, groupId, param[0]);
                    if(members != null && members.size()>0){
                        String newMemberName = " .";
                        for(GroupMember member:members) newMemberName += member.getFullName() + ".";
                        InstantMessage noticeMsg = InstantMessage.newNoticeMsg(userId,
                                                                               groupId,
                                                                               context.getString(R.string.group_self_add_member_notice) + " " + context.getString(R.string.group_add_member_notice_pre) + newMemberName + " " + context.getString(R.string.group_add_member_notice_suf),
                                                                               getFirstNoticeTime(json.optString("inputdates")));
                        InstantMessageModel.createInstantMessage(dbhelper, noticeMsg);

                        result = noticeMsg.getId();
                    }

                }
                break;
                case Remove:
                    String groupMemberId = param[0].get(0);//this id is not user id, it is initilized locally, used here for easier deleting
                    GroupMember member = GroupMemberModel.GetGroupMemberById(dbhelper, groupMemberId);
                    if(member != null){
                        response = NetworkRequestsUtil.postRemoveGroupChatMember(userId, pushId, member.getUserId(), groupId);
                        json = new JSONObject(response);
                        if(StringUtils.isNotBlank(json.optString("success"))){
                            InstantMessage noticeMsg = InstantMessage.newNoticeMsg(userId, groupId, member.getFullName() + " " + context.getString(R.string.group_remove_member_notice), getFirstNoticeTime(json.optString("inputdates")));
                            InstantMessageModel.createInstantMessage(dbhelper, noticeMsg);
                            GroupMemberModel.deleteGroupMemberById(dbhelper, groupMemberId);
                            //((GroupConversationActivity)context).updateLastMessage(noticeMsg);
                            result = "success";
                        }
                    }
                    break;
                case Quit:
                   response = NetworkRequestsUtil.postRemoveGroupChatMember(userId, pushId, userId, groupId);
                    json = new JSONObject(response);
                    if(StringUtils.isNotBlank(json.optString("success"))){
                        GroupConversation con = ConversationModel.getGroupConversationByGroupId(dbhelper,groupId);
                        if(con != null){
                            con.setStillInGroup(false);
                            ConversationModel.updateGroupConversation(dbhelper, con);
                            ((GroupConversationActivity)context).updateGroupChat(con);
                            InstantMessage noticeMsg = InstantMessage.newNoticeMsg(userId, groupId, context.getString(R.string.group_quit_notice), getFirstNoticeTime(json.optString("inputdates")));
                            InstantMessageModel.createInstantMessage(dbhelper, noticeMsg);
                            ((GroupConversationActivity)context).updateLastMessage(noticeMsg);
                            result = noticeMsg.getId();
                        }
                    }
                    break;
                /*case NewAdd:
                    while(StringUtils.isBlank(newGroupChatId)) newGroupChatId = updateGroupInformation(dbhelper, userId, pushId, groupId);
                    result = "success";
                    break;
                case NewRemove:
                    break;*/
                case ChangeTopic:
                    response = NetworkRequestsUtil.postChangeGroupChatTitle(userId, pushId, groupId, param[0].get(0));
                    json = new JSONObject(response);
                    if(StringUtils.isNotBlank(json.optString("success")) && param.length >= 1){
                        GroupConversation groupchat = ConversationModel.getGroupConversationByGroupId(this.dbhelper, groupId);
                        if(groupchat == null)   return null;
                        String newTopic = param[0].get(0);
                        groupchat.setTopic(newTopic);
                        result = param[0].get(0);
                        GroupMember operator = GroupMemberModel.GetGroupMemberByGroupIdAndId(dbhelper, groupId, userId);
                        if(operator != null){
                            //TODO:time
                            InstantMessage titleNotice =
                                    InstantMessage.newNoticeMsg(userId,
                                            groupId,
                                            operator.getFullName() + " " + context.getString(R.string.group_change_title_notice) + " " + newTopic,
                                            NetworkRequestsUtil.getBestCurrentTime());
                            InstantMessageModel.createInstantMessage(dbhelper, titleNotice);
                            ((GroupConversationActivity)context).updateGroupChat(groupchat);
                            //((GroupConversationActivity)context).updateLastMessage(titleNotice);
                        }

                    }
                    break;
                case ExportHistory:
                    response = NetworkRequestsUtil.postExportGroupHistory(userId, pushId, groupId);
                    json = new JSONObject(response);
                    if(StringUtils.isNotBlank(json.optString("success"))){
                        result = "success";

                    }
                    break;
                case AddGroupMember:
                    String targetUserId = param[0].get(0);
                    if(StringUtils.isNotBlank(targetUserId)){
                        if(FoundSettings.getInstance(context).isPatient()){
                            if(NetworkRequestsUtil.postSubscribeNewDoctor(targetUserId, userId, pushId))
                                result = "hahaha";
                        }
                        else{
                            response = NetworkRequestsUtil.getAddRelation(userId, targetUserId);
                            if (response.contains("success")) {
                               result = "hahaha";
                            }
                        }
                        if(StringUtils.isNotBlank(result)){
                            // successfully added relation. now get the recipient info
                            String recipientjson = NetworkRequestsUtil.getEntityByIdOrPin(targetUserId);
                            if (StringUtils.isNotEmpty(recipientjson)) {
                                // successfully get info
                                // parse the info and return
                                EntityUser user = EntityUser.parseJson(recipientjson);
                                if (EntityModel.getEntityById(dbhelper, user.getId()) == null) {
                                    EntityModel.createOrUpdateEntityUser(dbhelper, user);
                                }
                                result = "success";
                            } else {
                                // get info failed.
                                result = null;
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
            return result;
        }catch (Exception e){
            Log.e(TAG,e.getMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(dialog != null){
            dialog.dismiss();
        }
        if(StringUtils.isNotBlank(s)){
            if(myHandler != null){
                Message m = new Message();
                Bundle args = new Bundle();
                if(taskType == GroupChatTaskType.Add){
                    args.putString("addNoticeId",s);
                    m.setData(args);
                }
                else if(taskType == GroupChatTaskType.ChangeTopic){
                    args.putString("title",s);
                    m.setData(args);
                }
                else if(taskType == GroupChatTaskType.Remove){
                    args.putString("delete",s);
                    m.setData(args);
                }
                else if(taskType == GroupChatTaskType.AddGroupMember){
                    args.putString("addMemberContact",s);
                    m.setData(args);
                }
                myHandler.sendMessage(m);
            }
            if(taskType == GroupChatTaskType.Quit){
                ConversationActivity.start((Activity)context);
            }
            if(taskType == GroupChatTaskType.ExportHistory){
                Toast.makeText(context, context.getString(R.string.export_success), Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Log.e(TAG, "GroupChatTask: " + this.taskType + "failed");
            Toast.makeText(context, this.taskType + " failed",Toast.LENGTH_SHORT).show();
        }
    } 
    public static String updateGroupInformation(DatabaseHelper helper, String userId, String pushId, String targetGroupId){
        int retryTimes = 5;
        String resultGroupId = null;
        while(retryTimes> 0 && StringUtils.isBlank(resultGroupId)){
            try{
                String response = NetworkRequestsUtil.getGroupChatDetails(userId, pushId ,targetGroupId);
                JSONObject json = new JSONObject(response);
                GroupConversation groupchat = ConversationModel.createGroupConversationIfNotExist(helper,json);
                GroupMemberModel.CreateOrUpdateGroupMember(helper, groupchat.getTargetGroupchatId(), json);
                resultGroupId = groupchat.getId();
            }catch (Exception e){
                Log.e(TAG, "update group information failed for groupid: " + targetGroupId + " , remaining times for retry: " + retryTimes);
            }
            retryTimes--;
        }
        return resultGroupId;
    }

    /**
     * get first time in the array
     * using for create group notice message
     * @param noticeTimeList
     * @return
     */
    public static Long getFirstNoticeTime(String noticeTimeList){
        try{
            JSONArray array = new JSONArray(noticeTimeList);
            if(array != null && array.length()>0){
                return array.optLong(0);
            }
        }catch (Exception e){
            Log.e(TAG,e.getMessage());
        }
        return NetworkRequestsUtil.getBestCurrentTime();
    }
}
