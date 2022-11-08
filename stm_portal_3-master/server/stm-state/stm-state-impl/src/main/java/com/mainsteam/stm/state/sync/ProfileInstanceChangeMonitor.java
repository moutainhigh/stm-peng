package com.mainsteam.stm.state.sync;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.alarm.event.AlarmEventService;
import com.mainsteam.stm.alarm.notify.AlarmNotifyService;
import com.mainsteam.stm.alarm.obj.HandleType;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.common.metric.sync.InstanceProfileChange;
import com.mainsteam.stm.common.sync.DataSyncPO;
import com.mainsteam.stm.common.sync.DataSyncTypeEnum;
import com.mainsteam.stm.common.sync.dao.DataSyncDAO;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.exception.BaseException;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.lock.LockCallback;
import com.mainsteam.stm.lock.LockService;
import com.mainsteam.stm.state.engine.StateEngine;
import com.mainsteam.stm.state.obj.InstanceStateChangeData;
import com.mainsteam.stm.state.obj.InstanceStateData;
import com.mainsteam.stm.state.util.InstanceStateComputeUtil;
import com.mainsteam.stm.state.util.StateCaculatUtils;

public class ProfileInstanceChangeMonitor implements InitializingBean {
	private Log logger = LogFactory.getLog(ProfileInstanceChangeMonitor.class);

	private LockService lockService;
	private DataSyncDAO dataSyncDAO;
	private ResourceInstanceService resourceInstanceService;
	private AlarmEventService alarmEventService;
	private AlarmNotifyService alarmNotifyService;
	private StateCaculatUtils stateCaculatUtils;
	private InstanceStateComputeUtil instanceStateComputeUtil;
	private StateEngine stateEngine;
	private CapacityService capacityService;
	
	

	public void setInstanceStateComputeUtil(
			InstanceStateComputeUtil instanceStateComputeUtil) {
		this.instanceStateComputeUtil = instanceStateComputeUtil;
	}

	public void notify(InstanceProfileChange parameter) throws BaseException {
		/**
		 * 没有考虑基线，自定义定义指标
		 */
		if (logger.isInfoEnabled())
			logger.info("ProfileInstanceChange start:" + JSON.toJSONString(parameter));
		try {
			ResourceInstance resourceInstance = resourceInstanceService.getResourceInstanceWithDeleted(parameter.getInstanceID());

			// 删除资源实例或者资源实例取消监控
			if (InstanceLifeStateEnum.DELETED == parameter.getLifeState()
					|| InstanceLifeStateEnum.NOT_MONITORED == parameter.getLifeState()) {
				List<Long> needDeleteInstanceIDs = new ArrayList<Long>();// 需要恢复资源状态的实例ID
				if (InstanceLifeStateEnum.DELETED == parameter.getLifeState())
					needDeleteInstanceIDs.add(parameter.getInstanceID());
				// 当策略变化时需要删除告警等待表的中相关数据
				if (logger.isInfoEnabled()) {
					logger.info("Clean AlarmNotifyWait and AlarmEvent data by instance ID [" + parameter.getInstanceID() + "].");
				}
				alarmNotifyService.deleteAlarmNotifyWaitByRecoveryKey(String.valueOf(parameter.getInstanceID()));

				if (InstanceLifeStateEnum.DELETED == parameter.getLifeState())
					alarmEventService.deleteAlarmEventByInstanceId(String.valueOf(parameter.getInstanceID()));
				else {
					alarmEventService.recoverAlarmEventBySourceID(
							String.valueOf(parameter.getInstanceID()), HandleType.DELETE);
				}

				List<ResourceInstance> childrenResourceInstances = resourceInstanceService
						.getChildInstanceByParentId(parameter.getInstanceID(), true);
				if (childrenResourceInstances != null
						&& !childrenResourceInstances.isEmpty()) { // 当前为主资源
					for (ResourceInstance childResourceInstance : childrenResourceInstances) {
						if (childResourceInstance.getLifeState() == InstanceLifeStateEnum.DELETED
								|| childResourceInstance.getLifeState() == InstanceLifeStateEnum.NOT_MONITORED) {
							if (logger.isInfoEnabled()) {
								logger.info("Clean Alarm Notify Wait with child instance ID : " + childResourceInstance.getId());
							}
							alarmNotifyService.deleteAlarmNotifyWaitByRecoveryKey(String.valueOf(childResourceInstance.getId()));
							if (childResourceInstance.getLifeState() == InstanceLifeStateEnum.DELETED) {
								needDeleteInstanceIDs.add(childResourceInstance.getId());
								alarmEventService.deleteAlarmEventByInstanceId(String.valueOf(childResourceInstance.getId()));
							} else
								alarmEventService.recoverAlarmEventBySourceID(String.valueOf(childResourceInstance.getId()), HandleType.DELETE);

						}

					}

				} else { // 当前资源为子资源,需要考虑主资源的状态，因为可能主资源的状态恰好是由这个子资源状态改变引起的。
					if (null == resourceInstance) {// 物理删除后
						if (logger.isWarnEnabled()) {
							logger.warn("Cant' get resource instance by id ["
									+ parameter.getInstanceID()
									+ "] in profile instance changing module.");
						}
						return;
					}
					ResourceInstance parentInstance = resourceInstanceService.getResourceInstance(resourceInstance.getParentId());
					if (parentInstance == null) { // 错误处理
						if (logger.isWarnEnabled())
							logger.warn("ResourceInstance ["
									+ resourceInstance.getParentId()
									+ " not found,please check!]");
					} else {
						InstanceStateData preMainInstanceData = this.stateCaculatUtils.getInstanceState(parentInstance.getId());

						InstanceStateData mainInstanceData = this.instanceStateComputeUtil.calculateMainInstanceState(parentInstance, 0L, true);
						if (preMainInstanceData != null
								&& mainInstanceData != null
								&& mainInstanceData.compareTo(preMainInstanceData) != 0) {
							if (logger.isInfoEnabled()) {
								logger.info("Save instance state " + mainInstanceData);
							}
							stateCaculatUtils.saveInstanceState(mainInstanceData);

							// 用于通知其他业务模块状态变化，但不告警
							MetricCalculateData metricData = new MetricCalculateData();
							metricData.setResourceInstanceId(mainInstanceData.getInstanceID());
							metricData.setResourceId(parentInstance.getResourceId());
							InstanceStateChangeData instanceStateChangeData = stateCaculatUtils.generateNotifyInstanceState(
									mainInstanceData, null, false, 1, metricData, null, null);

							stateEngine.handleInstanceStateChange(instanceStateChangeData);
						} else
							logger.warn("calculate parent ins state is null with instance id " + parameter.getInstanceID());

					}
				}

				if (InstanceLifeStateEnum.DELETED == parameter.getLifeState()) {// 如果删除资源，则需要将相关的资源状态设置为正常
					if (logger.isInfoEnabled())
						logger.info("Clean all instanceState with id " + JSON.toJSONString(needDeleteInstanceIDs));

					stateCaculatUtils.cleanInstanceStates(needDeleteInstanceIDs);
					stateCaculatUtils.cleanMetricStates(needDeleteInstanceIDs);
				}

			} else { // 加入监控
				if (resourceInstance == null) { // 物理删除
					if (logger.isWarnEnabled()) {
						logger.warn("Cant' get resource instance by id [" + parameter.getInstanceID()
								+ "] in profile instance changing module.");
					}
					return;
				}
				ResourceInstance parentInstance = resourceInstance.getParentInstance();
				if (parentInstance == null) {
					if (logger.isInfoEnabled())
						logger.info("current instance is main resource instance with id [" + resourceInstance.getId() + "]");

				} else {
					InstanceStateData preMainInstanceData = this.stateCaculatUtils.getInstanceState(parentInstance.getId());
					InstanceStateData mainInstanceData = this.instanceStateComputeUtil.calculateMainInstanceState(parentInstance, 0L, true);
					if (preMainInstanceData != null && mainInstanceData != null
							&& mainInstanceData.compareTo(preMainInstanceData) != 0) {
						if (logger.isInfoEnabled()) {
							logger.info("Save instance state " + mainInstanceData);
						}
						stateCaculatUtils.saveInstanceState(mainInstanceData);
						// 用于通知其他业务模块状态变化，但不告警
						MetricCalculateData metricData = new MetricCalculateData();
						metricData.setResourceInstanceId(mainInstanceData.getInstanceID());
						InstanceStateChangeData instanceStateChangeData = stateCaculatUtils.generateNotifyInstanceState(mainInstanceData,
										null, false, 1, metricData, null, null);

						stateEngine.handleInstanceStateChange(instanceStateChangeData);

					} else
						logger.warn("calculate parent ins state is null with instance id "
								+ parameter.getInstanceID());

				}
				// 子资源加入监控后，需要把原来已经置为“delete”的告警记录重新更新为“none”或者“auto”
				if (logger.isInfoEnabled()) {
					logger.info("Recovery alarm event by instance id [" + parameter.getInstanceID() + "].");
				}
				alarmEventService.recoveryDeletedAlarmEventBySourceID(String.valueOf(parameter.getInstanceID()));
			}

		} catch (Exception e) {
			logger.error("found error for [" + JSON.toJSONString(parameter) + "]:" + e.getMessage(), e);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {

		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						List<DataSyncPO> list = lockService.sync(
								"ProfileInstanceChangeMonitor",
								new LockCallback<List<DataSyncPO>>() {
									@Override
									public List<DataSyncPO> doAction() {
										int start = 0;
										int limit = 5000;
										List<DataSyncPO> list = dataSyncDAO.catchList(
														DataSyncTypeEnum.INSTANCE_STATE, start, limit);
										if (list != null && !list.isEmpty()) {
											List<Long> ides = new ArrayList<>(list.size());
											for (DataSyncPO po : list) {
												ides.add(po.getId());
											}
											if (!ides.isEmpty())
												dataSyncDAO.updateForRunning(ides);
										}
										return list;
									}
								}, 2);

						if (list != null && list.size() > 0) {
							for (DataSyncPO po : list) {
								if (logger.isDebugEnabled()) {
									logger.debug("catch instanceChange message:" + po.getData());
								}
								try {
									InstanceProfileChange parameter = JSON.parseObject(po.getData(), InstanceProfileChange.class);
									ProfileInstanceChangeMonitor.this.notify(parameter);
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
		th.setName("ProfileInstanceChangeMonitor");
		th.start();
		if (logger.isInfoEnabled())
			logger.info("ProfileMetricChange Monitor has start!");
	}

	public void setLockService(LockService lockService) {
		this.lockService = lockService;
	}

	public void setDataSyncDAO(DataSyncDAO dataSyncDAO) {
		this.dataSyncDAO = dataSyncDAO;
	}

	public void setResourceInstanceService(
			ResourceInstanceService resourceInstanceService) {
		this.resourceInstanceService = resourceInstanceService;
	}

	public void setAlarmEventService(AlarmEventService alarmEventService) {
		this.alarmEventService = alarmEventService;
	}

	public void setAlarmNotifyService(AlarmNotifyService alarmNotifyService) {
		this.alarmNotifyService = alarmNotifyService;
	}

	public void setStateCaculatUtils(StateCaculatUtils stateCaculatUtils) {
		this.stateCaculatUtils = stateCaculatUtils;
	}

	public void setStateEngine(StateEngine stateEngine) {
		this.stateEngine = stateEngine;
	}

	public void setCapacityService(CapacityService capacityService) {
		this.capacityService = capacityService;
	}
}
