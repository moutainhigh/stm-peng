package com.mainsteam.stm.common.metric;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.common.state.dao.MetricStateDAO;
import com.mainsteam.stm.state.obj.MetricStateData;

public class MetricStateServiceImpl implements MetricStateService{
	
	private MetricStateDAO metricStateDAO;	
	
	
	public void setMetricStateDAO(MetricStateDAO metricStateDAO) {
		this.metricStateDAO = metricStateDAO;
	}
	
	
	@Override
	public List<MetricStateData> findMetricState(List<Long> instanceIDes,List<String> metricID) {
		return metricStateDAO.findMetricState(instanceIDes, metricID);
	}

	@Override
	public List<MetricStateData> findPerfMetricState(List<Long> instanceIDes, Set<MetricStateEnum> ignoreStates) {
		return metricStateDAO.findPerfMetricState(instanceIDes, ignoreStates);
	}

	@Override
	public MetricStateData getMetricState(long instanceID,String metricID) {
		return metricStateDAO.getMetricStateData(instanceID, metricID);
	}

	@Override @Deprecated
	public MetricStateData getPerformanceStateData(long instanceID,String metricID) {
		return getMetricState(instanceID, metricID);
	}
	@Override
	public boolean updateMetricState(MetricStateData state) {
		int flag = metricStateDAO.updateMetricState(state);
		return flag > 0 ? true : false;
	}

	@Override
	public boolean updateByInstances(Set<Long> instanceIds, Date collectTime) {
		return metricStateDAO.updateByInstances(instanceIds, collectTime) > 0 ? true : false;
	}

	@Override
	public void deleteMetricState(MetricStateData stateData) {
		metricStateDAO.deleteMetricState(stateData);
	}
}
