package com.mainsteam.stm.metric.exception;

import com.mainsteam.stm.exception.BaseException;

public class CustomMetricException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6627079562717391182L;

		
	public CustomMetricException(int code, String messsage) {
		super(code, messsage);
	}
}
