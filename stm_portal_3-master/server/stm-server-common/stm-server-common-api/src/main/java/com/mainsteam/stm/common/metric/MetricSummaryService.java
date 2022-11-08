package com.mainsteam.stm.common.metric;

import java.util.List;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.common.metric.obj.MetricSummaryData;
import com.mainsteam.stm.common.metric.query.MetricSummaryQuery;

/** 汇总数据查询接口
 * @author cx
 *
 */
public interface MetricSummaryService {
	
	/** 查询汇总指标数据
	 * @param query
	 * @return 返回的结果按 {@param:query}的顺序返回
	 */
	public List<List<MetricSummaryData>> queryMetricSummary(List<MetricSummaryQuery> query);
	
	/** 查询汇总指标数据
	 * @param query
	 * @return 返回的结果按 {@param:query}的顺序返回
	 */
	public List<MetricSummaryData> queryMetricSummary(MetricSummaryQuery query);
	
	public List<MetricSummaryData> queryCustomMetricSummary(MetricSummaryQuery query);

}
