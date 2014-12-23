package com.chc.dochoo.conversations;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.SparseArray;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

public abstract class FoundMessage {

    public final static short MARKETDOCTORMESSAGE_TYPE_NEWS = 0;
    public final static short MARKETDOCTORMESSAGE_TYPE_MESSAGE = 1;
    public final static short MARKETDOCTORMESSAGE_TYPE_STATUS_UPDATE = 2;
    public final static short CONTACT_STATUS_UPDATED = 3;
    public final static short USER_REAUTHENTICATION_NOTICE = 4;
    public final static short GROUP_STATUS_UPDATE = 5;

    public final static short CONTACT_UPDATE_ADD = 1;
    public final static short CONTACT_UPDATE_DELETED = -1;
    public final static short GROUP_TITLE_CHANGED = 0;

    public static final String COLUMN_NAME_ID = "id";
	public static final String COLUMN_NAME_TIME = "time";
    public static final String COLUMN_NAME_ENTITY_ID = "entity_id";

    public static final int MESSAGE_STATUS_UPDATE_DELIVERED = -1;
    public static final int MESSAGE_STATUS_UPDATE_READ = 1;
    public static final int MESSAGE_STATUS_UPDATE_UNREAD = 0;
    public static final int MESSAGE_STATUS_UPDATE_SENT = 2;
    public static final int MESSAGE_STATUS_UPDATE_SENDING = 2;
    public static final int MESSAGE_STATUS_UPDATE_FAILED = 2;
	
	@DatabaseField(id = true)
	private String id;
	@DatabaseField
	private String content;
	@DatabaseField(dataType = DataType.SERIALIZABLE)
	private String[] imagelink;
	@DatabaseField
	private long time;
	@DatabaseField
	private MessageType type;
	@DatabaseField(canBeNull = false, columnName = COLUMN_NAME_ENTITY_ID)
	private String entityId; // sender id
	
	
	// package-private
	static enum MessageType {
		NEWS(MARKETDOCTORMESSAGE_TYPE_NEWS, NewsMessage.class),
		MESSAGE(MARKETDOCTORMESSAGE_TYPE_MESSAGE, InstantMessage.class),
        MESSAGE_STATUS_UPDATE(MARKETDOCTORMESSAGE_TYPE_STATUS_UPDATE, StatusUpdateMessage.class),
        SERVER_CONTACT_STATUS_UPDATED(CONTACT_STATUS_UPDATED, ContactStatusUpdate.class),
        SERVER_USER_REAUTHENTICATION_NOTICE(USER_REAUTHENTICATION_NOTICE,AuthenticationStatusUpdate.class),
        GROUP_NOTICE(GROUP_STATUS_UPDATE,GroupStatusUpdate.class);

        private MessageType(int intConstant, Class<?> cls) {
			this.intConstant = intConstant;
			this.cls = cls;
		}
		
		public static MessageType getType(int intConstant) {
			MessageType res = intConstantMap.get(intConstant);
			if (res == null) throw new IllegalArgumentException("No such constant");
			return res;
		}

		public int getIntConstant() {
			return intConstant;
		}
		
		public Class<?> getCls() {
			return cls;
		}
		
		private static final SparseArray<MessageType>
		  intConstantMap = new SparseArray<MessageType>();
		
		static {
			for (MessageType t : MessageType.values()) {
				intConstantMap.put(t.getIntConstant(), t);
			}
		}
		
		
		private final int intConstant;
		private final Class<?> cls;
	}
	
	FoundMessage() {}
	
	FoundMessage(JSONObject jo, String entityId) {
	  this.entityId = entityId;
		this.id = jo.optString("id");
		this.content = jo.optString("content");
		JSONArray ja = jo.optJSONArray("imagelink");
		if (ja != null) {
			this.imagelink = new String[ja.length()];
			for (int i = 0; i < ja.length(); i++) {
				this.imagelink[i] = ja.optString(i);
			}
		}
		this.time = jo.optLong("date");
	}

	public static final FoundMessage newInstance(JSONObject jo, String entityId) {
		int intConstant = jo.optInt("type", -1);
		switch (MessageType.getType(intConstant)) {
		case NEWS:
			return new NewsMessage(jo, entityId);
		case MESSAGE:
			return new InstantMessage(jo, entityId);
		}
		
		throw new IllegalArgumentException("No such constant");
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String[] getImagelink() {
		return imagelink;
	}

	public void setImagelink(String[] imagelink) {
		this.imagelink = imagelink;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}


  public String getEntityId() {
    return entityId;
  }


  public void setEntityId(String entityId) {
    this.entityId = entityId;
  }


	
}
