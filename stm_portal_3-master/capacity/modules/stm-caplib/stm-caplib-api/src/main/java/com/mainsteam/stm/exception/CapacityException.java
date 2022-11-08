package com.mainsteam.stm.exception;

/**
 * Capacity base exception.
 *
 * @author lich
 * @version 4.2.0
 * @since 4.2.0
 */
public class CapacityException extends BaseException {
    public CapacityException(Throwable cause) {
        super(cause);
    }

    public CapacityException(String message, Throwable cause) {
        super(message, cause);
    }

    public CapacityException(int code, Throwable cause) {
        super(code, cause);
    }

    public CapacityException(int code, String msg) {
        super(code, msg);
    }

    public CapacityException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
