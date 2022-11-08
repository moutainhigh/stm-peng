package com.mainsteam.stm.portal.extendedplatform.resourceprofile.po;

import java.io.Serializable;

public class ProfileInstancePo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2200520449877880826L;
	
	private long instanceId;
	private long parentInstanceId;
	private long profileId;
	private String profileName;
	public long getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
	}
	public long getParentInstanceId() {
		return parentInstanceId;
	}
	public void setParentInstanceId(long parentInstanceId) {
		this.parentInstanceId = parentInstanceId;
	}
	public long getProfileId() {
		return profileId;
	}
	public void setProfileId(long profileId) {
		this.profileId = profileId;
	}
	public String getProfileName() {
		return profileName;
	}
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	
	
}
