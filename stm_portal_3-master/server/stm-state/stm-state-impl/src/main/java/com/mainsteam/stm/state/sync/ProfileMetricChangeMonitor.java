package com.mainsteam.stm.state.sync;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mainsteam.stm.state.engine.StateEngine;
import com.mainsteam.stm.state.obj.InstanceStateChangeData;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.alarm.event.AlarmEventService;
import com.mainsteam.stm.alarm.notify.AlarmNotifyService;
import com.mainsteam.stm.alarm.obj.HandleType;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.common.metric.sync.MetricProfileChange;
import com.mainsteam.stm.common.sync.DataSyncPO;
import com.mainsteam.stm.common.sync.DataSyncTypeEnum;
import com.mainsteam.stm.common.sync.dao.DataSyncDAO;
import com.mainsteam.stm.dataprocess.InstanceSyncUtils;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.dataprocess.engine.MetricDataProcessor;
import com.mainsteam.stm.exception.BaseException;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.lock.LockCallback;
import com.mainsteam.stm.lock.LockService;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.obj.ProfileInfo;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;
import com.mainsteam.stm.state.obj.InstanceStateData;
import com.mainsteam.stm.state.obj.MetricStateData;
import com.mainsteam.stm.state.util.InstanceStateComputeUtil;
import com.mainsteam.stm.state.util.StateCaculatUtils;

public class ProfileMetricChangeMonitor implements InitializingBean {
	private Log logger = LogFactory.getLog(ProfileMetricChangeMonitor.class);

	private LockService lockService;
	private DataSyncDAO dataSyncDAO;
	private CapacityService capacityService;
	private ProfileService profileService;
	private StateCaculatUtils stateCaculatUtils;
	private InstanceStateComputeUtil instanceStateComputeUtil;
	private ResourceInstanceService resourceInstanceService;
	private MetricDataProcessor availableMetricProcess;
	private MetricDataProcessor performanceMetricProcess;
	private MetricDataService metricDataService;
	private AlarmNotifyService alarmNotifyService;
	private AlarmEventService alarmEventService;
	private StateEngine stateEngine;

	public void setInstanceStateComputeUtil(
			InstanceStateComputeUtil instanceStateComputeUtil) {
		this.instanceStateComputeUtil = instanceStateComputeUtil;
	}
	public void setLockService(LockService lockService) {
		this.lockService = lockService;
	}

	public void setAvailableMetricProcess(
			MetricDataProcessor availableMetricProcess) {
		this.availableMetricProcess = availableMetricProcess;
	}

	public void setPerformanceMetricProcess(
			MetricDataProcessor performanceMetricProcess) {
		this.performanceMetricProcess = performanceMetricProcess;
	}

	public void setResourceInstanceService(
			ResourceInstanceService resourceInstanceService) {
		this.resourceInstanceService = resourceInstanceService;
	}

	public void setAlarmNotifyService(AlarmNotifyService alarmNotifyService) {
		this.alarmNotifyService = alarmNotifyService;
	}

	public void setAlarmEventService(AlarmEventService alarmEventService) {
		this.alarmEventService = alarmEventService;
	}

	public void setStateCaculatUtils(StateCaculatUtils stateCaculatUtils) {
		this.stateCaculatUtils = stateCaculatUtils;
	}

	public void setDataSyncDAO(DataSyncDAO dataSyncDAO) {
		this.dataSyncDAO = dataSyncDAO;
	}

	public void setCapacityService(CapacityService capacityService) {
		this.capacityService = capacityService;
	}

	public void setMetricDataService(MetricDataService metricDataService) {
		this.metricDataService = metricDataService;
	}

	public void setStateEngine(StateEngine stateEngine) {
		this.stateEngine = stateEngine;
	}

	public void notify(MetricProfileChange parameter) throws BaseException {
		/**
		 * 没有考虑基线，自定义定义指标
		 */
		logger.info("ProfileMetricChange start:" + JSON.toJSONString(parameter));
		try {

			ProfileInfo pf = profileService.getProfileBasicInfoByProfileId(parameter.getProfileID());
			if (pf == null) {
				logger.error("Profile[" + parameter.getProfileID() + ","
						+ parameter.getMetricID() + "] can't find.exit!");
				return;
			}
			ResourceMetricDef resourceMetricDef = capacityService.getResourceMetricDef(pf.getResourceId(), parameter.getMetricID());
			if (resourceMetricDef == null) {
				logger.error("can't find ResourceMetricDef[" + pf.getResourceId() + "," + parameter.getMetricID() + "],please check!");
				return;
			}

			List<Long> ides = profileService.getResourceInstanceByProfileId(parameter.getProfileID());
			if (ides == null || ides.isEmpty()) {
				logger.error("can't find instance ides from profile[" + parameter.getProfileID() + "],please check!");
				return;
			}
			logger.info("find ides:" + JSON.toJSONString(ides));
			if (Boolean.TRUE.equals(parameter.getIsAlarm())) {

				logger.info("Profile[" + parameter.getProfileID() + "," + parameter.getMetricID() + "] start to alarm!");

				if (resourceMetricDef.getMetricType() == MetricTypeEnum.PerformanceMetric) {
					for (long id : ides) {
						MetricData mdata = metricDataService.getMetricPerformanceData(id, parameter.getMetricID());
						if (mdata == null) {
							logger.warn("can't find metricData for[" + id + "," + parameter.getMetricID() + "]");
							continue;
						}
						performanceMetricProcess.process(convertMetricData(mdata, pf.getResourceId()), resourceMetricDef, null, null);
					}
				} else if (resourceMetricDef.getMetricType() == MetricTypeEnum.AvailabilityMetric) {
					for (long id : ides) {
						MetricData metricData = metricDataService.getMetricAvailableData(id, parameter.getMetricID());
						if (metricData == null) {
							logger.warn("can't find metricData for[" + id + "," + parameter.getMetricID() + "]");
							continue;
						}
						availableMetricProcess.process(convertMetricData(metricData, pf.getResourceId()), resourceMetricDef, null, null);
					}
				} else {
					logger.warn("MetricType[" + resourceMetricDef.getMetricType()
							+ "] can't calculate. data:" + JSON.toJSONString(parameter));
				}
			} else if (Boolean.FALSE.equals(parameter.getIsMonitor()) || Boolean.FALSE.equals(parameter.getIsAlarm())) {

				if (logger.isInfoEnabled())
					logger.info("Profile[" + parameter.getProfileID() + "," + parameter.getMetricID() + "] cancel alarm!");

				ProfileMetric profileMetric = profileService.getMetricByProfileIdAndMetricId(
								parameter.getProfileID(),
								parameter.getMetricID());
				if (profileMetric == null || !profileMetric.isMonitor()) {
					logger.error("Profile[" + parameter.getProfileID() + "," + parameter.getMetricID() + "] has not to monitor,exit!");
					return;
				}

				for (long id : ides) {
					MetricStateData metricStateData = stateCaculatUtils.getMetricState(id, parameter.getMetricID());
					if (metricStateData == null) {
						logger.warn("can't find MetricState for[" + id + "," + parameter.getMetricID() + "]");
						continue;
					}
					String recoveryKey = StringUtils.join(new String[] {
									String.valueOf(metricStateData.getInstanceID()),
									metricStateData.getMetricID() }, "_");
					if (resourceMetricDef.getMetricType() == MetricTypeEnum.AvailabilityMetric)
						recoveryKey = StringUtils.join(new String[] {
										"instanceState_",
										String.valueOf(metricStateData.getInstanceID()) }, "_");
					alarmEventService.recoverAlarmEventByRecoverKey(recoveryKey, HandleType.DELETE);
					// clean alarm notify wait
					if (logger.isInfoEnabled()) {
						logger.info("Clean Alarm Notify Wait with the recoveryKey : " + recoveryKey);
					}
					alarmNotifyService.deleteAlarmNotifyWaitByRecoveryKey(recoveryKey);

					MetricStateEnum newState = MetricStateEnum.NORMAL;
					if (newState == metricStateData.getState()) {
						logger.warn("MetricState for[" + id + "," + parameter.getMetricID()
								+ "] not change[oldState:" + metricStateData.getState() + "],exit!");
						continue;
					}
					metricStateData.setState(newState);
					metricStateData.setCollectTime(new Date());
					this.stateCaculatUtils.saveMetricState(metricStateData);
					ResourceInstance resourceInstance = resourceInstanceService.getResourceInstance(id);
					if (resourceInstance == null) {
						logger.warn("ResourceInstance [" + id + " not found,please check!]");
						continue;
					}

					synchronized (InstanceSyncUtils.getSyncObj(resourceInstance.getId())) {
						MetricCalculateData metricCalculateData = new MetricCalculateData(null, parameter.getMetricID(),
								resourceInstance.getId(),new Date(), resourceMetricDef.getResourceDef().getId());
						metricCalculateData.setProfileId(profileMetric.getProfileId());
						metricCalculateData.setTimelineId(profileMetric.getTimeLineId());

						this.instanceStateComputeUtil.changeInstanceState(resourceInstance, parameter.getMetricID(),
								resourceMetricDef,metricCalculateData);
					}
				}
			}
		} catch (Exception e) {
			logger.error("[ProfileID:" + parameter.getProfileID() + ",metric:"
					+ parameter.getMetricID() + "]exception:" + e.getMessage(), e);
		}
	}

	private MetricCalculateData convertMetricData(MetricData oldMd,
			String resourceID) {
		if (oldMd == null)
			return null;
		MetricCalculateData metricCalculateData = new MetricCalculateData();
		metricCalculateData.setCollectTime(oldMd.getCollectTime());
		metricCalculateData.setMetricData(oldMd.getData());
		metricCalculateData.setResourceId(resourceID);
		metricCalculateData.setResourceInstanceId(oldMd.getResourceInstanceId());
		metricCalculateData.setMetricId(oldMd.getMetricId());
		if (oldMd.getTimelineId() != null) {
			metricCalculateData.setTimelineId(oldMd.getTimelineId());
		}
		if (oldMd.getProfileId() != null) {
			metricCalculateData.setProfileId(oldMd.getProfileId());
		}
		return metricCalculateData;
	}

	public void setProfileService(ProfileService profileService) {
		this.profileService = profileService;
	}

	@Override
	public void afterPropertiesSet() throws Exception {

		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						List<DataSyncPO> list = lockService.sync(
								"ProfileMetricChangeMonitor",
								new LockCallback<List<DataSyncPO>>() {
									@Override
									public List<DataSyncPO> doAction() {

										int start = 0;
										int limit = 5000;

										List<DataSyncPO> list = dataSyncDAO.catchList(
														DataSyncTypeEnum.METRIC_STATE, start, limit);

										if (list != null && list.size() > 0) {

											List<Long> ides = new ArrayList<>();
											for (DataSyncPO po : list) {
												ides.add(po.getId());
											}
											dataSyncDAO.updateForRunning(ides);
										}
										return list;
									}
								}, 2);

						if (list != null && list.size() > 0) {
							for (DataSyncPO po : list) {
								try {
									if (logger.isDebugEnabled()) {
										logger.debug("catch metricChange message:" + po.getData());
									}
									MetricProfileChange metricProfileChange = JSON.parseObject(po.getData(), MetricProfileChange.class);
									ProfileMetricChangeMonitor.this.notify(metricProfileChange);
									dataSyncDAO.delete(po.getId());
								} catch (Exception e) {
									logger.error(e.getMessage(), e);

									po.setState(0);
									po.setUpdateTime(new Date());
									dataSyncDAO.update(po);
								}
							}
						}
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}

					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						logger.error(e.getMessage(), e);
					}
				}
			}
		});
		th.setName("ProfileMetricChangeMonitor");
		th.setDaemon(false);
		th.start();
		if (logger.isInfoEnabled())
			logger.info("ProfileMetricChange Monitor has start!");
	}

}
