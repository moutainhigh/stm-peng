package com.mainsteam.stm.metric.dao;

import java.util.List;

import com.mainsteam.stm.metric.dao.pojo.CustomMetricBindDO;

public interface CustomMetricBindDAO {

	public void insertMetricBind(final CustomMetricBindDO customMetricBindDO) throws Exception;
	
	public void insertMetricBinds(final List<CustomMetricBindDO> customMetricBindDOs) throws Exception;
	
	public void removeMetricBindbyDO(final CustomMetricBindDO customMetricBindDO) throws Exception;
	
	public void removeMetricBindbyDOs(final List<CustomMetricBindDO> customMetricBindDOs) throws Exception;
	
	public void removeMetricBindbyInstanceIds(List<Long> instanceIds);
	
	public List<CustomMetricBindDO> getCustomMetricBindByMetric(final String metricId) throws Exception;
	
	public List<CustomMetricBindDO> getCustomMetricBindByMetrics(final List<String> metricIds) throws Exception;
	
	public List<CustomMetricBindDO> getCustomMetricBindByMetricAndPluginId(final String metricId,final String pluginId) throws Exception;
	
	public List<CustomMetricBindDO> getCustomMetricBindByInstanceId(final long instancId) throws Exception;
	
	public List<CustomMetricBindDO> getCustomMetricBindByInstanceIds(final List<Long> instanceIds) throws Exception;
	
	public List<CustomMetricBindDO> getCustomMetricBindByNodeGroupId(final int nodeGroupId) throws Exception;
	
	public List<CustomMetricBindDO> getAllCustomMetricBinds();
}
