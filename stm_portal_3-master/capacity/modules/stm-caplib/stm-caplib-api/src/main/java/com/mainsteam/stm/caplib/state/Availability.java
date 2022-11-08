package com.mainsteam.stm.caplib.state;

/**
 * Availability Enum.
 *
 * @author lich
 * @version 4.2.0
 * @since 4.2.0
 */
public enum Availability {
    /**
     * Available
     */
    AVAILABLE,
    /**
     * Unavailable
     */
    UNAVAILABLE,
    /**
     * Unknown
     */
    UNKNOWN;

    public static final int BIT_MASK = 0x0f;
    public static final int AVAILABLE_CODE = 0x01;
    public static final int UNAVAILABLE_CODE = 0x00;
    public static final int UNKNOWN_CODE = 0x0f;
    private int code;

    static {
        AVAILABLE.code = AVAILABLE_CODE;
        UNAVAILABLE.code = UNAVAILABLE_CODE;
        UNKNOWN.code = UNAVAILABLE_CODE;
    }

    public static Availability valueOf(int state) {
        state = state & BIT_MASK;
        switch (state) {
            case AVAILABLE_CODE:
                return AVAILABLE;
            case UNAVAILABLE_CODE:
                return UNAVAILABLE;
            default:
                return UNKNOWN;
        }
    }

    public int getCode() {
        return code;
    }
}
