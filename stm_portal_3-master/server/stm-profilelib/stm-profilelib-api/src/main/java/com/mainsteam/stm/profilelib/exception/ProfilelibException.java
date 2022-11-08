package com.mainsteam.stm.profilelib.exception;

import com.mainsteam.stm.exception.BaseException;

public class ProfilelibException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2493596224874902032L;

	public ProfilelibException(int code, String msg) {
		super(code, msg);
	}

	public ProfilelibException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProfilelibException(Throwable cause) {
		super(cause);
	}
}
