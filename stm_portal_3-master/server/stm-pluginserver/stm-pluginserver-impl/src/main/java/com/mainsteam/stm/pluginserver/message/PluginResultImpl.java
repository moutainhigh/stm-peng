/**
 * 
 */
package com.mainsteam.stm.pluginserver.message;

import com.mainsteam.stm.exception.BaseException;
import com.mainsteam.stm.pluginprocessor.ResultSet;

/**
 * @author ziw
 * 
 */
public class PluginResultImpl implements PluginResult {

	private ResultSet resultSet;

	private int resultCode;

	private long requestId;
	
	private BaseException cause;

	/**
	 * 
	 */
	public PluginResultImpl(ResultSet resultSet, int resultCode, long requestId) {
		this.resultSet = resultSet;
		this.resultCode = resultCode;
		this.requestId = requestId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.pluginserver.message.PluginResult#getResultData()
	 */
	@Override
	public ResultSet getResultData() {
		return resultSet;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.pluginserver.message.PluginResult#getRequestId()
	 */
	@Override
	public long getRequestId() {
		return requestId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.pluginserver.message.PluginResult#getResultCode()
	 */
	@Override
	public int getResultCode() {
		return resultCode;
	}

	/**
	 * @return the cause
	 */
	public final BaseException getCause() {
		return cause;
	}

	/**
	 * @param cause the cause to set
	 */
	public final void setCause(BaseException cause) {
		this.cause = cause;
	}
}
