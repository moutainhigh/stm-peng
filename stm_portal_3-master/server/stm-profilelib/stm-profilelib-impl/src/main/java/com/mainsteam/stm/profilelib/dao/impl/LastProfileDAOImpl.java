package com.mainsteam.stm.profilelib.dao.impl;

import java.util.List;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.mainsteam.stm.profilelib.dao.LastProfileDAO;
import com.mainsteam.stm.profilelib.po.ProfileInstRelationPO;

public class LastProfileDAOImpl implements LastProfileDAO {

	private SqlSession session;

	private SqlSessionFactory myBatisSqlSessionFactory;
	
	public void setSession(SqlSession session) {
		this.session = session;
	}
	
	public void setMyBatisSqlSessionFactory(
			SqlSessionFactory myBatisSqlSessionFactory) {
		this.myBatisSqlSessionFactory = myBatisSqlSessionFactory;
	}
	
	@Override
	public void insertLastProfile(ProfileInstRelationPO lastProfilePO)
			throws Exception {
		session.insert("com.mainsteam.stm.profilelib.dao.LastProfileDAO.insertLastProfile", lastProfilePO);
	}
	
	@Override
	public void insertLastProfiles(List<ProfileInstRelationPO> lastProfilePOs)
			throws Exception {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (ProfileInstRelationPO lastProfilePO : lastProfilePOs) {
				batchSession.insert("com.mainsteam.stm.profilelib.dao.LastProfileDAO.insertLastProfile", lastProfilePO);
			}
			batchSession.commit();
		}
		
	}

	@Override
	public void updateLastProfiles(List<ProfileInstRelationPO> lastProfilePOs)
			throws Exception {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (ProfileInstRelationPO lastProfilePO : lastProfilePOs) {
				batchSession.update("com.mainsteam.stm.profilelib.dao.LastProfileDAO.updateLastProfile",lastProfilePO);
			}
			batchSession.commit();
		}
	}

	@Override
	public void updateLastProfile(ProfileInstRelationPO lastProfilePO) throws Exception {
		session.update("com.mainsteam.stm.profilelib.dao.LastProfileDAO.updateLastProfile",lastProfilePO);
	}

	@Override
	public List<ProfileInstRelationPO> getLastProfileByParentInstanceId(long instanceId) {
		return session.selectList("com.mainsteam.stm.profilelib.dao.LastProfileDAO.getLastProfileByParentInstanceId", instanceId);
	}

	@Override
	public void removeLastProfileByInstanceId(long instanceId) {
		session.delete("com.mainsteam.stm.profilelib.dao.LastProfileDAO.removeLastProfileByInstanceId",instanceId);
	}

	@Override
	public void removeLastProfileByInstanceIds(List<Long> instanceIds) {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (Long instanceId : instanceIds) {
				batchSession.delete("com.mainsteam.stm.profilelib.dao.LastProfileDAO.removeLastProfileByInstanceId",instanceId);
			}
			batchSession.commit();
		}
	}
	
	@Override
	public void removeLastProfilesByParentIds(List<Long> instanceIds) {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (Long instanceId : instanceIds) {
				batchSession.delete("com.mainsteam.stm.profilelib.dao.LastProfileDAO.removeLastProfilesByParentId",instanceId);
			}
			batchSession.commit();
		}
	}
	
	@Override
	public void removeLastProfilesByProfileIds(List<Long> profileIds) {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (Long profileId : profileIds) {
				batchSession.delete("com.mainsteam.stm.profilelib.dao.LastProfileDAO.removeLastProfileByProfileId",profileId);
			}
			batchSession.commit();
		}
	}
	
	@Override
	public List<ProfileInstRelationPO> getLastProfileByParentInstanceIds(
			List<Long> parentInstanceIds) throws Exception {
		return session.selectList("com.mainsteam.stm.profilelib.dao.LastProfileDAO.getLastProfileByParentInstanceIds", parentInstanceIds);
	}

	
}
