package com.mainsteam.stm.state.ext.aop;

import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.metric.obj.CustomMetric;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by Xiaopf on 2017/7/11.
 */
@Component("metricInterceptorManager")
@Aspect
public class MetricInterceptorManager implements ApplicationListener<ContextRefreshedEvent>,Ordered {

    private static final Log logger = LogFactory.getLog(MetricInterceptorManager.class);
    private static final int PRIORITY = 0;

    private List<ValidateInterceptor> validateInterceptors;

    @Pointcut(value="execution(* com.mainsteam.stm.state.ext.StateComputeDispatcher.process(..))")
    public void point(){}

    @AfterThrowing(pointcut = "point()", throwing = "e")
    public void filterThrow(JoinPoint joinPoint, Exception e) {
        if(logger.isErrorEnabled()) {
            Object[] args = joinPoint.getArgs();
            if(args != null && args.length > 0) {
                for(Object obj : args) {
                    if(obj instanceof MetricCalculateData) {
                        MetricCalculateData metricData = (MetricCalculateData) obj;
                        logger.error(e.getMessage()+ ",metricData:" + metricData, e);
                        break;
                    }
                }
            }
        }
    }

    @Around(value = "point()")
    public Object filterAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        Object[] objects = proceedingJoinPoint.getArgs();
        MetricCalculateData metricCalculateData = null;
        ResourceMetricDef metricDef = null;
        Map<String, Object> parameters = null;
        CustomMetric customMetric = null;
        for(Object object : objects) {
            if(object instanceof MetricCalculateData)
                metricCalculateData = (MetricCalculateData)object;
            else if(object instanceof Map)
                parameters = (Map)object;
            else if(object instanceof ResourceMetricDef)
                metricDef = (ResourceMetricDef)object;
            else if(object instanceof CustomMetric)
                customMetric = (CustomMetric)object;
        }

        if(null != validateInterceptors) {
            for(ValidateInterceptor interceptor : validateInterceptors) {
                boolean flag = interceptor.validate(metricCalculateData, metricDef, customMetric, parameters);
                if(flag)
                    return null;
            }
        }
        return proceedingJoinPoint.proceed(objects);

    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if(null == contextRefreshedEvent.getApplicationContext().getParent()) {
            Collection<ValidateInterceptor> values = contextRefreshedEvent.getApplicationContext().getBeansOfType(ValidateInterceptor.class).values();
            if(null != values) {
                validateInterceptors = new ArrayList<>(values.size());
                validateInterceptors.addAll(values);
                Collections.sort(validateInterceptors, new Comparator<ValidateInterceptor>() {
                    @Override
                    public int compare(ValidateInterceptor o1, ValidateInterceptor o2) {
                        return o1.getOrder() < o2.getOrder() ? -1 : (o1.getOrder() > o2.getOrder() ? 1 : 0);
                    }
                });
            }
        }
    }

    @Override
    public int getOrder() {
        return PRIORITY;
    }
}
