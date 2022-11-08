package com.mainsteam.stm.profilelib.obj;

import java.io.Serializable;
import java.util.List;


/**
 * 策略类
 * @author xiaoruqiang
 */
public class Profile implements Serializable{
	
	private static final long serialVersionUID = 2824030483878441780L;
	/**
	 * 策略基本信息
	 */
	private ProfileInfo profileInfo;
	/**
	 * 策略与资源实例关系
	 */
	private ProfileInstanceRelation profileInstanceRelations;

	private MetricSetting metricSetting;

	private List<Profile> children;
	
	private List<Timeline> timelines;
	
	public MetricSetting getMetricSetting() {
		return metricSetting;
	}

	public void setMetricSetting(MetricSetting metricSetting) {
		this.metricSetting = metricSetting;
	}

	
	public void setProfileInstanceRelations(
			ProfileInstanceRelation profileInstanceRelations) {
		this.profileInstanceRelations = profileInstanceRelations;
	}

	public ProfileInfo getProfileInfo() {
		return profileInfo;
	}

	public void setProfileInfo(ProfileInfo profileInfo) {
		this.profileInfo = profileInfo;
	}

	public List<Timeline> getTimeline() {
		return timelines;
	}

	public void setTimeline(List<Timeline> timelines) {
		this.timelines = timelines;
	}
	
	public ProfileInstanceRelation getProfileInstanceRelations() {
		return profileInstanceRelations;
	}

	public List<Profile> getChildren() {
		return children;
	}

	public void setChildren(List<Profile> children) {
		this.children = children;
	}
}
