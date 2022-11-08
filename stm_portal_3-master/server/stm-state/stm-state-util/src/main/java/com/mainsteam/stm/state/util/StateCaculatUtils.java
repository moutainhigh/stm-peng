package com.mainsteam.stm.state.util;

import java.util.List;
import java.util.Map;

import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.exception.BaseException;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.metric.exception.CustomMetricException;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.ProfileThreshold;
import com.mainsteam.stm.state.engine.StateEngine;
import com.mainsteam.stm.state.obj.AvailabilityMetricData;
import com.mainsteam.stm.state.obj.InstanceStateChangeData;
import com.mainsteam.stm.state.obj.InstanceStateData;
import com.mainsteam.stm.state.obj.MetricStateChangeData;
import com.mainsteam.stm.state.obj.MetricStateData;

public class StateCaculatUtils {
	private final StateEngine stateEngine;
	private final StateCacheUtils stateCacheUtils;

	private final FlappingComputer flappingComputer;
	private final MetricStateSelectUtil metricStateSelectUtil;
	private final AvailStateUtils availStateUtils;
	private final InstanceStateDataCacheUtil instanceStateDataCacheUtil;
	private final InstanceStateComputeUtil instanceStateComputeUtil;

	public StateCaculatUtils(StateEngine stateEngine,
			StateCacheUtils stateCacheUtils, FlappingComputer flappingComputer,
			MetricStateSelectUtil metricStateSelectUtil,
			AvailStateUtils availStateUtils,
			InstanceStateDataCacheUtil instanceStateDataCacheUtil,
			InstanceStateComputeUtil instanceStateComputeUtil) {
		super();
		this.stateEngine = stateEngine;
		this.stateCacheUtils = stateCacheUtils;
		this.flappingComputer = flappingComputer;
		this.metricStateSelectUtil = metricStateSelectUtil;
		this.availStateUtils = availStateUtils;
		this.instanceStateDataCacheUtil = instanceStateDataCacheUtil;
		this.instanceStateComputeUtil = instanceStateComputeUtil;
	}

	/**
	 * 计算当前可用性指标的可用性状态
	 * 
	 * @param resourceInstance
	 * @param metricID
	 * @param metricData
	 * @param preMetricStateData
	 * @return
	 */
	public MetricStateEnum calculateAvailMetricState(ResourceInstance resourceInstance,
			String metricID, String metricData, MetricStateData preMetricStateData) throws InstancelibException {

		return this.availStateUtils.calculateAvailMetricState(resourceInstance, metricID, metricData, preMetricStateData);
	}

	/**
	 * 资源状态变化，必须先保存子资源的状态，然后再计算主资源的状态，因为主资源的状态是根据所有子资源状态计算而来的
	 * 
	 * @param resourceInstance
	 * @param metricID
	 * @param resourceMetricDef
	 * @param metricData
	 * @throws BaseException
	 */
	public void changeInstanceState(
			final ResourceInstance resourceInstance, final String metricID,
			final ResourceMetricDef resourceMetricDef,
			final MetricCalculateData metricData) throws BaseException {

		 instanceStateComputeUtil.changeInstanceState(resourceInstance, metricID, resourceMetricDef, metricData);
	}

	public boolean flapping(long instanceId, String metricId,
			MetricStateEnum curMetricStateEnum,
			MetricStateEnum preMetricStateEnum, final int flapping)
			throws ProfilelibException {

		return flappingComputer.flapping(instanceId, metricId, curMetricStateEnum, preMetricStateEnum, flapping);
	}

	public void notifyMetricAlarmMsg(MetricCalculateData metricData,
			final ResourceMetricDef rdf, MetricStateData mps,
			MetricStateData oldStateData, ProfileThreshold threshold,
			Integer ftimes) {
		// 特殊处理
		if (MetricStateEnum.NORMAL == mps.getState() && (oldStateData == null || MetricStateEnum.isNotAlarm(oldStateData.getState()))) {
			return;
		}

		MetricStateChangeData mcd = new MetricStateChangeData();
		mcd.setOldState(oldStateData);
		mcd.setNewState(mps);
		mcd.setChangeNum(ftimes);
		mcd.setMetricDef(rdf);
		mcd.setCauseBySelf(true);
		mcd.setMetricData(metricData);
		mcd.setNotifiable(MetricTypeEnum.PerformanceMetric == rdf.getMetricType());

		if (threshold != null) {
			mcd.setThreshhold(threshold);
		}
		stateEngine.handleMetricStateChange(mcd);
	}

	public void notifyInstanceAlarmMsg (InstanceStateChangeData instanceStateChangeData) {
		stateEngine.handleInstanceStateChange(instanceStateChangeData);
	}

	public InstanceStateData getInstanceState(long instanceID) {
		return instanceStateDataCacheUtil.getInstanceState(instanceID);
	}

	public void saveInstanceState(InstanceStateData data) {
		instanceStateDataCacheUtil.saveInstanceState(data);
	}

	/**
	 * 清除资源状态：将资源的状态置为未知！ 在修改时，主动将影响指标调整为“可用性指标”
	 * 
	 * @param ides
	 */
	public void cleanInstanceStates(List<Long> ides) {
		instanceStateDataCacheUtil.cleanInstanceStates(ides);
	}

	public void cleanMetricStates(List<Long> ides) {
		metricStateSelectUtil.cleanMetricStates(ides);
	}

	public void removeFlapping(String key) {
		this.flappingComputer.removeFlapping(key);
	}

	public void saveAvailMetricData(long instanceID, String metricID, String metricData) {
		this.stateCacheUtils.setAvailabilityMetricDataIMemcache(instanceID, metricID, metricData);
	}

	public Map<String, String> getAvailMetricData(long instanceID) {
		AvailabilityMetricData availabilityMetricData = this.stateCacheUtils.getAvailabilityMetricDataIMemcache(instanceID);
		if (availabilityMetricData != null) {
			return availabilityMetricData.getMetricData();
		} else {
			return null;
		}
	}

	public static InstanceStateEnum convertMetricStateToInstanceState(MetricStateEnum metricState) {
		switch (metricState) {
		case CRITICAL:
			return InstanceStateEnum.CRITICAL;
		case CRITICAL_NOTHING:
			return InstanceStateEnum.CRITICAL_NOTHING;
		case SERIOUS:
			return InstanceStateEnum.SERIOUS;
		case WARN:
			return InstanceStateEnum.WARN;
		case NORMAL:
			return InstanceStateEnum.NORMAL;
		case NORMAL_UNKNOWN:
			return InstanceStateEnum.NORMAL;
		case NORMAL_NOTHING:
			return InstanceStateEnum.NORMAL_NOTHING;
		case UNKOWN:
			return InstanceStateEnum.NORMAL;
		case UNKNOWN_NOTHING:
			return InstanceStateEnum.UNKNOWN_NOTHING;
		default:
			throw new RuntimeException("can't convert null to instanceState!");
		}
	}

	/**
	 * 获取指定指标类型（性能指标或者可用性指标）的所有状态数据
	 * 
	 * @param instanceID
	 * @param resourceID
	 * @param metricType
	 * @return
	 * @throws ProfilelibException
	 * @throws CustomMetricException
	 */
	public List<MetricStateData> findMetricState(long instanceID, String resourceID, MetricTypeEnum metricType,
			long timelineId) throws ProfilelibException, CustomMetricException {

		return metricStateSelectUtil.findMetricState(instanceID, resourceID, metricType, timelineId);
	}

	public void saveMetricState(MetricStateData data) {
		metricStateSelectUtil.saveMetricState(data);
	}

	public static InstanceStateEnum checkChildStateForParent(
			InstanceStateEnum tmpState) {
		if (InstanceStateEnum.CRITICAL == tmpState)
			return InstanceStateEnum.NORMAL_CRITICAL;
		if (InstanceStateEnum.CRITICAL_NOTHING == tmpState)
			return InstanceStateEnum.NORMAL;
		if (InstanceStateEnum.UNKOWN == tmpState)
			return InstanceStateEnum.NORMAL;

		return tmpState;
	}

	public static InstanceStateChangeData generateNotifyInstanceState(
			InstanceStateData instanceStateData,
			InstanceStateData preInstanceStateData, boolean notify,
			int changeNum, MetricCalculateData metricCalculateData,
			String causeMetricId, ResourceMetricDef resourceMetricDef) {
		InstanceStateChangeData instanceStateChangeData = new InstanceStateChangeData();
		instanceStateChangeData.setMetricData(metricCalculateData);
		instanceStateChangeData.setNewState(instanceStateData);
		instanceStateChangeData.setOldState(preInstanceStateData);
		instanceStateChangeData.setChangeNum(changeNum);
		instanceStateChangeData.setNotifiable(notify);
		instanceStateChangeData.setCauseByMetricID(causeMetricId);
		instanceStateChangeData.setCauseByMetricDef(resourceMetricDef);
		return instanceStateChangeData;
	}

	public MetricStateData getMetricState(long instanceID, String metricID) {
		return this.metricStateSelectUtil.getMetricState(instanceID, metricID);
	}

	public InstanceStateData calculateSpecifiedInstanceState(
			ResourceInstance resourceInstance, long timelineId, boolean isCalculateResourceState) throws InstancelibException, ProfilelibException,
			CustomMetricException {
		return this.instanceStateComputeUtil.calculateSpecifiedInstanceState(
				resourceInstance, timelineId, isCalculateResourceState);
	}

	public InstanceStateData calculateMainInstanceState(
			ResourceInstance parentInstance, long timelineId, boolean isCalculateResourceState) {
		return this.instanceStateComputeUtil.calculateMainInstanceState(
				parentInstance, timelineId, isCalculateResourceState);
	}
}
