package com.mainsteam.stm.profilelib.interceptor.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.profilelib.interceptor.ProfileSwitchChange;
import com.mainsteam.stm.profilelib.obj.ProfileSwitchData;

/**
 * 策略切换通知实现类
 * @author Xiao Ruqiang
 */
public class ProfileSwitchManagerImpl extends DefaultProfileChangeManagerImpl {

	private static final Log logger = LogFactory.getLog(ProfileSwitchManagerImpl.class);
	
	private List<ProfileSwitchChange> profileSwitchChanges;
	
	public ProfileSwitchManagerImpl(){
		profileSwitchChanges = new ArrayList<ProfileSwitchChange>(1);
	}
	
	@Override
	public void register(ProfileSwitchChange profileSwitchChange) {
		profileSwitchChanges.add(profileSwitchChange);
	} 

	
	@Override
	public void doProfileSwitchInterceptor(List<ProfileSwitchData> profileSwitchDatas) {
		for (ProfileSwitchChange profileSwitchChange : profileSwitchChanges) {
			try {
				profileSwitchChange.switchChange(profileSwitchDatas);
			} catch (Exception e) {
				if(logger.isErrorEnabled()){
					logger.error("doInterceptor ProfileSwitchChange：" + profileSwitchChange.getClass().getName(), e);
				}
			}
		}
	}
}
