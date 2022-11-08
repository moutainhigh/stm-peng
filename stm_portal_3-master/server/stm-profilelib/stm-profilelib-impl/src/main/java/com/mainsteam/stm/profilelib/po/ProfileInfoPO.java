package com.mainsteam.stm.profilelib.po;

import java.util.Date;

public class ProfileInfoPO {

	private long profileId;
	private String profileName;
	private String profileDesc;
	private String isUse;
	private String resourceId;
	private Long parentProfileId;
	private String profileType;
	private long copyProfileId;
	private String updateUser;
	private String updateUserDomain;
	private Date updateTime;
	private long domainId;
	private long resourceInstanceId;
	private long createUser;
	
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

	public long getCopyProfileId() {
		return copyProfileId;
	}

	public void setCopyProfileId(long copyProfileId) {
		this.copyProfileId = copyProfileId;
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
	
	public String getIsUse() {
		return isUse;
	}

	public Long getParentProfileId() {
		return parentProfileId;
	}

	public void setIsUse(String isUse) {
		this.isUse = isUse;
	}

	public void setParentProfileId(Long parentProfileId) {
		this.parentProfileId = parentProfileId;
	}
	
	public String getProfileType() {
		return profileType;
	}

	public void setProfileType(String profileType) {
		this.profileType = profileType;
	}

	public long getDomainId() {
		return domainId;
	}

	public void setDomainId(long domainId) {
		this.domainId = domainId;
	}

	public long getResourceInstanceId() {
		return resourceInstanceId;
	}

	public void setResourceInstanceId(long resourceInstanceId) {
		this.resourceInstanceId = resourceInstanceId;
	}

	public long getCreateUser() {
		return createUser;
	}

	public void setCreateUser(long createUser) {
		this.createUser = createUser;
	}
}
