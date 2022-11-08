/**
 * 
 */
package com.mainsteam.stm.pluginserver.adapter.exception;

import com.mainsteam.stm.exception.BaseException;

/**
 * @author ziw
 *
 */
public class PluginServerAdapterException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 675836213076064642L;

	/**
	 * @param cause
	 */
	public PluginServerAdapterException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param code
	 * @param msg
	 */
	public PluginServerAdapterException(int code, String msg) {
		super(code, msg);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public PluginServerAdapterException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public PluginServerAdapterException(BaseException cause) {
		super(cause);
	}

	/**
	 * @param code
	 * @param message
	 * @param cause
	 */
	public PluginServerAdapterException(int code, String message,
			Throwable cause) {
		super(code, message, cause);
	}

	/**
	 * @param code
	 * @param cause
	 */
	public PluginServerAdapterException(int code, Throwable cause) {
		super(code, cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public PluginServerAdapterException(String message, BaseException cause) {
		super(message, cause);
	}
	
	
}
