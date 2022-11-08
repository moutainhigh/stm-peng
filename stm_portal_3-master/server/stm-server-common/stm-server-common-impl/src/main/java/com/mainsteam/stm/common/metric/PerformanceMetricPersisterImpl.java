package com.mainsteam.stm.common.metric;

import org.apache.ibatis.session.SqlSessionFactory;

public class PerformanceMetricPersisterImpl extends MetricDataPersister{

	public PerformanceMetricPersisterImpl(int capaticy,
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
		return "insertRealTimeMetricData";
	}

	@Override
	public String updateSessionName() {
		return "updateRealTimeMetricData";
	}

	@Override
	public boolean isExists(String metricId,long instanceId) {
		return super.getMetricTableNameCache().isPerformantMetricDataExist(metricId, instanceId);
	}

	@Override
	public void updateMetricDataExist(String metricId, long instanceId) {
		super.getMetricTableNameCache().updatePerformanceMetricDataExist(metricId, instanceId);
	}
}
