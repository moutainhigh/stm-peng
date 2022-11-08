package com.mainsteam.stm.state.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.instance.obj.CollectStateEnum;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.exception.BaseException;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.metric.CustomMetricService;
import com.mainsteam.stm.metric.exception.CustomMetricException;
import com.mainsteam.stm.metric.obj.CustomMetric;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.state.engine.StateEngine;
import com.mainsteam.stm.state.obj.InstanceStateChangeData;
import com.mainsteam.stm.state.obj.InstanceStateData;
import com.mainsteam.stm.state.obj.MetricStateData;
import com.mainsteam.stm.state.obj.ResourceInstanceState;

import static com.mainsteam.stm.state.util.StateCaculatUtils.checkChildStateForParent;
import static com.mainsteam.stm.state.util.StateCaculatUtils.convertMetricStateToInstanceState;
import static com.mainsteam.stm.state.util.StateCaculatUtils.generateNotifyInstanceState;

/**
 * @author 作者：ziw
 * @date 创建时间：2016年11月18日 下午4:55:39
 * @version 1.0
 */
public class InstanceStateComputeUtil {

	private static final Log logger = LogFactory
			.getLog(InstanceStateComputeUtil.class);
	private static final int MAIN_INSTANCE_FLAG = 0;

	private final StateEngine stateEngine;
	private final CapacityService capacityService;
	private final CustomMetricService customMetricService;
	private final ResourceInstanceService resourceInstanceService;

	private final AvailStateUtils availStateUtils;
	private final PerformanceMetricsStateUtil performanceMetricsStateUtil;
	private final InstanceStateDataCacheUtil instanceStateDataCacheUtil;
	private MetricStateSelectUtil metricStateSelectUtil;
	private ProfileMetricSelectUtil profileMetricSelectUtil;


	public InstanceStateComputeUtil(StateEngine stateEngine,
			CapacityService capacityService,
			CustomMetricService customMetricService,
			ResourceInstanceService resourceInstanceService,
			AvailStateUtils availStateUtils,
			PerformanceMetricsStateUtil performanceMetricsStateUtil,
			InstanceStateDataCacheUtil instanceStateDataCacheUtil,
			MetricStateSelectUtil metricStateSelectUtil,
			ProfileMetricSelectUtil profileMetricSelectUtil) {
		super();
		this.stateEngine = stateEngine;
		this.capacityService = capacityService;
		this.customMetricService = customMetricService;
		this.resourceInstanceService = resourceInstanceService;
		this.availStateUtils = availStateUtils;
		this.performanceMetricsStateUtil = performanceMetricsStateUtil;
		this.instanceStateDataCacheUtil = instanceStateDataCacheUtil;
		this.metricStateSelectUtil = metricStateSelectUtil;
		this.profileMetricSelectUtil = profileMetricSelectUtil;
	}

	/**
	 * 资源状态变化，必须先保存子资源的状态，然后再计算主资源的状态，因为主资源的状态是根据所有子资源状态计算而来的
	 *
	 * @param resourceInstance
	 * @param metricID
	 * @param resourceMetricDef
	 * @param metricData
	 * @return 主资源(网络设备,主机)不可用，返回所有接口子资源指标。用于链路可用性计算。
	 * @throws BaseException
	 */
	public void changeInstanceState(final ResourceInstance resourceInstance,
									final String metricID, final ResourceMetricDef resourceMetricDef,
									final MetricCalculateData metricData)
			throws BaseException {

		try{
			long mainInstanceID = -1;
			ResourceInstance parentInstance = null;
			ArrayList<InstanceStateData> instanceStateDataList = new ArrayList<>(2);
			boolean isChildInstance = (resourceInstance.getParentId() > MAIN_INSTANCE_FLAG) ? true : false;
			// 资源状态计算
			if (isChildInstance) { //子资源状态计算
				InstanceStateData preChildInstanceState = instanceStateDataCacheUtil.getInstanceState(resourceInstance.getId());
				InstanceStateData sd = changeInstanceStateForChild(resourceInstance, metricID, false, true, preChildInstanceState);
				if (sd != null) {
					instanceStateDataList.add(sd);
				}
				parentInstance = resourceInstance.getParentInstance();
			} else {
				parentInstance = resourceInstance;
			}
			mainInstanceID = parentInstance.getId();
			//如果当前计算的是子资源资源实例的可用性指标，则主资源的资源状态则不必纳入计算，因为子资源的状态不会改变主资源实例的资源状态，只能改变告警状态
			InstanceStateData preMainInstanceStateData = instanceStateDataCacheUtil.getInstanceState(mainInstanceID);
			InstanceStateData mainInstanceStateData = null;
			//特殊处理一下，主要原因：可用性值缓存失效
			if(resourceInstance != null && StringUtils.equals(resourceInstance.getResourceId(), "CameraRes")){
				if(resourceMetricDef.getMetricType() == MetricTypeEnum.AvailabilityMetric) {
					MetricStateData metricState = metricStateSelectUtil.getMetricState(resourceInstance.getId(), resourceMetricDef.getId());
					if(metricState.getState() == MetricStateEnum.CRITICAL) {
						mainInstanceStateData = new InstanceStateData(resourceInstance.getId(),InstanceStateEnum.CRITICAL,metricData.getCollectTime(),
								resourceMetricDef.getId(),resourceInstance.getId(),
								CollectStateEnum.UNCOLLECTIBLE);
					}

				}else if(resourceMetricDef.getMetricType() == MetricTypeEnum.PerformanceMetric && null != preMainInstanceStateData) {
					if(preMainInstanceStateData.getState() == InstanceStateEnum.CRITICAL) {
						if(logger.isInfoEnabled()) {
							logger.info("CameraRes instance is Critical, can't be recovered by perf metric:"
									+ (metricData.getResourceInstanceId() + "/" + metricData.getMetricId()));
						}
						mainInstanceStateData = preMainInstanceStateData;
					}
				}
			}
			if(null == mainInstanceStateData)
				mainInstanceStateData = calculateMainInstanceState(parentInstance, metricData.getTimelineId(), (!isChildInstance));

			if (mainInstanceStateData != null) {
				if (StringUtils.isBlank(mainInstanceStateData.getCauseBymetricID()))
					mainInstanceStateData.setCauseBymetricID(metricID);
				//如果采集状态为空，则设置上一次的值，如果没有的话就设置为空
				if(null == mainInstanceStateData.getCollectStateEnum()){
					if(logger.isWarnEnabled()) {
						StringBuffer stringBuffer = new StringBuffer(100);
						stringBuffer.append("Instance{").append(mainInstanceID).append(":");
						stringBuffer.append(metricID);
						stringBuffer.append("} can't calculate collection state, using COLLECTIBLE.");
						logger.warn(stringBuffer.toString());
					}
					mainInstanceStateData.setCollectStateEnum(CollectStateEnum.COLLECTIBLE);
				}

				if (preMainInstanceStateData == null
						|| mainInstanceStateData.compareTo(preMainInstanceStateData) != 0
						|| preMainInstanceStateData.getCollectStateEnum() == null
						|| mainInstanceStateData.getCollectStateEnum() != preMainInstanceStateData.getCollectStateEnum()) {
					if (logger.isInfoEnabled()) {
						StringBuilder b = new StringBuilder(100);
						b.append("Instance state has been changed, state{");
						b.append(mainInstanceStateData.getInstanceID()).append(":").append(mainInstanceStateData.getState());
						b.append("}");
						if(null != preMainInstanceStateData){
							b.append(", pre state is ");
							b.append(preMainInstanceStateData.getState());
						}
						b.append(",causing instance is ").append(mainInstanceStateData.getCauseByInstance());
						b.append(",causing metric is ").append(mainInstanceStateData.getCauseBymetricID());
						logger.info(b.toString());
					}
					if ((preMainInstanceStateData == null && mainInstanceStateData.getState() != InstanceStateEnum.NORMAL)
							|| (preMainInstanceStateData != null && mainInstanceStateData.compareTo(preMainInstanceStateData) != 0)) {
						if(logger.isInfoEnabled()) {
							logger.info("instance should alarm:" + mainInstanceStateData);
						}
						instanceStateDataList.add(mainInstanceStateData);
					}
					if(null == mainInstanceStateData.getCollectTime()) {
						MetricStateData metricStateData = metricStateSelectUtil.getMetricState(resourceInstance.getId(), metricID);
						if(null != metricStateData)
							mainInstanceStateData.setCollectTime(metricData.getCollectTime());
						else{
							mainInstanceStateData.setCollectTime(new Date());
						}
					}

					// 如果主资源不可用，那么子资源全部设置为不可用；
					if (InstanceStateEnum.CRITICAL == mainInstanceStateData.getState()) {
						List<ResourceInstance> childrenResourceInstance = resourceInstanceService.getChildInstanceByParentId(mainInstanceID);
						if (childrenResourceInstance != null) {

							boolean flag = false;
							for (ResourceInstance child : childrenResourceInstance) {
								if(child.getLifeState() == InstanceLifeStateEnum.MONITORED) {

									flag = checkMetricTypeWithInstance(child, MetricTypeEnum.AvailabilityMetric, 0);
									if(flag){
										InstanceStateData instanceStateData = instanceStateDataCacheUtil.getInstanceState(child.getId());
										if(instanceStateData != null ){
											if(InstanceStateEnum.CRITICAL == instanceStateData.getState())
												continue;
											instanceStateData.setState(InstanceStateEnum.CRITICAL);
											instanceStateData.setCollectTime(mainInstanceStateData.getCollectTime());
											instanceStateData.setCauseBymetricID(mainInstanceStateData.getCauseBymetricID());
											instanceStateData.setCauseByInstance(mainInstanceStateData.getCauseByInstance());
											if (logger.isDebugEnabled()) {
												logger.debug("Set children instance critical cause of main instance " + mainInstanceID);
											}
										}else {
											instanceStateData = new InstanceStateData();
											instanceStateData.setCauseBymetricID(mainInstanceStateData.getCauseBymetricID());
											instanceStateData.setState(InstanceStateEnum.CRITICAL);
											instanceStateData.setCollectTime(mainInstanceStateData.getCollectTime());
											instanceStateData.setInstanceID(child.getId());
											instanceStateData.setCauseByInstance(mainInstanceStateData.getCauseByInstance());
											instanceStateData.setCollectStateEnum(CollectStateEnum.UNCOLLECTIBLE);
											if (logger.isDebugEnabled()) {
												logger.debug("generate child state cause of no state before, " + instanceStateData);
											}
										}
										instanceStateDataCacheUtil.saveInstanceState(instanceStateData);
									}
								}

							}
						}
					} else if (preMainInstanceStateData != null && InstanceStateEnum.CRITICAL == preMainInstanceStateData.getState()) {
						List<ResourceInstance> childrenResourceInstance = resourceInstanceService.getChildInstanceByParentId(mainInstanceID);
						if (!CollectionUtils.isEmpty(childrenResourceInstance)) {
							InstanceStateData data;
							InstanceStateEnum maxState = InstanceStateEnum.NORMAL;
							for (ResourceInstance child : childrenResourceInstance) {
								if(child.getLifeState() == InstanceLifeStateEnum.MONITORED) {

									InstanceStateData preChildInstanceState = instanceStateDataCacheUtil.getInstanceState(child.getId());
									if(null == preChildInstanceState) {
										continue;
									}

									if(preChildInstanceState.getCauseByInstance() == mainInstanceID || StringUtils.equals(preChildInstanceState.getCauseBymetricID(),metricID))
										data = calculateSpecifiedInstanceState(child, 0 , false);
									else
										data = calculateSpecifiedInstanceState(child, 0, true);

									if(null == data) { //无法计算当前子资源实例状态;
										if(logger.isInfoEnabled()) {
											StringBuffer stringBuffer = new StringBuffer();
											stringBuffer.append("child instance ").append(child.getId()).
													append("sets to NORMAL, cause can not be found any state, while main instance starting to recover.");
											logger.info(stringBuffer.toString());
										}
										data = new InstanceStateData(child.getId(), InstanceStateEnum.NORMAL,metricData.getCollectTime(),
												metricData.getMetricId(),mainInstanceID, CollectStateEnum.COLLECTIBLE);

									}

									if(preChildInstanceState.getState() != data.getState() || preChildInstanceState.getCollectStateEnum() == null
											|| preChildInstanceState.getCollectStateEnum() != data.getCollectStateEnum()){
										//恢复告警
										if(logger.isInfoEnabled()) {
											StringBuffer stringBuffer = new StringBuffer();
											stringBuffer.append("child instance {").append(child.getId()).append("} should recover {")
													.append(data.getState()).append("} state, causing main instance {")
													.append(mainInstanceID).append("}");
											logger.info(stringBuffer.toString());
										}
										if(StringUtils.isBlank(data.getCauseBymetricID()))
											data.setCauseBymetricID(preChildInstanceState.getCauseBymetricID());
										data.setCollectTime(metricData.getCollectTime());
										instanceStateDataCacheUtil.saveInstanceState(data);
									}

									if(null !=data && null !=preChildInstanceState && data.getState() != preChildInstanceState.getState()
											&& preChildInstanceState.getCauseByInstance() != mainInstanceID
											&& !StringUtils.equals(preChildInstanceState.getCauseBymetricID(), metricID)) {

										ResourceMetricDef metricDef = capacityService.getResourceMetricDef(child.getResourceId(), preChildInstanceState.getCauseBymetricID());
										if(null != metricDef) {
											MetricCalculateData metricCalculateData = new MetricCalculateData();
											metricCalculateData.setMetricId(preChildInstanceState.getCauseBymetricID());
											metricCalculateData.setResourceId(child.getResourceId());
											metricCalculateData.setResourceInstanceId(child.getId());
											metricCalculateData.setCollectTime((data.getCollectTime()) == null ?(new Date()) : data.getCollectTime());
											InstanceStateChangeData instanceStateChangeData = generateNotifyInstanceState(data,preChildInstanceState,true,1,metricCalculateData,
													preChildInstanceState.getCauseBymetricID(),metricDef);
											if(logger.isInfoEnabled()) {
												StringBuffer stringBuffer = new StringBuffer();
												stringBuffer.append("recovery alarm event causing of main instance state changed, child id is ");
												stringBuffer.append(child.getId());
												logger.info(stringBuffer.toString());
											}
											stateEngine.handleInstanceStateChange(instanceStateChangeData);

										}
									}
									if (data != null && data.getState().getStateVal() > maxState.getStateVal()) {
										maxState = data.getState();
									}
								}
							}
							if (maxState.getStateVal() < mainInstanceStateData.getState().getStateVal()) {
								mainInstanceStateData = calculateMainInstanceState(parentInstance,metricData.getTimelineId(), !isChildInstance);
							}
						}
					}

					if(logger.isInfoEnabled()) {
						logger.info("save main instance state " + mainInstanceStateData);
					}
					mainInstanceStateData.setCollectTime(metricData.getCollectTime());
					instanceStateDataCacheUtil.saveInstanceState(mainInstanceStateData);

				}
			}
			if (MetricTypeEnum.AvailabilityMetric == resourceMetricDef.getMetricType()) {
				for (InstanceStateData instanceStateData : instanceStateDataList) {
					if (logger.isInfoEnabled()) {
						logger.info("Instance should be alarm " + instanceStateData);
					}
					InstanceStateChangeData instanceStateChangeData = generateNotifyInstanceState(instanceStateData,null,true,1,metricData,
							metricData.getMetricId(),resourceMetricDef);
					stateEngine.handleInstanceStateChange(instanceStateChangeData);
				}
			}else {
				if (logger.isInfoEnabled() ) {
					logger.info("instance("+resourceInstance.getId() + "/" + metricID +") don't alarm,cause not available metric :\n"+
					instanceStateDataList);
				}
			}
		}catch (Throwable throwable) {
			if(logger.isErrorEnabled()) {
				StringBuffer stringBuffer = new StringBuffer();
				stringBuffer.append("changing instance state occurs error, instance id is ").append(resourceInstance.getId());
				stringBuffer.append(",metric id is ").append(metricID).append(", message info is ").append(throwable.getMessage());

				logger.error(stringBuffer.toString(), throwable);

			}
		}

	}


	/**
	 * 计算主资源的状态，值得注意的是instanceId一定是主资源ID，
	 * 因为计算子资源状态可用通过calculateSpecifiedInstanceState方法得到，然后再计算主资源状态
	 *
	 * @param parentInstance
	 * @param timelineId
	 * @param isCalculateResourceState 是否计算资源状态，默认计算资源状态，false不计算资源状态
	 * @return
	 */
	public InstanceStateData calculateMainInstanceState(ResourceInstance parentInstance,long timelineId, boolean isCalculateResourceState) {
		long instanceId = parentInstance.getId();
		try {
			// 计算当前实例的资源状态与性能指标状态的最高级别状态
			InstanceStateData instanceStateData = this.calculateSpecifiedInstanceState(parentInstance,timelineId, isCalculateResourceState);
			// 当前为主资源，与所有的子资源状态进行比较
			InstanceStateData subInstanceStateData = this.calculateStateFromSubInstance(instanceId);
			if (subInstanceStateData != null) {
				InstanceStateEnum subInstanceState2MainState = checkChildStateForParent(subInstanceStateData.getState());
				subInstanceStateData.setState(subInstanceState2MainState);
			}
			InstanceStateData finalInstanceStateData = null;
			if (instanceStateData != null && subInstanceStateData != null) {
				finalInstanceStateData = Collections.max(Arrays.asList(subInstanceStateData, instanceStateData));
			} else {
				finalInstanceStateData = (instanceStateData == null ? (subInstanceStateData == null ? null : subInstanceStateData) : instanceStateData);
			}
			if (finalInstanceStateData != null) {
				if (finalInstanceStateData.equals(subInstanceStateData))
					finalInstanceStateData.setCollectStateEnum(null != instanceStateData ? instanceStateData.getCollectStateEnum() : CollectStateEnum.COLLECTIBLE);
				InstanceStateData result = new InstanceStateData(instanceId,
						finalInstanceStateData.getState(),
						finalInstanceStateData.getCollectTime(),
						finalInstanceStateData.getCauseBymetricID(),
						finalInstanceStateData.getCauseByInstance(),
						finalInstanceStateData.getCollectStateEnum());
				return result;
			}
		} catch (Exception e) {
			if (logger.isWarnEnabled()) {
				logger.warn("Calculate instance state{" + instanceId
						+ "} error," + e.getMessage(), e);
			}
		}
		if(logger.isWarnEnabled()) {
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append("Instance{").append(instanceId).append("} can't calculate main state,That's serious.");
		}
		return null;
	}

	/**
	 * 获取指定的主资源下所有子资源中最高级别的告警状态数据
	 *
	 * @param instanceId
	 * @return
	 * @throws InstancelibException
	 */
	private InstanceStateData calculateStateFromSubInstance(long instanceId)
			throws InstancelibException {
		List<ResourceInstance> resourceInstanceList = resourceInstanceService.getChildInstanceByParentId(instanceId);
		if (resourceInstanceList != null && !resourceInstanceList.isEmpty()) {
			List<InstanceStateData> instanceStateDataList = new ArrayList<>(resourceInstanceList.size());
			for (ResourceInstance resourceInstance : resourceInstanceList) {
				if (resourceInstance.getLifeState() == InstanceLifeStateEnum.NOT_MONITORED) // 未监控的资源不纳入状态计算
					continue;
				else {
					InstanceStateData instanceStateData = instanceStateDataCacheUtil.getInstanceState(resourceInstance.getId());
					if (instanceStateData != null)
						instanceStateDataList.add(instanceStateData);
				}
			}
			if (!instanceStateDataList.isEmpty()) {
				InstanceStateData instanceStateData = Collections.max(instanceStateDataList);
				if (logger.isDebugEnabled()) {
					StringBuffer stringBuffer = new StringBuffer();
					stringBuffer.append("instance ").append(instanceId).append(" has found the most serious sub instance state {");
					stringBuffer.append(instanceStateData.getInstanceID()).append(":").append(instanceStateData.getState()).append("}");
					logger.debug(instanceStateData.toString());
				}
				if (InstanceStateEnum.isUnknownForIns(instanceStateData.getState()))
					instanceStateData.setState(InstanceStateEnum.NORMAL);
				return instanceStateData;
			} else {
				if (logger.isInfoEnabled()) {
					logger.info("instance{" + instanceId + "} has not found any sub instance state data .");
				}
				return null;
			}

		} else {// 未查询到子资源
			if (logger.isInfoEnabled()) {
				logger.info("instance{" + instanceId
						+ "} has not found any sub instance");
			}
			return null;
		}
	}

	/**
	 * 计算指定资源实例的当前状态，只计算当前实例的资源状态与性能指标状态的比较结果，如果为主资源，则不计算所有的子资源状态
	 *
	 * @param resourceInstance
	 * @param timelineId
	 * @param isCalculateResourceState 是否计算当前实例的资源状态，默认计算资源状态，false表示不计算资源状态
	 * @return
	 */
	public InstanceStateData calculateSpecifiedInstanceState(ResourceInstance resourceInstance, long timelineId, boolean isCalculateResourceState )
			throws InstancelibException, ProfilelibException,
			CustomMetricException {

		long instanceId = resourceInstance.getId();
		ResourceInstanceState resourceInstanceState = null;
		if(isCalculateResourceState) {
			resourceInstanceState = availStateUtils.calculateResourceState(resourceInstance,timelineId);
		}else {
			if(logger.isInfoEnabled()) {
				StringBuffer stringBuffer = new StringBuffer();
				stringBuffer.append("Don't need calculate resource state {");
				stringBuffer.append(resourceInstance.getId()).append("},collection state may be empty.");
				logger.info(stringBuffer.toString());
			}
		}
		MetricStateData performanceMetricsState = performanceMetricsStateUtil.calculatePerformanceMetricsState(instanceId, timelineId);
		if (null == performanceMetricsState) {
			if(null != resourceInstanceState) {
				InstanceStateData instanceStateData = new InstanceStateData(
						instanceId, resourceInstanceState.getInstanceStateEnum(),
						null, null, instanceId,
						resourceInstanceState.getCollectStateEnum());
				if (logger.isInfoEnabled()) {
					StringBuffer stringBuffer = new StringBuffer();
					stringBuffer.append("instance ").append(instanceId);
					stringBuffer.append(" has not found any performance state, using resource state ");
					stringBuffer.append(instanceStateData.getState());
					stringBuffer.append(",collectState ");
					stringBuffer.append(resourceInstanceState.getCollectStateEnum());
					logger.info(stringBuffer.toString());
				}
				return instanceStateData;

			}else { //返回上一次的值
				if (logger.isInfoEnabled()) {
					StringBuffer stringBuffer = new StringBuffer();
					stringBuffer.append("instance ").append(instanceId).append(" has not found any performance or resource state,so return null.");
					logger.info(stringBuffer.toString());
				}
				return null;
			}
		}

		InstanceStateEnum performanceState = convertMetricStateToInstanceState(performanceMetricsState.getState());
		if(null == resourceInstanceState) {
			InstanceStateData instanceStateData = new InstanceStateData(instanceId, performanceState, performanceMetricsState.getCollectTime(),
					performanceMetricsState.getMetricID(), instanceId, null);

			return instanceStateData;
		}

		if (resourceInstanceState.getInstanceStateEnum().getStateVal() > performanceState.getStateVal()) {
			InstanceStateData instanceStateData = new InstanceStateData(
					instanceId, resourceInstanceState.getInstanceStateEnum(),
					null, null, instanceId,
					resourceInstanceState.getCollectStateEnum());

			return instanceStateData;
		} else {
			InstanceStateData instanceStateData = new InstanceStateData(
					instanceId, performanceState,
					performanceMetricsState.getCollectTime(),
					performanceMetricsState.getMetricID(),
					performanceMetricsState.getInstanceID(),
					resourceInstanceState.getCollectStateEnum());

			return instanceStateData;
		}

	}

	private InstanceStateData changeInstanceStateForChild(
			final ResourceInstance resourceInstance, String metricID,
			boolean causeParent, boolean isCalculateResourceState, InstanceStateData preInstanceStateData) throws InstancelibException,
			ProfilelibException, CustomMetricException {
		/*
		如果是主资源引起子资源恢复，那么需要判断引起子资源状态变化的指标和实例ID是不是当前主资源的实例ID或指标，
		如果是当前主资源实例ID或指标，则不需要计算子资源的资源状态（即恢复为正常状态），否则需要计算资源状态（致命），
		这样做的目的是，当主资源变为致命之前，子资源已经致命，这时已经产生告警，接着如果主资源恢复正常，按照逻辑，下面的所有子资源都恢复正常，
		但这只是状态变为正常，之前产生的告警信息却仍然存在。举个例子说明：比如有一台Windows主机，下面某个网络接口被禁用了，这时接口状态会变为致命并
		产生一条致命告警。接着如果这台主机关机，那么主资源会变为致命且产生一条告警信息，下面所有可用的接口也会变成不可用，但是不产生告警信息。
		如果这个时候开机的话，主机要恢复，主资源状态变为可用并产生一条恢复告警。下面的接口状态应该是除了之前禁用的接口之外都需要恢复正常并且不产生恢复告警信息。
		 */
		if(causeParent) {
			if(preInstanceStateData !=null && !(preInstanceStateData.getCauseByInstance() == resourceInstance.getParentId()
					|| StringUtils.equals(metricID, preInstanceStateData.getCauseBymetricID()))) {
				isCalculateResourceState = true;
			}
		}
		// 当前为子资源,没有基线，基线手动设置0
		InstanceStateData instanceStateData = calculateSpecifiedInstanceState(resourceInstance, 0, isCalculateResourceState);
		if (instanceStateData != null) {
			if(null == instanceStateData.getCollectStateEnum()) {
				if(null != preInstanceStateData)
					instanceStateData.setCollectStateEnum(preInstanceStateData.getCollectStateEnum());
				else {
					StringBuffer stringBuffer = new StringBuffer();
					stringBuffer.append("Instance{").append(resourceInstance.getId()).append("} can't set collection state.");
					logger.info(stringBuffer.toString());
				}
			}

			if (preInstanceStateData == null
					|| instanceStateData.compareTo(preInstanceStateData) != 0
					|| preInstanceStateData.getCollectStateEnum() == null
					|| preInstanceStateData.getCollectStateEnum() != instanceStateData.getCollectStateEnum()) {

				if (StringUtils.isBlank(instanceStateData.getCauseBymetricID()))
					instanceStateData.setCauseBymetricID(metricID);
				if (causeParent) {
					instanceStateData.setCauseByInstance(resourceInstance.getParentId());
				}
				if(null == instanceStateData.getCollectTime()) {
					MetricStateData metricStateData = metricStateSelectUtil.getMetricState(resourceInstance.getId(), metricID);
					if(metricStateData !=null)
						instanceStateData.setCollectTime(metricStateData.getCollectTime());
					else {
						StringBuffer stringBuffer = new StringBuffer();
						stringBuffer.append("Instance{").append(resourceInstance.getId()).append("} can't find collecting time, so using current date.");
						logger.info(stringBuffer.toString());
						instanceStateData.setCollectTime(new Date());
					}
				}
				if (logger.isInfoEnabled()) {
					StringBuilder b = new StringBuilder(100);
					b.append("Instance state has been changed.State{");
					b.append(instanceStateData);
					b.append("},pre state is ");
					if(null != preInstanceStateData)
						b.append(preInstanceStateData.getState());
					else
						b.append("null");
					logger.info(b.toString());
				}
				instanceStateDataCacheUtil.saveInstanceState(instanceStateData);
				if ((preInstanceStateData == null && instanceStateData.getState() != InstanceStateEnum.NORMAL)
						|| (preInstanceStateData != null && instanceStateData.compareTo(preInstanceStateData) != 0)) {
					return instanceStateData;
				}
			}
		}

		if(logger.isDebugEnabled()) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("instance ").append(resourceInstance.getId()).append(" computes state is null.");
			logger.debug(stringBuilder.toString());
		}
		return null;
	}

	/**
	 * 检查当前实例下是否存在指定的指标类型
	 *
	 * @param resourceInstance
	 * @param metricType
	 * @return
	 */
	private boolean checkMetricTypeWithInstance(
			ResourceInstance resourceInstance, MetricTypeEnum metricType,long timelineId)
			throws ProfilelibException, CustomMetricException {
		// 没有可用性指标的资源不改变状态
		boolean flag = false;
		List<ProfileMetric> profileMetricList = profileMetricSelectUtil.findProfileMetrics(
				resourceInstance.getId(), resourceInstance.getResourceId(),timelineId, metricType);
		if (!CollectionUtils.isEmpty(profileMetricList))
			flag = true;
		if (!flag) {
			List<CustomMetric> customMetricList = profileMetricSelectUtil.findCustomMetricsByType(
					resourceInstance.getId(), metricType);
			if (null != customMetricList && !customMetricList.isEmpty())
				flag = true;
		}
		return flag;
	}

}
