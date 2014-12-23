package com.chc.exceptions;


public class InternalErrorException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 11111111111111L;
	private static final String TAG = "InternalErrorException";
	private int statusCode;

	public InternalErrorException(int statusCode) {
		this.statusCode = statusCode;
	}

	public int getErrorCode() {
		return statusCode;
	}

	@Override
	public String getMessage() {
		return "Status Code: " + statusCode + '\n' + super.getMessage();
	}
	
	
}
