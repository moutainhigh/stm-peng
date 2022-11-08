/**
 * 
 */
package com.mainsteam.stm.profilelib.alarm.obj;

/**
 * 告警级别
 * 
 * @author ziw
 * 
 */
public enum AlarmLevelEnum {
	/**
	 * 资源实例不可用异常
	 */
	down,
	/**
	 * 资源业务指标严重超标异常
	 */
	metric_error,
	/**
	 * 资源业务指标警告异常
	 */
	metric_warn,
	/**
	 * 资源业务指标从异常中恢复
	 */
	metric_recover,
	/**
	 * 资源业务指标未知异常
	 */
	metric_unkwon,
	/**
	 * 性能指标告警恢复
	 */
	perf_metric_recover
}
