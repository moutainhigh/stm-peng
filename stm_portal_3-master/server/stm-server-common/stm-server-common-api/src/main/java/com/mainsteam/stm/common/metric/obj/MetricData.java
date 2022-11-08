/**
 * 
 */
package com.mainsteam.stm.common.metric.obj;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.mainsteam.stm.caplib.dict.MetricTypeEnum;

/**
 * 指标数据
 * 
 * @author ziw
 *
 */
public class MetricData implements Serializable{
	private static final long serialVersionUID = 1105827007793878126L;
	private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
	
	private String[] data;
	
	/**
	 * 指标id
	 */
	private String metricId;
	
	/**
	 * 资源实例id
	 */
	private long resourceInstanceId;
	
	/**
	 * 资源ids
	 */
	private String resourceId;
	
	/**
	 * 采集时间
	 */
	private Date collectTime;
	
	/**
	 * 使用的策略id
	 */
	private Long profileId ;
	
	/**
	 * 使用的基线id
	 */
	private Long timelineId ;
	
	private MetricTypeEnum metricType;
	
	private boolean isCustomMetric = false;
	
	public boolean isCustomMetric() {
		return isCustomMetric;
	}

	public void setCustomMetric(boolean isCustomMetric) {
		this.isCustomMetric = isCustomMetric;
	}

	public MetricData() {
	}

	public String[] getData() {
		return data;
	}

	public void setData(String[] data) {
		this.data = data;
	}

	public String getMetricId() {
		return metricId;
	}

	public void setMetricId(String metricId) {
		this.metricId = metricId;
	}

	public long getResourceInstanceId() {
		return resourceInstanceId;
	}

	public void setResourceInstanceId(long resourceInstanceId) {
		this.resourceInstanceId = resourceInstanceId;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public Date getCollectTime() {
		return collectTime;
	}

	public void setCollectTime(Date collectTime) {
		this.collectTime = collectTime;
	}

	public Long getTimelineId() {
		return timelineId;
	}

	public void setTimelineId(Long timelineId) {
		this.timelineId = timelineId;
	}

	public Long getProfileId() {
		return profileId;
	}

	public void setProfileId(Long profileId) {
		this.profileId = profileId;
	}

	public MetricTypeEnum getMetricType() {
		return metricType;
	}

	public void setMetricType(MetricTypeEnum metricType) {
		this.metricType = metricType;
	}
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder(100);
		b.append("[");
		b.append("instanceId=").append(getResourceInstanceId());
		b.append(" resourceId=").append(getResourceId());
		b.append(" metricId=").append(getMetricId());
		b.append(" metricData=").append(getData() != null && getData().length > 0 ? getData()[0]:"");
		b.append(" collectDate=").append(df.format(getCollectTime()));
		b.append("]");
		return b.toString();
	}
}
