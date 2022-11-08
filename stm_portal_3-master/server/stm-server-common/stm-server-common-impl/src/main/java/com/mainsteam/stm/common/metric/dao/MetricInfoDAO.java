package com.mainsteam.stm.common.metric.dao;


import java.util.List;

import com.mainsteam.stm.common.metric.obj.MetricData;


public interface MetricInfoDAO {

	/**
	 * @param list
	 */
	public void addMetricInfoData(MetricData metricData);

	/**
	 * @param instanceID
	 * @param metricID
	 * @return
	 */
	public MetricData getMetricInfoData(long instanceID, String metricID);

	/**
	 * @param instanceID
	 * @param metricID
	 * @return
	 */
	public List<MetricData> getMetricInfoDatas(long[] instanceID, String[] metricID);

}
