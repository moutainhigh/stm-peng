/**
 * 
 */
package com.mainsteam.stm.executor;

import java.util.List;
import java.util.Map;

import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.executor.exception.MetricExecutorException;
import com.mainsteam.stm.executor.obj.CustomMetricPluginParameter;
import com.mainsteam.stm.executor.obj.DiscoveryMetricData;
import com.mainsteam.stm.executor.obj.InstanceMetricExecuteParameter;
import com.mainsteam.stm.executor.obj.MetricDiscoveryParameter;
import com.mainsteam.stm.executor.obj.MetricExecuteParameter;
import com.mainsteam.stm.pluginserver.adapter.PluginRequestClient;

/**
 * 采集服务器上的指标取值接口
 * 
 * @author ziw
 * 
 */
public interface MetricExecutor {
	/**
	 * 抓取发现使用的指标数据
	 * 
	 * @param List
	 *            <MetricBindParameter> 执行指标取值需要的数据
	 * @return List<MetricData> 指标数据
	 */
	public List<DiscoveryMetricData> catchDiscoveryMetricDatas(
			List<MetricDiscoveryParameter> bindParameters, String discoveryWay)
			throws MetricExecutorException;

	/**
	 * 根据输入的发现参数，获取网络设备的systemoid
	 * 
	 * 只适用于网络设备。
	 * 
	 * @param bindpParameter
	 *            参数类型
	 * @param discoveryWay
	 *            发现方式
	 * @return
	 * @throws MetricExecutorException
	 */
	public String getNetworkSystemOid(MetricDiscoveryParameter bindpParameter)
			throws MetricExecutorException;

	/**
	 * 获取自定义指标的值
	 * 
	 * @param customMetricPluginParameter
	 * @return
	 * @throws MetricExecutorException
	 */
	public MetricData catchMetricDataWithCustomPlugin(
			CustomMetricPluginParameter customMetricPluginParameter)
			throws MetricExecutorException;

	/**
	 * 通过 资源实例id与指标ID获取即时指标数据
	 * @param instanceID   资源实例id
	 * @param metricID  指标id
	 * @return MetricData 指标值
	 * @throws MetricExecutorException
	 */
	public MetricData catchRealtimeMetricData(long instanceID, String metricID)throws MetricExecutorException;
	/**
	 * 通过 资源实例id与指标ID获取即时指标数据
	 * @param instanceID   资源实例id
	 * @param metricID  指标id
	 * @return MetricData 指标值
	 * @throws MetricExecutorException
	 */
	public MetricData catchRealtimeMetricData(long instanceID, String metricID,Map<String,String> params)throws MetricExecutorException;

	/**
	 * 通过 资源实例id与指标ID获取即时指标数据
	 * 
	 * @param instanceID
	 *            资源实例id
	 * @param metricID
	 *            指标id
	 * @return MetricData 指标值
	 * @throws MetricExecutorException
	 */
	public List<MetricData> catchRealtimeMetricData(
			List<InstanceMetricExecuteParameter> parameters)
			throws MetricExecutorException;

	/**
	 * 指标监控取值
	 * 
	 * @param parameters
	 *            要执行的指标列表
	 */
	public void execute(List<MetricExecuteParameter> parameters);

	/**
	 * 设置请求发送器
	 * 
	 * @param requestClient
	 */
	public void setRequestClient(PluginRequestClient requestClient);
}
