package com.mainsteam.stm.portal.resource.bo;

import java.io.Serializable;
import java.util.List;

import com.mainsteam.stm.profilelib.obj.ProfileInfo;
import com.mainsteam.stm.profilelib.obj.ProfileInstanceRelation;
import com.mainsteam.stm.profilelib.obj.Timeline;

public class ProfileBo implements Serializable {

	private static final long serialVersionUID = 3936089493842404950L;
	private ProfileInfoBo profileInfo;
	private ProfileInstanceRelation profileInstanceRelations;
	private MetricSettingBo metricSetting;
	private List<ProfileBo> children;
	private List<Timeline> timelines;
	private int childProfileMaxCount;
	private long personalize_instanceDomainId;
	
	public long getPersonalize_instanceDomainId() {
		return personalize_instanceDomainId;
	}

	public void setPersonalize_instanceDomainId(long personalize_instanceDomainId) {
		this.personalize_instanceDomainId = personalize_instanceDomainId;
	}

	public int getChildProfileMaxCount() {
		return childProfileMaxCount;
	}

	public void setChildProfileMaxCount(int childProfileMaxCount) {
		this.childProfileMaxCount = childProfileMaxCount;
	}

	public ProfileInfo getProfileInfo() {
		return profileInfo;
	}

	public void setProfileInfo(ProfileInfoBo profileInfo) {
		this.profileInfo = profileInfo;
	}

	public ProfileInstanceRelation getProfileInstanceRelations() {
		return profileInstanceRelations;
	}

	public void setProfileInstanceRelations(
			ProfileInstanceRelation profileInstanceRelations) {
		this.profileInstanceRelations = profileInstanceRelations;
	}

	public MetricSettingBo getMetricSetting() {
		return metricSetting;
	}

	public void setMetricSetting(MetricSettingBo metricSetting) {
		this.metricSetting = metricSetting;
	}

	public List<ProfileBo> getChildren() {
		return children;
	}

	public void setChildren(List<ProfileBo> children) {
		this.children = children;
	}

	public List<Timeline> getTimelines() {
		return timelines;
	}

	public void setTimelines(List<Timeline> timelines) {
		this.timelines = timelines;
	}

}
