package com.mainsteam.stm.state.ext.aop.interceptor;

import com.mainsteam.stm.caplib.dict.CapacityConst;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.metric.obj.CustomMetric;
import com.mainsteam.stm.state.ext.aop.ValidateInterceptor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by Xiaopf on 2017/7/12.
 */
@Component("linkComputeInterceptor")
public class LinkComputeInterceptor implements ValidateInterceptor {

    public static final int PRIORITY = 4;//拦截器执行优先级

    @Override
    public boolean validate(MetricCalculateData metricCalculateData, ResourceMetricDef resourceMetricDef, CustomMetric customMetric, Map<String, Object> map) {
        ResourceInstance resourceInstance = (ResourceInstance) map.get("resourceInstance");
        if (CapacityConst.LINK.equals(resourceInstance.getCategoryId())) {
            map.put("isLinkCompute", Boolean.TRUE);
        }
        return false;
    }

    @Override
    public int getOrder() {
        return PRIORITY;
    }
}
