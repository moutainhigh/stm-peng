package com.mainsteam.stm.instancelib.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.mainsteam.stm.instancelib.dao.RelationDAO;
import com.mainsteam.stm.instancelib.dao.pojo.RelationPO;

public class RelationDAOImpl implements RelationDAO {

	private SqlSession session;

	/**
	 * 
	 */
	public RelationDAOImpl() {
	}

	public void setSession(SqlSession session) {
		this.session = session;
	}
	
	@Override
	public List<RelationPO> getRelationPOsByInstanceId(long instanceId)
			throws Exception {
		return session.selectList("com.mainsteam.stm.instancelib.dao.RelationDAO.getRelationPOsByInstanceId", instanceId);
	}

	@Override
	public void insertRelationPOs(List<RelationPO> POs) throws Exception {
		for (RelationPO PO : POs) {
			session.insert("com.mainsteam.stm.instancelib.dao.RelationDAO.insertRelationPO", PO);
		}
	}

	@Override
	public void insertRelationPO(RelationPO PO) throws Exception {
		session.insert("com.mainsteam.stm.instancelib.dao.RelationDAO.insertRelationPO", PO);
	}

	@Override
	public void updateRelationPOs(List<RelationPO> POs) throws Exception {
		for (RelationPO PO : POs) {
			 session.delete("com.mainsteam.stm.instancelib.dao.RelationDAO.removeRelationPOByInstanceId", PO.getInstanceId());
		}
		for (RelationPO PO : POs) {
			 session.insert("com.mainsteam.stm.instancelib.dao.RelationDAO.insertRelationPO", PO);
		}
	}

	@Override
	public void updateRelationPO(RelationPO PO) throws Exception {
		 session.delete("com.mainsteam.stm.instancelib.dao.RelationDAO.removeRelationPOByInstanceId", PO.getInstanceId());
		 session.insert("com.mainsteam.stm.instancelib.dao.RelationDAO.insertRelationPO", PO);
	}

	@Override
	public int removeRelationPOByInstanceId(long instanceId) throws Exception {
		return session.delete("com.mainsteam.stm.instancelib.dao.RelationDAO.removeRelationPOByInstanceId", instanceId);
	}

	@Override
	public List<RelationPO> getRelationPOsByInstanceType(String type)
			throws Exception {
		return session.selectList("com.mainsteam.stm.instancelib.dao.RelationDAO.getRelationPOsByInstanceType", type);
	}

	@Override
	public void removeRelationPOByInstanceIdAndType(long instanceId, String type)
			throws Exception {
		Map<String,String> parms = new HashMap<>();
		parms.put("instanceId", String.valueOf(instanceId));
		parms.put("type", type);
		session.delete("com.mainsteam.stm.instancelib.dao.RelationDAO.removeRelationPOByInstanceIdAndType", parms);
	}

}
