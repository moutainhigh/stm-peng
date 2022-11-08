package com.mainsteam.stm.common.state.dao;


import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.state.obj.InstanceStateData;
import com.mainsteam.stm.state.obj.MetricStateData;

/**
 * @author cx
 *
 */
public interface MetricStateDAO {

	int updateByInstances(Set<Long> instanceIds, Date collectTime);

	int updateMetricState(MetricStateData state);
	/** 通过资源实例ID 获取所有性能指标数据
	 * @param instanceID
	 * @param metricID
	 * @return
	 */
	MetricStateData getMetricStateData(long instanceID,String metricID);
	
	/**
	 * @param data
	 */
	int addInstanceState(InstanceStateData data);

	int addInstanceState(List<InstanceStateData> data);
	
	/**
	 * @param instanceID
	 * @return
	 */
	InstanceStateData getInstanceState(long instanceID);

	/**
	 * @param startTime
	 * @param endTime
	 * @param instanceIDes
	 */
	List<InstanceStateData> findInstanceStateHistory(Date startTime, Date endTime,	Long[] instanceIDes);

	InstanceStateData getPreInstanceState(long instanceID,Date startTime);

	InstanceStateData getAfterInstanceState(long instanceID,Date endTime);

	List<InstanceStateData> getAllInstanceState();

	/**
	 * @param instanceIDes
	 * @return
	 */
	List<InstanceStateData> findInstanceStates(List<Long> instanceIDes);

	/**
	 * @param instanceIDes
	 * @param metricID
	 * @return
	 */
	List<MetricStateData> findMetricState(List<Long> instanceIDes,List<String> metricID);

	List<MetricStateData> findPerfMetricState(List<Long> instanceIds, Set<MetricStateEnum> ignores);

	InstanceStateData findLatestInstanceState(Long instanceID);

	void deleteBatchInstance(Set<Long> instanceSet);

	void deleteMetricState(MetricStateData metricStateData);
}
