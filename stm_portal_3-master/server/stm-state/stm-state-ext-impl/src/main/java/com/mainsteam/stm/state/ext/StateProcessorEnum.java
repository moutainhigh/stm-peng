package com.mainsteam.stm.state.ext;

/**
 * Created by Xiaopf on 2017/7/19.
 */
public enum StateProcessorEnum {
    FIRST_STEP_PROCESSOR(0),
    LINK_PROCESSOR(1),
    AVAIL_METRIC_PROCESSOR(2),
    PERF_METRIC_PROCESSOR(3),
    INFO_METRIC_PROCESSOR(4),
    METRIC_FLAPPING_PROCESSOR(5),
    INST_STATE_PROCESSOR(6),
    ALARM_STATE_PROCESSOR(7),
    ALARM_EVNET_PROCESSOR(8);

    private int order ;

    StateProcessorEnum(int i) {
        this.order = i;
    }

    public int getOrder() {
        return order;
    }
}
