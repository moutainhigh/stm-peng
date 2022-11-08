package com.mainsteam.stm.profile.fault.dao.impl;

import java.util.List;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.mainsteam.stm.profile.fault.dao.ProfileFaultInstanceDao;
import com.mainsteam.stm.profile.fault.dao.ProfileFaultMetricDao;
import com.mainsteam.stm.profilelib.fault.obj.ProfileFaultInstance;
import com.mainsteam.stm.profilelib.fault.obj.ProfileFaultMetric;

public class ProfileFaultMetricDaoImpl implements ProfileFaultMetricDao
{

	private SqlSession session;
	private SqlSessionFactory myBatisSqlSessionFactory;

	public void setSession(SqlSession session) {
		this.session = session;
	}

	@Override
	public int deleteProfileFaultMetric(long profileId) {
		
		return session.delete("deleteAllProfileMetric",profileId);
	}

	@Override
	public int batchInsertProfileFaultMetric(List<ProfileFaultMetric> profileFaultMetrics) {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (ProfileFaultMetric profileFaultMetric : profileFaultMetrics) {
				batchSession.insert("insertProfileMetric",profileFaultMetric);
			}
			batchSession.commit();
		}
		return 1;
	}

	@Override
	public List<ProfileFaultMetric> selectProfileFaultMetrics(long profileId) {
		
		return session.selectList("selectMetricByProfile",profileId);
	}
	
	public void setMyBatisSqlSessionFactory(
			SqlSessionFactory myBatisSqlSessionFactory) {
		this.myBatisSqlSessionFactory = myBatisSqlSessionFactory;
	}
}
