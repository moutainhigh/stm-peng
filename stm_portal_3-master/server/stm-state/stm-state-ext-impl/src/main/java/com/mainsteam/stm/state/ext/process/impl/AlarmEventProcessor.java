package com.mainsteam.stm.state.ext.process.impl;

import com.mainsteam.stm.alarm.event.AlarmEventTemplateService;
import com.mainsteam.stm.alarm.obj.*;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.metric.obj.CustomMetric;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmRuleProfileEnum;
import com.mainsteam.stm.profilelib.obj.ProfileThreshold;
import com.mainsteam.stm.state.ext.StateComputeContext;
import com.mainsteam.stm.state.ext.StateProcessorEnum;
import com.mainsteam.stm.state.ext.process.StateProcessor;
import com.mainsteam.stm.state.ext.process.bean.AlarmEventBean;
import com.mainsteam.stm.state.ext.persistence.AlarmEventPersist;
import com.mainsteam.stm.util.UnitTransformUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Xiaopf on 2017/7/17.
 * 告警时间处理接口，在这个处理器需要完成的工作有：
 * 1、可用性指标值与指标状态变化封装
 * 2、资源状态封装
 * 3、告警事件封装
 * 4、如果是主资源状态变化，正常-->致命，致命-->正常，所有的子资源状态与告警事件封装
 * 把这些都封装到一个实体类中，然后统一由一个事务处理，从业务来说，这几个操作应为原子操作
 */
@Component("alarmEventProcessor")
public class AlarmEventProcessor implements StateProcessor {

    private static final Log logger = LogFactory.getLog(AlarmEventProcessor.class);

    public static final int PARENT_INSTANCE_FLAG = 0;//parent instance flag
    public static final String CUSTOM_METRIC_PERF_FLAG = "4";
    public static final String CUSTOM_METRIC_AVAIL_FLAG = "3";
    public static final String AVAIL_METRIC_FLAG = "1";
    public static final String INFO_METRIC_FLAG = "2";
    public static final String PERF_METRIC_FLAG = "0";
    @Autowired
    private CapacityService capacityService;
    @Autowired
    @Qualifier("alarmEventPersist")
    private AlarmEventPersist alarmEventPersist;
    @Autowired
    @Qualifier("alarmEventTemplateService")
    private AlarmEventTemplateService alarmEventTemplateService;


    @Override
    public Object process(StateComputeContext context) {

        MetricCalculateData metricCalculateData = context.getMetricData();
        ResourceMetricDef metricDef = context.getMetricDef();
        CustomMetric customMetric = context.getCustomMetric();

        AlarmEventBean eventBean = new AlarmEventBean();

        Map<String, Object> additions = context.getAdditions();

        ResourceInstance instance = (ResourceInstance)additions.get("resourceInstance");

        MetricTypeEnum metricType = (MetricTypeEnum) additions.get("metricType");

        ProfileThreshold threshold = null;
        InstanceStateEnum curInstState = null;//当前资源状态
        boolean isLink = false;
        if(metricType != MetricTypeEnum.AvailabilityMetric) { //perf metric or info metric
            threshold = (ProfileThreshold) additions.get("profileThreshold");
        }
        MetricStateEnum metricStateEnum = (MetricStateEnum)additions.get("metricState");
        if(null != metricStateEnum) {
            curInstState = InstanceStateEnum.metricState2InstState(metricStateEnum);
        }
        if(null != context.getAdditions().get("isLinkCompute")) {//链路告警
            if(metricType == MetricTypeEnum.AvailabilityMetric) {
                curInstState = (InstanceStateEnum) additions.get("linkCurInstState");
            }
            isLink = true;
        }
        List<AlarmSenderParamter> alarmList = new ArrayList<>(2);
        if(null != curInstState) {
            Boolean isAlarm = (Boolean) additions.get("isAlarm");
            if(isAlarm == Boolean.TRUE) {
                AlarmSenderParamter alarmEvent = createAlarmEvent(metricCalculateData,instance,curInstState,metricDef,
                        metricType,isLink,customMetric, threshold, false, (Boolean) additions.get("hasChangedExp"),
                        (String) additions.get("preMetricData"));
                if(null != alarmEvent){
                    alarmList.add(alarmEvent);
                }
            }
            Boolean isAlarmAvail = (Boolean) additions.get("isAlarmAvail");
            if(isAlarmAvail == Boolean.TRUE) {
                AlarmSenderParamter availAlarmEvent = createAlarmEvent(metricCalculateData,instance,curInstState,metricDef,
                        metricType,isLink,customMetric, threshold, true, (Boolean) additions.get("hasChangedExp"),
                        (String) additions.get("preMetricData"));
                if(null != availAlarmEvent){
                    alarmList.add(availAlarmEvent);
                }
            }
            if(!alarmList.isEmpty())
                eventBean.setAlarmEventList(alarmList);
        }

        eventBean.setAdditions(additions);

        if(logger.isInfoEnabled()) {
            logger.info("AlarmEventBean Data:" + eventBean);
        }
        alarmEventPersist.offer(eventBean);
        return null;
    }

    private AlarmSenderParamter createAlarmEvent(MetricCalculateData metricCalculateData, ResourceInstance instance, InstanceStateEnum instanceStateEnum,
                                        ResourceMetricDef metricDef, MetricTypeEnum metricTypeEnum, boolean isLink, CustomMetric customMetric,
                                        ProfileThreshold threshold, boolean isAlarmAvail, Boolean hasChangedExp, String preMetricData) {
        AlarmSenderParamter alarmEvent = new AlarmSenderParamter();
        alarmEvent.setProvider(AlarmProviderEnum.OC4);
        if(isLink)
            alarmEvent.setSysID(SysModuleEnum.LINK);
        else
            alarmEvent.setSysID(SysModuleEnum.MONITOR);

        alarmEvent.setProfileID(metricCalculateData.getProfileId());
        alarmEvent.setSourceID(String.valueOf(instance.getId()));
        alarmEvent.setGenerateTime(metricCalculateData.getCollectTime());
        alarmEvent.setLevel(instanceStateEnum);
        alarmEvent.setRuleType(AlarmRuleProfileEnum.model_profile);

        if(metricTypeEnum == MetricTypeEnum.AvailabilityMetric && !isAlarmAvail) {
            alarmEvent.setRecoverKeyValue(new String[]{"instanceState_", String.valueOf(instance.getId())});
        }else {
            alarmEvent.setRecoverKeyValue(new String[]{String.valueOf(instance.getId()), metricCalculateData.getMetricId()});
        }
        ResourceDef resourceDef ;
        String resourceType ;
        if(null != metricDef) {
            resourceDef = metricDef.getResourceDef();
            resourceType = resourceDef.getName();
            if(null != resourceDef && null == resourceDef.getCategory()) {
                resourceDef = resourceDef.getParentResourceDef();
            }
        }else {
            resourceDef = capacityService.getResourceDefById(metricCalculateData.getResourceId());
            resourceType = resourceDef.getName();
        }
        if(null != resourceDef) {
            alarmEvent.setExt0(resourceDef.getId()); // resource id
            alarmEvent.setExt1(resourceDef.getCategory().getId()); //category id
            alarmEvent.setExt2(resourceDef.getCategory().getParentCategory().getId()); // parent category id
        }else {
            if(logger.isInfoEnabled()) {
                logger.info("fail to query resource while generating alarm event:" + metricCalculateData.getResourceInstanceId()
                        + "/" + metricCalculateData.getMetricId());
            }
        }
        alarmEvent.setExt3(metricCalculateData.getMetricId()); // metric id
        if(instance.getParentId() > PARENT_INSTANCE_FLAG){
            ResourceInstance parentIns = instance.getParentInstance();
            alarmEvent.setSourceIP(parentIns.getShowIP());
            alarmEvent.setSourceName(parentIns!=null?(StringUtils.isEmpty(parentIns.getShowName())?parentIns.getName():parentIns.getShowName()):null);
            alarmEvent.setExt8(String.valueOf(instance.getParentId()));
        }else{
            alarmEvent.setSourceName(instance!=null?(StringUtils.isEmpty(instance.getShowName())?instance.getName():instance.getShowName()):null);
            alarmEvent.setSourceIP(instance.getShowIP());
            alarmEvent.setExt8(String.valueOf(instance.getId()));
        }
        //策略ID主要用于查询告警规则，不需要考虑基线，因为基线对应着策略
        alarmEvent.setProfileID(metricCalculateData.getProfileId());

        if(null != customMetric) {
            alarmEvent.setExt6(customMetric.getCustomMetricInfo().getStyle() == MetricTypeEnum.AvailabilityMetric?CUSTOM_METRIC_AVAIL_FLAG:CUSTOM_METRIC_PERF_FLAG);
        }else {
            alarmEvent.setExt6(metricTypeEnum == MetricTypeEnum.AvailabilityMetric ? AVAIL_METRIC_FLAG :
                    (metricTypeEnum == MetricTypeEnum.PerformanceMetric ? PERF_METRIC_FLAG : INFO_METRIC_FLAG));
        }
        String metricData = null;
        String[] metricDataArray = metricCalculateData.getMetricData();
        if(metricDataArray !=null && metricDataArray.length > 0)
            metricData = metricDataArray[0];
        alarmEvent.setDefaultMsg(getContent(isLink,isAlarmAvail,instanceStateEnum,metricDef,customMetric,instance, resourceType,
                metricData, threshold, metricTypeEnum, metricCalculateData.getCollectTime(), hasChangedExp, preMetricData));

        alarmEvent.setDefaultMsgTitle(alarmEvent.getDefaultMsg());

        return alarmEvent;
    }

    private String getContent(boolean isLink, boolean isAvailAlarm, InstanceStateEnum instanceStateEnum, ResourceMetricDef metricDef,
                              CustomMetric customMetric, ResourceInstance instance, String resourceType,
                              String metricData, ProfileThreshold threshold, MetricTypeEnum metricTypeEnum, Date collectionTime,
                              Boolean hasChangedExp, String preMetricData) {
        String key = null;
        if(threshold != null) {
            key = threshold.getAlarmTemplate();
        }
        String contentTemplate = null;
        boolean isMainProfile = instance.getParentId() > 0 ? false:true;
        boolean isReplace = true;
        if(StringUtils.isNotBlank(key)) {
            AlarmEventTemplate alarmEventTemplate = new AlarmEventTemplate();
            alarmEventTemplate.setUniqueKey(key);
            AlarmEventTemplate templateWithoutDefault = alarmEventTemplateService.getTemplateWithoutDefault(alarmEventTemplate);
            Map<InstanceStateEnum, String> content = templateWithoutDefault.getContent();
            if(content !=null){
                contentTemplate = content.get(instanceStateEnum);
            }
        }else {
            if(StringUtils.equals(instance.getResourceId(), "CameraRes")){
                contentTemplate = alarmEventTemplateService.getDefaultTemplateContent("CameraRes", "MONITOR", metricTypeEnum, instanceStateEnum);
                if(instanceStateEnum != InstanceStateEnum.NORMAL)
                    contentTemplate = StringUtils.replace(contentTemplate, "${metricValue}", metricData);
                isReplace = false;
            }else{
                SysModuleEnum moduleEnum = isLink? SysModuleEnum.LINK : SysModuleEnum.MONITOR;
                String alarmTemplateMetricId = isAvailAlarm?"AVAIL":moduleEnum.name(); //告警模板中关联的指标ID（实际是模块类型，除了可用性指标外）
                contentTemplate = alarmEventTemplateService.getDefaultTemplateContent(isMainProfile,alarmTemplateMetricId,metricTypeEnum,instanceStateEnum);
            }
        }
        if(logger.isDebugEnabled()) {
            logger.debug("find alarm event template using key : " + key + ":" + contentTemplate);
        }
        if(contentTemplate == null) {
            throw new RuntimeException("alarm template is missing {" + instance.getId() +"/" + metricDef + "/"+ instanceStateEnum + "}");
        }
        if(isReplace) {
            String[] searchList;
            String[] replacementList;
            String showName = StringUtils.isEmpty(instance.getShowName())?instance.getName():instance.getShowName();
            if(metricTypeEnum == MetricTypeEnum.AvailabilityMetric) {
                String metricName =null;
                if(isAvailAlarm) {
                    metricName = customMetric != null ?customMetric.getCustomMetricInfo().getName():metricDef.getDescription();
                }
                if(isMainProfile){
                    if(isAvailAlarm) { //主资源可用性告警模板
                        searchList = new String[]{"${metricName}"};
                        replacementList = new String[]{metricName};
                    }else{
                        searchList = new String[]{"${resourceName}"};
                        replacementList = new String[]{showName};
                    }
                }else{
                    if(isAvailAlarm || instanceStateEnum != InstanceStateEnum.NORMAL) { //子资源可用性指标告警模板
                        searchList = new String[]{"${resourceType}","${resourceName}", "${metricName}"};
                        replacementList = new String[]{resourceType, showName, metricName};
                    }else{
                        searchList = new String[]{"${resourceType}","${resourceName}"};
                        replacementList = new String[]{resourceType, showName};
                    }
                }
            }else {
                AlarmEventTemplateEnum[] values = AlarmEventTemplateEnum.values();
                searchList = new String[values.length-1]; //过滤上次采集值枚举，只有has changed表达式才有这个替换
                for (int i = 0; i < values.length-1; i++) {
                    searchList[i] = "${"+values[i].getKey()+"}";
                }
                String metricName = customMetric != null ?customMetric.getCustomMetricInfo().getName():metricDef.getDescription();
                String alarmTime = "";
                if(contentTemplate.contains("${alarmTime}")){
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    alarmTime = simpleDateFormat.format(collectionTime);
                }
                String value = UnitTransformUtil.transform(metricData, customMetric != null ?customMetric.getCustomMetricInfo().getUnit() : metricDef.getUnit());
                replacementList = new String[]{showName,resourceType,metricName,value,alarmTime,
                        InstanceStateEnum.getValue(instanceStateEnum)};
            }
            contentTemplate = StringUtils.replaceEach(contentTemplate, searchList, replacementList);
        }

        //针对是否变化表达式告警
        if(hasChangedExp == Boolean.TRUE) {
            contentTemplate = StringUtils.replace(contentTemplate, "${preMetricValue}", preMetricData);
        }

        return contentTemplate;
    }

    @Override
    public StateProcessorEnum processOrder() {
        return StateProcessorEnum.ALARM_EVNET_PROCESSOR;
    }

    @Override
    public String toString() {
        return "AlarmEventProcessor{"+processOrder()+"}";
    }
}
