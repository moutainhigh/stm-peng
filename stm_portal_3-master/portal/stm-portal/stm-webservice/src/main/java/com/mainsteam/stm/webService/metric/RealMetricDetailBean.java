package com.mainsteam.stm.webService.metric;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "RealMetricDetailBean")
public class RealMetricDetailBean {
	@XmlElement(name = "instanceId", required = true)
	private long instanceId;
	@XmlElement(name = "instanceName", required = true)
	private String instanceName;
	@XmlElement(name = "isMain", required = true)
	private boolean isMain;
	@XmlElement(name = "metricId", required = true)
	private String metricId;
	@XmlElement(name = "unit", required = true)
	private String unit;
	@XmlElement(name = "metricDataList", required = true)
	private List<MetricDataBean> metricDataList;
	public RealMetricDetailBean(){
		
	}
	public RealMetricDetailBean(RealMetricDetailBean bean){
		this.instanceId = bean.getInstanceId();
		this.instanceName = bean.getInstanceName();
		this.isMain = bean.isMain();
		this.metricId = bean.getMetricId();
		this.unit = bean.getUnit();
		this.metricDataList = bean.getMetricDataList();
		
	}

	public long getInstanceId() {
		return instanceId;
	}
   
	public void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	public boolean isMain() {
		return isMain;
	}

	public void setMain(boolean isMain) {
		this.isMain = isMain;
	}

	public String getMetricId() {
		return metricId;
	}

	public void setMetricId(String metricId) {
		this.metricId = metricId;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
	 
	public List<MetricDataBean> getMetricDataList() {
		return metricDataList;
	}

	public void setMetricDataList(List<MetricDataBean> metricDataList) {
		this.metricDataList = metricDataList;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{instanceId:" + instanceId)
				.append(",instanceName:" + instanceName)
				.append(",isMain:" + isMain).append(",metricId:" + metricId)
				.append(",unit:" + unit)
				.append(",metricDatalList.size:" + (metricDataList != null ? metricDataList.size() : ""));
		if (metricDataList != null && !metricDataList.isEmpty()) {
			for (MetricDataBean bean : metricDataList) {
				sb.append("[").append(bean.toString()).append("]");
			}
		}
		return sb.toString();
	}

}
