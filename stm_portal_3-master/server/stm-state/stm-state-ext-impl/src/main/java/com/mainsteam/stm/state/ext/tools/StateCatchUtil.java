package com.mainsteam.stm.state.ext.tools;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.InstanceStateService;
import com.mainsteam.stm.common.metric.MetricStateService;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.metric.CustomMetricService;
import com.mainsteam.stm.metric.exception.CustomMetricException;
import com.mainsteam.stm.metric.obj.CustomMetric;
import com.mainsteam.stm.metric.obj.CustomMetricInfo;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.TimelineService;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.ProfileInfo;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;
import com.mainsteam.stm.profilelib.obj.Timeline;
import com.mainsteam.stm.profilelib.obj.TimelineInfo;
import com.mainsteam.stm.state.ext.process.bean.AlarmStateQueue;
import com.mainsteam.stm.state.ext.process.bean.CompareInstanceState;
import com.mainsteam.stm.state.obj.InstanceStateData;
import com.mainsteam.stm.state.obj.MetricStateData;
import com.mainsteam.stm.state.thirdparty.ThirdPartyMetricStateService;
import com.mainsteam.stm.state.thirdparty.obj.ThirdPartyMetricStateData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component("stateCatchUtil")
public class StateCatchUtil {

	private static final Log logger = LogFactory.getLog(StateCatchUtil.class);
	public static final int PARENT_INST_FLAG = 0;
	@Autowired
	private InstanceStateService instanceStateService;
	@Autowired
	private StateComputeCacheUtil stateComputeCacheUtil;
	@Autowired
	private MetricStateService metricStateService;
	@Autowired
	private ResourceInstanceService resourceInstanceService;
	@Autowired
	private ThirdPartyMetricStateService thirdPartyMetricStateService; //加入第三方告警
	@Autowired
	private CapacityService capacityService;
	@Autowired
	private ProfileService profileService;
	@Autowired
	private TimelineService timelineService;
	@Autowired
	private CustomMetricService customMetricService;

	public InstanceStateData getInstanceState(long instanceID) {
		InstanceStateData data = stateComputeCacheUtil.getInstanceState(String.valueOf(instanceID));
		if (data == null) {
			data = instanceStateService.getState(instanceID);
			if (data != null) {
				stateComputeCacheUtil.setInstanceState(String.valueOf(instanceID), data);
			}
		}
		return data;
	}

	public boolean saveInstanceState(String key, InstanceStateData data) {

		boolean isSuccess = stateComputeCacheUtil.setInstanceState(key, data);
		if (!isSuccess) {
			logger.warn("failed to cache instance state with " + data);
		}
		return isSuccess;
	}

	public void saveInstanceState(InstanceStateData data) {
		if(saveInstanceState(String.valueOf(data.getInstanceID()), data)) {
			instanceStateService.addState(data);
		}
	}

	/**
	 * 清除资源状态：将资源的状态置为未知！ 在修改时，主动将影响指标调整为“可用性指标”
	 * 
	 * @param ides
	 */
	public void cleanInstanceStates(List<Long> ides) {
		for (long id : ides) {
			cleanInstanceState(id);
		}
	}

	public void cleanInstanceState(Long id) {
		stateComputeCacheUtil.deleteAvailabilityMetricData(id);
		stateComputeCacheUtil.deleteInstanceState(String.valueOf(id));
	}


	public void cleanMetricStates(List<Long> ides) {
		Set<MetricStateEnum> ignoreState = new HashSet<>(1);
		ignoreState.add(MetricStateEnum.NORMAL);
		List<MetricStateData> mss = metricStateService.findPerfMetricState(ides, ignoreState);
		//List<MetricStateData> mss = metricStateService.findMetricState(ides, null);
		if(mss !=null) {
			for (MetricStateData ms : mss) {
				ms.setState(MetricStateEnum.NORMAL);
				ms.setCollectTime(new Date());
				saveMetricState(ms);
			}
		}
	}

	public void removeMetricState(long instanceId, String metricId) {
		stateComputeCacheUtil.deleteMetricState(instanceId, metricId);
	}

	public boolean saveMetricState(MetricStateData data) {
		if (logger.isDebugEnabled())
			logger.debug("save metric state" + JSONObject.toJSONString(data));
		boolean successful = stateComputeCacheUtil.setMetricState(data);
		if (!successful) {
			if (logger.isWarnEnabled()) {
				logger.warn("failed to cache metric state with " + data);
			}
		}
		return successful;
	}

	public MetricStateData getMetricState(long instanceID, String metricID) {
		MetricStateData oldData = stateComputeCacheUtil.getMetricState(instanceID, metricID);
		if (oldData == null) {
			oldData = metricStateService.getMetricState(instanceID, metricID);
			if (oldData != null)
				stateComputeCacheUtil.setMetricState(oldData);
		}
		return oldData;
	}

	@Deprecated
	public InstanceStateData getLatestInstanceState(long instanceID) {

		InstanceStateData data = stateComputeCacheUtil.getInstanceState("latest_" + instanceID);
		if (data == null) {
			data = instanceStateService.getLatestInstanceState(instanceID);
			if (data != null) {
				stateComputeCacheUtil.setInstanceState("latest_" + instanceID, data);
			}
		}
		if (data != null) {
			InstanceStateData instanceStateData = new InstanceStateData();
			BeanUtils.copyProperties(data, instanceStateData);
			return instanceStateData;
		}
		return null;
	}

	@Deprecated
	public boolean saveLatestInstanceState(InstanceStateData data) {

		InstanceStateData instanceStateData = new InstanceStateData();
		
		BeanUtils.copyProperties(data, instanceStateData);

		boolean isSuccess = stateComputeCacheUtil.setInstanceState("latest_" + data.getInstanceID(), instanceStateData);
		if (!isSuccess) {
			logger.warn("failed to cache latest instance state with " + data);
		}
		return isSuccess;
	}

	public void setAvailabilityMetricState(long instanceID, String metricID, MetricStateEnum metricData) {
		Map<String, MetricStateEnum> availabilityMetricData = stateComputeCacheUtil.getAvailabilityMetricData(instanceID);
		if(availabilityMetricData !=null) {
			availabilityMetricData.put(metricID, metricData);
		}else{
			availabilityMetricData = loadAvailMetricData(null, instanceID, false);
			if(null != availabilityMetricData) {
				availabilityMetricData.put(metricID, metricData);
			}else{
				logger.warn("set avail metric state error, causing can't find avail metrics("+instanceID +"/"+ metricID+"/"+metricData+").");
			}
		}
		if(availabilityMetricData !=null) {
			stateComputeCacheUtil.setAvailabilityMetricData(instanceID, availabilityMetricData);
		}
	}

	public Map<String, MetricStateEnum> getAvailabilityMetricState(long instanceID) {
		Map<String, MetricStateEnum> availabilityMetricData = stateComputeCacheUtil.getAvailabilityMetricData(instanceID);
		if(availabilityMetricData !=null){
			return availabilityMetricData;
		}else{
			return loadAvailMetricData(null, instanceID, true);
		}
	}

	public void removeAvailabilityMetricState(long instanceID, String metricId) {
		stateComputeCacheUtil.deleteMetricFromAvailabilityMetricData(instanceID, metricId);
	}

	public void setAlarmStateQueue(String instanceId, AlarmStateQueue alarmStateQueue) {
		stateComputeCacheUtil.setAlarmStateQueue(instanceId, alarmStateQueue);
	}

	public void setAlarmStateQueue(String instanceId, CompareInstanceState compareInstanceState) {
		AlarmStateQueue alarmStateQueue = getAlarmStateQueue(instanceId);
		alarmStateQueue.add(compareInstanceState);
		stateComputeCacheUtil.setAlarmStateQueue(instanceId, alarmStateQueue);
	}

	public AlarmStateQueue getAlarmStateQueue(String instanceId) {
		AlarmStateQueue alarmStateQueue = stateComputeCacheUtil.getAlarmStateQueue(instanceId);
		if(alarmStateQueue.isEmpty()) {
			try {
				return loadAlarmStateQueue(Long.valueOf(instanceId), null);
			} catch (InstancelibException e) {
				if(logger.isErrorEnabled()) {
					logger.error("load priority queue failed(instance:"+instanceId+")"+ e.getMessage(), e);
				}
				alarmStateQueue = new AlarmStateQueue();
				return alarmStateQueue;
			}
		}else{
			return alarmStateQueue;
		}
	}

	public AlarmStateQueue getAlarmStateQueue(String instanceId, ResourceInstance resourceInstance) {

		AlarmStateQueue alarmStateQueue = stateComputeCacheUtil.getAlarmStateQueue(instanceId);
		if(alarmStateQueue.isEmpty()) {
			try {
				return loadAlarmStateQueue(Long.valueOf(instanceId), resourceInstance);
			} catch (InstancelibException e) {
				if(logger.isErrorEnabled()) {
					logger.error("load priority queue failed(instance:"+instanceId+")"+ e.getMessage(), e);
				}
				return alarmStateQueue;
			}
		}else{
			return alarmStateQueue;
		}
	}

	public boolean removeAlarmStateQueue(String instanceId) {
		return stateComputeCacheUtil.removeAlarmStateQueue(instanceId);
	}

	public boolean removeAlarmStateQueue(CompareInstanceState compareInstanceState, Long instanceId, AlarmStateQueue alarmStateQueue) {
		if(instanceId == null || compareInstanceState == null)
			return false;
		else {
			if(alarmStateQueue == null) {
				alarmStateQueue = this.getAlarmStateQueue(instanceId.toString());
			}
			if(null == alarmStateQueue)
				return false;
			else {
				if(alarmStateQueue.isEmpty())
					return false;
				alarmStateQueue.remove(compareInstanceState);
				return true;
			}
		}
	}

	/**
	 * 加载当前资源实例的告警状态队列,只加载当前资源下面的所有告警状态（现在是可用性指标单独告警，故可用性指标的指标状态需要加入告警告警状态队列）
	 * @param instanceId
	 * @param resourceInstance
	 * @throws InstancelibException
	 */
	public AlarmStateQueue loadAlarmStateQueue(long instanceId, ResourceInstance resourceInstance) throws InstancelibException {
		/*
			为了不仅能在容器启动之后可以调用该方法(这是缓存中是没有数据的),并且在程序运行过程中也能调用(比如重启了memcached,但DHS等服务却没有重启),
			调用该方法会读缓存,缓存无数据才读数据库,并会将数据库数据加入缓存
		*/
		AlarmStateQueue alarmStateQueue = new AlarmStateQueue();
		if(null == resourceInstance) {
			resourceInstance = resourceInstanceService.getResourceInstance(instanceId);
		}
		if(null == resourceInstance) {
			if(logger.isInfoEnabled()) {
				logger.info("load alarm state queue,but instance is null(" + instanceId + ").");
			}
			return alarmStateQueue;
		}
		InstanceStateData state = this.getInstanceState(resourceInstance.getId());

		if(null != state) {
			//如果当前主资源的告警状态为Normal,那么下面所有的子资源的告警状态均为Normal,那么只需要对资源状态缓存预热即可
			if(state.getAlarmState() == InstanceStateEnum.NORMAL) {
				if(resourceInstance.getParentId() <= PARENT_INST_FLAG) {
					List<ResourceInstance> childInstanceByParentId = resourceInstanceService.getChildInstanceByParentId(resourceInstance.getId());
					if(null != childInstanceByParentId && !childInstanceByParentId.isEmpty()) {
						for (ResourceInstance child : childInstanceByParentId) {
							if(child.getLifeState() == InstanceLifeStateEnum.MONITORED) {
								this.getInstanceState(child.getId());
							}
						}
					}
				}
			}else {
				//把所有指标的故障状态（包括可用性指标）加载进来,这里之所以不考虑指标是否取消监控，原因在于，指标取消监控或者告警，状态必须恢复
				Set<MetricStateEnum> ignoreSet = new HashSet<>(3); //需要过滤掉的指标状态
				ignoreSet.add(MetricStateEnum.NORMAL);
				//ignoreSet.add(MetricStateEnum.CRITICAL);//通过将指标状态的Critical和Normal状态过滤就可以将可用性指标状态过滤掉，因为可用性指标只有这两种状态
				if(state.getAlarmState() == InstanceStateEnum.WARN) {
					ignoreSet.add(MetricStateEnum.SERIOUS);
					ignoreSet.add(MetricStateEnum.CRITICAL);
				}else if(state.getAlarmState() == InstanceStateEnum.SERIOUS) {
					ignoreSet.add(MetricStateEnum.CRITICAL);
				}
				List<Long> instanceIds = new ArrayList<>(1);
				instanceIds.add(state.getInstanceID());
				//实际上我们需要将取消监控的指标状态过滤掉，但取消监控的指标状态已经被重置为Normal，所在在ignoreSet里面已经将Normal过滤掉了
				//2.将主资源的性能指标的故障状态都查询出来，组装部分故障告警状态队列
				List<MetricStateData> perfMetricState = metricStateService.findPerfMetricState(instanceIds, ignoreSet);
				if(null != perfMetricState && !perfMetricState.isEmpty()) {
					for (MetricStateData metricState : perfMetricState) {
						stateComputeCacheUtil.setMetricState(metricState);
						//如果当前实例的资源状态为Critical,那么也需要将所有的故障状态缓存入列
						alarmStateQueue.add(new CompareInstanceState(metricState.getInstanceID() + "_" + metricState.getMetricID(), metricState.getState()));
					}
				}
				if(resourceInstance.getParentId() <= PARENT_INST_FLAG) {
					//由于主资源可能绑定了第三方告警的状态，所以需要加入缓存队列
					List<ThirdPartyMetricStateData> thirdPartyMetricState = thirdPartyMetricStateService.findThirdPartyMetricState(resourceInstance.getId());
					if(null != thirdPartyMetricState && !thirdPartyMetricState.isEmpty()) {
						for(ThirdPartyMetricStateData t : thirdPartyMetricState) {
							if(t.getState() != MetricStateEnum.NORMAL) {
								//目前第三方告警不影响可用性状态
								alarmStateQueue.add(new CompareInstanceState(t.getInstanceID() + "_" + t.getMetricID(), t.getState()));
							}
						}
					}
					//3.查询当前主资源下面的所有子资源的状态，及其性能指标状态
					List<ResourceInstance> childInstanceByParentId = resourceInstanceService.getChildInstanceByParentId(resourceInstance.getId());
					if(null != childInstanceByParentId && !childInstanceByParentId.isEmpty()) {
						for (ResourceInstance child : childInstanceByParentId) {
							if(InstanceLifeStateEnum.MONITORED == child.getLifeState() ) {
								InstanceStateData childState = this.getInstanceState(child.getId());
								if(null != childState) {
									/*
									将所有由自身可用性指标引起致命告警的状态加入缓存
									 */
//									if(childState.getResourceState() == InstanceStateEnum.CRITICAL && child.getId()== childState.getCauseByInstance()) {
//										CompareInstanceState compareInstanceState = new CompareInstanceState(String.valueOf(childState.getCauseByInstance()), InstanceStateEnum.CRITICAL);
//										alarmStateQueue.add(compareInstanceState);
//									}
									/*
									另外在子资源中同样需要维护自身的指标的告警状态队列
									 */
									if(childState.getAlarmState() != InstanceStateEnum.NORMAL) {
										instanceIds.clear();
										ignoreSet.clear();
										//ignoreSet.add(MetricStateEnum.CRITICAL);
										ignoreSet.add(MetricStateEnum.NORMAL);
										instanceIds.add(child.getId());
										List<MetricStateData> childPerfStates = metricStateService.findPerfMetricState(instanceIds, ignoreSet);

										if(null != childPerfStates && !childPerfStates.isEmpty()) {
											AlarmStateQueue childPriorityQueue = new AlarmStateQueue();
											for(MetricStateData metricStateData : childPerfStates) {
												stateComputeCacheUtil.setMetricState(metricStateData);
												//在这里仍然需要维护主资源与子资源的告警状态队列
												CompareInstanceState compareInstanceState = new CompareInstanceState(metricStateData.getInstanceID() + "_" + metricStateData.getMetricID(),
														metricStateData.getState());
												childPriorityQueue.add(compareInstanceState);
												//另外子资源的指标状态也有需要缓存到主资源的告警状态队列中
												alarmStateQueue.add(compareInstanceState);
											}
											if(logger.isDebugEnabled()) {
												logger.debug("load child inst("+child.getId()+") alarm state queue:" + childPriorityQueue);
											}
											stateComputeCacheUtil.setAlarmStateQueue(String.valueOf(child.getId()), childPriorityQueue);
										}
									}
								}
							}
						}
					}
				}
				if(logger.isDebugEnabled()) {
					logger.debug("load inst("+state.getInstanceID()+") alarm state queue:" + alarmStateQueue);
				}
				stateComputeCacheUtil.setAlarmStateQueue(String.valueOf(state.getInstanceID()), alarmStateQueue);
			}
		}
		return alarmStateQueue;

	}

	/**
	 * 加载数据库可用性指标的状态集合
	 * @param resourceInstance
	 * @param instanceId
	 * @param isCache 是否实时缓存
	 */
	public Map<String, MetricStateEnum> loadAvailMetricData(ResourceInstance resourceInstance, long instanceId, boolean isCache) {
		if(null == resourceInstance && instanceId == 0L) {
			return null;
		}else if(null == resourceInstance) {
			try {
				resourceInstance = resourceInstanceService.getResourceInstance(instanceId);
			} catch (InstancelibException e) {
				if(logger.isWarnEnabled()) {
					logger.warn("get inst{"+instanceId+"} failed,"+e.getMessage(), e);
				}
			}
			if(null == resourceInstance){
				if(logger.isWarnEnabled()){
					logger.warn("can't load avail value cause of missed instance:" + instanceId);
				}
				return null;
			}
		}else {
			instanceId = resourceInstance.getId();
		}
		Set<String> availMetricIds = new HashSet<>(2);
		long profileId;
		try {
			ProfileInfo basicInfoByResourceInstanceId = profileService.getBasicInfoByResourceInstanceId(instanceId);
			if(basicInfoByResourceInstanceId == null) {
				if(logger.isWarnEnabled()) {
					logger.warn("inst {" + instanceId + "} can't find profile info while loading avail metric data...");
				}
				return null;
			}
			profileId = basicInfoByResourceInstanceId.getProfileId();
		} catch (ProfilelibException e) {
			if(logger.isWarnEnabled()) {
				logger.warn("query profile failed{" + instanceId +"}," + e.getMessage(), e);
			}
			return null;
		}
		List<ProfileMetric> metricList = null;
		try {
			List<Timeline> timelinesByProfileId = timelineService.getTimelinesByProfileId(profileId);
			if(null != timelinesByProfileId) {
				Date current = new Date();
				for (Timeline timeline : timelinesByProfileId) {
					TimelineInfo timelineInfo = timeline.getTimelineInfo();
					if(current.after(timelineInfo.getStartTime()) && current.before(timelineInfo.getEndTime())) {
						metricList = timelineService.getMetricByTimelineId(timelineInfo.getTimeLineId());
					}
				}
			}
		} catch (ProfilelibException e) {
			if(logger.isWarnEnabled()) {
				logger.warn(e.getMessage(), e);
			}
		}
		if(metricList == null || metricList.isEmpty()) {
			try {
				metricList = profileService.getMetricByProfileId(profileId);
			} catch (ProfilelibException e) {
				if(logger.isWarnEnabled()) {
					logger.warn("query profile metric failed{"+instanceId+"},"+e.getMessage(), e);
				}
				return null;
			}
		}
		if(metricList !=null && !metricList.isEmpty()) {
			for (ProfileMetric metric : metricList) {
				if(profileId == 0L){
					profileId = metric.getProfileId();
				}
				try{
					ResourceMetricDef resourceMetricDef = capacityService.getResourceMetricDef(resourceInstance.getResourceId(), metric.getMetricId());
					if(resourceMetricDef !=null && resourceMetricDef.getMetricType() == MetricTypeEnum.AvailabilityMetric){
						if(metric.isAlarm()) {
							availMetricIds.add(metric.getMetricId());
						}
					}
				}catch (Exception e){
					if(logger.isWarnEnabled()) {
						logger.warn(e.getMessage(), e);
					}
				}

			}
		}
		//加载自定义可用性指标
		try {
			List<CustomMetric> customMetricsByInstanceId = customMetricService.getCustomMetricsByInstanceId(instanceId);
			if(null != customMetricsByInstanceId) {
				for (CustomMetric customMetric : customMetricsByInstanceId) {
					CustomMetricInfo customMetricInfo = customMetric.getCustomMetricInfo();
					if(customMetricInfo !=null && customMetricInfo.getStyle() == MetricTypeEnum.AvailabilityMetric) {
						if(customMetricInfo.isAlert()) {
							availMetricIds.add(customMetricInfo.getId());
						}
					}
				}
			}
		} catch (CustomMetricException e) {
			if(logger.isWarnEnabled()) {
				logger.warn(e.getMessage(), e);
			}
		}
		if(!availMetricIds.isEmpty()) {
			if(logger.isDebugEnabled()) {
				logger.debug("load avail metric data{"+instanceId+"} :" + availMetricIds);
			}
			Map<String, MetricStateEnum> data = new HashMap<>(availMetricIds.size());
			Iterator<String> iterator = availMetricIds.iterator();
			while (iterator.hasNext()) {
				String next = iterator.next();
				MetricStateData metricState = getMetricState(instanceId, next);
				if(null != metricState) {
					data.put(next, metricState.getState());
				}else{
					data.put(next, MetricStateEnum.NORMAL);
				}
				iterator.remove();
			}
			if(isCache)
				stateComputeCacheUtil.setAvailabilityMetricData(instanceId, data);
			return data;
		}
		return null;

	}

}
