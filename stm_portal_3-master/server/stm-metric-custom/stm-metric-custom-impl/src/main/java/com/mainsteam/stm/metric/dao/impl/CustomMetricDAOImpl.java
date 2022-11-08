package com.mainsteam.stm.metric.dao.impl;

import java.util.List;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.mainsteam.stm.metric.dao.CustomMetricDAO;
import com.mainsteam.stm.metric.dao.pojo.CustomMetricDO;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

public class CustomMetricDAOImpl implements CustomMetricDAO {

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
	public void insertMetric(CustomMetricDO customMetricDO) {
		session.insert("insertCustomMetric",customMetricDO);
	}

	@Override
	public void insertMetrics(List<CustomMetricDO> customMetricDOs) {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (CustomMetricDO customMetricDO : customMetricDOs) {
				batchSession.insert("insertCustomMetric",customMetricDO);
			}
			batchSession.commit();
		}
	}

	@Override
	public void updateMetric(CustomMetricDO customMetricDO) {
		session.update("updateCustomMetric",customMetricDO);
	}

	@Override
	public void updateMetrics(List<CustomMetricDO> customMetricDOs) {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (CustomMetricDO customMetricDO : customMetricDOs) {
				batchSession.update("updateCustomMetric",customMetricDO);
			}
			batchSession.commit();
		}
	}

	@Override
	public CustomMetricDO getMetricDOById(String customMetricId) {
		return session.selectOne("getMetricDOById", customMetricId);
	}

	@Override
	public void removeMetricById(String customMetricId) {
		session.delete("removeMetricById", customMetricId);
	}

	@Override
	public void removeMetricByIds(List<String> customMetricIds) {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (String customMetricId : customMetricIds) {
				batchSession.delete("removeMetricById",customMetricId);
			}
			batchSession.commit();
		}
	}

	@Override
	public List<CustomMetricDO> getAllMetric(Page<CustomMetricDO,CustomMetricDO> page) {
		return session.selectList("getAllMetric",page);
	}

	@Override
	public List<CustomMetricDO> getMetrics(List<String> metricIds)
			throws Exception {
		return session.selectList("getMetricDOByIds",metricIds);
	}

	@Override
	public int getCustomMetricsCount(CustomMetricDO query) {
		return session.selectOne("getMetricCount",query);
	}

	@Override
	public List<CustomMetricDO> getAllCustomMetricDOs() {
		return session.selectList("getAllCustomMetricDOs");
	}
}
