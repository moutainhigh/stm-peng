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
     * @param alarmID ????????????ID
     * @param instanceID ????????????ID??????????????????????????????ID
     * @param metricId ??????ID?????????????????????????????????????????????null?????????????????????ID
     * @param isOther ?????????????????????????????????false??????OC4?????????????????????true????????????????????????
     * @return ???????????????????????????success???????????????failed???????????????done?????????????????????
     */
    @Override
    public String confirmAlarmEventById(String alarmID, final String instanceID, final String metricId, final boolean isOther) {
        /**
         * ???????????????????????????
         * 1.????????????isOther???????????????????????????????????????????????????????????????????????????????????????????????????????????????
         * 2.??????alarmID?????????AlarmEvent?????????????????????recoverKey????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
         * ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
         * 3.????????????????????????????????????????????????????????????????????????????????????recoverKey???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
         * ??????????????????????????????
         * 4.????????????AlarmEvent???????????????????????????????????????????????????????????????
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
                newAlarmEvent.setEventID(0L);//???AlarmEventServiceImpl??????????????????
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
//                            if(resourceMetricDef == null) { //???????????????
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
     * ?????????????????????????????????????????????
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
                //Integer blockedTime = (Integer) systemConfigMap.get("time"); //?????????????????????
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
                if(calendar.getTime().after(new Date())) { //?????????????????????????????????
                    return true;
                }else{//?????????????????????????????????
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
