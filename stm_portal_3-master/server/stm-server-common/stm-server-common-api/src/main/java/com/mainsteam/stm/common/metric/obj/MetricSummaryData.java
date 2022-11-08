/**
 * 
 */
package com.mainsteam.stm.common.metric.obj;

import java.io.Serializable;
import java.util.Date;

/**
 * 指标数据
 * 
 * @author ziw
 *
 */
public class MetricSummaryData implements Serializable{
	private static final long serialVersionUID = 1105827007793878126L;

	private Float metricData;
	private Float maxMetricData;
	private Float minMetricData;
	/**指标id*/
	private String metricId;
	/**资源实例id*/
	private long instanceId;
	/**资源ids*/
	private String resourceId;
	/**汇总开始时间*/
	private Date startTime;
	/**汇总结束时间*/
	private Date endTime;
	private Date updateTime;
	
	public String getMetricId() {
		return metricId;
	}

	public void setMetricId(String metricId) {
		this.metricId = metricId;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public Float getMetricData() {
		return metricData;
	}

	public void setMetricData(Float metricData) {
		this.metricData = metricData;
	}

	public Float getMaxMetricData() {
		return maxMetricData;
	}

	public void setMaxMetricData(Float maxMetricData) {
		this.maxMetricData = maxMetricData;
	}

	public Float getMinMetricData() {
		return minMetricData;
	}

	public void setMinMetricData(Float minMetricData) {
		this.minMetricData = minMetricData;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public long getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
}
