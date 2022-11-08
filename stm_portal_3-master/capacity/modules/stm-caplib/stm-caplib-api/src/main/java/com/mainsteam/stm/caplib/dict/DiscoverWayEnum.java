package com.mainsteam.stm.caplib.dict;

public enum DiscoverWayEnum {
	/**
	 * 使用Telnet发现方式
	 */
	TELNET,
	/**
	 * 使用SSH发现方式
	 */
	SSH,
	
	/**
	 * 使用WMI方式
	 */
	WMI,
	
	/**
	 * SNMP发现方式，主要用于网络设备和主机
	 */
	SNMP,
	/**
	 * 无发现方式
	 */
	NONE;
	
}
