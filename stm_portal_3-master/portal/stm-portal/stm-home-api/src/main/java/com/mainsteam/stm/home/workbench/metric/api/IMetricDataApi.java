/**
 * 
 */
package com.mainsteam.stm.home.workbench.metric.api;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mainsteam.stm.common.metric.obj.MetricSummaryType;


/**
 * 
 * <li>文件名称: IMetricDataApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2017年4月1日
 * @author  tandl
 */
public interface IMetricDataApi {
	
	/**
	 * 获取资源实例指定指标在某个时间段内汇总后的指标值
	 * @param instanceId 
	 * @param metricId
	 * @param dateStart
	 * @param dateEnd
	 * @param metricSummaryType
	 */
	public Map<String, Object>  getSummaryMetricData(long instanceId ,String[] metricId,Date dateStart,Date dateEnd,MetricSummaryType metricSummaryType);
	
	/**
	 * 获取资源实例指定指标在某个时间段内指标值
	 * @param instanceId 
	 * @param metricId
	 * @param dateStart
	 * @param dateEnd
	 */
	public Map<String, Object> getMetricData(long instanceId ,String[] metricId,Date dateStart,Date dateEnd);
	
	/**
	 * 通过指标类型获取资源相应的指标
	 * @param instanceId
	 * @param metricType
	 * @return
	 */
	public List<Map<String, Object>> getMetricByType(Long instanceId, String metricType);
	
}
