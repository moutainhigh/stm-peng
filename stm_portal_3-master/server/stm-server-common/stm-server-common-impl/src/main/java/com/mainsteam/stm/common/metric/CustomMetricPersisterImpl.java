package com.mainsteam.stm.common.metric;

import org.apache.ibatis.session.SqlSessionFactory;

public class CustomMetricPersisterImpl extends MetricDataPersister{

	public CustomMetricPersisterImpl(int capaticy,
			boolean metricSplitTable,
			SqlSessionFactory myBatisSqlSessionFactory,
			MetricTableNameManager metricTableNameCache,
			int fixed_core_threads, int fixed_max_threads, String... sessionNames) {
		super(capaticy, metricSplitTable, myBatisSqlSessionFactory,
				metricTableNameCache, fixed_core_threads, fixed_max_threads,
				sessionNames);
	}

	@Override
	public String insertSessionName() {
		return "addCustomerData";
	}

	@Override
	public String updateSessionName() {
		return "updateCustomerData";
	}

	@Override
	public boolean isExists(String metricId,long instanceId) {
		return super.getMetricTableNameCache().isCustomMetricDataExist(metricId, instanceId);
	}

	@Override
	public void updateMetricDataExist(String metricId, long instanceId) {
		super.getMetricTableNameCache().updateCustomMetricDataExist(metricId, instanceId);
	}
}
