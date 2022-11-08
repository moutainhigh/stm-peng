package com.mainsteam.stm.portal.resource.api;

import java.util.Date;

import com.mainsteam.stm.portal.resource.bo.HighChartsDataBo;

public interface IMetricChartsDataApi {

	/**
	 * 获取data
	 * 
	 * @return
	 */
	public HighChartsDataBo getHighChartsData(String highChartsQueryType,int highChartsQueryNum,long instanceId ,String metricId);
	
	/**
	 * 获取data
	 * 
	 * @return
	 */
	public HighChartsDataBo getSpecialMetricChartsData(long instanceId ,String[] metricId,Date dateStart,Date dateEnd,String metricDataType);
	
	/**
	 * 获取data
	 * 
	 * @return
	 */
	public HighChartsDataBo getSpecialMetricChartData(long instanceId ,String[] metricId,Date dateStart,Date dateEnd,String metricDataType);
	
	/**
	 * 获取data
	 * 
	 * @return
	 */
	public HighChartsDataBo getHighChartsDataByTime(long instanceId ,String metricId,Date dateStart,Date dateEnd);
	
}
