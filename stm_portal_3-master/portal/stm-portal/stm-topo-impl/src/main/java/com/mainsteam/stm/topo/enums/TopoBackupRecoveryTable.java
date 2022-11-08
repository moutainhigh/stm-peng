package com.mainsteam.stm.topo.enums;

/**
 * 拓扑数据备份/恢复表枚举
 * @author zwx
 */
public enum TopoBackupRecoveryTable {
	STM_TOPO_AUTH_SETTING,		//拓扑权限设置表
	STM_TOPO_BACKBORD_REAL,		//背板信息-实时表
	STM_TOPO_ICON,				//拓扑图的图标
	STM_TOPO_LATEST_MAC,			//新增MAC表
	STM_TOPO_LINK,				//链接关系图元表
	STM_TOPO_MAC_BASE,			//MAC基准表
	STM_TOPO_MAC_HISTORY,		//MAC历史变更表
	STM_TOPO_MAC_RUNTIME,		//MAC实时表
	STM_TOPO_NODE,				//设备节点图元表
	STM_TOPO_OTHERS,				//拓扑图上的其他节点
	STM_TOPO_SETTING,			//拓扑管理中的所有配置项
	STM_TOPO_SUBTOPO,			//子拓扑
	STM_TOPO_GROUP				//组，包含多个设备节点，用来绘制层
//	STM_TOPO_SUBTOPO_NODE,		//二层拓扑的节点表（无用）
//	stm_topo_backbord_base		//背板基准表（只在建表是初始化，以后不会更改数据，故不用备份）
}
