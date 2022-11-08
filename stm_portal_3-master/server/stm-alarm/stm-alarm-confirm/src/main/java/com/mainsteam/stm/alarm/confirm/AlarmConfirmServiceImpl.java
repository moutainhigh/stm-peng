package com.mainsteam.stm.alarm.confirm;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.alarm.cache.AlarmConfirmCacheUtils;
import com.mainsteam.stm.alarm.confirm.dao.AlarmConfirmDAO;
import com.mainsteam.stm.alarm.event.AlarmEventService;
import com.mainsteam.stm.alarm.event.AlarmEventTemplateService;
import com.mainsteam.stm.alarm.notify.AlarmNotifyService;
import com.mainsteam.stm.alarm.obj.*;
import com.mainsteam.stm.alarm.po.AlarmEventPO;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.sync.DataSyncPO;
import com.mainsteam.stm.common.sync.DataSyncService;
import com.mainsteam.stm.common.sync.DataSyncTypeEnum;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.platform.system.config.bean.SystemConfigBo;
import com.mainsteam.stm.platform.system.config.bean.SystemConfigConstantEnum;
import com.mainsteam.stm.platform.system.config.service.ISystemConfigApi;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Xiaopf on 2016/5/12.
 */
public class AlarmConfirmServiceImpl implements AlarmConfirmService, InitializingBean {

    private static final Log logger= LogFactory.getLog(AlarmConfirmServiceImpl.class);
    private static final String SUCCESS = "success";
    private static final String DONE = "done";
    private static final String FAILED = "failed";

    private AlarmConfirmDAO alarmConfirmDAO;
    private ISequence sequence;
    private AlarmEventService alarmEventService;
    private AlarmNotifyService alarmNotifyService;
    private ISystemConfigApi systemConfigApi;
    private AlarmConfirmCacheUtils alarmConfirmCacheUtils;
    private DataSyncService dataSyncService;
    private AlarmEventTemplateService alarmEventTemplateService;
    private CapacityService capacityService;

    private final LinkedBlockingQueue<AlarmConfirm> alarmConfirmQueue = new LinkedBlockingQueue<>();

    public void setAlarmConfirmDAO(AlarmConfirmDAO alarmConfirmDAO) {
        this.alarmConfirmDAO = alarmConfirmDAO;
    }

    public void setSequence(ISequence sequence) {
        this.sequence = sequence;
    }

    public void setAlarmEventService(AlarmEventService alarmEventService) {
        this.alarmEventService = alarmEventService;
    }

    public void setSystemConfigApi(ISystemConfigApi systemConfigApi) {
        this.systemConfigApi = systemConfigApi;
    }

    public void setAlarmNotifyService(AlarmNotifyService alarmNotifyService) {
        this.alarmNotifyService = alarmNotifyService;
    }

    public void setAlarmConfirmCacheUtils(AlarmConfirmCacheUtils alarmConfirmCacheUtils) {
        this.alarmConfirmCacheUtils = alarmConfirmCacheUtils;
    }

    public void setDataSyncService(DataSyncService dataSyncService) {
        this.dataSyncService = dataSyncService;
    }

    public void setAlarmEventTemplateService(AlarmEventTemplateService alarmEventTemplateService) {
        this.alarmEventTemplateService = alarmEventTemplateService;
    }

    public void setCapacityService(CapacityService capacityService) {
        this.capacityService = capacityService;
    }

    @Override
    public int addAlarmConfirm(AlarmConfirm alarmConfirm) {
        alarmConfirm.setConfirmId(sequence.next());
        if(logger.isDebugEnabled()) {
            logger.debug("Starts to add alarm confirm cache " + alarmConfirm);
        }
        alarmConfirmCacheUtils.setAlarmConfirmCache(alarmConfirm);
        return alarmConfirmDAO.addAlarmConfirm(alarmConfirm);
    }

    @Override
    public int deleteAlarmConfirm(AlarmConfirm alarmConfirm) {
        if(logger.isDebugEnabled()) {
            logger.debug("starts to remove alarm confirm cache " + alarmConfirm);
        }
        alarmConfirmCacheUtils.removeAlarmConfirmCache(alarmConfirm);
        return alarmConfirmDAO.deleteAlarmConfirm(alarmConfirm);
    }

    @Override
    public AlarmConfirm findAlarmConfirm(AlarmConfirm alarmConfirm) {
        AlarmConfirm result = alarmConfirmCacheUtils.getAlarmConfirmCache(alarmConfirm);
        if(result !=null) {
            if(logger.isDebugEnabled()) {
                logger.debug("find alarm confirm cache " + alarmConfirm);
            }
        }
        return result;
    }

    /**
     *
     * @param alarmID 告警信息ID
     * @param instanceID 资源实例ID，告警信息关联的实例ID
     * @param metricId 指标ID，如果是如果是第三方告警，则为null，其他传入指标ID
     * @param isOther 是否为第三方告警信息，false表示OC4系统自身告警，true表示为第三方告警
     * @return 返回是否成功标志，success表示成功，failed表示失败，done返回已经处理过
     */
    @Override
    public String confirmAlarmEventById(String alarmID, final String instanceID, final String metricId, final boolean isOther) {
        /**
         * 告警确认逻辑如下：
         * 1.首先根据isOther判断是否为第三方告警，如果是第三方告警需要判断是否绑定资源，否则不予以处理
         * 2.根据alarmID查询出AlarmEvent实例，然后根据recoverKey，来恢复所有相关告警（如果是资源告警则需要把当前资源实例下的所有告警信息都确认，
         * 即都恢复可用；如果是指标告警，则需要把当前资源实例下的该指标的告警信息都恢复可用）。
         * 3.需要注意的是：如果勾选了多条告警信息，其中有些告警信息的recoverKey相同，即这些告警信息都属于一个指标（或资源）告警，那么再第一次调用的时候就会恢复，
         * 故不再需要重复恢复。
         * 4.拷贝当前AlarmEvent，修改其相关数据，新增一条“已恢复”告警。
         */
        if(logger.isDebugEnabled()) {
            logger.debug("Starts to confirm alarm event. data is {alarmID:" + alarmID + ",instanceID:" + instanceID + ",metricId:"
                    + metricId + ", isOther:" + isOther + "}");
        }
        if(isOther && StringUtils.isBlank(instanceID)) {
            if(logger.isWarnEnabled()) {
                logger.warn("Can not bind instance with other alarm, using instanceId{"+instanceID+"},metricId{"+metricId+"},alarmID{"+alarmID+"}");
            }
            return FAILED;
        }
        try{
            AlarmEvent alarmEvent = alarmEventService.getAlarmEvent(Long.valueOf(alarmID), false);
            String recoveryKey = alarmEvent.getRecoverKey();
            Boolean isRecovery = alarmEvent.isRecovered();

            if(!isRecovery) {
                alarmEventService.recoverAlarmEventByRecoverKey(recoveryKey, HandleType.CONFIRM);
                alarmNotifyService.deleteAlarmNotifyWaitByRecoveryKey(recoveryKey);
                AlarmEvent newAlarmEvent = new AlarmEventPO();
                BeanUtils.copyProperties(alarmEvent, newAlarmEvent);
                newAlarmEvent.setLevel(InstanceStateEnum.NORMAL);
                newAlarmEvent.setHandleType(HandleType.CONFIRM);
                newAlarmEvent.setCollectionTime(new Date());
                newAlarmEvent.setEventID(0L);//在AlarmEventServiceImpl里面自动设置
                newAlarmEvent.setRecovered(true);
                newAlarmEvent.setRecoveryEvent(true);
                newAlarmEvent.setRecoveryEventID(newAlarmEvent.getEventID());
                newAlarmEvent.setRecoveryTime(newAlarmEvent.getCollectionTime());

//                if(alarmEvent.getSysID() == SysModuleEnum.MONITOR || alarmEvent.getSysID() == SysModuleEnum.LINK) {
//                    boolean isMainResource = false;
//                    String templateMetricId = alarmEvent.getSysID().name() + "_CONFIRM";
//                    MetricTypeEnum metricTypeEnum = MetricTypeEnum.PerformanceMetric;
//                    if(alarmEvent.getRecoverKey().startsWith("instanceState_"))
//                        metricTypeEnum = MetricTypeEnum.AvailabilityMetric;
//                    if(StringUtils.equals(alarmEvent.getSourceID(), alarmEvent.getExt8())){
//                        isMainResource = true;
//                    }
//
//                    String defaultTemplateContent = alarmEventTemplateService.getDefaultTemplateContent(isMainResource, templateMetricId, metricTypeEnum, InstanceStateEnum.NORMAL);
//                    if(null == defaultTemplateContent) {
//                        if(logger.isInfoEnabled()) {
//                            logger.info("alarmEvent {" + alarmEvent.getRecoverKey() + "/"+alarmEvent.getContent()+"} can't find alarm confirm template...");
//                        }
//                    }else{
//                        String[] searchList = null;
//                        String[] replaceList = null;
//                        boolean isNeed = true;
//                        if(isMainResource && metricTypeEnum == MetricTypeEnum.AvailabilityMetric) {
//                            searchList = new String[]{"${resourceName}"};
//                            replaceList = new String[]{alarmEvent.getSourceName()};
//                        }else {
//                            ResourceMetricDef resourceMetricDef = capacityService.getResourceMetricDef(alarmEvent.getExt0(), alarmEvent.getExt3());
//                            if(resourceMetricDef == null) { //自定义指标
//                                if(logger.isInfoEnabled()) {
//                                    logger.info("customize metric has no alarm template :" + alarmEvent);
//                                }
//                                isNeed = false;
//                            }else{
//                                searchList = new String[]{"${resourceType}","${resourceName}","${metricName}"};
//                                replaceList = new String[]{resourceMetricDef.getResourceDef().getName(),alarmEvent.getSourceName(),resourceMetricDef.getDescription()};
//                            }
//
//                        }
//                        if(isNeed) {
//                            String replaceContent = StringUtils.replaceEach(defaultTemplateContent, searchList, replaceList);
//                            newAlarmEvent.setContent(replaceContent);
//                        }
//                    }
//                }
                alarmEventService.addAlarmEvent(newAlarmEvent, false);

                AlarmConfirm alarmConfirm = new AlarmConfirm();
                alarmConfirm.setInstanceId(Long.valueOf(instanceID));
                alarmConfirm.setMetricId(metricId);
                alarmConfirm.setConfirmTime(new Date());
                alarmConfirm.setOtherAlarm(isOther);
                alarmConfirmQueue.offer(alarmConfirm);

            }else {
                if (logger.isInfoEnabled()) {
                    logger.info("The alarm is recovered. Data is {" + JSONObject.toJSONString(alarmEvent) + "}");
                }
                return DONE;
            }
        }catch (Exception e) {
            if(logger.isWarnEnabled()) {
                logger.warn(e.getMessage(), e);
            }
            return FAILED;
        }

        return SUCCESS;

    }

    /**
     * 判断当前状态计算是否在抑制期内
     * @param alarmConfirm
     * @return
     */
    @Override
    public boolean getAlarmConfirmBlocked(AlarmConfirm alarmConfirm) {
        if(alarmConfirm != null) {
            Date startConfirmDate = alarmConfirm.getConfirmTime();
            SystemConfigBo systemConfigBo = systemConfigApi.getSystemConfigById(SystemConfigConstantEnum.SYSTEM_CONFIG_MESSAGE_SETTING_CFG.getCfgId());
            if(systemConfigBo != null && systemConfigBo.getContent() !=null) {
                Map systemConfigMap = JSONObject.parseObject(systemConfigBo.getContent(), Map.class);
                //Integer blockedTime = (Integer) systemConfigMap.get("time"); //这行代码会阻塞
                int blockedTime = 0;
                Object obj = systemConfigMap.get("time");
                if(logger.isDebugEnabled()){
                    logger.debug("distinguish confirm time block or not, " + alarmConfirm);
                }
                if(obj !=null) {
                    try{
                        blockedTime = Integer.valueOf(obj.toString());
                    }catch (Exception e){
                        if(logger.isWarnEnabled())
                            logger.warn("Alarm confirm error. " + alarmConfirm);
                    }
                }
                if(logger.isDebugEnabled()){
                    logger.debug("confirm time block or not, " + alarmConfirm);
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(startConfirmDate);
                calendar.add(Calendar.MINUTE, +blockedTime);
                if(calendar.getTime().after(new Date())) { //抑制期内不进行状态计算
                    return true;
                }else{//需要删除该抑制告警信息
                    if (logger.isInfoEnabled())
                        logger.info("Should delete alarm confirm with " + alarmConfirm);
                    deleteAlarmConfirm(alarmConfirm);
                    return false;
                }
            }else {
                if(logger.isWarnEnabled())
                    logger.warn("Can't find setting." + alarmConfirm.toString());
                deleteAlarmConfirm(alarmConfirm);
                return false;
            }
        }else{
            return false;
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        List<AlarmConfirm> alarmConfirmList = alarmConfirmDAO.findAll();
        if(null != alarmConfirmList && !alarmConfirmList.isEmpty()) {
            for(AlarmConfirm alarmConfirm : alarmConfirmList) {
                alarmConfirmCacheUtils.setAlarmConfirmCache(alarmConfirm);
            }
        }
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        AlarmConfirm take = alarmConfirmQueue.take();
                        deal(take);
                    } catch (InterruptedException e) {
                        if(logger.isWarnEnabled()) {
                            logger.warn(e.getMessage(), e);
                        }
                    }
                }
            }
        });
        thread.setName("AlarmConfirm-init");
        thread.start();

    }

    private void deal(AlarmConfirm alarmConfirm) {
        if(logger.isInfoEnabled()) {
            logger.info("AlarmConfirm starts with data {instanceID:" + alarmConfirm.getInstanceId()
                    + ",metricId:" + alarmConfirm.getMetricId() + "}");
        }
        try{
            if(!alarmConfirm.isOtherAlarm()) {
                addAlarmConfirm(alarmConfirm);
            }

            DataSyncPO dataSyncPO = new DataSyncPO();
            dataSyncPO.setData(JSONObject.toJSONString(alarmConfirm));
            dataSyncPO.setType(DataSyncTypeEnum.ALARM_CONFIRM);
            dataSyncPO.setUpdateTime(alarmConfirm.getConfirmTime());
            dataSyncPO.setCreateTime(alarmConfirm.getConfirmTime());
            dataSyncService.save(dataSyncPO);

        }catch (Exception e) {
            if(logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
            }
        }

    }

}
