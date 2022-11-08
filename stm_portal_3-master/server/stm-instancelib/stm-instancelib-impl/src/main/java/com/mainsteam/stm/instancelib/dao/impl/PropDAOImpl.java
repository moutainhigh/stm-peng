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

import com.mainsteam.stm.instancelib.dao.PropDAO;
import com.mainsteam.stm.instancelib.dao.pojo.PropDO;

/**
 * @author xiaoruqiang
 */
public class PropDAOImpl implements PropDAO {

	private SqlSession session;

	private SqlSessionFactory myBatisSqlSessionFactory;
	
	public void setSession(SqlSession session) {
		this.session = session;
	}
	
	public void setMyBatisSqlSessionFactory(
			SqlSessionFactory myBatisSqlSessionFactory) {
		this.myBatisSqlSessionFactory = myBatisSqlSessionFactory;
	}

	/**
	 * 
	 */
	public PropDAOImpl() {
	}

	@Override
	public List<PropDO> getPropDOsByInstance(long instanceId, String type) throws Exception {

		Map<String, Object> param=new HashMap<String, Object>();
		param.put("instanceId", instanceId);
		param.put("propType", type);
		
		return session.selectList("com.mainsteam.stm.instancelib.dao.PropDAO.getPropDOsByInstance", param);
	}

	@Override
	public List<PropDO> getPropDOByInstanceAndKey(long instanceId, String key,
			String type) throws Exception{
		
		Map<String, Object> param=new HashMap<String, Object>();
		param.put("instanceId", instanceId);
		param.put("propKey", key);
		param.put("propType", type);
		
		return session.selectList("com.mainsteam.stm.instancelib.dao.PropDAO.getPropDOByInstanceAndKey",param);
	}
	
	@Override
	public List<PropDO> getPropDOByInstanceAndKeys(long instanceId,List<String> keys,String type) throws Exception{
		Map<String, Object> param=new HashMap<String, Object>();
		param.put("instanceId", instanceId);
		param.put("keys", keys);
		param.put("propType", type);
		return session.selectList("com.mainsteam.stm.instancelib.dao.PropDAO.getPropDOByInstanceAndKeys",param);
	}
	
	@Override
	public synchronized void insertPropDOs(List<PropDO> propDos) throws Exception{
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (PropDO propDO : propDos) {
				batchSession.insert("com.mainsteam.stm.instancelib.dao.PropDAO.insertPropDO", propDO);
			}
			batchSession.commit();
		}
	}

	@Override
	public void insertPropDO(PropDO propDo)throws Exception {
		session.insert("com.mainsteam.stm.instancelib.dao.PropDAO.insertPropDO", propDo);
	}

	@Override
	public void updatePropDOs(List<PropDO> propDOs) throws Exception{
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for(PropDO prop : propDOs){
				Map<String, Object> param=new HashMap<String, Object>();
				param.put("instanceId", prop.getInstanceId());
				param.put("propKey", prop.getPropKey());
				param.put("propType", prop.getPropType());
				batchSession.delete("com.mainsteam.stm.instancelib.dao.PropDAO.removePropDOByInstanceAndKey",param);
			}
			for(PropDO prop : propDOs){
				batchSession.insert("com.mainsteam.stm.instancelib.dao.PropDAO.insertPropDO", prop);
			}
			batchSession.commit();
		}
	}

	@Override
	public void updatePropDO(PropDO propDo) throws Exception{
		 // session.update("com.mainsteam.stm.instancelib.dao.PropDAO.updatePropDO", propDo);
		//先删除，后添加
		Map<String, Object> param=new HashMap<String, Object>();
		param.put("instanceId", propDo.getInstanceId());
		param.put("propKey", propDo.getPropKey());
		param.put("propType", propDo.getPropType());
		session.delete("com.mainsteam.stm.instancelib.dao.PropDAO.removePropDOByInstanceAndKey",param);
		session.insert("com.mainsteam.stm.instancelib.dao.PropDAO.insertPropDO", propDo);
	}

	@Override
	public int removePropDOByInstanceAndKey(long instanceId, String key,
			String type) throws Exception{
		
		Map<String, Object> param=new HashMap<String, Object>();
		param.put("instanceId", instanceId);
		param.put("propKey", key);
		param.put("propType", type);
		
		return session.delete("com.mainsteam.stm.instancelib.dao.PropDAO.removePropDOByInstanceAndKey",param);
	}

	@Override
	public int removePropDOByInstanceAndType(long instanceId, String type) throws Exception{
		
		Map<String, Object> param=new HashMap<String, Object>();
		param.put("instanceId", instanceId);
		param.put("propType", type);
	
		return session.delete("com.mainsteam.stm.instancelib.dao.PropDAO.removePropDOByInstanceAndType",param);
	}
	
	@Override
	public List<PropDO> getAllModuleAndDiscoverProp(List<String> moduleKeys,List<String> discoverKeys) throws Exception {
		Map<String, Object> param=new HashMap<String, Object>();
		param.put("moduleKeys", moduleKeys);
		param.put("discoverKeys", discoverKeys);
		return session.selectList("com.mainsteam.stm.instancelib.dao.PropDAO.getAllModuleAndDiscoverProp",param);
	}

	@Override
	public int removePropDOByInstances(List<Long> instanceIds) throws Exception {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (Long instanceId : instanceIds) {
				batchSession.delete("com.mainsteam.stm.instancelib.dao.PropDAO.removePropByInstances",instanceId);
			}
			batchSession.commit();
		}
		return	instanceIds.size();
	}

}
