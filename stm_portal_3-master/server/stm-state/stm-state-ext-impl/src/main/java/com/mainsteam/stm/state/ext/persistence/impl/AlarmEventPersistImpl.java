package com.mainsteam.stm.state.ext.persistence.impl;

import com.mainsteam.stm.alarm.AlarmService;
import com.mainsteam.stm.alarm.obj.AlarmSenderParamter;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.InstanceStateService;
import com.mainsteam.stm.common.metric.MetricStateService;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.obj.ProfileInfo;
import com.mainsteam.stm.state.engine.StateHandle;
import com.mainsteam.stm.state.ext.persistence.AlarmEventPersist;
import com.mainsteam.stm.state.ext.process.bean.AlarmEventBean;
import com.mainsteam.stm.state.ext.tools.AlarmSnapshotUtils;
import com.mainsteam.stm.state.ext.tools.ThreadPoolUtil;
import com.mainsteam.stm.state.obj.InstanceStateChangeData;
import com.mainsteam.stm.state.obj.InstanceStateData;
import com.mainsteam.stm.state.obj.MetricStateChangeData;
import com.mainsteam.stm.state.obj.MetricStateData;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.*;

/**
 * Created by Xiaopf on 2017/7/21.
 */
@Component("alarmEventPersist")
public class AlarmEventPersistImpl implements AlarmEventPersist,ApplicationListener<ContextRefreshedEvent> {

    private static final Log logger = LogFactory.getLog(AlarmEventPersistImpl.class);

    @Autowired
    @Qualifier("stateThreadPoolUtil")
    private ThreadPoolUtil threadPoolUtil;

    private final List<StateHandle> dispatcher = new ArrayList<>(2); //告警事件分发

    private final LinkedBlockingQueue<AlarmEventBean> metricDataQueue = new LinkedBlockingQueue<>();
    //告警分发线程
    private ExecutorService dispatchThread;

    @Autowired
    private InstanceStateService instanceStateService;
    @Autowired
    private MetricStateService metricStateService;
    @Autowired
    @Qualifier("alarmService")
    private AlarmService alarmService;
    @Autowired
    private AlarmSnapshotUtils alarmSnapshotUtils;
    @Autowired
    private ProfileService profileService;


    @Override
    public void offer(final AlarmEventBean event) {
        try {
            metricDataQueue.put(event);
        } catch (InterruptedException e) {
            if(logger.isErrorEnabled())
                logger.error(e.getMessage(), e);
        }
    }

    @Override
    public AlarmEventBean poll() {
        try {
            return metricDataQueue.take();
        } catch (InterruptedException e) {
            if(logger.isErrorEnabled()){
                logger.error(e.getMessage(), e);
            }
        }
        return null;
    }

    @Override
    public Set<String> occurEvent(final AlarmEventBean event) {

        if(logger.isDebugEnabled()) {
            logger.debug("AlarmEvent Persists : " + event);
        }

        Set<String> result = addTransactionalEvent(event);
        Map<String, Object> additions = event.getAdditions();
        final MetricStateData curMetricState = (MetricStateData) additions.get("persistenceMetricState");
        if(null != curMetricState) { //链接告警不通知
            if(null != dispatcher && !dispatcher.isEmpty()) { //指标状态分发到各个模块

                Future<?> submit = dispatchThread.submit(new Runnable() {
                    @Override
                    public void run() {
                        InstanceStateChangeData stateChangeData =null;
                        MetricStateChangeData metricStateChangeData =null;
                        if(curMetricState.getState() == MetricStateEnum.CRITICAL) {
                            stateChangeData = new InstanceStateChangeData();
                            InstanceStateData instanceStateData = new InstanceStateData(curMetricState.getInstanceID(),
                                    InstanceStateEnum.metricState2InstState(curMetricState.getState()), curMetricState.getCollectTime(),
                                    curMetricState.getMetricID(), curMetricState.getInstanceID(),null);
                            stateChangeData.setNewState(instanceStateData);

                        }else {
                            metricStateChangeData = new MetricStateChangeData();
                            metricStateChangeData.setNewState(curMetricState);

                        }
                        for (StateHandle each : dispatcher) {
                            try { //通知其他模块如果出现异常，不影响事务的提交
                                if(null != stateChangeData){
                                    each.onInstanceStateChange(stateChangeData);
                                }else if(null != metricStateChangeData){
                                    each.onMetricStateChange(metricStateChangeData);
                                }

                            } catch (Throwable throwable) {
                                if (logger.isWarnEnabled()) {
                                    logger.warn("notify StateEngine :" + each.getClass().getSimpleName() + "error," + curMetricState,
                                            throwable);
                                }
                            }
                        }
                    }
                });

                try {
                    submit.get(2, TimeUnit.MINUTES);
                } catch (Exception e) {
                    if(logger.isWarnEnabled()) {
                        List<AlarmSenderParamter> alarmEventList = event.getAlarmEventList();
                        logger.warn("StateEngine dispatches failed:" + alarmEventList + ",error:" + e.getMessage(), e);
                    }
                }


            }
        }
        return result;

    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Set<String> addTransactionalEvent(AlarmEventBean event) {
        Map<String, Object> additions = event.getAdditions();
        //该集合用于在多线程环境中，如果线程执行超时或者中断导致事务部分提交，而剩下的部分仍然需要再次提交
        Set<String> result = new HashSet<>(3);

        final MetricStateData curMetricState = (MetricStateData) additions.get("persistenceMetricState");
        //persists metric state data
        if(null != curMetricState) {
            metricStateService.updateMetricState(curMetricState);
            result.add("persistenceMetricState");
        }
        //persists instance state data
        Map<Long, InstanceStateData> instanceStateMap = (Map<Long, InstanceStateData>) additions.get("persistenceInstStates");
        if(null != instanceStateMap && !instanceStateMap.isEmpty()) {
            List<InstanceStateData> instanceStateDataList = new ArrayList<>(instanceStateMap.size());
            instanceStateDataList.addAll(instanceStateMap.values());
            instanceStateService.addState(instanceStateDataList);
            result.add("persistenceInstStates");
        }
        //persists alarm event data
        List<AlarmSenderParamter> alarmEventList = event.getAlarmEventList();
        if(null != alarmEventList) {
            for (AlarmSenderParamter alarmEvent : alarmEventList) {
                //告警脚本执行及其告警规则
                try {
                    //如果是子资源绑定的策略ID，那么需要查询出父策略ID，因为只有父策略才绑定了告警规则
                    if (!StringUtils.equals(alarmEvent.getSourceID(), alarmEvent.getExt8())) {
                        ProfileInfo profileBasicInfo = profileService.getProfileBasicInfoByProfileId(alarmEvent.getProfileID());
                        if (null != profileBasicInfo)
                            alarmEvent.setProfileID(profileBasicInfo.getParentProfileId());
                    }
                    alarmEvent.setExt7(alarmSnapshotUtils.handleAlarm(Long.parseLong(alarmEvent.getSourceID()), alarmEvent.getExt3(),
                            alarmEvent.getSourceIP(), alarmEvent.getLevel()));
                } catch (Exception e) {
                    if (logger.isWarnEnabled()) {
                        logger.warn("fail to execute alarm script,data(" + alarmEvent.getSourceID() + "/" + alarmEvent.getExt3() + ")," + e.getMessage(), e);
                    }
                }
                alarmService.notify(alarmEvent);
                result.add("alarmEvent");
            }
        }

        return result;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if(null == contextRefreshedEvent.getApplicationContext().getParent()){
            Collection<StateHandle> values = contextRefreshedEvent.getApplicationContext().getBeansOfType(StateHandle.class).values();
            if(null != values && !values.isEmpty()) {
                if(logger.isInfoEnabled()) {
                    logger.info("load alarmEvent dispatchers : " + values);
                }
                dispatcher.addAll(values);
            }

            dispatchThread = new ThreadPoolExecutor(5,threadPoolUtil.getAlarmEventThread(),60L, TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>(),new ThreadFactory() {
                private int counter = 0;
                @Override
                public Thread newThread(Runnable runnable) {
                    Thread t = new Thread(runnable,"StateEngine-dispatch-"+ counter++);
                    if (t.isDaemon())
                        t.setDaemon(false);
                    if (t.getPriority() != Thread.NORM_PRIORITY)
                        t.setPriority(Thread.NORM_PRIORITY);
                    return t;
                }
            });

        }
    }

}
