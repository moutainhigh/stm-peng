package com.mainsteam.stm.common.instance.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.mainsteam.stm.common.instance.dao.obj.InstanceLifeCycle;

public class InstanceLifeCycleDAOImpl implements InstanceLifeCycleDAO{
	private SqlSession session;

	public void setSession(SqlSession session) {
		this.session = session;
	}	
	@Override
	public void addLifeCycle(InstanceLifeCycle po) {
		session.insert("addLifeCycle",po);
	}
	
	public List<InstanceLifeCycle> findLifeCycle(List<Long> instanceIDes,Date startTime,Date endTime){
		Map<String,Object> param=new HashMap<>();
		param.put("instanceIDes", instanceIDes);
		param.put("startTime", startTime);
		param.put("endTime", endTime);
		return session.selectList("findLifeCycle", param);
	}
	@Override
	public InstanceLifeCycle findPreState(long instanceID, Date startTime) {
		Map<String,Object> param=new HashMap<>();
		param.put("startTime", startTime);
		param.put("instanceID", instanceID);
		return session.selectOne("findLifeCycle", param);
	}

}
