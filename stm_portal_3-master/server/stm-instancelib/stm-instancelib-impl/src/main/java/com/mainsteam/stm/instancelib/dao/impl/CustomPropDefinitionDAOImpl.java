/**
 * 
 */
package com.mainsteam.stm.instancelib.dao.impl;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.mainsteam.stm.instancelib.dao.CustomPropDefinitionDAO;
import com.mainsteam.stm.instancelib.dao.pojo.CustomPropDefinitionDO;
import com.mainsteam.stm.instancelib.dao.pojo.CustomPropDefinitionQueryDO;

/**
 * @author ziw
 * 
 */
public class CustomPropDefinitionDAOImpl implements CustomPropDefinitionDAO {

	private SqlSession session;

	/**
	 * 
	 */
	public CustomPropDefinitionDAOImpl() {
	}

	public void setSession(SqlSession session) {
		this.session = session;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.instancelib.dao.CustomPropDefinitionDAO#
	 * getCustomPropDefinitionDOsByCategory(java.lang.String)
	 */
	@Override
	public List<CustomPropDefinitionDO> getCustomPropDefinitionDOsByCategory(
			String category) throws Exception {
		return session
				.selectList(
						"com.mainsteam.stm.instancelib.dao.CustomPropDefinitionDAO.getCustomPropDefinitionDOsByCategory",
						category);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.instancelib.dao.CustomPropDefinitionDAO#
	 * getCustomPropDefinitionDOByKey(java.lang.String)
	 */
	@Override
	public CustomPropDefinitionDO getCustomPropDefinitionDOByKey(String key)
			throws Exception {
		return session
				.selectOne(
						"com.mainsteam.stm.instancelib.dao.CustomPropDefinitionDAO.getCustomPropDefinitionDOByKey",
						key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.instancelib.dao.CustomPropDefinitionDAO#
	 * getCustomPropDefinitionDOsByKeys(java.util.List)
	 */
	@Override
	public List<CustomPropDefinitionDO> getCustomPropDefinitionDOsByKeys(
			List<String> keys) throws Exception {
		return session
				.selectList(
						"com.mainsteam.stm.instancelib.dao.CustomPropDefinitionDAO.getCustomPropDefinitionDOsByKeys",
						keys);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.instancelib.dao.CustomPropDefinitionDAO#
	 * getCustomPropDefinitionDOsByQuery
	 * (com.mainsteam.stm.instancelib.dao.pojo.CustomPropDefinitionQueryDO)
	 */
	@Override
	public List<CustomPropDefinitionDO> getCustomPropDefinitionDOsByQuery(
			CustomPropDefinitionQueryDO def) throws Exception {
		return session
				.selectList(
						"com.mainsteam.stm.instancelib.dao.CustomPropDefinitionDAO.queryCustomPropDefinitionDOs",
						def);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.instancelib.dao.CustomPropDefinitionDAO#
	 * insertCustomPropDefinitionDO
	 * (com.mainsteam.stm.instancelib.dao.pojo.CustomPropDefinitionDO)
	 */
	@Override
	public void insertCustomPropDefinitionDO(CustomPropDefinitionDO definitionDO)
			throws Exception {
		session.insert(
				"com.mainsteam.stm.instancelib.dao.CustomPropDefinitionDAO.insertCustomPropDefinitionDO",
				definitionDO);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.instancelib.dao.CustomPropDefinitionDAO#
	 * updateCustomPropDefinitionDO
	 * (com.mainsteam.stm.instancelib.dao.pojo.CustomPropDefinitionDO)
	 */
	@Override
	public void updateCustomPropDefinitionDO(CustomPropDefinitionDO definitionDO)
			throws Exception {
		session.update(
				"com.mainsteam.stm.instancelib.dao.CustomPropDefinitionDAO.updateCustomPropDefinitionDO",
				definitionDO);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.instancelib.dao.CustomPropDefinitionDAO#
	 * removeCustomPropDefinitionDOByKey(java.lang.String)
	 */
	@Override
	public int removeCustomPropDefinitionDOByKey(String key) throws Exception {
		return session
				.delete("com.mainsteam.stm.instancelib.dao.CustomPropDefinitionDAO.removeCustomPropDefinitionDOByKey",
						key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.instancelib.dao.CustomPropDefinitionBatchDAO#
	 * insertCustomPropDefinitionDOs(java.util.List)
	 */
	@Override
	public void insertCustomPropDefinitionDOs(
			List<CustomPropDefinitionDO> definitionDOs) throws Exception {
		for (CustomPropDefinitionDO customPropDefinitionDO : definitionDOs) {
			session.insert(
					"com.mainsteam.stm.instancelib.dao.CustomPropDefinitionDAO.insertCustomPropDefinitionDO",
					customPropDefinitionDO);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.instancelib.dao.CustomPropDefinitionBatchDAO#
	 * updateCustomPropDefinitionDOs(java.util.List)
	 */
	@Override
	public void updateCustomPropDefinitionDOs(
			List<CustomPropDefinitionDO> definitionDOs) throws Exception {
		for (CustomPropDefinitionDO customPropDefinitionDO : definitionDOs) {
			session.update(
					"com.mainsteam.stm.instancelib.dao.CustomPropDefinitionDAO.updateCustomPropDefinitionDO",
					customPropDefinitionDO);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.instancelib.dao.CustomPropDefinitionBatchDAO#
	 * removeCustomPropDefinitionDOByKey(java.util.List)
	 */
	@Override
	public int removeCustomPropDefinitionDOByKey(List<String> keys)
			throws Exception {
		int count = 0;
		for (String key : keys) {
			count += session
					.delete("com.mainsteam.stm.instancelib.dao.CustomPropDefinitionDAO.removeCustomPropDefinitionDOByKey",
							key);
		}
		return count;
	}
}
