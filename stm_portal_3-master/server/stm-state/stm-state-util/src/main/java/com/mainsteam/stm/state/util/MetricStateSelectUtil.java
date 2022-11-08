package com.mainsteam.stm.state.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.metric.MetricStateService;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.metric.exception.CustomMetricException;
import com.mainsteam.stm.metric.obj.CustomMetric;
import com.mainsteam.stm.metric.obj.CustomMetricInfo;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;
import com.mainsteam.stm.state.obj.MetricStateData;

/**
 * @author 作者：ziw
 * @date 创建时间：2016年11月18日 上午10:00:36
 * @version 1.0
 */
public class MetricStateSelectUtil {

	private static final Log logger = LogFactory
			.getLog(MetricStateSelectUtil.class);

	private final ProfileMetricSelectUtil profileMetricSelectUtil;
	private final CapacityService capacityService;
	private final StateCacheUtils stateCacheUtils;
	private final MetricStateService metricStateService;

	public MetricStateSelectUtil(
			ProfileMetricSelectUtil profileMetricSelectUtil,
			CapacityService capacityService, StateCacheUtils stateCacheUtils,
			MetricStateService metricStateService) {
		super();
		this.profileMetricSelectUtil = profileMetricSelectUtil;
		this.capacityService = capacityService;
		this.stateCacheUtils = stateCacheUtils;
		this.metricStateService = metricStateService;
	}

	/**
	 * @param instanceID
	 * @param metricID
	 * @return
	 */
	public MetricStateData getMetricState(long instanceID, String metricID) {
		MetricStateData oldData = stateCacheUtils.getMetricState(instanceID, metricID);
		if (oldData == null) {
			oldData = metricStateService.getMetricState(instanceID, metricID);
			if (oldData != null)
				stateCacheUtils.setMetricState(oldData);
		}
		if (logger.isTraceEnabled()){
			StringBuffer b = new StringBuffer(100);
			b.append("get metric state ");
			b.append(" instanceID= ");
			b.append(instanceID);
			b.append(" metricID= ");
			b.append(metricID);
			b.append(" ");
			b.append(JSONObject.toJSONString(oldData));
			logger.trace(b);
		}
		return oldData;
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

		if (StringUtils.isEmpty(resourceID)) {
			throw new NullPointerException("[instance:" + instanceID
					+ "] resourceID is null,please check!");
		}
		// 策略指标
		List<ProfileMetric> profileMetricList = profileMetricSelectUtil.findProfileMetrics(instanceID, resourceID, timelineId, metricType);
		List<MetricStateData> metricStateDataList = new ArrayList<MetricStateData>();
		// 策略指标
		if (profileMetricList != null && !profileMetricList.isEmpty()) {
			for (ProfileMetric profileMetric : profileMetricList) {
				if(!(profileMetric.isAlarm() || profileMetric.isMonitor()))
					continue;
				MetricStateData data = getMetricState(instanceID, profileMetric.getMetricId());
				if (data != null) {
					ResourceMetricDef resourceMetricDef = capacityService.getResourceMetricDef(resourceID, profileMetric.getMetricId());
					data.setType(resourceMetricDef.getMetricType());
					metricStateDataList.add(data);
				}
			}
		}

		// 自定义指标
		List<CustomMetric> customMetricList = profileMetricSelectUtil.findCustomMetricsByType(instanceID, metricType);
		if (customMetricList != null && !customMetricList.isEmpty()) {
			for (CustomMetric customMetric : customMetricList) {
				CustomMetricInfo customMetricInfo = customMetric.getCustomMetricInfo();
				if (!(customMetricInfo.isAlert() || customMetricInfo.isMonitor())) {
					continue;
				}
				MetricStateData data = getMetricState(instanceID, customMetricInfo.getId());
				if (data != null) {
					data.setType(customMetricInfo.getStyle());
					metricStateDataList.add(data);
				}
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("findMetricState with instanceID{" + instanceID
					+ "}, resourceID {" + resourceID + "}:"
					+ JSON.toJSONString(metricStateDataList));
		}
		return metricStateDataList;
	}

	public void cleanMetricStates(List<Long> ides) {
		List<MetricStateData> mss = metricStateService.findMetricState(ides,
				null);
		for (MetricStateData ms : mss) {
			ms.setState(MetricStateEnum.NORMAL);
			ms.setCollectTime(new Date());
			saveMetricState(ms);
		}
	}

	public void saveMetricState(MetricStateData data) {
		if (logger.isDebugEnabled())
			logger.debug("save metric state" + JSONObject.toJSONString(data));
		boolean successful = stateCacheUtils.setMetricState(data);
		if (!successful) {
			if (logger.isWarnEnabled()) {
				logger.warn("failed to cache metric state with " + data);
			}
		}
		metricStateService.updateMetricState(data);
	}

}
