package com.mainsteam.stm.executor;

import java.util.List;
import java.util.Map;

import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.executor.exception.MetricExecutorException;
import com.mainsteam.stm.executor.obj.CustomMetricPluginParameter;
import com.mainsteam.stm.executor.obj.InstanceMetricExecuteParameter;

public interface MetricRpcExecutorMBean {

	/**
	 * 通过资源ID与指标ID获取即时指标数据
	 * 
	 * @param resourceID
	 * @param metricID
	 * @return
	 * @throws MetricExecutorException
	 * @throws Exception
	 */
	public MetricData catchRealtimeMetricData(long instanceID, String metricID) throws MetricExecutorException;

	/**
	 * 通过资源ID与指标ID获取即时指标数据
	 * 
	 * @param resourceID
	 * @param metricID
	 * @return
	 * @throws MetricExecutorException
	 * @throws Exception
	 */
	public List<MetricData> catchRealtimeMetricDatas(List<Long> instanceID, String metricID)throws MetricExecutorException;
	
	/**
	 * @param instanceID
	 * @param metricID
	 * @param params
	 * @return
	 * @throws MetricExecutorException 
	 */
	public MetricData catchRealtimeMetricDataWithParameter(long instanceID,String metricID, Map<String, String> params) throws MetricExecutorException;
	
	/**
	 * 通过 资源实例id与指标ID获取即时指标数据
	 * @param instanceID 资源实例id
	 * @param metricID  指标id
	 * @return MetricData 指标值
	 * @throws MetricExecutorException
	 */
	public List<MetricData> catchRealtimeMetricData( List<InstanceMetricExecuteParameter> parameters) throws MetricExecutorException;

	/**
	 * 获取自定义指标的值
	 * @param customMetricPluginParameter
	 * @return
	 * @throws MetricExecutorException
	 */
	public MetricData getMetricDataWithCustomPlugin( CustomMetricPluginParameter customMetricPluginParameter) throws MetricExecutorException;

	/**
	 * 驱动指标立即采值。
	 * @param parameters 待采值的指标请求
	 */
	public void fireCollectMetricDatas( List<InstanceMetricExecuteParameter> parameters);

	/**
	 * 手动采集信息指标
	 * @param parentInstanceID
	 * @param containChild
	 */
	public void triggerInfoMetricGather(long parentInstanceID, boolean containChild);

	/**
	 * 手动采集指标
	 * @param parentInstanceID
	 * @param containChild
	 */
	public void triggerMetricGather(long parentInstanceID, boolean containChild);
	
	/**
	 * 采集指标是否在执行
	 * @param parentInstanceID
	 * @return true 采集 , false 不采集
	 */
	public boolean isMetricGather(long parentInstanceID);
}
