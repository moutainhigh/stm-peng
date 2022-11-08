/**
 * 
 */
package com.mainsteam.stm.discovery.exception;

import com.mainsteam.stm.exception.BaseException;

/**
 * @author ziw
 *
 */
public class InstanceDiscoveryException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1756703238311590280L;
	
	
	/**
	 * @param cause
	 */
	public InstanceDiscoveryException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param code
	 * @param msg
	 */
	public InstanceDiscoveryException(int code, String msg) {
		super(code, msg);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InstanceDiscoveryException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public InstanceDiscoveryException(BaseException cause) {
		super(cause);
	}

	/**
	 * @param code
	 * @param message
	 * @param cause
	 */
	public InstanceDiscoveryException(int code, String message, Throwable cause) {
		super(code, message, cause);
	}

	/**
	 * @param code
	 * @param cause
	 */
	public InstanceDiscoveryException(int code, Throwable cause) {
		super(code, cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InstanceDiscoveryException(String message, BaseException cause) {
		super(message, cause);
	}
}
