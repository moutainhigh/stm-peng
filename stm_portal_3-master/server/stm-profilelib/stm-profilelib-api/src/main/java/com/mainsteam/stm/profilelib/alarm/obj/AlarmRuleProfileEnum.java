/**
 * 
 */
package com.mainsteam.stm.profilelib.alarm.obj;

/**
 * 告警规则里的策略分类
 * 
 * @author ziw
 *
 */
public enum AlarmRuleProfileEnum {
	model_profile,biz_profile,
	/**SysLog*/
	sysLog,
	/**配置管理*/
	configfile_manager,
	/**其它*/
	other,
	
	/**流量*/
	netFlow,
	/**拓扑*/
	link,
	ip_mac_port
}
