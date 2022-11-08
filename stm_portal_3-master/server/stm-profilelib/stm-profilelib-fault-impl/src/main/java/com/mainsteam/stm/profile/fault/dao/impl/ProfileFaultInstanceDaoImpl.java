package com.mainsteam.stm.profile.fault.dao.impl;

import java.util.List;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.mainsteam.stm.instancelib.dao.pojo.ResourceInstanceDO;
import com.mainsteam.stm.profile.fault.dao.ProfileFaultInstanceDao;
import com.mainsteam.stm.profilelib.fault.obj.ProfileFaultInstance;

public class ProfileFaultInstanceDaoImpl implements ProfileFaultInstanceDao {

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
	public int deleteProfileFaultInstance(long profileId) {
		
		return session.delete("deleteAllProfileInstance",profileId);
	}

	@Override
	public int batchInsertProfileFaultInstance(List<ProfileFaultInstance> profileFaultInstances) {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (ProfileFaultInstance profileFaultInstance : profileFaultInstances) {
				batchSession.insert("insertProfileInstance",profileFaultInstance);
			}
			batchSession.commit();
		}
		return 1;
	}

	@Override
	public List<ProfileFaultInstance> selectProfileFaultInstances(long profileId) {
		
		return session.selectList("selectInstanceByProfile",profileId);
	}

}
