package com.mainsteam.stm.state.ext.process.impl;

import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.metric.obj.CustomMetric;
import com.mainsteam.stm.state.ext.StateComputeContext;
import com.mainsteam.stm.state.ext.StateProcessorEnum;
import com.mainsteam.stm.state.ext.process.StateProcessor;
import org.springframework.stereotype.Component;

/**
 * Created by Xiaopf on 2017/7/20.
 */
@Component("firstStepProcessor")
public class FirstStepProcessor implements StateProcessor {

    private static final String IS_LINK_COMPUTE = "isLinkCompute";//是否链路计算标志

    @Override
    public Object process(StateComputeContext context) {
        Object isLink = context.getAdditions().get(IS_LINK_COMPUTE);
        ResourceMetricDef metricDef = context.getMetricDef();
        CustomMetric customMetric = context.getCustomMetric();
        MetricTypeEnum metricTypeEnum = (null != metricDef) ? metricDef.getMetricType() : (customMetric.getCustomMetricInfo().getStyle());
        context.getAdditions().put("metricType", metricTypeEnum);
        if(null != isLink)
            return StateProcessorEnum.LINK_PROCESSOR;
        else {
            switch (metricTypeEnum){
                case AvailabilityMetric:
                    return StateProcessorEnum.AVAIL_METRIC_PROCESSOR;
                case PerformanceMetric:
                    return StateProcessorEnum.PERF_METRIC_PROCESSOR;
                case InformationMetric:
                    return StateProcessorEnum.INFO_METRIC_PROCESSOR;
            }
        }
        return null;
    }

    @Override
    public StateProcessorEnum processOrder() {
        return StateProcessorEnum.FIRST_STEP_PROCESSOR;
    }

    @Override
    public String toString() {
        return "FirstStepProcessor{"+processOrder()+"}";
    }
}
