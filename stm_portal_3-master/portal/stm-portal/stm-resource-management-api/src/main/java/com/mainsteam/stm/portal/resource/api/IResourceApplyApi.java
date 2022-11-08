package com.mainsteam.stm.portal.resource.api;

import java.util.List;
import java.util.Map;

import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.bo.HighChartsDataBo;
import com.mainsteam.stm.portal.resource.bo.ResourceApplyCPUBo;

public interface IResourceApplyApi {
	
	/**
	 * 根据指标类型获取资源的子资源类型
	 * 
	 * @return
	 */
	public ResourceMetricDef[] getHeaderInfoByMetricType(long instanceId, String metricType);
	
	public ResourceMetricDef[] getHeaderInfoByResourceId(long instanceId, String resourceId);
	
	
	/**
	 * 获取data
	 * 
	 * @return
	 */
	public List<Map<String,String>> getMetricInfo(long instanceId, String metricType , String resourceType,ILoginUser user);
	
	public Map<String, Object> getMetricData(long instanceId, String metricType,String type);
	
	public List<Map<String,String>> getMetricInfoByResourceId(long instanceId, String resourceId,List<Long> instanceidList);
	/**
	 * 获取资源实例及时状态
	 * 
	 * @return
	 */
	public String getStandardApplicationCurrentState(Long instanceId);
	
	/**
	 * 修改资源显示名称
	 * 
	 * @return
	 */
	public boolean updateResourceShowName(Long instanceId,String showName);
	
	/**
	 * 手动设置接口带宽
	 * 
	 * @return
	 */
	public boolean updateIfSpeedValue(String key,String value,String realTimeValue,Long instanceId);
	
	/**
	 * 接口带宽恢复采集
	 * 
	 * @return
	 */
	public boolean updateIfSpeedCollection(Long instanceId,String key);
	
	/**
	 * 业务查询关联指标信息
	 * 
	 * @return
	 */
	public Map<Long ,List<Map<String,String>>> getBizMetricInfo(Map<Long ,List<String>> queryMap,ResourceInstance parentResourceIns);
}
