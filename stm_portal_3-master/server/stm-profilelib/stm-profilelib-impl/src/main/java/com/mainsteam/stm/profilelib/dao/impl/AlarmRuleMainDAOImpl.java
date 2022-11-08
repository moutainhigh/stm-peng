/**
 * 
 */
package com.mainsteam.stm.profilelib.dao.impl;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.mainsteam.stm.profilelib.dao.AlarmRuleMainDAO;
import com.mainsteam.stm.profilelib.po.AlarmRuleMainPO;

/**
 * @author ziw
 * 
 */
public class AlarmRuleMainDAOImpl implements AlarmRuleMainDAO {

	private SqlSession session;

	/**
	 * 
	 */
	public AlarmRuleMainDAOImpl() {
	}

	/**
	 * @param session
	 *            the session to set
	 */
	public final void setSession(SqlSession session) {
		this.session = session;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.profilelib.dao.AlarmRuleMainDAO#insert(com.mainsteam
	 * .oc.profilelib.po.AlarmRuleMainPO)
	 */
	@Override
	public int insert(AlarmRuleMainPO mainPO) {
		return session.insert(
				"com.mainsteam.stm.profilelib.dao.AlarmRuleMainDAO.insert",
				mainPO);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.profilelib.dao.AlarmRuleMainDAO#updateById(com.mainsteam
	 * .oc.profilelib.po.AlarmRuleMainPO)
	 */
	@Override
	public int updateById(AlarmRuleMainPO mainPO) {
		return session.update(
				"com.mainsteam.stm.profilelib.dao.AlarmRuleMainDAO.update",
				mainPO);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.profilelib.dao.AlarmRuleMainDAO#delete(com.mainsteam
	 * .oc.profilelib.po.AlarmRuleMainPO)
	 */
	@Override
	public int delete(AlarmRuleMainPO mainPO) {
		return session.delete(
				"com.mainsteam.stm.profilelib.dao.AlarmRuleMainDAO.delete",
				mainPO);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.profilelib.dao.AlarmRuleMainDAO#get(com.mainsteam
	 * .oc.profilelib.po.AlarmRuleMainPO)
	 */
	@Override
	public List<AlarmRuleMainPO> get(AlarmRuleMainPO mainPO) {
		return session.selectList(
				"com.mainsteam.stm.profilelib.dao.AlarmRuleMainDAO.get",
				mainPO);
	}
}
