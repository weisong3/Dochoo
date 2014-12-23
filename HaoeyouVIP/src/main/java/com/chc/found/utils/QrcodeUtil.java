package com.chc.found.utils;

import org.apache.commons.lang.StringUtils;

public class QrcodeUtil {
	
	/**
	 * Returns the parsed id or pin of a given scan string
	 * @param input
	 * @return id or pin string, or empty string if input is empty or null
	 */
	public static final String getIdOrPin(String input) {
		if (StringUtils.isBlank(input)) return "";
		
		return input.substring(input.lastIndexOf('/') + 1);
	}
}
