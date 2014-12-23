package com.chc.found.models;

import org.json.JSONObject;

public class UniversalUserView {
	private String id;
	private String username;
	private String firstName;
	private String lastName;
	private String name; // for centers
	private String typedEntityJsonData;
	
	public UniversalUserView(JSONObject jo) {
		this.id = jo.optString("id");
		this.username = jo.optString("username");
		this.firstName = jo.optString("firstName");
		this.lastName = jo.optString("lastName");
		this.name = jo.optString("name");
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTypedEntityJsonData() {
		return typedEntityJsonData;
	}

	public void setTypedEntityJsonData(String typedEntityJsonData) {
		this.typedEntityJsonData = typedEntityJsonData;
	}
	
	
}
