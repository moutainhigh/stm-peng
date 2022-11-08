/**
 * 
 */
package com.mainsteam.stm.common.metric;

import java.util.List;

import com.mainsteam.stm.common.metric.obj.AvailMetricData;

/**
 * 查询可用性指标数据
 * 
 * @author ziw
 * 
 */
public interface MetricAvailQueryServiceMBean {
	/**
	 * 获取所有主资源的可用性指标
	 * 
	 * 排除链路
	 * 
	 * @param limit
	 *            一次获取的最大条数
	 * @return List<AvailMetricData> 可用性指标数据
	 */
	public List<AvailMetricData> getParentInsanceAvailMetricDatas(int nodeGroupId,int start,int length);
}
