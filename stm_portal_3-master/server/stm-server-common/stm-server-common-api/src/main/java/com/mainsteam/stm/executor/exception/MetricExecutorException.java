/**
 * 
 */
package com.mainsteam.stm.executor.exception;

import com.mainsteam.stm.exception.BaseException;

/**
 * @author ziw
 * 
 */
public class MetricExecutorException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6383669781381869702L;

	/**
	 * @param cause
	 */
	public MetricExecutorException(BaseException cause) {
		super(cause);
	}

	/**
	 * @param code
	 * @param message
	 * @param cause
	 */
	public MetricExecutorException(int code, String message, Throwable cause) {
		super(code, message, cause);
	}

	/**
	 * @param code
	 * @param msg
	 */
	public MetricExecutorException(int code, String msg) {
		super(code, msg);
	}

	/**
	 * @param code
	 * @param cause
	 */
	public MetricExecutorException(int code, Throwable cause) {
		super(code, cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MetricExecutorException(String message, BaseException cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MetricExecutorException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public MetricExecutorException(Throwable cause) {
		super(cause);
	}
}
