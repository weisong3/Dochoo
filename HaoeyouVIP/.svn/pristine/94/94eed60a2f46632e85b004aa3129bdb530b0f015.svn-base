package com.chc.dochoo.conversations;

import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class NewsMessage extends FoundMessage implements Comparable<NewsMessage> {
	
	@DatabaseField
	private String title;

	public NewsMessage() {
		setType(MessageType.NEWS);
	}
	
	/**
	 * Constructs a news message instance {@literal that is a subclass of} {@link FoundMessage}
	 * @param jo json object of the news message
	 * @throws IllegalArgumentException if the type is not one of the predefined ones
	 * <pre>
	 * {@code
	 * 
	 * Predefined types:
	 * 
	 * type == 0 for MessageType.MESSAGE;
	 * OR
	 * type == 1 for MessageType.NEWS; 
	 * 
	 * }
	 * </pre>
	 */
	public NewsMessage(JSONObject jo, String entityId) {
		super(jo, entityId);
		setType(MessageType.NEWS);
		setTitle(jo.optString("title"));
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public int compareTo(NewsMessage another) {
		return ((Long) this.getTime()).compareTo(another.getTime());
	}

}
