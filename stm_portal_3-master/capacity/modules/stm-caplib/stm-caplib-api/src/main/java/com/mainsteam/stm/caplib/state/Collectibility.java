package com.mainsteam.stm.caplib.state;

/**
 * Availability Enum.
 *
 * @author lich
 * @version 4.2.0
 * @since 4.2.0
 */
public enum Collectibility {
    /**
     * Collectible
     */
    COLLECTIBLE,
    /**
     * Uncollectible
     */
    UNCOLLECTIBLE;

    public static final int BIT_MASK = 0xf0;
    public static final int COLLECTIBLE_CODE = 0x00;
    public static final int UNCOLLECTIBLE_CODE = 0x10;
    private int code;

    static {
        COLLECTIBLE.code = COLLECTIBLE_CODE;
        UNCOLLECTIBLE.code = UNCOLLECTIBLE_CODE;
    }

    public static Collectibility valueOf(int state) {
        state = state & BIT_MASK;
        switch (state) {
            case UNCOLLECTIBLE_CODE:
                return UNCOLLECTIBLE;
            default:
                return COLLECTIBLE;
        }
    }

    public int getCode() {
        return code;
    }
}
