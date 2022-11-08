package com.mainsteam.stm.state.ext.process;

import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.state.ext.StateComputeContext;
import com.mainsteam.stm.state.ext.StateProcessorEnum;
import com.mainsteam.stm.state.ext.exception.StateComputeException;
import com.mainsteam.stm.state.ext.tools.ThreadPoolUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Xiaopf on 2017/7/11.
 * 告警计算调度处理
 */
@Component("stateComputeForward")
public class StateComputeForwardImpl implements StateComputeForward,ApplicationListener<ContextRefreshedEvent> {

    private static final Log logger = LogFactory.getLog(StateComputeForwardImpl.class);

    @Autowired
    @Qualifier("alarmEventProcessor")
    private StateProcessor alarmEventProcessor;
    @Autowired
    @Qualifier("instStateQueueProcessor")
    private StateProcessor instStateQueueProcessor;
    @Autowired
    @Qualifier("alarmStateQueueProcessor")
    private StateProcessor alarmStateQueueProcessor;
    @Autowired
    private StateComputeException stateComputeException;
    @Autowired
    @Qualifier("stateThreadPoolUtil")
    private ThreadPoolUtil threadPoolUtil;

    private ExecutorService workers ;
    //处理器集合
    private StateProcessor[] processors;

    @Override
    public void fireStateCompute(StateComputeContext context) {
        final StateComputeTask task = this.new StateComputeTask(context);
        Future future = workers.submit(task);
        StateProcessorEnum result = null;
        try {
            result = (StateProcessorEnum) future.get(threadPoolUtil.getStateComputeTimeout(), TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            //重新设置线程的中断状态
            Thread.currentThread().interrupt();
            if(logger.isErrorEnabled()) {
                StringBuffer errorMsg = new StringBuffer(200);
                errorMsg.append("state thread was interrupted, stopped at ");
                errorMsg.append(result);
                errorMsg.append(",data(");
                errorMsg.append(context.getMetricData());
                errorMsg.append(";");
                errorMsg.append(context.getAdditions());
                errorMsg.append(")");
                logger.error(errorMsg.toString(), e);
            }
            if(null != result && result.getOrder() > StateProcessorEnum.METRIC_FLAPPING_PROCESSOR.getOrder()) {
                stateComputeException.dealWithException(context.getAdditions(),false);
            }
            future.cancel(true);

        } catch (ExecutionException e) {
            if(logger.isErrorEnabled()) {
                StringBuffer errorMsg = new StringBuffer(200);
                errorMsg.append("state fails to execute, stops at ");
                errorMsg.append(result);
                errorMsg.append(",data(");
                errorMsg.append(context.getMetricData());
                errorMsg.append(";");
                errorMsg.append(context.getAdditions());
                errorMsg.append(")");
                logger.error(errorMsg.toString(), e);
            }
            if(null != result && result.getOrder() > StateProcessorEnum.METRIC_FLAPPING_PROCESSOR.getOrder()) {
                stateComputeException.dealWithException(context.getAdditions(),false);
            }
            future.cancel(true);

        } catch (TimeoutException e) {
            if(logger.isErrorEnabled()) {
                StringBuffer errorMsg = new StringBuffer(200);
                errorMsg.append("state thread executes timeout,data(");
                errorMsg.append(context.getMetricData());
                errorMsg.append(";");
                errorMsg.append(context.getAdditions());
                errorMsg.append(")");
                logger.error(errorMsg.toString(), e);
            }
            if(null != result && result.getOrder() > StateProcessorEnum.METRIC_FLAPPING_PROCESSOR.getOrder()) {
                //retry again
                if(logger.isInfoEnabled()) {
                    logger.info("retry compute inst state, starts at :" + result);
                }
                Map<String, Object> additions = context.getAdditions();
                if(result == StateProcessorEnum.ALARM_EVNET_PROCESSOR) {
                    alarmEventProcessor.process(context);
                }else {
                    if(result == StateProcessorEnum.INST_STATE_PROCESSOR) {
                        /*
                            简单处理，如果此时指标状态已更新缓存，那么直接将状态状态重置（如果是可用性指标，则还需要将指标值重置），因为我们在计算实例状态时，
                            没办法判断线程超时的具体位置，有可能指标状态已更新，而实例状态还未计算，也有可能只计算了主资源的状态，而子资源的状态还未计算.
                        */
                        stateComputeException.dealWithException(additions,true);
                        StateProcessorEnum stateProcessorEnum = (StateProcessorEnum)instStateQueueProcessor.process(context);
                        if(stateProcessorEnum == StateProcessorEnum.ALARM_STATE_PROCESSOR){
                            alarmStateQueueProcessor.process(context);
                        }
                        alarmEventProcessor.process(context);
                    }else if(result == StateProcessorEnum.ALARM_STATE_PROCESSOR) {
                        stateComputeException.dealWithException(additions,true);
                        if(MetricTypeEnum.AvailabilityMetric != additions.get("metricType")){
                            alarmStateQueueProcessor.process(context);
                        }else {
                            StateProcessorEnum stateProcessorEnum = (StateProcessorEnum)instStateQueueProcessor.process(context);
                            if(stateProcessorEnum == StateProcessorEnum.ALARM_STATE_PROCESSOR){
                                alarmStateQueueProcessor.process(context);
                            }
                        }
                        alarmEventProcessor.process(context);
                    }
                }

            }else {
                future.cancel(true);
            }
        }
    }

    private class StateComputeTask implements Callable<StateProcessorEnum> {
        final StateComputeContext context;
        public StateComputeTask(StateComputeContext context) {
            this.context = context;
        }

        @Override
        public StateProcessorEnum call() {
            return start(context);
        }

        private StateProcessorEnum start(StateComputeContext context) {
            /*
            1.指标状态计算
            2.Flapping计算
            3.资源状态计算
            4.告警信息组装
            5.告警信息入库
            6.告警通知处理
             */
            StateProcessorEnum processorEnum = null;
            for (StateProcessor stateProcessor : processors) {
                try {
                    if(null != processorEnum && processorEnum != stateProcessor.processOrder())
                        continue;
                    if(logger.isDebugEnabled()) {
                        Object resourceInstance = context.getAdditions().get("resourceInstance");
                        MetricCalculateData metricData = context.getMetricData();
                        logger.debug("{" + ((ResourceInstance)resourceInstance).getId() + "/" + metricData.getMetricId() +
                                "} process at " + stateProcessor.getClass().getSimpleName());
                    }
                    Object result = stateProcessor.process(context);
                    if(null == result) {//finish to execute processor
                        if(logger.isDebugEnabled()) {
                            Object resourceInstance = context.getAdditions().get("resourceInstance");
                            MetricCalculateData metricData = context.getMetricData();
                            logger.debug("{" + ((ResourceInstance)resourceInstance).getId() + "/" + metricData.getMetricId() +
                                    "} stops at " + stateProcessor.getClass().getSimpleName());
                        }
                        break;
                    }else {
                        if(result instanceof StateProcessorEnum){
                            processorEnum = (StateProcessorEnum) result;
                        }
                    }
                }catch (Throwable t) {
                    /*
                    如果执行过程中抛出异常，并且当前操作在MetricFlappingProcessor之后的操作（因为这个操作之后才会有缓存更新或者数据库持久化的操作），
                    那么已经改变的指标状态，或者指标值等等均需要重置
                     */
                    if(logger.isErrorEnabled()) {
                        StringBuffer errorMsg = new StringBuffer(200);
                        errorMsg.append("state processor occurs exception,data(");
                        errorMsg.append(context.getMetricData());
                        errorMsg.append(")");
                        logger.error(errorMsg.toString(), t);
                    }
                    if(null != processorEnum && processorEnum.getOrder() > StateProcessorEnum.METRIC_FLAPPING_PROCESSOR.getOrder()) {
                        stateComputeException.dealWithException(context.getAdditions(), false);
                    }
                    break;
                }
            }
            return processorEnum;
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if(null == contextRefreshedEvent.getApplicationContext().getParent()){
            Collection<StateProcessor> values = contextRefreshedEvent.getApplicationContext().getBeansOfType(StateProcessor.class).values();
            if(null != values && !values.isEmpty()) {

                this.processors = new StateProcessor[values.size()];
                values.toArray(this.processors);
                Arrays.sort(this.processors, new Comparator<StateProcessor>() {
                    @Override
                    public int compare(StateProcessor o1, StateProcessor o2) {
                        return o1.processOrder().getOrder() > o2.processOrder().getOrder() ? 1 :
                                (o1.processOrder().getOrder() < o2.processOrder().getOrder() ? -1 : 0);
                    }
                });

                if(logger.isInfoEnabled()) {
                    logger.info("load state processor : " + Arrays.toString(this.processors));
                }
            }

            workers = Executors.newFixedThreadPool(threadPoolUtil.getStateComputeThread(), new ThreadFactory() {
                AtomicInteger index = new AtomicInteger(1);
                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "StateComputeForward-" + index.getAndIncrement());
                }
            });
        }
    }
}
