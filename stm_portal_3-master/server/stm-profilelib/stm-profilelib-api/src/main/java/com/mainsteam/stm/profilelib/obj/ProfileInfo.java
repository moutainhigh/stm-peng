package com.mainsteam.stm.profilelib.obj;

import java.io.Serializable;
import java.util.Date;

import com.mainsteam.stm.profilelib.objenum.ProfileTypeEnum;

public class ProfileInfo implements Serializable{

	private static final long serialVersionUID = 9010719213021562547L;
	private long profileId;
	private long parentProfileId;
	private String profileName;
	private String profileDesc;
	private String resourceId;
	private String updateUser;
	private String updateUserDomain;
	private ProfileTypeEnum profileType;
	private Date updateTime;
	private long domainId;
	private boolean haveTimeline;
	private long personalize_instanceId;
	private long createUser;
	/**
	 * 是否使用策略(针对个性化策略)
	 */
	private boolean isUse = true;
	

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

	public String getProfileDesc() {
		return profileDesc;
	}

	public void setProfileDesc(String profileDesc) {
		this.profileDesc = profileDesc;
	}

	

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public String getUpdateUserDomain() {
		return updateUserDomain;
	}

	public void setUpdateUserDomain(String updateUserDomain) {
		this.updateUserDomain = updateUserDomain;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}


	public boolean isHaveTimeline() {
		return haveTimeline;
	}

	public void setHaveTimeline(boolean haveTimeline) {
		this.haveTimeline = haveTimeline;
	}
	
	public ProfileTypeEnum getProfileType() {
		return profileType;
	}

	public void setProfileType(ProfileTypeEnum profileType) {
		this.profileType = profileType;
	}

	public long getDomainId() {
		return domainId;
	}

	public void setDomainId(long domainId) {
		this.domainId = domainId;
	}

	public boolean isUse() {
		return isUse;
	}

	public void setUse(boolean isUse) {
		this.isUse = isUse;
	}
	
	public long getParentProfileId() {
		return parentProfileId;
	}

	public void setParentProfileId(long parentProfileId) {
		this.parentProfileId = parentProfileId;
	}

	public long getPersonalize_instanceId() {
		return personalize_instanceId;
	}

	public void setPersonalize_instanceId(long personalize_instanceId) {
		this.personalize_instanceId = personalize_instanceId;
	}

	public long getCreateUser() {
		return createUser;
	}

	public void setCreateUser(long createUser) {
		this.createUser = createUser;
	}

}
