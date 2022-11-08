package com.mainsteam.stm.common.metric.report;

import java.util.List;
import java.util.Map;


/**
 * @author cx
 *
 */
public interface MetricDataReportService {
	/** 查询多实例汇总信息
	 * @param query
	 * @return
	 */
	public List<InstanceMetricSummeryReportData> findHistorySummaryData(MetricSummeryReportQuery query);

	/** 查询单实例的（定期）汇总信息
	 * @param query
	 * @return
	 */
	public List<InstanceMetricSummeryReportData> findInstanceHistorySummaryData(MetricSummeryReportQuery query);
	
	/**
	 * @param query
	 * @return
	 */
	public List<MetricSummeryReportData> findTopSummaryData(MetricDataTopQuery query);
	
	
	/** 用作报表线图的数据
	 * @param query
	 * @return
	 */
	public Map<String,Map<Long,List<MetricSummeryReportData>>> findInstanceMetricHistoryGroupByMetricID(MetricSummeryReportQuery query);
}
