/**
 * 
 */
package com.mainsteam.stm.profilelib.deploy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.dict.FrequentEnum;
import com.mainsteam.stm.common.metric.CustomMetricQueryServiceMBean;
import com.mainsteam.stm.common.metric.obj.CustomMetricMonitorInfo;
import com.mainsteam.stm.common.metric.obj.CustomMetricMonitorInfoWrapper;
import com.mainsteam.stm.errorcode.ServerErrorCodeConstant;
import com.mainsteam.stm.node.LocaleNodeService;
import com.mainsteam.stm.node.Node;
import com.mainsteam.stm.node.NodeFunc;
import com.mainsteam.stm.node.exception.NodeException;
import com.mainsteam.stm.profilelib.deploy.obj.ProfileDeployInfo;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.MonitorProfile;
import com.mainsteam.stm.profilelib.obj.ProfileInstanceRelation;
import com.mainsteam.stm.profilelib.obj.ProfileMetricMonitor;
import com.mainsteam.stm.profilelib.obj.TimelineProfileMetricMonitor;
import com.mainsteam.stm.profilelib.objenum.ProfileChangeEnum;
import com.mainsteam.stm.profilelib.objenum.TimelineTypeEnum;
import com.mainsteam.stm.profilelib.remote.ProfileLoaderServiceMBean;
import com.mainsteam.stm.rpc.client.OCRPCClient;
import com.mainsteam.stm.scheduler.SchedulerService;
import com.mainsteam.stm.scheduler.obj.DailyLimitedPeriodTimeFire;
import com.mainsteam.stm.scheduler.obj.DateLimitedPeriodTimeFire;
import com.mainsteam.stm.scheduler.obj.LimitedPeriodTimeFire;
import com.mainsteam.stm.scheduler.obj.MetricScheduleTask;
import com.mainsteam.stm.scheduler.obj.PeriodTimeFire;
import com.mainsteam.stm.scheduler.obj.ProfileScheduleTask;
import com.mainsteam.stm.scheduler.obj.SecondPeriodFire;
import com.mainsteam.stm.scheduler.obj.TimelineScheduleTask;
import com.mainsteam.stm.scheduler.obj.WeeklyLimitedPeriodTimeFire;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;

/**
 * 需要和策略修改结合。
 * 
 * @author ziw
 * 
 */
public class ProfileCollectDeploy implements ProfileCollectDeployMBean {

	private static final Log logger = LogFactory
			.getLog(ProfileCollectDeploy.class);

	private SchedulerService schedulerService;

	private CustomMetricQueryServiceMBean customMetricQueryServiceMBean;

	private CustomMetricCollectDeploy customMetricCollectDeploy;

	private OCRPCClient client;

	private LocaleNodeService localeNodeService;
	
	private ResourceInstanceService resourceInstanceService;

	private int dcsGroupId;
	
	public void setResourceInstanceService(
			ResourceInstanceService resourceInstanceService) {
		this.resourceInstanceService = resourceInstanceService;
	}

	/**
	 * @param client
	 *            the client to set
	 */
	public final void setClient(OCRPCClient client) {
		this.client = client;
	}

	/**
	 * @param customMetricCollectDeploy
	 *            the customMetricCollectDeploy to set
	 */
	public final void setCustomMetricCollectDeploy(
			CustomMetricCollectDeploy customMetricCollectDeploy) {
		this.customMetricCollectDeploy = customMetricCollectDeploy;
	}

	/**
	 * @param localeNodeService
	 *            the localeNodeService to set
	 */
	public final void setLocaleNodeService(LocaleNodeService localeNodeService) {
		this.localeNodeService = localeNodeService;
	}

	/**
	 * @param schedulerService
	 *            the schedulerService to set
	 */
	public final void setSchedulerService(SchedulerService schedulerService) {
		this.schedulerService = schedulerService;
	}

	/**
	 * 
	 */
	public ProfileCollectDeploy() {
	}

	/**
	 * 加载策略的设置
	 */
	public void loadProfileSettings() {
		if (logger.isInfoEnabled()) {
			logger.info("loadProfileSettings start");
		}
		Node currentNode = null;
		try {
			currentNode = localeNodeService.getCurrentNode();
		} catch (NodeException e1) {
			if (logger.isErrorEnabled()) {
				logger.error("loadProfileSettings", e1);
			}
		}
		if (currentNode == null) {
			if (logger.isErrorEnabled()) {
				logger.error("loadProfileSettings not found currentNode.");
			}
			return;
		}
		// 通过rpc client，拿到mbean的远程对象
		ProfileLoaderServiceMBean loaderServiceMBean;
		MonitorProfile[] monitorProfiles = null;
		try {
			loaderServiceMBean = client.getParentRemoteSerivce(
					NodeFunc.processer, ProfileLoaderServiceMBean.class);
			monitorProfiles = loaderServiceMBean.loadProfile(currentNode
					.getGroupId());
			dispatchMonitorProfiles(monitorProfiles);

			// 执行完成后，在这里加载自定义指标
			if (client != null) {
				customMetricQueryServiceMBean = client
						.getParentRemoteSerivce(NodeFunc.processer,
								CustomMetricQueryServiceMBean.class);
				CustomMetricMonitorInfoWrapper wrapper = customMetricQueryServiceMBean
						.selectCustomMetricMonitorInfos(currentNode
								.getGroupId());
				if (wrapper != null) {
					CustomMetricMonitorInfo[] infos = wrapper
							.getCustomMetricMonitorInfos();
					if (infos != null && infos.length > 0) {
						for (CustomMetricMonitorInfo customMetricMonitorInfo : infos) {
							customMetricCollectDeploy.addCustomMetricMonitor(
									customMetricMonitorInfo,
									wrapper.getInstanceProfileMap());
						}
					}
				}
			}
		} catch (IOException e) {
			if (logger.isErrorEnabled()) {
				logger.error("addInstancesMonitor", e);
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("loadProfileSettings end");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.profilelib.remote.ProfileCollectDeployMBean#
	 * addInstancesMonitor(java.util.List)
	 */
	public void addInstancesMonitor(
			List<ProfileInstanceRelation> instanceRelations) {
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder(200);
			b.append("addInstancesMonitor start\n");
			for (ProfileInstanceRelation profileInstanceRelation : instanceRelations) {
				b.append(" profileId=").append(profileInstanceRelation.getProfileId());
				b.append(" instanceIds=").append(profileInstanceRelation.getInstances());
				b.append("\n");
			}
			logger.info(b);
		}
		/**
		 * 安装profileId分组
		 */
		Set<Long> toLoadProfileIds = new HashSet<>();
		for (ProfileInstanceRelation profileInstanceRelation : instanceRelations) {
			Long profileId = profileInstanceRelation.getProfileId();
			if (profileInstanceRelation.getInstances() == null
					|| profileInstanceRelation.getInstances().size() <= 0) {
				continue;
			}
			long[] resourceInstIds = toResourceInstIds(profileInstanceRelation);
			schedulerService.removeScheduleByResourceInstanceIds(resourceInstIds);
			if (schedulerService.hasProfileScheduleTask(profileId)) {
				schedulerService
						.schedule(profileInstanceRelation.getProfileId(),
								resourceInstIds);
			} else {
				toLoadProfileIds.add(profileInstanceRelation.getProfileId());
			}
		}
		if (toLoadProfileIds.size() > 0) {
			long[] profileIds = new long[toLoadProfileIds.size()];
			int i = 0;
			for (Long profileId : toLoadProfileIds) {
				profileIds[i++] = profileId.longValue();
			}
			// 通过rpc client，拿到mbean的远程对象
			ProfileLoaderServiceMBean loaderServiceMBean;
			MonitorProfile[] monitorProfiles = null;
			try {
				loaderServiceMBean = client.getParentRemoteSerivce(
						NodeFunc.processer, ProfileLoaderServiceMBean.class);
				monitorProfiles = loaderServiceMBean
						.loadProfileByProfileId(profileIds,localeNodeService.getCurrentNode().getGroupId());
				dispatchMonitorProfiles(monitorProfiles);
			} catch (IOException | NodeException e) {
				if (logger.isErrorEnabled()) {
					logger.error("addInstancesMonitor", e);
				}
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("addInstancesMonitor end");
		}
	}

	private void dispatchMonitorProfiles(MonitorProfile[] monitorProfiles) {
		if (monitorProfiles != null && monitorProfiles.length > 0) {
			for (MonitorProfile monitorProfile : monitorProfiles) {
				ProfileScheduleTask profileScheduleTask = toScheduleTask(monitorProfile);
				schedulerService.schedule(profileScheduleTask);
				if (logger.isInfoEnabled()) {
					logger.info("addInstancesMonitor profileId="
							+ monitorProfile.getProfileId()
							+ " instanceIds = "
							+ Arrays.toString(profileScheduleTask
									.getResourceInstIds()));
				}
			}
		}
	}

	private long[] toResourceInstIds(
			ProfileInstanceRelation profileInstanceRelation) {
		long[] resourceInstIds = new long[profileInstanceRelation
		                  				.getInstances().size()];
  		for (int i = 0; i < resourceInstIds.length; i++) {
  			resourceInstIds[i] = profileInstanceRelation.getInstances().get(i)
  					.getInstanceId();
  		}
  		return resourceInstIds;
	}

	private ProfileScheduleTask toScheduleTask(MonitorProfile profile) {
		ProfileScheduleTask task = new ProfileScheduleTask();
		task.setProfileId(profile.getProfileId());
		task.setResourceInstIds(toResourceInstIds(profile
				.getProfileInstanceRelations()));
		task.setParentProfileId(profile.getParentProfileId());
		List<TimelineProfileMetricMonitor> timelineProfileMetricMonitors = profile
				.getTimelineProfileMetricMonitors();
		if (timelineProfileMetricMonitors != null
				&& timelineProfileMetricMonitors.size() > 0) {
			List<TimelineScheduleTask> timelineScheduleTasks = new ArrayList<>(
					timelineProfileMetricMonitors.size());
			for (TimelineProfileMetricMonitor timelineProfileMetricMonitor : timelineProfileMetricMonitors) {
				timelineScheduleTasks
						.add(toTimelineScheduleTask(timelineProfileMetricMonitor));
			}
			task.setTimelineScheduleTasks(timelineScheduleTasks);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("toScheduleTask profile.getProfileMetricMonitors.size="
					+ profile.getProfileMetricMonitors().size());
		}
		task.setTasks(toMetricScheduleTasks(profile.getProfileMetricMonitors()));
		return task;
	}

	private LimitedPeriodTimeFire toLimitedPeriodTimeFire(
			TimelineTypeEnum timelineType, Date start, Date end) {
		LimitedPeriodTimeFire limitedPeriodTimeFire = null;
		if (timelineType == TimelineTypeEnum.WEEKLY) {
			limitedPeriodTimeFire = new WeeklyLimitedPeriodTimeFire();
		} else if (timelineType == TimelineTypeEnum.DAILY) {
			limitedPeriodTimeFire = new DailyLimitedPeriodTimeFire();
		} else {
			limitedPeriodTimeFire = new DateLimitedPeriodTimeFire();
		}
		limitedPeriodTimeFire.setLimitedPeriodTime(start.getTime(),
				end.getTime());
		return limitedPeriodTimeFire;
	}

	private List<MetricScheduleTask> toMetricScheduleTasks(
			List<ProfileMetricMonitor> profileMetricMonitors) {
		List<MetricScheduleTask> tasks = new ArrayList<>(
				profileMetricMonitors.size());
		for (ProfileMetricMonitor profileMetricMonitor : profileMetricMonitors) {
			tasks.add(toMetricScheduleTask(profileMetricMonitor));
		}
		return tasks;
	}

	private MetricScheduleTask toMetricScheduleTask(
			ProfileMetricMonitor profileMetricMonitor) {
		MetricScheduleTask mTask = new MetricScheduleTask();
		mTask.setActive(profileMetricMonitor.isMonitor());
		mTask.setMetricId(profileMetricMonitor.getMetricId());
		mTask.setProfileId(profileMetricMonitor.getProfileId());
		mTask.setTimeFire(getPeriodTimeFire(profileMetricMonitor
				.getMonitorFeq()));
		if(profileMetricMonitor.isTimeline()){
			mTask.setTimeline(true);
			mTask.setTimelineId(profileMetricMonitor.getTimelineId());
		}
		if (logger.isDebugEnabled()) {
			StringBuilder b = new StringBuilder();
			b.append("toMetricScheduleTask.ProfileId=").append(
					mTask.getProfileId());
			b.append(" MetricId=").append(mTask.getMetricId());
			b.append(" IsActive=").append(mTask.isActive());
			b.append(" IsTimeLine=").append(mTask.isTimeline());
			b.append(" SecondFreq=").append(
					((SecondPeriodFire) mTask.getTimeFire()).getSecond());
			logger.debug(b);
		}
		return mTask;
	}

	public static PeriodTimeFire getPeriodTimeFire(FrequentEnum f) {
		SecondPeriodFire periodFire = new SecondPeriodFire();
		if (f.getSeconds() <= 0 || f.getSeconds() > 86400) {
			throw new RuntimeException("invalid frequence f=" + f
					+ " f.seconds" + f.getSeconds());
		}
		periodFire.setSecond(f.getSeconds());
		return periodFire;
	}

	private TimelineScheduleTask toTimelineScheduleTask(
			TimelineProfileMetricMonitor timelineProfileMetricMonitor) {
		TimelineScheduleTask scheduleTask = new TimelineScheduleTask();
		scheduleTask.setLimitedPeriodTimeFire(toLimitedPeriodTimeFire(
				timelineProfileMetricMonitor.getTimelineType(),
				timelineProfileMetricMonitor.getStartTime(),
				timelineProfileMetricMonitor.getEndTime()));
		scheduleTask.setProfileId(timelineProfileMetricMonitor.getProfileId());
		scheduleTask
				.setTasks(toMetricScheduleTasks(timelineProfileMetricMonitor
						.getProfileMetricMonitors()));
		scheduleTask
				.setTimelineId(timelineProfileMetricMonitor.getTimelineId());
		return scheduleTask;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.profilelib.remote.ProfileCollectDeployMBean#
	 * cancelInstancesMonitor(java.util.List)
	 */
	public void cancelInstancesMonitor(List<Long> instanceIdList) {
		if (logger.isInfoEnabled()) {
			logger.info("cancelInstancesMonitor start instanceIdList="
					+ instanceIdList);
		}
		long[] instanceIds = new long[instanceIdList.size()];
		for (int i = 0; i < instanceIds.length; i++) {
			instanceIds[i] = instanceIdList.get(i);
		}
		schedulerService.removeScheduleByResourceInstanceIds(instanceIds);
		if (logger.isInfoEnabled()) {
			logger.info("changeMetricsMonitor end");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.profilelib.remote.ProfileCollectDeployMBean#
	 * changeMetricsMonitor(java.util.List)
	 */
	public void changeMetricsMonitor(List<ProfileMetricMonitor> metricMonitors) {
		if (logger.isInfoEnabled()) {
			logger.info("batch changeMetricsMonitor start");
		}
		if (metricMonitors != null && metricMonitors.size() > 0) {
			for (ProfileMetricMonitor profileMetricMonitor : metricMonitors) {
				changeMetricsMonitor(profileMetricMonitor);
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("batch changeMetricsMonitor end");
		}
	}

	public void changeMetricsMonitor(ProfileMetricMonitor metricMonitor) {
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder();
			b.append("changeMetricsMonitor start ").append(metricMonitor);
			logger.info(b.toString());
		}
		schedulerService.schedule(toMetricScheduleTask(metricMonitor));
		if (logger.isInfoEnabled()) {
			logger.info("changeMetricsMonitor end");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.profilelib.remote.ProfileCollectDeployMBean#
	 * deleteTimelines(java.util.List)
	 */
	public void deleteTimelines(List<Long> timelineIds) {
		if (logger.isInfoEnabled()) {
			logger.info("deleteTimelines start timelineIds=" + timelineIds);
		}
		for (Long timelineId : timelineIds) {
			schedulerService.removeScheduleByTimeline(timelineId.longValue());
		}
		if (logger.isInfoEnabled()) {
			logger.info("deleteTimelines end");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.profilelib.remote.ProfileCollectDeployMBean#addTimeline
	 * (com.mainsteam.stm.profilelib.obj.TimelineProfileMetricMonitor)
	 */
	public void addTimeline(
			TimelineProfileMetricMonitor timelineProfileMetricMonitor) {
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder(100);
			b.append("addTimeline start").append(timelineProfileMetricMonitor);
			logger.info(b);
		}
		TimelineScheduleTask scheduleTask = toTimelineScheduleTask(timelineProfileMetricMonitor);
		schedulerService.schedule(scheduleTask);
		if (logger.isInfoEnabled()) {
			logger.info("addTimeline end");
		}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.profilelib.remote.ProfileCollectDeployMBean#
	 * upateTimelineTime
	 * (com.mainsteam.stm.profilelib.obj.TimelineProfileMetricMonitor)
	 */
	public void updateTimelineTime(
			TimelineProfileMetricMonitor timelineProfileMetricMonitor) {
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder(100);
			b.append("upateTimelineTime start").append(timelineProfileMetricMonitor);
			logger.info(b);
		}
		schedulerService.updateTimeline(timelineProfileMetricMonitor.getTimelineId(), toLimitedPeriodTimeFire(
				timelineProfileMetricMonitor.getTimelineType(),
				timelineProfileMetricMonitor.getStartTime(),
				timelineProfileMetricMonitor.getEndTime()));
		if (logger.isInfoEnabled()) {
			logger.info("upateTimelineTime end");
		}
	}

	public void deleteProfile(List<Long> profileIds) {
		if (logger.isInfoEnabled()) {
			logger.info("deleteProfile start profileIds" + profileIds );
		}
		schedulerService.removeSchedule(profileIds);
		if (logger.isInfoEnabled()) {
			logger.info("deleteProfile end");
		}
	}

	@Override
	public void deployProfileInfo(ProfileDeployInfo[] deployInfos) {
		if (logger.isInfoEnabled()) {
			logger.info("deployProfileInfo start");
		}
		if (deployInfos == null || deployInfos.length <= 0) {
			if (logger.isWarnEnabled()) {
				logger.warn("deployProfileInfo no deployInfos.");
			}
		}
		List<Long> toRemoveprofileIds = new ArrayList<>();
		List<Long> toRemoveTimelineIds = new ArrayList<>();
		List<Long> toCancelMonitorInstanceIds = new ArrayList<>();
		List<ProfileInstanceRelation> toAddMonitorRelations = new ArrayList<>();

		for (ProfileDeployInfo profileDeployInfo : deployInfos) {
			ProfileChangeEnum changeEnum = ProfileChangeEnum
					.valueOf(profileDeployInfo.getAction());

			/**
			 * 这种方式，是为了保证操作的顺序.
			 */
			if (changeEnum == ProfileChangeEnum.DELETE_PROFILE) {
				toRemoveprofileIds
						.add((Long) profileDeployInfo.getDeployData());
				continue;
			} else if (toRemoveprofileIds.size() > 0) {
				deleteProfile(toRemoveprofileIds);
				toRemoveprofileIds.clear();
			}
			if (changeEnum == ProfileChangeEnum.DELETE_TIMELINE) {
				toRemoveTimelineIds.add((Long) profileDeployInfo
						.getDeployData());
				continue;
			} else if (toRemoveTimelineIds.size() > 0) {
				deleteTimelines(toRemoveTimelineIds);
				toRemoveTimelineIds.clear();
			}
			if (changeEnum == ProfileChangeEnum.CANCEL_MONITOR || changeEnum == ProfileChangeEnum.CANCEL_LAST_MONITOR) {
				toCancelMonitorInstanceIds.add((Long) profileDeployInfo
						.getDeployData());
				continue;
			} else if (toCancelMonitorInstanceIds.size() > 0) {
				cancelInstancesMonitor(toCancelMonitorInstanceIds);
				toCancelMonitorInstanceIds.clear();
			}

			if (changeEnum == ProfileChangeEnum.ADD_MONITOR) {
				toAddMonitorRelations
						.add((ProfileInstanceRelation) profileDeployInfo
								.getDeployData());
				continue;
			} else if (toAddMonitorRelations.size() > 0) {
				addInstancesMonitor(toAddMonitorRelations);
				toAddMonitorRelations.clear();
			}

			if (changeEnum == ProfileChangeEnum.ADD_TIMELINE) {
				addTimeline((TimelineProfileMetricMonitor) profileDeployInfo
						.getDeployData());
			}else if (changeEnum == ProfileChangeEnum.UPDATE_TIMELINE_MONITOR) {
				changeMetricsMonitor((ProfileMetricMonitor) profileDeployInfo
							.getDeployData());
			}else if (changeEnum == ProfileChangeEnum.UPDATE_TIMELINE_MONITORFEQ) {
				changeMetricsMonitor((ProfileMetricMonitor) profileDeployInfo
						.getDeployData());
			}else if (changeEnum == ProfileChangeEnum.UPDATE_METRIC_MONITOR) {
				changeMetricsMonitor((ProfileMetricMonitor) profileDeployInfo
						.getDeployData());
			} else if (changeEnum == ProfileChangeEnum.UPDATE_METRIC_MONITORFEQ) {
				changeMetricsMonitor((ProfileMetricMonitor) profileDeployInfo
						.getDeployData());
			} else if (changeEnum == ProfileChangeEnum.UPDATE_TIMELINE_TIME) {
				updateTimelineTime((TimelineProfileMetricMonitor) profileDeployInfo
						.getDeployData());
			} else {
				if (logger.isErrorEnabled()) {
					logger.error("deployProfileInfo invalid changeEnum="
							+ changeEnum);
				}
			}
		}

		if (toRemoveprofileIds.size() > 0) {
			deleteProfile(toRemoveprofileIds);
			toRemoveprofileIds.clear();
		}

		if (toRemoveTimelineIds.size() > 0) {
			deleteTimelines(toRemoveTimelineIds);
			toRemoveTimelineIds.clear();
		}

		if (toCancelMonitorInstanceIds.size() > 0) {
			cancelInstancesMonitor(toCancelMonitorInstanceIds);
			toCancelMonitorInstanceIds.clear();
		}

		if (toAddMonitorRelations.size() > 0) {
			addInstancesMonitor(toAddMonitorRelations);
			toAddMonitorRelations.clear();
		}
		if (logger.isInfoEnabled()) {
			logger.info("deployProfileInfo end");
		}
	}
}
