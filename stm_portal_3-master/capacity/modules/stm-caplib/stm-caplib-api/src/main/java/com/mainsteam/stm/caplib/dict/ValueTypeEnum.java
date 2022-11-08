package com.mainsteam.stm.caplib.dict;

public enum ValueTypeEnum {
	/**
	 * ${key}，这种是发现信息
	 */
	DiscoveryInfo,
	/**
	 * ${key},文件发现信息
	 */
	CollectInfo,
	/**
	 * ${key}，这种是资源属性
	 */
	ResourceProperty,
	/**
	 * Parameter 可选参数
	 */
	Optional,

	/**
	 * ${key}，这种是sysoid,snmp模型中使用
	 */
	Sysoid;
}
