package com.mainsteam.stm.state.ext.process.impl;

import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.common.instance.obj.CollectStateEnum;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.dataprocess.InstanceSyncUtils;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.state.ext.StateComputeContext;
import com.mainsteam.stm.state.ext.StateProcessorEnum;
import com.mainsteam.stm.state.ext.exception.StateComputeException;
import com.mainsteam.stm.state.ext.process.StateProcessor;
import com.mainsteam.stm.state.ext.tools.AvailStateComputeUtil;
import com.mainsteam.stm.state.ext.tools.StateCatchUtil;
import com.mainsteam.stm.state.ext.tools.StateComputeCacheUtil;
import com.mainsteam.stm.state.obj.InstanceStateData;
import com.mainsteam.stm.state.obj.MetricStateData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by Xiaopf on 2017/7/25.
 */
@Component("instStateQueueProcessor")
public class InstStateQueueProcessor implements StateProcessor {

    private static final Log logger = LogFactory.getLog(InstStateQueueProcessor.class);

    public static final int PARENT_INSTANCE_FLAG = 0; //主资源标志

    @Autowired
    private StateCatchUtil stateCatchUtil;
    @Autowired
    private StateComputeCacheUtil stateComputeCacheUtil;
    @Autowired
    private AvailStateComputeUtil availStateComputeUtil;
    @Autowired
    private ResourceInstanceService resourceInstanceService;
    @Autowired
    private StateComputeException stateComputeException;

    /**
     * 资源状态与告警状态算法：
     * 由当前指标计算出指标状态后，再满足flapping之后，再转换成资源状态（InstanceStateData）
     * InstanceStateData实例表示当前实例的状态，在这个实例中维护了一个优先级队列，改队列保存了当前实例下所有的指标状态（实际上是指标状态转换而来的资源实例状态，为了不混淆，
     * 这是称之为指标状态），假设有n个指标（X1 .... Xn）,X1s表示该指标的状态，同理，Xns表示第N个指标的状态，如果X1s ...Xns状态有变化则入列，此时，优先级队列维护了一个按
     * 指标状态由高到低的顺序(告警状态)。如果状态由低-->高，则把上次的状态入列，如果状态由高-->低，
     * 只需要与列头元素比较状态大小即可计算出当前实例的最高状态
     * @param context
     * @return
     */
    @Override
    public Object process(StateComputeContext context) {

        MetricCalculateData metricData = context.getMetricData();
        Map<String, Object> additions = context.getAdditions();
        InstanceStateEnum currentState = null;
        ResourceInstance resourceInstance = (ResourceInstance) additions.get("resourceInstance");
        long curInstId = resourceInstance.getId();
        /*
        由于资源状态可能有多个可用性指标联合计算，即使在上一步指标状态已经发生改变，并且满足flapping的情况下，仍然需要使用可用性指标缓存数据进行资源状态计算,
        但有一种情况不需要再计算资源状态：当前资源只有一个可用性指标时，可用性指标状态就是资源状态
         */
        Map<String, MetricStateEnum> availCacheData = (Map<String, MetricStateEnum>) additions.get("currentAvailCache");
        if(null != availCacheData) {
            InstanceStateEnum resourceState = availStateComputeUtil.calculateResourceState(resourceInstance, availCacheData);
            if(null == resourceState || InstanceStateEnum.UNKOWN == resourceState) { //计算发生错误，保持上次状态，并返回
                return null;
            }else {
                currentState = resourceState;
            }
        }
        //当前资源实例只有一个可用性指标，指标状态即资源状态
        MetricStateEnum curMetricState = (MetricStateEnum) additions.get("metricState");
        if(null == currentState) {
            currentState = InstanceStateEnum.metricState2InstState(curMetricState);
        }
        InstanceStateData currentInstanceState = null;
        //临时保存的资源实例状态，在后续的持久化过程中，如果出现异常，则需要回退到上一次的值
        Map<Long,InstanceStateData> historyInstanceStates = new HashMap<>(2);
        Map<Long,InstanceStateData> persistenceInstStates = new HashMap<>(2); //需要保存到数据库的资源实例状态
        MetricStateData persistenceMetricState; //需要保存至数据库，历史数据已经缓存
        InstanceStateEnum preParentResourceState = null;
        synchronized (InstanceSyncUtils.getSyncObj(curInstId)) {
            InstanceStateData preInstanceData = stateCatchUtil.getInstanceState(curInstId);
            if(null != preInstanceData) {
                preParentResourceState = preInstanceData.getResourceState();
                if(currentState != preParentResourceState) {
                    //当前资源：正常 --> 致命或者致命-->正常，这时只需要改变其资源状态即可，故障状态维持原来的不变即可，因为可用性指标并不影响告警状态
                    currentInstanceState = new InstanceStateData();
                    currentInstanceState.setResourceState(currentState);
                    currentInstanceState.setAlarmState(preInstanceData.getAlarmState());//不改变告警状态
                    currentInstanceState.setInstanceID(preInstanceData.getInstanceID());
                    currentInstanceState.setCauseByInstance(preInstanceData.getInstanceID());
                    currentInstanceState.setCauseBymetricID(preInstanceData.getCauseBymetricID());
                    currentInstanceState.setCollectTime(metricData.getCollectTime());
//                    if(resourceInstance.getParentId() <= 0) {
//                        CollectStateEnum curCollectState = (CollectStateEnum) additions.get("collectState");
//                        currentInstanceState.setCollectStateEnum(curCollectState);
//                    }
                    additions.put("isAlarm", Boolean.TRUE);//是否需要告警
                }
            }else { //首次计算默认告警状态为Normal
                CollectStateEnum collectStateEnum = null;
                String causeMetric = curInstId + "_" + metricData.getMetricId();
//                if(resourceInstance.getParentId() <=0)
//                    collectStateEnum = (CollectStateEnum) additions.get("collectState");
                currentInstanceState = new InstanceStateData(curInstId, currentState, currentState,
                        metricData.getCollectTime(), causeMetric, metricData.getResourceInstanceId(), collectStateEnum);
                if(currentState == InstanceStateEnum.CRITICAL)
                    additions.put("isAlarm", Boolean.TRUE);//是否需要告警
            }
            //缓存资源状态,为了尽快释放锁
            if(null != currentInstanceState) {
                if(logger.isInfoEnabled()) {
                    logger.info("Instance changes:" + currentInstanceState + ", avail data map:" + availCacheData);
                }
//                if(null != currentInstanceState.getCollectStateEnum() || resourceInstance.getParentId() > 0)
                    stateCatchUtil.saveInstanceState(String.valueOf(curInstId), currentInstanceState);

                persistenceInstStates.put(currentInstanceState.getInstanceID(), currentInstanceState);
            }
            if(null != preInstanceData)
                historyInstanceStates.put(preInstanceData.getInstanceID(),preInstanceData);

        }
        //缓存可用性指标状态值
        Boolean removeAvailMetricState = (Boolean) additions.get("removeAvailMetricState");
        if(removeAvailMetricState == Boolean.TRUE) {
            stateCatchUtil.removeAvailabilityMetricState(curInstId, metricData.getMetricId());
        }else{
            stateCatchUtil.setAvailabilityMetricState(curInstId, metricData.getMetricId(),
                    (MetricStateEnum) additions.get("metricState"));
        }
        persistenceMetricState = new MetricStateData();
        persistenceMetricState.setType(MetricTypeEnum.AvailabilityMetric);
        persistenceMetricState.setState(curMetricState);
        persistenceMetricState.setMetricID(metricData.getMetricId());
        persistenceMetricState.setInstanceID(curInstId);
        persistenceMetricState.setCollectTime(metricData.getCollectTime());

        if(logger.isInfoEnabled()) {
            logger.info("metric state changes : " + persistenceMetricState);
        }
        stateComputeCacheUtil.setMetricState(persistenceMetricState);
        //缓存指标状态
        additions.put("persistenceMetricState", persistenceMetricState);
        //缓存实例状态
        additions.put("persistenceInstStates", persistenceInstStates);
        additions.put("historyInstanceStates", historyInstanceStates);

        if(null != currentInstanceState) {
            if(resourceInstance.getParentId() > PARENT_INSTANCE_FLAG) {
                /*
                如果当前计算的是子资源，由于子资源状态（资源状态和告警状态）并不影响主资源的资源状态，（但会影响告警状态），
                并且子资源不关心是否有可采集状态，所以将转到告警状态处理器上处理主资源的告警状态
                 */
                return StateProcessorEnum.ALARM_STATE_PROCESSOR;
            }else {
                /*
                当前计算的是主资源的资源状态，那么在正常-->致命，致命-->恢复过程中，都会对下面所有的子资源的资源状态影响，但不改变子资源原有的告警状态
                 */
                boolean isNeedChange = false;
                boolean isCritical = false;
                if(InstanceStateEnum.CRITICAL == currentState && (null == preParentResourceState || InstanceStateEnum.NORMAL == preParentResourceState)) {
                    //主资源状态：正常-->致命
                    isNeedChange = true;
                    isCritical = true;
                }else if(InstanceStateEnum.NORMAL == currentState && InstanceStateEnum.CRITICAL == preParentResourceState) {
                    //资源状态恢复：致命--> 正常
                    isNeedChange = true;
                }
                if(isNeedChange) {
                    List<ResourceInstance> childInstanceByParentId ;
                    try {
                        childInstanceByParentId = resourceInstanceService.getChildInstanceByParentId(curInstId);
                    } catch (Exception e) {
                        if(logger.isErrorEnabled()) {
                            logger.error("Query instances exception(instance id :"+ curInstId +")." + e.getMessage(), e);
                        }
                        stateComputeException.dealWithException(additions, false);
                        return null;
                    }

                    if(null != childInstanceByParentId && !childInstanceByParentId.isEmpty()) {
                        for(ResourceInstance child : childInstanceByParentId) {
                            if(InstanceLifeStateEnum.MONITORED == child.getLifeState()) {
                                synchronized (InstanceSyncUtils.getSyncObj(child.getId())) {
                                    InstanceStateData preState = stateCatchUtil.getInstanceState(child.getId());

                                    if(null == preState || preState.getCauseByInstance() == curInstId ||
                                            (isCritical && preState.getResourceState() != InstanceStateEnum.CRITICAL)) {
                                            InstanceStateData curState = new InstanceStateData(child.getId(),
                                                isCritical?InstanceStateEnum.CRITICAL:InstanceStateEnum.NORMAL,
                                                preState !=null ?preState.getAlarmState():InstanceStateEnum.NORMAL,
                                                metricData.getCollectTime(),
                                                preState !=null? preState.getCauseBymetricID():String.valueOf(curInstId),
                                                curInstId,null);

                                        if(logger.isInfoEnabled()) {
                                            logger.info("child instance changes by parent ("+curInstId+")," + curState);
                                        }
                                        stateCatchUtil.saveInstanceState(String.valueOf(child.getId()), curState);
                                        persistenceInstStates.put(curState.getInstanceID(), curState);
                                        if(preState != null)
                                            historyInstanceStates.put(preState.getInstanceID(),preState);
                                    }
                                }
                            }
                        }
                    }
                }else{
                    if(logger.isDebugEnabled()) {
                        logger.debug("inst {" + curInstId + "} don't need update child inst state.");
                    }
                }
            }

        }
        if(additions.get("availStateChanged") == Boolean.TRUE) //可用性指标单独告警，还需维护相关资源的告警状态队列
            return StateProcessorEnum.ALARM_STATE_PROCESSOR;
        return StateProcessorEnum.ALARM_EVNET_PROCESSOR;
    }

    @Override
    public StateProcessorEnum processOrder() {
        return StateProcessorEnum.INST_STATE_PROCESSOR;
    }

    @Override
    public String toString() {
        return "InstStateQueueProcessor{"+processOrder()+"}";
    }
}
