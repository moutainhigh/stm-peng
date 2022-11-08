package com.mainsteam.stm.profilelib.obj;

import java.io.Serializable;

/**
 * 策略切换只会设计到父资源操作。
 * 定义一个资源用一个profile绑定，通知给状态计算模块计算资源状态
 * @author xiaoruqiang
 */
public class ProfileSwitchData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 12341434351546L;

	private Profile profile;

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}
	
}
