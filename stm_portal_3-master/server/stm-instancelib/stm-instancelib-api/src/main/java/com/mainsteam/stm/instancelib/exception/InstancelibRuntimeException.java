package com.mainsteam.stm.instancelib.exception;

import com.mainsteam.stm.exception.BaseRuntimeException;

public class InstancelibRuntimeException extends BaseRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2152043126956113247L;

	/**
	 * 缓存操作异常
	 */
	public static final int CODE_ERROR_CACHE=701;
	/**
	 * 验证数据异常
	 */
	public static final int CODE_ERROR_VALIDATE=700;
	
	public InstancelibRuntimeException(int arg0, String arg1) {
		super(arg0, arg1);
	}

}
