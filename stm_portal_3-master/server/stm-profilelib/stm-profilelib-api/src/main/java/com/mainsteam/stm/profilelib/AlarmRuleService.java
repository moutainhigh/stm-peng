/**
 * 
 */
package com.mainsteam.stm.profilelib;

import java.util.List;

import com.mainsteam.stm.profilelib.alarm.obj.AlarmConditonEnableInfo;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmConditonQuery;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmRule;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmRuleProfileEnum;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmSendCondition;
import com.mainsteam.stm.profilelib.alarm.obj.SendWayEnum;

/**
 * 告警服务
 * 
 * @author ziw
 * 
 */
public interface AlarmRuleService {
	/**
	 * 添加告警规则
	 * 
	 * @param rule
	 */
	public void addAlarmRule(AlarmRule rule);

	/**
	 * 添加一条告警发送规则
	 * 
	 * @param alarmSendCondition
	 */
	public void addAlarmConditon(long ruleId,
			AlarmSendCondition alarmSendCondition);

	/**
	 * 更新一条告警发送规则
	 * 
	 * @param alarmSendCondition
	 */
	public void updateAlarmConditon(long ruleId,
			AlarmSendCondition alarmSendCondition);

	/**
	 * 改变指定告警发送条件的激活状态。
	 * 
	 * @param alarmConditonEnableInfos
	 */
	public void changeAlarmConditionEnabled(
			List<AlarmConditonEnableInfo> alarmConditonEnableInfos);

	public void deleteAlarmCondition(long ruleID,SendWayEnum sendWay);
	/**
	 * 
	 * 获取一条告警发送规则
	 * 
	 * @return AlarmSendCondition
	 */
	public AlarmSendCondition getAlarmSendCondition(long ruleId,SendWayEnum sendWay);

	//
	// /**
	// * 更新告警规则
	// *
	// * @param rule
	// */
	// public void updateAlarmRule(AlarmRule rule);

	/**
	 * 删除告警规则
	 * 
	 * @param ruleIds
	 *            告警规则id
	 */
	public void deleteAlarmRuleById(long[] ruleIds);

	/**
	 * 查询指定策略下的告警规则
	 * 
	 * @param profileId
	 *            告警规则id
	 * @return 告警规则列表
	 */
	public List<AlarmRule> getAlarmRulesByProfileId(long profileId,
			AlarmRuleProfileEnum profileType);

	/**
	 * 根据用户id来查询设置了该用户的所有的告警规则
	 * 
	 * @param userId
	 * @return List<AlarmRule>
	 */
	public List<AlarmRule> getAlarmRulesByUserId(String userId,
			AlarmRuleProfileEnum profileType);

	/**
	 * 查询所有的告警规则
	 * 
	 * @return List<AlarmRule> 告警规则列表
	 */
	public List<AlarmRule> getAllAlarmRules(AlarmRuleProfileEnum profileType);

	public List<AlarmSendCondition> getAlarmSendConditionByTemplateId(long templateId, SendWayEnum sendWay);
}
