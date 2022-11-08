package com.mainsteam.stm.profilelib.obj;

import java.io.Serializable;


/**
 * 基线类
 * @author xiaoruqiang
 */
public class Timeline implements Serializable{
	
	private static final long serialVersionUID = -5200915586408588618L;

	private TimelineInfo timelineInfo;
	
    private MetricSetting metricSetting;
 
	public TimelineInfo getTimelineInfo() {
		return timelineInfo;
	}

	public void setTimelineInfo(TimelineInfo timelineInfo) {
		this.timelineInfo = timelineInfo;
	}

	public MetricSetting getMetricSetting() {
		return metricSetting;
	}

	public void setMetricSetting(MetricSetting metricSetting) {
		this.metricSetting = metricSetting;
	}
}
