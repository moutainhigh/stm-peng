package com.mainsteam.stm.profilelib.dao.impl;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.mainsteam.stm.profilelib.dao.ProfileChangeHistoryDAO;
import com.mainsteam.stm.profilelib.po.ProfileChangeHistoryPO;

public class ProfileChangeHistoryDAOImpl implements ProfileChangeHistoryDAO {

	private SqlSession session;

	public void setSession(SqlSession session) {
		this.session = session;
	}

	@Override
	public List<ProfileChangeHistoryPO> getHistoryByPO(
			ProfileChangeHistoryPO profileChangeHistoryPO) throws Exception {
		return session.selectList("com.mainsteam.stm.profilelib.dao.ProfileChangeHistoryDAO.getHistoryByPO",profileChangeHistoryPO);
	}

	@Override
	public int insertProfileChangeHistory(
			ProfileChangeHistoryPO profileChangeHistoryPO) throws Exception {
		return session.insert("com.mainsteam.stm.profilelib.dao.ProfileChangeHistoryDAO.insertProfileChangeHistory",profileChangeHistoryPO);
	}

	@Override
	public int insertProfileChangeHistorys(
			List<ProfileChangeHistoryPO> profileChangeHistoryPOs)
			throws Exception {
		for (ProfileChangeHistoryPO profileChangeHistoryPO : profileChangeHistoryPOs) {
			insertProfileChangeHistory(profileChangeHistoryPO);
		}
		return 0;
	}

	@Override
	public List<ProfileChangeHistoryPO> getHistoryByProfileChangeIds(
			List<Long> profileChangeIds) throws Exception {
		return session.selectList("com.mainsteam.stm.profilelib.dao.ProfileChangeHistoryDAO.getHistoryByProfileChangeIds",profileChangeIds);
	}
	
	public List<ProfileChangeHistoryPO> getAllFailedHistory() throws Exception{
		return session.selectList("com.mainsteam.stm.profilelib.dao.ProfileChangeHistoryDAO.getAllHistory");
	}
	
	public int updateProfileChangeHistory(ProfileChangeHistoryPO profileChangeHistoryPO) throws Exception{
		return session.update("com.mainsteam.stm.profilelib.dao.ProfileChangeHistoryDAO.updateProfileChangeHistory",profileChangeHistoryPO);
	}
	
	public int updateProfileChangeHistorys(List<ProfileChangeHistoryPO> profileChangeHistoryPOs) throws Exception{
		for (ProfileChangeHistoryPO profileChangeHistoryPO : profileChangeHistoryPOs) {
			updateProfileChangeHistory(profileChangeHistoryPO);
		}
		return 0;
	}
}
