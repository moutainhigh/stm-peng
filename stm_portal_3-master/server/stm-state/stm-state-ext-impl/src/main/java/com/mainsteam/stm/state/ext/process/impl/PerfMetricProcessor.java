package com.mainsteam.stm.state.ext.process.impl;

import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
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
import java.util.Map;

/**
 * Created by Xiaopf on 2017/7/11.
 * 性能指标计算接口
 */
@Component("perfMetricProcessor")
public class PerfMetricProcessor extends AbstractMetricCompute implements StateProcessor {

    private static final Log logger = LogFactory.getLog(PerfMetricProcessor.class);

    @Autowired
    @Qualifier("perfMetricComputeAlgorithm")
    private MetricCompute metricCompute;
    @Autowired
    private MetricFlappingUtil metricFlappingUtil;

    @Override
    public Object process(StateComputeContext context) {
        MetricCalculateData metricData = context.getMetricData();
        if(null == metricData.getMetricData() || metricData.getMetricData().length == EMPTY_METRIC_DATA){
            if(logger.isDebugEnabled()) {
                logger.debug("perf metric value is null(" + metricData.getResourceInstanceId() + "/" + metricData.getMetricId() + ").");
            }
            //重置Flapping
            metricFlappingUtil.resetFlapping(metricData.getResourceInstanceId() + metricData.getMetricId());
            return null;
        }
        List<ProfileThreshold> profileThresholds = dealWithThresholds(context);
        if(profileThresholds == null || profileThresholds.isEmpty())
            return null;
        MetricStateEnum metricStateEnum = computeByThresholds(profileThresholds, context);
        if(null == metricStateEnum) {
            if(logger.isInfoEnabled()) {
                logger.info("metric state is null, stop processing:" + metricData);
            }
            metricFlappingUtil.resetFlapping(metricData.getResourceInstanceId() + metricData.getMetricId());
            return null;
        }
        Map<String, Object> additions = context.getAdditions();
        //has changed阈值表达式不能自动恢复状态,判断是否包含一次指标的状态是为了首次计算可以有指标状态
        if(additions.containsKey("hasChangedReturn") && additions.containsKey("preMetricStateData")) {
            return null;
        }

        additions.put("metricState", metricStateEnum);
        if(additions.containsKey("skipMetricFlapping")){
            if(additions.get("metricType") == MetricTypeEnum.AvailabilityMetric){
                return StateProcessorEnum.INST_STATE_PROCESSOR;
            }else
                return StateProcessorEnum.ALARM_STATE_PROCESSOR;
        }

        return StateProcessorEnum.METRIC_FLAPPING_PROCESSOR;
    }

    @Override
    public Object compute(ProfileThreshold profileThreshold, StateComputeContext context) throws Exception {
        MetricCalculateData metricData = context.getMetricData();
        return metricCompute.compute(metricData.getMetricData()[0], profileThreshold.getMetricId(), profileThreshold.getThresholdExpression(),
                metricData.getResourceInstanceId(), metricData.isCustomMetric());
    }

    @Override
    public StateProcessorEnum processOrder() {
        return StateProcessorEnum.PERF_METRIC_PROCESSOR;
    }

    @Override
    public String toString() {
        return "PerfMetricProcessor{"+processOrder()+"}";
    }
}
