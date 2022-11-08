/**
 * 
 */
package com.mainsteam.stm.node;

/**
 * 节点功能类型
 * 
 * @author ziw
 * 
 */
public enum NodeFunc {
	portal,
	/**采集器*/
	collector,
	/**处理器*/
	processer,
	/** SysLog与snmp服务* */
	trap
}
