package com.chc.custom;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.chcgp.hpad.util.general.CHCGeneralUtil;

public class CustomProperties {
	private static final String JSON_NAME_TAG = "name";
	private static final String JSON_VALUE_TAG = "value";
	private static volatile CustomProperties cp; 
	private static Object lock = new Object();
	
	private String clientName = new String();
	private Map<String, String> properties; 
	private boolean customized = false;
	
  private CustomProperties() {
  	properties = new HashMap<String, String>();
  	readConfigs();
  }

	private void readConfigs() {
		// read customization settings
		BufferedReader reader = null;
		try {
			clientName = CHCCustomUtil.getVideoChannelName();
			
			customized = !clientName.isEmpty();
			if (!customized)
				return;
			File file = CHCCustomUtil.getConfigPropertiesFile();
			// if no config properties file, just return
			if (!file.isFile())
				return;
			
			// read json input
			reader = new BufferedReader(new FileReader(file));
			
			StringBuilder builder = new StringBuilder();
	    for (String s = reader.readLine(); s != null; s = reader.readLine()) {
	      builder.append(s);
	    }
	    JSONArray propsArr = new JSONArray(builder.toString());
	    
	    // convert json to mapping
	    for (int i = 0, size = propsArr.length(); i < size; i++) {
	    	JSONObject jo = propsArr.optJSONObject(i);
	    	properties.put(jo.getString(JSON_NAME_TAG), jo.getString(JSON_VALUE_TAG)); 
	    }
	    
		} catch (Exception e) {
			Log.w(getClass().getName(), e);
		} finally {
			CHCGeneralUtil.closeQuietly(reader);
		}
		
	}
  
  public static CustomProperties getInstance() {
  	if (cp == null) {
  		synchronized (lock) {
  			if (cp == null)
  				cp = new CustomProperties();
  		}
  	}
  	return cp;
  }
  
  public static void checkCustomization() {
  	getInstance().readConfigs();
  }
  
  public static Map<String, String> getCustomProperties() {
  	return getInstance().properties;
  }
  
  public static boolean isCustomized() {
  	return getInstance().customized;
  }
  
}
