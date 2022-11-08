/**
 * 
 */
package com.mainsteam.stm.profilelib.dao.impl;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.mainsteam.stm.profilelib.alarm.obj.SendWayEnum;
import com.mainsteam.stm.profilelib.dao.AlarmRuleConditionDAO;
import com.mainsteam.stm.profilelib.po.AlarmRuleConditionPO;

/**
 * @author ziw
 * 
 */
public class AlarmRuleConditionDAOImpl implements AlarmRuleConditionDAO {

	private SqlSession session;

	/**
	 * 
	 */
	public AlarmRuleConditionDAOImpl() {
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
	 * .oc.profilelib.po.AlarmRuleConditionPO)
	 */
	@Override
	public void insert(AlarmRuleConditionPO conditionPO) {
		session.insert(
				"com.mainsteam.stm.profilelib.dao.AlarmRuleConditionDAO.insert",
				conditionPO);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.profilelib.dao.AlarmRuleConditionDAO#updateById(com
	 * .mainsteam .oc.profilelib.po.AlarmRuleConditionPO)
	 */
	@Override
	public void updateById(AlarmRuleConditionPO conditionPO) {
		session.update(
				"com.mainsteam.stm.profilelib.dao.AlarmRuleConditionDAO.update",
				conditionPO);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.profilelib.dao.AlarmRuleConditionDAO#delete(com.
	 * mainsteam .oc.profilelib.po.AlarmRuleConditionPO)
	 */
	
	@Override
	public void delete(long ruleID,SendWayEnum sendWay) {
		AlarmRuleConditionPO conditionPO=new AlarmRuleConditionPO();
		conditionPO.setRuleId(ruleID);
		if(sendWay!=null){
			conditionPO.setSendWay(sendWay.name());
		}
		session.delete(
				"com.mainsteam.stm.profilelib.dao.AlarmRuleConditionDAO.delete",
				conditionPO);
	}
	@Override
	public void delete(long ruleID) {
		delete(ruleID,null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.profilelib.dao.AlarmRuleConditionDAO#get(com.mainsteam
	 * .oc.profilelib.po.AlarmRuleConditionPO)
	 */
	@Override
	public List<AlarmRuleConditionPO> get(AlarmRuleConditionPO conditionPO) {
		return session.selectList(
				"com.mainsteam.stm.profilelib.dao.AlarmRuleConditionDAO.get",
				conditionPO);
	}

	@Override
	public int updateEnabledById(AlarmRuleConditionPO mainPO) {
		return session
				.update("com.mainsteam.stm.profilelib.dao.AlarmRuleConditionDAO.updateEnabled",
						mainPO);
	}
}
