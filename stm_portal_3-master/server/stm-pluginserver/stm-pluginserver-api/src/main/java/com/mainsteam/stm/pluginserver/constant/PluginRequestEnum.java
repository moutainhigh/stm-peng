/**
 * 
 */
package com.mainsteam.stm.pluginserver.constant;

/**
 * Plugin数据请求类型
 * 
 * @author ziw
 * 
 */
public enum PluginRequestEnum {
	/**
	 * 立即取值，该类型的数据不参与计算状态，不保存数据
	 */
	immediate,

	/**
	 * 监控取值，计算状态，计算事件告警等等
	 */
	monitor,

	/**
	 * 请求trap监控
	 */
	trap,

	/**
	 * 取消trap监控的请求
	 */
	close_trap,

	/**
	 * 发现取值，不参与计算，不匹配资源实例
	 */
	discovery,
	/**
	 * 发现取值结束，关闭发现使用的session。
	 */
	discovery_end
}
