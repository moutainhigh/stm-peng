package com.mainsteam.stm.profilelib.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.mainsteam.stm.profilelib.dao.ProfileDAO;
import com.mainsteam.stm.profilelib.po.ProfileInfoPO;

public class ProfileDAOImpl implements ProfileDAO {

	private SqlSession session;

	private SqlSessionFactory myBatisSqlSessionFactory;
	
	public void setSession(SqlSession session) {
		this.session = session;
	}

	public void setMyBatisSqlSessionFactory(SqlSessionFactory myBatisSqlSessionFactory) {
		this.myBatisSqlSessionFactory = myBatisSqlSessionFactory;
	}
	
	@Override
	public List<ProfileInfoPO> getProfileInfoPO(final ProfileInfoPO profileInfoPO) throws Exception {
		return session.selectList("getProfileInfoPO",profileInfoPO);
	}
	
//	@Override
//	public List<ProfileInfoPO> getParentProfilePos() {
//		return session.selectList(
//				"getParentProfilePos");
//	}

	@Override
	public ProfileInfoPO getProfilePoById(final long profileId) throws Exception {
		return session.selectOne("getProfilePoById",profileId);
	}

	@Override
	public void insertProfiles(final List<ProfileInfoPO> profiles) throws Exception {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (ProfileInfoPO profile : profiles) {
				batchSession.insert("insertProfile",profile);
			}
			batchSession.commit();
		}
	}

	@Override
	public void insertProfile(final ProfileInfoPO profile) throws Exception {
		session.insert("insertProfile",profile);
	}

	@Override
	public void updateProfiles(final List<ProfileInfoPO> profiles) throws Exception{
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (ProfileInfoPO profile : profiles) {
				batchSession.update("updateProfile",profile);
			}
			batchSession.commit();
		}
	}

	@Override
	public void updateProfileStateByResourceIds(final List<String> resourceIds,String isUse) throws Exception{
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			ProfileInfoPO profileInfo = new ProfileInfoPO();
			profileInfo.setIsUse(isUse);
			for(String resourceId : resourceIds){
				profileInfo.setResourceId(resourceId);
				batchSession.update("updateProfileStateByResourceId",profileInfo);
			}
			batchSession.commit();
		}
	}
	
	@Override
	public void updateProfile(final ProfileInfoPO profile) throws Exception{
		session.update("updateProfile",profile);
	}

	@Override
	public int removeProfileByProfileIds(final List<Long> profileIds) throws Exception {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for(Long profileId : profileIds){
				batchSession.delete("removeProfileByProfileId",profileId);
			}
			batchSession.commit();
		}
		return profileIds==null?0:profileIds.size();
	}
	
	@Override
	public int removeProfileByProfileId(final long profileId) throws Exception {
		return session.delete("removeProfileByProfileId",profileId);
	}

	@Override
	public List<ProfileInfoPO> getAllProfilePos() throws Exception {
		return session.selectList("getAllProfilePos");
	}

	@Override
	public List<ProfileInfoPO> getProfileBasicInfoByResourceIds(List<String> resourceIds,String profileType) throws Exception {
		Map<String,Object> param = new HashMap<String, Object>(2);
		param.put("resourceIds", resourceIds);
		param.put("profileType", profileType);
		return session.selectList("getProfileBasicInfoByResourceIds",param);
	}

	@Override
	public List<ProfileInfoPO> getProfilePoByIds(List<Long> profileIds)
			throws Exception {
		return session.selectList("getProfilePoByIds",profileIds);
	}

	@Override
	public List<ProfileInfoPO> getPersonalizeProfileBasicInfoByParentProfileId(
			long profileId) throws Exception {
		return session.selectList("getPersonalizeProfileBasicInfoByParentProfileId",profileId);
	}

	@Override
	public List<ProfileInfoPO> getProfileBasicInfoByResourceIds(
			List<String> resourceIds) throws Exception {
		return session.selectList("getProfileBasicInfoByResourceId",resourceIds);
	}
	
}

