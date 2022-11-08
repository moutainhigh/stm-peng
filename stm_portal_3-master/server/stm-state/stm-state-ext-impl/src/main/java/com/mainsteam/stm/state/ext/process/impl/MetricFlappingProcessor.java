package com.mainsteam.stm.state.ext.process.impl;

import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.metric.obj.CustomMetric;
import com.mainsteam.stm.state.ext.FlappingEnum;
import com.mainsteam.stm.state.ext.StateComputeContext;
import com.mainsteam.stm.state.ext.StateProcessorEnum;
import com.mainsteam.stm.state.ext.process.StateProcessor;
import com.mainsteam.stm.state.ext.tools.MetricFlappingUtil;
import com.mainsteam.stm.state.obj.MetricStateData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Xiaopf on 2017/7/13.
 * 指标Flapping计算
 */
@Component("metricFlappingProcessor")
public class MetricFlappingProcessor implements StateProcessor {

    private static final Log logger = LogFactory.getLog(MetricFlappingProcessor.class);
    @Autowired
    private MetricFlappingUtil metricFlappingUtil;

    @Override
    public Object process(StateComputeContext context) {

        MetricStateData preMetricStateObj = (MetricStateData) context.getAdditions().get("preMetricStateData");
        MetricCalculateData metricData = context.getMetricData();

        CustomMetric customMetric = context.getCustomMetric();
        MetricStateEnum currentState = (MetricStateEnum) context.getAdditions().get("metricState");

        FlappingEnum matchFlapping ;
        int flappingValue = customMetric == null ? Integer.valueOf(context.getAdditions().get("flappingCount").toString()).intValue():
                customMetric.getCustomMetricInfo().getFlapping() ;
        try{
            matchFlapping = metricFlappingUtil.flapping(metricData.getResourceInstanceId(),
                    metricData.getMetricId(), currentState,
                    preMetricStateObj == null ? null : preMetricStateObj.getState(), flappingValue);
        }catch (Exception e) {
            if(logger.isErrorEnabled()) {
                logger.error("compute metric flapping error:" + e.getMessage(), e);
            }
            return null;
        }
        //flapping满足条件或者首次计算指标状态
        if(matchFlapping == FlappingEnum.MATCHED || matchFlapping == FlappingEnum.FIRST_MATCH) {
            if(logger.isInfoEnabled()) {
                StringBuilder message = new StringBuilder(400);
                message.append("metric matches flapping(data:");
                message.append(metricData.getResourceInstanceId());
                message.append("/");
                message.append(metricData.getMetricId());
                message.append("/threshold flapping:");
                message.append(flappingValue);
                message.append(").Metric state(");
                message.append(preMetricStateObj == null ? null : preMetricStateObj.getState());
                message.append(") turns to ");
                message.append(currentState);
                logger.info(message);
            }
            if(MetricTypeEnum.AvailabilityMetric == context.getAdditions().get("metricType")) {
                if(!(currentState == MetricStateEnum.NORMAL && matchFlapping == FlappingEnum.FIRST_MATCH))
                    context.getAdditions().put("availStateChanged", Boolean.TRUE);
                return StateProcessorEnum.INST_STATE_PROCESSOR;
            }else
                return StateProcessorEnum.ALARM_STATE_PROCESSOR;
        }
        if(logger.isDebugEnabled()) {
            logger.debug("metric("+metricData.getResourceInstanceId() + "/"+metricData.getMetricId()+
                    ") has not reached flapping yet, [flapping/pre_metric_state-->cur_metric_state : " + flappingValue + "/"
                    + (preMetricStateObj == null ? null : preMetricStateObj.getState()) + "-->" + currentState + "]");
        }
        return null;
    }

    @Override
    public StateProcessorEnum processOrder() {
        return StateProcessorEnum.METRIC_FLAPPING_PROCESSOR;
    }

    @Override
    public String toString() {
        return "MetricFlappingProcessor{"+processOrder()+"}";
    }
}
