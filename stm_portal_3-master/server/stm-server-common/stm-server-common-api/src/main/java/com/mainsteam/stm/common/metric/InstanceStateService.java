package com.mainsteam.stm.common.metric;

import java.util.List;

import com.mainsteam.stm.executor.exception.MetricExecutorException;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.state.obj.InstanceStateData;

public interface InstanceStateService {

	/**
	 * @param instanceID
	 * @return
	 */
	InstanceStateData getState(long instanceID);
	/**
	 * @param instanceID
	 * @return
	 */
	InstanceStateData getStateAdapter(long instanceID);
	/**
	 * @param instanceIDes
	 * @return
	 */
	List<InstanceStateData> findStates(List<Long> instanceIDes);
	/**
	 * @param instanceIDes
	 * @return
	 */
	List<InstanceStateData> findStatesAdapter(List<Long> instanceIDes);
	/**
	 * @param data
	 */
	boolean addState(InstanceStateData data);

	boolean addState(List<InstanceStateData> data);

	/**获取实时数据的(适配)状态
	 * @param instanceID
	 * @return
	 * @throws MetricExecutorException
	 * @throws InstancelibException
	 */
	InstanceStateData catchRealtimeMetricData(long instanceID)throws MetricExecutorException, InstancelibException;

	List<InstanceStateData> getAllInstanceState();

	InstanceStateData getLatestInstanceState(Long instanceId);
}
