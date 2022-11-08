package com.mainsteam.stm.profilelib.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.mainsteam.stm.profilelib.dao.ProfileInstanceRelationDAO;
import com.mainsteam.stm.profilelib.po.ProfileInstRelationPO;

public class ProfileInstanceRelationDAOImpl implements ProfileInstanceRelationDAO {

	private SqlSession session;

	public void setSession(SqlSession session) {
		this.session = session;
	} 
	
	private SqlSessionFactory myBatisSqlSessionFactory;
	
	public void setMyBatisSqlSessionFactory(
			SqlSessionFactory myBatisSqlSessionFactory) {
		this.myBatisSqlSessionFactory = myBatisSqlSessionFactory;
	}
	
	@Override
	public void insertInstRel(ProfileInstRelationPO profileInstRelPojo) throws Exception{
		session.insert("insertInstRel", profileInstRelPojo);
	}

	@Override
	public void insertInstRels(List<ProfileInstRelationPO> profileInstRelPojos)
			throws Exception {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (ProfileInstRelationPO instanceRelation : profileInstRelPojos) {
				session.delete("removeInstRelByInstId", instanceRelation.getInstanceId());
			}
			batchSession.commit();
		}
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (ProfileInstRelationPO instanceRelation : profileInstRelPojos) {
				batchSession.insert("insertInstRel",instanceRelation);
			}
			batchSession.commit();
		}
	}

	@Override
	public void removeInstRelByProfileIds(List<Long> profileIds)
			throws Exception {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (Long profileId : profileIds) {
				batchSession.delete("removeInstRelByProfileId", profileId);
			}
			batchSession.commit();
		}
	}

	@Override
	public void removeInstRelByProfileId(final long profileId) throws Exception {
		session.delete("removeInstRelByProfileId", profileId);
	}

	@Override
	public void removeInstRelByInstIds(final List<Long> instanceIds)
			throws Exception {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (Long instanceId : instanceIds) {
				session.delete("removeInstRelByInstId", instanceId);
			}
			batchSession.commit();
		}
	}

	@Override
	public List<ProfileInstRelationPO> getInstRelationsByProfileId(long profileId)
			throws Exception {
		return session.selectList("getInstRelationsByProfileId", profileId);
	}

	@Override
	public List<ProfileInstRelationPO> getInstRelationsByProfileIds(List<Long> profileIds)
			throws Exception {
		return session.selectList("getInstRelationsByProfileIds", profileIds);
	}

	@Override
	public List<ProfileInstRelationPO> getInstRelationByInstIds(List<Long> instances)
			throws Exception {
		return session.selectList("getInstRelationByInstIds", instances);
	}

	@Override
	public ProfileInstRelationPO getInstRelationByInstId(long instanceId)
			throws Exception {
		return session.selectOne("getInstRelationByInstId", instanceId);
	}

	@Override
	public void removeInstRelByparentInstIds(List<Long> parentInstanceIds)
			throws Exception {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (Long parentInstanceId : parentInstanceIds) {
				batchSession.delete("removeInstRelByparentInstId",parentInstanceId);
			}
			batchSession.commit();
		}
	}

	@Override
	public void removeInstRelByparentInstId(long parentInstanceId)
			throws Exception {
		session.delete("removeInstRelByparentInstId", parentInstanceId);
	}

	@Override
	public List<ProfileInstRelationPO> getInstRelationByParentInstId(
			long parentInstId) throws Exception {
		return session.selectList("getInstRelationByParentInstId",parentInstId);
	}

	@Override
	public List<ProfileInstRelationPO> getInstRelationByParentInstIds(
			List<Long> parentInstIds) throws Exception {
		return session.selectList("getInstRelationByParentInstIds",parentInstIds);
	}

	@Override
	public List<ProfileInstRelationPO> getAllInstRelation() throws Exception {
		return session.selectList("getAllInstRelation");
	}

	@Override
	public List<ProfileInstRelationPO> getInstRelationsByProfileId(
			long profileId, int nodeGroupId) throws Exception {
		Map<String, Object> param = new HashMap<String, Object>(2);
		param.put("profileId", profileId);
		param.put("nodeGroupId", nodeGroupId);
		return session.selectList("getInstRelationsByProfileIdAndDCSGroup", param);
	}

	@Override
	public List<ProfileInstRelationPO> getInstRelationsByProfileIds(
			List<Long> profileIds, int nodeGroupId) throws Exception {
		Map<String, Object> param = new HashMap<String, Object>(2);
		param.put("profileIds", profileIds);
		param.put("nodeGroupId", nodeGroupId);
		return session.selectList("getInstRelationsByProfileIdsAndDCSGroup", param);
	}
}
