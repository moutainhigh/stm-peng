package com.mainsteam.stm.caplib.dict;

import java.io.Serializable;

public class CaplibAPIResult implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7356957582741795187L;

	private boolean isSucessful;

	public static final CaplibAPIResult SUCESSFUL = new CaplibAPIResult(true,CaplibAPIErrorCode.OK);
	
	private CaplibAPIErrorCode errorCode;

	public CaplibAPIResult(boolean isSucessful, CaplibAPIErrorCode errorCode) {
		super();
		this.isSucessful = isSucessful;
		this.errorCode = errorCode;
	}

	public boolean isSucessful() {
		return isSucessful;
	}

	public CaplibAPIErrorCode getErrorCode() {
		return errorCode;
	}

}
