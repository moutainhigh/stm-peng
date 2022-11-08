package com.mainsteam.stm.deploy.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.mainsteam.stm.deploy.obj.DeployRecord;

public class DeployRecordDAOImpl implements DeployRecordDAO {
	private SqlSession session;

	public void setSession(SqlSession session) {
		this.session = session;
	}

	@Override
	public void save(DeployRecord record){
		if(1>session.update("updateRecord", record)){
			session.insert("insertRecord",record);
		}
	}
	
	@Override
	public List<DeployRecord> getDeployRecordBySourceID(String sourceID){
		return session.selectList("getDeployRecordBySourceID", sourceID);
	}
}
