package com.mainsteam.stm.instancelib.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.mainsteam.stm.instancelib.dao.CustomModulePropDAO;
import com.mainsteam.stm.instancelib.dao.pojo.CustomModulePropDO;

/**
 * 
 * @author xiaoruqiang
 *
 */
public class CustomModulePropDAOImpl implements CustomModulePropDAO {
	
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
	public void addCustomModuleProDO(CustomModulePropDO proDO) throws Exception {
		session.insert("addCustomProDO", proDO);
	}

	@Override
	public void addCustomModuleProDOs(List<CustomModulePropDO> proDOs) throws Exception {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (CustomModulePropDO propDO : proDOs) {
				batchSession.insert("addCustomProDO", propDO);
			}
			batchSession.commit();
		}
	}

	@Override
	public List<CustomModulePropDO> getCustomModulePropDOsById(long instanceId)
			throws Exception {
		return session.selectList("getCustomPropDOsById", instanceId);
	}

	@Override
	public CustomModulePropDO getCustomModulePropDOsByIdAndKey(long instanceId, String key)
			throws Exception {
		Map<String, Object> param = new HashMap<>(2);
		param.put("instanceId", instanceId);
		param.put("propKey", key);
		return session.selectOne("getCustomPropDOsByIdAndKey", param);
	}

	@Override
	public void updateCustomModulePropDOs(List<CustomModulePropDO> propDOs)
			throws Exception {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (CustomModulePropDO propDO : propDOs) {
				batchSession.update("updateCustomPropDO", propDO);
			}
			batchSession.commit();
		}
	}

	@Override
	public void updateCustomModulePropDO(CustomModulePropDO propDO) throws Exception {
		session.update("updateCustomPropDO", propDO);
	}

	@Override
	public void removeCustomProDOById(long instanceId) {
		session.delete("removeCustomProDOById", instanceId);
	}

	@Override
	public void removeCustomProDOByIdAndKey(long instanceId, String key) {
		Map<String, Object> param = new HashMap<>(2);
		param.put("instanceId", instanceId);
		param.put("propKey", key);
		session.delete("removeCustomProDOByIdAndKey", param);
	}

	@Override
	public List<CustomModulePropDO> getCustomPropDOs() {
		return session.selectList("getCustomPropDOs");
	}

	@Override
	public void removeCustomProDOByIds(List<Long> instanceIds) {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (long instanceId : instanceIds) {
				batchSession.update("removeCustomProDOById", instanceId);
			}
			batchSession.commit();
		}
	}
	 
}
