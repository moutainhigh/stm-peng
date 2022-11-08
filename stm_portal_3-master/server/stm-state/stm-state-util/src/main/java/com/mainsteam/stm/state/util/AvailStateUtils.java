package com.mainsteam.stm.state.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.state.Availability;
import com.mainsteam.stm.caplib.state.Collectibility;
import com.mainsteam.stm.caplib.state.ResourceState;
import com.mainsteam.stm.common.instance.obj.CollectStateEnum;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.metric.CustomMetricService;
import com.mainsteam.stm.metric.exception.CustomMetricException;
import com.mainsteam.stm.metric.obj.CustomMetric;
import com.mainsteam.stm.metric.obj.CustomMetricBind;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.TimelineService;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;
import com.mainsteam.stm.state.obj.AvailabilityMetricData;
import com.mainsteam.stm.state.obj.InstanceStateData;
import com.mainsteam.stm.state.obj.MetricStateData;
import com.mainsteam.stm.state.obj.ResourceInstanceState;

/**
 * @author 作者：ziw
 * @date 创建时间：2016年11月18日 下午3:28:05
 * @version 1.0
 */
public class AvailStateUtils {

	private static final Log logger = LogFactory.getLog(AvailStateUtils.class);

	private StateCacheUtils stateCacheUtils;

	private final TimelineService timelineService;
	private final ProfileService profileService;
	private final CustomMetricService customMetricService;
	private final CapacityService capacityService;
	private final InstanceStateDataCacheUtil stateDataCacheUtil;

	public AvailStateUtils(StateCacheUtils stateCacheUtils,
			TimelineService timelineService, ProfileService profileService,
			CustomMetricService customMetricService,
			CapacityService capacityService,
			InstanceStateDataCacheUtil stateDataCacheUtil) {
		super();
		this.stateCacheUtils = stateCacheUtils;
		this.timelineService = timelineService;
		this.profileService = profileService;
		this.customMetricService = customMetricService;
		this.capacityService = capacityService;
		this.stateDataCacheUtil = stateDataCacheUtil;
	}


	/**
	 * 根据资源实例ID获取当前资源下所有的可用性指标进行联合计算，即计算资源状态，资源状态取消‘未知’状态
	 *
	 * @param resourceInstance
	 * @return
	 */
	public ResourceInstanceState calculateResourceState(
			ResourceInstance resourceInstance,long timeLineId) throws ProfilelibException,
			CustomMetricException {
		AvailabilityMetricData availabilityMetricData = this.stateCacheUtils.getAvailabilityMetricDataIMemcache(resourceInstance.getId());
		ResourceInstanceState resourceInstanceState = new ResourceInstanceState(InstanceStateEnum.NORMAL, CollectStateEnum.COLLECTIBLE);
		InstanceStateData instanceStateData = stateDataCacheUtil.getInstanceState(resourceInstance.getId());
		if (availabilityMetricData != null) {
			Map<String, String> metricMap = availabilityMetricData.getMetricData();
			Map<String,ProfileMetric> profileOrTimelineMetricMaps = new HashMap<>(100);
			List<ProfileMetric> allMetrics = null;
			if(timeLineId > 0){
				allMetrics = timelineService.getMetricByTimelineId(timeLineId);
			}else{
				allMetrics = profileService.getMetricByInstanceId(resourceInstance.getId());
			}
			if(allMetrics != null){
				for (ProfileMetric m : allMetrics) {
					profileOrTimelineMetricMaps.put(m.getMetricId(),m);
				}
			}
			// 去掉未监控或者不告警的指标
			if (!metricMap.isEmpty()) {
				Iterator<String> iterator = metricMap.keySet().iterator();
				while (iterator.hasNext()) {
					String metricID = iterator.next();
					ProfileMetric profileMetric = profileOrTimelineMetricMaps.get(metricID);
					if (profileMetric != null
							&& (!profileMetric.isAlarm() || !profileMetric.isMonitor())) { // 未监控未告警
						iterator.remove();
					} else if (profileMetric == null) {
						List<CustomMetricBind> customMetricBinds = customMetricService.getCustomMetricBindsByMetricId(metricID);
						if (customMetricBinds != null && !customMetricBinds.isEmpty()) {
							boolean isBind = false;
							for (CustomMetricBind customMetricBind : customMetricBinds) {
								if (customMetricBind.getInstanceId() == resourceInstance.getId()) {
									isBind = true;
								}
							}
							if (!isBind) {
								iterator.remove();
							}
						}
						CustomMetric customMetric = customMetricService.getCustomMetric(metricID);
						if (customMetric != null
								&& customMetric.getCustomMetricInfo() != null &&
								(!customMetric.getCustomMetricInfo().isMonitor() ||
										!customMetric.getCustomMetricInfo().isAlert())) {
							try {
								iterator.remove();
							} catch (Exception e) {
							}
						}
					}
				}
			}

			if (!metricMap.isEmpty()) {
				ResourceState resourceState = null;
				try{
					resourceState =	capacityService.evaluateResourceState(resourceInstance.getResourceId(), metricMap);
				}catch (Exception e) {
					if(logger.isWarnEnabled()) {
						StringBuffer sb = new StringBuffer();
						sb.append("capacityService evaluateResourceState failed, instance id is");
						sb.append(resourceInstance.getId()).append(", resource id is ");
						sb.append(resourceInstance.getResourceId());
						logger.warn(sb.toString());
					}
					return null;
				}
				if (logger.isDebugEnabled()) {
					logger.debug("Calculate resource{"
							+ resourceInstance.getId() + ":"
							+ resourceInstance.getResourceId()
							+ "} state by capacity "
							+ JSONObject.toJSONString(resourceState)
							+ ", metric data is "
							+ JSONObject.toJSONString(metricMap));
				}

				if (resourceState.availability == Availability.AVAILABLE) {
					resourceInstanceState.setInstanceStateEnum(InstanceStateEnum.NORMAL);
				} else if (resourceState.availability == Availability.UNAVAILABLE) {
					resourceInstanceState.setInstanceStateEnum(InstanceStateEnum.CRITICAL);
				} else {
					resourceInstanceState.setInstanceStateEnum(InstanceStateEnum.UNKOWN);
				}
				if(logger.isInfoEnabled()) {
					if(null != instanceStateData && instanceStateData.getState() != resourceInstanceState.getInstanceStateEnum()) {
						logger.info("instance " + resourceInstance.getId() + " resource state changed, cached data is " +
								JSONObject.toJSONString(metricMap));
					}
				}
				if(InstanceStateEnum.UNKOWN == resourceInstanceState.getInstanceStateEnum()) {
					if (instanceStateData != null) {
						InstanceStateEnum newInstanceState = (instanceStateData.getState() == InstanceStateEnum.SERIOUS ||
								instanceStateData.getState() == InstanceStateEnum.NORMAL_CRITICAL
								|| instanceStateData.getState() == InstanceStateEnum.UNKOWN ||
								instanceStateData.getState() == InstanceStateEnum.UNKNOWN_NOTHING ||
								instanceStateData.getState() == InstanceStateEnum.NORMAL_NOTHING)
								? InstanceStateEnum.NORMAL : (instanceStateData.getState() == InstanceStateEnum.CRITICAL_NOTHING ? InstanceStateEnum.CRITICAL :
								instanceStateData.getState());

						resourceInstanceState.setInstanceStateEnum(newInstanceState);
					}else {
						resourceInstanceState.setInstanceStateEnum(InstanceStateEnum.NORMAL);
					}
				}

				if (resourceState.collectibility == Collectibility.COLLECTIBLE)
					resourceInstanceState.setCollectStateEnum(CollectStateEnum.COLLECTIBLE);
				else
					resourceInstanceState.setCollectStateEnum(CollectStateEnum.UNCOLLECTIBLE);
			}else {
				if(logger.isInfoEnabled()) {
					logger.info("instance can't find cached data while computing resource state," +
							" so using previous state first except for WARN or SERIOUS,and NORMAL second.");
				}
				if(null != instanceStateData) {
					if(InstanceStateEnum.WARN == instanceStateData.getState() || InstanceStateEnum.SERIOUS == instanceStateData.getState()){
						resourceInstanceState.setInstanceStateEnum(InstanceStateEnum.NORMAL);
					}else {
						resourceInstanceState.setInstanceStateEnum(instanceStateData.getState());
					}
					resourceInstanceState.setCollectStateEnum(instanceStateData.getCollectStateEnum());
				}
			}
		}else {
			if(logger.isInfoEnabled()) {
				logger.info("instance " + resourceInstance.getId() + "can't find cached data, using previous state or NORMAL second");
			}

			if(null != instanceStateData){
				if(InstanceStateEnum.WARN == instanceStateData.getState() || InstanceStateEnum.SERIOUS == instanceStateData.getState()){
					resourceInstanceState.setInstanceStateEnum(InstanceStateEnum.NORMAL);
				}else {
					resourceInstanceState.setInstanceStateEnum(instanceStateData.getState());
				}
				resourceInstanceState.setCollectStateEnum(instanceStateData.getCollectStateEnum());
			}
		}
		return resourceInstanceState;
	}


	/**
	 * 计算当前可用性指标的可用性状态
	 *
	 * @param resourceInstance
	 * @param metricID
	 * @param metricData
	 * @return
	 */
	public MetricStateEnum calculateAvailMetricState(ResourceInstance resourceInstance,
													 String metricID, String metricData, MetricStateData preMetricStateData) throws InstancelibException {
		Map<String, String> metricMap = new HashMap<String, String>(1);
		metricMap.put(metricID, metricData);
		if (resourceInstance == null || InstanceLifeStateEnum.NOT_MONITORED == resourceInstance.getLifeState()) {
			if (logger.isWarnEnabled()) {
				logger.warn("Instance life state is null or not monitored with " + resourceInstance.getId());
			}
			return null;
		}
		Availability availabilityState = null;
		try{
			availabilityState = capacityService.evaluateResourceAvailability(resourceInstance.getResourceId(), metricMap);
		}catch (Exception e) {
			if(logger.isWarnEnabled()) {
				StringBuffer sb = new StringBuffer();
				sb.append("capacityService evaluateResourceAvailability failed, instance is {");
				sb.append(resourceInstance.getId()).append(":").append(metricID).append("}, resource is ");
				sb.append(resourceInstance.getResourceId());
				logger.warn(sb.toString());
			}

			if(null == preMetricStateData)
				return null;
			else
				return preMetricStateData.getState();
		}
		if(availabilityState == Availability.AVAILABLE){
			if(logger.isDebugEnabled()){
				StringBuffer bf = new StringBuffer();
				bf.append("capacity calculateAvailMetricState AVAILABLE is {").append(resourceInstance.getId());
				bf.append(":").append(metricID);
				bf.append("}, value is ").append(metricMap);
				logger.debug(bf);
			}
			return MetricStateEnum.NORMAL;
		}else if(availabilityState == Availability.UNAVAILABLE){
			if(logger.isDebugEnabled()){
				StringBuffer bf = new StringBuffer();
				bf.append("capacity calculateAvailMetricState  UNAVAILABLE is {").append(resourceInstance.getId());
				bf.append(":").append(metricID);
				bf.append("}, value is ").append(metricMap);
				logger.debug(bf);
			}
			return MetricStateEnum.CRITICAL;
		}else {
			if(logger.isDebugEnabled()){
				StringBuffer bf = new StringBuffer();
				bf.append("calculateAvailMetricState unknown, using previous state. Instance is {").append(resourceInstance.getId());
				bf.append(":").append(metricID);
				bf.append("}");
				logger.debug(bf);
			}
			if (preMetricStateData != null) {
				return (preMetricStateData.getState() == MetricStateEnum.UNKOWN || preMetricStateData
						.getState() == MetricStateEnum.UNKNOWN_NOTHING) ? MetricStateEnum.NORMAL
						: preMetricStateData.getState();
			} else
				return MetricStateEnum.NORMAL;
		}

	}

}
