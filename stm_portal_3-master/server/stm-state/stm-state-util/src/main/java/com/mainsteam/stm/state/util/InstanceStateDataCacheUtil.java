package com.mainsteam.stm.state.util;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.InstanceStateService;
import com.mainsteam.stm.state.obj.InstanceStateData;

/**
 * @author 作者：ziw
 * @date 创建时间：2016年11月18日 下午3:30:41
 * @version 1.0
 */
public class InstanceStateDataCacheUtil {

	private static final Log logger = LogFactory
			.getLog(InstanceStateDataCacheUtil.class);

	private final InstanceStateService instanceStateService;
	private final StateCacheUtils stateCacheUtils;

	public InstanceStateDataCacheUtil(
			InstanceStateService instanceStateService,
			StateCacheUtils stateCacheUtils) {
		super();
		this.instanceStateService = instanceStateService;
		this.stateCacheUtils = stateCacheUtils;
	}

	public InstanceStateData getInstanceState(long instanceID) {
		InstanceStateData data = stateCacheUtils.getInstanceState(instanceID);
		if (data == null) {
			data = instanceStateService.getState(instanceID);
			if (data != null) {
				stateCacheUtils.setInstanceState(data);
			}
		}
		if (data != null) {
			if (logger.isDebugEnabled())
				logger.debug("get instance state" + data.toString());
			InstanceStateData instanceStateData = new InstanceStateData();
			instanceStateData.setCollectTime(data.getCollectTime());
			instanceStateData.setInstanceID(data.getInstanceID());
			instanceStateData.setState(data.getState());
			instanceStateData.setCauseByInstance(data.getCauseByInstance());
			instanceStateData.setCauseBymetricID(data.getCauseBymetricID());
			instanceStateData.setCollectStateEnum(data.getCollectStateEnum());
			return instanceStateData;
		}
		return null;
	}

	public void saveInstanceState(InstanceStateData data) {
		if (logger.isDebugEnabled())
			logger.debug("save Instance State" + data.toString());

		InstanceStateData instanceStateData = new InstanceStateData();
		instanceStateData.setCollectTime(data.getCollectTime());
		instanceStateData.setInstanceID(data.getInstanceID());
		instanceStateData.setState(data.getState());
		instanceStateData.setCauseByInstance(data.getCauseByInstance());
		instanceStateData.setCauseBymetricID(data.getCauseBymetricID());
		instanceStateData.setCollectStateEnum(data.getCollectStateEnum());

		boolean isSuccess = stateCacheUtils.setInstanceState(instanceStateData);
		if (!isSuccess) {
			logger.warn("failed to cache instance state with " + data);
		}
		instanceStateService.addState(instanceStateData);
	}

	/**
	 * 清除资源状态：将资源的状态置为未知！ 在修改时，主动将影响指标调整为“可用性指标”
	 * 
	 * @param ides
	 */
	public void cleanInstanceStates(List<Long> ides) {
		for (long id : ides) {
			InstanceStateData data = getInstanceState(id);
			if (data == null)
				continue;
			data.setState(InstanceStateEnum.NORMAL);
			data.setCollectTime(new Date());
			saveInstanceState(data);
			stateCacheUtils.deleteAvailabilityMetricDataIMemcache(id);
		}
	}

}
