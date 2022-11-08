package com.mainsteam.stm.metric.dao.impl;

import java.util.List;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.mainsteam.stm.metric.dao.CustomMetricBindDAO;
import com.mainsteam.stm.metric.dao.pojo.CustomMetricBindDO;

public class CustomMetricBindDAOImpl implements CustomMetricBindDAO {

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
	public void insertMetricBind(CustomMetricBindDO customMetricBindDO) {
		session.insert("insertMetricBind", customMetricBindDO);
	}

	@Override
	public void insertMetricBinds(List<CustomMetricBindDO> customMetricBindDOs) {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (CustomMetricBindDO customMetricBindDO : customMetricBindDOs) {
				batchSession.insert("insertMetricBind", customMetricBindDO);
			}
			batchSession.commit();
		}
	}

	@Override
	public void removeMetricBindbyDO(CustomMetricBindDO customMetricBindDO) {
		session.delete("removeMetricBindbyDO", customMetricBindDO);
	}

	@Override
	public void removeMetricBindbyDOs(
			List<CustomMetricBindDO> customMetricBindDOs) {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (CustomMetricBindDO customMetricBindDO : customMetricBindDOs) {
				batchSession.delete("removeMetricBindbyDO", customMetricBindDO);
			}
			batchSession.commit();
		}
	}

	@Override
	public List<CustomMetricBindDO> getCustomMetricBindByInstanceId(
			long instancId) {
		return session.selectList("getCustomMetricBindByInstanceId", instancId);
	}

	@Override
	public void removeMetricBindbyInstanceIds(List<Long> instanceIds) {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (Long instanceId : instanceIds) {
				batchSession.delete("removeMetricBindbyInstanceId", instanceId);
			}
			batchSession.commit();
		}
	}

	@Override
	public List<CustomMetricBindDO> getCustomMetricBindByNodeGroupId(
			int nodeGroupId) throws Exception {
		return session.selectList("getCustomMetricBindByNodeGroupId",
				nodeGroupId);
	}

	@Override
	public List<CustomMetricBindDO> getCustomMetricBindByInstanceIds(
			List<Long> instanceIds) throws Exception {
		return session.selectList("getCustomMetricBindByInstanceIds",
				instanceIds);
	}

	@Override
	public List<CustomMetricBindDO> getCustomMetricBindByMetric(String metricId)
			throws Exception {
		return session.selectList("getCustomMetricBindByMetricId", metricId);
	}

	@Override
	public List<CustomMetricBindDO> getCustomMetricBindByMetrics(
			List<String> metricIds) throws Exception {
		return session.selectList("getCustomMetricBindByMetricIds", metricIds);
	}

	@Override
	public List<CustomMetricBindDO> getCustomMetricBindByMetricAndPluginId(
			String metricId, String pluginId) throws Exception {
		CustomMetricBindDO bindDO = new CustomMetricBindDO();
		bindDO.setMetricId(metricId);
		bindDO.setPluginId(pluginId);
		return session.selectList("getCustomMetricBindByMetricIdAndPluginId", bindDO);
	}

	@Override
	public List<CustomMetricBindDO> getAllCustomMetricBinds() {
		return session.selectList("getAllCustomMetricBinds");
	}
}
