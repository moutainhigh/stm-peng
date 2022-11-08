/**
 * 
 */
package com.mainsteam.stm.instancelib.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.mainsteam.stm.instancelib.dao.PropTypeDAO;
import com.mainsteam.stm.instancelib.dao.pojo.PropDO;

/**
 * @author xiaoruqiang
 */
public class PropTypeDAOImpl implements PropTypeDAO {

	private SqlSession session;

	public void setSession(SqlSession session) {
		this.session = session;
	}
	
	private SqlSessionFactory myBatisSqlSessionFactory;
	
	public void setMyBatisSqlSessionFactory(
			SqlSessionFactory myBatisSqlSessionFactory) {
		this.myBatisSqlSessionFactory = myBatisSqlSessionFactory;
	}

	/**
	 * 
	 */
	public PropTypeDAOImpl() {
	}

	@Override
	public List<PropDO> getPropTypeDOsByInstance(long instanceId, String type) {

		Map<String, Object> param=new HashMap<String, Object>();
		param.put("instanceId", instanceId);
		param.put("type", type);
		
		return session.selectList("com.mainsteam.stm.instancelib.dao.PropTypeDAO.getPropTypeDOsByInstance", param);
	}

	@Override
	public synchronized void insertPropTypeDOs(List<PropDO> propDos) {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (PropDO propDO : propDos) {
				batchSession.insert("com.mainsteam.stm.instancelib.dao.PropTypeDAO.insertPropTypeDO", propDO);
			}
			batchSession.commit();
		}
	}

	@Override
	public void insertPropTypeDO(PropDO propDo) {
		session.insert("com.mainsteam.stm.instancelib.dao.PropTypeDAO.insertPropTypeDO", propDo);
	}

	@Override
	public void updatePropTypeDOs(List<PropDO> propDOs) {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for(PropDO prop : propDOs){
				Map<String, Object> param=new HashMap<String, Object>();
				param.put("instanceId", prop.getInstanceId());
				param.put("propKey", prop.getPropKey());
				param.put("propType", prop.getPropType());
				batchSession.delete("com.mainsteam.stm.instancelib.dao.PropTypeDAO.removePropTypeDOByInstanceAndKey",param);
			}
			for(PropDO prop : propDOs){
				batchSession.insert("com.mainsteam.stm.instancelib.dao.PropTypeDAO.insertPropTypeDO", prop);
			}
			batchSession.commit();
		}
	}

	@Override
	public void updatePropTypeDO(PropDO propDo) {
		Map<String, Object> param=new HashMap<String, Object>();
		param.put("instanceId", propDo.getInstanceId());
		param.put("propKey", propDo.getPropKey());
		param.put("propType", propDo.getPropType());
		session.delete("com.mainsteam.stm.instancelib.dao.PropTypeDAO.removePropTypeDOByInstanceAndKey",param);
		session.insert("com.mainsteam.stm.instancelib.dao.PropTypeDAO.insertPropTypeDO", propDo);
	}

	@Override
	public int removePropTypeDOByInstanceAndKey(long instanceId, String key,
			String type) {
		
		Map<String, Object> param=new HashMap<String, Object>();
		param.put("instanceId", instanceId);
		param.put("key", key);
		param.put("type", type);
		
		return session.delete("com.mainsteam.stm.instancelib.dao.PropTypeDAO.removePropTypeDOByInstanceAndKey",param);
	}

	@Override
	public int removePropTypeDOByInstanceAndType(long instanceId, String type) {
		
		Map<String, Object> param=new HashMap<String, Object>();
		param.put("instanceId", instanceId);
		param.put("type", type);
	
		return session.delete("com.mainsteam.stm.instancelib.dao.PropTypeDAO.removePropTypeDOByInstanceAndType",param);
	}

	@Override
	public int removePropTypeDOByInstances(List<Long> instanceIds) {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (Long instanceId : instanceIds) {
				batchSession.delete("com.mainsteam.stm.instancelib.dao.PropTypeDAO.removePropTypeByInstances", instanceId);
			}
			batchSession.commit();
		}
		return instanceIds.size();
	}
}
