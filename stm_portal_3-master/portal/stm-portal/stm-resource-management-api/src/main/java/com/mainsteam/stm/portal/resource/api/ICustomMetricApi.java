package com.mainsteam.stm.portal.resource.api;

import java.util.List;

import com.mainsteam.stm.caplib.dict.DiscoverWayEnum;
import com.mainsteam.stm.caplib.dict.PluginIdEnum;
import com.mainsteam.stm.metric.obj.CollectMeticSetting;
import com.mainsteam.stm.metric.obj.CustomMetricBind;
import com.mainsteam.stm.metric.obj.CustomMetricInfo;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.bo.CustomMetricBo;
import com.mainsteam.stm.portal.resource.bo.CustomMetricPageBo;
import com.mainsteam.stm.portal.resource.bo.CustomMetricResourceBo;
import com.mainsteam.stm.system.resource.bo.ResourceInstanceBo;


public interface ICustomMetricApi {
	
	/**
	 * 通过资源ID 查询自定义指标
	 * @param instanceId
	 * @return
	 */
	public List<CustomMetricBo> getCustomMetricsByInstanceId(long instanceId);
	
	/**
	 * 查询所有自定义指标
	 * 
	 * @return 自定义指标列表
     * @exception CustomMetricException
	 */
	public List<CustomMetricBo> getCustomMetrics(Long startRow, Long pageSize,CustomMetricBo customMetricBo);
	
	/**
	 * 获取指标
	 * @param metricId
	 * @return
	 */
	public CustomMetricBo getCustomMetric(String metricId);
	
	/**
	 * 创建自定义指标
	 * 
	 * @param metric
	 *            自定义指标
	 * @return 自定指标id
	 * @exception CustomMetricException
	 */
	public String createCustomMetric(CustomMetricBo metric);
	
	/**
	 * 创建自定义指标
	 * 
	 * @param metric
	 *            自定义指标
	 * @return 自定指标id
	 * @exception CustomMetricException
	 */
	public void updateCustomMetric(CustomMetricBo metric);
	
	/**
	 * 删除自定义指标
	 * @param metricId
	 */
	public void deleteCustomMetric(String metricId);
	
	/**
	 * 删除自定义指标
	 * @param metricId
	 */
	public void deleteCustomMetrics(List<String> metricIds);
	
	/**
	 * 更新是否告警/是否监控
	 * @param collectMeticSetting
	 */
	public void updateCustomMeticSetting(CollectMeticSetting collectMeticSetting);
	
	
	/**
	 * 根据资源类型获取所有的资源
	 * @param user
	 * @param categoryId
	 * @return
	 */
	public List<CustomMetricResourceBo> getResourceInstances(ILoginUser user,String categoryId,Long domainId,PluginIdEnum pluginId);
	
	/**
	 * 根据指标查询当前指标绑定的所有资源
	 * @param metricId
	 * @return
	 */
	public List<CustomMetricResourceBo> getCustomMetricBinds(String metricId);
	
	/**
	 * 更新绑定的资源
	 * @param bindResources
	 */
	public void updateResuorceBind(String metricId,String pluginId,List<CustomMetricResourceBo> bindResources);
	
	/**
	 * 获取自定义指标总数
	 */
	public int getCustomMetricCount();
}
