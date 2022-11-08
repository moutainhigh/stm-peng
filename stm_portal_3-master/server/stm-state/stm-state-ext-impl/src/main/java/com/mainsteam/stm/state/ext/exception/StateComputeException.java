package com.mainsteam.stm.state.ext.exception;

import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.dataprocess.InstanceSyncUtils;
import com.mainsteam.stm.state.ext.tools.StateCatchUtil;
import com.mainsteam.stm.state.obj.InstanceStateData;
import com.mainsteam.stm.state.obj.MetricStateData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/*
状态计算错误异常处理类
 */
@Component("stateComputeException")
public class StateComputeException {

    private static final Log logger = LogFactory.getLog(StateComputeException.class);

    @Autowired
    private StateCatchUtil stateCatchUtil;

    /**
     * 当线程执行过程中抛出异常时的回退操作
     * @param additions 状态等缓存值
     * @param isClear 是否需要删除
     */
    public void dealWithException(Map<String, Object> additions, boolean isClear) {

        //因为指标状态计算只有在满足Flapping之后才有可能进行一些缓存更新或者持久化操作，所以只需要处理Flapping之后的操作，其他的直接cancel即可
        if(null != additions && !additions.isEmpty()) {

            MetricStateData persistenceMetricState = (MetricStateData) additions.get("persistenceMetricState");
            MetricStateData preMetricStateData = (MetricStateData) additions.get("preMetricStateData");
            if(null != persistenceMetricState) {

                if(null == preMetricStateData) {
                    //首次计算指标状态
                    if(logger.isInfoEnabled()) {
                        logger.info("remove metric state (" + persistenceMetricState.getInstanceID() + "/" + persistenceMetricState.getMetricID() + ")");
                    }
                    stateCatchUtil.removeMetricState(persistenceMetricState.getInstanceID(), persistenceMetricState.getMetricID());
                }else {
                    if(logger.isInfoEnabled()) {
                        logger.info("reset metric state {" + preMetricStateData + "}");
                    }
                    stateCatchUtil.saveMetricState(preMetricStateData);
                }
            }
            MetricTypeEnum metricType = (MetricTypeEnum) additions.get("metricType");
            //如果是可用性指标，则可能还需要更新缓存值
            if(MetricTypeEnum.AvailabilityMetric == metricType){
                if(logger.isInfoEnabled()) {
                    logger.info("reset avail metric data " + persistenceMetricState);
                }
                if(null == preMetricStateData) {
                    stateCatchUtil.removeAvailabilityMetricState(persistenceMetricState.getInstanceID(), persistenceMetricState.getMetricID());
                }else{
                    stateCatchUtil.setAvailabilityMetricState(persistenceMetricState.getInstanceID(), persistenceMetricState.getMetricID(), preMetricStateData.getState());
                }
            }
            //重置资源状态,需要考虑到首次计算没有资源状态的情况
            Map<Long, InstanceStateData> historyInstStateMap = (Map<Long, InstanceStateData>) additions.get("historyInstanceStates");
            Map<Long, InstanceStateData> persistInstStateMap = (Map<Long, InstanceStateData>) additions.get("persistenceInstStates");

            if(null != persistInstStateMap && !persistInstStateMap.isEmpty()) {
                Set<Map.Entry<Long, InstanceStateData>> entries = persistInstStateMap.entrySet();
                Iterator<Map.Entry<Long, InstanceStateData>> iterator = entries.iterator();
                while (iterator.hasNext()) {
                    InstanceStateData newInstanceState = iterator.next().getValue();
                    long instanceId = newInstanceState.getInstanceID();
                    InstanceStateData resetInstState = null;
                    if(null != historyInstStateMap) {
                        resetInstState = historyInstStateMap.get(instanceId);
                    }
                    synchronized (InstanceSyncUtils.getSyncObj(instanceId)) {
                        if(null == resetInstState) {
                            if(logger.isInfoEnabled()) {
                                logger.info("remove instance state from cache:" + instanceId);
                            }
                            stateCatchUtil.cleanInstanceState(instanceId);
                            //如果当前资源实例没有历史状态，那么它肯定没有故障告警状态
                        }else {
                            if(logger.isInfoEnabled()) {
                                logger.info("reset instance state to " + resetInstState);
                            }
                            stateCatchUtil.saveInstanceState(String.valueOf(instanceId), resetInstState);
                        }
                        stateCatchUtil.removeAlarmStateQueue(String.valueOf(instanceId));

                    }
                }
            }

//            Map<String,CompareInstanceState> historyAlarmStateQueue = (Map<String, CompareInstanceState>) additions.get("historyAlarmStateQueue");
//            if(null != historyAlarmStateQueue && !historyAlarmStateQueue.isEmpty()) {
//                Iterator<String> iterator = historyAlarmStateQueue.keySet().iterator();
//                while (iterator.hasNext()) {
//                    String next = iterator.next();
//                    CompareInstanceState compareInstanceState = historyAlarmStateQueue.get(next);
//                    String[] split = next.split("_");
//                    long instanceId = Long.parseLong(split[0]);
//                    String method = split[1];
//                    synchronized (InstanceSyncUtils.getSyncObj(instanceId)) {
//                        PriorityQueue<CompareInstanceState> priorityQueueAlarmState = stateCatchUtil.getAlarmStateQueue(split[0]);
//                        if(null != priorityQueueAlarmState ) {
//                            if(StringUtils.equals(method, "offer")){
//                                if(logger.isInfoEnabled()) {
//                                    logger.info("inst(" + instanceId + ") offer alarm state item:" + compareInstanceState);
//                                }
//                                priorityQueueAlarmState.offer(compareInstanceState);
//                            }else {
//                                priorityQueueAlarmState.remove(compareInstanceState);
//                                if(StringUtils.equals(method, "update")) {
//                                    priorityQueueAlarmState.offer(compareInstanceState);
//                                    if(logger.isInfoEnabled()) {
//                                        logger.info("inst(" + instanceId + ") update alarm state item:" + compareInstanceState);
//                                    }
//                                }else{
//                                    if(logger.isInfoEnabled()) {
//                                        logger.info("inst(" + instanceId + ") poll alarm state item:" + compareInstanceState);
//                                    }
//                                }
//                            }
//                        }
//                        stateCatchUtil.setPriorityQueueAlarmState(split[0], priorityQueueAlarmState);
//                    }
//                }
//            }
            if(isClear) {
                additions.remove("historyInstanceStates");
                additions.remove("persistenceInstStates");
                //additions.remove("historyAlarmStateQueue");
            }

        }
    }
}
