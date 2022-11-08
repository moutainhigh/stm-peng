package com.mainsteam.stm.state.util;

import java.util.ArrayList;
import java.util.List;

import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.metric.CustomMetricService;
import com.mainsteam.stm.metric.exception.CustomMetricException;
import com.mainsteam.stm.metric.obj.CustomMetric;
import com.mainsteam.stm.metric.obj.CustomMetricInfo;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.TimelineService;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;

/**
 * @author 作者：ziw
 * @date 创建时间：2016年11月18日 上午9:20:48
 * @version 1.0
 */
public class ProfileMetricSelectUtil {

	private static final Log logger = LogFactory.getLog(ProfileMetricSelectUtil.class);

	private final ProfileService profileService;
	private final CustomMetricService customMetricService;
	private final TimelineService timelineService;
	private final CapacityService capacityService;

	public ProfileMetricSelectUtil(ProfileService profileService,
			CustomMetricService customMetricService,
			TimelineService timelineService, CapacityService capacityService) {
		super();
		this.profileService = profileService;
		this.customMetricService = customMetricService;
		this.timelineService = timelineService;
		this.capacityService = capacityService;
	}

	public List<ProfileMetric> findProfileMetrics(long instanceID, String resourceID, long timelineId, MetricTypeEnum metricTypeEnum)
			throws ProfilelibException {
		List<ProfileMetric> profileMetrics = null;
		// 策略指标
		List<ProfileMetric> profileMetricList = null;
		if (timelineId > 0) {
			profileMetricList = timelineService.getMetricByTimelineId(timelineId);
		} else {
			profileMetricList = profileService.getMetricByInstanceId(instanceID);
		}
		// 策略指标
		if (!CollectionUtils.isEmpty(profileMetricList)) {
			profileMetrics = new ArrayList<>(profileMetricList.size());
			for (ProfileMetric profileMetric : profileMetricList) {
				ResourceMetricDef resourceMetricDef = capacityService.getResourceMetricDef(resourceID, profileMetric.getMetricId());
				if (resourceMetricDef == null) {
					logger.warn("can't find ResourceMetricDef [" + resourceID
							+ "," + profileMetric.getMetricId()
							+ "],please check!");
					continue;
				}
				if(null == metricTypeEnum){
					profileMetrics.add(profileMetric);
				}else if (metricTypeEnum == resourceMetricDef.getMetricType()) {
					profileMetrics.add(profileMetric);
				}
			}
		}
		return profileMetrics;
	}

	/**
	 * 获取指定类型的自定义指标
	 * 
	 * @param instanceID
	 * @return
	 * @throws CustomMetricException
	 */
	public List<CustomMetric> findCustomMetricsByType(long instanceID, MetricTypeEnum metricType) throws CustomMetricException {
		// 自定义指标
		List<CustomMetric> customMetricList = this.customMetricService.getCustomMetricsByInstanceId(instanceID);
		if (customMetricList != null && !customMetricList.isEmpty()) {
			List<CustomMetric> customMetrics = new ArrayList<>(customMetricList.size());
			for (CustomMetric customMetric : customMetricList) {
				CustomMetricInfo customMetricInfo = customMetric.getCustomMetricInfo();
				if(null == metricType) {
					customMetrics.add(customMetric);
				}else if(customMetricInfo.getStyle() == metricType){
					customMetrics.add(customMetric);
				}
			}
			return customMetrics;
		}
		return null;
	}
}
