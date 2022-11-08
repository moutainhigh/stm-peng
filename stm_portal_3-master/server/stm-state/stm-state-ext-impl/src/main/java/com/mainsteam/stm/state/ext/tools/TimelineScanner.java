package com.mainsteam.stm.state.ext.tools;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.common.metric.sync.MetricProfileChange;
import com.mainsteam.stm.common.sync.DataSyncPO;
import com.mainsteam.stm.common.sync.DataSyncService;
import com.mainsteam.stm.common.sync.DataSyncTypeEnum;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.TimelineService;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.MetricSetting;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;
import com.mainsteam.stm.profilelib.obj.Timeline;
import com.mainsteam.stm.profilelib.obj.TimelineInfo;
import com.mainsteam.stm.state.obj.MetricStateData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;

/**
 * 定期扫描基线中取消监控或者告警的指标，恢复指标状态&资源状态
 */
@Component("stateTimelineScanner")
public class TimelineScanner implements ApplicationListener<ContextRefreshedEvent> {

    private static final Log logger = LogFactory.getLog(TimelineScanner.class);

    @Autowired
    @Qualifier("dataSyncService")
    private DataSyncService dataSyncService;
    @Autowired
    private TimelineService timelineService;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private StateCatchUtil stateCatchUtil;
    @Autowired
    private ResourceInstanceService resourceInstanceService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if(null == contextRefreshedEvent.getApplicationContext().getParent()) {
            if(logger.isInfoEnabled()){
                logger.info("timeline scanner starts...");
            }
            process();
        }
    }

    private void process() {
        final Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {

                    try {
                        List<Timeline> allTimelineList = timelineService.getTimelines();
                        if(allTimelineList !=null) {
                            for (final Timeline timeline : allTimelineList) {
                                final TimelineInfo timelineInfo = timeline.getTimelineInfo();
                                boolean isSkipped = false;
                                if(timelineInfo !=null ) {
                                    try {
                                        MetricSetting timelineMetricSetting = timeline.getMetricSetting();
                                        List<ProfileMetric> timelineMetricSettingMetrics = timelineMetricSetting.getMetrics();
                                        final Date currentDate = new Date();
                                        /**
                                         * 1.基线有效期内，取消指标告警
                                         * 2.基线过期后，判断指标是否告警
                                         */
                                        boolean isExpired = timelineInfo.getEndTime().before(currentDate);
                                        boolean isInTime = timelineInfo.getStartTime().before(currentDate) && timelineInfo.getEndTime().after(currentDate);
                                        if(isExpired || isInTime) {
                                            if(logger.isDebugEnabled()) {
                                                logger.debug("matches timeline(timeline/profile/name):(" + timelineInfo.getTimeLineId() + "/"
                                                        + timelineInfo.getProfileId() + "/"+timelineInfo.getName()+"}");
                                            }
                                            long profileId = timelineInfo.getProfileId();
                                            List<Long> resourceInstanceByProfileId =null;
                                            try {
                                                resourceInstanceByProfileId = profileService.getResourceInstanceByProfileId(profileId);
                                            } catch (ProfilelibException e) {
                                                if(logger.isWarnEnabled()) {
                                                    logger.warn(e.getMessage() + ",profile id :" + profileId, e);
                                                }
                                                isSkipped = true;
                                            }
                                            if(!isSkipped) {
                                                if(resourceInstanceByProfileId == null){
                                                    if(logger.isInfoEnabled()) {
                                                        logger.info("can't find insts while matching timeline:" + profileId);
                                                    }
                                                    isSkipped = true;
                                                }
                                                if(!isSkipped) {
                                                    for (final Long instanceId : resourceInstanceByProfileId) {
                                                        final ResourceInstance resourceInstance ;
                                                        try {
                                                            resourceInstance = resourceInstanceService.getResourceInstance(instanceId);
                                                        } catch (InstancelibException e) {
                                                            if(logger.isWarnEnabled()){
                                                                logger.warn(e.getMessage() + ", instance{" + instanceId + "}", e);
                                                            }
                                                            continue;
                                                        }

                                                        if(resourceInstance !=null && resourceInstance.getLifeState() == InstanceLifeStateEnum.MONITORED) {
                                                            if(isExpired){
                                                                timelineMetricSettingMetrics = profileService.getMetricByInstanceId(instanceId);
                                                            }
                                                            for (ProfileMetric metric : timelineMetricSettingMetrics) {
                                                                if(!metric.isAlarm()) {
                                                                    MetricStateData metricState = stateCatchUtil.getMetricState(instanceId, metric.getMetricId());
                                                                    if(metricState !=null && metricState.getState() != MetricStateEnum.NORMAL) { //需要通知
                                                                        if(logger.isInfoEnabled()) {
                                                                            logger.info("metric{" + instanceId + "/" + metric.getMetricId() +"} cancel alarm cause of timeline "
                                                                                    + timelineInfo.getTimeLineId());
                                                                        }
                                                                        //告警指标取消监控通知事件
                                                                        DataSyncPO dataSyncPO = new DataSyncPO();
                                                                        dataSyncPO.setCreateTime(currentDate);
                                                                        dataSyncPO.setType(DataSyncTypeEnum.METRIC_STATE);
                                                                        MetricProfileChange metricProfileChange = new MetricProfileChange();
                                                                        metricProfileChange.setMetricID(metric.getMetricId());
                                                                        metricProfileChange.setInstanceId(instanceId);
                                                                        metricProfileChange.setAlarmConfirm(false);
                                                                        metricProfileChange.setIsAlarm(false);
                                                                        metricProfileChange.setCustomMetric(false);

                                                                        dataSyncPO.setData(JSON.toJSONString(metricProfileChange));

                                                                        dataSyncService.save(dataSyncPO);
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                        }

                                    }catch (Exception e) {
                                        if(logger.isWarnEnabled()) {
                                            logger.warn(e.getMessage(), e);
                                        }
                                    }

                                }
                            }
                        }
                    } catch (ProfilelibException e) {
                        if(logger.isWarnEnabled()) {
                            logger.warn(e.getMessage(), e);
                        }
                    } catch (Exception e) {
                        if(logger.isWarnEnabled()) {
                            logger.warn(e.getMessage(), e);
                        }
                    }

                    try {
                        TimeUnit.SECONDS.sleep(30L);
                    } catch (InterruptedException e) {
                        continue;
                    }
                }

            }
        }, "TimelineScanner");

        t.start();

    }
}
