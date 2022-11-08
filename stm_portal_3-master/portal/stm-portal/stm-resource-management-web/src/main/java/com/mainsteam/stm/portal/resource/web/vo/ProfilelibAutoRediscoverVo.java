package com.mainsteam.stm.portal.resource.web.vo;

import java.io.Serializable;

import com.mainsteam.stm.profilelib.obj.ProfilelibAutoRediscover;

public class ProfilelibAutoRediscoverVo extends ProfilelibAutoRediscover implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1305058474425596410L;
	private String createUserName;
	private String updateUserName;
	public String getCreateUserName() {
		return createUserName;
	}
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}
	public String getUpdateUserName() {
		return updateUserName;
	}
	public void setUpdateUserName(String updateUserName) {
		this.updateUserName = updateUserName;
	}
	
	
	
}
