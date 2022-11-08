/**
 * 
 */
package com.mainsteam.stm.metric;

import java.util.List;

import com.mainsteam.stm.metric.exception.CustomMetricException;
import com.mainsteam.stm.metric.obj.CollectMeticSetting;
import com.mainsteam.stm.metric.obj.CustomMetric;
import com.mainsteam.stm.metric.obj.CustomMetricBind;
import com.mainsteam.stm.metric.obj.CustomMetricCollectParameter;
import com.mainsteam.stm.metric.obj.CustomMetricDataProcess;
import com.mainsteam.stm.metric.obj.CustomMetricInfo;
import com.mainsteam.stm.metric.obj.CustomMetricQuery;
import com.mainsteam.stm.metric.obj.CustomMetricThreshold;

/**
 * @author ziw
 * 
 */
public interface CustomMetricService {
	/**
	 * 创建自定义指标
	 * 
	 * @param metric
	 *            自定义指标
	 * @return 自定指标id
	 * @exception CustomMetricException
	 */
	public String createCustomMetric(CustomMetric metric)
			throws CustomMetricException;

	/**
	 * 查询所有自定义指标
	 * 
	 * @return 自定义指标列表
	 * @exception CustomMetricException
	 */
	public CustomMetric getCustomMetric(String metricId)
			throws CustomMetricException;
	
	/**
	 * 查询自定义指标的个数
	 * 
	 * @return
	 */
	public int getCustomMetricsCount(CustomMetricQuery customMetricQuery);

	

	/**
	 * 分页查询自定义指标
	 * @return 自定义指标列表
	 * @exception CustomMetricException
	 */
	public List<CustomMetric> getCustomMetrics(CustomMetricQuery CustomMetricQuery,int startRow, int pageSize) 	throws CustomMetricException;;
	/**
	 * 查询所有自定义指标
	 * 
	 * @param instanceId
	 * @return 自定义指标列表
	 * @exception CustomMetricException
	 */
	public List<CustomMetric> getCustomMetricsByInstanceId(long instanceId)
			throws CustomMetricException;

	/**
	 * 更新自定义指标的监控配置信息
	 * 
	 * @param collectMeticSettings
	 *            CollectMeticSetting:修改哪个属性，就设置哪个属性的值。metricId必须设置
	 * @exception CustomMetricException
	 */
	public void updateCustomMericSettings(
			List<CollectMeticSetting> collectMeticSettings)
			throws CustomMetricException;

	/**
	 * 更新自定义指标的监控配置信息
	 * 
	 * @param collectMeticSetting
	 *            CollectMeticSetting:修改哪个属性，就设置哪个属性的值。metricId必须设置
	 * @exception CustomMetricException
	 */
	public void updateCustomMericSetting(
			CollectMeticSetting collectMeticSetting)
			throws CustomMetricException;
	
	/**
	 * 更新自定义指标的基础信息。（指标名称，指标单位，指标类型创建后不允许修改）
	 * 
	 * @param metric
	 *            指标的基础信息
	 * @exception CustomMetricException
	 */
	public void updateCustomMetricBasicInfo(CustomMetricInfo customMetricInfo)
			throws CustomMetricException;

	/**
	 * 删除一个自定义指标
	 * 
	 * @param metricId
	 *            自定义指标id
	 * @exception CustomMetricException
	 */
	public void deleteCustomMetric(String metricId)
			throws CustomMetricException;

	/**
	 * 批量删除自定义指标
	 * 
	 * @param metricIds
	 *            自定义指标id列表
	 * @exception CustomMetricException
	 */
	public void deleteCustomMetric(List<String> metricIds)
			throws CustomMetricException;

	/**
	 * 设置自定义指标的阈值
	 * 
	 * @param thresholds
	 * @exception CustomMetricException
	 */
	public void updateCustomMetricThreshold(List<CustomMetricThreshold> thresholds)
			throws CustomMetricException;

	/**
	 * 查询自定义指标的阈值定义
	 * 
	 * @param metricId
	 *            自定义指标id
	 * @return List<CustomMetricThreshold> 自定义指标的阈值定义列表
	 * @exception CustomMetricException
	 */
	public List<CustomMetricThreshold> getCustomMetricThresholds(String metricId)
			throws CustomMetricException;

	/**
	 * 设置自定义指标的采值参数
	 * 
	 * @param metricId
	 *            自定义指标id
	 * @param pluginId
	 *            采值插件id
	 * @param collectParameters
	 * 
	 * @exception CustomMetricException
	 */
	public void updateCustomMetricCollects(String metricId, String pluginId,
			List<CustomMetricCollectParameter> collectParameters)
			throws CustomMetricException;

	/**
	 * 查找自定义指标的采值参数
	 * 
	 * @param metricId
	 *            自定义指标id
	 * @param pluginId
	 *            采值插件id
	 * @return List<CustomMetricCollectParameter> 自定义指标的采值参数
	 * 
	 * @exception CustomMetricException
	 */
	public List<CustomMetricCollectParameter> getCustomMetricCollects(
			String metricId, String pluginId) throws CustomMetricException;

	/**
	 * 查找自定义指标的采值参数
	 * 
	 * @param metricId
	 *            自定义指标id
	 * @return List<CustomMetricCollectParameter> 自定义指标的采值参数
	 * 
	 * @exception CustomMetricException
	 */
	public List<CustomMetricCollectParameter> getCustomMetricCollectsByMetricId(
			String metricId) throws CustomMetricException;

	/**
	 * 查找自定义指标的采值参数
	 * 
	 * @param instanceId
	 *            资源实例id
	 * @return List<CustomMetricCollectParameter> 自定义指标的采值参数
	 * 
	 * @exception CustomMetricException
	 */
	public List<CustomMetricCollectParameter> getCustomMetricCollectsByInstanceId(
			long instanceId) throws CustomMetricException;

	/**
	 * 绑定自定义指标和资源实例的关系
	 * 
	 * @param customMetricBinds
	 * 
	 * @exception CustomMetricException
	 */
	public void addCustomMetricBinds(List<CustomMetricBind> customMetricBinds)
			throws CustomMetricException;

	/**
	 * 删除之定义指标和资源实例的绑定关系
	 * 
	 * @param instanceIds
	 * 
	 * @exception CustomMetricException
	 */
	public void deleteCustomMetricBinds(List<CustomMetricBind> customMetricBinds)
			throws CustomMetricException;

	/**
	 * 删除自定义指标的指定插件的所有绑定关系。
	 * 
	 * @param metricId
	 *            自定义指标id
	 * @param pluginId
	 *            采值插件id
	 * @exception CustomMetricException
	 */
	public void clearCustomMetricBinds(String metricId, String pluginId)
			throws CustomMetricException;

	/**
	 * 删除实例id的自定义指标的指定插件的所有绑定关系。
	 * 
	 * @param instanceIds
	 * 
	 * @exception CustomMetricException
	 */
	public void clearCustomMetricBindsByInstanceIds(List<Long> instanceIds)
			throws CustomMetricException;

	/**
	 * 查找指定指标的绑定关系
	 * 
	 * @param metricId
	 *            指标id
	 * @return List<CustomMetricBind> 自定义指标列绑定关系表
	 */
	public List<CustomMetricBind> getCustomMetricBindsByMetricId(String metricid) 	throws CustomMetricException;
	
	/**
	* @Title: updateCustomMetricDataProcess
	* @Description: 更新自定义指标数据处理方式
	* @param dataProcess
	* @throws CustomMetricException  void
	* @throws
	*/
	public void updateCustomMetricDataProcess(CustomMetricDataProcess dataProcess) throws CustomMetricException;
}
