/**
 * 
 */
package com.mainsteam.stm.common.metric;

import com.mainsteam.stm.common.metric.obj.CustomMetricMonitorInfoWrapper;

/**
 * @author ziw
 * 
 */
public interface CustomMetricQueryServiceMBean {
	/**
	 * 查询指定资源实例的对应的自定义指标监控信息
	 * 
	 * @param nodeGroupId
	 *            节点组id
	 * @return CustomMetricMonitorInfo[] 自定义指标监控信息列表
	 */
	public CustomMetricMonitorInfoWrapper selectCustomMetricMonitorInfos(
			int nodeGroupId);
}
