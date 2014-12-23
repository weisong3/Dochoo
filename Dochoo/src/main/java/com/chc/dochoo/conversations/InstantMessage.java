package com.chc.dochoo.conversations;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Represents an instant message view
 */
@DatabaseTable
public class InstantMessage extends FoundMessage {
    public static final int VERSION_1 = 0;
    public static final int VERSION_2 = 1; // encrypted

    public static final String COLUMN_NAME_GROUP_ID = "group_id";
    /* Beginning database fields */
	@DatabaseField
	private boolean fromPatient; // this field is determined by comparing the senderid to the target id
	// the name is no longer constrained to patient. Now it means the direction of the message
	// i.e. fromPatient == true if and only if the direction is from the mobile user to another user
	// i.e. when senderid != targetid
    //fromPatient == true    means message sent by this device
	@DatabaseField
	private InstantMessageType messageType; // "TEXT"
                                            // IMAGEMESSAGE
                                            // FILEMESSAGE
                                            // VOICEMESSAGE
                                            // GROUPNOTICE
                                            // null. If messageType is null then we should not display it in the future.
	@DatabaseField
	private String multimediaPath;
	@DatabaseField
	private boolean unread; // for incoming messages only
    @DatabaseField
    private int mVersion;

    //for group message
    @DatabaseField(columnName = COLUMN_NAME_GROUP_ID)
    private String groupId;
    //group message only
    @DatabaseField
    private String groupDisplyName;
    /* Ending database fields */

	public static enum InstantMessageType {
		TEXT("TEXT"),
		IMAGE("IMAGEMESSAGE"),
		FILE("FILEMESSAGE"),
		VOICE("VOICEMESSAGE"),
        GROUPNOTICE("GROUPNOTICE");

		private final static Map<String, InstantMessageType> map
		= new HashMap<String, InstantMessageType>();

		static {
			for (InstantMessageType t : values()) {
				map.put(t.getTypeStr(), t);
			}
		}

		private final String typeStr;

		private InstantMessageType(String str) {
			this.typeStr = str;
		}

		public String getTypeStr() {
			return typeStr;
		}

		public static InstantMessageType getType(String msgtype) {
			return map.get(msgtype);
		}

	}

    // for out going messages only
    @DatabaseField(dataType = DataType.SERIALIZABLE)
	private AtomicReference<OutgoingMessageStatus> status;

    private AtomicReference<OutgoingMessageStatus> initStatusReference() {
        return new AtomicReference<OutgoingMessageStatus>(OutgoingMessageStatus.SENT);
    }

    private Observable subject = new Observable() {

		@Override
		public void notifyObservers() {
			super.setChanged();
			super.notifyObservers();
		}

	};


	/**
	 * Should not be used unless necessary
	 * This is intended for ORMLite use only
	 */
	public InstantMessage() {
		setType(MessageType.MESSAGE);
		setMessageType(InstantMessageType.TEXT);
	}
	
	/**
	 * Creates a new text message object
	 * @param entityId
	 * @param status
	 * @param fromPatient
	 * @param content
	 * @return
	 */
	public static InstantMessage newMessage(String entityId, String groupId, OutgoingMessageStatus status, boolean fromPatient, String content, Long lastMsgDate) {
		return newMessage(InstantMessageType.TEXT, entityId, groupId, status, fromPatient, content, null, lastMsgDate);
	}
	
	public static InstantMessage newImageMsg(String entityId, String groupId, OutgoingMessageStatus status, boolean fromPatient, String content, String fileUri, Long lastMsgDate) {
		return newMessage(InstantMessageType.IMAGE, entityId, groupId, status, fromPatient, content, fileUri, lastMsgDate);
	}
	
	public static InstantMessage newVoiceMsg(
			String entityId,
            String groupId,
			OutgoingMessageStatus status,
			boolean fromPatient,
			String content,
			String fileAbsolutePath,
            Long lastMsgDate
			) {
		return newMessage(InstantMessageType.VOICE, entityId, groupId, status, fromPatient, content, fileAbsolutePath, lastMsgDate);
	}

    public static InstantMessage newNoticeMsg(
            String entityId,    //targetId
            String groupId,     //targetGroup
            String content,     //content to show in the group
            Long lastMsgDate
    ){
        InstantMessage msg = newMessage(InstantMessageType.GROUPNOTICE, entityId, groupId, OutgoingMessageStatus.READ, false, content, null, lastMsgDate);
        msg.setGroupDisplyName("GROUPNOTICE");
        return msg;
    }

	/**
	 * Create message according to its {@link InstantMessageType}
	 * @param type
	 * @param entityId
	 * @param status
	 * @param fromPatient
	 * @param content
	 * @param fileFullPath 
	 * @return
	 */
	private static InstantMessage newMessage(InstantMessageType type, String entityId, String groupId, OutgoingMessageStatus status,
			boolean fromPatient, String content, String fileFullPath, Long lastMsgTime) {
		InstantMessage msg = new InstantMessage();

        msg.setId(UUID.randomUUID().toString().replace("-",""));
		msg.setMessageType(type);
		msg.setType(MessageType.MESSAGE);
		msg.setEntityId(entityId);
		msg.setStatus(status);
		msg.setFromPatient(fromPatient);
		msg.setContent(content);
		msg.setMultimediaPath(fileFullPath);
		msg.setGroupId(groupId);
        msg.setTime(lastMsgTime);
		return msg;
	}
	
	/**
	 * 
	 * @param jo
	 * @param entityId is the target id regardless of the direction of the message
	 */
	public InstantMessage(JSONObject jo, String entityId) {
		super(jo, entityId);
		setType(MessageType.MESSAGE);
		if (jo.isNull("senderid")) {
			setFromPatient(true);
		} else {
			String senderid = jo.optString("senderid");
			setFromPatient(!entityId.equals(senderid));
		}
		String msgtype = jo.optString("messageType");
		if (StringUtils.isNotBlank(msgtype) && !msgtype.equals("null")) {
			this.messageType = InstantMessageType.getType(msgtype);
			// if messageType is not recognized then we should not display it in the future
		} else {
			this.messageType = InstantMessageType.TEXT;
		}
	}

	public synchronized OutgoingMessageStatus getStatus() {
        if (this.status == null) this.status = initStatusReference();
		return status.get();
	}

	public synchronized void setStatus(OutgoingMessageStatus status) {
        if (this.status == null) this.status = initStatusReference();
		this.status.set(status);
		
		subject.notifyObservers();
	}
	
	public void registerStatusObserver(Observer ob) {
        this.subject.deleteObservers();
		this.subject.addObserver(ob);
	}

	public boolean isFromPatient() {
		return fromPatient;
	}

	public void setFromPatient(boolean fromPatient) {
		this.fromPatient = fromPatient;
	}

	public InstantMessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(InstantMessageType messageType) {
		this.messageType = messageType;
	}

	public String getMultimediaPath() {
		return multimediaPath;
	}

	public void setMultimediaPath(String multimediaPath) {
		this.multimediaPath = multimediaPath;
	}

	public boolean isUnread() {
		return unread;
	}

	public void setUnread(boolean unread) {
		this.unread = unread;
	}

    public int getmVersion() {
        return mVersion;
    }

    public void setmVersion(int mVersion) {
        this.mVersion = mVersion;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setGroupDisplyName(String groupDisplyName) {
        this.groupDisplyName = groupDisplyName;
    }

    public String getGroupDisplyName() {
        return groupDisplyName;
    }

}
