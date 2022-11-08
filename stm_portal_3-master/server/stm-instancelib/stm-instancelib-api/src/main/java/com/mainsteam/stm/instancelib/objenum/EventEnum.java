package com.mainsteam.stm.instancelib.objenum;

/**
 *  定义操作事件
 */
public enum EventEnum {

	/**
	 * 实例添加事件
	 */
	INSTANCE_ADD_EVENT,
	/**
	 * 实例添加事件(针对link链路)
	 */
	INSTANCE_LINK_ADD_EVENT,
	/**
	 * 实例删除事件
	 */
	INSTANCE_DELETE_EVENT,
	
	/**
	 * 子资源实例删除事件
	 */
	INSTANCE_CHILDREN_DELETE_EVENT,
	
	/**
	 * 实例物理删除事件
	 */
	INSTANCE_PHYSICAL_DELETE_EVENT,
	
	/**
	 * 实例更新状态事件
	 */
	INSTANCE_UPDATE_STATE_EVENT,
	
	/**
	 * 实例更新名称事件
	 */
	INSTANCE_UPDATE_NAME_EVENT,
	
	/**
	 * 实例更新发现IP事件
	 */
	INSTANCE_UPDATE_DISCOVER_IP_EVENT,
	
	/**
	 * 实例更新发现节点事件
	 */
	INSTANCE_UPDATE_DISCOVER_NODE_EVENT,
	
	/**
	 * 实例刷新事件
	 */
	INSTANCE_REFRESH_EVENT,
	
	/**
	 * 发现属性添加事件
	 */
	DISCOVER_PROPERTY_ADD_EVENT,
	
	/**
	 * 发现属性更新事件
	 */
	DISCOVER_PROPERTY_UPDATE_EVENT,

	/**
	 * 发现属性删除事件
	 */
	DISCOVER_PROPERTY_DELETE_EVENT,
	
	/**
	 * 扩展属性添加事件
	 */
	EXTEND_PROPERTY_ADD_EVENT,
	
	/**
	 * 扩展属性更新事件
	 */
	EXTEND_PROPERTY_UPDATE_EVENT,

	/**
	 * 扩展属性删除事件
	 */
	EXTEND_PROPERTY_DELETE_EVENT,
	
	/**
	 * 模型属性添加事件
	 */
	MODULE_PROPERTY_ADD_EVENT,
	
	/**
	 * 模型属性更新事件
	 */
	MODULE_PROPERTY_UPDATE_EVENT,

	/**
	 * 模型属性删除事件
	 */
	MODULE_PROPERTY_DELETE_EVENT,
	
	/**
	 * 自定义属性添加事件
	 */
	CUSTOM_PROPERTY_ADD_EVENT,
	
	/**
	 * 自定义属性更新事件
	 */
	CUSTOM_PROPERTY_UPDATE_EVENT,

	/**
	 * 自定义属性删除事件
	 */
	CUSTOM_PROPERTY_DELETE_EVENT,
	
	/**
	 * 复合实例添加关系事件
	 */
	INSTANCE_RELATION_ADD_EVENT,
	
	/**
	 * 复合实例更新关系事件
	 */
	INSTANCE_RELATION_UPDATE_EVENT,
	
	/**
	 * 复合实例删除关系事件
	 */
	INSTANCE_RELATION_DELETE_EVENT
}
