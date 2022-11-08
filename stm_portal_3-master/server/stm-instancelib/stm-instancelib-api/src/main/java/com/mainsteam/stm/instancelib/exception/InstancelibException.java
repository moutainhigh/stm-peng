package com.mainsteam.stm.instancelib.exception;

import com.mainsteam.stm.exception.BaseException;

public class InstancelibException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6627079562717391182L;

	/**
	 * 验证数据异常
	 */
	public static final int CODE_ERROR_VALIDATE=700;
	/**
	 * 资源实例重复
	 */
	public static final int CODE_ERROR_RESOURCE_REPEAT=701;
	
	//资源实例Id
	private long resourceInstanceId;
	
	public InstancelibException(int code, String messsage) {
		super(code, messsage);
	}

	public InstancelibException(int code, String messsage,long instanceId) {
		super(code, messsage);
		this.resourceInstanceId = instanceId;
	}

	public long getResourceInstanceId() {
		return resourceInstanceId;
	}

	public void setResourceInstanceId(long resourceInstanceId) {
		this.resourceInstanceId = resourceInstanceId;
	}

}
