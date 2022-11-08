/**
 * 
 */
package com.mainsteam.stm.metric.service.impl.interceptor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.interceptor.InstancelibEvent;
import com.mainsteam.stm.instancelib.interceptor.InstancelibListener;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.EventEnum;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.metric.dao.CustomMetricBindDAO;
import com.mainsteam.stm.metric.dao.CustomMetricChangeDAO;
import com.mainsteam.stm.metric.dao.pojo.CustomMetricBindDO;
import com.mainsteam.stm.metric.dao.pojo.CustomMetricChangeDO;
import com.mainsteam.stm.metric.objenum.CustomMetricChangeEnum;
import com.mainsteam.stm.platform.sequence.service.ISequence;

/**
 * 监听资源实例状态改变
 * 
 * @author ziw
 * 
 */
public class InstancelibInterceptorStateChangeImpl implements
		InstancelibListener {

	private static final Log logger = LogFactory
			.getLog(InstancelibInterceptorStateChangeImpl.class);

	private CustomMetricBindDAO bindDAO;

	private CustomMetricChangeDAO changeDAO;

	private ResourceInstanceService instanceService;

	private ISequence changeSeq;

	/**
	 * @param instanceService
	 *            the instanceService to set
	 */
	public final void setInstanceService(ResourceInstanceService instanceService) {
		this.instanceService = instanceService;
	}

	/**
	 * @param changeDAO
	 *            the changeDAO to set
	 */
	public final void setChangeDAO(CustomMetricChangeDAO changeDAO) {
		this.changeDAO = changeDAO;
	}

	/**
	 * @param bindDAO
	 *            the bindDAO to set
	 */
	public final void setBindDAO(CustomMetricBindDAO bindDAO) {
		this.bindDAO = bindDAO;
	}

	/**
	 * @param changeSeq
	 *            the changeSeq to set
	 */
	public final void setChangeSeq(ISequence changeSeq) {
		this.changeSeq = changeSeq;
	}

	/**
	 * 
	 */
	public InstancelibInterceptorStateChangeImpl() {
	}


	@Override
	public void listen(InstancelibEvent event) throws Exception {
		if (event.getEventType() == EventEnum.INSTANCE_UPDATE_STATE_EVENT) {
			if (logger.isTraceEnabled()) {
				logger.trace("listen start");
			}
			@SuppressWarnings("unchecked")
			List<ResourceInstance> oldResourceInstances = (List<ResourceInstance>) event
					.getSource();
			List<Long> queryInstanceIds = new ArrayList<>();
			for (ResourceInstance resourceInstance : oldResourceInstances) {
				if (resourceInstance.getParentId() > 0) {
					continue;
				}
				if ("Layer2Link".equals(resourceInstance.getResourceId())) {
					continue;
				}
				queryInstanceIds.add(resourceInstance.getId());
			}
			if (queryInstanceIds.size() > 0) {
				if (logger.isDebugEnabled()) {
					logger.debug("listen queryInstanceIds=" + queryInstanceIds);
				}
				List<CustomMetricBindDO> bindDOs = bindDAO
						.getCustomMetricBindByInstanceIds(queryInstanceIds);
				if (bindDOs != null && bindDOs.size() > 0) {
					Set<Long> changeInstanceIds = new HashSet<>();
					for (CustomMetricBindDO customMetricBindDO : bindDOs) {
						changeInstanceIds.add(customMetricBindDO
								.getInstanceId());
					}
					List<CustomMetricChangeDO> metricChangeDOs = new ArrayList<>();
					Date changeDate = new Date();
					for (Long instId : changeInstanceIds) {
						ResourceInstance instance = instanceService
								.getResourceInstance(instId.longValue());
						if (instance != null
								&& instance.getLifeState() == InstanceLifeStateEnum.MONITORED) {
							CustomMetricChangeDO changeDO = new CustomMetricChangeDO();
							changeDO.setChange_id(changeSeq.next());
							changeDO.setChange_time(changeDate);
							changeDO.setOccur_time(changeDate);
							changeDO.setOperate_state(0);
							changeDO.setOperateMode(CustomMetricChangeEnum.INSTANCE_MONITOR
									.name());
							changeDO.setInstance_id(instId.longValue());
							metricChangeDOs.add(changeDO);
							if (logger.isInfoEnabled()) {
								StringBuilder b = new StringBuilder(
										"record change for customMetric.");
								b.append("instanceId=").append(instId);
								b.append(" lifeState=").append(
										instance.getLifeState());
								logger.info(b.toString());
							}
						} else {
							if (logger.isWarnEnabled()) {
								StringBuilder b = new StringBuilder(
										"record change for customMetric.instance not exist.");
								b.append("instanceId=").append(instId);
								logger.info(b.toString());
							}
						}
					}
					addCustomMetricChanges(metricChangeDOs);
				}
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("listen on intance need to check custom metric.");
				}
			}
			if (logger.isTraceEnabled()) {
				logger.trace("listen end");
			}
		}
	}

	/**
	 * 单独提取出来，是为了事务控制的原因。
	 * 
	 * @param metricChangeDOs
	 */
	public void addCustomMetricChanges(
			List<CustomMetricChangeDO> metricChangeDOs) {
		changeDAO.addCustomMetricChangeDOs(metricChangeDOs);
	}
}
