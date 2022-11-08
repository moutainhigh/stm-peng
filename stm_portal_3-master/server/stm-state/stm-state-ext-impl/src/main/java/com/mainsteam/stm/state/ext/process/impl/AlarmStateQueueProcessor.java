package com.mainsteam.stm.state.ext.process.impl;

import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.common.instance.obj.CollectStateEnum;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.dataprocess.InstanceSyncUtils;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.state.ext.StateComputeContext;
import com.mainsteam.stm.state.ext.StateProcessorEnum;
import com.mainsteam.stm.state.ext.process.StateProcessor;
import com.mainsteam.stm.state.ext.process.bean.AlarmStateQueue;
import com.mainsteam.stm.state.ext.tools.StateCatchUtil;
import com.mainsteam.stm.state.ext.process.bean.CompareInstanceState;
import com.mainsteam.stm.state.obj.InstanceStateData;
import com.mainsteam.stm.state.obj.MetricStateData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by Xiaopf on 2017/7/25.
 * 因为每个资源实例都维护这着一个告警状态的优先级队列，所以需要在Spring容器启动成功之后，对这些优先级队列先进行缓存预热，
 * 避免在第一次计算中读取数据库操作，然后再放入缓存（实际上这是一个很耗时的操作，因为必须读取该资源下面所有的指标状态数据）。
 */
@Component("alarmStateQueueProcessor")
public class AlarmStateQueueProcessor implements StateProcessor,ApplicationListener<ContextRefreshedEvent> {

    private static final Log logger = LogFactory.getLog(AlarmStateQueueProcessor.class);

    public static final int PARENT_INSTANCE_FLAG = 0;
    @Autowired
    private StateCatchUtil stateCatchUtil;
    @Autowired
    private ResourceInstanceService resourceInstanceService;

    @Override
    public Object process(StateComputeContext context) {
        Map<String, Object> additions = context.getAdditions();
        MetricTypeEnum metricType = (MetricTypeEnum) additions.get("metricType");
        ResourceInstance resourceInstance = (ResourceInstance) additions.get("resourceInstance");
        MetricStateEnum currentMetricState = (MetricStateEnum) additions.get("metricState");
        MetricStateData preMetricStateData = (MetricStateData) additions.get("preMetricStateData");
        MetricCalculateData metricData = context.getMetricData();
        boolean isMetricStateChanged = true;
        if(additions.containsKey("skipMetricFlapping")){ //只有跳过Flapping计算的指标才存在指标状态不变也能进入到这个执行流程
            if(preMetricStateData != null && preMetricStateData.getState() == currentMetricState)
                isMetricStateChanged = false;

        }
        if(MetricTypeEnum.AvailabilityMetric != metricType) {
            /*
            1.对于子资源的性能指标或者信息指标计算而来的告警状态而言，不仅会影响子资源自身的告警状态，也有可能影响主资源的告警状态；
            2.对于主资源而言，则相对比较简单，它仅仅影响自身的状态
             */
            //临时保存的资源实例状态，在后续的持久化过程中，如果出现异常，则需要回退到上一次的值
            if(!additions.containsKey("isThirdPartyState") && isMetricStateChanged) { //第三方告警不需要保存状态

                MetricStateData metricStateData = new MetricStateData();
                metricStateData.setCollectTime(metricData.getCollectTime());
                metricStateData.setInstanceID(resourceInstance.getId());
                metricStateData.setMetricID(metricData.getMetricId());
                metricStateData.setState(currentMetricState);
                metricStateData.setType(metricType);
                stateCatchUtil.saveMetricState(metricStateData);
                if(logger.isInfoEnabled()) {
                    logger.info("store metric state " + metricStateData);
                }
                additions.put("persistenceMetricState", metricStateData);
            }
            if(!additions.containsKey("isAlarm")) {
                if((null == preMetricStateData && currentMetricState != MetricStateEnum.NORMAL) ||
                        (null !=preMetricStateData && preMetricStateData.getState() != currentMetricState)){
                    additions.put("isAlarm", Boolean.TRUE);
                }
            }

        }
        if(isMetricStateChanged) { //只有指标状态发生变化才需要计算告警状态
            String curInstStateIdStr = resourceInstance.getId() + "_" + metricData.getMetricId();
            CompareInstanceState cur = new CompareInstanceState(curInstStateIdStr, currentMetricState);;
            CompareInstanceState pre = null;
            if(preMetricStateData != null){
                pre = new CompareInstanceState(curInstStateIdStr, preMetricStateData.getState());
            }
            computeAlarmState(resourceInstance.getId(), context, cur, pre);
            //如果当前计算的是子资源，那么还需要在计算主资源的告警状态
            if(resourceInstance.getParentId() > PARENT_INSTANCE_FLAG) {
                computeAlarmState(resourceInstance.getParentId(), context, cur, pre);
            }

        }
        return StateProcessorEnum.ALARM_EVNET_PROCESSOR;
    }

    /**
     * 告警状态计算方法
     * @param computeInstanceId 当前计算告警状态的资源实例
     * @param context 处理器执行上下文
     * @param cur 当前告警状态
     * @param pre 最近一次的告警状态
     */
    private void computeAlarmState(long computeInstanceId, StateComputeContext context, CompareInstanceState cur, CompareInstanceState pre) {
        Map<String, Object> additions = context.getAdditions();
        MetricCalculateData metricData = context.getMetricData();
        String computeInstanceIdStr = String.valueOf(computeInstanceId);
        synchronized (InstanceSyncUtils.getSyncObj(computeInstanceId)) {
            InstanceStateData preInstState = stateCatchUtil.getInstanceState(computeInstanceId);
            InstanceStateData curInstState = null;
            if(null == preInstState) { //如果此时主资源还未有状态，那么需要先添加状态
                //如果子资源的资源状态为致命，那么对应于主资源的告警状态也为致命，只是引起资源状态变化的实例为子资源实例
                curInstState = new InstanceStateData(computeInstanceId, InstanceStateEnum.NORMAL,
                        InstanceStateEnum.metricState2InstState(cur.getAlarmState()), metricData.getCollectTime(), cur.getId(), computeInstanceId,
                        CollectStateEnum.COLLECTIBLE);
                if(cur.getAlarmState() != MetricStateEnum.NORMAL) {
                    if(logger.isInfoEnabled()) {
                        logger.info("inst{" + computeInstanceId + "} offer item :" + cur);
                    }
                    this.stateCatchUtil.setAlarmStateQueue(computeInstanceIdStr, cur);
                }
            }else {
                AlarmStateQueue alarmStateQueue = this.stateCatchUtil.getAlarmStateQueue(computeInstanceIdStr);
                MetricStateEnum alarmState = cur.getAlarmState();
                String causeId = cur.getId();
                if(null != alarmStateQueue) {
                    if(alarmState == MetricStateEnum.NORMAL){
                        if(logger.isInfoEnabled()){
                            logger.info("inst{" + computeInstanceId + "} remove item:" + pre);
                        }
                        alarmStateQueue.remove(pre);
                    }else{
                        if(logger.isInfoEnabled()) {
                            logger.info("inst{" + computeInstanceId + "} replace item :" + pre + " to " + cur);
                        }
                        alarmStateQueue.replace(pre, cur);
                    }
                    CompareInstanceState peek = alarmStateQueue.peek();
                    if(null != peek) {
                        alarmState = peek.getAlarmState();
                        causeId = peek.getId();
                    }
                    this.stateCatchUtil.setAlarmStateQueue(computeInstanceIdStr, alarmStateQueue);
                }else if(alarmState != MetricStateEnum.NORMAL){
                    if(logger.isInfoEnabled()) {
                        logger.info("inst{" + computeInstanceId + "} insert item:" + cur);
                    }
                    this.stateCatchUtil.setAlarmStateQueue(computeInstanceIdStr, cur);
                }
                InstanceStateEnum alarmState2InstState = InstanceStateEnum.metricState2InstState(alarmState);
                if(alarmState2InstState != preInstState.getAlarmState()) {
                    curInstState = new InstanceStateData(preInstState.getInstanceID(),preInstState.getResourceState(),
                            alarmState2InstState, metricData.getCollectTime(),causeId, preInstState.getCauseByInstance(),
                            preInstState.getCollectStateEnum());
                }

            }
            if(null != curInstState) {
                //先存入缓存，持久化数据后面存入
                if(logger.isInfoEnabled()) {
                    logger.info("save inst{"+computeInstanceIdStr+"} alarm state" + curInstState);
                }
                stateCatchUtil.saveInstanceState(String.valueOf(curInstState.getInstanceID()), curInstState);
                Map<Long, InstanceStateData> persistenceInstStates = (Map<Long, InstanceStateData>) additions.get("persistenceInstStates");
                if(null == persistenceInstStates){
                    persistenceInstStates = new HashMap<>(2);
                    additions.put("persistenceInstStates", persistenceInstStates);
                }
                persistenceInstStates.put(curInstState.getInstanceID(), curInstState);
                if(null != preInstState){
                    Map<Long, InstanceStateData> historyInstanceStates = (Map<Long, InstanceStateData>) additions.get("historyInstanceStates");
                    if(historyInstanceStates == null){
                        historyInstanceStates = new HashMap<>(2);
                        additions.put("historyInstanceStates", historyInstanceStates);
                    }
                    historyInstanceStates.put(preInstState.getInstanceID(), preInstState);
                }
            }
        }
    }

    @Override
    public StateProcessorEnum processOrder() {
        return StateProcessorEnum.ALARM_STATE_PROCESSOR;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent applicationContext) {
        if(applicationContext.getApplicationContext().getParent() == null) {
            try {
                initAlarmStateQueue();
            } catch (InstancelibException e) {
                if(logger.isErrorEnabled()) {
                    logger.error(e.getMessage(), e);
                }
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 读取缓存数据，主要为了构造资源的告警状态优先级队列，方法如下：
     * 读取所有的资源实例，只取已监控的资源实例，根据资源实例的告警状态来判断是否需要去查询指标状态来构造告警状态队列：
     * a)如果资源实例的告警状态为Normal，那么优先级队列为空集合即可；
     * b)其他则需要遍历所有的指标状态，另外，causeMetric对应的指标ID则不需要查询
     */
    private void initAlarmStateQueue() throws InstancelibException {
        //1.先获取所有的主资源
        List<ResourceInstance> allParentInstance = resourceInstanceService.getAllParentInstance();
        for(ResourceInstance resourceInstance : allParentInstance) {
            if(resourceInstance.getLifeState() == InstanceLifeStateEnum.MONITORED) {//只将已监控的资源加入缓存
                AlarmStateQueue alarmStateQueue = stateCatchUtil.getAlarmStateQueue(String.valueOf(resourceInstance.getId()), resourceInstance);
                checkout(resourceInstance.getId(), alarmStateQueue);
                stateCatchUtil.getAvailabilityMetricState(resourceInstance.getId());
                List<ResourceInstance> childInstanceByParentId = resourceInstanceService.getChildInstanceByParentId(resourceInstance.getId());
                if(null != childInstanceByParentId) {
                    for (ResourceInstance child : childInstanceByParentId) {
                        if(child.getLifeState() == InstanceLifeStateEnum.MONITORED)
                            stateCatchUtil.getAvailabilityMetricState(child.getId());

                        checkout(child.getId(), null);
                    }
                }
            }
        }
    }

    private void checkout(long instanceId, AlarmStateQueue alarmStateQueue) {
        InstanceStateData instanceState = stateCatchUtil.getInstanceState(instanceId);
        if(alarmStateQueue == null)
            alarmStateQueue = stateCatchUtil.getAlarmStateQueue(String.valueOf(instanceId));
        if(instanceState != null && alarmStateQueue !=null && !alarmStateQueue.isEmpty()) {
            /*
            告警状态容错，每次启动时，对比一下InstanceStateData里面的告警状态和实际计算的告警状态是否一致，如果不一致，
            则按实际计算值更新告警状态
             */
            CompareInstanceState peek = alarmStateQueue.peek();
            InstanceStateEnum realAlarmState = InstanceStateEnum.metricState2InstState(peek.getAlarmState());
            if(instanceState.getAlarmState() != realAlarmState){
                if(logger.isInfoEnabled())
                    logger.info("inst{" + instanceId + "} alarm state checkout :" + peek);
                instanceState.setAlarmState(realAlarmState);
                instanceState.setCauseBymetricID(peek.getId());
                instanceState.setCollectTime(new Date());
                stateCatchUtil.saveInstanceState(instanceState);
            }

        }
    }

    @Override
    public String toString() {
        return "AlarmStateQueueProcessor{"+processOrder()+"}";
    }
}
