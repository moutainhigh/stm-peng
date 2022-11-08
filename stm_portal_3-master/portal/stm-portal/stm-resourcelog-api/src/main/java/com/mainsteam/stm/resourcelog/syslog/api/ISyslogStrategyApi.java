package com.mainsteam.stm.resourcelog.syslog.api;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.resourcelog.strategy.api.IStrategyApi;
import com.mainsteam.stm.resourcelog.strategy.bo.AlarmListBo;
import com.mainsteam.stm.resourcelog.syslog.bo.SysLogRuleBo;
import com.mainsteam.stm.resourcelog.syslog.bo.SyslogResourceBo;


/**
 * <li>文件名称: ISyslogStrategyApi</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年11月10日
 * @author   ziwenwen
 */
public interface ISyslogStrategyApi extends IStrategyApi{
	/**
	 * <pre>
	 * 保存最新的策略规则到策略上
	 * 先删除关联关系
	 * </pre>
	 * @param strategyId 策略id
	 * @param rules 规则集合
	 * @return 返回保存成功的条数
	 */
	int saveStrategyRule(Long strategyId,List<SysLogRuleBo> rules);
	
	/**
	 * 保存一条策略规则
	 *
	 * @param strategyId
	 * @param rule
	 * @return
	 */
	int saveSingleStrateRule(Long strategyId, SysLogRuleBo rule);
	
	/**
	 * <pre>
	 * 根据策略id获取策略规则
	 * </pre>
	 * @param strategyId
	 * @return
	 */
	List<SysLogRuleBo> getRules(Long strategyId);
	
	/**
	 * 
	* @Title: updateRules
	* @Description: 更新规则信息
	* @param sysLogRuleBo
	* @return  int
	* @throws
	 */
	int updateRules(SysLogRuleBo sysLogRuleBo);
	
	/**
	 * 
	* @Title: delRules
	* @Description: 批量删除规则
	* @param ids
	* @return  int
	* @throws
	 */
	int delRules(Long[] ids);
	
	/**
	 * 
	* @Title: getRuleById
	* @Description: 根据规则ID得到规则信息
	* @param sysLogRuleId
	* @return  SysLogRuleBo
	* @throws
	 */
	SysLogRuleBo getRuleById(Long sysLogRuleId);
	
	/**
	 * 
	* @Title: updateStrateRule
	* @Description: 更新策略规则
	* @param sysLogRuleBo
	* @return  int
	* @throws
	 */
	int updateStrateRule(SysLogRuleBo sysLogRuleBo);
	
	/**
	 * 
	* @Title: getAlarmList
	* @Description: 查询告警列表
	* @param page
	* @return  List<AlarmListBo>
	* @throws
	 */
	List<AlarmListBo> getAlarmList(Page<AlarmListBo, AlarmListBo> page);
	
	/**
	 * 
	* @Title: updateRuleLevel
	* @Description: 更新告警级别
	* @param alarmLevel
	* @param ruleId
	* @return  int
	* @throws
	 */
	int updateRuleLevel(String alarmLevel,Long ruleId);
	
	/**
	 * 
	* @Title: isRuleNameExist
	* @Description: 当前策略中规则名字是否存在
	* @param ruleBo
	* @return  true存在 false不存在
	* @throws
	 */
	boolean isRuleNameExist(SysLogRuleBo ruleBo);

	/**
	 *
	 * @Title: updateResourceStrategy
	 * @Description: 更新资源下的策略
	 * @param syslogResourceBo
	 * @return  int
	 * @throws
	 */
	int updateResourceStrategy(SyslogResourceBo syslogResourceBo);

	 List<SyslogResourceBo> getResource(SyslogResourceBo syslogResourceBo);
}


