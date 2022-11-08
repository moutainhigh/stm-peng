package com.mainsteam.stm.instancelib.dao.impl;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.mainsteam.stm.instancelib.dao.InstaceCollectionDAO;
import com.mainsteam.stm.instancelib.dao.pojo.InstanceCollectionPO;

public class InstaceCollectionDAOImpl implements InstaceCollectionDAO {

	private SqlSession session;

	public InstaceCollectionDAOImpl() {
	}

	public void setSession(SqlSession session) {
		this.session = session;
	}
	
	@Override
	public List<InstanceCollectionPO> getInstaceCollectPOsByInstanceId(
			long instanceId) throws Exception {
		return session.selectList("com.mainsteam.stm.instancelib.dao.InstaceCollectionDAO.getInstaceCollectPOsByInstanceId", instanceId);
	}

	@Override
	public void insertInstanceCollectionPOs(List<InstanceCollectionPO> POs) throws Exception {
		for(InstanceCollectionPO PO: POs){
			session.insert("com.mainsteam.stm.instancelib.dao.InstaceCollectionDAO.insertInstanceCollectionPO",PO);
		}
	}

	@Override
	public void insertInstanceCollectionPO(InstanceCollectionPO PO)  throws Exception {
		session.insert("com.mainsteam.stm.instancelib.dao.InstaceCollectionDAO.insertInstanceCollectionPO",PO);
	}

	@Override
	public void updateInstanceCollectionPOs(List<InstanceCollectionPO> POs)  throws Exception {
		for(InstanceCollectionPO PO: POs){
			session.delete("com.mainsteam.stm.instancelib.dao.InstaceCollectionDAO.removeInstanceCollectionPOByInstanceId",PO.getInstanceId());
		}
		for(InstanceCollectionPO PO: POs){
			session.insert("com.mainsteam.stm.instancelib.dao.InstaceCollectionDAO.insertInstanceCollectionPO",PO);
		}
	}

	@Override
	public void updateInstanceCollectionPO(InstanceCollectionPO PO)  throws Exception {
		session.delete("com.mainsteam.stm.instancelib.dao.InstaceCollectionDAO.removeInstanceCollectionPOByInstanceId",PO.getInstanceId());
		session.insert("com.mainsteam.stm.instancelib.dao.InstaceCollectionDAO.insertInstanceCollectionPO",PO);
	}

	@Override
	public int removeInstanceCollectionPOByInstanceId(long instanceId) throws Exception {
		return session.delete("com.mainsteam.stm.instancelib.dao.InstaceCollectionDAO.removeInstanceCollectionPOByInstanceId",instanceId);
	}

}
