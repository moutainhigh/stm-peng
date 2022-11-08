package com.mainsteam.stm.state.ext.process.strategy;

import com.mainsteam.stm.dsl.expression.*;
import com.mainsteam.stm.metric.obj.CustomMetricThreshold;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("availMetricComputeAlgorithm")
public class AvailMetricComputeImpl implements MetricCompute{

    @Autowired
    @Qualifier("ocelEngine")
    private OCELEngine ocelEngine;

    @Override
    public <T> Object compute(T... args) throws Exception {

        try {
            String metricData = (String)args[0];
            String metricId = (String)args[1];
            String expression = (String)args[2];
            Boolean isCustom = (Boolean) args[3];

            OCELExpression ocelExpression = ocelEngine.createExpression(expression);
            OCELContext context = new MapContext();
            context.set(isCustom?CustomMetricThreshold.PLACEHOLDER:metricId, metricData);
            Object evaluate = ocelExpression.evaluate(context);
            return (T) evaluate;

        }catch (Exception e){
            throw e;
        }
    }
}
