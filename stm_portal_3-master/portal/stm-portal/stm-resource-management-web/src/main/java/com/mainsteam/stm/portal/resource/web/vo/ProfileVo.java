package com.mainsteam.stm.portal.resource.web.vo;

import java.io.Serializable;
import java.util.List;

import com.mainsteam.stm.profilelib.obj.Profile;
import com.mainsteam.stm.profilelib.obj.ProfileInfo;
import com.mainsteam.stm.profilelib.obj.ProfileInstanceRelation;
import com.mainsteam.stm.profilelib.obj.Timeline;

public class ProfileVo implements Serializable {

	private static final long serialVersionUID = 3957455812743305425L;
	private ProfileInfo profileInfo;
	private ProfileInstanceRelation profileInstanceRelations;
	private MetricSettingVo metricSetting;
	private List<Profile> chirldren;
	private List<Timeline> timelines;

	public ProfileInfo getProfileInfo() {
		return profileInfo;
	}

	public void setProfileInfo(ProfileInfo profileInfo) {
		this.profileInfo = profileInfo;
	}

	public ProfileInstanceRelation getProfileInstanceRelations() {
		return profileInstanceRelations;
	}

	public void setProfileInstanceRelations(
			ProfileInstanceRelation profileInstanceRelations) {
		this.profileInstanceRelations = profileInstanceRelations;
	}

	public MetricSettingVo getMetricSetting() {
		return metricSetting;
	}

	public void setMetricSetting(MetricSettingVo metricSetting) {
		this.metricSetting = metricSetting;
	}

	public List<Profile> getChirldren() {
		return chirldren;
	}

	public void setChirldren(List<Profile> chirldren) {
		this.chirldren = chirldren;
	}

	public List<Timeline> getTimelines() {
		return timelines;
	}

	public void setTimelines(List<Timeline> timelines) {
		this.timelines = timelines;
	}

}
