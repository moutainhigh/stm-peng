package com.mainsteam.stm.auto.rediscover.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.mainsteam.stm.auto.rediscover.dao.ProfileAutoRediscoverDao;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.profilelib.ProfileAutoRediscoverService;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.ProfilelibAutoRediscover;
import com.mainsteam.stm.profilelib.obj.ProfilelibAutoRediscoverInstance;

public class ProfileAutoRediscoverServiceImpl implements ProfileAutoRediscoverService {

	private static Log logger = LogFactory.getLog(ProfileAutoRediscoverServiceImpl.class);
	private ProfileAutoRediscoverDao profileAutoRediscoveryDao;
	private ISequence ocProfileAutoRediscoverSequence;
	private ISequence ocProfileAutoRediscoverInstanceSequence;
	private int jobExecuteTime;

	public void setProfileAutoRediscoveryDao(ProfileAutoRediscoverDao profileAutoRediscoveryDao) {
		this.profileAutoRediscoveryDao = profileAutoRediscoveryDao;
	}

	public void setOcProfileAutoRediscoverInstanceSequence(ISequence ocProfileAutoRediscoverInstanceSequence) {
		this.ocProfileAutoRediscoverInstanceSequence = ocProfileAutoRediscoverInstanceSequence;
	}

	public void setOcProfileAutoRediscoverSequence(ISequence ocProfileAutoRediscoverSequence) {
		this.ocProfileAutoRediscoverSequence = ocProfileAutoRediscoverSequence;
	}

	public void setJobExecuteTime(int jobExecuteTime) {
		this.jobExecuteTime = jobExecuteTime;
	}
	
	@Override
	public void addAutoRediscoverProfileBaseInfo(ProfilelibAutoRediscover autoRediscover) {
		if (null != autoRediscover) {
			long profileId = ocProfileAutoRediscoverSequence.next();
			autoRediscover.setId(profileId);
			autoRediscover.setCreateTime(Calendar.getInstance().getTime());
			profileAutoRediscoveryDao.addProfileAutoRediscover(autoRediscover);
			List<ProfilelibAutoRediscoverInstance> profileInstanceRel = autoRediscover.getProfileInstanceRel();
			if (!CollectionUtils.isEmpty(profileInstanceRel)) {
				for (ProfilelibAutoRediscoverInstance profilelibAutoRediscoverInstance : profileInstanceRel) {
					profilelibAutoRediscoverInstance.setProfileId(profileId);
				}
				addAutoRediscoverProfileInstance(profileInstanceRel);
			}
		}
	}

	@Override
	public void addAutoRediscoverProfileInstance(List<ProfilelibAutoRediscoverInstance> autoRediscoverInstances) {
		if (!CollectionUtils.isEmpty(autoRediscoverInstances)) {
			List<Long> delete = new ArrayList<Long>();
			for (ProfilelibAutoRediscoverInstance profilelibAutoRediscoverInstance : autoRediscoverInstances) {
				ProfilelibAutoRediscoverInstance instance = getProfilelibAutoRediscoverInstanceByInstanceId(profilelibAutoRediscoverInstance.getInstanceId());
				ProfilelibAutoRediscover rediscover = getAutoRediscoverProfile(profilelibAutoRediscoverInstance.getProfileId());
				Calendar calendar = getExecuteCalendar();
				calendar.add(Calendar.DATE, rediscover.getExecutRepeat());
				Date nextExecuteTime = new Date(calendar.getTimeInMillis());//
				if(instance!=null){
					delete.add(profilelibAutoRediscoverInstance.getInstanceId());
				}
				profilelibAutoRediscoverInstance.setId(ocProfileAutoRediscoverInstanceSequence.next());
				profilelibAutoRediscoverInstance.setNextExecuteTime(nextExecuteTime);
			}
			deleteAutoRediscoverProfileInstanceByInstanceId(delete);
			profileAutoRediscoveryDao.addProfileAutoRediscoverInstance(autoRediscoverInstances);
		}
	}

	@Override
	public void deleteAutoRediscoverProfile(long profileId) {
		deleteAutoRediscoverProfileInstanceByProfileId(profileId);
		profileAutoRediscoveryDao.deleteProfileAutoRediscover(profileId);
	}

	@Override
	public void deleteAutoRediscoverProfile(List<Long> profileIds) {
		deleteAutoRediscoverProfileInstanceByProfileId(profileIds);
		profileAutoRediscoveryDao.deleteProfileAutoRediscover(profileIds);

	}

	@Override
	public void updateAutoRediscoverProfileBaseInfo(ProfilelibAutoRediscover autoRediscoverProfile) {
		if (null != autoRediscoverProfile) {
			autoRediscoverProfile.setUpdateTime(Calendar.getInstance().getTime());
			profileAutoRediscoveryDao.updateProfileAutoRediscover(autoRediscoverProfile);
			List<ProfilelibAutoRediscoverInstance> rediscoverInstances = queryAutoRediscoverProfileInstance(autoRediscoverProfile.getId());
			if(!CollectionUtils.isEmpty(rediscoverInstances)){
				for (ProfilelibAutoRediscoverInstance instance : rediscoverInstances) {
					Calendar calendar = null;//
					if(null!=instance.getExecuteTime()){
						calendar = Calendar.getInstance();
						calendar.setTime(instance.getExecuteTime());
					}else{
						calendar = getExecuteCalendar();
					}
					calendar.add(Calendar.DATE, autoRediscoverProfile.getExecutRepeat());
					Date nextExecuteTime = new Date(calendar.getTimeInMillis());//
					instance.setNextExecuteTime(nextExecuteTime);
					updateProfileInstanceExecuteTime(instance);
				}
			}
		}
	}

	@Override
	public void updateAutoRediscoverProfileUsedState(long userId, long profileId) {
		ProfilelibAutoRediscover rediscover = getAutoRediscoverProfile(profileId);
		if (rediscover != null) {
			int isUsed = rediscover.getIsUse();
			rediscover.setIsUse(isUsed == 1 ? 0 : 1);
			rediscover.setUpdateUser(userId);
			updateAutoRediscoverProfileBaseInfo(rediscover);
		}
	}

	@Override
	public List<ProfilelibAutoRediscover> queryAllAutoRediscoverProfile() {
		List<ProfilelibAutoRediscover> profiles = profileAutoRediscoveryDao.getAllProfileAutoRediscover();
		if (!CollectionUtils.isEmpty(profiles)) {
			for (ProfilelibAutoRediscover profilelibAutoRediscover : profiles) {
				List<ProfilelibAutoRediscoverInstance> profileInstanceRel = queryAutoRediscoverProfileInstance(profilelibAutoRediscover.getId());
				profilelibAutoRediscover.setProfileInstanceRel(profileInstanceRel);
			}
		}
		return profiles;
	}

	@Override
	public List<ProfilelibAutoRediscover> queryAllAutoRediscoverProfileByUsed() {
		List<ProfilelibAutoRediscover> profiles = profileAutoRediscoveryDao.getUsedProfileAutoRediscover();
		if (!CollectionUtils.isEmpty(profiles)) {
			for (ProfilelibAutoRediscover profilelibAutoRediscover : profiles) {
				List<ProfilelibAutoRediscoverInstance> profileInstanceRel = queryAutoRediscoverProfileInstance(profilelibAutoRediscover.getId());
				profilelibAutoRediscover.setProfileInstanceRel(profileInstanceRel);
			}
		}
		return profiles;
	}

	@Override
	public List<ProfilelibAutoRediscoverInstance> queryAutoRediscoverProfileInstance(long profileId) {
		return profileAutoRediscoveryDao.getAutoRediscoverProfileInstanceByProfileId(profileId);
	}

	@Override
	public void deleteAutoRediscoverProfileInstanceByInstanceId(long instanceId) {
		profileAutoRediscoveryDao.deleteProfileAutoRediscoverInstanceByInstanceId(instanceId);
	}

	@Override
	public void deleteAutoRediscoverProfileInstanceByInstanceId(List<Long> instanceIds) {
		profileAutoRediscoveryDao.deleteProfileAutoRediscoverInstanceByInstanceId(instanceIds);
	}

	@Override
	public void deleteAutoRediscoverProfileInstanceByProfileId(long profileId) {
		profileAutoRediscoveryDao.deleteProfileAutoRediscoverInstanceByProfileId(profileId);
	}

	@Override
	public void deleteAutoRediscoverProfileInstanceByProfileId(List<Long> profileIds) {
		if (!CollectionUtils.isEmpty(profileIds)) {
			profileAutoRediscoveryDao.deleteProfileAutoRediscoverInstanceByProfileId(profileIds);
		}
	}

	@Override
	public ProfilelibAutoRediscover getAutoRediscoverProfile(long profileId) {
		ProfilelibAutoRediscover rediscover = profileAutoRediscoveryDao.getProfileAutoRediscover(profileId);
		if (rediscover != null) {
			List<ProfilelibAutoRediscoverInstance> profileInstanceRel = queryAutoRediscoverProfileInstance(profileId);
			rediscover.setProfileInstanceRel(profileInstanceRel);
		}
		return rediscover;
	}

	@Override
	public void updateProfileInstanceExecuteTime(ProfilelibAutoRediscoverInstance profilelibAutoRediscoverInstance) {
		profileAutoRediscoveryDao.updateProfileInstanceExecuteTime(profilelibAutoRediscoverInstance);
	}
	
	@Override
	public void updateProfileInstanceExecuteTime(List<ProfilelibAutoRediscoverInstance> profilelibAutoRediscoverInstances) {
		if (!CollectionUtils.isEmpty(profilelibAutoRediscoverInstances)) {
			profileAutoRediscoveryDao.updateProfileInstanceExecuteTime(profilelibAutoRediscoverInstances);
		}
	}

	@Override
	public int countProfileInstanceSize() {
		return profileAutoRediscoveryDao.getProfileInstanceCount();
	}

	@Override
	public ProfilelibAutoRediscover getProfilelibAutoRediscoverByInstance(long instanceId) {
		ProfilelibAutoRediscoverInstance pi = getProfilelibAutoRediscoverInstanceByInstanceId(instanceId);
		if(null!=pi){
			return getAutoRediscoverProfile(pi.getProfileId());
		}
		return null;
	}

	@Override
	public ProfilelibAutoRediscoverInstance getProfilelibAutoRediscoverInstanceByInstanceId(long instanceId) {
		return profileAutoRediscoveryDao.getProfilelibAutoRediscoverInstanceByInstanceId(instanceId);
	}

	@Override
	public void updateProfileAutoRediscoverInstanceRelByProfile(long profileId, List<ProfilelibAutoRediscoverInstance> instanceRel) {
		ProfilelibAutoRediscover rediscover = getAutoRediscoverProfile(profileId);
		List<ProfilelibAutoRediscoverInstance> oldRels = rediscover.getProfileInstanceRel();
		List<Long> delInstanceIds = new ArrayList<Long>();
		List<ProfilelibAutoRediscoverInstance> addRels = new ArrayList<ProfilelibAutoRediscoverInstance>();
		List<ProfilelibAutoRediscoverInstance> updateRels = new ArrayList<ProfilelibAutoRediscoverInstance>();
		if(!CollectionUtils.isEmpty(oldRels)){
			for (ProfilelibAutoRediscoverInstance oldRel : oldRels) {
				boolean flag = false;
				if(!CollectionUtils.isEmpty(instanceRel)){
					for (ProfilelibAutoRediscoverInstance newRel : instanceRel) {
						if(oldRel.getInstanceId()==newRel.getInstanceId()){
							flag = true;
							break;
						}
					}
				}
				if(!flag){
					delInstanceIds.add(oldRel.getInstanceId());
				}else{
					updateRels.add(oldRel);
				}
			}
		}
		if(!CollectionUtils.isEmpty(instanceRel)){
			for (ProfilelibAutoRediscoverInstance newRel : instanceRel) {
				boolean flag = false;
				if(!CollectionUtils.isEmpty(oldRels)){
					for (ProfilelibAutoRediscoverInstance oldRel : oldRels) {
						if(oldRel.getInstanceId()==newRel.getInstanceId()){
							flag = true;
						}
					}
				}
				if(!flag){
					addRels.add(newRel);
				}
			}
		}
		profileAutoRediscoveryDao.deleteProfileAutoRediscoverInstanceByInstanceId(delInstanceIds);
		if(!CollectionUtils.isEmpty(updateRels)){
			for (ProfilelibAutoRediscoverInstance updateRel : updateRels) {
				Calendar calendar=null;
				if(updateRel.getExecuteTime()!=null){
					calendar = Calendar.getInstance();
					calendar.setTime(updateRel.getExecuteTime());
				}else{
					calendar = getExecuteCalendar();
				}
				calendar.add(Calendar.DATE, rediscover.getExecutRepeat());
				updateRel.setNextExecuteTime(calendar.getTime());
				profileAutoRediscoveryDao.updateProfileInstanceExecuteTime(updateRel);
			}
		}
		if(!CollectionUtils.isEmpty(addRels)){
			addAutoRediscoverProfileInstance(addRels);
		}
	}
	
	private Calendar getExecuteCalendar(){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, jobExecuteTime);
		calendar.set(Calendar.MINUTE,0);
		calendar.set(Calendar.SECOND,0);
		return calendar;
	}

	@Override
	public boolean checkProfileNameIsExist(String name) throws ProfilelibException {
		if(!StringUtils.isEmpty(name)){
			boolean flag = false;
			List<ProfilelibAutoRediscover> profiles = queryAllAutoRediscoverProfile();
			for (ProfilelibAutoRediscover profilelibAutoRediscover : profiles) {
				if(profilelibAutoRediscover.getProfileName().equals(name)){
					flag = true;
					break;
				}
			}
			return flag;
		}else{
			throw new ProfilelibException("验证的策略名称为空，无法验证！",null);
		}
	}
	
	public void init(){
		if(logger.isInfoEnabled()){
			logger.info("ProfileAutoRediscoverServiceImpl init start,check jobExecute config time!");
		}
		List<ProfilelibAutoRediscover> rediscovers = queryAllAutoRediscoverProfile();
		if(!CollectionUtils.isEmpty(rediscovers)){
			List<ProfilelibAutoRediscoverInstance> updateRel = new ArrayList<ProfilelibAutoRediscoverInstance>();
			for (ProfilelibAutoRediscover rediscover : rediscovers) {
				List<ProfilelibAutoRediscoverInstance> rel = rediscover.getProfileInstanceRel();
				if(null!=rediscover && !CollectionUtils.isEmpty(rel)){
					for (ProfilelibAutoRediscoverInstance piRel : rel) {
						if(null!=piRel){
							Calendar nextExecuteCalendat = Calendar.getInstance();
							nextExecuteCalendat.setTime(piRel.getNextExecuteTime());
							if(nextExecuteCalendat.get(Calendar.HOUR_OF_DAY)!=jobExecuteTime){
								nextExecuteCalendat.set(Calendar.HOUR_OF_DAY, jobExecuteTime);
								piRel.setNextExecuteTime(nextExecuteCalendat.getTime());
								updateRel.add(piRel);
							}
						}
					}
				}
			}
			if(!CollectionUtils.isEmpty(updateRel)){
				updateProfileInstanceExecuteTime(updateRel);
			}
		}
	}
}
