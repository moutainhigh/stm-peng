package com.mainsteam.stm.portal.resource.web.vo;

import java.io.Serializable;

import com.mainsteam.stm.profilelib.obj.ProfileInfo;

public class ProfileInfoVo extends ProfileInfo implements Serializable{
	
	private static final long serialVersionUID = -4719579029706038111L;

	private long profileParentId;
	
	private int profileTypeMapping;
	
	private String resources;
	
	private String reduceResources;
	
	public long getProfileParentId() {
		return profileParentId;
	}

	public void setProfileParentId(long profileParentId) {
		this.profileParentId = profileParentId;
	}

	public int getProfileTypeMapping() {
		return profileTypeMapping;
	}

	public void setProfileTypeMapping(int profileTypeMapping) {
		this.profileTypeMapping = profileTypeMapping;
	}

	public String getResources() {
		return resources;
	}

	public void setResources(String resources) {
		this.resources = resources;
	}

	public String getReduceResources() {
		return reduceResources;
	}

	public void setReduceResources(String reduceResources) {
		this.reduceResources = reduceResources;
	}
	
	
	
}
