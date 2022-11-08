package com.mainsteam.stm.metric.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.mainsteam.stm.metric.dao.CustomMetricCollectDAO;
import com.mainsteam.stm.metric.dao.pojo.CustomMetricCollectDO;

public class CustomMetricCollectDAOImpl implements CustomMetricCollectDAO {

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
	public void insertMetricCollect(CustomMetricCollectDO customMetricCollectDO) {
		session.insert("insertMetricCollect", customMetricCollectDO);
	}

	@Override
	public void insertMetricCOllects(
			List<CustomMetricCollectDO> customMetricCollectDOs) {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (CustomMetricCollectDO customMetricCollectDO : customMetricCollectDOs) {
				batchSession.insert("insertMetricCollect", customMetricCollectDO);
			}
			batchSession.commit();
		}
	}

	@Override
	public void updateMetricCollect(CustomMetricCollectDO customMetricCollectDO) {
		session.update("updateMetricCollect", customMetricCollectDO);
	}

	@Override
	public void updateMetricCollects(
			List<CustomMetricCollectDO> customMetricCollectDOs) {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (CustomMetricCollectDO customMetricCollectDO : customMetricCollectDOs) {
				batchSession.update("updateMetricCollect", customMetricCollectDO);
			}
			batchSession.commit();
		}
	}

	@Override
	public void removeMetricCollectById(long customMetricCollectId) {
		session.delete("removeMetricCollectById", customMetricCollectId);
	}

	@Override
	public void removeMetricCollectByIds(List<String> metricCollectIds) {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (String metricCollectId : metricCollectIds) {
				batchSession.delete("removeMetricCollectById", metricCollectId);
			}
			batchSession.commit();
		}
	}

	@Override
	public CustomMetricCollectDO getMetricCollectDOById(long metricCollectId) {
		return session.selectOne("getMetricCollectDOById", metricCollectId);
	}

	@Override
	public List<CustomMetricCollectDO> getMetricCollectDOByMetricIdAndpluginId(
			String metricId, String pluginId) throws Exception {
		Map<String, String> parms = new HashMap<>(2);
		parms.put("metricId", metricId);
		parms.put("pluginId", pluginId);
		return session.selectList("getMetricCollectDOByMetricIdAndpluginId",
				parms);
	}

	@Override
	public void removeMetricCollectByMetricId(String metricId) throws Exception {
		session.delete("removeMetricCollectByMetricId", metricId);
	}

	@Override
	public void removeMetricCollectByMetricIds(List<String> metricIds)
			throws Exception {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (String metricId : metricIds) {
				batchSession.delete("removeMetricCollectByMetricId", metricId);
			}
			batchSession.commit();
		}
	}

	@Override
	public List<CustomMetricCollectDO> getMetricCollectDOById(
			List<String> metricIds) throws Exception {
		return session.selectList("getMetricCollectByMetricIds", metricIds);
	}

	@Override
	public void removeMetricCollectByMetricIdAndPluginId(String metricId,
			String pluginId) throws Exception {
		CustomMetricCollectDO collectDO = new CustomMetricCollectDO();
		collectDO.setMetricId(metricId);
		collectDO.setPluginId(pluginId);
		session.delete("removeMetricCollectByMetricIdAndPluginId", collectDO);
	}
}
