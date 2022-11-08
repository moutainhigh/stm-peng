package com.mainsteam.stm.dataprocess;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用来计算的指标数据
 * 
 * @author ziw
 * 
 */
public class MetricCalculateData  {
	
	private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
	
	/**
	 * 指标数据
	 */
	private String[] metricData;

	/**
	 * 指标id
	 */
	private String metricId;
	
	/**
	 * 资源实例id
	 */
	private long resourceInstanceId;
	
	/**
	 * 资源id
	 */
	private String resourceId;
	
	/**
	 * 采集时间
	 */
	private Date collectTime;
	
	
	private List<Object> metricRelationData;
	
	/**
	 * 使用的策略id
	 */
	private long profileId = -1;
	
	/**
	 * 使用的基线id
	 */
	private long timelineId = -1;
	
	/**
	 * 是否是自定义指标
	 */
	private boolean customMetric;
	
	private boolean delay = false;
	
	//该指标来源。除链路不一样，其余跟resourceInstanceId 一样。
	private long fromInstanceId;
	
	public long getFromInstanceId() {
		return fromInstanceId;
	}


	public void setFromInstanceId(long fromInstanceId) {
		this.fromInstanceId = fromInstanceId;
	}


	/**
	 * 
	 */
	public MetricCalculateData() {
		metricRelationData = new ArrayList<>();
	}
	
	
	public boolean isDelay() {
		return delay;
	}


	public void setDelay(boolean delay) {
		this.delay = delay;
	}


	/**
	 * @return the customMetric
	 */
	public final boolean isCustomMetric() {
		return customMetric;
	}



	/**
	 * @param customMetric the customMetric to set
	 */
	public final void setCustomMetric(boolean customMetric) {
		this.customMetric = customMetric;
	}



	public MetricCalculateData(String[] metricData, String metricId,
			long resourceInstanceId, Date collectTime,String resourceId) {
		super();
		this.metricData = metricData;
		this.metricId = metricId;
		this.resourceInstanceId = resourceInstanceId;
		this.collectTime = collectTime;
		this.resourceId = resourceId;
		this.fromInstanceId = resourceInstanceId;
	}
	public void setMetricData(String[] metricData) {
		this.metricData = metricData;
	}

	public void setMetricId(String metricId) {
		this.metricId = metricId;
	}

	public void setResourceInstanceId(long resourceInstanceId) {
		this.resourceInstanceId = resourceInstanceId;
		this.fromInstanceId = resourceInstanceId;
	}

	public void setCollectTime(Date collectTime) {
		this.collectTime = collectTime;
	}

	public void setMetricRelationData(List<Object> metricRelationData) {
		this.metricRelationData = metricRelationData;
	}

	public void appendMetricRelationData(Object data){
		this.metricRelationData.add(data);
	}

	public List<Object> getMetricRelationData() {
		return metricRelationData;
	}

	public String[] getMetricData() {
		return metricData;
	}

	public String getMetricId() {
		return metricId;
	}

	public long getResourceInstanceId() {
		return resourceInstanceId;
	}

	public Date getCollectTime() {
		return collectTime;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	/**
	 * @return the profileId
	 */
	public final long getProfileId() {
		return profileId;
	}

	/**
	 * @param profileId the profileId to set
	 */
	public final void setProfileId(long profileId) {
		this.profileId = profileId;
	}

	/**
	 * @return the timelineId
	 */
	public final long getTimelineId() {
		return timelineId;
	}

	/**
	 * @param timelineId the timelineId to set
	 */
	public final void setTimelineId(long timelineId) {
		this.timelineId = timelineId;
	}
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder(10000);
		b.append("[");
		b.append(" delay=").append(isDelay());
		b.append(" instanceId=").append(getResourceInstanceId());
		b.append(" resourceId=").append(getResourceId());
		b.append(" metricId=").append(getMetricId());
		b.append(" metricValue=").append(getMetricData()!= null?getMetricData()[0]:"");
		b.append(" collectDate=").append(df.format(getCollectTime()));
		b.append("]");
		return b.toString();
	}
	
}
