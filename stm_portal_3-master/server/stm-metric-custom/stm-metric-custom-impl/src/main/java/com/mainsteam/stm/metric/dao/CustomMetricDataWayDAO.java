package com.mainsteam.stm.metric.dao;

import java.util.List;

import com.mainsteam.stm.metric.dao.pojo.CustomMetricDataWayDO;

public interface CustomMetricDataWayDAO {

	public void insertMetricDataWay(final CustomMetricDataWayDO customMetricDataWayDO) throws Exception;
	
	public void insertMetricDataWays(final List<CustomMetricDataWayDO> customMetricDataWayDOs) throws Exception;
	
	public void removeMetricDataWaybyDO(final CustomMetricDataWayDO customMetricDataWayDO) throws Exception;
	
	public void removeMetricDataWaybyDOs(final List<CustomMetricDataWayDO> customMetricDataWayDOs) throws Exception;
	
	public void removeMetricDataWaybyMetricIds(final List<String> metricIds) throws Exception;
	
	public void removeMetricDataWaybyMetricId(final String metricId) throws Exception;
	
	public List<CustomMetricDataWayDO> getCustomMetricDataWayByMetric(final String metricId) throws Exception;
	
	public List<CustomMetricDataWayDO> getCustomMetricDataWayByMetrics(final List<String> metricIds) throws Exception;
	
	public List<CustomMetricDataWayDO> getCustomMetricDataWayByMetricAndPluginId(final String metricId,final String pluginId) throws Exception;
	
}
