package com.mainsteam.stm.metric.dao;

import java.util.List;

import com.mainsteam.stm.metric.dao.pojo.CustomMetricCollectDO;

public interface CustomMetricCollectDAO {

	public void insertMetricCollect(final CustomMetricCollectDO customMetricCollectDO) throws Exception;
	
	public void insertMetricCOllects(final List<CustomMetricCollectDO> customMetricCollectDOs) throws Exception;
	
	public void updateMetricCollect(final CustomMetricCollectDO customMetricCollectDO) throws Exception;
	
	public void updateMetricCollects(final List<CustomMetricCollectDO> customMetricCollectDOs) throws Exception;
	
	public void removeMetricCollectById(final long metricCollectId) throws Exception;
	
	public void removeMetricCollectByIds(final List<String> metricCollectIds) throws Exception; 
	
	public void removeMetricCollectByMetricId(final String metricId) throws Exception;
	
	public void removeMetricCollectByMetricIdAndPluginId(final String metricId,final String pluginId) throws Exception;
	
	public void removeMetricCollectByMetricIds(final List<String> metricIds) throws Exception;
	
	public CustomMetricCollectDO getMetricCollectDOById(final long metricCollectId) throws Exception;
	
	public List<CustomMetricCollectDO> getMetricCollectDOById(final List<String> metricIds) throws Exception;

	public List<CustomMetricCollectDO> getMetricCollectDOByMetricIdAndpluginId(final String metricId,final String pluginId) throws Exception;
}
