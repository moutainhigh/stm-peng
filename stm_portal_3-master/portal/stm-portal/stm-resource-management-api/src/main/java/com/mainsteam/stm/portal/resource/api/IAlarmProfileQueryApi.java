package com.mainsteam.stm.portal.resource.api;

import java.util.List;
import java.util.Map;

import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.resource.bo.HighChartsDataBo;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmConditonEnableInfo;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmRule;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmRuleProfileEnum;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmSendCondition;
import com.mainsteam.stm.profilelib.alarm.obj.SendWayEnum;
import com.mainsteam.stm.profilelib.obj.ProfileInfo;
import com.mainsteam.stm.system.um.user.bo.User;
import com.mainsteam.stm.system.um.user.vo.UserConditionVo;

public interface IAlarmProfileQueryApi {
	
	/**
	 * 获取所有维护人员用于告警规则添加
	 * 
	 * @return
	 */
	public void getUser(Page<User, User> page);
	
	/**
	 * 获取用户
	 * 
	 * @return
	 */
	public User getUserById(long userId);
	
	/**
	 * 获取父级资源模型
	 * 
	 * @return
	 */
    public List<Map<String,String>> getParentCategory();
    
    /**
	 * 获取子资源模型
	 * 
	 * @return
	 */
	public List<Map<String,String>> getChildCategory(String profileID);
	
	/**
	 * 获取所有告警规则
	 * 
	 * @return
	 */
	public List<AlarmRule> getAllAlarmRules(String profileType);
	
	/**
	 * 根据资源ID获取策略
	 * 
	 * @return
	 */
	public List<ProfileInfo> getProfileBasicInfoByResourceId(List<String> resourceIdList);
	
	/**
	 * 只查询资源模型类别下的资源实例ID
	 * 
	 * @return
	 */
	public List<String> getChildCategoryIdList(String profileID);
	
	/**
	 * 根据前台选择的资源模型,返回响应类型的策略
	 * 
	 * @return
	 */
	public List<ProfileInfo> getProfileInfoByChoseResource(String parentResourceId ,String childResourceId );
	
	/**
	 * 根据资源ID获取告警规则
	 * 
	 * @return
	 */
	public List<AlarmRule> getAlarmRulesByProfileId(long profileId,String profileType);
	
	/**
	 * 根据ruleID,type查询alarmSendCondition
	 * 
	 * @return
	 */
	public AlarmSendCondition getAlarmSendCondition(SendWayEnum sendWay,long alarmRulesId);
	
	/**
	 * 添加alarmSendCondition
	 * 
	 * @return
	 */
	public void addAlarmCondition(AlarmSendCondition alarmSendCondition ,long ruleId);
	
	/**
	 * 编辑alarmSendCondition
	 * 
	 * @return
	 */
	public void updateAlarmCondition(AlarmSendCondition alarmSendCondition ,long ruleId);
	
	/**
	 * 批量更新AlarmCondition发送状态
	 * 
	 * @return
	 */
	public void changeAlarmConditionEnabled(List<AlarmConditonEnableInfo> aceiList);
	
	/**
	 * 删除告警规则
	 * 
	 * @return
	 */
	public void deleteAlarmRule(long[] ruleId);
	
	/**
	 * 添加告警规则
	 * 
	 * @return
	 */
	public void addAlarmRule(AlarmRule ar);
	
	/**
	 * 根据userId获取告警规则
	 * 
	 * @return
	 */
	public List<AlarmRule> getAlarmRulesByUserId(String userId,String profileType);
	
	/**
	 * 将指定profileId的告警规则绑定到新的profileId上
	 * 
	 * @return
	 */
	public void bindAlarmRuleToNewProfile(Long profileIdSource,Long profileIdTarget,AlarmRuleProfileEnum profileType);
	
	/**
	 * 策略操作资源时,减少的域ID,根据减少的域ID删除,配置在策略上的告警接收人
	 * 
	 * @return
	 */
	public void filterUserByResourceDomainIdList(List<Long> domainIdList,Long profileId);
	
	public void filterUserByResourceDomainIdArr(Long[] domainIdArr,Long profileId);
	
	/**
	 * 根据策略ID获取资源实例Id
	 * 
	 * @return
	 */
	public List<Long> getProfileResourceInstance(long profileId);

}
