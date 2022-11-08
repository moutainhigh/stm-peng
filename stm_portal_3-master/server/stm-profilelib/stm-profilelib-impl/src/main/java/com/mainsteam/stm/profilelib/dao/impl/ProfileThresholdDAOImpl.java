package com.mainsteam.stm.profilelib.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.mainsteam.stm.profilelib.dao.ProfileThresholdDAO;
import com.mainsteam.stm.profilelib.po.ProfileThresholdPO;

public class ProfileThresholdDAOImpl implements ProfileThresholdDAO {

	private SqlSession session;
	private SqlSessionFactory myBatisSqlSessionFactory;
	public void setSession(SqlSession session) {
		this.session = session;
	} 
	public void setMyBatisSqlSessionFactory(SqlSessionFactory myBatisSqlSessionFactory) {
		this.myBatisSqlSessionFactory = myBatisSqlSessionFactory;
	}
	@Override
	public void insertThreshold(ProfileThresholdPO thresholdPojo)
			throws Exception {
		session.insert("insertThreshold", thresholdPojo);
	}

	@Override
	public void insertThresholds(List<ProfileThresholdPO> thresholdPojos)
			throws Exception {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (ProfileThresholdPO thresholdPojo : thresholdPojos) {
				batchSession.insert("insertThreshold", thresholdPojo);
			}
			batchSession.commit();
		}
		
	}

	
	@Override
	public void removeThresholdByProfileId(long profileId) throws Exception {
		session.delete("removeThresholdByProfileId", profileId);
	}
	
	@Override
	public void removeThresholdByProfileIds(List<Long> profileIds) throws Exception {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (Long profileId : profileIds) {
				batchSession.delete("removeThresholdByProfileId", profileId);
			}
			batchSession.commit();
		}
	}

	@Override
	public void updateThreshold(ProfileThresholdPO profileThresholdPojo)
			throws Exception {
		session.update("updateThreshold", profileThresholdPojo);
	}
	
	@Override
	public void updateThresholds(List<ProfileThresholdPO> profileThresholdPojos)
			throws Exception {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (ProfileThresholdPO profileThresholdPojo : profileThresholdPojos) {
				batchSession.update("updateThreshold", profileThresholdPojo);
			}
			batchSession.commit();
		}
		
	}

	@Override
	public List<ProfileThresholdPO> getThresholdByProfileId(long profileId)
			throws Exception {
		return session.selectList("getThresholdByProfileId", profileId);
	}

	@Override
	public List<ProfileThresholdPO> getThresholdByProfileIdAndMetricId(
			long profileId, String metricId) throws Exception {
		Map<String,String> param = new HashMap<String, String>();
		param.put("profileId", String.valueOf(profileId));
		param.put("metricId", metricId);
		return session.selectList("getThresholdByProfileIdAndMetricId", param);
	}
	
	@Override
	public List<ProfileThresholdPO> getThresholdByTimelineId(long timelineId)
			throws Exception {
		return session.selectList("getThresholdByTimelineId", timelineId);
	}

	@Override
	public void removeThresholdByTimelineId(long timelineId) throws Exception {
		session.delete("removeThresholdByTimelineId", timelineId);
	}
	
	public void removeThresholdByTimelineIds(final List<Long> timelineIds) throws Exception{
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (Long timelineId : timelineIds) {
				batchSession.delete("removeThresholdByTimelineId", timelineId);
			}
			batchSession.commit();
		}
		
	}

	@Override
	public List<ProfileThresholdPO> getAllThreshold() throws Exception {
		return session.selectList("getAllThresholds");
	}

	@Override
	public void removeThresholdByProfileIdAndMetricId(List<Long> profileIds,
			String metricId) throws Exception {
		Map<String, Object> param=new HashMap<String, Object>();
		param.put("profileIds", profileIds);
		param.put("metricId", metricId);
		session.delete("removeThresholdByProfileIdAndMetricId",param);
	}
}
