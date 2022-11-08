package com.mainsteam.stm.state.ext;

import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.dataprocess.engine.MetricDataPersistence;
import com.mainsteam.stm.dataprocess.engine.MetricDataProcessor;
import com.mainsteam.stm.metric.obj.CustomMetric;
import com.mainsteam.stm.state.ext.process.StateComputeForward;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by Xiaopf on 2017/7/10.
 */
@Component("stateComputeDispatcher")
public class StateComputeDispatcher implements MetricDataProcessor, ApplicationListener<ContextRefreshedEvent> {

    private static final Log logger = LogFactory.getLog(StateComputeDispatcher.class);
    //指标数据队列
    private final LinkedBlockingQueue<StateComputeContext> metricDataQueue = new LinkedBlockingQueue<>();
    @Autowired
    @Qualifier("stateComputeForward")
    private StateComputeForward stateComputeForward;

    @Override
    public MetricDataPersistence process(MetricCalculateData metricCalculateData, ResourceMetricDef resourceMetricDef,
                                         CustomMetric customMetric, Map<String, Object> map) throws Exception {
        StateComputeContext context = new StateComputeContext(metricCalculateData,resourceMetricDef,customMetric,map);
        dispatch(context);
        return null;
    }

    public void dispatch(StateComputeContext context) {
        try {
            metricDataQueue.put(context);
        } catch (InterruptedException e) {
            if(logger.isErrorEnabled())
                logger.error(e.getMessage(), e);
        }
    }

    public void run() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try{
                        StateComputeContext poll = metricDataQueue.take();
                        if(null != poll) {
                            stateComputeForward.fireStateCompute(poll);
                        }
                    }catch (Throwable throwable) {
                        if(logger.isErrorEnabled()) {
                            logger.error(throwable.getMessage(), throwable);
                        }
                    }
                }
            }
        }, "stateComputeDispather-init");
        thread.start();
    }

    @Override
    public int getOrder() {
        return 8;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent applicationEvent) {
        if(null == applicationEvent.getApplicationContext().getParent()){
            run();
        }
    }
}
