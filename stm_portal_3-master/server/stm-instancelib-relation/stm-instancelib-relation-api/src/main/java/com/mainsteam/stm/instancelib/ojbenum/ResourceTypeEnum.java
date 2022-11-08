package com.mainsteam.stm.instancelib.ojbenum;

import com.mainsteam.stm.caplib.dict.CapacityConst;

/**
 * 关系系统资源实例类型
 * @author xiaoruqiang
 */
public enum ResourceTypeEnum {

	/**
	 * 业务
	 */
	BUSINESS,
	/**
	 * 主机
	 */
	Host{
		public String toString(){
			return CapacityConst.HOST;
		}
	},
	/**
	 * 网络设备
	 */
	NetworkDevice{
		public String toString(){
			return CapacityConst.NETWORK_DEVICE;
		}
	},
	/**
	 * 主机下面的资源（基础协议，存储，应用server，中间件，数据库等。除网络，主机之外的所有资源）
	 */
	OTHER,
	/**
	 * 子资源
	 */
	SUB_RESOURCE

}
