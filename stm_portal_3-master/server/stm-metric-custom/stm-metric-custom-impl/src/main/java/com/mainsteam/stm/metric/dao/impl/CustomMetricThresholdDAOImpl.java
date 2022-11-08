package com.mainsteam.stm.metric.dao.impl;

import java.util.List;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.mainsteam.stm.metric.dao.CustomMetricThresholdDAO;
import com.mainsteam.stm.metric.dao.pojo.CustomMetricThresholdDO;

public class CustomMetricThresholdDAOImpl implements CustomMetricThresholdDAO {

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
	public void insertCustomMetricThreshold(CustomMetricThresholdDO customMetricThresholdDO) {
		session.insert("insertCustomMetricThreshold",customMetricThresholdDO);
	}

	@Override
	public void insertCustomMetricThresholds(List<CustomMetricThresholdDO> customMetricThresholdDOs) {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (CustomMetricThresholdDO customMetricThresholdDO : customMetricThresholdDOs) {
				batchSession.insert("insertCustomMetricThreshold",customMetricThresholdDO);
			}
			batchSession.commit();
		}
	}

	@Override
	public void updateCustomMetricThreshold(CustomMetricThresholdDO customMetricThresholdDO) {
		session.update("updateCustomMetricThreshold",customMetricThresholdDO);
	}

	@Override
	public void updateCustomMetricThresholds(List<CustomMetricThresholdDO> customMetricThresholdDOs) {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (CustomMetricThresholdDO customMetricThresholdDO : customMetricThresholdDOs) {
				batchSession.update("updateCustomMetricThreshold",customMetricThresholdDO);
			}
			batchSession.commit();
		}
	}

	@Override
	public List<CustomMetricThresholdDO> getCustomMetricThresholdsByMetricId(String metricId) {
		return session.selectList("getCustomMetricThresholdsByMetricId", metricId);
	}

	@Override
	public void removeThresholdsByMetricId(String metricId) {
		session.delete("removeThresholdsByMetricId", metricId);
	}

	@Override
	public void removeThresholdsByMetricIds(List<String> metricIds) {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (String metricId : metricIds) {
				batchSession.delete("removeThresholdsByMetricId", metricId);
			}
			batchSession.commit();
		}
	}
}
