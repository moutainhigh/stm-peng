/**
 * 
 */
package com.mainsteam.stm.pluginsession.exception;

import com.mainsteam.stm.exception.BaseException;

/**
 * @author ziw
 * 
 */
public class PluginSessionRunException extends BaseException {

	private static final long serialVersionUID = 7327713255884979028L;

	public PluginSessionRunException(int code, String msg) {
		super(code, msg);
	}

	/**
	 * @param cause
	 */
	public PluginSessionRunException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public PluginSessionRunException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public PluginSessionRunException(BaseException cause) {
		super(cause);
	}

	/**
	 * @param code
	 * @param message
	 * @param cause
	 */
	public PluginSessionRunException(int code, String message, Throwable cause) {
		super(code, message, cause);
	}

	/**
	 * @param code
	 * @param cause
	 */
	public PluginSessionRunException(int code, Throwable cause) {
		super(code, cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public PluginSessionRunException(String message, BaseException cause) {
		super(message, cause);
	}
}
