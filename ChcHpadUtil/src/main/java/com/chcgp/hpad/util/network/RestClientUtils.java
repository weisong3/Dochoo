package com.chcgp.hpad.util.network;

public class RestClientUtils {
	
	private RestClientUtils() {}
	
	public static String encodePassword(String password) {
		return ShaPasswordEncoder.encodePassword(password, "heartcenters");
	}
}
	
