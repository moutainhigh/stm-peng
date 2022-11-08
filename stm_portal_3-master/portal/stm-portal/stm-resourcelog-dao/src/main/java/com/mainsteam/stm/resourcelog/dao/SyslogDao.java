/**
 * 
 */
package com.mainsteam.stm.resourcelog.dao;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.resourcelog.strategy.bo.AlarmListBo;
import com.mainsteam.stm.resourcelog.syslog.bo.SysLogRuleBo;
import com.mainsteam.stm.resourcelog.syslog.bo.SyslogBo;
import com.mainsteam.stm.resourcelog.syslog.bo.SyslogResourceBo;

/**
 * <li>文件名称: SyslogDao.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 接口流量统计</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年11月12日
 * @author   lil
 */
public interface SyslogDao {
	
	/**
	 * 保存syslog策略规则
	 *
	 * @param rules
	 * @return
	 */
	int saveStrategyRule(SysLogRuleBo rule);
	
	/**
	 * 
	* @Title: getSysLogRule
	* @Description: 根据策略ID查询syslog策略规则
	* @param strategyId
	* @return  List<SysLogRuleBo>
	* @throws
	 */
	List<SysLogRuleBo> getSysLogRule(Long strategyId);
	
	/**
	 * 
	* @Title: updateRules
	* @Description: 更新规则
	* @param sysLogRuleBo
	* @return  int
	* @throws
	 */
	int updateRules(SysLogRuleBo sysLogRuleBo);
	
	/**
	 * 
	* @Title: batchRules
	* @Description: 批量删除规则
	* @param ids
	* @return  int
	* @throws
	 */
	int batchRules(Long[] ids);
	
	/**
	 * 
	* @Title: getRuleById
	* @Description: 根据规则ID获取规则信息
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
	List<AlarmListBo> getSyslogHistory(Page<AlarmListBo, AlarmListBo> page);
	
	/**
	 * 
	* @Title: saveStrategyRule
	* @Description: 批量保存策略集合
	* @param strategyId
	* @param rules
	* @return  int
	* @throws
	 */
	int saveStrategyRule(List<SysLogRuleBo> rules);
	
	/**
	 * 
	* @Title: getResourceBo
	* @Description: 根据条件查询资源
	* @param resBo
	* @return  List<SyslogResourceBo>
	* @throws
	 */
	List<SyslogResourceBo> getResourceBo(SyslogResourceBo resBo);
	
	/**
	 * 
	* @Title: saveSyslogHistroy
	* @Description: 保存syslog历史记录
	* @param syslogBo
	* @return  int
	* @throws
	 */
	int saveSyslogHistroy(SyslogBo syslogBo);
	
	/**
	 * 
	* @Title: countHistory
	* @Description: 计算当日产生的数量和总量
	* @param syslogBo
	* @return  int
	* @throws
	 */
	int countHistory(SyslogBo syslogBo);
	
	/**
	 * 
	* @Title: updateResourceSta
	* @Description: 更新resourceSta
	* @param resBo
	* @return  int
	* @throws
	 */
	int updateResourceSta(SyslogResourceBo resBo);
	
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
	* @Title: deleteResourceByResourceId
	* @Description: 批量删除资源表及其相关联
	* @param resourceId
	* @return  int
	* @throws
	 */
	int deleteResourceByResourceId(long[] resourceId);
	
	/**
	 * 
	* @Title: isRuleNameExist
	* @Description: 当前策略中规则名字是否存在
	* @param ruleBo
	* @return  true存在 false不存在
	* @throws
	 */
	boolean isRuleNameExist(SysLogRuleBo ruleBo);
}
