package com.mainsteam.stm.state.ext.process.strategy;

import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.dsl.expression.*;
import com.mainsteam.stm.metric.obj.CustomMetricThreshold;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Created by Xiaopf on 2017/7/12.
 * 性能指标计算
 */
@Component("perfMetricComputeAlgorithm")
public class PerfMetricComputeImpl extends AbstractSpecifiedMetricCompute implements MetricCompute {

    @Autowired
    @Qualifier("ocelEngine")
    private OCELEngine ocelEngine;

    @Override
    public <T> T compute(T[] args) throws Exception {
        String metricData = (String)args[0];
        if(StringUtils.isEmpty(metricData)) {
            return null;
        }
        String metricId = (String)args[1];
        String expression = (String)args[2];
        Boolean isCustom = (Boolean) args[4];
        try {
            double v = Double.parseDouble(metricData);
            OCELBooleanExpression booleanExpression = ocelEngine.createBooleanExpression(expression);
            Object evaluate;
            if(booleanExpression.checkChange()){
                Long instanceId = (Long) args[3];
                evaluate = computeHasChanged(booleanExpression, metricId, metricData, instanceId, MetricTypeEnum.PerformanceMetric, isCustom);
            }else{
                OCELContext context = new MapContext();
                context.set(isCustom? CustomMetricThreshold.PLACEHOLDER:metricId, v);
                evaluate = booleanExpression.evaluate(context);
            }
            return (T) evaluate;

        }catch (Exception e){
            throw e;
        }
    }

}
