/**
 * 
 */
package com.mainsteam.stm.common.metric;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.common.metric.obj.CustomMetricMonitorInfo;
import com.mainsteam.stm.common.metric.obj.CustomMetricMonitorInfoWrapper;
import com.mainsteam.stm.metric.dao.CustomMetricBindDAO;
import com.mainsteam.stm.metric.dao.CustomMetricCollectDAO;
import com.mainsteam.stm.metric.dao.CustomMetricDAO;
import com.mainsteam.stm.metric.dao.CustomMetricDataWayDAO;
import com.mainsteam.stm.metric.dao.pojo.CustomMetricBindDO;
import com.mainsteam.stm.metric.dao.pojo.CustomMetricCollectDO;
import com.mainsteam.stm.metric.dao.pojo.CustomMetricDO;
import com.mainsteam.stm.metric.dao.pojo.CustomMetricDataWayDO;
import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.obj.ProfileInfo;

/**
 * @author ziw
 * 
 */
public class CustomMetricQueryService implements CustomMetricQueryServiceMBean {

	private static final Log logger = LogFactory
			.getLog(CustomMetricQueryService.class);

	private CustomMetricBindDAO customMetricBindDAO;

	private CustomMetricDAO customMetricDAO;

	private CustomMetricDataWayDAO dataWayDAO;

	private CustomMetricCollectDAO customMetricCollectDAO;

	private ProfileService profileService;

	/**
	 * 
	 */
	public CustomMetricQueryService() {
	}

	/**
	 * @param dataWayDAO
	 *            the dataWayDAO to set
	 */
	public final void setDataWayDAO(CustomMetricDataWayDAO dataWayDAO) {
		this.dataWayDAO = dataWayDAO;
	}

	/**
	 * @param customMetricBindDAO
	 *            the customMetricBindDAO to set
	 */
	public final void setCustomMetricBindDAO(
			CustomMetricBindDAO customMetricBindDAO) {
		this.customMetricBindDAO = customMetricBindDAO;
	}

	/**
	 * @param customMetricDAO
	 *            the customMetricDAO to set
	 */
	public final void setCustomMetricDAO(CustomMetricDAO customMetricDAO) {
		this.customMetricDAO = customMetricDAO;
	}

	/**
	 * @param customMetricCollectDAO
	 *            the customMetricCollectDAO to set
	 */
	public final void setCustomMetricCollectDAO(
			CustomMetricCollectDAO customMetricCollectDAO) {
		this.customMetricCollectDAO = customMetricCollectDAO;
	}

	/**
	 * @param profileService
	 *            the profileService to set
	 */
	public final void setProfileService(ProfileService profileService) {
		this.profileService = profileService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.common.metric.CustomMetricQueryServiceMBean#
	 * selectCustomMetricMonitorInfos(int)
	 */
	@Override
	public CustomMetricMonitorInfoWrapper selectCustomMetricMonitorInfos(
			int nodeGroupId) {
		if (logger.isInfoEnabled()) {
			logger.info("selectCustomMetricMonitorInfos start nodeGroupId="
					+ nodeGroupId);
		}
		CustomMetricMonitorInfoWrapper wrapper = null;

		try {
			List<CustomMetricBindDO> bindDOs = customMetricBindDAO
					.getCustomMetricBindByNodeGroupId(nodeGroupId);
			if (bindDOs != null && bindDOs.size() > 0) {
				wrapper = new CustomMetricMonitorInfoWrapper();
				List<CustomMetricMonitorInfo> customMetricMonitorInfos = new ArrayList<>();
				Set<Long> instanceIdSets = new HashSet<>(bindDOs.size());
				Map<String, List<Long>> metricInstanceIds = new HashMap<>();

				for (CustomMetricBindDO customMetricBindDO : bindDOs) {
					Long instanceIdObj = customMetricBindDO.getInstanceId();
					instanceIdSets.add(instanceIdObj);
					if (metricInstanceIds.containsKey(customMetricBindDO
							.getMetricId())) {
						metricInstanceIds.get(customMetricBindDO.getMetricId())
								.add(instanceIdObj);
					} else {
						List<Long> list = new ArrayList<>();
						list.add(instanceIdObj);
						metricInstanceIds.put(customMetricBindDO.getMetricId(),
								list);
					}
				}

				Map<Long, ProfileInfo> resourceInstanceIdProfileMap = profileService
						.getBasicInfoByResourceInstanceIds(new ArrayList<>(
								instanceIdSets));

				List<String> metricIds = new ArrayList<>(
						metricInstanceIds.keySet());

				List<CustomMetricDO> customMetricDOs = customMetricDAO
						.getMetrics(metricIds);
				List<CustomMetricCollectDO> customMetricCollectDOs = customMetricCollectDAO
						.getMetricCollectDOById(metricIds);
				List<CustomMetricDataWayDO> dataWayDOs = dataWayDAO
						.getCustomMetricDataWayByMetrics(metricIds);
				Map<String, Map<String, String>> dataWayMap = null;
				if (dataWayDOs != null && dataWayDOs.size() > 0) {
					dataWayMap = new HashMap<>();
					for (CustomMetricDataWayDO customMetricDataWayDO : dataWayDOs) {
						if (dataWayMap.containsKey(customMetricDataWayDO
								.getMetricId())) {
							dataWayMap.get(customMetricDataWayDO.getMetricId())
									.put(customMetricDataWayDO.getPluginId(),
											customMetricDataWayDO.getDataWay());
						} else {
							Map<String, String> map = new HashMap<>();
							map.put(customMetricDataWayDO.getPluginId(),
									customMetricDataWayDO.getDataWay());
							dataWayMap.put(customMetricDataWayDO.getMetricId(),
									map);
						}
					}
				}

				Map<String, Map<String, List<ParameterValue>>> parametersMap = new HashMap<>(
						customMetricDOs.size());
				for (CustomMetricCollectDO customMetricCollectDO : customMetricCollectDOs) {
					Map<String, List<ParameterValue>> parameterMap = null;
					if (parametersMap.containsKey(customMetricCollectDO
							.getMetricId())) {
						parameterMap = parametersMap.get(customMetricCollectDO
								.getMetricId());
					} else {
						parameterMap = new HashMap<>();
						parametersMap.put(customMetricCollectDO.getMetricId(),
								parameterMap);
					}
					List<ParameterValue> values = null;
					if (parameterMap.containsKey(customMetricCollectDO
							.getPluginId())) {
						values = parameterMap.get(customMetricCollectDO
								.getPluginId());
					} else {
						values = new ArrayList<ParameterValue>();
						parameterMap.put(customMetricCollectDO.getPluginId(),
								values);
					}
					ParameterValue p = new ParameterValue();
					p.setKey(customMetricCollectDO.getParamKey());
					p.setType(customMetricCollectDO.getParamType());
					p.setValue(customMetricCollectDO.getParamValue());
					values.add(p);
				}

				for (CustomMetricDO customMetricDO : customMetricDOs) {
					Map<String, List<ParameterValue>> parameterMap = parametersMap
							.get(customMetricDO.getCustomMetricId());
					if (parameterMap == null) {
						if (logger.isWarnEnabled()) {
							StringBuilder b = new StringBuilder(
									"selectCustomMetricMonitorInfos customMetric has not collect setting.");
							b.append(" metricId=").append(
									customMetricDO.getCustomMetricId());
							logger.warn(b.toString());
						}
						continue;
					}
					List<Long> instanceIdList = metricInstanceIds
							.get(customMetricDO.getCustomMetricId());
					if (instanceIdList == null) {
						if (logger.isWarnEnabled()) {
							StringBuilder b = new StringBuilder(
									"selectCustomMetricMonitorInfos customMetric has not instanceIdList setting.");
							b.append(" metricId=").append(
									customMetricDO.getCustomMetricId());
							logger.warn(b.toString());
						}
						continue;
					}

					if (logger.isInfoEnabled()) {
						StringBuilder b = new StringBuilder(
								"selectCustomMetricMonitorInfos ");
						b.append(" metricId=").append(
								customMetricDO.getCustomMetricId());
						logger.info(b.toString());
					}

					for (Iterator<Map.Entry<String, List<ParameterValue>>> iterator = parameterMap
							.entrySet().iterator(); iterator.hasNext();) {
						Map.Entry<String, List<ParameterValue>> entry = iterator
								.next();
						String pluginId = entry.getKey();
						List<ParameterValue> value = entry.getValue();
						ParameterValue[] values = value
								.toArray(new ParameterValue[value.size()]);
						String dataWay = null;
						if (dataWayMap != null
								&& dataWayMap.containsKey(customMetricDO
										.getCustomMetricId())) {
							dataWay = dataWayMap.get(
									customMetricDO.getCustomMetricId()).get(
									pluginId);
						}
						boolean isMonitor = customMetricDO.getIsMonitor()
								.equals("1");
						for (int i = 0; i < instanceIdList.size(); i++) {
							CustomMetricMonitorInfo monitorInfo = new CustomMetricMonitorInfo();
							monitorInfo.setCustomMetricId(customMetricDO
									.getCustomMetricId());
							monitorInfo.setFreq(customMetricDO.getFreq());
							monitorInfo.setMonitor(isMonitor);
							monitorInfo.setParameters(values);
							monitorInfo.setPluginId(pluginId);
							monitorInfo.setInstanceId(instanceIdList.get(i));
							monitorInfo.setCustomMetricProcessWay(dataWay);
							customMetricMonitorInfos.add(monitorInfo);
						}
					}
				}

				Map<Long, Long> instanceProfileMap = new HashMap<>(
						resourceInstanceIdProfileMap.size());
				for (Iterator<Entry<Long, ProfileInfo>> iterator = resourceInstanceIdProfileMap
						.entrySet().iterator(); iterator.hasNext();) {
					Entry<Long, ProfileInfo> entry = iterator.next();
					instanceProfileMap.put(entry.getKey(), entry.getValue()
							.getProfileId());
				}

				wrapper.setInstanceProfileMap(instanceProfileMap);
				wrapper.setCustomMetricMonitorInfos(customMetricMonitorInfos
						.toArray(new CustomMetricMonitorInfo[customMetricMonitorInfos
								.size()]));
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("selectCustomMetricMonitorInfos", e);
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("selectCustomMetricMonitorInfos end");
		}
		return wrapper;
	}
}
