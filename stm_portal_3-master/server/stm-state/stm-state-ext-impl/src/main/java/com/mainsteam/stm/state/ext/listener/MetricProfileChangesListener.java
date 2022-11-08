package com.mainsteam.stm.state.ext.listener;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.alarm.event.AlarmEventService;
import com.mainsteam.stm.alarm.notify.AlarmNotifyService;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.common.metric.sync.MetricProfileChange;
import com.mainsteam.stm.common.sync.DataSyncPO;
import com.mainsteam.stm.common.sync.DataSyncTypeEnum;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.metric.CustomMetricService;
import com.mainsteam.stm.metric.exception.CustomMetricException;
import com.mainsteam.stm.metric.obj.CustomMetric;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.ProfileInfo;
import com.mainsteam.stm.state.ext.StateComputeContext;
import com.mainsteam.stm.state.ext.StateProcessorEnum;
import com.mainsteam.stm.state.ext.process.StateProcessor;
import com.mainsteam.stm.state.ext.tools.StateCatchUtil;
import com.mainsteam.stm.state.obj.InstanceStateData;
import com.mainsteam.stm.state.obj.MetricStateData;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 指标策略变化监听器
 */
@Component("metricProfileChangesListener")
public class MetricProfileChangesListener extends AbstractProfileChangesListener {

    private static final Log logger = LogFactory.getLog(MetricProfileChangesListener.class);

    @Autowired
    private ProfileService profileService;
    @Autowired
    private CapacityService capacityService;
    @Autowired
    @Qualifier("instStateQueueProcessor")
    private StateProcessor instStateQueueProcessor;
    @Autowired
    @Qualifier("alarmStateQueueProcessor")
    private StateProcessor alarmStateQueueProcessor;
    @Autowired
    @Qualifier("alarmEventProcessor")
    private StateProcessor alarmEventProcessor;
    @Autowired
    private ResourceInstanceService resourceInstanceService;
    @Autowired
    private StateCatchUtil stateCatchUtil;
    @Autowired
    private AlarmNotifyService alarmNotifyService;
    @Autowired
    private AlarmEventService alarmEventService;
    @Autowired
    private CustomMetricService customMetricService;

    void process(MetricProfileChange profileChange) {
        /*
        处理逻辑如下：当指标加入监控或者加入告警，则直接删除stm_data_sync里面的表记录即可。指标按照原有的采集逻辑开始计算；如果指标取消告警或者监控，
        则当前指标状态默认为恢复正常（Normal），然后计算告警状态（因为目前只有性能指标可取消告警，如果可用性指标放开策略变化，则需要加入逻辑计算）。
         */
        Boolean isAlarm = profileChange.getIsAlarm();
        Boolean isMonitor = profileChange.getIsMonitor();
        if(logger.isInfoEnabled()) {
            logger.info("metric profile changes : " + profileChange);
        }
        if(isAlarm == Boolean.FALSE || isMonitor == Boolean.FALSE) {
            MetricStateData preMetricState ;
            ResourceMetricDef resourceMetricDef = null;
            String resourceId ;
            ResourceInstance resourceInstance = null;
            if(profileChange.getProfileID() != 0L) {
                ProfileInfo pf;
                try {
                    pf = profileService.getProfileBasicInfoByProfileId(profileChange.getProfileID());
                } catch (ProfilelibException e) {
                    if(logger.isWarnEnabled()) {
                        logger.warn(e.getMessage(), e);
                    }
                    return;
                }
                if (null == pf) {
                    logger.error("Profile[" + profileChange.getProfileID() + "," + profileChange.getMetricID() + "] can't find.exit!");
                    return;
                }
                resourceId = pf.getResourceId();

            }else {
                if(profileChange.getInstanceId() != 0L) {
                    preMetricState =stateCatchUtil.getMetricState(profileChange.getInstanceId(), profileChange.getMetricID());
                    if(null == preMetricState || preMetricState.getState() == MetricStateEnum.NORMAL) {
                        if(logger.isInfoEnabled()) {
                            logger.info("metric{" + profileChange.getInstanceId() + "/" + profileChange.getMetricID() +
                                    "} state is null or normal,so don't compute,when metric cancels to monitor or alarm.");
                        }
                        return;
                    }
                    try {
                        resourceInstance = resourceInstanceService.getResourceInstance(profileChange.getInstanceId());
                    } catch (InstancelibException e) {
                        if(logger.isWarnEnabled()) {
                            logger.warn("instance query failed("+profileChange.getInstanceId()+")," + e.getMessage(), e);
                        }
                        return;
                    }
                    resourceId = resourceInstance.getResourceId();
                }else {
                    if(logger.isWarnEnabled()) {
                        logger.warn("missing profile and instance while metric cancels monitoring or alarm :" + profileChange);
                    }
                    return;
                }
            }
            CustomMetric customMetric = null;
            if(Boolean.TRUE == profileChange.getCustomMetric()) {
                logger.info("query customized metric[" + resourceId + "," + profileChange.getMetricID() + "].");
                //查询自定义指标
                try {
                    customMetric = customMetricService.getCustomMetric(profileChange.getMetricID());
                } catch (CustomMetricException e) {
                    if(logger.isWarnEnabled()) {
                        logger.warn("query customized metric failed("+profileChange.getMetricID()+"),"+ e.getMessage(), e);
                    }
                }
            }else{
                resourceMetricDef = capacityService.getResourceMetricDef(resourceId, profileChange.getMetricID());
            }

            if (null == resourceMetricDef && null == customMetric) {
                if(logger.isWarnEnabled()) {
                    logger.warn("can not find resource metric or custom metric [" + profileChange + "]");
                }
                return;
            }

            List<Long> resourceInstanceByProfileId ;
            if(profileChange.getInstanceId() == 0L) {
                try {
                    resourceInstanceByProfileId = profileService.getResourceInstanceByProfileId(profileChange.getProfileID());
                    if (null == resourceInstanceByProfileId || resourceInstanceByProfileId.isEmpty()) {
                        logger.error("can't find instance ides from profile[" + profileChange.getProfileID() + "],please check!");
                        return;
                    }
                } catch (ProfilelibException e) {
                    if(logger.isWarnEnabled()) {
                        logger.warn(e.getMessage(), e);
                    }
                    return;
                }

            }else {
                resourceInstanceByProfileId = new ArrayList<>(1);
                resourceInstanceByProfileId.add(profileChange.getInstanceId());
            }

            for(Long instanceId : resourceInstanceByProfileId) {
                preMetricState = stateCatchUtil.getMetricState(instanceId, profileChange.getMetricID());
                if(null == preMetricState || preMetricState.getState() == MetricStateEnum.NORMAL) {
                    if(logger.isInfoEnabled()) {
                        logger.info("metric{" + instanceId + "/" + profileChange.getMetricID() +
                                "} state is null or normal,so don't compute,when metric cancels to monitor or alarm.");
                    }
                    continue;
                }
                StateComputeContext context = new StateComputeContext();
                if(logger.isInfoEnabled()) {
                    logger.info("starts to compute inst state while metric canceled (" + instanceId + "/" + profileChange.getMetricID() + ")");
                }
                Map<String, Object> additions = new HashMap<>(10);
                try {
                    if(null == resourceInstance || resourceInstance.getId() != instanceId){
                        resourceInstance = resourceInstanceService.getResourceInstance(instanceId);
                    }
                    additions.put("resourceInstance", resourceInstance);
                } catch (InstancelibException e) {
                    if(logger.isWarnEnabled()) {
                        logger.warn("instance query failed("+instanceId+")," + e.getMessage(), e);
                    }
                    return;
                }
                MetricTypeEnum metricType = (resourceMetricDef != null ? resourceMetricDef.getMetricType() : customMetric.getCustomMetricInfo().getStyle());
                additions.put("metricType", metricType);
                additions.put("metricState", MetricStateEnum.NORMAL);
                additions.put("preMetricStateData", preMetricState);
                context.setAdditions(additions);

                MetricCalculateData metricData = new MetricCalculateData();
                metricData.setCollectTime(new Date());
                metricData.setMetricId(profileChange.getMetricID());
                metricData.setResourceInstanceId(instanceId);
                metricData.setProfileId(profileChange.getProfileID());
                context.setMetricData(metricData);

                context.setMetricDef(resourceMetricDef);
                context.setCustomMetric(customMetric);

                List<String> recoveryKeyList = new ArrayList<>(2);
                if(metricType == MetricTypeEnum.AvailabilityMetric) {
                    //可用性指标先计算资源状态，然后再计算告警状态，最后计算告警信息
                    //可用性指标状态恢复正常时，值可能不是1，这块主要是为了计算可采集状态，资源状态依赖指标状态而来
                    additions.put("availMetricValue", "1");//1表示正常
                    additions.put("availStateChanged", Boolean.TRUE);// 标志可用性指标状态已变化
                    StateProcessorEnum stateProcessorEnum = (StateProcessorEnum) instStateQueueProcessor.process(context);
                    //由于可用性指标和资源状态告警分离，所以当取消指标监控时，还需考虑相应的恢复相应的指标告警
                    if(Boolean.TRUE != profileChange.getAlarmConfirm()){
                        //存在多个可用性指标的时候，可用性指标单独告警，所以在取消某个可用性指标的时候，需要删除其缓存的指标状态值
                        additions.put("removeAvailMetricState", Boolean.TRUE);
                        List<InstanceStateData> persistenceInstStates = (List<InstanceStateData>) context.getAdditions().get("persistenceInstStates");
                        Boolean isAlarmAvail = (Boolean) context.getAdditions().get("isAlarmAvail");
                        if(null != persistenceInstStates) {
                            for (InstanceStateData currentInstState : persistenceInstStates) {
                                if(currentInstState.getInstanceID() == instanceId) {
                                    if(currentInstState.getResourceState() == InstanceStateEnum.NORMAL) { //状态已恢复
                                        recoveryKeyList.add(StringUtils.join(new String[] {"instanceState_", String.valueOf(instanceId) }, "_"));
                                    }
                                    break;
                                }
                            }
                        }
                        if(Boolean.TRUE == isAlarmAvail){ //需恢复可用性指标告警
                            recoveryKeyList.add(StringUtils.join(new String[] {String.valueOf(instanceId), profileChange.getMetricID()}, "_"));
                            context.getAdditions().remove("isAlarmAvail");//需删除该标识，否则可用性指标恢复出现重复
                        }

                    }
                    if(stateProcessorEnum == StateProcessorEnum.ALARM_STATE_PROCESSOR)
                        alarmStateQueueProcessor.process(context);
                }else {
                    //性能指标或者信息指标直接计算告警状态，然后再计算告警信息
                    alarmStateQueueProcessor.process(context);
                    if(Boolean.TRUE != profileChange.getAlarmConfirm())
                        recoveryKeyList.add(StringUtils.join(new String[] {String.valueOf(instanceId), profileChange.getMetricID() }, "_"));
                }
                if(Boolean.TRUE != profileChange.getAlarmConfirm()) //告警确认时，由于可能存在多个可用性指标，确认其中一个告警，可能引起资源状态的恢复
                    additions.put("isAlarm", Boolean.FALSE);
                alarmEventProcessor.process(context);
                if(!recoveryKeyList.isEmpty()) { //告警确认无需处理告警信息
                    //告警信息单独处理
                    if (logger.isInfoEnabled()) {
                        logger.info("Clean Alarm with the recoveryKey : " + recoveryKeyList);
                    }
                    for (String recoveryKey: recoveryKeyList) {
                        //alarmEventService.recoverAlarmEventByRecoverKey(recoveryKey, HandleType.DELETE);
                        alarmEventService.deleteAlarmEventByRecoveryKey(recoveryKey, SysModuleEnum.MONITOR);
                        alarmNotifyService.deleteAlarmNotifyWaitByRecoveryKey(recoveryKey);
                    }
                }
            }
        }

    }

    @Override
    public void process(DataSyncPO dataSyncPO) throws Exception {
        MetricProfileChange metricProfileChange = JSON.parseObject(dataSyncPO.getData(), MetricProfileChange.class);
        process(metricProfileChange);
    }

    @Override
    public DataSyncTypeEnum get() {
        return DataSyncTypeEnum.METRIC_STATE;
    }

}
