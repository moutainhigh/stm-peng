/**
 * 
 */
package com.mainsteam.stm.profilelib.deploy;

import java.util.Map;

import com.mainsteam.stm.common.metric.obj.CustomMetricMonitorInfo;

/**
 * 采集器策略部署接口，用来修改对资源实例自定义指标的调度设置进行修改，运行在DCS
 * 
 * @author ziw
 * 
 */
public interface CustomMetricCollectDeployMBean {
	/**
	 * 添加自定义指标监控
	 * 
	 * @param customMetricMonitorInfos
	 *            需要填写完整信息
	 */
	public void deployCustomMetricMonitors(
			CustomMetricMonitorInfo[] customMetricMonitorInfos,
			Map<Long, Long> profileMap);

}
