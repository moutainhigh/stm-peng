package com.mainsteam.stm.auto.rediscover.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import com.mainsteam.stm.auto.rediscover.dao.ProfileAutoRefreshDao;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.profilelib.ProfileAutoRefreshService;
import com.mainsteam.stm.profilelib.obj.ProfileAutoRefresh;

public class ProfileAutoRefreshServiceImpl implements ProfileAutoRefreshService {
	private Log logger = LogFactory.getLog(ProfileAutoRefreshServiceImpl.class);
	private ProfileAutoRefreshDao profileAutoRefreshDao;
	private ISequence ocProfileAutoRefreshSequence;
	private int jobExecuteTime;
	
	public void setProfileAutoRefreshDao(ProfileAutoRefreshDao profileAutoRefreshDao) {
		this.profileAutoRefreshDao = profileAutoRefreshDao;
	}
	public void setOcProfileAutoRefreshSequence(ISequence ocProfileAutoRefreshSequence) {
		this.ocProfileAutoRefreshSequence = ocProfileAutoRefreshSequence;
	}
	public void setJobExecuteTime(int jobExecuteTime) {
		this.jobExecuteTime = jobExecuteTime;
	}
	
	@Override
	public void addAutoRefreshProfile(ProfileAutoRefresh autoRefresh) {
		ProfileAutoRefresh oldRefresh = getAutoRefreshProfileByInstance(autoRefresh.getInstanceId());
		Calendar calendar = getExecuteCalendar();
		calendar.add(Calendar.DATE, autoRefresh.getExecutRepeat());
		Date nexeExecuteTime = new Date(calendar.getTimeInMillis());
		if(oldRefresh!=null){
			oldRefresh.setIsUse(1);
			oldRefresh.setNextExecuteTime(nexeExecuteTime);
			oldRefresh.setExecutRepeat(autoRefresh.getExecutRepeat());
			updateAutoRefreshProfile(oldRefresh);
		}else{
			autoRefresh.setId(ocProfileAutoRefreshSequence.next());
			autoRefresh.setNextExecuteTime(nexeExecuteTime);
			autoRefresh.setIsUse(1);
			profileAutoRefreshDao.addAutoRefreshProfile(autoRefresh);
		}
	}

	@Override
	public void addAutoRefreshProfile(List<ProfileAutoRefresh> autoRefreshs) {
		long profileId = ocProfileAutoRefreshSequence.next();
		List<ProfileAutoRefresh> update = new ArrayList<ProfileAutoRefresh>();
		List<ProfileAutoRefresh> add = new ArrayList<ProfileAutoRefresh>();
		for (ProfileAutoRefresh profileAutoRefresh : autoRefreshs) {
			Calendar calendar =getExecuteCalendar();
			calendar.add(Calendar.DATE, profileAutoRefresh.getExecutRepeat());
			Date nexeExecuteTime = new Date(calendar.getTimeInMillis());
			ProfileAutoRefresh refresh = getAutoRefreshProfileByInstance(profileAutoRefresh.getInstanceId());
			if(refresh!=null){
				refresh.setIsUse(1);
				refresh.setNextExecuteTime(nexeExecuteTime);
				refresh.setExecutRepeat(profileAutoRefresh.getExecutRepeat());
				update.add(refresh);
			}else{
				profileAutoRefresh.setIsUse(1);
				profileAutoRefresh.setNextExecuteTime(nexeExecuteTime);
				profileAutoRefresh.setId(profileId);
				add.add(profileAutoRefresh);
			}
			
		}
		
		if(!CollectionUtils.isEmpty(update)){
			updateAutoRefreshProfile(update);
		}
		if(!CollectionUtils.isEmpty(add)){
			profileAutoRefreshDao.addAutoRefreshProfile(add);
		}
	}

	@Override
	public void removeAutoRefreshProfile(long instanceId) {
		ProfileAutoRefresh autoRefresh = getAutoRefreshProfileByInstance(instanceId);
		if(autoRefresh!=null && autoRefresh.getIsUse()==1){
			autoRefresh.setIsUse(0);
			updateAutoRefreshProfile(autoRefresh);
		}
	}

	@Override
	public void removeAutoRefreshProfile(List<Long> instanceIds) {
		profileAutoRefreshDao.removeAutoRefreshProfile(instanceIds);
	}

	@Override
	public void autoUpdateAutoRefreshProfile(ProfileAutoRefresh autoRefresh) {
		ProfileAutoRefresh oldAutoRefresh = profileAutoRefreshDao.getAutoRefreshProfileByInstance(autoRefresh.getInstanceId());
		if(oldAutoRefresh!=null){
			oldAutoRefresh.setNextExecuteTime(autoRefresh.getNextExecuteTime());
			oldAutoRefresh.setExecuteTime(autoRefresh.getExecuteTime());
			oldAutoRefresh.setExecutRepeat(autoRefresh.getExecutRepeat());
			profileAutoRefreshDao.updateAutoRefreshProfile(oldAutoRefresh);
		}else{
			addAutoRefreshProfile(autoRefresh);
		}
	}
	
	@Override
	public void updateAutoRefreshProfile(ProfileAutoRefresh autoRefresh) {
		ProfileAutoRefresh oldAutoRefresh = profileAutoRefreshDao.getAutoRefreshProfileByInstance(autoRefresh.getInstanceId());
		if(oldAutoRefresh!=null){
			Date nextExecuteTime = null;
			Calendar calendar=null;
			if(null!=oldAutoRefresh.getExecuteTime()){
				calendar = Calendar.getInstance();
				calendar.setTime(oldAutoRefresh.getExecuteTime());
			}else{
				calendar = getExecuteCalendar();
			}
			calendar.add(Calendar.DATE, autoRefresh.getExecutRepeat());
			nextExecuteTime = new Date(calendar.getTimeInMillis());
			oldAutoRefresh.setNextExecuteTime(nextExecuteTime);
			oldAutoRefresh.setExecuteTime(autoRefresh.getExecuteTime());
			oldAutoRefresh.setExecutRepeat(autoRefresh.getExecutRepeat());
			oldAutoRefresh.setIsUse(autoRefresh.getIsUse());
			profileAutoRefreshDao.updateAutoRefreshProfile(oldAutoRefresh);
		}else{
			addAutoRefreshProfile(autoRefresh);
		}
	}

	@Override
	public void updateAutoRefreshProfile(List<ProfileAutoRefresh> autoRefreshs) {
		List<ProfileAutoRefresh> updateRefreshs = new ArrayList<>();
		List<ProfileAutoRefresh> addRefreshs = new ArrayList<>();
		for (ProfileAutoRefresh autoRefresh : autoRefreshs) {
			ProfileAutoRefresh oldAutoRefresh = getAutoRefreshProfileByInstance(autoRefresh.getInstanceId());
			if(oldAutoRefresh!=null){
				Date nextExecuteTime = null;
				Calendar calendar=null;
				if(null!=oldAutoRefresh.getExecuteTime()){
					calendar = Calendar.getInstance();
					calendar.setTime(oldAutoRefresh.getExecuteTime());
				}else{
					calendar = getExecuteCalendar();
				}
				calendar.add(Calendar.DATE, autoRefresh.getExecutRepeat());
				nextExecuteTime = new Date(calendar.getTimeInMillis());
				if(null!=nextExecuteTime){
					oldAutoRefresh.setNextExecuteTime(nextExecuteTime);
				}
				oldAutoRefresh.setExecuteTime(autoRefresh.getExecuteTime());
				oldAutoRefresh.setExecutRepeat(autoRefresh.getExecutRepeat());
				oldAutoRefresh.setIsUse(autoRefresh.getIsUse());
				updateRefreshs.add(oldAutoRefresh);
			}else{
				addRefreshs.add(autoRefresh);
			}
		}
		if(!CollectionUtils.isEmpty(addRefreshs)){
			addAutoRefreshProfile(addRefreshs);
		}
		if(!CollectionUtils.isEmpty(updateRefreshs)){
			profileAutoRefreshDao.updateAutoRefreshProfile(updateRefreshs);
		}
	}

	@Override
	public List<ProfileAutoRefresh> getAllAutoRefreshProfile() {
		return profileAutoRefreshDao.getAllAutoRefreshProfile();
	}
	
	@Override
	public List<ProfileAutoRefresh> getAutoRefreshProfileByProfileId(long id) {
		return profileAutoRefreshDao.getAutoRefreshProfileById(id);
	}

	@Override
	public ProfileAutoRefresh getAutoRefreshProfileByInstance(long instanceId) {
		return profileAutoRefreshDao.getAutoRefreshProfileByInstance(instanceId);
	}
	
	private Calendar getExecuteCalendar(){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY,jobExecuteTime);
		calendar.set(Calendar.MINUTE,0);
		calendar.set(Calendar.SECOND,0);
		return calendar;
	}
	
	public void init(){
		if(logger.isInfoEnabled()){
			logger.info("ProfileAutoRefreshServiceImpl init start,check jobExecute config time!");
		}
		List<ProfileAutoRefresh> autoRefreshs = getAllAutoRefreshProfile();
		if(!CollectionUtils.isEmpty(autoRefreshs)){
			List<ProfileAutoRefresh> updateRefreshs = new ArrayList<ProfileAutoRefresh>();
			for (ProfileAutoRefresh profileAutoRefresh : autoRefreshs) {
				Calendar nexeExecuteCalendar = Calendar.getInstance();
				nexeExecuteCalendar.setTime(profileAutoRefresh.getNextExecuteTime());
				if(nexeExecuteCalendar.get(Calendar.HOUR_OF_DAY)!=jobExecuteTime){
					nexeExecuteCalendar.set(Calendar.HOUR_OF_DAY, jobExecuteTime);
					profileAutoRefresh.setNextExecuteTime(nexeExecuteCalendar.getTime());
					updateRefreshs.add(profileAutoRefresh);
				}
			}
			if(!CollectionUtils.isEmpty(updateRefreshs)){
				profileAutoRefreshDao.updateAutoRefreshProfile(updateRefreshs);
			}
		}
	}

}
