package com.mainsteam.stm.profilelib.dao.impl;

import java.util.List;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.profilelib.dao.ProfileChangeDAO;
import com.mainsteam.stm.profilelib.po.ProfileChangePO;

public class ProfileChangeDAOImpl implements ProfileChangeDAO {

	private SqlSession session;
	private SqlSessionFactory myBatisSqlSessionFactory;
	public void setSession(SqlSession session) {
		this.session = session;
	}
	
	public void setMyBatisSqlSessionFactory(SqlSessionFactory myBatisSqlSessionFactory) {
		this.myBatisSqlSessionFactory = myBatisSqlSessionFactory;
	}
	
//	@Override
//	public List<ProfileChangePO> getChangeByPO(ProfileChangePO profileChangePO)
//			throws Exception {
//		return session.selectList("com.mainsteam.stm.profilelib.dao.ProfileChangeDAO.getChangeByPO",profileChangePO);
//	}

	@Override
	public int updateProfileChange(ProfileChangePO ProfileChangePO)
			throws Exception {
		return session.update("com.mainsteam.stm.profilelib.dao.ProfileChangeDAO.updateProfileChange",ProfileChangePO);
	}

	@Override
	public int updateProfileChanges(List<ProfileChangePO> ProfileChangePOs)
			throws Exception {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (ProfileChangePO profileChangePO : ProfileChangePOs) {
				batchSession.update("com.mainsteam.stm.profilelib.dao.ProfileChangeDAO.updateProfileChange",profileChangePO);
			}
			batchSession.commit();
		}
		return 0;
	}

	@Override
	public int insertProfileChange(ProfileChangePO profileChangePO)
			throws Exception {
		return session.insert("com.mainsteam.stm.profilelib.dao.ProfileChangeDAO.insertProfileChange",profileChangePO);
	}

	@Override
	public int insertProfileChanges(List<ProfileChangePO> ProfileChangePOs)
			throws Exception {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (ProfileChangePO profileChangePO : ProfileChangePOs) {
				batchSession.insert("com.mainsteam.stm.profilelib.dao.ProfileChangeDAO.insertProfileChange",profileChangePO);
			}
			batchSession.commit();
		}
		
		return 0;
	}

	public int deleteProfileChangeById(long profileChangeId) throws Exception{
		return session.delete("com.mainsteam.stm.profilelib.dao.ProfileChangeDAO.deleteProfileChangeById", profileChangeId);
	}

	@Override
	public List<ProfileChangePO> getProfileChange(
			Page<ProfileChangePO, ProfileChangePO> page) {
		return session.selectList("com.mainsteam.stm.profilelib.dao.ProfileChangeDAO.getProfileChange",page);
	}
	
//	@Override
//	public int updateProfileChangeByPO(ProfileChangePO profileChangePO)
//			throws Exception {
//		// TODO Auto-generated method stub
//		return session.update("com.mainsteam.stm.profilelib.dao.ProfileChangeDAO.updateProfileChangeByPO",profileChangePO);
//	}
//
//	@Override
//	public int updateProfileChangeByPOs(List<ProfileChangePO> ProfileChangePOs)
//			throws Exception {
//		for (ProfileChangePO profileChangePO : ProfileChangePOs) {
//			updateProfileChangeByPO(profileChangePO);
//		}
//		return 0;
//	}

}
