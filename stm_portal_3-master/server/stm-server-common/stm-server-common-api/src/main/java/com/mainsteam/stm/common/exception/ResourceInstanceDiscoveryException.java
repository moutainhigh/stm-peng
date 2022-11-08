/**
 * 
 */
package com.mainsteam.stm.common.exception;

import com.mainsteam.stm.exception.BaseException;

/**
 * @author ziw
 *
 */
public class ResourceInstanceDiscoveryException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3001213082836950590L;

	/**
	 * @param cause
	 */
	public ResourceInstanceDiscoveryException(BaseException cause) {
		super(cause);
	}

	/**
	 * @param code
	 * @param message
	 * @param cause
	 */
	public ResourceInstanceDiscoveryException(int code, String message,
			Throwable cause) {
		super(code, message, cause);
	}

	/**
	 * @param code
	 * @param msg
	 */
	public ResourceInstanceDiscoveryException(int code, String msg) {
		super(code, msg);
	}

	/**
	 * @param code
	 * @param cause
	 */
	public ResourceInstanceDiscoveryException(int code, Throwable cause) {
		super(code, cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ResourceInstanceDiscoveryException(String message,
			BaseException cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ResourceInstanceDiscoveryException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public ResourceInstanceDiscoveryException(Throwable cause) {
		super(cause);
	}
}
