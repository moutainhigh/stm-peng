package com.mainsteam.stm.state.ext.process.strategy;

import com.mainsteam.stm.dsl.expression.MapContext;
import com.mainsteam.stm.dsl.expression.OCELBooleanExpression;
import com.mainsteam.stm.dsl.expression.OCELContext;
import com.mainsteam.stm.dsl.expression.OCELEngine;
import com.mainsteam.stm.metric.obj.CustomMetricThreshold;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("infoMetricComputeAlgorithm")
public class InfoMetricComputeImpl extends AbstractSpecifiedMetricCompute implements MetricCompute {

    @Autowired
    @Qualifier("ocelEngine")
    private OCELEngine ocelEngine;

    @Override
    public <T> Object compute(T... args) throws Exception {
        try {
            String metricData = (String)args[0];
            String metricId = (String)args[1];
            String expression = (String)args[2];

            OCELBooleanExpression booleanExpression = ocelEngine.createBooleanExpression(expression);
            Object evaluate;
            Boolean isCustom = (Boolean) args[4];
            if(booleanExpression.checkChange()) { //Has Changed 表达式特殊处理
                Long instanceId = (Long) args[3];
                evaluate = computeHasChanged(booleanExpression, metricId, metricData, instanceId, null, isCustom);
            }else{
                OCELContext context = new MapContext();
                context.set(isCustom ? CustomMetricThreshold.PLACEHOLDER : metricId, metricData);
                evaluate = booleanExpression.evaluate(context);
            }
            return (T) evaluate;

        }catch (Exception e){
            throw e;
        }

    }
}
