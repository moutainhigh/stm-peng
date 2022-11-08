package com.mainsteam.stm.common.metric.dao;

import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.common.metric.MetricDataBatchPersister;
import com.mainsteam.stm.common.metric.MetricDataBatchPersisterFactory;
import com.mainsteam.stm.common.metric.obj.MetricData;

public class MetricAvailableDAOImpl implements MetricAvailableDAO{
	
	private MetricDataBatchPersister metricDataBatchPersister;

	public void setMetricDataBatchPersisterFactory(
			MetricDataBatchPersisterFactory metricDataBatchPersisterFactory) {
		this.metricDataBatchPersister = metricDataBatchPersisterFactory
				.getMetricDataBatchPersister(MetricTypeEnum.AvailabilityMetric);
	}
	
	@Override
	public void updateMetricAvailableData(MetricData data) {
		metricDataBatchPersister.saveData(data);
	}
	
	public void start() {
	}
}
