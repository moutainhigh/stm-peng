/**
 * 
 */
package com.mainsteam.stm.metric.service.impl.job;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.common.metric.obj.CustomMetricMonitorInfo;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.metric.CustomMetricChangeService;
import com.mainsteam.stm.metric.CustomMetricService;
import com.mainsteam.stm.metric.exception.CustomMetricException;
import com.mainsteam.stm.metric.obj.CustomMetric;
import com.mainsteam.stm.metric.obj.CustomMetricBind;
import com.mainsteam.stm.metric.obj.CustomMetricChange;
import com.mainsteam.stm.metric.obj.CustomMetricChangeResult;
import com.mainsteam.stm.metric.obj.CustomMetricCollectParameter;
import com.mainsteam.stm.metric.objenum.CustomMetricChangeEnum;
import com.mainsteam.stm.node.LocaleNodeService;
import com.mainsteam.stm.node.NodeGroup;
import com.mainsteam.stm.node.exception.NodeException;
import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.deploy.CustomMetricCollectDeployMBean;
import com.mainsteam.stm.profilelib.obj.ProfileInfo;
import com.mainsteam.stm.rpc.client.OCRPCClient;
import com.mainsteam.stm.util.SpringBeanUtil;


public class CustomMetricDeployTimer {

	private static final Log logger = LogFactory
			.getLog(CustomMetricDeployTimer.class);

	private CustomMetricChangeService changeService;

	private CustomMetricService metricService;

	private ResourceInstanceService resourceInstanceService;

	private ProfileService profileService;

	private OCRPCClient rpcClient;

	private int max_changes = 5000;

	private LocaleNodeService localeNodeService;
	//10秒
	private final static long PERIOD = 1000 * 10;
		
	private volatile boolean isRun = true;
	//存放当前处理器下采集器组Id
	private HashMap<String,String> allNodeGroupIds = new HashMap<>(5);
	
	/**
	 * 
	 */
	public CustomMetricDeployTimer() {
	}

	
	public void start(){
		// 每隔10秒启动，扫描未同步到采集器的策略信息
		Timer instanceTimer = new Timer("CustomMetricDeployTimer");
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, 2);// 系统启动后2分钟
		instanceTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				if(isRun){
					try {
						isRun = false;
						execute();
					} catch (Exception e) {
						if(logger.isErrorEnabled()){
							logger.error("ProfileDispatchTimer error",e);
						}
					}finally{
						isRun = true;
					}
				}
			}
		}, calendar.getTime(), PERIOD);
	}

	private void setService() {
		if (this.changeService == null) {
			this.changeService = SpringBeanUtil
					.getBean(CustomMetricChangeService.class);
		}
		if (this.metricService == null) {
			this.metricService = SpringBeanUtil
					.getBean(CustomMetricService.class);
		}
		if (this.resourceInstanceService == null) {
			this.resourceInstanceService = SpringBeanUtil
					.getBean(ResourceInstanceService.class);
		}
		if (this.profileService == null) {
			this.profileService = SpringBeanUtil.getBean(ProfileService.class);
		}
		if (this.rpcClient == null) {
			this.rpcClient = SpringBeanUtil.getBean(OCRPCClient.class);
		}
		if(this.localeNodeService == null){
			localeNodeService = SpringBeanUtil.getBean(LocaleNodeService.class);
		}
	}

	private boolean executeDeploy(List<CustomMetricChange> changes,
			Map<String, CustomMetric> customMetricMap) {
		boolean hasFailureOne = false;
		List<Long> changeSateIds = new ArrayList<>();
		Set<Long> allInstanceIds = new HashSet<>();
		Set<Long> validChangeIds = new HashSet<>();
		long[] change_Ids = new long[changes.size()];
		for (int i = 0; i < change_Ids.length; i++) {
			change_Ids[i] = changes.get(i).getChangeId();
		}
		try {
			Map<Long, Map<String, Boolean>> changeResultsMap = getChangeResultMap(change_Ids);
			Map<String, CustomMetricMonitorInfoWrapper> metricMonitorsMap = selectAndGroupChanges(
					changeSateIds, allInstanceIds, validChangeIds, changes,
					changeResultsMap, customMetricMap);
			if (metricMonitorsMap != null && metricMonitorsMap.size() > 0) {
				// select instanceProfileMap
				Map<Long, ProfileInfo> profileBasicInfoMap = profileService
						.getBasicInfoByResourceInstanceIds(new ArrayList<>(
								allInstanceIds));
				Map<Long, Long> instanceIdProfileIdMap = null;
				if (profileBasicInfoMap != null
						&& profileBasicInfoMap.size() > 0) {
					instanceIdProfileIdMap = new HashMap<>(
							profileBasicInfoMap.size());
					for (Iterator<Entry<Long, ProfileInfo>> iterator = profileBasicInfoMap
							.entrySet().iterator(); iterator.hasNext();) {
						Entry<Long, ProfileInfo> profileEntry = iterator.next();
						instanceIdProfileIdMap.put(profileEntry.getKey(),
								profileEntry.getValue().getProfileId());
					}
				} else {
					if (logger.isWarnEnabled()) {
						logger.warn("no profile map was found.break the job.InstanceIds="
								+ allInstanceIds);
					}
					return false;
				}
				Set<Long> failChangeIds = deployMonitorInfos(metricMonitorsMap,
						instanceIdProfileIdMap, changeResultsMap);

				/**
				 * 将发布成功的的改变记录的状态置为已经改变成功。
				 */
				validChangeIds.addAll(changeSateIds);
				makesureChangeState(validChangeIds, failChangeIds);
				if (hasFailureOne == false) {
					hasFailureOne = failChangeIds.size() > 0;
				}
			}
			/**
			 * 将不需要发布的改变记录的状态直接置为已经改变成功。
			 */
			makesureChangeState(changeSateIds);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("CustomMetricDeploy execute error.", e);
			}
		}
		return hasFailureOne;
	}

	
	public void execute(){
		if (logger.isTraceEnabled()) {
			logger.trace("CustomMetricDeployTimer execute start");
		}
		if (SpringBeanUtil.isSpringContextReady() == false) {
			if (logger.isWarnEnabled()) {
				logger.warn("execute spring context not ready.wait for next time to execute.");
			}
			return;
		}
		setService();
		// boolean hasFailureOne = false;
		Map<String, CustomMetric> customMetricMap = new HashMap<>();
		// int selectCount = 0;
		// do {
		List<CustomMetricChange> changes = getNotApplyChanges();
		if (changes == null || changes.size() <= 0) {
			if (logger.isTraceEnabled()) {
				logger.trace("CustomMetricDeployJob execute end.no changes found.");
			}
			// break;
			return;
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("CustomMetricDeployJob changes found.size="
						+ changes.size());
			}
		}
		NodeGroup currentNodeGroup = null;
		try {
			currentNodeGroup = localeNodeService.getCurrentNodeGroup();
		} catch (NodeException e1) {
			if (logger.isErrorEnabled()) {
				logger.error("sync to collector data", e1);
			}
		}
		if (currentNodeGroup == null) {
			if (logger.isErrorEnabled()) {
				logger.error("sync to collector data not found currentNode.");
			}
			return;
		}
		List<NodeGroup> collectNodeGroup = currentNodeGroup.getNextGroups();
		if(collectNodeGroup != null){
			allNodeGroupIds.clear();
			for (NodeGroup nodeGroup : collectNodeGroup) {
				String nodeGroupId = String.valueOf(nodeGroup.getId());
				allNodeGroupIds.put(nodeGroupId, nodeGroupId);
			}
		}
		// selectCount = changes.size();
		executeDeploy(changes, customMetricMap);
		// } while (hasFailureOne == false && selectCount == max_changes);
		if (logger.isTraceEnabled()) {
			logger.trace("CustomMetricDeployJob execute end.");
		}
	}

	private Map<Long, Map<String, Boolean>> getChangeResultMap(long[] change_Ids) {
		List<CustomMetricChangeResult> changeResults = changeService
				.getCustomMetricChangeResults(change_Ids);
		Map<Long, Map<String, Boolean>> changeResultsMap = null;
		if (changeResults != null && changeResults.size() > 0) {
			changeResultsMap = new HashMap<>(changeResults.size());
			for (CustomMetricChangeResult customMetricChangeResult : changeResults) {
				Long changeId = customMetricChangeResult.getChangeId();
				Map<String, Boolean> dmsGroupReslultMap;
				if (changeResultsMap.containsKey(changeId)) {
					dmsGroupReslultMap = changeResultsMap.get(changeId);
				} else {
					dmsGroupReslultMap = new HashMap<>();
					changeResultsMap.put(changeId, dmsGroupReslultMap);
				}
				dmsGroupReslultMap.put(String.valueOf(customMetricChangeResult
						.getDcsGroupId()), Boolean
						.valueOf(customMetricChangeResult.isResultState()));
			}
		} else {
			changeResultsMap = new HashMap<>(0);
		}
		return changeResultsMap;
	}

	private List<CustomMetricChange> getNotApplyChanges() {
		List<CustomMetricChange> changes = this.changeService
				.selectChangesWithNotApply(max_changes);
		// TODO compress the data.将相同的操作过滤，将矛盾的操作合并或者过滤。
		return changes;
	}

	private String getDiscoveryNodeGroupId(List<Long> invalidChangeSateIds,
			long instanceId, long changeId) throws InstancelibException {
		String discoveryNodeGroupId = null;
		ResourceInstance instance;
		instance = resourceInstanceService.getResourceInstance(instanceId);
		if (instance == null) {
			invalidChangeSateIds.add(changeId);
			if (logger.isErrorEnabled()) {
				logger.error("selectAndGroupChanges resourceInstance is null.instanceId="
						+ instanceId);
			}
		} else {
			discoveryNodeGroupId = instance.getDiscoverNode();
			if (StringUtils.isEmpty(discoveryNodeGroupId)) {
				invalidChangeSateIds.add(changeId);
				if (logger.isWarnEnabled()) {
					logger.warn("selectAndGroupChanges instance's discoveryNodeGroupId is empty.instanceId="
							+ instanceId);
				}
			}
		}
		return discoveryNodeGroupId;
	}

	private boolean isIgnoreChange(String discoveryNodeGroupId,
			CustomMetricChange customMetricChange,
			Map<Long, Map<String, Boolean>> changeResultsMap,
			List<Long> invalidChangeSateIds) {
		if (changeResultsMap.containsKey(customMetricChange.getChangeId())) {
			Map<String, Boolean> resultMap = changeResultsMap
					.get(customMetricChange.getChangeId());
			if (resultMap.containsKey(discoveryNodeGroupId)
					&& resultMap.get(discoveryNodeGroupId).booleanValue() && allNodeGroupIds.containsKey(discoveryNodeGroupId)) {
				if (invalidChangeSateIds != null) {
					invalidChangeSateIds.add(customMetricChange.getChangeId());
				}
				if (logger.isInfoEnabled()) {
					StringBuilder b = new StringBuilder();
					b.append(
							"selectAndGroupChanges change's result is ok.ignore it.changeId=")
							.append(customMetricChange.getChangeId());
					b.append(" discoveryNodeGroupId=").append(
							discoveryNodeGroupId);
					logger.info(b.toString());
				}
				return true;
			}
		}
		return false;
	}

	private CustomMetric getCustomMetric(String metricId,
			Map<String, CustomMetric> customMetricMap)
			throws CustomMetricException {
		if (customMetricMap.containsKey(metricId)) {
			return customMetricMap.get(metricId);
		} else {
			CustomMetric m = metricService.getCustomMetric(metricId);
			customMetricMap.put(metricId, m);
			return m;
		}
	}

	private ParameterValue[] getParameterValues(CustomMetric customMetric) {
		return getParameterValues(customMetric
				.getCustomMetricCollectParameters());
	}

	private ParameterValue[] getParameterValues(
			List<CustomMetricCollectParameter> parameters) {
		ParameterValue[] values = null;
		if (parameters != null && parameters.size() > 0) {
			values = new ParameterValue[parameters.size()];
			int i = 0;
			for (CustomMetricCollectParameter parameterValue : parameters) {
				values[i] = new ParameterValue();
				values[i].setKey(parameterValue.getParameterKey());
				values[i].setType(parameterValue.getParameterType());
				values[i].setValue(parameterValue.getParameterValue());
				i++;
			}
		}
		return values;
	}

	private CustomMetricMonitorInfoWrapper getWrapper(
			String discoveryNodeGroupId,
			Map<String, CustomMetricMonitorInfoWrapper> metricMonitorsMap) {
		if (metricMonitorsMap.containsKey(discoveryNodeGroupId)) {
			return metricMonitorsMap.get(discoveryNodeGroupId);
		} else {
			CustomMetricMonitorInfoWrapper wrapper = new CustomMetricMonitorInfoWrapper();
			wrapper.info = new ArrayList<>();
			wrapper.changeId = new HashSet<>();
			metricMonitorsMap.put(discoveryNodeGroupId, wrapper);
			return wrapper;
		}
	}

	private Map<String, CustomMetricMonitorInfoWrapper> selectAndGroupChanges(
			List<Long> invalidChangeSateIds, Set<Long> allInstanceIds,
			Set<Long> validChangeIds, List<CustomMetricChange> changes,
			Map<Long, Map<String, Boolean>> changeResultsMap,
			Map<String, CustomMetric> customMetricMap) throws Exception {
		Map<String, CustomMetricMonitorInfoWrapper> metricMonitorsMap = null;
		if (changes != null && changes.size() > 0) {
			metricMonitorsMap = new HashMap<>();
			for (CustomMetricChange customMetricChange : changes) {
				if (logger.isInfoEnabled()) {
					StringBuilder b = new StringBuilder();
					b.append("selectAndGroupChanges changeId=").append(
							customMetricChange.getChangeId());
					b.append(" instanceId=").append(
							customMetricChange.getInstanceId());
					b.append(" metricId=").append(
							customMetricChange.getMetricId());
					b.append(" operateMode=").append(
							customMetricChange.getOperateMode());
					b.append(" pluginId=").append(
							customMetricChange.getPluginId());
					logger.info(b.toString());
				}
				CustomMetricChangeEnum changeEnum = CustomMetricChangeEnum
						.valueOf(customMetricChange.getOperateMode());
				if (changeEnum == CustomMetricChangeEnum.ADD_METRIC_PLUGIN_BIND
						|| changeEnum == CustomMetricChangeEnum.DELETE_METRIC_PLUGIN_BIND) {
					String discoveryNodeGroupId = getDiscoveryNodeGroupId(
							invalidChangeSateIds,
							customMetricChange.getInstanceId(),
							customMetricChange.getChangeId());
					if (discoveryNodeGroupId == null) {
						continue;
					}
					if (isIgnoreChange(discoveryNodeGroupId,
							customMetricChange, changeResultsMap,
							invalidChangeSateIds)) {
						continue;
					}
					validChangeIds.add(customMetricChange.getChangeId());
					CustomMetricMonitorInfoWrapper wrapper = getWrapper(
							discoveryNodeGroupId, metricMonitorsMap);
					if (changeEnum == CustomMetricChangeEnum.ADD_METRIC_PLUGIN_BIND) {
						CustomMetric customMetric = getCustomMetric(
								customMetricChange.getMetricId(),
								customMetricMap);
						if (customMetric == null) {
							if (logger.isErrorEnabled()) {
								logger.error("selectAndGroupChanges CustomMetric not exist.metricId="
										+ customMetricChange.getMetricId());
							}
							invalidChangeSateIds.add(customMetricChange
									.getChangeId());
							continue;
						}
						CustomMetricMonitorInfo info = new CustomMetricMonitorInfo();
						info.setCustomMetricId(customMetricChange.getMetricId());
						info.setCustomMetricProcessWay(customMetric
								.getCustomMetricDataProcess()
								.getDataProcessWay().name());
						info.setFreq(customMetric.getCustomMetricInfo()
								.getFreq().name());
						info.setMonitor(customMetric.getCustomMetricInfo()
								.isMonitor());
						info.setInstanceId(customMetricChange.getInstanceId());
						info.setParameters(getParameterValues(customMetric));
						info.setPluginId(customMetricChange.getPluginId());
						info.setChangeAction(changeEnum.name());
						wrapper.info.add(info);
					} else {
						CustomMetricMonitorInfo info = new CustomMetricMonitorInfo();
						info.setCustomMetricId(customMetricChange.getMetricId());
						info.setInstanceId(customMetricChange.getInstanceId());
						info.setPluginId(customMetricChange.getPluginId());
						info.setChangeAction(changeEnum.name());
						wrapper.info.add(info);
					}
					wrapper.changeId.add(customMetricChange.getChangeId());
					allInstanceIds.add(customMetricChange.getInstanceId());
				} else if (changeEnum == CustomMetricChangeEnum.INSTANCE_CANCEL_MONITOR
						|| changeEnum == CustomMetricChangeEnum.INSTANCE_MONITOR) {
					String discoveryNodeGroupId = getDiscoveryNodeGroupId(
							invalidChangeSateIds,
							customMetricChange.getInstanceId(),
							customMetricChange.getChangeId());
					if (discoveryNodeGroupId == null) {
						continue;
					}
					if (isIgnoreChange(discoveryNodeGroupId,
							customMetricChange, changeResultsMap,
							invalidChangeSateIds)) {
						continue;
					}
					if (changeEnum == CustomMetricChangeEnum.INSTANCE_MONITOR) {
						List<CustomMetric> customMetrics = metricService
								.getCustomMetricsByInstanceId(customMetricChange
										.getInstanceId());
						if (customMetrics == null || customMetrics.size() <= 0) {
							invalidChangeSateIds.add(customMetricChange
									.getChangeId());
							if (logger.isWarnEnabled()) {
								logger.warn("selectAndGroupChanges instance's customMetric is empty.instanceId="
										+ customMetricChange.getInstanceId());
							}
							continue;
						}
						validChangeIds.add(customMetricChange.getChangeId());
						for (CustomMetric customMetric : customMetrics) {
							String metricId = customMetric
									.getCustomMetricInfo().getId();
							List<CustomMetricCollectParameter> parameters = customMetric
									.getCustomMetricCollectParameters();
							if (logger.isInfoEnabled()) {
								logger.info("selectAndGroupChanges add custom metric "
										+ metricId);
							}
							if (parameters != null && parameters.size() > 0) {
								Map<String, List<CustomMetricCollectParameter>> pluginMap = new HashMap<>();
								for (CustomMetricCollectParameter customMetricCollectParameter : parameters) {
									if (pluginMap
											.containsKey(customMetricCollectParameter
													.getPluginId())) {
										pluginMap.get(
												customMetricCollectParameter
														.getPluginId()).add(
												customMetricCollectParameter);
									} else {
										List<CustomMetricCollectParameter> collectParameters = new ArrayList<>();
										collectParameters
												.add(customMetricCollectParameter);
										pluginMap.put(
												customMetricCollectParameter
														.getPluginId(),
												collectParameters);
									}
								}
								for (Iterator<Entry<String, List<CustomMetricCollectParameter>>> iterator = pluginMap
										.entrySet().iterator(); iterator
										.hasNext();) {
									Entry<String, List<CustomMetricCollectParameter>> pEntry = iterator
											.next();
									String pluginId = pEntry.getKey();
									List<CustomMetricCollectParameter> ps = pEntry
											.getValue();
									CustomMetricMonitorInfo info = new CustomMetricMonitorInfo();
									info.setCustomMetricId(metricId);
									info.setCustomMetricProcessWay(customMetric
											.getCustomMetricDataProcess()
											.getDataProcessWay().name());
									info.setFreq(customMetric
											.getCustomMetricInfo().getFreq()
											.name());
									info.setInstanceId(customMetricChange
											.getInstanceId());
									info.setParameters(getParameterValues(ps));
									info.setPluginId(pluginId);
									info.setMonitor(customMetric.getCustomMetricInfo()
											.isMonitor());									
									info.setChangeAction(changeEnum.name());

									CustomMetricMonitorInfoWrapper wrapper = getWrapper(
											discoveryNodeGroupId,
											metricMonitorsMap);
									wrapper.info.add(info);
									wrapper.changeId.add(customMetricChange
											.getChangeId());
									if (logger.isInfoEnabled()) {
										logger.info("selectAndGroupChanges add plugin paraemter. pluginId="
												+ pluginId);
									}
								}
							}
						}
					} else {
						CustomMetricMonitorInfo info = new CustomMetricMonitorInfo();
						info.setInstanceId(customMetricChange.getInstanceId());
						info.setChangeAction(changeEnum.name());
						CustomMetricMonitorInfoWrapper wrapper = getWrapper(
								discoveryNodeGroupId, metricMonitorsMap);
						wrapper.info.add(info);
						wrapper.changeId.add(customMetricChange.getChangeId());
						validChangeIds.add(customMetricChange.getChangeId());
					}
					allInstanceIds.add(customMetricChange.getInstanceId());
				} else if (changeEnum == CustomMetricChangeEnum.METRIC_BASIC_UPDATE
						|| changeEnum == CustomMetricChangeEnum.CHANGE_METRIC_PLUGIN_COLLECT) {
					String metricId = customMetricChange.getMetricId();
					List<CustomMetricBind> binds = metricService
							.getCustomMetricBindsByMetricId(metricId);
					if (binds == null || binds.size() <= 0) {
						if (logger.isWarnEnabled()) {
							logger.warn("execute custommetric has no binds.MetricId="
									+ customMetricChange.getMetricId());
						}
						invalidChangeSateIds.add(customMetricChange
								.getChangeId());
						continue;
					}
					boolean isCollect = false;
					if (customMetricChange.getOperateMode().equals(
							CustomMetricChangeEnum.CHANGE_METRIC_PLUGIN_COLLECT
									.name())) {
						isCollect = true;
					}
					boolean atLeastOneChange = false;
					ParameterValue[] values = null;
					Map<String, Boolean> nodeGroupMap = new HashMap<>();
					for (CustomMetricBind customMetricBind : binds) {
						long instanceId = customMetricBind.getInstanceId();
						String discoveryNodeGroupId = getDiscoveryNodeGroupId(
								invalidChangeSateIds, instanceId,
								customMetricChange.getChangeId());
						if (discoveryNodeGroupId == null) {
							continue;
						}
						if (nodeGroupMap.containsKey(discoveryNodeGroupId)) {
							continue;
						}
						if (isIgnoreChange(discoveryNodeGroupId,
								customMetricChange, changeResultsMap, null)) {
							nodeGroupMap.put(discoveryNodeGroupId,
									Boolean.FALSE);
							continue;
						}
						CustomMetricMonitorInfo info = new CustomMetricMonitorInfo();
						CustomMetric customMetric = getCustomMetric(metricId,
								customMetricMap);
						info.setCustomMetricId(metricId);
						info.setChangeAction(changeEnum.name());
						if (isCollect) {
							if (values == null) {
								values = getParameterValues(customMetric);
							}
							info.setParameters(values);
							info.setPluginId(customMetricBind.getPluginId());
						} else {
							info.setFreq(customMetric.getCustomMetricInfo()
									.getFreq().name());
							info.setMonitor(customMetric.getCustomMetricInfo()
									.isMonitor());
							info.setCustomMetricProcessWay(customMetric
									.getCustomMetricDataProcess()
									.getDataProcessWay().name());
						}
						CustomMetricMonitorInfoWrapper wrapper = getWrapper(
								discoveryNodeGroupId, metricMonitorsMap);
						wrapper.info.add(info);
						wrapper.changeId.add(customMetricChange.getChangeId());
						allInstanceIds.add(customMetricBind.getInstanceId());
						atLeastOneChange = true;
						nodeGroupMap.put(discoveryNodeGroupId, Boolean.TRUE);
					}
					if (atLeastOneChange) {
						validChangeIds.add(customMetricChange.getChangeId());
					} else {
						invalidChangeSateIds.add(customMetricChange
								.getChangeId());
					}
				}
			}
		}
		return metricMonitorsMap;
	}

	private Set<Long> deployMonitorInfos(
			Map<String, CustomMetricMonitorInfoWrapper> metricMonitorsMap,
			Map<Long, Long> instanceIdProfileIdMap,
			Map<Long, Map<String, Boolean>> changeResultsMap) throws Exception {
		Set<Long> allFailChanges = new HashSet<>(this.max_changes);
		for (Iterator<Entry<String, CustomMetricMonitorInfoWrapper>> iterator = metricMonitorsMap
				.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, CustomMetricMonitorInfoWrapper> nodeGroupEntry = iterator
					.next();
			String nodeGroupId = nodeGroupEntry.getKey();
			CustomMetricMonitorInfoWrapper wrapper = nodeGroupEntry.getValue();
			Set<Long> failchangeIds = new HashSet<>();
			boolean changeResultFlag = deploy(instanceIdProfileIdMap, wrapper,
					nodeGroupId, failchangeIds);
			allFailChanges.addAll(failchangeIds);
			saveChangeResultState(nodeGroupId, wrapper.changeId,
					changeResultFlag, changeResultsMap);
		}
		return allFailChanges;
	}

	private boolean deploy(Map<Long, Long> instanceIdProfileIdMap,
			CustomMetricMonitorInfoWrapper wrapper, String nodeGroupId,
			Set<Long> allFailChanges) throws Exception {
		if (logger.isTraceEnabled()) {
			logger.trace("deploy start");
		}
		boolean changeResultFlag = false;
		CustomMetricCollectDeployMBean customMetricCollectDeployMBean = rpcClient
				.getRemoteSerivce(Integer.parseInt(nodeGroupId),
						CustomMetricCollectDeployMBean.class);
		if (wrapper.info != null && wrapper.info.size() > 0) {
			if (logger.isInfoEnabled()) {
				logger.info("CustomMetricCollectDeploy info.size="
						+ wrapper.info.size());
			}
			try {
				customMetricCollectDeployMBean
						.deployCustomMetricMonitors(
								wrapper.info
										.toArray(new CustomMetricMonitorInfo[wrapper.info
												.size()]),
								instanceIdProfileIdMap);
				changeResultFlag = true;
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("deploy addCustomMetricMonitors", e);
				}
				allFailChanges.addAll(wrapper.changeId);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("deploy end");
		}
		return changeResultFlag;
	}

	private void makesureChangeState(List<Long> changeIds) {
		if (logger.isInfoEnabled()) {
			logger.info("makesureChangeState start changeIds=" + changeIds);
		}
		if (changeIds != null && changeIds.size() > 0) {
			long[] toChangeIds = new long[changeIds.size()];
			for (int i = 0; i < toChangeIds.length; i++) {
				toChangeIds[i] = changeIds.get(i);
			}
			changeService.updateCustomMetricChangeToApply(toChangeIds);
		}
		if (logger.isTraceEnabled()) {
			logger.trace("makesureChangeState end");
		}
	}

	private void makesureChangeState(Set<Long> allChangeIds,
			Set<Long> failChangeIds) {
		if (logger.isTraceEnabled()) {
			logger.trace("makesureChangeState start");
		}
		if (allChangeIds != null && allChangeIds.size() > 0) {
			long[] toChangeIds = new long[allChangeIds.size()
					- (failChangeIds == null ? 0 : failChangeIds.size())];
			Map<Long, Object> failChangeIdsMap = null;
			if (failChangeIds != null && failChangeIds.size() > 0) {
				failChangeIdsMap = new HashMap<>();
				for (Long changeId : failChangeIds) {
					failChangeIdsMap.put(changeId, changeId);
				}
			}
			int index = 0;
			for (Long changeId : allChangeIds) {
				if (failChangeIdsMap != null
						&& failChangeIdsMap.containsKey(changeId)) {
					continue;
				}
				toChangeIds[index] = changeId;
				index++;
			}
			if (logger.isInfoEnabled()) {
				logger.info("makesureChangeState toChangeIds="
						+ Arrays.toString(toChangeIds));
			}
			changeService.updateCustomMetricChangeToApply(toChangeIds);
		}
		if (logger.isTraceEnabled()) {
			logger.trace("makesureChangeState end");
		}
	}

	private void saveChangeResultState(String nodeGroupId, Set<Long> changeIds,
			boolean changeResultFlag,
			Map<Long, Map<String, Boolean>> changeResultsMap) {
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder();
			b.append("saveChangeResultState start nodeGroupId=")
					.append(nodeGroupId).append(" changeIds=")
					.append(changeIds).append(" changeResult=")
					.append(changeResultFlag);
			logger.info(b.toString());
		}
		List<CustomMetricChangeResult> insertChangeResults = new ArrayList<>(
				changeIds.size());
		List<CustomMetricChangeResult> updateChangeResults = new ArrayList<>();
		int ndoeGroupIdValue = Integer.parseInt(nodeGroupId);
		Date dateTime = new Date();
		for (Long changeId : changeIds) {
			CustomMetricChangeResult changeResult = new CustomMetricChangeResult();
			changeResult.setChangeId(changeId);
			changeResult.setDcsGroupId(ndoeGroupIdValue);
			changeResult.setOperateTime(dateTime);
			changeResult.setResultState(changeResultFlag);
			if (changeResultsMap.containsKey(changeId)
					&& changeResultsMap.get(changeId).containsKey(nodeGroupId)) {
				if (changeResult.isResultState()) {
					updateChangeResults.add(changeResult);
				}
			} else {
				insertChangeResults.add(changeResult);
			}
		}
		if (insertChangeResults.size() > 0) {
			changeService.insertCustomMetricChangeResults(insertChangeResults);
		}
		if (updateChangeResults.size() > 0) {
			changeService.updateCustomMetricChangeResults(updateChangeResults);
		}
		if (logger.isTraceEnabled()) {
			logger.trace("saveChangeResultState end");
		}
	}

	private class CustomMetricMonitorInfoWrapper {
		List<CustomMetricMonitorInfo> info;
		private Set<Long> changeId;
	}
}
