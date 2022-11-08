/**
 * 
 */
package com.mainsteam.stm.pluginserver.excepton;

import com.mainsteam.stm.exception.BaseException;

/**
 * @author ziw
 *
 */
public class PluginServerExecuteException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7659635052690714176L;

	/**
	 * @param code
	 * @param msg
	 */
	public PluginServerExecuteException(int code, String msg) {
		super(code, msg);
	}

	/**
	 * @param cause
	 */
	public PluginServerExecuteException(BaseException cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public PluginServerExecuteException(String message, BaseException cause) {
		super(message, cause);
	}

	/**
	 * @param code
	 * @param cause
	 */
	public PluginServerExecuteException(int code, Throwable cause) {
		super(code, cause);
	}

	/**
	 * @param code
	 * @param message
	 * @param cause
	 */
	public PluginServerExecuteException(int code, String message,
			Throwable cause) {
		super(code, message, cause);
	}

	/**
	 * @param cause
	 */
	public PluginServerExecuteException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public PluginServerExecuteException(String message, Throwable cause) {
		super(message, cause);
	}
}
