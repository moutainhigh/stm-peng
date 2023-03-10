package com.mainsteam.stm.common.metric.sync;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.common.sync.DataSyncPO;
import com.mainsteam.stm.common.sync.DataSyncService;
import com.mainsteam.stm.common.sync.DataSyncTypeEnum;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.TimelineService;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.interceptor.ProfileSwitchChange;
import com.mainsteam.stm.profilelib.obj.*;
import com.mainsteam.stm.profilelib.objenum.ProfileTypeEnum;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

public class ProfileChangeMonitor implements ProfileSwitchChange {

    private static final Log logger = LogFactory.getLog(ProfileChangeMonitor.class);

    private ProfileService profileService;

    private DataSyncService dataSyncService;

    private ResourceInstanceService resourceInstanceService;

    private TimelineService timelineService;

    public void setProfileService(ProfileService profileService) {
        this.profileService = profileService;
    }

    public void setDataSyncService(DataSyncService dataSyncService) {
        this.dataSyncService = dataSyncService;
    }

    public void setResourceInstanceService(ResourceInstanceService resourceInstanceService) {
        this.resourceInstanceService = resourceInstanceService;
    }

    public void setTimelineService(TimelineService timelineService) {
        this.timelineService = timelineService;
    }

    @Override
    public void switchChange(List<ProfileSwitchData> profileSwitchData) throws Exception {

        if(logger.isInfoEnabled()) {
            logger.info("profile change: " + profileSwitchData);
        }

        if(null != profileSwitchData) {
            for (ProfileSwitchData profileData : profileSwitchData) {
                Profile profile = profileData.getProfile();
                if(profile !=null) {
                    ProfileInstanceRelation profileInstanceRelations = profile.getProfileInstanceRelations();
                    if(null != profileInstanceRelations) {
                        for (Instance instance : profileInstanceRelations.getInstances()) { //?????????????????????
                            InstanceCancelMonitorData instanceCancelMonitorData = deal(profile, instance, true, null);
                            List<Profile> children = profile.getChildren();
                            if(null != children) {
                                for (Profile childProfile : children) {
                                    ProfileInstanceRelation childProfileProfileInstanceRelations = childProfile.getProfileInstanceRelations();
                                    if(null != childProfileProfileInstanceRelations) {
                                        //Set<Long> childrenInstSet = new HashSet<>();
                                        for (Instance childInstance : childProfileProfileInstanceRelations.getInstances()) {
//                                            ResourceInstance resourceInstance = resourceInstanceService.getResourceInstance(childInstance.getInstanceId());
//                                            if(null != resourceInstance && resourceInstance.getLifeState() != InstanceLifeStateEnum.MONITORED){
//                                                childrenInstSet.add(resourceInstance.getId());
//                                                continue;
//                                            }
                                            deal(childProfile, childInstance, false, instanceCancelMonitorData);
                                        }
//                                        if(!childrenInstSet.isEmpty())
//                                            instanceCancelMonitorData.setChildren(childrenInstSet);
                                    }
                                }
                            }
                            //???????????????
                            if(!instanceCancelMonitorData.isEmpty()) { //??????????????????????????????
                                DataSyncPO dataSyncPO = new DataSyncPO();
                                dataSyncPO.setType(DataSyncTypeEnum.PROFILE_STATE);
                                dataSyncPO.setCreateTime(new Date());
                                dataSyncPO.setData(JSON.toJSONString(instanceCancelMonitorData));
                                if(logger.isInfoEnabled()) {
                                    logger.info("profile change data : " + dataSyncPO);
                                }
                                this.dataSyncService.save(dataSyncPO);
                            }
                        }
                    }
                }
            }
        }
    }

    private InstanceCancelMonitorData deal(Profile profile,Instance instance, boolean isMainProfile, InstanceCancelMonitorData instanceCancelMonitorData) throws Exception{

        if(instanceCancelMonitorData == null)
             instanceCancelMonitorData = new InstanceCancelMonitorData();
        if(instanceCancelMonitorData.getInstanceId() ==0L)
            instanceCancelMonitorData.setInstanceId(instance.getInstanceId());
        MetricSetting metricSetting = profile.getMetricSetting();

        if(metricSetting != null) {
            List<ProfileMetric> metrics = metricSetting.getMetrics();
            if(null != metrics) {
                //profile??????????????????????????????
                List<Timeline> timelineList = profile.getTimeline();
                Set<String> timelineMetricSet = new HashSet<>();
                if(timelineList !=null && !timelineList.isEmpty()) {
                    for (Timeline timeline: timelineList) {
                        MetricSetting timelineMetricSetting = timeline.getMetricSetting();
                        if(null != timelineMetricSetting) {
                            List<ProfileMetric> timelineMetrics = timelineMetricSetting.getMetrics();
                            if(timelineMetrics != null) {
                                for (ProfileMetric metric : timelineMetrics) {
                                    if(metric.isAlarm()){
                                        timelineMetricSet.add(metric.getMetricId());
                                    }
                                }
                            }
                        }
                    }
                }else {
                    for (ProfileMetric metric : metrics) {
                        if(metric.isAlarm()){
                            timelineMetricSet.add(metric.getMetricId());
                        }
                    }
                }
                if(logger.isInfoEnabled() && !timelineMetricSet.isEmpty()) {
                    logger.info("find alarm metric while profile changed :" + timelineMetricSet);
                }

                Set<String> cancelMetrics = new HashSet<>();
                boolean isMatchTimeline = false;
                //????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                ProfileInfo basicInfoByResourceInstanceId = profileService.getBasicInfoByResourceInstanceId(instance.getInstanceId());
                if(null != basicInfoByResourceInstanceId) {
                    if(basicInfoByResourceInstanceId.getProfileType() == ProfileTypeEnum.SPECIAL) { //???????????????????????????
                        long profileId = basicInfoByResourceInstanceId.getProfileId();
                        List<Timeline> timelinesByProfileId = timelineService.getTimelinesByProfileId(profileId);
                        if(timelinesByProfileId !=null && !timelinesByProfileId.isEmpty()){
                            for (Timeline timeline : timelinesByProfileId) {
                                TimelineInfo timelineInfo = timeline.getTimelineInfo();
                                Date currentDate = new Date();
                                if(timelineInfo.getStartTime().before(currentDate) && timelineInfo.getEndTime().after(currentDate)){ //???????????????????????????????????????
                                    isMatchTimeline = true;
                                    List<ProfileMetric> metricByTimelineId = timelineService.getMetricByTimelineId(timelineInfo.getTimeLineId());
                                    if(null != metricByTimelineId && !metricByTimelineId.isEmpty()){
                                        for (ProfileMetric timelineMetric : metricByTimelineId) {
                                            if(timelineMetricSet.contains(timelineMetric.getMetricId()) && !timelineMetric.isAlarm()) {
                                                //????????????????????????????????????????????????????????????
                                                if(logger.isInfoEnabled()) {
                                                    logger.info("metric cancels to alarm in the new timeline while profile changed(metric/timeline/inst): "
                                                            + timelineMetric.getMetricId() + "/" + timelineInfo.getTimeLineId() + "/" + instance.getInstanceId());
                                                }
                                                cancelMetrics.add(timelineMetric.getMetricId());
                                            }
                                        }
                                    }
                                    break;
                                }
                            }
                        }

                    }
                }


                if(!isMatchTimeline) {
                    for (String metricId : timelineMetricSet) {
                        ProfileMetric metricByInstanceIdAndMetricId = null;
                        try {
                            //?????????????????????????????????????????????????????????
                            metricByInstanceIdAndMetricId = profileService.getMetricByInstanceIdAndMetricId(instance.getInstanceId(), metricId);
                        } catch (ProfilelibException e) {
                            if(logger.isErrorEnabled()) {
                                logger.error(e.getMessage() +",query metric error:" + metricId, e);
                            }
                            continue;
                        }
                        //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                        if(metricByInstanceIdAndMetricId == null || !metricByInstanceIdAndMetricId.isAlarm()){
                            if(logger.isInfoEnabled()) {
                                logger.info("metric cancels to alarm while profile changed(metric/inst): "
                                        + metricId + "/" + instance.getInstanceId());
                            }
                            cancelMetrics.add(metricId);
                        }
                    }
                }

                if(!cancelMetrics.isEmpty()){
                    if(isMainProfile) {
                        instanceCancelMonitorData.setMetricList(cancelMetrics);
                    }else{
                        Map<Long, Set<String>> childrenMetrics = instanceCancelMonitorData.getChildrenMetrics();
                        if(null == childrenMetrics) {
                            childrenMetrics = new HashMap<>(2);
                            instanceCancelMonitorData.setChildrenMetrics(childrenMetrics);
                        }
                        childrenMetrics.put(instance.getInstanceId(), cancelMetrics);
                    }

                }
            }
        }
        return instanceCancelMonitorData;
    }

}
