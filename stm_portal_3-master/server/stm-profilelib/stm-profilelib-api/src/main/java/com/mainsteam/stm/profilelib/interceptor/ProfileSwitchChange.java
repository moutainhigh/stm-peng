package com.mainsteam.stm.profilelib.interceptor;

import java.util.List;

import com.mainsteam.stm.profilelib.obj.ProfileSwitchData;

public interface ProfileSwitchChange {

	public void switchChange(List<ProfileSwitchData> profileSwitchDatas) throws Exception;
	
}
