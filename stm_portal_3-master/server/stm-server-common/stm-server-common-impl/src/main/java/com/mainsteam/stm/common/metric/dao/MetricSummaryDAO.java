package com.mainsteam.stm.common.metric.dao;

import java.util.Date;
import java.util.List;

import com.mainsteam.stm.common.metric.obj.MetricSummaryData;
import com.mainsteam.stm.common.metric.obj.MetricSummaryType;
import com.mainsteam.stm.common.metric.query.MetricSummaryQuery;
import com.mainsteam.stm.common.metric.report.InstanceMetricSummeryReportData;
import com.mainsteam.stm.common.metric.report.MetricDataTopQuery;
import com.mainsteam.stm.common.metric.report.MetricSummeryReportData;
import com.mainsteam.stm.common.metric.report.MetricSummeryReportQuery;

/**
 * @author cx
 *
 */
public interface MetricSummaryDAO {

	
	public void createTable(String metricID, MetricSummaryType type);
	/**
	 * @param query
	 * @return
	 */
	public List<MetricSummaryData> query(MetricSummaryQuery query);
	
	/**
	 * @param queries
	 */
	public List<MetricSummaryData> query(List<MetricSummaryQuery> queries);


	/**
	 * @param metricList
	 * @param type
	 */
	public void summaryMetricData(String metricID,Date startTime,Date endTime,MetricSummaryType type);

	/** 报表汇总查询接口
	 * @param query
	 * @return
	 */
	public List<InstanceMetricSummeryReportData> findHistorySummaryDataForReport(MetricSummeryReportQuery query);

	/** 按时段（天/小时）
	 * @param query
	 * @return
	 */
	public List<MetricSummeryReportData> findInstanceHistorySummaryData(List<String> metricIDes,List<Long> instanceIDes,Date startTime,Date endTime,MetricSummaryType type);

	public List<MetricSummeryReportData> findTopSummaryData(MetricDataTopQuery query);

	public List<MetricSummaryData> queryCustomMetricSummary(MetricSummaryQuery query);

	public void addCustomMetricSummary(Date start, Date end, MetricSummaryType type);

	
}
