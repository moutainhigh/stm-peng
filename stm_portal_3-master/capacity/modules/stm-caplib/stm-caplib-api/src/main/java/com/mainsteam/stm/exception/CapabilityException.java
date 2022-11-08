package com.mainsteam.stm.exception;

/**
 * Capacity base exception.
 *
 * @author lich
 * @version 4.2.0
 * @since 4.1.0
 * @deprecated replaced by {@link com.mainsteam.stm.exception.CapacityException}, will be removed in the future.
 */
@Deprecated
public class CapabilityException extends BaseException {

    private static final long serialVersionUID = -7935553206573226466L;

    public CapabilityException(Throwable cause) {
        super(cause);
    }

    public CapabilityException(String message, Throwable cause) {
        super(message, cause);
    }

    public CapabilityException(int code, String message) {
        super(code, message);
    }

    public CapabilityException(int code, String message, Throwable cause) {
        super(message, cause);
        super.code = code;
    }

}
