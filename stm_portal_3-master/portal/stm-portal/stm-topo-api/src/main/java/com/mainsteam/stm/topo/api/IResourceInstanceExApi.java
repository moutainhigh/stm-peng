package com.mainsteam.stm.topo.api;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;


/**
 * <li>获取【资源实例】数据封装接口</li>
 * <li>1.本类只用来获【资源实例】的各种值</li>
 * <li>2.可自己扩展自己的接口封装</li>
 * <li>3.命名规则：资源级值（getResource+XX）、指标级值（getMetric+XX）、模型属性级值（getProp+XX）</li>
 * @version  ms.stm
 * @since  2019年11月29日
 * @author zwx
 */
public interface IResourceInstanceExApi {
	
	/**
	 * 获取登录用户所属域下的资源实例Ids
	 * @param domainSet
	 * @return
	 */
	public List<Long> getResourceIdsByDomainId(Set<Long> domainSet);
	
	/**
	 * 把字符串数组转换成字符串
	 * @param data
	 * @return
	 */
	public String parseArrayToString(String[] data);
	
	/**
	 * 转换链路接口状态级别（主要用于前台展示,返回颜色与前台css对应）
	 * @param resourceInstance
	 * @return
	 */
	@Deprecated
	public String getLinkIfStateColor(ResourceInstance resourceInstance) ;
	
	/**
	 * 转换链路-取值接口状态级别（主要用于前台展示,返回颜色与前台css对应）
	 * @param collInstance 资源实例
	 * @param type	(链路：link，接口：interface)
	 * @return
	 */
	public String getResourceLinkStateColor(ResourceInstance collInstance,String type);
	
	/**
	 * 根据链路两端状态综合计算链路状态
	 * @param linkInstance 		链路实例
	 * @param srcIfInstance 	源端接口实例
	 * @param destIfInstance	目的端接口实例
	 * @return
	 */
	public String convertLinkStateColor(ResourceInstance linkInstance,ResourceInstance srcIfInstance,ResourceInstance destIfInstance);
	
	/**
	 * 获取资源实例类型名称
	 * @param categoryId
	 * @return string	资源类型名称（如：交换机）
	 */
	@Deprecated
	public String getResourceTypeName(String categoryId);
	
	/**
	 * 批量查询【资源实例-性能指标】数据(PerformanceMetric：性能指标、AvailabilityMetric：可用性指标、InformationMetric：信息指标)
	 * @param metricIds	指标ids
	 * @param instanceIds	资源实例ids
	 * @return List<Map<String, ?>>
	 */
	public List<Map<String, ?>> getMerictRealTimeVals(String[] metricIds,long[] instanceIds);
	
	/**
	 *  获取资源实例属性值
	 * @param resourceInstance
	 * @param property	模型属性
	 * @return
	 */
	public String getPropVal(ResourceInstance resourceInstance,String property);
	
	/**
	 *  获取指标当前【状灯态】颜色（主要用于前台展示,返回颜色与前台css对应）
	 * @param resourceInstanceId
	 * @param metricId
	 * @return String
	 */
	public String getMetricStateColor(long resourceInstanceId,String metricId);
	
	/**
	 * 获取资源实例当前状态颜色（主要用于前台展示,返回颜色与前台css对应）
	 * @param InstanceStateEnum
	 * @return String
	 */
	public String getResourceStateColor(InstanceStateEnum stateEnum);
	
	/**
	 * 根据资源id+指标类型+指标id获取指标对象
	 * @param resourceInstanceId
	 * @param metricType	指标类型(PerformanceMetric：性能指标、AvailabilityMetric：可用性指标、InformationMetric：信息指标)
	 * @param metricId	指标id
	 * @return MetricData 指标数据
	 */
	public MetricData getMetricData(long resourceInstanceId,MetricTypeEnum metricType,String metricId);
	
	/**
	 * 根据资源id+【资源实例的[资源指标列表]】+指标id获取指标对象
	 * @param resourceInstanceId
	 * @param resourceMetricDefs
	 * @param metricId
	 * @return MetricData
	 */
	public MetricData getMetricData(long resourceInstanceId,ResourceMetricDef[] resourceMetricDefs,String metricId);
	
	/**
	 * 获取资源实例指标对象
	 * @param resourceInstance
	 * @param metricId
	 * @return String
	 */
	public MetricData getMetricData(ResourceInstance resourceInstance,String metricId);
	
	/**
	 * 获取资源实例指标值（定义了单位的同时返回单位，如：34%）
	 * @param resourceInstanceId	资源实例id
	 * @param metricType	指标类型
	 * @param metricId	指标id
	 * @param unit	指标单位
	 * @return String
	 */
	public String getMetricVal(Long resourceInstanceId,MetricTypeEnum metricType,String metricId,String unit);
	
	/**
	 * 根据【指标map】+指标id+【指标单位定义map】获取资源实例指标值（定义了单位的同时返回单位，如：34%）
	 * @param metric	指标map集合（{"cpuRate":0,"cpuRateCollectTime":1418996627000,"instanceid":15344,"memRateCollectTime":1418996627000}）
	 * @param metricId	指标id
	 * @param metricsUnit	指标单位
	 * @return String	指标值
	 */
	public String getMetricVal(Map<String, ?> metric,String metricId,Map<String,String> metricsUnit);
	
	/**
	 * 根据【资源实例的[资源指标列表]】获取资源实例指标值（定义了单位的同时返回单位，如：34%）
	 * @param resourceInstanceId
	 * @param resourceMetricDefs
	 * @param metricId
	 * @return String
	 */
	public String getMetricVal(Long resourceInstanceId,ResourceMetricDef[] resourceMetricDefs,String metricId);
	
	/**
	 * 根据【资源实例】获取资源实例指标值（定义了单位的同时返回单位，如：34%）
	 * @param resourceInstance
	 * @param metricId
	 * @return String
	 */
	public String getMetricVal(ResourceInstance resourceInstance,String metricId);
	
	/**
	 * 获取资源实例所有指标定义
	 * @param resourceInstance
	 * @return ResourceMetricDef[]
	 */
	public ResourceMetricDef[] getMetricDefs(ResourceInstance resourceInstance);
	
	/**
	 * 时间转换为字符串(如:0天14小时0分钟36秒)
	 * @param date
	 * @return string
	 */
	public String parseDateToStr(Date date);
	
	/**
	 * 根据【资源实例的[资源指标列表]】获取所有指标单位(没有单位返回"")
	 * @param resourceMetricDefs
	 * @return Map<String,String>
	 */
	public Map<String,String> getMetricsUnitMap(ResourceMetricDef[] resourceMetricDefs);
	
	/**
	 * 获取资源实例当前状态
	 * @param resourceInstance
	 * @return InstanceStateEnum
	 */
//	public InstanceStateEnum getResourceState(ResourceInstance resourceInstance);
	
	/**
	 * 获取资源实例定义
	 * @param resourceId
	 * @return ResourceDef
	 */
//	public ResourceDef getResourceDef(ResourceInstance resourceInstance);
	
	/**
	 * 获取指标单位(没有单位返回"")
	 * @param resourceInstance
	 * @param metricId
	 * @return String
	 */
//	public String getMetricUnit(ResourceInstance resourceInstance,String metricId);
}
