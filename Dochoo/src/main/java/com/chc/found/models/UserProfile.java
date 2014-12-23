//package com.chc.found.models;
//
//import com.j256.ormlite.field.DatabaseField;
//import com.j256.ormlite.table.DatabaseTable;
//
//@DatabaseTable(tableName = "userprofile")
//public class UserProfile {
//	public static enum UserType {
//		PATIENT,
//		PROFESSIONAL;
//	}
//	
//	@DatabaseField(id = true)
//	private String id;
//	@DatabaseField
//	private String fullname;
//	@DatabaseField
//	private String username;
//	@DatabaseField
//	private UserType type;
//	
//	public UserProfile() {}
//	
//	public static UserProfile newUserProfile(String id, String username, String fullname, UserType type) {
//		UserProfile user = new UserProfile();
//		
//		user.id = id;
//		user.username = username;
//		user.fullname = fullname;
//		user.type = type;
//		
//		return user;
//	}
//
//	public String getFullname() {
//		return fullname;
//	}
//
//	public void setFullname(String fullname) {
//		this.fullname = fullname;
//	}
//
//	public String getUsername() {
//		return username;
//	}
//
//	public void setUsername(String username) {
//		this.username = username;
//	}
//
//	public UserType getType() {
//		return type;
//	}
//
//	public void setType(UserType type) {
//		this.type = type;
//	}
//	
//	
//	
//}
