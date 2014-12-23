package com.chc.tipapi;

import java.util.HashMap;

import com.chc.tipapi.TipTableAndUri.TipsTableAndUri;

public class Tip {
	private int index;
	private String tipId;
	private String type;
	private String category;
	private String gender;
	private String title;
	private String key;
	private String image;
	private String content;
	
	public Tip() {
	}
	
	public Tip(HashMap<TipsTableAndUri.ColumnNames, String> tipData) {
		this.index = Integer.parseInt(tipData.get(TipsTableAndUri.ColumnNames._ID));
		this.tipId = tipData.get(TipsTableAndUri.ColumnNames.TIP_ID);
		this.type = tipData.get(TipsTableAndUri.ColumnNames.TYPE);
		this.category = tipData.get(TipsTableAndUri.ColumnNames.CATEGORY);
		this.gender = tipData.get(TipsTableAndUri.ColumnNames.GENDER);
		this.title = tipData.get(TipsTableAndUri.ColumnNames.TITLE);
		this.key = tipData.get(TipsTableAndUri.ColumnNames.KEY);
		this.image = tipData.get(TipsTableAndUri.ColumnNames.IMAGE);
		this.content = tipData.get(TipsTableAndUri.ColumnNames.CONTENT);
	}
	
	public HashMap<String, String> getTipData() {
		HashMap<String, String> tipData = new HashMap<String, String>();
		tipData.put(TipsTableAndUri.getColumnString(TipsTableAndUri.ColumnNames._ID), Integer.toString(index));
		tipData.put(TipsTableAndUri.getColumnString(TipsTableAndUri.ColumnNames.TIP_ID), tipId);
		tipData.put(TipsTableAndUri.getColumnString(TipsTableAndUri.ColumnNames.TYPE), type);
		tipData.put(TipsTableAndUri.getColumnString(TipsTableAndUri.ColumnNames.CATEGORY), category);
		tipData.put(TipsTableAndUri.getColumnString(TipsTableAndUri.ColumnNames.GENDER), gender);
		tipData.put(TipsTableAndUri.getColumnString(TipsTableAndUri.ColumnNames.TITLE), title);
		tipData.put(TipsTableAndUri.getColumnString(TipsTableAndUri.ColumnNames.KEY), key);
		tipData.put(TipsTableAndUri.getColumnString(TipsTableAndUri.ColumnNames.IMAGE), image);
		tipData.put(TipsTableAndUri.getColumnString(TipsTableAndUri.ColumnNames.CONTENT), content);
		return tipData;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getTipId() {
		return tipId;
	}

	public void setTipId(String tipId) {
		this.tipId = tipId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
