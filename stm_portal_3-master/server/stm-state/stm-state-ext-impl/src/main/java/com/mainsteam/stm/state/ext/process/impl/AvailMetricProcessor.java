package com.mainsteam.stm.state.ext.process.impl;

import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.profilelib.obj.ProfileThreshold;
import com.mainsteam.stm.state.ext.StateComputeContext;
import com.mainsteam.stm.state.ext.StateProcessorEnum;
import com.mainsteam.stm.state.ext.process.StateProcessor;
import com.mainsteam.stm.state.ext.process.strategy.AbstractMetricCompute;
import com.mainsteam.stm.state.ext.process.strategy.MetricCompute;
import com.mainsteam.stm.state.ext.tools.MetricFlappingUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Xiaopf on 2017/7/11.
 * 可用性指标计算接口
 */
@Component("availableMetricProcessor")
public class AvailMetricProcessor extends AbstractMetricCompute implements StateProcessor {

    private static final Log logger = LogFactory.getLog(AvailMetricProcessor.class);
    public static final int VALID_DATA_LENGTH = 1; //指标值有效长度

    @Autowired
    @Qualifier("availMetricComputeAlgorithm")
    private MetricCompute availMetricCompute;
    @Autowired
    private MetricFlappingUtil metricFlappingUtil;

    @Override
    public Object process(StateComputeContext context) {

        MetricCalculateData metricData = context.getMetricData();
        String metricValue;
        if (null != metricData.getMetricData() && metricData.getMetricData().length >= VALID_DATA_LENGTH) {
            metricValue = metricData.getMetricData()[0];
        }else{
            String key = metricData.getResourceInstanceId() + metricData.getMetricId();
            if(logger.isInfoEnabled()) {
                logger.info("avail metric data is null("+ key +"), remove flapping...");
            }
            //需要考虑一种情况：上次采集值不为空，当次采集为空，如果Flapping已经计算过一次，这时需要将Flapping重置
            metricFlappingUtil.resetFlapping(key);
            return null;
        }

        List<ProfileThreshold> profileThresholds = dealWithThresholds(context);
        if(profileThresholds == null || profileThresholds.isEmpty())
            return null;
        MetricStateEnum metricStateEnum = computeByThresholds(profileThresholds, context);
        //如果当前计算状态为空，则不往下计算，因为出现这种情况有两种情况，一是资源未监控，二是状态计算出现异常；
        if(null == metricStateEnum || MetricStateEnum.UNKOWN == metricStateEnum) {
            if(logger.isDebugEnabled()) {
                logger.debug("metric state is null, stop processing:" + metricData);
            }
            metricFlappingUtil.resetFlapping(metricData.getResourceInstanceId() + metricData.getMetricId());
            return null;
        }
        context.getAdditions().put("metricState", metricStateEnum);
        context.getAdditions().put("availMetricValue", metricValue);

        return StateProcessorEnum.METRIC_FLAPPING_PROCESSOR;
    }

    @Override
    public Object compute(ProfileThreshold profileThreshold, StateComputeContext context) throws Exception {
        MetricCalculateData metricData = context.getMetricData();
        return availMetricCompute.compute(metricData.getMetricData()[0], profileThreshold.getMetricId(), profileThreshold.getThresholdExpression(),
                metricData.isCustomMetric());
    }

    @Override
    public StateProcessorEnum processOrder() {
        return StateProcessorEnum.AVAIL_METRIC_PROCESSOR;
    }

    @Override
    public String toString() {
        return "AvailMetricProcessor{"+processOrder()+"}";
    }
}
