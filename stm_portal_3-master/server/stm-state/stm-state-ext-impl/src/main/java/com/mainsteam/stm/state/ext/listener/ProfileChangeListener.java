package com.mainsteam.stm.state.ext.listener;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.common.metric.sync.InstanceCancelMonitorData;
import com.mainsteam.stm.common.metric.sync.InstanceProfileChange;
import com.mainsteam.stm.common.metric.sync.MetricProfileChange;
import com.mainsteam.stm.common.sync.DataSyncPO;
import com.mainsteam.stm.common.sync.DataSyncTypeEnum;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component("stateProfileChangeListener")
public class ProfileChangeListener extends AbstractProfileChangesListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final Log logger = LogFactory.getLog(ProfileChangeListener.class);
    public static final int START_RECURISE = 2; //是否开始循环

    @Autowired
    private MetricProfileChangesListener metricProfileChangesListener;
//    @Autowired
//    private InstanceProfileChangesListener instanceProfileChangesListener;

    //指标取消告警队列
    private LinkedBlockingQueue<MetricProfileChange> profileMetricQueue = new LinkedBlockingQueue<>();
    //子资源取消监控队列
    //private LinkedBlockingQueue<InstanceProfileChange> instanceQueue = new LinkedBlockingQueue<>();

    private ExecutorService executor = Executors.newFixedThreadPool(8, new ThreadFactory() {
        private AtomicInteger index = new AtomicInteger(1);
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "profileChangeListener-" + index.getAndIncrement());
        }
    });

    @Override
    public void process(DataSyncPO dataSyncPO) throws Exception {
        String data = dataSyncPO.getData();
        if(logger.isInfoEnabled()){
            logger.info("profile has been changed:" + data);
        }
        InstanceCancelMonitorData monitorData = JSON.parseObject(data, InstanceCancelMonitorData.class);
        Map<Long, Set<String>> childrenMetrics = monitorData.getChildrenMetrics();
        if(childrenMetrics != null) {
            for (Long aLong : childrenMetrics.keySet()) {
                Set<String> metricSet = childrenMetrics.get(aLong);
                for (String metric : metricSet) {
                    offerMetric(metric, aLong);
                }

            }
        }

        Set<String> metricList = monitorData.getMetricList();
        if(metricList !=null) {
            for (String metric : metricList) {
                offerMetric(metric, monitorData.getInstanceId());
            }
        }
//        InstancelibInterceptor接口会将资源取消监控或加入监控通知
//        Set<Long> children = monitorData.getChildren();
//        for (Long childId : children) {
//            InstanceProfileChange instanceProfile = new InstanceProfileChange();
//            instanceProfile.setInstanceID(childId);
//            instanceProfile.setLifeState(InstanceLifeStateEnum.NOT_MONITORED);
//
//            instanceQueue.offer(instanceProfile);
//        }

    }

    private void offerMetric(String metricId, long instanceId) {
        MetricProfileChange metricProfile = new MetricProfileChange();
        metricProfile.setCustomMetric(false);
        metricProfile.setAlarmConfirm(false);
        metricProfile.setIsAlarm(false);
        metricProfile.setInstanceId(instanceId);
        metricProfile.setMetricID(metricId);

        try {
            profileMetricQueue.put(metricProfile);
        } catch (InterruptedException e) {
            if(logger.isErrorEnabled())
                logger.error(e.getMessage(), e);
        }
    }

    @Override
    public DataSyncTypeEnum get() {
        return DataSyncTypeEnum.PROFILE_STATE;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if(null == contextRefreshedEvent.getApplicationContext().getParent()){
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        int flag = 0;
                        try{
                            final MetricProfileChange poll = profileMetricQueue.take();
                            if(null != poll) {
                                flag = 0;
                                executor.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        metricProfileChangesListener.process(poll);
                                    }
                                });
                            }else{
                                flag++;
                            }
                        }catch (Throwable throwable) {
                            if(logger.isErrorEnabled()) {
                                logger.error(throwable.getMessage(), throwable);
                            }
                        }
//                        try{
//                            final InstanceProfileChange poll = instanceQueue.poll();
//                            if(null != poll) {
//                                flag = 0;
//                                executor.execute(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        try {
//                                            instanceProfileChangesListener.process(poll);
//                                        } catch (CloneNotSupportedException e) {
//                                            if(logger.isErrorEnabled()) {
//                                                logger.error(e.getMessage() + "," + poll, e);
//                                            }
//                                        }
//                                    }
//                                });
//                            }else {
//                                flag++;
//                            }
//                        }catch (Throwable throwable) {
//                            if(logger.isErrorEnabled()) {
//                                logger.error(throwable.getMessage(), throwable);
//                            }
//                        }
                        if(flag == START_RECURISE) {
                            try {
                                TimeUnit.SECONDS.sleep(30);
                            } catch (InterruptedException e) {
                                if(logger.isErrorEnabled()){
                                    logger.error(e.getMessage(), e);
                                }
                            }
                        }
                    }
                }
            }, "stateProfileChangeListener-init");
            thread.start();

        }
    }
}
