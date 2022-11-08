package com.mainsteam.stm.portal.resource.api;

import java.util.List;

import com.mainsteam.stm.common.metric.obj.MetricData;

/**
 * <li>文件名称: InfoMetricQueryAdaptApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  oc4.2.1
 * @since    2016年9月28日
 * @author   tongpl
 */
public interface InfoMetricQueryAdaptApi {

	public MetricData getMetricInfoData(long instanceId,String metricId);
	
	public List<MetricData>  getMetricInfoDatas(long instanceId,String[] metricIds);
	
	public List<MetricData>  getMetricInfoDatas(long[] instanceIds,String[] metricIds);
}
