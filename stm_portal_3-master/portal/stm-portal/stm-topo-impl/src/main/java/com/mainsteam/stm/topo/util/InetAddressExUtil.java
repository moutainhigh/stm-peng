package com.mainsteam.stm.topo.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * IP地址封装类扩展工具
 * @author zwx
 */
public class InetAddressExUtil {
	
	/**
	 * 根据主机ip获取主机名称
	 * 如果IP地址不存在或DNS服务器不允许进行IP地址和域名映射，就返回这个IP地址
	 * @param ip 主机ip
	 * @return hostName
	 */
	public static String getHostName(String ip){
		String hostName = "";
		InetAddress netAddress = getInetAddress(ip);
		if(null != netAddress){
			hostName = netAddress.getHostName();			//主机别名
//			hostName = netAddress.getCanonicalHostName();	//主机名
			
//			Properties properties = System.getProperties();	//获取主机系统属性
//			Set<String> set = properties.stringPropertyNames(); //获取java虚拟机和系统的信息。
//			for(String name : set){
//				System.out.println(name + ":" + properties.getProperty(name));
//			}
		}
		return hostName;
	}
	
	/**
	 * 根据主机ip获取主机
	 * @param ip
	 * @return InetAddress
	 */
	private static InetAddress getInetAddress(String ip){
	    try{
	    	return InetAddress.getByName(ip);
	    }catch(UnknownHostException e){
			e.printStackTrace();
	    }
	    return null;
	}
}
