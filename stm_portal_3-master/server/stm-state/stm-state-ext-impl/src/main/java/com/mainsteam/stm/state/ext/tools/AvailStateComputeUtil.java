package com.mainsteam.stm.state.ext.tools;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.state.Availability;
import com.mainsteam.stm.caplib.state.Collectibility;
import com.mainsteam.stm.common.instance.obj.CollectStateEnum;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component("availStateComputeUtil")
public class AvailStateComputeUtil {

	private static final Log logger = LogFactory.getLog(AvailStateComputeUtil.class);

	@Autowired
	private CapacityService capacityService;

	/**
	 * 根据资源实例ID获取当前资源下所有的可用性指标进行联合计算，即计算资源状态，资源状态取消‘未知’状态
	 * 1.不需要判断可用性指标取消告警的情况，在策略变化时，如果可用性指标取消告警，则需要将缓存清除
	 * @param resourceInstance
	 * @return
	 */
	public InstanceStateEnum calculateResourceState(ResourceInstance resourceInstance, Map<String, MetricStateEnum> cacheData) {

		Availability resourceState;
		try{
			resourceState =	capacityService.evaluateResourceAvailabilityByMetricState(resourceInstance.getResourceId(), cacheData);
		}catch (Exception e) {
			if(logger.isWarnEnabled()) {
				StringBuffer sb = new StringBuffer();
				sb.append("Fail to compute resource state(data:");
				sb.append(resourceInstance.getId()).append("/");
				sb.append(resourceInstance.getResourceId());
				sb.append("/");
				sb.append(cacheData);
				sb.append(").");
				logger.warn(sb.toString());
			}
			return null;
		}

		if (Availability.AVAILABLE == resourceState) {
			return InstanceStateEnum.NORMAL;
		} else if (Availability.UNAVAILABLE == resourceState) {
			return InstanceStateEnum.CRITICAL;
		} else {
			return InstanceStateEnum.UNKOWN;
		}

	}

	/**
	 * 计算当前可用性指标的可用性状态
	 *
	 * @param resourceInstance
	 * @param metricID
	 * @param metricData
	 * @return
	 */
	@Deprecated
	public MetricStateEnum calculateAvailMetricState(ResourceInstance resourceInstance, String metricID, String metricData) {
		Map<String, String> metricMap = new HashMap<>(1);
		metricMap.put(metricID, metricData);

		Availability availabilityState ;
		try{
			availabilityState = capacityService.evaluateResourceAvailability(resourceInstance.getResourceId(), metricMap);
		}catch (Exception e) {
			if(logger.isErrorEnabled()) {
				StringBuffer sb = new StringBuffer();
				sb.append("EvaluateResourceAvailability failed, instance is {");
				sb.append(resourceInstance.getId()).append(":").append(metricID).append("}, resource is ");
				sb.append(resourceInstance.getResourceId());
				logger.error(sb.toString());
			}
			return null;
		}
		if(availabilityState == Availability.AVAILABLE){
			if(logger.isDebugEnabled()){
				StringBuffer bf = new StringBuffer();
				bf.append("Calculate AvailMetricState AVAILABLE is {").append(resourceInstance.getId());
				bf.append(":").append(metricID);
				bf.append("}, value is ").append(metricMap);
				logger.debug(bf);
			}
			return MetricStateEnum.NORMAL;
		}else if(availabilityState == Availability.UNAVAILABLE){
			if(logger.isDebugEnabled()){
				StringBuffer bf = new StringBuffer();
				bf.append("Calculate AvailMetricState  UNAVAILABLE is {").append(resourceInstance.getId());
				bf.append(":").append(metricID);
				bf.append("}, value is ").append(metricMap);
				logger.debug(bf);
			}
			return MetricStateEnum.CRITICAL;
		}else {
			return MetricStateEnum.UNKOWN;
		}

	}

	/**
	 * 计算可采集状态
	 * @param resourceInstance
	 * @param cacheData
	 * @return
	 */
	public CollectStateEnum calculateCollectState(ResourceInstance resourceInstance, Map<String, String> cacheData) {

		Collectibility resourceState ;
		try{
			resourceState =	capacityService.evaluateResourceCollectibility(resourceInstance.getResourceId(), cacheData);
		}catch (Exception e) {
			if(logger.isErrorEnabled()) {
				StringBuffer sb = new StringBuffer();
				sb.append("Fail to compute collecting state(data:");
				sb.append(resourceInstance.getId()).append("/");
				sb.append(resourceInstance.getResourceId());
				sb.append("/");
				sb.append(cacheData);
				sb.append(").");
				logger.error(sb.toString());
			}
			return null;
		}

		if (Collectibility.COLLECTIBLE == resourceState) {
			return CollectStateEnum.COLLECTIBLE;
		} else if (Collectibility.UNCOLLECTIBLE == resourceState) {
			return CollectStateEnum.UNCOLLECTIBLE;
		} else {
			return null;
		}
	}

}
