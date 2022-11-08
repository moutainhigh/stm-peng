/**
 * 
 */
package com.mainsteam.stm.node.exception;

import com.mainsteam.stm.exception.BaseException;

/**
 * @author ziw
 *
 */
public class NodeException extends BaseException {
	
	public static final int ERROR_CODE_IP_PORT_CONFLICT = 1000;
	public static final int ERROR_CODE_NODEFUN_NOT_MATCH_GROUP = 1001;
	public static final int ERROR_CODE_NODEGROUP_NOT_EXIST = 1002;

	/**
	 * 
	 */
	private static final long serialVersionUID = -5428605775654671176L;

	/**
	 * @param arg0
	 */
	public NodeException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public NodeException(int arg0, String arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public NodeException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}
