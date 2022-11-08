package com.mainsteam.stm.profilelib.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.mainsteam.stm.profilelib.dao.ProfileMetricDAO;
import com.mainsteam.stm.profilelib.po.ProfileMetricPO;

public class ProfileMetricDAOImpl implements ProfileMetricDAO {

	private SqlSession session;
	private SqlSessionFactory myBatisSqlSessionFactory;
	public void setSession(SqlSession session) {
		this.session = session;
	} 
	public void setMyBatisSqlSessionFactory(SqlSessionFactory myBatisSqlSessionFactory) {
		this.myBatisSqlSessionFactory = myBatisSqlSessionFactory;
	}
	
	@Override
	public void insertMetric(ProfileMetricPO profileMetric) throws Exception {
		session.insert("insertMetric", profileMetric);
	}

	@Override
	public void insertMetrics(List<ProfileMetricPO> metrics) throws Exception {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (ProfileMetricPO metric : metrics) {
				batchSession.insert("insertMetric", metric);
			}
			batchSession.commit();
		}
	}

	@Override
	public void removeMetricByProfileId(long profileId) throws Exception {
		session.delete("removeMetricByProfileId", profileId);
	}
	
	@Override
	public void removeMetricByTimelineId(long timelineId) throws Exception {
		session.delete("removeMetricByTimelineId", timelineId);
	}
	
	/**
	 * 根据基线ID删除指标.
	 * 
	 * @param timelineIds
	 *            策略ID
	 * @throws Exception
	 *             DAO层异常
	 */
	public void removeMetricByTimelineIds(final List<Long> timelineIds) throws Exception{
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (Long timelineId : timelineIds) {
				session.delete("removeMetricByTimelineId", timelineId);
			}
			batchSession.commit();
		}
	}

	@Override
	public void removeMetricByProfileIds(List<Long> profileIds)
			throws Exception {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (Long profileId : profileIds) {
				session.delete("removeMetricByProfileId", profileId);
			}
			batchSession.commit();
		}
	}

	@Override
	public void updateProfileMetric(ProfileMetricPO profileMetric) throws Exception {
		session.update("updateMetric", profileMetric);
	}

	@Override
	public void updateProfileMetrics(List<ProfileMetricPO> profileMetrics) throws Exception {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (ProfileMetricPO profileMetric : profileMetrics) {
				batchSession.update("updateMetric", profileMetric);
			}
			batchSession.commit();
		}
	}
	@Override
	public void updateTimelineMetric(ProfileMetricPO profileMetric)
			throws Exception {
		session.update("updateTimelineMetric", profileMetric);
	}

	@Override
	public void updateTimelineMetrics(List<ProfileMetricPO> profileMetrics)
			throws Exception {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (ProfileMetricPO profileMetricPO : profileMetrics) {
				batchSession.update("updateTimelineMetric", profileMetricPO);
			}
			batchSession.commit();
		}
	}

	@Override
	public List<ProfileMetricPO> getMetricsByProfileId(final long profileId)
			throws Exception {
		return session.selectList("getMetricsByProfileId", profileId);
	}

	@Override
	public List<ProfileMetricPO> getMetricsByTimelineId(final long timelineId) throws Exception {
		return session.selectList("getMetricsByTimelineId", timelineId);
	}

	@Override
	public List<ProfileMetricPO> getAllMetric() throws Exception {
		return session.selectList("getAllMetrics");
	}

	@Override
	public void removeMetricByProfileIdAndMetricId(List<Long> profileIds,
			String metricId) throws Exception {
		Map<String, Object> param=new HashMap<String, Object>();
		param.put("profileIds", profileIds);
		param.put("metricId", metricId);
		session.delete("removeMetricByProfileIdAndMetricId",param);
	}	

}
