package com.mainsteam.stm.dataprocess.bigData.dao.pojo;

public class ProfileInstanceDO {

	private long profileId;
	private long instanceId;
	private Long parentInstanceId;
	
	public ProfileInstanceDO() {
	}

	public long getProfileId() {
		return profileId;
	}

	public void setProfileId(long profileId) {
		this.profileId = profileId;
	}

	public long getInstanceId() {
		return instanceId;
	}

	public Long getParentInstanceId() {
		return parentInstanceId;
	}

	public void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
	}

	public void setParentInstanceId(Long parentInstanceId) {
		this.parentInstanceId = parentInstanceId;
	}
	
}
