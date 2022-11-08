/**
 * 
 */
package com.mainsteam.stm.route;

import com.mainsteam.stm.exception.BaseRuntimeException;

/**
 * @author ziw
 *
 */
public class RouteConnectionException extends BaseRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5244538738319109160L;

	public static final int CONNECTON_VERSION_VALID = 2000;
	
	/**
	 * @param arg0
	 */
	public RouteConnectionException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public RouteConnectionException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public RouteConnectionException(int arg0, String arg1) {
		super(arg0, arg1);
	}
}
