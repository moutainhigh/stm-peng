package com.mainsteam.stm.webService.metric;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "MetricRealSummaryBean")
public class MetricRealSummaryBean {
	/*资源实例id*/
	@XmlElement(name="instanceid",required = true)
	private long instanceId; 
	/* 模型类型Id */
	@XmlElement(name = "categoryId",required = true)
	private String categoryId;
	/* 模型名称 */
	@XmlElement(name = "categoryName",required = true)
	private String categoryName;
	/* 资源Id */
	@XmlElement(name = "resourceId",required = true)
	private String resourceId;
	/* IP地址 */
	@XmlElement(name = "ip",required = true)
	private String ip;
	/*接口资源ID*/
	@XmlElement(name = "netFaceResourceId",required = true)
	private String netFaceResourceId;
	/*接口子资源实例集合*/
	@XmlElement(name = "netChildInstanceIdList",required = true)
	private List<Long> netChildInstanceIdList;
	/*<String metricId,RealMetricDetailBean 统计指标的详情>*/
	@XmlElement(name = "realMetricMap",required = true)
	private Map<String,List<RealMetricDetailBean>> realMetricMap;
	public long getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
	}
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getNetFaceResourceId() {
		return netFaceResourceId;
	}
	public void setNetFaceResourceId(String netFaceResourceId) {
		this.netFaceResourceId = netFaceResourceId;
	}
	public List<Long> getNetChildInstanceIdList() {
		return netChildInstanceIdList;
	}
	public void setNetChildInstanceIdList(List<Long> netChildInstanceIdList) {
		this.netChildInstanceIdList = netChildInstanceIdList;
	}
	public Map<String, List<RealMetricDetailBean>> getRealMetricMap() {
		return realMetricMap;
	}
	public void setRealMetricMap(Map<String, List<RealMetricDetailBean>> realMetricMap) {
		this.realMetricMap = realMetricMap;
	}
	
}
