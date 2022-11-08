package com.mainsteam.stm.state.ext.tools;

import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;

/**
 * Created by Xiaopf on 2017/7/15.
 */
@Deprecated
public class InstStateComputeUtil {

    @Deprecated
    public static InstanceStateEnum convertMetricStateToInstanceState(MetricStateEnum metricState) {
        switch (metricState) {
            case CRITICAL:
                return InstanceStateEnum.CRITICAL;
            case SERIOUS:
                return InstanceStateEnum.SERIOUS;
            case WARN:
                return InstanceStateEnum.WARN;
            case NORMAL:
                return InstanceStateEnum.NORMAL;
            case UNKOWN:
                return InstanceStateEnum.NORMAL;
            default:
                throw new RuntimeException("can't convert null to instanceState!");
        }
    }

}
