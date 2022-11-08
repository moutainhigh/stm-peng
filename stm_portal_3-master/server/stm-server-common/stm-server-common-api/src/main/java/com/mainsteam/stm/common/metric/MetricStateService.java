package com.mainsteam.stm.common.metric;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.state.obj.MetricStateData;


/** 指标状态接口
 * @author cx
 *
 */
public interface MetricStateService {
	
	
	/**
	 * @param instanceID
	 * @param metricID
	 * @return
	 */
	MetricStateData getMetricState(long instanceID,String metricID);

	void deleteMetricState(MetricStateData stateData);

	/**
	 * @param state
	 */
	boolean updateMetricState(MetricStateData state);

	boolean updateByInstances(Set<Long> instanceIds, Date collectTime);

	/**
	 * @param instanceIDes
	 * @param metricID
	 * @return
	 */
	List<MetricStateData> findMetricState(List<Long> instanceIDes,List<String> metricID);

	List<MetricStateData> findPerfMetricState(List<Long> instanceIDes, Set<MetricStateEnum> ignoreStates);

	@Deprecated
	MetricStateData getPerformanceStateData(long instanceID, String metricID);

}
