package com.mainsteam.stm.resourcelog.strategy.api;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.resourcelog.strategy.bo.StrategyBo;
import com.mainsteam.stm.resourcelog.syslog.bo.SyslogResourceBo;

/**
 * <li>文件名称: IStrategyApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年11月10日
 * @author   ziwenwen
 */
public interface IStrategyApi {

	/**
	 * <pre>
	 * 保存或新建策略基本信息
	 * </pre>
	 * @return 返回操作成功的条数
	 */
	int saveStrategyBasic(StrategyBo strategy);
	
	/**
	 * 
	* @Title: updateStrategyBasic
	* @Description: 修改策略基本信息
	* @param strategy
	* @return  int
	* @throws
	 */
	int updateStrategyBasic(StrategyBo strategy);
	
	/**
	 * 
	* @Title: getStrategyBasic
	* @Description: 根据ID获取策略基本信息
	* @param strategyId
	* @return  StrategyBo
	* @throws
	 */
	StrategyBo getStrategyBasic(Long strategyId);
	
	/**
	 * <pre>
	 * 将资源挂到一个策略上（如果后期确定一个资源只能使用一个策略，需要注意删除资源以前的策略关系）
	 * 先删除关联关系
	 * </pre>
	 * @param strategyId 策略id
	 * @return 返回操作成功的条数
	 */
	int saveStrategyResource(Long strategyId,List<SyslogResourceBo> listRes,int strategyType);
	
	/**
	 * <pre>
	 * 根据策略id集合删除策略
	 * </pre>
	 * @param strategyIds
	 * @return 返回删除成功的条数
	 */
	int delStrategy(Long[] strategyIds);
	
	/**
	 * <pre>
	 * 根据域id分页查询策略基础信息集合，并将查询出的基础信息保存到分页对象中
	 * </pre>
	 */
	void queryStrategy(Page<StrategyBo, StrategyBo> page);
	
	/**
	 * <pre>
	 * 根据策略id获取资源实例
	 * </pre>
	 * @param strategyId
	 * @return
	 */
	List<SyslogResourceBo> getResource(Long strategyId);

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
	* @Title: updateMonitorState
	* @Description: 更新监控状态
	* @param syslogResourceBo
	* @return  int
	* @throws
	 */
	int updateMonitorState(Long[] id, int isMonitor);
	
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
	* @Title: countRes
	* @Description: 判断资源是否在该策略下
	* @param resBo
	* @return  int
	* @throws
	 */
	int countRes(SyslogResourceBo resBo);
	
	/**
	 * 
	* @Title: isStrategyNameExist
	* @Description: 在同一个域下策略名是否存在
	* @param strategyBo
	* @return  true为存在，false为不存在
	* @throws
	 */
	boolean isStrategyNameExist(StrategyBo strategyBo);
}


