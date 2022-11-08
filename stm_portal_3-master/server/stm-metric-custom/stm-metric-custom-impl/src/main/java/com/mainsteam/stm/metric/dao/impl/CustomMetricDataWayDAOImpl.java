package com.mainsteam.stm.metric.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.mainsteam.stm.metric.dao.CustomMetricDataWayDAO;
import com.mainsteam.stm.metric.dao.pojo.CustomMetricDataWayDO;

public class CustomMetricDataWayDAOImpl implements CustomMetricDataWayDAO {

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
	public void insertMetricDataWay(CustomMetricDataWayDO customMetricDataWayDO)
			throws Exception {
		session.insert("insertMetricDataWay",customMetricDataWayDO);
	}

	@Override
	public void insertMetricDataWays(
			List<CustomMetricDataWayDO> customMetricDataWayDOs)
			throws Exception {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (CustomMetricDataWayDO customMetricDataWayDO : customMetricDataWayDOs) {
				batchSession.insert("insertMetricDataWay",customMetricDataWayDO);
			}
			batchSession.commit();
		}
	}

	@Override
	public void removeMetricDataWaybyDO(
			CustomMetricDataWayDO customMetricDataWayDO) throws Exception {
		session.delete("removeMetricDataWaybyDO", customMetricDataWayDO);
	}

	@Override
	public void removeMetricDataWaybyDOs(
			List<CustomMetricDataWayDO> customMetricDataWayDOs)
			throws Exception {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (CustomMetricDataWayDO customMetricDataWayDO : customMetricDataWayDOs) {
				batchSession.delete("removeMetricDataWaybyDO", customMetricDataWayDO);
			}
			batchSession.commit();
		}
	}

	@Override
	public List<CustomMetricDataWayDO> getCustomMetricDataWayByMetric(
			String metricId) throws Exception {
		return session.selectList("getCustomMetricDataWayByMetric", metricId);
	}

	@Override
	public List<CustomMetricDataWayDO> getCustomMetricDataWayByMetrics(
			List<String> metricIds) throws Exception {
		return session.selectList("getCustomMetricDataWayByMetrics", metricIds);
	}

	@Override
	public List<CustomMetricDataWayDO> getCustomMetricDataWayByMetricAndPluginId(
			String metricId, String pluginId) throws Exception {
		Map<String,String> parms  = new HashMap<>();
		parms.put("metricId", metricId);
		parms.put("pluginId", pluginId);
		return session.selectList("getCustomMetricDataWayByMetricAndPluginId", parms);
	}

	@Override
	public void removeMetricDataWaybyMetricIds(List<String> metricIds)
			throws Exception {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (String metricId : metricIds) {
				batchSession.delete("removeMetricDataWaybyMetricId", metricId);
			}
			batchSession.commit();
		}
	}

	@Override
	public void removeMetricDataWaybyMetricId(String metricId) throws Exception {
		session.delete("removeMetricDataWaybyMetricId", metricId);
	}
}
