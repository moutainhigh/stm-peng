package com.mainsteam.stm.profilelib.objenum;

/**
 * 
 * 策略信息改变后，需要通知采集器。
 * 设计到具体修改操作枚举定义
 * @author xiaoruqiang
 *
 */
public enum ProfileChangeEnum {

	/**
	 * 更新指标监控
	 */
	UPDATE_METRIC_MONITOR,
	
	/**
	 *  更新指标监控频度
	 */
	UPDATE_METRIC_MONITORFEQ,
	
	/**
	 * 添加基线
	 */
	ADD_TIMELINE,
	
	/**
	 * 删除基线
	 */
	DELETE_TIMELINE,
	
	/**
	 * 修改基线
	 */
	UPDATE_TIMELINE_TIME,
	
	/**
	 * 更新基线指标监控
	 */
	UPDATE_TIMELINE_MONITOR,
	
	/**
	 *  更新基线监控频度
	 */
	UPDATE_TIMELINE_MONITORFEQ,
	
	/**
	 * 添加到监控
	 */
	ADD_MONITOR,
	
	/**
	 * 取消上一次监控
	 */
	CANCEL_LAST_MONITOR,
	
	/**
	 * 取消监控
	 */
	CANCEL_MONITOR,
	
	/**
	 * 删除策略
	 */
	DELETE_PROFILE
}
