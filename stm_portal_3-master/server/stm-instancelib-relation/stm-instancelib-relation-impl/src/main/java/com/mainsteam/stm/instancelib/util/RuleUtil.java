//package com.mainsteam.stm.instancelib.util;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import com.mainsteam.stm.instancelib.ojbenum.ResourceTypeEnum;
//
//public final class RuleUtil {
//
//	private static int BUSINESS = 1;
//	
//	private static int OTHER = 2;
//	
//	private static int HOST = 3;
//	
//	private static int NETWORKDEVICE = 4;
//	
//	private static Map<String,Integer> rules;
//	
//	public static int getRuleLevel(String name){
//		if(rules == null){
//			rules = new HashMap<>(4);
//			rules.put(ResourceTypeEnum.BUSINESS.toString(),BUSINESS);
//			rules.put(ResourceTypeEnum.OTHER.toString(), OTHER);
//			rules.put(ResourceTypeEnum.Host.toString(), HOST);
//			rules.put(ResourceTypeEnum.NetworkDevice.toString(), NETWORKDEVICE);
//		}
//		return rules.get(name);
//	}
//}
