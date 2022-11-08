package com.mainsteam.stm.caplib.dict;

/**
 * Availability Enum.
 *
 * @author sunsht
 * @author lich
 * @version 4.2.0
 * @since 4.1.0
 * @deprecated replaced by {@link com.mainsteam.stm.caplib.state.Availability}, will be removed in the future.
 */
@Deprecated
public enum AvailableStateEnum {
    // 未知
    Indeterminate(-1),
    // 可用
    Normal(1),
    // 致命
    Critical(0);

    private int stateVal;

    AvailableStateEnum(int stateVal) {
        this.stateVal = stateVal;
    }

    public int getStateVal() {
        return this.stateVal;
    }
}
