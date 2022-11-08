package com.mainsteam.stm.state.util;

import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.metric.exception.CustomMetricException;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.state.obj.MetricStateData;
import com.mainsteam.stm.state.thirdparty.ThirdPartyMetricStateService;
import com.mainsteam.stm.state.thirdparty.obj.ThirdPartyMetricStateData;

/**
 * @author 作者：ziw
 * @date 创建时间：2016年11月18日 下午3:36:29
 * @version 1.0
 */
public class PerformanceMetricsStateUtil {

	private static final Log logger = LogFactory.getLog(PerformanceMetricsStateUtil.class);
	private final MetricStateSelectUtil metricStateSelectUtil;
	private final ResourceInstanceService resourceInstanceService;
	private final ThirdPartyMetricStateService thirdPartyMetricStateService;

	public PerformanceMetricsStateUtil(
			MetricStateSelectUtil metricStateSelectUtil,
			ResourceInstanceService resourceInstanceService,
			ThirdPartyMetricStateService thirdPartyMetricStateService) {
		super();
		this.metricStateSelectUtil = metricStateSelectUtil;
		this.resourceInstanceService = resourceInstanceService;
		this.thirdPartyMetricStateService = thirdPartyMetricStateService;
	}

	/**
	 * 获取指定实例下的所有性能指标（不包括任何子资源的性能指标）最高级别状态数据
	 *
	 * @param instanceId
	 * @return
	 * @throws InstancelibException
	 * @throws ProfilelibException
	 * @throws CustomMetricException
	 */
	public MetricStateData calculatePerformanceMetricsState(long instanceId,long timelineId)
			throws InstancelibException, ProfilelibException,
			CustomMetricException {
		ResourceInstance resourceInstance = resourceInstanceService.getResourceInstance(instanceId);
		if (resourceInstance == null || resourceInstance.getLifeState() == InstanceLifeStateEnum.NOT_MONITORED) {
			if (logger.isWarnEnabled()) {
				logger.warn("Can not find instance with id{" + instanceId
						+ "}, maybe null or not monitored");
			}
			return null;
		}
		// 获取当前实例下所有已监控已告警的性能指标状态
		List<MetricStateData> metricStateDataList = metricStateSelectUtil.findMetricState(instanceId,
				resourceInstance.getResourceId(), MetricTypeEnum.PerformanceMetric, timelineId);
		// 获取第三方告警状态
		MetricStateData thirdPartyMetricState = findThirdPartyMetricState(instanceId);
		if (thirdPartyMetricState != null)
			metricStateDataList.add(thirdPartyMetricState);

		if (metricStateDataList != null && !metricStateDataList.isEmpty()) {
			MetricStateData metricStateData = Collections.max(metricStateDataList);
			if (MetricStateEnum.isUnknown(metricStateData.getState())) {
				metricStateData.setState(MetricStateEnum.NORMAL);
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Instance{" + instanceId
						+ "} has found the most serious metric state data {"
						+ metricStateData.toString() + "}");
			}
			return metricStateData;
		} else {
			if (logger.isInfoEnabled()) {
				logger.info("Instance{" + instanceId
						+ "} has not found any metric state.");
			}
			return null;
		}
	}

	/**
	 * 获取绑定到当前实例下的第三方状态
	 * 
	 * @param instanceId
	 * @return
	 */
	public MetricStateData findThirdPartyMetricState(long instanceId) {
		List<ThirdPartyMetricStateData> thirdPartyMetricStateDataList = thirdPartyMetricStateService.findThirdPartyMetricState(instanceId);
		if (null != thirdPartyMetricStateDataList && !thirdPartyMetricStateDataList.isEmpty()) {
			ThirdPartyMetricStateData thirdPartyMetricStateData = Collections.max(thirdPartyMetricStateDataList);
			if (logger.isDebugEnabled()) {
				logger.debug("Find the third party, " + thirdPartyMetricStateData.toString());
			}
			MetricStateData metricStateData = new MetricStateData();
			metricStateData.setInstanceID(thirdPartyMetricStateData.getInstanceID());
			metricStateData.setMetricID(thirdPartyMetricStateData.getMetricID());
			metricStateData.setCollectTime(thirdPartyMetricStateData.getUpdateTime());
			metricStateData.setState(thirdPartyMetricStateData.getState());
			metricStateData.setType(MetricTypeEnum.PerformanceMetric);

			return metricStateData;
		}
		return null;
	}

}
