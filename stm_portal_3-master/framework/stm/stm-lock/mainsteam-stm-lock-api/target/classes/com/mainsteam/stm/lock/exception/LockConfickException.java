package com.mainsteam.stm.lock.exception;

/**
 * 如果同一个key，已经被greed类型请求锁定，然后再去使用非greed去请求该锁，则抛出该锁。 <br>
 * 反之，如果被一个非greed类型请求锁定，然后再去使用greed请求，则不会抛出异常。<br>
 * <br>
 * 
 * @author 作者：ziw
 * @date 创建时间：2017年2月7日 下午4:20:18
 * @version 1.0
 */
public class LockConfickException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 575451800814803864L;

	public LockConfickException() {
	}

	public LockConfickException(String message) {
		super(message);
	}

	public LockConfickException(Throwable cause) {
		super(cause);
	}

	public LockConfickException(String message, Throwable cause) {
		super(message, cause);
	}

	public LockConfickException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
