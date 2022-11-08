package com.mainsteam.stm.instancelib.objenum;
/**
 * 
 * 实例生命周期状态类
 * @author xiaoruqiang
 *
 */
public enum InstanceLifeStateEnum  {

	/**
	 * 初始化状态
	 */
	INITIALIZE,
	/**
	 * 初始化实例-未监控状态
	 */
	NOT_MONITORED,
	/**
	 * 初始化实例-已监控状态
	 */
	MONITORED,
	/**
	 * 已删除状态
	 */
	DELETED,
	/**
	 * 新增的
	 * */
	NEWER,
	/**
	 * 删除的
	 */
	REMOVED
}
