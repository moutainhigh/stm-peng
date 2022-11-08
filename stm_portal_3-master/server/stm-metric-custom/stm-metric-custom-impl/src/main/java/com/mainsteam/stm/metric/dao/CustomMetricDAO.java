package com.mainsteam.stm.metric.dao;

import java.util.List;

import com.mainsteam.stm.metric.dao.pojo.CustomMetricDO;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

public interface CustomMetricDAO {

	public void insertMetric(final CustomMetricDO customMetricDO) throws Exception;
	
	public void insertMetrics(final List<CustomMetricDO> customMetricDOs) throws Exception;
	
	public void updateMetric(final CustomMetricDO customMetricDO) throws Exception;
	
	public void updateMetrics(final List<CustomMetricDO> customMetricDOs) throws Exception;
	
	public CustomMetricDO getMetricDOById(final String metriceId) throws Exception;
	
	public List<CustomMetricDO> getAllMetric(Page<CustomMetricDO, CustomMetricDO> page) throws Exception;
	
	public List<CustomMetricDO> getMetrics(final List<String> metricId) throws Exception;
	
	public void removeMetricById(final String metricId) throws Exception;
	
	public void removeMetricByIds(final List<String> metricId) throws Exception;
	
	public int getCustomMetricsCount(CustomMetricDO query);
	
	public List<CustomMetricDO> getAllCustomMetricDOs();
}
