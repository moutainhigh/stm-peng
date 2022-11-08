package com.mainsteam.stm.profilelib.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.profilelib.ProfileChangeService;
import com.mainsteam.stm.profilelib.dao.ProfileChangeDAO;
import com.mainsteam.stm.profilelib.dao.ProfileChangeHistoryDAO;
import com.mainsteam.stm.profilelib.obj.ProfieChange;
import com.mainsteam.stm.profilelib.obj.ProfileChangeResult;
import com.mainsteam.stm.profilelib.objenum.ProfileChangeEnum;
import com.mainsteam.stm.profilelib.po.ProfileChangeHistoryPO;
import com.mainsteam.stm.profilelib.po.ProfileChangePO;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

public class ProfileChangeServiceImpl implements ProfileChangeService {

	private static final Log logger=LogFactory.getLog(ProfileChangeServiceImpl.class);
	
	private ProfileChangeDAO profileChangeDAO;

	private ProfileChangeHistoryDAO profileChangeHistoryDAO;
	
	private ISequence ocProfileChangeHistorySequence;
	
	public void setOcProfileChangeHistorySequence(
			ISequence ocProfileChangeHistorySequence) {
		this.ocProfileChangeHistorySequence = ocProfileChangeHistorySequence;
	}

	public void setProfileChangeDAO(ProfileChangeDAO profileChangeDAO) {
		this.profileChangeDAO = profileChangeDAO;
	}

	public void setProfileChangeHistoryDAO(
			ProfileChangeHistoryDAO profileChangeHistoryDAO) {
		this.profileChangeHistoryDAO = profileChangeHistoryDAO;
	}
	
	@Override
	public void updateProfileChangeSucceed(List<Long> profieChangeIds) {
		if(logger.isTraceEnabled()){
			logger.trace("updateProfileChangeSucceed start profieChangeIds=" + profieChangeIds);
		}
		List<ProfileChangePO> profileChangePOs = new ArrayList<>(profieChangeIds.size());
		for (long profieChangeId : profieChangeIds) {
			ProfileChangePO profileChangePO = new ProfileChangePO();
			profileChangePO.setProfileChangeId(profieChangeId);
			profileChangePO.setOperateState(1);
			//profileChangePO.setChangeTime(new Date());
			profileChangePOs.add(profileChangePO);
		}
		try {
			profileChangeDAO.updateProfileChanges(profileChangePOs);
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error("updateProfileChanges error!",e);
			}
		}
		if(logger.isTraceEnabled()){
			logger.trace("updateProfileChangeSucceed end profieChangeIds=" + profieChangeIds);
		}
	}

	@Override
	public List<ProfieChange> getDispatcherProfileChanges(int pageSize) {
		if(logger.isTraceEnabled()){
			logger.trace("getDispatcherProfileChanges start");
		}
		List<ProfieChange> profieChanges = null;
		Page<ProfileChangePO,ProfileChangePO> page = new Page<>(0,pageSize,null);
		page.setOrder("ASC");
		page.setSort("profile_change_id");
		List<ProfileChangePO> profileChangePOs  = profileChangeDAO.getProfileChange(page);
		if(profileChangePOs != null){
			profieChanges = new ArrayList<>(profileChangePOs.size());
			for (ProfileChangePO profileChangePO : profileChangePOs) {
				ProfieChange profileChange = new ProfieChange();
				profileChange.setChangeTime(profileChangePO.getChangeTime());
				profileChange.setOperateState(profileChangePO.getOperateState()==1?true:false);
				profileChange.setProfileChangeEnum(ProfileChangeEnum.valueOf(profileChangePO.getOperateMode()));
				profileChange.setProfileId(profileChangePO.getProfileId());
				profileChange.setProfileChangeId(profileChangePO.getProfileChangeId());
				profileChange.setTimelineId(profileChangePO.getTimelineId());
				profileChange.setProfileId(profileChangePO.getProfileId());
				profileChange.setSource(profileChangePO.getSource());
				profieChanges.add(profileChange);
			}
		}
		if(logger.isTraceEnabled()){
			logger.trace("getDispatcherProfileChanges end");
		}
		return profieChanges;
	}

	@Override
	public void addProfileChangeResults(
			List<ProfileChangeResult> profileChangeResults) {
		if(logger.isTraceEnabled()){
			logger.trace("addProfileChangeResults start");
		}
		List<ProfileChangeHistoryPO> profileChangeHistoryPOs = new ArrayList<>(profileChangeResults.size());
		Date date = new Date();
		for (ProfileChangeResult profileChangeResult : profileChangeResults) {
			ProfileChangeHistoryPO profileChangeHistoryPO = new ProfileChangeHistoryPO();
			profileChangeHistoryPO.setDcsGroupId(profileChangeResult.getDcsGroupId());
			profileChangeHistoryPO.setOperateTime(date);
			profileChangeHistoryPO.setProfileChangeId(profileChangeResult.getProfileChangeId());
			profileChangeHistoryPO.setProfileChangeHistoryId(ocProfileChangeHistorySequence.next());
			profileChangeHistoryPO.setResultState(profileChangeResult.isResultState()?1:0);
			profileChangeHistoryPOs.add(profileChangeHistoryPO);
		}
		try {
			profileChangeHistoryDAO.insertProfileChangeHistorys(profileChangeHistoryPOs);
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error("addProfileChangeResults error.", e);
			}
		}
		if(logger.isTraceEnabled()){
			logger.trace("addProfileChangeResults end");
		}
	}

	@Override
	public void updateProfileChangeResultSucceed(
			List<ProfileChangeResult> profileChangeResults) {
		if(logger.isTraceEnabled()){
			logger.trace("updateProfileChangeResultSucceed start");
		}
		List<ProfileChangeHistoryPO> profileChangeHistoryPOs = new ArrayList<>(profileChangeResults.size());
		Date date = new Date();
		for (ProfileChangeResult profileChangeResult : profileChangeResults) {
			ProfileChangeHistoryPO profileChangeHistoryPO = new ProfileChangeHistoryPO();
			profileChangeHistoryPO.setDcsGroupId(profileChangeResult.getDcsGroupId());
			profileChangeHistoryPO.setOperateTime(date);
			profileChangeHistoryPO.setProfileChangeId(profileChangeResult.getProfileChangeId());
			profileChangeHistoryPO.setProfileChangeHistoryId(profileChangeResult.getProfileChangeResultId());
			profileChangeHistoryPO.setResultState(1);
			profileChangeHistoryPOs.add(profileChangeHistoryPO);
		}
		try {
			profileChangeHistoryDAO.updateProfileChangeHistorys(profileChangeHistoryPOs);
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error("updateProfileChangeResultSucceed error.", e);
			}
		}
		if(logger.isTraceEnabled()){
			logger.trace("updateProfileChangeResultSucceed end");
		}
	}

	@Override
	public List<ProfileChangeResult> getProfileChangeResultByProfileChangeId(
			List<Long> profieChangeIds) {
		if(logger.isTraceEnabled()){
			logger.trace("getProfileChangeResultByProfileChangeId start");
		}
		List<ProfileChangeResult> profileChangeResults = null;
		List<ProfileChangeHistoryPO> profileChangeHistoryPOs = null;
		try {
			profileChangeHistoryPOs = profileChangeHistoryDAO.getHistoryByProfileChangeIds(profieChangeIds);
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error("getProfileChangeResultByProfileChangeId error.",e);
			}
		}
		if(profileChangeHistoryPOs != null){
			profileChangeResults = new ArrayList<>(profileChangeHistoryPOs.size());
			for (ProfileChangeHistoryPO profileChangeHistoryPO : profileChangeHistoryPOs) {
				ProfileChangeResult profileChangeResult = new ProfileChangeResult();
				profileChangeResult.setChangeTime(profileChangeHistoryPO.getOperateTime());
				profileChangeResult.setDcsGroupId(profileChangeHistoryPO.getDcsGroupId());
				profileChangeResult.setProfileChangeId(profileChangeHistoryPO.getProfileChangeId());
				profileChangeResult.setProfileChangeResultId(profileChangeHistoryPO.getProfileChangeHistoryId());
				profileChangeResult.setResultState(profileChangeHistoryPO.getResultState()==1?true:false);
				profileChangeResults.add(profileChangeResult);
			}
		}
		if(logger.isTraceEnabled()){
			logger.trace("getProfileChangeResultByProfileChangeId end");
		}
		return profileChangeResults;
	}

}
 
