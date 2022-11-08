package com.mainsteam.stm.metric.dao;

import java.util.List;

import com.mainsteam.stm.metric.dao.pojo.CustomMetricThresholdDO;

public interface CustomMetricThresholdDAO {

	public void insertCustomMetricThreshold(final CustomMetricThresholdDO customMetricThresholdDO) throws Exception;
	
	public void insertCustomMetricThresholds(final List<CustomMetricThresholdDO> customMetricThresholdDOs) throws Exception;
	
	public void updateCustomMetricThreshold(final CustomMetricThresholdDO customMetricThresholdDO) throws Exception;
	
	public void updateCustomMetricThresholds(final List<CustomMetricThresholdDO> customMetricThresholdDOs) throws Exception;
	
	public List<CustomMetricThresholdDO> getCustomMetricThresholdsByMetricId(final String metricId) throws Exception;
	
	public void removeThresholdsByMetricId(String metricId);
	
	public void removeThresholdsByMetricIds(List<String> metricIds);
}
