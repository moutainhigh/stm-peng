package com.mainsteam.stm.resourcelog.dao;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.resourcelog.snmptrap.bo.SnmpLogBo;
import com.mainsteam.stm.resourcelog.snmptrap.bo.SnmpResourceBo;
import com.mainsteam.stm.resourcelog.strategy.bo.AlarmListBo;
import com.mainsteam.stm.resourcelog.strategy.bo.StrategyBo;
import com.mainsteam.stm.resourcelog.syslog.bo.SyslogResourceBo;

public interface SnmpTrapDao {
	
	/**
	 * 
	* @Title: updateSnmptrapType
	* @Description: 跟新snmptrap日志级别
	* @param strategyBo
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
	* @Title: saveSnmptrapHistroy
	* @Description:保存trap历史记录
	* @param snmpLogBo
	* @return  int
	* @throws
	 */
	int saveSnmptrapHistroy(SnmpLogBo snmpLogBo);
	
	/**
	 * 
	* @Title: countSnmptrapHistory
	* @Description: 计算trap历史记录个数
	* @param snmpLogBo
	* @return  int
	* @throws
	 */
	int countSnmptrapHistory(SnmpLogBo snmpLogBo);
	
	/**
	 * 
	* @Title: saveSnmpResourceSta
	* @Description: 保存trap resourceSta
	* @param snmpResourceBo
	* @return  int
	* @throws
	 */
	int saveSnmpResourceSta(SnmpResourceBo snmpResourceBo);
	
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
	* @param snmpResourceBo
	* @return  int
	* @throws
	 */
	int countResourceIp(SyslogResourceBo syslogResBo);
	
	/**
	 * 
	* @Title: countIp
	* @Description: 判断resourceIP是否存在
	* @param syslogResBo
	* @return  int
	* @throws
	 */
	int countIp(SyslogResourceBo syslogResBo);
	
	/**
	 * 
	* @Title: updateTrapLogNum
	* @Description: 更新trap日志数量
	* @param syslogResourceBo
	* @return  int
	* @throws
	 */
	int updateTrapLogNum(SyslogResourceBo syslogResourceBo);
}
