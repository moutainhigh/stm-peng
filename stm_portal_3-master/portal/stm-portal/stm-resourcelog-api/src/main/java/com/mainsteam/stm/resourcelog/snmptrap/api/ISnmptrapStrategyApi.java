package com.mainsteam.stm.resourcelog.snmptrap.api;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.resourcelog.strategy.api.IStrategyApi;
import com.mainsteam.stm.resourcelog.strategy.bo.AlarmListBo;
import com.mainsteam.stm.resourcelog.strategy.bo.StrategyBo;
import com.mainsteam.stm.resourcelog.syslog.bo.SyslogResourceBo;

/**
 * <li>文件名称: ISnmptrapStrategyApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年11月10日
 * @author   ziwenwen
 */
public interface ISnmptrapStrategyApi extends IStrategyApi{
	
	/**
	 * 
	* @Title: updateSnmptrapType
	* @Description: 保存SNMPTrap策略
	* @return  int
	* @throws
	 */
	int updateSnmptrapType(StrategyBo strategyBo);
	

	/**
	 * 
	* @Title: getSnmptrapHistory
	* @Description: 查询snmptrap历史记录
	* @param page
	* @return  List<AlarmListBo>
	* @throws
	 */
	List<AlarmListBo> getSnmptrapHistory(Page<AlarmListBo, AlarmListBo> page);
	
	/**
	 * 
	* @Title: saveIpAddress
	* @Description: 保存trap的IP地址
	* @param resourceBo
	* @return  int
	* @throws
	 */
	int saveIpAddress(SyslogResourceBo resourceBo);
	
	/**
	 * 
	* @Title: getTrapLog
	* @Description: 查询trap日志
	* @return  List<SyslogResourceBo>
	* @throws
	 */
	List<SyslogResourceBo> getTrapLog(Page<SyslogResourceBo, StrategyBo> page);
	
	/**
	 * 
	* @Title: countResourceIp
	* @Description: 判断IP是否存在
	* @param syslogResBo
	* @return  int
	* @throws
	 */
	boolean isIpExist(SyslogResourceBo syslogResBo);
	
	/**
	 * 
	* @Title: saveIpResource
	* @Description: 保存IP资源集合
	* @param list
	* @return  int
	* @throws
	 */
	int saveIpResource(List<SyslogResourceBo> list);
}


