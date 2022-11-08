/**
 * 
 */
package com.mainsteam.stm.profilelib.deploy;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.dict.FrequentEnum;
import com.mainsteam.stm.common.metric.obj.CustomMetricMonitorInfo;
import com.mainsteam.stm.executor.obj.CustomMetricPluginParameter;
import com.mainsteam.stm.metric.objenum.CustomMetricChangeEnum;
import com.mainsteam.stm.metric.objenum.CustomMetricDataProcessWayEnum;
import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.scheduler.SchedulerService;
import com.mainsteam.stm.scheduler.obj.CustomMetricScheduleTask;

/**
 * @author ziw
 * 
 */
public class CustomMetricCollectDeploy implements
		CustomMetricCollectDeployMBean {

	private static final Log logger = LogFactory
			.getLog(CustomMetricCollectDeploy.class);

	private SchedulerService schedulerService;

	/**
	 * 
	 */
	public CustomMetricCollectDeploy() {
	}

	/**
	 * @param schedulerService
	 *            the schedulerService to set
	 */
	public final void setSchedulerService(SchedulerService schedulerService) {
		this.schedulerService = schedulerService;
	}

	private String getProcessClass(String processWay) {
		if (processWay != null) {
			CustomMetricDataProcessWayEnum wayEnum = CustomMetricDataProcessWayEnum
					.valueOf(processWay);
			if (wayEnum == CustomMetricDataProcessWayEnum.MAX) {
				return CustomMetricDataProcessMaxProcesser.class.getName();
			} else if (wayEnum == CustomMetricDataProcessWayEnum.AVG) {
				return CustomMetricDataProcessAvgProcesser.class.getName();
			} else if (wayEnum == CustomMetricDataProcessWayEnum.MIN) {
				return CustomMetricDataProcessMinProcesser.class.getName();
			}
		}
		return null;
	}

	public void addCustomMetricMonitor(
			CustomMetricMonitorInfo customMetricMonitorInfo,
			Map<Long, Long> profileMap) {
		if (logger.isInfoEnabled()) {
			logger.info("addCustomMetricMonitor start");
		}
		if (profileMap == null) {
			if (logger.isErrorEnabled()) {
				logger.error("addCustomMetricMonitor profileMap is null.");
			}
			return;
		}
		long instanceId = customMetricMonitorInfo.getInstanceId();
		Long profileId = profileMap.get(instanceId);
		if (profileId == null) {
			if (logger.isWarnEnabled()) {
				logger.warn("addCustomMetricMonitor instanceId's profileid not exist.");
			}
			return;
		}
		CustomMetricScheduleTask task = new CustomMetricScheduleTask();
		task.setActive(customMetricMonitorInfo.isMonitor());
		task.setMetricId(customMetricMonitorInfo.getCustomMetricId());
		CustomMetricPluginParameter parameter = new CustomMetricPluginParameter();
		parameter
				.setCustomMetricId(customMetricMonitorInfo.getCustomMetricId());
		parameter.setInstanceId(instanceId);
		parameter.setParameters(customMetricMonitorInfo.getParameters());
		parameter.setPluginId(customMetricMonitorInfo.getPluginId());
		parameter.setDataProcessClass(getProcessClass(customMetricMonitorInfo
				.getCustomMetricProcessWay()));
		task.setPluginParameter(parameter);
		task.setTimeFire(ProfileCollectDeploy.getPeriodTimeFire(FrequentEnum
				.valueOf(customMetricMonitorInfo.getFreq())));
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder("addCustomMetricMonitor");
			b.append(" ProfileId=").append(profileId);
			b.append(" InstanceId=").append(instanceId);
			b.append(" CustomMetricId=").append(
					customMetricMonitorInfo.getCustomMetricId());
			b.append(" PluginId=")
					.append(customMetricMonitorInfo.getPluginId());
			b.append(" CustomMetricProcessWay=").append(
					customMetricMonitorInfo.getCustomMetricProcessWay());
			b.append(" isMonitor=").append(customMetricMonitorInfo.isMonitor());
			b.append(" Freq=").append(customMetricMonitorInfo.getFreq());
			b.append(" ParameterValue=[");
			if (customMetricMonitorInfo.getParameters() != null) {
				for (ParameterValue value : customMetricMonitorInfo
						.getParameters()) {
					b.append(value.getKey()).append('(')
							.append(value.getType()).append(')').append('=')
							.append(value.getValue());
				}
			}
			b.append(']');
			logger.info(b.toString());
		}
		schedulerService.schedule(task);
		if (logger.isInfoEnabled()) {
			logger.info("addCustomMetricMonitor end");
		}
	}

	private void deleteCustomMetricMonitor(
			CustomMetricMonitorInfo customMetricMonitorInfo,
			Map<Long, Long> profileMap) {
		if (logger.isInfoEnabled()) {
			logger.info("deleteCustomMetricMonitor start");
		}
		long instanceId = customMetricMonitorInfo.getInstanceId();
		Long profileId = null;
		if (profileMap != null) {
			profileId = profileMap.get(instanceId);
		}
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder("deleteCustomMetricMonitor ");
			b.append(" ProfileId=").append(profileId);
			b.append(" InstanceId=").append(instanceId);
			b.append(" CustomMetricId=").append(
					customMetricMonitorInfo.getCustomMetricId());
			b.append(" isMonitor=").append(customMetricMonitorInfo.isMonitor());
			b.append(" Freq=").append(customMetricMonitorInfo.getFreq());
			logger.info(b.toString());
		}
		if (profileId != null) {
			schedulerService.removeCustomMetricScheduleTaskByInstanceId(
					profileId.longValue(), instanceId,
					customMetricMonitorInfo.getCustomMetricId());
		} else {
			schedulerService
					.removeCustomMetricTasksByResourceInstanceIds(new long[] { instanceId });
		}
		if (logger.isInfoEnabled()) {
			logger.info("deleteCustomMetricMonitor end");
		}
	}

	public void changeCustomMetricMonitorMonitorInfo(
			CustomMetricMonitorInfo customMetricMonitorInfo,
			Map<Long, Long> profileMap, boolean collect) {
		if (logger.isInfoEnabled()) {
			logger.info("changeCustomMetricMonitorMonitorInfo start collect="
					+ collect);
		}
		if (profileMap == null) {
			if (logger.isErrorEnabled()) {
				logger.error("changeCustomMetricMonitorMonitorInfo profileMap is null.");
			}
			return;
		}
		if (collect) {
			if (logger.isInfoEnabled()) {
				StringBuilder b = new StringBuilder(
						"changeCustomMetricsPluginParameters ");
				b.append(" CustomMetricId=").append(
						customMetricMonitorInfo.getCustomMetricId());
				b.append(" ParameterValue=[");
				if (customMetricMonitorInfo.getParameters() != null) {
					for (ParameterValue value : customMetricMonitorInfo
							.getParameters()) {
						b.append(value.getKey()).append('(')
								.append(value.getType()).append(')')
								.append('=').append(value.getValue());
					}
				}
				b.append(']');
				logger.info(b.toString());
			}
		} else {
			if (logger.isInfoEnabled()) {
				StringBuilder b = new StringBuilder(
						"changeCustomMetricMonitorMonitorInfo ");
				b.append(" CustomMetricId=").append(
						customMetricMonitorInfo.getCustomMetricId());
				b.append(" isMonitor=").append(
						customMetricMonitorInfo.isMonitor());
				b.append(" CustomMetricProcessWay=").append(
						customMetricMonitorInfo.getCustomMetricProcessWay());
				b.append(" Freq=").append(customMetricMonitorInfo.getFreq());
				logger.info(b.toString());
			}
		}
		List<CustomMetricScheduleTask> tasks = schedulerService
				.getCustomMetricScheduleTasksByMetricId(customMetricMonitorInfo
						.getCustomMetricId());
		if (tasks != null && tasks.size() > 0) {
			for (CustomMetricScheduleTask task : tasks) {
				if (collect) {
					task.getPluginParameter().setParameters(
							customMetricMonitorInfo.getParameters());
				} else {
					task.setActive(customMetricMonitorInfo.isMonitor());
					if (customMetricMonitorInfo.getFreq() != null) {
						task.setTimeFire(ProfileCollectDeploy
								.getPeriodTimeFire(FrequentEnum
										.valueOf(customMetricMonitorInfo
												.getFreq())));
					}
					task.getPluginParameter().setDataProcessClass(
							getProcessClass(customMetricMonitorInfo
									.getCustomMetricProcessWay()));
				}
				if (logger.isInfoEnabled()) {
					StringBuilder b = new StringBuilder(
							"changeCustomMetricMonitorMonitorInfo ok.");
					b.append(" ProfileId=").append(task.getProfileId());
					b.append(" InstanceId=").append(
							task.getPluginParameter().getInstanceId());
					b.append(" PluginId=").append(
							task.getPluginParameter().getPluginId());
					logger.info(b.toString());
				}
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("changeCustomMetricMonitorMonitorInfo end");
		}
	}

	public void deleteCustomMetricMonitorsByInstanceId(
			Set<Long> toDeleteInstanceIds, Map<Long, Long> profileMap) {
		if (logger.isInfoEnabled()) {
			logger.info("deleteCustomMetricMonitorsByInstanceId start");
		}
		if (toDeleteInstanceIds != null && toDeleteInstanceIds.size() > 0) {
			int i = 0;
			long[] instanceIdValues = new long[toDeleteInstanceIds.size()];
			for (Long long1 : toDeleteInstanceIds) {
				instanceIdValues[i] = long1;
				i++;
			}
			schedulerService
					.removeCustomMetricTasksByResourceInstanceIds(instanceIdValues);
		}
		if (logger.isInfoEnabled()) {
			logger.info("deleteCustomMetricMonitorsByInstanceId end");
		}
	}

	@Override
	public void deployCustomMetricMonitors(
			CustomMetricMonitorInfo[] customMetricMonitorInfos,
			Map<Long, Long> profileMap) {
		if (logger.isInfoEnabled()) {
			logger.info("deployCustomMetricMonitors start");
		}
		if (customMetricMonitorInfos == null
				|| customMetricMonitorInfos.length <= 0) {
			if (logger.isWarnEnabled()) {
				logger.warn("addCustomMetricMonitors customMetricMonitorInfos is empty.");
			}
			return;
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("addCustomMetricMonitors customMetricMonitorInfos.size="
						+ customMetricMonitorInfos.length);
			}
		}
		if (profileMap == null) {
			if (logger.isErrorEnabled()) {
				logger.error("addCustomMetricMonitors profileMap is null.");
			}
			return;
		}
		Set<Long> toDeleteInstanceIds = new HashSet<>();
		for (CustomMetricMonitorInfo customMetricMonitorInfo : customMetricMonitorInfos) {
			CustomMetricChangeEnum changeEnum = CustomMetricChangeEnum
					.valueOf(customMetricMonitorInfo.getChangeAction());
			if (changeEnum == CustomMetricChangeEnum.INSTANCE_CANCEL_MONITOR) {
				toDeleteInstanceIds
						.add(customMetricMonitorInfo.getInstanceId());
				continue;
			} else if (toDeleteInstanceIds.size() > 0) {
				deleteCustomMetricMonitorsByInstanceId(toDeleteInstanceIds,
						profileMap);
				toDeleteInstanceIds.clear();
			}
			if (changeEnum == CustomMetricChangeEnum.ADD_METRIC_PLUGIN_BIND
					|| changeEnum == CustomMetricChangeEnum.INSTANCE_MONITOR) {
				addCustomMetricMonitor(customMetricMonitorInfo, profileMap);
			} else if (changeEnum == CustomMetricChangeEnum.DELETE_METRIC_PLUGIN_BIND) {
				deleteCustomMetricMonitor(customMetricMonitorInfo, profileMap);
			} else if (changeEnum == CustomMetricChangeEnum.CHANGE_METRIC_PLUGIN_COLLECT) {
				changeCustomMetricMonitorMonitorInfo(customMetricMonitorInfo,
						profileMap, true);
			} else if (changeEnum == CustomMetricChangeEnum.METRIC_BASIC_UPDATE) {
				changeCustomMetricMonitorMonitorInfo(customMetricMonitorInfo,
						profileMap, false);
			}
		}

		if (logger.isInfoEnabled()) {
			logger.info("deployCustomMetricMonitors end");
		}
	}
}
