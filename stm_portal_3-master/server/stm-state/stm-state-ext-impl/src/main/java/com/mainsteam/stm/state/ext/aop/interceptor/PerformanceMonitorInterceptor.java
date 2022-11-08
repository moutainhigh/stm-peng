package com.mainsteam.stm.state.ext.aop.interceptor;

import com.mainsteam.stm.state.ext.monitor.PerformanceMonitor;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Component("performanceMonitorInterceptor")
@Aspect
public class PerformanceMonitorInterceptor implements Ordered{

    private static final int PRIORITY = 1;//在同一切面，aop切入的顺序
    @Autowired
    private PerformanceMonitor performanceMonitor;

    @After(value="execution(* com.mainsteam.stm.state.ext.StateComputeDispatcher.process(..))")
    public void pointcutOffer(){
        performanceMonitor.incrementOffer();
    }

    @After(value = "execution(* com.mainsteam.stm.state.ext.process.StateComputeForwardImpl.fireStateCompute(..))")
    public void pointcutPoll(){
        performanceMonitor.incrementPoll();
    }

    @Override
    public int getOrder() {
        return PRIORITY;
    }
}
