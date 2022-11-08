package com.mainsteam.stm.profilelib.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.FrequentEnum;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.service.ResourceInstanceExtendService;
import com.mainsteam.stm.job.IJob;
import com.mainsteam.stm.job.ScheduleManager;
import com.mainsteam.stm.metric.CustomMetricChangeService;
import com.mainsteam.stm.metric.obj.CustomMetricChange;
import com.mainsteam.stm.metric.objenum.CustomMetricChangeEnum;
import com.mainsteam.stm.profilelib.ProfileChangeService;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.TimelineService;
import com.mainsteam.stm.profilelib.deploy.ProfileCollectDeployMBean;
import com.mainsteam.stm.profilelib.deploy.obj.ProfileDeployInfo;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.Instance;
import com.mainsteam.stm.profilelib.obj.ProfieChange;
import com.mainsteam.stm.profilelib.obj.ProfileChangeResult;
import com.mainsteam.stm.profilelib.obj.ProfileInfo;
import com.mainsteam.stm.profilelib.obj.ProfileInstanceRelation;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;
import com.mainsteam.stm.profilelib.obj.ProfileMetricMonitor;
import com.mainsteam.stm.profilelib.obj.Timeline;
import com.mainsteam.stm.profilelib.obj.TimelineInfo;
import com.mainsteam.stm.profilelib.obj.TimelineProfileMetricMonitor;
import com.mainsteam.stm.profilelib.objenum.ProfileChangeEnum;
import com.mainsteam.stm.rpc.client.OCRPCClient;
import com.mainsteam.stm.util.SpringBeanUtil;

@Deprecated 
@DisallowConcurrentExecution
public class ProfileDispatchJob implements Job {

	private static final Log logger = LogFactory
			.getLog(ProfileDispatchJob.class);
	private static final int MAX_CHANGE = 5000;
	private ScheduleManager scheduleManager;
	private OCRPCClient client;
	private CustomMetricChangeService customMetricChangeService;
	private ResourceInstanceExtendService resourceInstanceService;
	private ProfileChangeService profileChangeService;
	private ProfileService profileService;
	private TimelineService timelineService;
	private CapacityService capacityService;

	public void setScheduleManager(ScheduleManager scheduleManager) {
		this.scheduleManager = scheduleManager;
	}

	public void start() {
		try {
			// 每隔10秒启动一个Job，扫描未同步到采集器的策略信息
			scheduleManager.scheduleJob(new IJob(this, "*/10 * * * * ?"));
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("ProfileDispatchJob error!", e);
			}
		}
	}

	private ResourceInstance getResourceInstance(long instanceId) {
		// 获取资源实例节点组
		ResourceInstance resourceInstance = null;
		try {
			resourceInstance = resourceInstanceService
					.getResourceInstanceById(instanceId);
		} catch (InstancelibException e1) {
			if (logger.isErrorEnabled()) {
				logger.error("getResourceInstance error!", e1);
			}
		}
		return resourceInstance;
	}

	/**
	 * 添加资源实例监控
	 * 
	 * @param instanceRelations
	 */
	private void addInstancesMonitor(
			Map<String, ProfileDeployInfoWrapper> profileDeployMap,
			ProfieChange profileChange,
			Map<Long, Map<String, Boolean>> changeResultsMap,
			List<Long> invalidChangeSateIds) {
		if (logger.isTraceEnabled()) {
			logger.trace("addInstancesMonitor start instanceId="
					+ profileChange.getSource());
		}
		long instanceId = Long.parseLong(profileChange.getSource());
		ResourceInstance resourceInstance = getResourceInstance(instanceId);
		String nodeGroupId = null;
		if (resourceInstance == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("ResourceInstance is not exist.instanceId="
						+ instanceId);
			}
		} else {
			nodeGroupId = resourceInstance.getDiscoverNode();
		}
		if (nodeGroupId == null) {
			if (logger.isWarnEnabled()) {
				logger.warn("addInstancesMonitor instance's nodeGroupId not found.instanceId="
						+ instanceId);
			}
			// 将profileChange结果变成1
			invalidChangeSateIds.add(profileChange.getProfileChangeId());
			return;
		}
		if (isIgnoreChange(nodeGroupId, profileChange, changeResultsMap,
				invalidChangeSateIds)) {
			return;
		}
		long profileId = profileChange.getProfileId();

		ProfileInstanceRelation instanceRelation = new ProfileInstanceRelation();
		instanceRelation.setProfileId(profileId);
		List<Instance> instances = new ArrayList<>(1);
		instances.add(new Instance(instanceId, resourceInstance.getParentId()));
		instanceRelation.setInstances(instances);
		ProfileDeployInfoWrapper wrapper = getProfileDeployInfoWrapper(
				profileDeployMap, nodeGroupId);

		/**
		 * 父资源实例加入监控，需要准备通知给自定义指标
		 */
		if (resourceInstance.getParentId() <= 0) {
			wrapper.addMonitorInstance.add(instanceId);
		}
		wrapper.changeId.add(profileChange.getProfileChangeId());
		ProfileDeployInfo info = new ProfileDeployInfo();
		info.setAction(profileChange.getProfileChangeEnum().name());
		info.setDeployData(instanceRelation);
		wrapper.info.add(info);
		if (logger.isTraceEnabled()) {
			logger.trace("addInstancesMonitor end instanceId="
					+ profileChange.getSource());
		}
	}

	private ProfileDeployInfoWrapper getProfileDeployInfoWrapper(
			Map<String, ProfileDeployInfoWrapper> profileDeployMap,
			String nodeGroupId) {
		ProfileDeployInfoWrapper wrapper = null;
		if (profileDeployMap.containsKey(nodeGroupId)) {
			wrapper = profileDeployMap.get(nodeGroupId);
		} else {
			wrapper = new ProfileDeployInfoWrapper();
			wrapper.addMonitorInstance = new HashSet<>();
			wrapper.changeId = new HashSet<Long>();
			wrapper.info = new ArrayList<ProfileDeployInfo>();
			profileDeployMap.put(nodeGroupId, wrapper);
		}
		return wrapper;
	}

	/**
	 * 删除时间基线的设置
	 * 
	 * @param timelineIds
	 *            基线id
	 */
	private void deleteTimelines(
			Map<String, ProfileDeployInfoWrapper> profileDeployMap,
			ProfieChange profileChange,
			Map<Long, Map<String, Boolean>> changeResultsMap,
			List<Long> invalidChangeSateIds,
			Map<Long, List<Long>> profileInstanceMap) {
		if (logger.isTraceEnabled()) {
			logger.trace("deleteTimelines start profileId ="
					+ profileChange.getProfileId() + " timelineId="
					+ profileChange.getSource());
		}
		Long timelineId = Long.valueOf(profileChange.getSource());
		// key: groupNodeId
		Set<String> groupNodeIds = getNodeGroupsByProfileId(profileChange,
				changeResultsMap, invalidChangeSateIds, profileInstanceMap);
		if (groupNodeIds == null) {
			return;
		}
		ProfileDeployInfo info = new ProfileDeployInfo();
		info.setAction(profileChange.getProfileChangeEnum().name());
		info.setDeployData(timelineId);

		for (String nodeGroupId : groupNodeIds) {
			ProfileDeployInfoWrapper wrapper = getProfileDeployInfoWrapper(
					profileDeployMap, nodeGroupId);
			wrapper.changeId.add(profileChange.getProfileChangeId());
			wrapper.info.add(info);
		}
		if (logger.isTraceEnabled()) {
			logger.trace("deleteTimelines end profileId ="
					+ profileChange.getProfileId() + " timelineId="
					+ profileChange.getSource());
		}
	}

	private String getResourceIdByProfileId(
			Map<Long, String> profileResourceIdMap, Long profileId) {
		if (profileResourceIdMap.containsKey(profileId)) {
			return profileResourceIdMap.get(profileId);
		} else {
			ProfileInfo p = null;
			try {
				p = profileService.getProfileBasicInfoByProfileId(profileId
						.longValue());
				if (p != null) {
					profileResourceIdMap.put(profileId, p.getResourceId());
					return p.getResourceId();
				} else {
					if (logger.isWarnEnabled()) {
						logger.warn("getResourceIdByProfileId profile not exist.profileId="
								+ profileId);
					}
				}
			} catch (ProfilelibException e) {
				if (logger.isErrorEnabled()) {
					logger.error("getResourceIdByProfileId", e);
				}
			}
		}
		return null;
	}

	private void addTimeline(
			Map<String, ProfileDeployInfoWrapper> profileDeployMap,
			ProfieChange profileChange,
			Map<Long, Map<String, Boolean>> changeResultsMap,
			List<Long> invalidChangeSateIds, Map<Long, Timeline> timelineMap,
			Map<Long, List<Long>> profileInstanceMap,
			Map<Long, String> profileResourceIdMap) {

		// key: groupNodeId
		Set<String> groupNodeIds = getNodeGroupsByProfileId(profileChange,
				changeResultsMap, invalidChangeSateIds, profileInstanceMap);
		if (groupNodeIds == null) {
			return;
		}
		Timeline timeline = null;
		Long timelineId = Long.valueOf(profileChange.getSource());
		if (timelineMap.containsKey(timelineId)) {
			timeline = timelineMap.get(timelineId);
		} else {
			try {
				timeline = timelineService.getTimelinesById(timelineId);
				timelineMap.put(timelineId, timeline);
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("addOrUpdateTimeline profileChange error!", e);
				}
			}
		}
		if (timeline == null) {
			if (logger.isWarnEnabled()) {
				StringBuilder b = new StringBuilder();
				b.append("timeline  not exists,profileChangeId=");
				b.append(profileChange.getProfileChangeId());
				b.append(" timelineId=").append(timelineId);
				logger.warn(b);
			}
			invalidChangeSateIds.add(profileChange.getProfileChangeId());
			return;
		}
		TimelineInfo timelineInfo = timeline.getTimelineInfo();
		String resourceId = getResourceIdByProfileId(profileResourceIdMap,
				timelineInfo.getProfileId());
		if (resourceId == null) {
			if (logger.isWarnEnabled()) {
				StringBuilder b = new StringBuilder();
				b.append("resourceId not exists,profileChangeId=");
				b.append(profileChange.getProfileChangeId());
				b.append(" ProfileId=").append(timelineInfo.getProfileId());
				logger.warn(b);
			}
			invalidChangeSateIds.add(profileChange.getProfileChangeId());
			return;
		}
		TimelineProfileMetricMonitor timelineProfileMetricMonitor = new TimelineProfileMetricMonitor();
		timelineProfileMetricMonitor.setEndTime(timelineInfo.getEndTime());
		timelineProfileMetricMonitor.setStartTime(timelineInfo.getStartTime());
		timelineProfileMetricMonitor.setProfileId(timelineInfo.getProfileId());
		timelineProfileMetricMonitor.setTimelineId(timelineId);
		timelineProfileMetricMonitor
				.setTimelineType(timelineInfo.getTimeLineType());
		List<ProfileMetric> profileMetrics = timeline.getMetricSetting()
				.getMetrics();
		if (profileMetrics != null && !profileMetrics.isEmpty()) {
			List<ProfileMetricMonitor> profileMetricMonitors = new ArrayList<>();
			for (ProfileMetric profileMetric : profileMetrics) {
				ResourceMetricDef mDef = capacityService.getResourceMetricDef(
						resourceId, profileMetric.getMetricId());
				if (mDef == null) {
					if (logger.isErrorEnabled()) {
						StringBuilder b = new StringBuilder(
								"toProfileMetricMonitors");
						b.append(" profileId=").append(
								profileMetric.getProfileId());
						b.append(" resourceId=").append(resourceId);
						b.append(" metricId=").append(
								profileMetric.getMetricId());
						logger.error(b.toString());
					}
					continue;
				}
				ProfileMetricMonitor profileMetricMonitor = new ProfileMetricMonitor();
				profileMetricMonitor.setMetricId(profileMetric.getMetricId());
				profileMetricMonitor.setMonitor(profileMetric.isMonitor());
				profileMetricMonitor.setMonitorFeq(FrequentEnum
						.valueOf(profileMetric.getDictFrequencyId()));
				profileMetricMonitor.setProfileId(profileMetric.getProfileId());
				profileMetricMonitor.setTimeline(true);
				profileMetricMonitor.setTimelineId(timelineId);
				/**
				 * 保证可用性指标在前
				 */
				if (mDef.getMetricType() == MetricTypeEnum.AvailabilityMetric) {
					profileMetricMonitors.add(0, profileMetricMonitor);
				} else {
					profileMetricMonitors.add(profileMetricMonitor);
				}
			}
			timelineProfileMetricMonitor
					.setProfileMetricMonitors(profileMetricMonitors);
		}

		ProfileDeployInfo info = new ProfileDeployInfo();
		info.setAction(profileChange.getProfileChangeEnum().name());
		info.setDeployData(timelineProfileMetricMonitor);
		for (String nodeGroupId : groupNodeIds) {
			ProfileDeployInfoWrapper wrapper = getProfileDeployInfoWrapper(
					profileDeployMap, nodeGroupId);
			wrapper.changeId.add(profileChange.getProfileChangeId());
			wrapper.info.add(info);
		}
	}

	private Set<String> getNodeGroupsByProfileId(ProfieChange profileChange,
			Map<Long, Map<String, Boolean>> changeResultsMap,
			List<Long> invalidChangeSateIds,
			Map<Long, List<Long>> profileInstanceMap) {
		Long profileId = profileChange.getProfileId();
		long profileChangeId = profileChange.getProfileChangeId();

		List<Long> instanceIds = null;
		// 先从集合中获取策略绑定的实例信息
		if (profileInstanceMap.containsKey(profileId)) {
			instanceIds = profileInstanceMap.get(profileId);
		} else {
			try {
				instanceIds = profileService
						.getResourceInstanceByProfileId(profileId);
			} catch (Exception e1) {
				if (logger.isErrorEnabled()) {
					logger.error("getResourceInstanceByProfileId error", e1);
				}
			}

			if (instanceIds == null) {
				if (logger.isDebugEnabled()) {
					logger.debug("getNodeGroupsByProfileId profileId="
							+ profileId + "not found instances.");
				}
				invalidChangeSateIds.add(profileChange.getProfileChangeId());
				return null;
			}
		}

		// key: groupNodeId
		HashSet<String> groupNodeIds = new HashSet<String>();

		for (long instanceId : instanceIds) {
			ResourceInstance resourceInstance = getResourceInstance(instanceId);
			if (resourceInstance == null) {
				if (logger.isDebugEnabled()) {
					logger.debug("ResourceInstance is not exist.instanceId="
							+ instanceId);
				}
				continue;
			}
			String nodeGroupId = resourceInstance.getDiscoverNode();
			if (nodeGroupId == null) {
				if (logger.isWarnEnabled()) {
					logger.warn("changeMetricsMonitor instance's nodeGroupId not found instanceId="
							+ instanceId);
				}
				continue;
			}
			groupNodeIds.add(nodeGroupId);
		}
		if (groupNodeIds.isEmpty()) {
			invalidChangeSateIds.add(profileChange.getProfileChangeId());
			if (logger.isInfoEnabled()) {
				logger.info("no nodeGroupId. profileChangeId="
						+ profileChangeId);
			}
			return null;
		}
		return groupNodeIds;
	}

	private void setService() {
		client = SpringBeanUtil.getBean(OCRPCClient.class);
		customMetricChangeService = SpringBeanUtil
				.getBean(CustomMetricChangeService.class);
		resourceInstanceService = SpringBeanUtil.getBean(ResourceInstanceExtendService.class);
		profileChangeService = SpringBeanUtil
				.getBean(ProfileChangeService.class);
		profileService = SpringBeanUtil.getBean(ProfileService.class);
		timelineService = SpringBeanUtil.getBean(TimelineService.class);
		capacityService = SpringBeanUtil.getBean(CapacityService.class);
	}

	private List<ProfieChange> getChangeInfos() {
		return profileChangeService.getDispatcherProfileChanges(MAX_CHANGE);
	}

	private boolean isIgnoreChange(String discoveryNodeGroupId,
			ProfieChange profileChange,
			Map<Long, Map<String, Boolean>> changeResultsMap,
			List<Long> invalidChangeSateIds) {
		Long profileChangeId = profileChange.getProfileChangeId();
		if (changeResultsMap.containsKey(profileChangeId)) {
			Map<String, Boolean> resultMap = changeResultsMap
					.get(profileChangeId);
			if (resultMap.containsKey(discoveryNodeGroupId)
					&& resultMap.get(discoveryNodeGroupId).booleanValue()) {
				invalidChangeSateIds.add(profileChangeId);
				if (logger.isInfoEnabled()) {
					StringBuilder b = new StringBuilder();
					b.append(
							"selectProfileChanges change's result is ok.ignore it.changeId=")
							.append(profileChangeId);
					b.append(" discoveryNodeGroupId=").append(
							discoveryNodeGroupId);
					logger.info(b.toString());
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取原来的处理结果
	 * 
	 * @param changeIds
	 * @return
	 */
	private Map<Long, Map<String, Boolean>> getChangeResultMap(
			List<Long> changeIds) {
		Map<Long, Map<String, Boolean>> changeResult = new HashMap<>();
		List<ProfileChangeResult> changeResults = profileChangeService
				.getProfileChangeResultByProfileChangeId(changeIds);
		if (changeResults != null) {
			for (ProfileChangeResult profileChangeResult : changeResults) {
				String nodeGroupId = String.valueOf(profileChangeResult
						.getDcsGroupId());
				Long changeId = profileChangeResult.getProfileChangeId();
				if (changeResult.containsKey(changeId)) {
					changeResult.get(changeId)
							.put(nodeGroupId,
									Boolean.valueOf(profileChangeResult
											.isResultState()));
				} else {
					Map<String, Boolean> map = new HashMap<>();
					map.put(nodeGroupId, Boolean.valueOf(profileChangeResult
							.isResultState()));
					changeResult.put(changeId, map);
				}
			}
		}
		return changeResult;
	}

	private ProfileCollectDeployMBean getCollectorMbean(String groupNodeId) {
		ProfileCollectDeployMBean profileCollectDeployMBean = null;
		try {
			profileCollectDeployMBean = client.getRemoteSerivce(
					Integer.parseInt(groupNodeId),
					ProfileCollectDeployMBean.class);
			if (profileCollectDeployMBean == null) {
				if (logger.isWarnEnabled()) {
					logger.warn("ProfileCollectDeployMBean not found!");
				}
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("getCollectorMbean", e);
			}
		}
		return profileCollectDeployMBean;
	}

	private void cancelMonitor(
			Map<String, ProfileDeployInfoWrapper> profileDeployMap,
			ProfieChange profileChange,
			Map<Long, Map<String, Boolean>> changeResultsMap,
			List<Long> invalidChangeSateIds) {
		if (logger.isTraceEnabled()) {
			logger.trace("cancelMonitor start instanceId = "
					+ profileChange.getSource());
		}
		cancelInstance(profileDeployMap, profileChange, changeResultsMap, invalidChangeSateIds);
		if (logger.isTraceEnabled()) {
			logger.trace("cancelMonitor end instanceId = "
					+ profileChange.getSource());
		}
	}


	private void cancelLastMonitor(
			Map<String, ProfileDeployInfoWrapper> profileDeployMap,
			ProfieChange profileChange,
			Map<Long, Map<String, Boolean>> changeResultsMap,
			List<Long> invalidChangeSateIds) {
		if (logger.isTraceEnabled()) {
			logger.trace("cancelLastMonitor start instanceId = "
					+ profileChange.getSource());
		}
		cancelInstance(profileDeployMap, profileChange, changeResultsMap, invalidChangeSateIds);
		if (logger.isTraceEnabled()) {
			logger.trace("cancelLastMonitor end instanceId = "
					+ profileChange.getSource());
		}
	}
	
	private void cancelInstance(Map<String, ProfileDeployInfoWrapper> profileDeployMap,
			ProfieChange profileChange,
			Map<Long, Map<String, Boolean>> changeResultsMap,
			List<Long> invalidChangeSateIds){
		// key：nodegroupId value: 删除监控同步到采集器
				long instanceId = Long.parseLong(profileChange.getSource());
				ResourceInstance resourceInstance = getResourceInstance(instanceId);
				String nodeGroupId = null;
				if (resourceInstance == null) {
					if (logger.isDebugEnabled()) {
						logger.debug("ResourceInstance is not exist.instanceId="
								+ instanceId);
					}
				} else {
					nodeGroupId = resourceInstance.getDiscoverNode();
				}
				if (nodeGroupId == null) {
					if (logger.isWarnEnabled()) {
						logger.warn("addInstancesMonitor instance's nodeGroupId nof found.instanceId="
								+ instanceId);
					}
					invalidChangeSateIds.add(profileChange.getProfileChangeId());
					return;
				}
				if (isIgnoreChange(nodeGroupId, profileChange, changeResultsMap,
						invalidChangeSateIds)) {
					return;
				}
				/*
				 * 删除监控实例数据
				 */
				ProfileDeployInfoWrapper wrapper = getProfileDeployInfoWrapper(
						profileDeployMap, nodeGroupId);
				wrapper.changeId.add(profileChange.getProfileChangeId());
				ProfileDeployInfo info = new ProfileDeployInfo();
				info.setAction(profileChange.getProfileChangeEnum().name());
				info.setDeployData(instanceId);
				wrapper.info.add(info);
				if (logger.isTraceEnabled()) {
					logger.trace("cancelMonitor end instanceId = "
							+ profileChange.getSource());
				}
	}
	
	private void deleteProfile(
			Map<String, ProfileDeployInfoWrapper> profileDeployMap,
			ProfieChange profileChange,
			Map<Long, Map<String, Boolean>> changeResultsMap,
			List<Long> invalidChangeSateIds,
			Map<String, Map<Long, ProfileDeployInfo>> profileDeleteMap) {
		if (logger.isTraceEnabled()) {
			logger.trace("deleteProfile start profileId="
					+ profileChange.getProfileId() + " instanceId="
					+ profileChange.getSource());
		}
		long instanceId = Long.parseLong(profileChange.getSource());
		ResourceInstance resourceInstance = getResourceInstance(instanceId);
		String nodeGroupId = null;
		if (resourceInstance == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("ResourceInstance is not exist.instanceId="
						+ instanceId);
			}
		} else {
			nodeGroupId = resourceInstance.getDiscoverNode();
		}
		if (nodeGroupId == null) {
			if (logger.isWarnEnabled()) {
				logger.warn("addInstancesMonitor instance's nodeGroupId nof found.instanceId="
						+ instanceId);
			}
			invalidChangeSateIds.add(profileChange.getProfileChangeId());
			return;
		}
		if (isIgnoreChange(nodeGroupId, profileChange, changeResultsMap,
				invalidChangeSateIds)) {
			return;
		}

		Long profileId = profileChange.getProfileId();
		if (profileDeleteMap.containsKey(nodeGroupId)
				&& profileDeleteMap.get(nodeGroupId).containsKey(profileId)) {
			if (logger.isTraceEnabled()) {
				logger.trace("deleteProfile end.ignore repeat action on the nodeGroupId for the profile.nodeGroupId="
						+ nodeGroupId);
			}
			invalidChangeSateIds.add(profileChange.getProfileChangeId());
			// TODO:将changeId放到ProfileDeployInfoWrapper中。但是还没有想到好的办法。
			return;
		}

		ProfileDeployInfoWrapper wrapper = getProfileDeployInfoWrapper(
				profileDeployMap, nodeGroupId);
		wrapper.changeId.add(profileChange.getProfileChangeId());
		ProfileDeployInfo info = new ProfileDeployInfo();
		info.setAction(profileChange.getProfileChangeEnum().name());
		info.setDeployData(profileId);
		wrapper.info.add(info);
		if (profileDeleteMap.containsKey(nodeGroupId)) {
			profileDeleteMap.get(nodeGroupId).put(profileId, info);
		} else {
			Map<Long, ProfileDeployInfo> map = new HashMap<>();
			map.put(profileId, info);
			profileDeleteMap.put(nodeGroupId, map);
		}
		if (logger.isTraceEnabled()) {
			logger.trace("deleteProfile end");
		}
	}

	private void changeMetricsMonitor(
			Map<String, ProfileDeployInfoWrapper> profileDeployMap,
			ProfieChange profileChange,
			Map<Long, Map<String, Boolean>> changeResultsMap,
			List<Long> invalidChangeSateIds,
			Map<String, Map<Long, ProfileDeployInfo>> profileDeleteMap,
			Map<Long, List<Long>> profileInstanceMap,
			Map<Long, Map<String, ProfileMetric>> profileMetricMap) {
		if (logger.isTraceEnabled()) {
			logger.trace("changeMetricsMonitor start profileId="
					+ profileChange.getProfileId() + "metriceId"
					+ profileChange.getSource());
		}
		// key: groupNodeId
		Set<String> groupNodeIds = getNodeGroupsByProfileId(profileChange,
				changeResultsMap, invalidChangeSateIds, profileInstanceMap);

		if (groupNodeIds == null) {
			return;
		}
		Long profileId = profileChange.getProfileId();
		// 先从集合中获取策略对应的指标
		ProfileMetric profileMetric = null;
		String metricId = profileChange.getSource();
		Map<String, ProfileMetric> cacheMetric = null;
		if (profileMetricMap.containsKey(profileId)) {
			cacheMetric = profileMetricMap.get(profileId);
		} else {
			cacheMetric = new HashMap<String, ProfileMetric>();
			profileMetricMap.put(profileId, cacheMetric);
		}
		if (cacheMetric.containsKey(metricId)) {
			profileMetric = cacheMetric.get(metricId);
		} else {
			try {
				profileMetric = profileService.getMetricByProfileIdAndMetricId(
						profileId, metricId);
				cacheMetric.put(metricId, profileMetric);
			} catch (ProfilelibException e) {
				if (logger.isErrorEnabled()) {
					logger.error("getMetricByProfileIdAndMetricId error", e);
				}
			}
		}
		if (profileMetric == null) {
			if (logger.isWarnEnabled()) {
				StringBuilder b = new StringBuilder();
				b.append("changeMetricsMonitor  not found profileId=").append(
						profileId);
				b.append(" metricId=").append(metricId);
				logger.warn(b);
			}
			invalidChangeSateIds.add(profileChange.getProfileChangeId());
			return;
		}

		ProfileMetricMonitor profileMetricMonitor = new ProfileMetricMonitor();
		profileMetricMonitor.setMetricId(profileMetric.getMetricId());
		profileMetricMonitor.setMonitor(profileMetric.isMonitor());
		profileMetricMonitor.setMonitorFeq(FrequentEnum.valueOf(profileMetric
				.getDictFrequencyId()));
		profileMetricMonitor.setProfileId(profileMetric.getProfileId());
		profileMetricMonitor.setTimeline(false);

		ProfileDeployInfo info = new ProfileDeployInfo();
		info.setAction(profileChange.getProfileChangeEnum().name());
		info.setDeployData(profileMetricMonitor);

		for (String nodeGroupId : groupNodeIds) {
			ProfileDeployInfoWrapper wrapper = getProfileDeployInfoWrapper(
					profileDeployMap, nodeGroupId);
			wrapper.changeId.add(profileChange.getProfileChangeId());
			wrapper.info.add(info);
		}
		if (logger.isTraceEnabled()) {
			logger.trace("changeMetricsMonitor end");
		}
	}

	private void updateTimelineInfo(Map<String, ProfileDeployInfoWrapper> profileDeployMap,
			ProfieChange profileChange,
			Map<Long, Map<String, Boolean>> changeResultsMap,
			List<Long> invalidChangeSateIds, Map<Long, Timeline> timelineMap,
			Map<Long, List<Long>> profileInstanceMap,
			Map<Long, String> profileResourceIdMap){
		if (logger.isTraceEnabled()) {
			logger.trace("updateTimelineInfo start timelineId=" + profileChange.getTimelineId());
		}
		// key: groupNodeId
		Set<String> groupNodeIds = getNodeGroupsByProfileId(profileChange,
				changeResultsMap, invalidChangeSateIds, profileInstanceMap);
		if (groupNodeIds == null) {
			return;
		}
		Timeline timeline = null;
		Long timelineId = Long.valueOf(profileChange.getTimelineId());
		TimelineInfo timelineInfo = null;
		if (timelineMap.containsKey(timelineId)) {
			timeline = timelineMap.get(timelineId);
		} else {
			try {
				timelineInfo = timelineService.getTimelineInfosByTimelineId(timelineId);
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("addOrUpdateTimeline profileChange error!", e);
				}
			}
		}
		if (timeline == null && timelineInfo == null) {
			if (logger.isWarnEnabled()) {
				StringBuilder b = new StringBuilder();
				b.append("timeline  not exists,profileChangeId=");
				b.append(profileChange.getProfileChangeId());
				b.append(" timelineId=").append(timelineId);
				logger.warn(b);
			}
			invalidChangeSateIds.add(profileChange.getProfileChangeId());
			return;
		}
		if(timeline != null){
			timelineInfo = timeline.getTimelineInfo();
		}
		String resourceId = getResourceIdByProfileId(profileResourceIdMap,
				timelineInfo.getProfileId());
		if (resourceId == null) {
			if (logger.isWarnEnabled()) {
				StringBuilder b = new StringBuilder();
				b.append("resourceId not exists,profileChangeId=");
				b.append(profileChange.getProfileChangeId());
				b.append(" ProfileId=").append(timelineInfo.getProfileId());
				logger.warn(b);
			}
			invalidChangeSateIds.add(profileChange.getProfileChangeId());
			return;
		}
		TimelineProfileMetricMonitor timelineProfileMetricMonitor = new TimelineProfileMetricMonitor();
		timelineProfileMetricMonitor.setEndTime(timelineInfo.getEndTime());
		timelineProfileMetricMonitor.setStartTime(timelineInfo.getStartTime());
		timelineProfileMetricMonitor.setProfileId(timelineInfo.getProfileId());
		timelineProfileMetricMonitor.setTimelineId(timelineId);
		timelineProfileMetricMonitor
				.setTimelineType(timelineInfo.getTimeLineType());
		timelineProfileMetricMonitor.setProfileId(profileChange.getProfileId());
		ProfileDeployInfo info = new ProfileDeployInfo();
		info.setAction(profileChange.getProfileChangeEnum().name());
		info.setDeployData(timelineProfileMetricMonitor);
		for (String nodeGroupId : groupNodeIds) {
			ProfileDeployInfoWrapper wrapper = getProfileDeployInfoWrapper(
					profileDeployMap, nodeGroupId);
			wrapper.changeId.add(profileChange.getProfileChangeId());
			wrapper.info.add(info);
		}
		if (logger.isTraceEnabled()) {
			logger.trace("updateTimelineInfo end timelineId=" + profileChange.getTimelineId());
		}
	}
	
	private void changeTimelineMetricsMonitor(
			Map<String, ProfileDeployInfoWrapper> profileDeployMap,
			ProfieChange profileChange,
			Map<Long, Map<String, Boolean>> changeResultsMap,
			List<Long> invalidChangeSateIds,
			Map<String, Map<Long, ProfileDeployInfo>> profileDeleteMap,
			Map<Long, List<Long>> profileInstanceMap,
			Map<Long, Map<String, ProfileMetric>> profileMetricMap) {
		if (logger.isTraceEnabled()) {
			StringBuilder b = new StringBuilder();
			b.append("changeTimelineMetricsMonitor start profileId=");
			b.append(profileChange.getProfileId());
			b.append("metriceId");
			b.append(profileChange.getSource());
			logger.trace(b);
		}
		// key: groupNodeId
		Set<String> groupNodeIds = getNodeGroupsByProfileId(profileChange,
				changeResultsMap, invalidChangeSateIds, profileInstanceMap);

		if (groupNodeIds == null) {
			return;
		}
		Long profileId = profileChange.getProfileId();
		Long timelineId = profileChange.getTimelineId();
		// 先从集合中获取策略对应的指标
		ProfileMetric profileMetric = null;
		String metricId = profileChange.getSource();
		try {
			profileMetric = timelineService.getMetricByTimelineIdAndMetricId(timelineId, metricId);
		} catch (ProfilelibException e) {
			if (logger.isErrorEnabled()) {
				logger.error("getMetricByProfileIdAndMetricId error", e);
			}
		}
		if (profileMetric == null) {
			if (logger.isWarnEnabled()) {
				StringBuilder b = new StringBuilder();
				b.append("changeMetricsMonitor  not found profileId=").append(
						profileId);
				b.append(" metricId=").append(metricId);
				logger.warn(b);
			}
			invalidChangeSateIds.add(profileChange.getProfileChangeId());
			return;
		}

		ProfileMetricMonitor profileMetricMonitor = new ProfileMetricMonitor();
		profileMetricMonitor.setMetricId(profileMetric.getMetricId());
		profileMetricMonitor.setMonitor(profileMetric.isMonitor());
		profileMetricMonitor.setMonitorFeq(FrequentEnum.valueOf(profileMetric
				.getDictFrequencyId()));
		profileMetricMonitor.setProfileId(profileMetric.getProfileId());
		profileMetricMonitor.setTimelineId(timelineId);
		profileMetricMonitor.setTimeline(true);

		ProfileDeployInfo info = new ProfileDeployInfo();
		info.setAction(profileChange.getProfileChangeEnum().name());
		info.setDeployData(profileMetricMonitor);

		for (String nodeGroupId : groupNodeIds) {
			ProfileDeployInfoWrapper wrapper = getProfileDeployInfoWrapper(
					profileDeployMap, nodeGroupId);
			wrapper.changeId.add(profileChange.getProfileChangeId());
			wrapper.info.add(info);
		}
		if (logger.isTraceEnabled()) {
			logger.trace("changeTimelineMetricsMonitor end");
		}
	}
	
	private void selectAndGroupProfileDeployInfos(
			Map<String, ProfileDeployInfoWrapper> profileDeployMap,
			List<ProfieChange> changes,
			Map<Long, Map<String, Boolean>> changeResultsMap,
			Map<Long, List<Long>> profileInstanceMap,
			Map<Long, Map<String, ProfileMetric>> profileMetricMap,
			List<Long> invalidChangeSateIds,
			Map<String, Map<Long, ProfileDeployInfo>> profileDeleteMap,
			Map<Long, Timeline> timelineMap,
			Map<Long, String> profileResourceIdMap) {
		for (ProfieChange profileChange : changes) {
			ProfileChangeEnum changeEnum = profileChange.getProfileChangeEnum();
			switch (changeEnum) {
			case ADD_MONITOR:
				addInstancesMonitor(profileDeployMap, profileChange,
						changeResultsMap, invalidChangeSateIds);
				break;
			case CANCEL_MONITOR:
				cancelMonitor(profileDeployMap, profileChange,
						changeResultsMap, invalidChangeSateIds);
				break;
			case CANCEL_LAST_MONITOR:
				cancelLastMonitor(profileDeployMap, profileChange,
						changeResultsMap, invalidChangeSateIds);
				break;
			case DELETE_PROFILE:
				deleteProfile(profileDeployMap, profileChange,
						changeResultsMap, invalidChangeSateIds,
						profileDeleteMap);
				break;
			case UPDATE_METRIC_MONITOR:
				changeMetricsMonitor(profileDeployMap, profileChange,
						changeResultsMap, invalidChangeSateIds,
						profileDeleteMap, profileInstanceMap, profileMetricMap);
				break;
			case UPDATE_METRIC_MONITORFEQ:
				changeMetricsMonitor(profileDeployMap, profileChange,
						changeResultsMap, invalidChangeSateIds,
						profileDeleteMap, profileInstanceMap, profileMetricMap);
				break;
			case UPDATE_TIMELINE_MONITOR:
				changeTimelineMetricsMonitor(profileDeployMap, profileChange,
						changeResultsMap, invalidChangeSateIds,
						profileDeleteMap, profileInstanceMap, profileMetricMap);
				break;
			case UPDATE_TIMELINE_MONITORFEQ:
				changeTimelineMetricsMonitor(profileDeployMap, profileChange,
						changeResultsMap, invalidChangeSateIds,
						profileDeleteMap, profileInstanceMap, profileMetricMap);
				break;
			case UPDATE_TIMELINE_TIME:
				updateTimelineInfo(profileDeployMap, profileChange,
						changeResultsMap, invalidChangeSateIds, timelineMap,
						profileInstanceMap, profileResourceIdMap);
				break;
			case ADD_TIMELINE:
				addTimeline(profileDeployMap, profileChange,
				changeResultsMap, invalidChangeSateIds, timelineMap,
				profileInstanceMap, profileResourceIdMap);
				break;
			case DELETE_TIMELINE:
				deleteTimelines(profileDeployMap, profileChange,
						changeResultsMap, invalidChangeSateIds,
						profileInstanceMap);
				break;
			default:
				break;
			}
		}
	}

	private void deploy(Map<String, ProfileDeployInfoWrapper> profileDeployMap,
			List<Long> validChangeIds,
			Map<Long, Map<String, Boolean>> changeResultsMap) {
		if (logger.isTraceEnabled()) {
			logger.trace("deploy start");
		}
		Set<Long> failChangeIds = new HashSet<Long>();
		for (Entry<String, ProfileDeployInfoWrapper> item : profileDeployMap
				.entrySet()) {
			String nodeGroupId = item.getKey();
			ProfileDeployInfoWrapper profileDeployInfoWrapper = item.getValue();
			ProfileCollectDeployMBean profileCollectDeployMBean = getCollectorMbean(nodeGroupId);
			if (profileCollectDeployMBean == null) {
				failChangeIds.addAll(profileDeployInfoWrapper.changeId);
				continue;
			} else {
				int size = profileDeployInfoWrapper.changeId.size();
				boolean isSucess = true;
				try {
					profileCollectDeployMBean
							.deployProfileInfo(profileDeployInfoWrapper.info
									.toArray(new ProfileDeployInfo[size]));
					validChangeIds.addAll(profileDeployInfoWrapper.changeId);
					if (!profileDeployInfoWrapper.addMonitorInstance.isEmpty()) {
						List<CustomMetricChange> customMetricChanges = new ArrayList<>(
								profileDeployInfoWrapper.addMonitorInstance
										.size());
						for (Long instanceId : profileDeployInfoWrapper.addMonitorInstance) {
							CustomMetricChange customMetricChange = new CustomMetricChange();
							customMetricChange.setInstanceId(instanceId);
							customMetricChange
									.setOperateMode(CustomMetricChangeEnum.INSTANCE_MONITOR
											.name());
							customMetricChanges.add(customMetricChange);
						}
						customMetricChangeService
								.occurCustomMetricChanges(customMetricChanges);
					}
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {
						logger.error("deployProfileInfo error.", e);
					}
					failChangeIds.addAll(profileDeployInfoWrapper.changeId);
					isSucess = false;
				}
				// 需要添加的数据
				List<ProfileChangeResult> addProfileChangeResults = new ArrayList<ProfileChangeResult>(
						size);
				// 需要修改的数据
				List<ProfileChangeResult> udpateProfileChangeResults = new ArrayList<ProfileChangeResult>(
						size);
				for (Long profileChangeId : profileDeployInfoWrapper.changeId) {
					ProfileChangeResult profileChangeResult = new ProfileChangeResult();
					profileChangeResult.setDcsGroupId(Integer
							.parseInt(nodeGroupId));
					profileChangeResult.setProfileChangeId(profileChangeId);
					profileChangeResult.setResultState(isSucess);
					if (changeResultsMap.containsKey(profileChangeId)) {
						Map<String, Boolean> oldGroupId = changeResultsMap
								.get(profileChangeId);
						if (oldGroupId.containsKey(nodeGroupId)) {
							if(isSucess){
								udpateProfileChangeResults.add(profileChangeResult);
							}
						} else {
							addProfileChangeResults.add(profileChangeResult);
						}
					} else {
						addProfileChangeResults.add(profileChangeResult);
					}
				}
				if (!addProfileChangeResults.isEmpty()) {
					// 成功
					profileChangeService
							.addProfileChangeResults(addProfileChangeResults);
				}
				if (!udpateProfileChangeResults.isEmpty()) {
					profileChangeService
							.updateProfileChangeResultSucceed(udpateProfileChangeResults);
				}
			}
		}
		/*
		 * 采集器Id,profileChangeId有交叉情况，只有所有的采集器都部署成功，profileChange 才成功
		 */
		HashMap<Long, Object> failChangeIdMap = new HashMap<>(
				failChangeIds.size());
		for (Long profileChangeId : failChangeIds) {
			failChangeIdMap.put(profileChangeId, null);
		}
		List<Long> validChangeIdList = new ArrayList<Long>(
				validChangeIds.size());
		for (Long profileChangeId : validChangeIds) {
			if (failChangeIdMap.containsKey(profileChangeId)) {
				continue;
			} else {
				validChangeIdList.add(profileChangeId);
			}
		}
		profileChangeService.updateProfileChangeSucceed(validChangeIdList);
		if (logger.isTraceEnabled()) {
			logger.trace("deploy end");
		}
	}

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		if (logger.isDebugEnabled()) {
			logger.debug("Profile Deploy Job start.");
		}
		if (!SpringBeanUtil.isSpringContextReady()) {
			logger.debug("spring bean not load! Profile Deploy Job end.");
			return;
		}
		setService();
		List<ProfieChange> changes = getChangeInfos();
		if (changes == null || changes.isEmpty()) {
			if (logger.isDebugEnabled()) {
				logger.debug("execute on profile changes was not found.");
			}
			return;
		}
		List<Long> allChangeIds = new ArrayList<>(changes.size());
		for (ProfieChange profieChange : changes) {
			allChangeIds.add(profieChange.getProfileChangeId());
		}
		/**
		 * Map<ProfileChangeId, Map<nodeGroupId, Boolean>>
		 */
		Map<Long, Map<String, Boolean>> changeResultsMap = getChangeResultMap(allChangeIds);
		/**
		 * 等待3s，等待本地cache超时，以便加载远程cache数据内容。
		 */
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			if (logger.isErrorEnabled()) {
				logger.error("execute", e);
			}
		}
		/**
		 * Map<profileId,List<InstanceId>> profileInstanceMap 用于缓存策略监控的资源实例
		 */
		Map<Long, List<Long>> profileInstanceMap = new HashMap<>();

		/**
		 * Map<nodeGroupId,ProfileDeployInfoWrapper> profileDeployMap
		 * 用于缓存同步到采集器的数据
		 */
		Map<String, ProfileDeployInfoWrapper> profileDeployMap = new HashMap<>();

		/**
		 * 用于缓存策略指标的信息 Map<ProfileId,Map<MetricId,ProfileMetric>>
		 * profileMetricMap
		 */
		Map<Long, Map<String, ProfileMetric>> profileMetricMap = new HashMap<>();

		/**
		 * Map<nodeGroupId, Map<ProfileId, ProfileDeployInfo>> profileDeleteMap
		 */
		Map<String, Map<Long, ProfileDeployInfo>> profileDeleteMap = new HashMap<>();

		/**
		 * Map<ProfileId,ResourceId> profileResourceIdMap
		 */
		Map<Long, String> profileResourceIdMap = new HashMap<>();

		/**
		 * 缓存基线对象
		 */
		Map<Long, Timeline> timelineMap = new HashMap<Long, Timeline>();

		List<Long> invalidChangeSateIds = new ArrayList<>();

		selectAndGroupProfileDeployInfos(profileDeployMap, changes,
				changeResultsMap, profileInstanceMap, profileMetricMap,
				invalidChangeSateIds, profileDeleteMap, timelineMap,
				profileResourceIdMap);
		// TODO:
		deploy(profileDeployMap, invalidChangeSateIds, changeResultsMap);
		if (logger.isDebugEnabled()) {
			logger.debug("job end:" + new Date());
		}
	}

	private class ProfileDeployInfoWrapper {
		List<ProfileDeployInfo> info;
		/**
		 * Set<InstanceId>
		 */
		Set<Long> addMonitorInstance;
		/**
		 * 成功或失败的profileId
		 */
		private Set<Long> changeId;
	}
}
