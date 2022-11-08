package com.mainsteam.stm.instancelib.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.mainsteam.stm.instancelib.dao.InstanceDependenceRelationResultDAO;
import com.mainsteam.stm.instancelib.dao.pojo.InstanceDependenceResultPO;

public class InstanceDependenceRelationResultDAOImpl implements
		InstanceDependenceRelationResultDAO {

	private SqlSession session;
	
	public void setSession(SqlSession session) {
		this.session = session;
	}

	@Override
	public InstanceDependenceResultPO getDependenceResultByAlarmEventIdAndType(
			long alarmEventId, String type) throws Exception {
		Map<String,String> parms = new HashMap<>(2);
		parms.put("alarmEventId", String.valueOf(alarmEventId));
		parms.put("resultType", type);
		return session.selectOne("getDependenceResultByAlarmEventIdAndType",parms);
	}

	@Override
	public void insertDependenceResult(InstanceDependenceResultPO instanceDependenceResultPO)
			throws Exception {
		session.insert("insertDependenceResult",instanceDependenceResultPO);
	}

	@Override
	public void removeDependenceResultByAlarmEventId(long alarmEventId)
			throws Exception {
		session.delete("removeDependenceResultByAlarmEventId", alarmEventId);
	}

}
