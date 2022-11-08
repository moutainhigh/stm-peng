package com.mainsteam.stm.state.ext.aop.interceptor;

import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.state.ext.StateComputeContext;
import com.mainsteam.stm.state.obj.MetricStateData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Created by Xiaopf on 2017/7/13.
 */
@Deprecated
//@Component("metricFlappingInterceptor")
//@Aspect
public class MetricFlappingInterceptor {

    private static final Log logger = LogFactory.getLog(MetricFlappingInterceptor.class);

    @Pointcut(value="execution(* com.mainsteam.stm.state.ext.process.impl.MetricFlappingProcessor.process(..))")
    public void pointcut(){}

    //@Around(value = "pointcut()")
    public Object filterAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        Object[] objects = proceedingJoinPoint.getArgs();
        StateComputeContext context = (StateComputeContext)objects[0];
        //检查资源状态是否变化
        Object preMetricStateObj = context.getAdditions().get("preMetricStateData");
        if(null == preMetricStateObj)
            return proceedingJoinPoint.proceed(objects);

        MetricStateData previous = (MetricStateData) preMetricStateObj;
        MetricStateEnum current = (MetricStateEnum) context.getAdditions().get("metricState");
        if(current != previous.getState()){
            if(logger.isInfoEnabled()) {
                logger.info("metric state changed("+previous.getInstanceID() + "/"
                        + previous.getMetricID() + "/" + previous.getState() + "-->" + current +").");
            }
            return proceedingJoinPoint.proceed(objects);
        }else {
            /*
            Flapping计算波动，所以，即使状态未变，需要将Flapping重置
             */
            if(logger.isDebugEnabled()) {
                logger.debug("metric state does not changed("+previous.getInstanceID() + "/"
                        + previous.getMetricID() + "/" + current +").");
            }
        }

        return null;
    }

}
