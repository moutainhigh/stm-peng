package com.mainsteam.stm.portal.resource.api;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.mainsteam.stm.platform.web.vo.ILoginUser;

public interface IResourceDetailInfoApi {
	
	
	/**
	 * 获取资源详细信息
	 * @param instanceId
	 * @return
	 */
	public Map<String, Object> getResourceDetailInfo(Long instanceId);
	/**
	 * 通过指标类型获取资源相应的指标
	 * @param instanceId
	 * @param metricType
	 * @return isQueryNotMonitor
	 */
	public List<Map<String, Object>> getMetricByType(Long instanceId, String metricType,Boolean isQueryNotMonitor);
	/**
	 * 通过指标类型获取资源相应的指标联通状态信息
	 * @param instanceId
	 * @param metricType
	 * @return
	 */
	public Map<Long, Object> getResourceConnectivity(String[] instanceId, String metricType);
	/**
	 * 获取资源详情主页面布局及指标信息
	 * @param instanceId
	 * @return
	 */
	public List<List<List<Object>>> getMetricFromXML(Long instanceId,ILoginUser user);
	/**
	 * 保存资源相关图片
	 * @param instanceId
	 * @return
	 */
	public long saveResourceImg(MultipartFile file,Long instanceId);
	/**
	 * 查询存储信息
	 * @param instanceId
	 * @return
	 */
	public Map<String, Object> getMetricInfo(Long instanceId);
	/**
	 * 获取子资源
	 * @param instanceId
	 * @param childType
	 * @return
	 */
	public List<Map<String, Object>> getChildInstance(Long instanceId, String childType);
	/**
	 * 获取资源信息
	 * @param instanceId
	 * @return
	 */
	public Map<String, Object> getResourceInfo(Long instanceId);
	/**
	 * 获取资源的所有指标
	 * @param instanceId
	 * @return
	 */
	public List<Map<String, Object>> getAllMetric(Long instanceId);
	
	/**
	 * 手动获取信息指标
	 * @param instanceId
	 * @return
	 */
	public String getMetricHand(Long instanceId);
	/**
	 * 是否启动了手动采集
	 * @param instanceId
	 * @return
	 */
	public String isMetricHand(Long instanceId);
	
	/**
	 * 修改端口管理状态
	 * @param instanceId
	 * @return 
	 */
	public String editPortStatus(long instanceId, String condition);

	
	public List<Map<String, Object>> getMerticinfos(long instanceId,String type);

	
	/**
	 * 获取所有指标
	 * @param instanceId
	 * @param metricName
	 * @return
	 */
	public List<Map<String, Object>> getcustomMetric(Long instanceId,String metricName);
	
	/**
	 * 根据指标名称查询
	 * @param instanceId
	 * @param metricType
	 * @param metricName
	 * @return
	 */
	public List<Map<String, Object>> getMetricByTypeName(Long instanceId, String metricType,String metricName);
	/***
	 * 判断是否有查看权限
	 * @param instanceid
	 */
	public int getDomainHave(long instanceid);
	/**
	 * 获取接口流量TOP5
	 * @param instanceId
	 * @return
	 */
	public Map<String, Object> getMetricTop5(long instanceId);
}
