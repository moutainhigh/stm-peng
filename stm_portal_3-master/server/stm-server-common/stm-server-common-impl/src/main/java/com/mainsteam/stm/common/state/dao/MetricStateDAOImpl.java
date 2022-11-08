package com.mainsteam.stm.common.state.dao;

import java.util.*;

import com.alibaba.druid.util.StringUtils;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import com.mainsteam.stm.state.obj.InstanceStateData;
import com.mainsteam.stm.state.obj.MetricStateData;
import org.apache.ibatis.session.SqlSessionFactory;

public class MetricStateDAOImpl implements MetricStateDAO {
	private SqlSession session;
	private SqlSessionFactory myBatisSqlSessionFactory;

	public void setSession(SqlSession session) {
		this.session = session;
	}

	public void setMyBatisSqlSessionFactory(SqlSessionFactory myBatisSqlSessionFactory) {
		this.myBatisSqlSessionFactory = myBatisSqlSessionFactory;
	}

	@Override
	public int updateByInstances(Set<Long> instanceIds, Date collectTime) {
		Map<String, Object> params = new HashMap<>(2);
		params.put("instanceIds", instanceIds);
		params.put("collectTime", collectTime);
		return session.update("updateByInstances", params);
	}

	@Override
	public int updateMetricState(MetricStateData state) {
		state.setUpdateTime(new Date());
		String databaseId = session.getConfiguration().getDatabaseId();
		if(StringUtils.equals(databaseId, "mysql")) {
			return session.update("updateMetricStateForMysql", state);
		}else if(StringUtils.equals(databaseId, "oracle")) {
			return session.update("updateMetricStateForOracle", state);
		}else {
			session.delete("deleteMetricState",state);
			return session.insert("addMetricState",state);
		}
	}
	
	@Override
	public MetricStateData getMetricStateData(long instanceID,String metriID) {
		Map<String,Object> param=new HashMap<>();
		param.put("instanceID", instanceID);
		param.put("metricID", metriID);
		return session.selectOne("getMetricState",param);
	}

	
	@Override
	public int addInstanceState(InstanceStateData data) {
		data.setUpdateTime(new Date());
		String databaseId = session.getConfiguration().getDatabaseId();
		if(StringUtils.equals(databaseId, "mysql")) {
			return session.update("updateInstanceStateForMysql", data);
		}else if(StringUtils.equals(databaseId, "oracle")) {
			return session.update("updateInstanceStateForOracle", data);
		}else {
			session.delete("deleteInstanceState",data.getInstanceID());
			session.insert("addInstanceState",data);
		}
		return session.insert("addInstanceStateHistory",data);
	}

	@Override
	public int addInstanceState(List<InstanceStateData> data) {
		String databaseId = session.getConfiguration().getDatabaseId();
		if(StringUtils.equals(databaseId, "mysql")) {
			session.update("updateInstanceStateForMysqlBatch", data);
		}else {
			for (InstanceStateData state: data) {
				state.setUpdateTime(new Date());
				if(StringUtils.equals(databaseId, "oracle")) {
					session.update("updateInstanceStateForOracle", state);
				}else {
					session.delete("deleteInstanceState",state.getInstanceID());
					session.insert("addInstanceState",state);
				}
			}
		}
		session.insert("addInstanceStateHistoryBatch", data);
		return 0;
	}

	@Override
	public InstanceStateData getInstanceState(long instanceID) {
		return session.selectOne("getInstanceState",instanceID);
	}

	@Override
	public InstanceStateData getPreInstanceState(long instanceID,Date startTime ) {
		Map<String,Object> param=new HashMap<>();
		param.put("startTime",startTime);
		param.put("instanceID",instanceID);
		
		return session.selectOne("getPreInstanceState",param);
		
	}

	@Override
	public InstanceStateData getAfterInstanceState(long instanceID, Date endTime) {
		Map<String,Object> param=new HashMap<>();
		param.put("endTime",endTime);
		param.put("instanceID",instanceID);

		return session.selectOne("getAfterInstanceState",param);
	}

	@Override
	public List<InstanceStateData> findInstanceStateHistory(Date startTime, Date endTime,Long[] instanceIDes) {
		Map<String,Object> param=new HashMap<>();
		param.put("startTime",startTime);
		param.put("endTime",endTime);
		param.put("instanceIDes",instanceIDes);
		
		return session.selectList("findInstanceStateHistory",param);
		
	}

	@Override
	public List<InstanceStateData> getAllInstanceState() {
		return session.selectList("findAllInstanceState");
	}

	@Override
	public List<InstanceStateData> findInstanceStates(List<Long> instanceIDes) {
		return session.selectList("findInstanceStates",instanceIDes);
	}

	@Override
	public List<MetricStateData> findMetricState(List<Long> instanceIDes,
			List<String> metricIDes) {
		Map<String,Object> param=new HashMap<>();
		param.put("instanceIDes",instanceIDes);
		param.put("metricIDes",metricIDes);
		
		return session.selectList("findMetricState",param);
	}

	@Override
	public List<MetricStateData> findPerfMetricState(List<Long> instanceIds, Set<MetricStateEnum> ignores) {
		Map<String, Object> params = new HashMap<>(2);
		params.put("instanceIDes", instanceIds);
		params.put("ignores", ignores);
		return session.selectList("findPerfMetricState", params);
	}

	@Override
	public InstanceStateData findLatestInstanceState(Long instanceID) {
		return session.selectOne("findLatestInstanceState", instanceID);
	}

	@Override
	public void deleteBatchInstance(Set<Long> instanceSet) {
		session.delete("deleteInstanceBatch", instanceSet);
	}

	@Override
	public void deleteMetricState(MetricStateData metricStateData) {
		session.delete("deleteMetricState",metricStateData);
	}
}
