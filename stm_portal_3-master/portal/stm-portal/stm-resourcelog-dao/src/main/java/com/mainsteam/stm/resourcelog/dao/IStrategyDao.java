package com.mainsteam.stm.resourcelog.dao;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.resourcelog.strategy.bo.StrategyBo;
import com.mainsteam.stm.resourcelog.syslog.bo.SyslogResourceBo;

public interface IStrategyDao {

	/**
	 * 保存syslog基本信息
	 *
	 * @param strategy
	 * @return
	 */
	int saveStrategyBasic(StrategyBo strategy);
	
	/**
	 * 
	* @Title: batchDel
	* @Description: 批量删除策略
	* @param ids
	* @return  int
	* @throws
	 */
	int batchDel(Long[] ids);
	
	int batchDelBase(Long[] ids);
	
	int batchDelRule(Long[] strategyIds);
	
	int batchDelResource(Long[] strategyIds);
	/**
	 * 
	* @Title: queryStrategy
	* @Description: 根据用户ID查询策略
	* @param page  void
	* @throws
	 */
	List<StrategyBo> queryStrategy(Page<StrategyBo, StrategyBo> page);
	
	/**
	 * 
	* @Title: getStrategyResource
	* @Description: 根据策略ID查询策略下的资源
	* @param strategyId
	* @return  List<SyslogResourceBo>
	* @throws
	 */
	List<SyslogResourceBo> getStrategyResource(Long strategyId);
	
	/**
	 * 
	* @Title: updateMonitorState
	* @Description: 更新监控状态
	* @param syslogResourceBo
	* @return  int
	* @throws
	 */
	int updateMonitorState(Long[] id, int isMonitor);
	
	/**
	 * 
	* @Title: get
	* @Description: 根据策略ID查询策略基本信息
	* @param strategyId
	* @return  StrategyBo
	* @throws
	 */
	StrategyBo get(Long strategyId);
	
	/**
	 * 
	* @Title: updateStrategyBasic
	* @Description: 更新策略基本信息
	* @param strategy
	* @return  int
	* @throws
	 */
	int updateStrategyBasic(StrategyBo strategy);
	
	/**
	 * 
	* @Title: getResourceBos
	* @Description: 根据类型查询资源
	* @return  List<SyslogResourceBo>
	* @throws
	 */
	List<SyslogResourceBo> getResourceBos(Page<SyslogResourceBo, StrategyBo> page);
	
	/**
	 * 
	* @Title: updateResourceStrategy
	* @Description: 更新资源下的策略
	* @param syslogResourceBo
	* @return  int
	* @throws
	 */
	int updateResourceStrategy(SyslogResourceBo syslogResourceBo);
	
	/**
	 * 
	* @Title: countRes
	* @Description: 判断资源是否在该策略下
	* @param resBo
	* @return  int
	* @throws
	 */
	int countRes(SyslogResourceBo resBo);
	
	/**
	 * 
	* @Title: delResourceByStrategyId
	* @Description: 根据策略ID删除资源
	* @param strategyId
	* @return  int
	* @throws
	 */
	int delResourceByStrategyId(Long strategyId);
	
	/**
	 * 保存策略资源
	 *
	 * @param list
	 * @return
	 */
	int saveStrategyResource(List<SyslogResourceBo> list);
	
	/**
	 * 
	* @Title: saveResourceSta
	* @Description:保存resourceSta
	* @param list
	* @return  int
	* @throws
	 */
	int saveResourceSta(List<SyslogResourceBo> list);
	
	/**
	 * 
	* @Title: updateAllResourceSta
	* @Description: 更新当日产生数量为0
	* @return  int
	* @throws
	 */
	int updateAllResourceSta(SyslogResourceBo resBo);
	
	/**
	 * 
	* @Title: delRelationResource
	* @Description: 删除资源和策略的关联关系
	* @param resBo
	* @return  int
	* @throws
	 */
	int delRelationResource(SyslogResourceBo resBo);
	
	/**
	 * 
	* @Title: delRelationResource
	* @Description: 删除资源和策略的关联关系
	* @param resBo
	* @return  int
	* @throws
	 */
	int delRelationByResourceIdAndStrategyId(SyslogResourceBo resBo);
	
	/**
	 * 
	* @Title: isStrategyNameExist
	* @Description: 在同一个域下策略名是否存在
	* @param strategyBo
	* @return  boolean
	* @throws
	 */
	boolean isStrategyNameExist(StrategyBo strategyBo);
	
}
