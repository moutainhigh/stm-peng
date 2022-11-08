package com.mainsteam.stm.portal.resource.bo;

import java.io.Serializable;

import com.mainsteam.stm.profilelib.obj.ProfileInfo;

public class ProfileInfoBo extends ProfileInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 763907798608937420L;
	
	private String profileResourceType;

	public String getProfileResourceType() {
		return profileResourceType;
	}

	public void setProfileResourceType(String profileResourceType) {
		this.profileResourceType = profileResourceType;
	}
	
	

}
