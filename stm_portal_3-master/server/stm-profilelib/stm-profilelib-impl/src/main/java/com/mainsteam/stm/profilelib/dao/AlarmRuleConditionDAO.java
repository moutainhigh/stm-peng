/**
 * 
 */
package com.mainsteam.stm.profilelib.dao;

import java.util.List;

import com.mainsteam.stm.profilelib.alarm.obj.SendWayEnum;
import com.mainsteam.stm.profilelib.po.AlarmRuleConditionPO;

/**
 * 告警规则主表操作
 * 
 * @author ziw
 * 
 */
public interface AlarmRuleConditionDAO {

	/**
	 * 插入
	 * 
	 * @param mainPO
	 */
	public void insert(AlarmRuleConditionPO mainPO);

	/**
	 * 根据id更新
	 * 
	 * @param mainPO
	 */
	public void updateById(AlarmRuleConditionPO mainPO);
	
	/**
	 * 根据id更新enabled字段
	 * 
	 * @param mainPO
	 * @return 
	 */
	public int updateEnabledById(AlarmRuleConditionPO mainPO);

	/**
	 * 删除
	 * 
	 * @param mainPO
	 */
	public void delete(long ruleID);
	/**
	 * 删除
	 * 
	 * @param mainPO
	 */
	public void delete(long ruleID,SendWayEnum sendWay);
	/**
	 * 查询
	 * 
	 * @param mainPO
	 * @return List<AlarmRuleConditionPO>
	 */
	public List<AlarmRuleConditionPO> get(AlarmRuleConditionPO mainPO);
}
