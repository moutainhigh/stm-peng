package com.mainsteam.stm.resourcelog.util;

import java.util.HashMap;
@SuppressWarnings({ "rawtypes", "unchecked" })
public class UtilTrap {
	public static final HashMap map;
	static
	{
		map = new HashMap();
		map.put("0", "coldStart:代理进行了初始化");
		map.put("1", "warmStart:代理进行了重新初始化");
		map.put("2","linkDown:一个接口从工作状态变为故障状态 ");
		map.put("3","linkUp:一个接口从故障状态变为工作状态");
		map.put("4","authenticationFailure:从SNMP管理进程接收到具有一个无效共同体的报文");
		map.put("5","egpNeighborLoss:一个EGP相邻路由器变为故障状态 ");
	}
	public static String parseTrapContent(String old)
	{
		return (String)map.get(old);
	}

}
