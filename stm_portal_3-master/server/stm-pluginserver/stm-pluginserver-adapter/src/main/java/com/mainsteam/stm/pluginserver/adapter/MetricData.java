package com.mainsteam.stm.pluginserver.adapter;

import java.util.Date;
import java.util.List;

import com.mainsteam.stm.dataprocess.MetricCalculateData;

public class MetricData extends MetricCalculateData {

	public MetricData(String[] metricData, String metricId,
			long resourceInstanceId, Date collectTime,String resourceId) {
		super();
		super.setMetricData(metricData);
		super.setMetricId(metricId);
		super.setResourceInstanceId(resourceInstanceId);
		super.setCollectTime(collectTime);
		super.setResourceId(resourceId);
	}

	@Override
	public void setCollectTime(Date collectTime) {
		// do nothing
	}

	@Override
	public void setMetricData(String[] metricData) {
		// do nothing
	}

	@Override
	public void setMetricId(String metricId) {
		// do nothing
	}

	@Override
	public void setMetricRelationData(List<Object> metricRelationData) {
		// do nothing
	}

	@Override
	public void setResourceId(String resourceId) {
		// do nothing
	}

	@Override
	public void setResourceInstanceId(long resourceInstanceId) {
		// do nothing
	}
}
