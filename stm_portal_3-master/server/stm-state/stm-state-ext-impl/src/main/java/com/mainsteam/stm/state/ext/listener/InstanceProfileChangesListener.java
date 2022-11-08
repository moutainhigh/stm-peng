package com.mainsteam.stm.state.ext.listener;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.alarm.event.AlarmEventService;
import com.mainsteam.stm.alarm.notify.AlarmNotifyService;
import com.mainsteam.stm.alarm.obj.HandleType;
import com.mainsteam.stm.common.instance.obj.CollectStateEnum;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.InstanceStateService;
import com.mainsteam.stm.common.metric.MetricStateService;
import com.mainsteam.stm.common.metric.sync.InstanceProfileChange;
import com.mainsteam.stm.common.sync.DataSyncPO;
import com.mainsteam.stm.common.sync.DataSyncTypeEnum;
import com.mainsteam.stm.dataprocess.InstanceSyncUtils;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.state.ext.process.bean.AlarmStateQueue;
import com.mainsteam.stm.state.ext.process.bean.CompareInstanceState;
import com.mainsteam.stm.state.ext.tools.StateCatchUtil;
import com.mainsteam.stm.state.obj.InstanceStateData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@Component("instanceProfileChangesListener")
public class InstanceProfileChangesListener extends AbstractProfileChangesListener {

    private static final Log logger = LogFactory.getLog(InstanceProfileChangesListener.class);
    public static final int CHILD_INST_FLAG = 0;//子资源标志

    @Autowired
    private StateCatchUtil stateCatchUtil;
    @Autowired
    private ResourceInstanceService resourceInstanceService;
    @Autowired
    private AlarmEventService alarmEventService;
    @Autowired
    private AlarmNotifyService alarmNotifyService;
    @Autowired
    private MetricStateService metricStateService;
    @Autowired
    private InstanceStateService instanceStateService;

    private final ExecutorService executorService = Executors.newFixedThreadPool(10, new ThreadFactory() {
        final AtomicInteger atomicInteger = new AtomicInteger(1);
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName("InstanceProfileChangedPersist--" + atomicInteger.getAndIncrement());
            return t;
        }
    });

    @Override
    public void process(DataSyncPO dataSyncPO) throws Exception {
        //可以需要考虑批量操作，例如批量取消某些子资源的监控，或者删除等等
        InstanceProfileChange parameter = JSON.parseObject(dataSyncPO.getData(), InstanceProfileChange.class);
        process(parameter);
    }

    void process(InstanceProfileChange profileChange) throws CloneNotSupportedException {
        /*
        1、子资源取消监控：相关告警设置为Delete状态；告警通知等待发送列表记录删除；故障队列清除（缓存数据）；主资源相关故障队列清除;主资源状态计算；
        2、主资源取消监控：相关告警（包括所有子资源）设置为Delete状态；告警通知等待发送列表记录删除；
        3、子资源删除：相关告警删除，告警通知等待发送列表记录删除；故障队列清除（缓存数据）；主资源相关故障队列清除；相关指标状态恢复正常；可用性指标缓存值删除;主资源状态计算
        4、主资源删除：相关告警删除（包括所有子资源），告警通知等待发送列表删除；故障队列清除（包括所有子资源）；相关指标状态恢复正常；可用性指标缓存值删除
        5、子资源加入监控：相关告警恢复；重新加载故障队列（包括更新主资源的故障队列）；重新计算子资源状态及其主资源状态
        6、主资源加入监控：相关告警恢复；重新加载故障队列（包括所有子资源的故障队列）；
         */
        if(logger.isInfoEnabled()) {
            logger.info("instance profile changes: " + profileChange);
        }
        InstanceLifeStateEnum lifeState = profileChange.getLifeState();
        long currentInstId = profileChange.getInstanceID();
        InstanceStateData instanceState = stateCatchUtil.getInstanceState(currentInstId);
        //如果该资源的状态为正常，那么取消监控并不会引起其他资源的变化，故不需要计算
        if(null == instanceState || instanceState.getState() == InstanceStateEnum.NORMAL)
            return;
        ResourceInstance resourceInstance ;
        long parentInstId = currentInstId;
        try {
            resourceInstance = resourceInstanceService.getResourceInstanceWithDeleted(currentInstId);
        } catch (InstancelibException e) {
            if(logger.isErrorEnabled()) {
                logger.error("instance query failed:" + e.getMessage() + "," + profileChange, e);
            }
            return;
        }
        boolean isParentInstance = true;
        if(null == resourceInstance ) {
            if(lifeState == InstanceLifeStateEnum.MONITORED || lifeState == InstanceLifeStateEnum.NOT_MONITORED) {
                if(logger.isWarnEnabled()) {
                    logger.warn("resource instance is null when instance profile is changed." + profileChange);
                }
                return;
            }
            if(profileChange.getParentID() != 0L){ //需要考虑进程及文件子资源物理删除的情况
                isParentInstance = false;
                parentInstId = profileChange.getParentID();
            }
        }else{
            if(resourceInstance.getParentId() > CHILD_INST_FLAG){
                isParentInstance = false;
                parentInstId = resourceInstance.getParentId();
            }
        }

        if(lifeState == InstanceLifeStateEnum.MONITORED) {
            List<InstanceStateData> persistInstStates = new ArrayList<>(1);

            if(!isParentInstance) {
                synchronized (InstanceSyncUtils.getSyncObj(currentInstId)) {
                    //将子资源的故障队列加入到主资源的故障队列中
                    AlarmStateQueue alarmStateQueue = stateCatchUtil.getAlarmStateQueue(String.valueOf(currentInstId));
                    if(null != alarmStateQueue && !alarmStateQueue.isEmpty()) {
                        String parentIdStr = String.valueOf(parentInstId);
                        synchronized (InstanceSyncUtils.getSyncObj(parentInstId)) {
                            AlarmStateQueue parentPriorityQueue = stateCatchUtil.getAlarmStateQueue(parentIdStr);
                            if(null == parentPriorityQueue) {
                                parentPriorityQueue = new AlarmStateQueue();
                            }
                            List<CompareInstanceState> compareInstanceStates = alarmStateQueue.pollAll();
                            if(compareInstanceStates != null) {
                                for (CompareInstanceState c: compareInstanceStates) {
                                    parentPriorityQueue.add(c);
                                }
                            }
                            InstanceStateData parentInstState = stateCatchUtil.getInstanceState(parentInstId);
                            CompareInstanceState peek = parentPriorityQueue.peek();
                            if(null != peek) {
                                parentInstState.setCollectTime(new Date());
                                parentInstState.setAlarmState(InstanceStateEnum.metricState2InstState(peek.getAlarmState()));
                                parentInstState.setCauseBymetricID(peek.getId());
                                if(logger.isInfoEnabled()) {
                                    logger.info("parent inst state changed while child instance monitored:" + parentInstState);
                                }
                                stateCatchUtil.saveInstanceState(parentIdStr, parentInstState);
                                persistInstStates.add(parentInstState);

                            }
                            if(logger.isInfoEnabled()) {
                                logger.info("inst{" + parentIdStr + "} add child queue when monitored:" + compareInstanceStates);
                            }
                            stateCatchUtil.setAlarmStateQueue(parentIdStr, parentPriorityQueue);

                        }
                    }
                }

            }
            StatePersitWork statePersitWork = new StatePersitWork(null, isParentInstance?parentInstId:currentInstId, persistInstStates,
                    InstanceStateEnum.MONITORED, isParentInstance);
            executorService.submit(statePersitWork);

        }else {
            if(lifeState == InstanceLifeStateEnum.NOT_MONITORED || lifeState == InstanceLifeStateEnum.DELETED) { //资源取消监控或者删除
                /*
                资源取消监控，相当于一个资源的恢复过程，如果取消的是某个子资源，那么当前资源的故障状态队列需要清空，并且主资源相关的故障状态队列也需要清空
                子资源删除操作和子资源取消操作，处理过程基本一致，唯一不同的是删除操作需要从数据库中恢复相关状态（实例状态或者指标状态）
                并且，如果当前子资源是正常状态，那么取消告警将不需要有任务操作，因为它不影响主资源的状态。
                 */
                //用于资源删除时需要删除的指标状态的实例ID，之所以需要这个集合，是为了不再同步代码块中删除缓存,耗时不必须的操作都不适合放到同步代码块
                Set<Long> removeMetricByInstances = null;
                List<InstanceStateData> removeInstStates = new ArrayList<>(2);

                if(lifeState == InstanceLifeStateEnum.DELETED) {
                    removeMetricByInstances = new HashSet<>(20);
                    removeMetricByInstances.add(currentInstId);
                }
                if(!isParentInstance ) { //子资源
                    InstanceStateData persist = null; //主资源是否需要入库
                    InstanceStateData parentInstState = stateCatchUtil.getInstanceState(parentInstId);
                    String parentInstIdStr = String.valueOf(parentInstId);
                    String currentInstIdStr = String.valueOf(currentInstId);
                    synchronized (InstanceSyncUtils.getSyncObj(parentInstId)) {
                        AlarmStateQueue parentAlarmStateQueue = stateCatchUtil.getAlarmStateQueue(parentInstIdStr);
                        AlarmStateQueue priorityQueueAlarmState = stateCatchUtil.getAlarmStateQueue(currentInstIdStr);//删除或取消子资源时不需要加锁，因为没有其他写入和读取操作
                        boolean isSaveParentInst = false;
                        if(null != priorityQueueAlarmState && !priorityQueueAlarmState.isEmpty()) {
                            isSaveParentInst = true;
                            //先更新主资源的故障告警队列，然后再删除
                            List<CompareInstanceState> compareInstanceStates = priorityQueueAlarmState.pollAll();
                            if(compareInstanceStates != null) {
                                for (CompareInstanceState c: compareInstanceStates) {
                                    parentAlarmStateQueue.remove(c);
                                }
                            }
                            if(logger.isInfoEnabled()) {
                                logger.info("inst{" + parentInstIdStr + "} remove child queue:" + compareInstanceStates);
                            }
                            stateCatchUtil.setAlarmStateQueue(currentInstIdStr, priorityQueueAlarmState);
                            if(logger.isInfoEnabled()) {
                                logger.info("remove child inst priority queue when profile changed:" + priorityQueueAlarmState);
                            }
                        }
                        //还需要将主资源告警队列中当前子资源的故障状态删除
//                        if(parentAlarmStateQueue.remove(new CompareInstanceState(currentInstIdStr, InstanceStateEnum.CRITICAL))){
//                            isSaveParentInst = true;
//                        }
                        //接下来计算主资源状态,只有主资源的状态由当前子资源状态引起的才需要重新计算，否则直接更新主资源告警队列即可
                        if(!parentAlarmStateQueue.isEmpty()) {
                            CompareInstanceState peek = parentAlarmStateQueue.peek();
                            if(isSaveParentInst) {
                                parentInstState.setCollectTime(new Date());
                                parentInstState.setCauseBymetricID(peek.getId());
                                parentInstState.setAlarmState(InstanceStateEnum.metricState2InstState(peek.getAlarmState()));
                                if(logger.isInfoEnabled()) {
                                    logger.info("parent inst state changed by child ("+currentInstIdStr+") canceled :" + parentInstState);
                                }
                            }
                        }else if(isSaveParentInst){
                            if(logger.isInfoEnabled()) {
                                logger.info("parent inst alarm state turns to normal, cause remove inst " + currentInstIdStr);
                            }
                            parentInstState.setAlarmState(InstanceStateEnum.NORMAL);
                            parentInstState.setCauseBymetricID(currentInstIdStr);
                            parentInstState.setCollectTime(new Date());
                        }
                        if(isSaveParentInst){
                            persist = parentInstState;
                            stateCatchUtil.saveInstanceState(parentInstIdStr, parentInstState);
                            stateCatchUtil.setAlarmStateQueue(parentInstIdStr, parentAlarmStateQueue);
                        }
                    }
                    if(null != persist) {
                        removeInstStates.add(persist);
                    }
                }else { //主资源
                    /*
                       对于主资源取消告警，相关的指标状态，资源状态，故障告警队列均维持不变，只需将相关的告警信息设置为delete状态即可。
                     */
                    if(InstanceLifeStateEnum.DELETED == lifeState) {
                        //如果主资源删除时为Critical状态，那么下面的所有子资源都将是Critical，需要一并恢复回来
                        try {
                            List<ResourceInstance> childInstanceByParentId = resourceInstanceService.getChildInstanceByParentId(parentInstId, true);
                            if(null != childInstanceByParentId) {
                                for (ResourceInstance child : childInstanceByParentId) {
                                    if(child.getLifeState() == InstanceLifeStateEnum.DELETED) {
                                        InstanceStateData childState = stateCatchUtil.getInstanceState(child.getId());
                                        if(childState !=null && childState.getState() != InstanceStateEnum.NORMAL){
                                            removeMetricByInstances.add(child.getId());
                                            AlarmStateQueue alarmStateQueue = this.stateCatchUtil.getAlarmStateQueue(String.valueOf(child.getId()));
                                            alarmStateQueue.removeAll();
                                            this.stateCatchUtil.setAlarmStateQueue(String.valueOf(child.getId()), alarmStateQueue);

                                        }
                                    }
                                }

                            }
                            removeMetricByInstances.add(parentInstId);
                            String parentInstIdStr = String.valueOf(parentInstId);
                            AlarmStateQueue alarmStateQueue = this.stateCatchUtil.getAlarmStateQueue(parentInstIdStr);
                            alarmStateQueue.removeAll();
                            this.stateCatchUtil.setAlarmStateQueue(parentInstIdStr, alarmStateQueue);
                        } catch (InstancelibException e) {
                            if(logger.isWarnEnabled()) {
                                logger.warn("failed to query all child instances by "+ currentInstId + "," + e.getMessage(), e);
                            }
                        }
                        if(logger.isInfoEnabled()) {
                            logger.info("These instance state & metric state should turn to normal while main instance deleted:" + removeMetricByInstances);
                        }
                    }
                }

                StatePersitWork statePersitWork = new StatePersitWork(removeMetricByInstances, isParentInstance?parentInstId:currentInstId,
                        removeInstStates,InstanceLifeStateEnum.DELETED == profileChange.getLifeState()?InstanceStateEnum.DELETED:InstanceStateEnum.NOT_MONITORED,
                        isParentInstance);

                executorService.submit(statePersitWork);

            }
        }

    }

    @Override
    public DataSyncTypeEnum get() {
        return DataSyncTypeEnum.INSTANCE_STATE;
    }

    private class StatePersitWork implements Runnable {

        private Set<Long> removeMetricByInstances; //根据资源实例恢复指标状态,和实例状态

        private Long curInstance; // 根据资源实例ID删除某些告警信息

        private List<InstanceStateData> instanceStateDataList;

        private InstanceStateEnum operation;

        private boolean isParentInstance;

        public InstanceStateEnum getOperation() {
            return operation;
        }

        public void setOperation(InstanceStateEnum operation) {
            this.operation = operation;
        }

        public StatePersitWork(Set<Long> removeMetricByInstances, Long curInstance, List<InstanceStateData> instanceStateDataList,
                               InstanceStateEnum operation, boolean isParentInstance) {
            this.removeMetricByInstances = removeMetricByInstances;
            this.curInstance = curInstance;
            this.instanceStateDataList = instanceStateDataList;
            this.operation = operation;
            this.isParentInstance = isParentInstance;
        }

        @Override
        public void run() {
            try{

                if(null != removeMetricByInstances && !removeMetricByInstances.isEmpty()) {
                    if(operation == InstanceStateEnum.DELETED){
                        metricStateService.updateByInstances(removeMetricByInstances, new Date());
                        List<Long> longList = new ArrayList<>(removeMetricByInstances.size());
                        longList.addAll(removeMetricByInstances);
                        stateCatchUtil.cleanMetricStates(longList);
                    }
                    for (Long instanceId : removeMetricByInstances) {
                        if(operation == InstanceStateEnum.DELETED) {
                            InstanceStateData instanceState = stateCatchUtil.getInstanceState(instanceId);
                            instanceState.setResourceState(InstanceStateEnum.NORMAL);
                            instanceState.setAlarmState(InstanceStateEnum.NORMAL);
                            instanceState.setCollectTime(new Date());
                            instanceState.setCollectStateEnum(CollectStateEnum.COLLECTIBLE);
                            //必须设置这个属性，因为如果主资源致命，将会影响到causeByInstance，从而在下次加入监控时影响状态计算的准确性
                            instanceState.setCauseByInstance(instanceState.getInstanceID());
                            instanceStateService.addState(instanceState);
                            stateCatchUtil.saveInstanceState(String.valueOf(instanceId), instanceState);

                        }
                    }
                }
                if(operation == InstanceStateEnum.NOT_MONITORED) {
                    if(logger.isInfoEnabled()) {
                        logger.info("cancel alarm event while instance cancels to monitored [" + curInstance + "]");
                    }
                    alarmNotifyService.deleteAlarmNotifyWaitByInsts(isParentInstance, curInstance);
                    alarmEventService.recoverAlarmEventBySourceID(String.valueOf(curInstance), HandleType.DELETE, isParentInstance);
                }else if(operation == InstanceStateEnum.DELETED) {
                    if(logger.isInfoEnabled()) {
                        logger.info("delete alarm event while instance was deleted [" + curInstance + "]");
                    }
                    alarmNotifyService.deleteAlarmNotifyWaitByInsts(isParentInstance, curInstance);
                    alarmEventService.deleteAlarmEventByInstanceId(String.valueOf(curInstance), isParentInstance);
                } else {
                    //加入监控
                    if (logger.isInfoEnabled()) {
                        logger.info("Recovery alarm event by instance monitored [" + curInstance + "].");
                    }
                    alarmEventService.recoveryDeletedAlarmEventBySourceID(String.valueOf(curInstance), isParentInstance);
                }

                if(null != instanceStateDataList && !instanceStateDataList.isEmpty()) {
                    instanceStateService.addState(instanceStateDataList);
                }
            }catch (Exception e) {
                if(logger.isErrorEnabled()) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }
}
