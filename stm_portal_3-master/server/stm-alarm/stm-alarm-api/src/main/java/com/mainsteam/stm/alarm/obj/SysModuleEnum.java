package com.mainsteam.stm.alarm.obj;

public enum SysModuleEnum {
	/**监控组件：资源，指标*/
	MONITOR,
	/**业务组件*/
	BUSSINESS,	
	/**配置管理  */
	CONFIG_MANAGER,
	/**SystemLog*/
	SYSLOG,
	/**trap*/
	TRAP,
	/**流量分析*/
	NETFLOW,
	/**拓扑:链路*/
	LINK,
	/**拓扑:ip,mac,port*/
	IP_MAC_PORT,
	/**其它*/
	OTHER;
}
