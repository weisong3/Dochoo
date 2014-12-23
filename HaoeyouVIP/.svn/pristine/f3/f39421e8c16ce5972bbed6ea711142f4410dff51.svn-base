package com.chc.dochoo.conversations;

import android.os.Bundle;
import android.util.Log;

import com.chc.dochoo.conversations.InstantMessage.InstantMessageType;
import com.chc.exceptions.IllegalBundleException;
import com.chc.found.config.Apis;

import org.apache.commons.lang.StringUtils;

import java.security.acl.Group;

/**
 * Handles push messages from cloud
 * Always receiving but not sending
 * @author HenryW
 *
 */
public class FoundModel implements IFoundModel {
    public static final String KEY_VERSION = "version";

	@Override
	public FoundMessage parseBundle(Bundle bundle) throws IllegalBundleException {
		FoundMessage output = null;
		try {
			int type = Integer.parseInt(bundle.getString("type"));
			String content = bundle.getString("content");
//			try {
//				content = URLDecoder.decode(content, "UTF-8");
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//			}
			String[] imagelink = bundle.getStringArray("imagelink");
			if (imagelink == null) imagelink = new String[0];
			String id = bundle.getString("id");
			String senderId = bundle.getString("senderid");
            int version = getVersion(bundle);
            if (Apis.DEBUG) Log.w("GCM", type + "," + content + "," + id);

			long date = System.currentTimeMillis();
            //PUSH MESSAGE FOR GROUP, which has no id or senderId
            if(type <= FoundMessage.USER_REAUTHENTICATION_NOTICE){
                if (StringUtils.isEmpty(id) || StringUtils.isEmpty(senderId))
                    throw new IllegalBundleException("Empty id or senderid.");
                if(!StringUtils.equals(id,"foo") && !StringUtils.equals(senderId,"foo")){
                    date = Long.parseLong(bundle.getString("date"));
                }
            }

			switch (FoundMessage.MessageType.getType(type)) {
                case NEWS:
                    String title = bundle.getString("title");
                    output = new NewsMessage();
                    ((NewsMessage) output).setTitle(title);
                    break;
                case MESSAGE:
                    String groupId = bundle.getString("groupId");
                    String imtype = bundle.getString("messageType");

                    output = new InstantMessage();
                    groupId = StringUtils.isBlank(groupId)?null:groupId;
                    ((InstantMessage) output).setGroupId(groupId);
                    ((InstantMessage) output).setGroupDisplyName(bundle.getString("sendername"));
                    if (imtype != null) {
                        ((InstantMessage) output).setMessageType(InstantMessageType.getType(imtype));
                        ((InstantMessage) output).setUnread(true);
                    }
                    ((InstantMessage) output).setmVersion(version);
                    break;
                case MESSAGE_STATUS_UPDATE:
                    //FIXME:GROUP STATUS IN THE FUTURE
                    if(StringUtils.equalsIgnoreCase("GROUPMESSAGE",bundle.getString("receiverid"))) return null;
                    output = new StatusUpdateMessage();
                    if (bundle.containsKey("status")) {
                        String statusStr = bundle.getString("status");
                        int status = Integer.parseInt(statusStr);
                        ((StatusUpdateMessage) output).setStatus(status);
                        if (Apis.DEBUG) Log.w("GCM", String.valueOf(status));
                    } else {
                        // error.
                        throw new IllegalBundleException();
                    }
                    break;
                case SERVER_CONTACT_STATUS_UPDATED:
                    String updateType = bundle.getString("cus");
                    output = new ContactStatusUpdate(Short.parseShort(updateType));
                    break;
                case SERVER_USER_REAUTHENTICATION_NOTICE:
                    output = new AuthenticationStatusUpdate();
                    break;
                case GROUP_NOTICE:
                    try{
                        groupId = bundle.getString("groupId");
                        String groupUpdateType = bundle.getString("update");
                        Short groupNoticeType = Short.parseShort(groupUpdateType);
                        String targetMemberId = bundle.getString("groupMembers","");
                        String newTitle = bundle.getString("title","");
                        String timeList = bundle.getString("date","");
                        String operatorId = bundle.getString("userid","");
                        output = new GroupStatusUpdate(operatorId, groupId, groupNoticeType, targetMemberId, newTitle, timeList);
                    }catch (Exception innerE){
                        throw new IllegalBundleException();
                    }



                    break;
			}

			output.setContent(content);
			output.setImagelink(imagelink);
			output.setTime(date);
			output.setId(id);
			output.setEntityId(senderId);
		} catch (Exception e) {
			throw new IllegalBundleException(bundle.toString());
		}
		return output;
	}

    /**
     * Reads the version number from bundle
     * @param bundle
     * @return
     * @throws java.lang.NumberFormatException if version cannot be parsed as int
     */
    private int getVersion(Bundle bundle) {
        if (bundle.containsKey(KEY_VERSION)) {
            return Integer.parseInt(bundle.getString(KEY_VERSION));
        }
        return InstantMessage.VERSION_1;
    }

}
