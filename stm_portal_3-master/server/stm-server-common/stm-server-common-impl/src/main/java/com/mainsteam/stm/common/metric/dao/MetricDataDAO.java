package com.mainsteam.stm.common.metric.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.common.metric.obj.MetricSummaryData;
import com.mainsteam.stm.common.metric.query.MetricHistoryDataQuery;
import com.mainsteam.stm.common.metric.query.MetricRealtimeDataQuery;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

/**
 * @author heshengchao
 *
 */
public interface MetricDataDAO {

	public String selectMetricTable(Map<String, String> param);

	public List<String> getMetricTableList(Map<String, String> param);

	public List<Long> selectExistRealTimeMetric(Map<String, String> param);

	
	public void updateRealTimeMetricData(MetricData md);

	public void addMetricDataBatch(List<MetricData> metricList);

	/** 分页查询历史指标数据
	 * @param pager
	 * @return
	 */
	public Page<List<MetricData>,MetricHistoryDataQuery> queryHistoryMetricDatas(Page<List<MetricData>,MetricHistoryDataQuery> pager);
	
	/** 查询历史指标数据
	 * @param query
	 * @return
	 */
	public List<MetricData> queryHistoryMetricDatas(MetricHistoryDataQuery query);
	
	/**
	 * @param pager
	 * @return
	 */
	public List<Map<String,?>> queryRealtimeMetricDataForPage(Page<Map<String,?>,MetricRealtimeDataQuery> pager) ;

	public List<Map<String,?>> queryRealtimeMetricData(MetricRealtimeDataQuery query) ;
	/**
	 * @param metricID
	 */
	String createRealtimeMetricTable(String metricID);
	
	/**
	 * @param metricID
	 */
	String createHistoryMetricTable(String metricID);

	/**获取性能指标列表
	 * @return
	 */
	public Set<String> getPerformanceMetricList();

	/**
	 * @param collectionTime 统计的时间区间
	 * @param metric
	 * @return
	 */
	public List<MetricSummaryData> countHistory(Date startTime,Date endTime,String metric);

	/**
	 * @param metricID
	 * @param instanceIDes
	 * @param topn
	 * @param order
	 * @return
	 */
	public List<MetricData> findTop(String metricID, long[] instanceIDes,int topn,String order);

	public MetricData getMetricPerformanceData(long instanceID, String metricID);

	public MetricData getMetricAvailableData(long instanceID, String metricID);

	List<MetricData> getMetricAvailableData(long instanceID, Set<String> metrics);

	public void addCustomerData(MetricData data);

	public MetricData getCustomerData(long instanceID, String metricID);

	public List<MetricData> queryHistoryCustomerMetricDatas(MetricHistoryDataQuery query);

}
