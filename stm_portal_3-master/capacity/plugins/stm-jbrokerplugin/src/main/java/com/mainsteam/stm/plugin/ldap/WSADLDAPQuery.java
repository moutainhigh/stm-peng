package com.mainsteam.stm.plugin.ldap;

import java.util.Map;

import netscape.ldap.LDAPConnection;

import com.mainsteam.stm.plugin.parameter.JBrokerParameter;

/**
 * windows sever2012AD目录服务器采集
 * @author xiehf
 *
 */
public class WSADLDAPQuery extends LDAPQuery {
	private static final String WSAD = "wsad";
	private static final String LIMITS = "limits";
	
	/**
	 * 最大接收缓冲区
	 * @param oneobj
	 * @return
	 */
	public static String searchMaxReceiveBuffer(JBrokerParameter parameter) throws Exception{
		Map<String, String[]> map = LDAPConfigManager.getCmds(WSAD);
		String[] array = map.get(LIMITS);
		String domainName = parameter.getDomainName();
		String dn = array[0].replace("*", domainName);
		LDAPConnection connection = (LDAPConnection)parameter.getConnection();
		String value =  query(connection, dn, new String[]{array[1]}, array[1]); 
		if(value != null){
			String[] oneValue = value.split(";");
			if(oneValue != null && oneValue.length > 0){
				for(int i =0 ;i < oneValue.length ;i++ ){
					if(oneValue[i].contains("MaxReceiveBuffer")){
						return	oneValue[i].substring(17);
					} 
					
				}
			}
		}
		 return null;
	}
	
	/**
	 * 处理最大数据大小
	 * @param oneobj
	 * @return
	 */
	public static String searchMaxDatagramRecv(JBrokerParameter parameter) throws Exception{
		Map<String, String[]> map = LDAPConfigManager.getCmds(WSAD);
		String[] array = map.get(LIMITS);
		String domainName = parameter.getDomainName();
		String dn = array[0].replace("*", domainName);
		LDAPConnection connection = (LDAPConnection)parameter.getConnection();
		String value =  query(connection, dn, new String[]{array[1]}, array[1]); 
		if(value != null){
			String[] oneValue = value.split(";");
			if(oneValue != null && oneValue.length > 0){
				for(int i =0 ;i < oneValue.length ;i++ ){
					if(oneValue[i].contains("MaxDatagramRecv")){
						return	oneValue[i].substring(16);
					} 
					
				}
			}
		}
		 return null;
	}
	/**
	 * 最大线程连接数
	 * @param oneobj
	 * @return
	 */
	public static String searchMaxPoolThreads(JBrokerParameter parameter) throws Exception{
		Map<String, String[]> map = LDAPConfigManager.getCmds(WSAD);
		String[] array = map.get(LIMITS);
		String domainName = parameter.getDomainName();
		String dn = array[0].replace("*", domainName);
		LDAPConnection connection = (LDAPConnection)parameter.getConnection();
		String value =  query(connection, dn, new String[]{array[1]}, array[1]); 
		if(value != null){
			String[] oneValue = value.split(";");
			if(oneValue != null && oneValue.length > 0){
				for(int i =0 ;i < oneValue.length ;i++ ){
					if(oneValue[i].contains("MaxPoolThreads")){
						return	oneValue[i].substring(15);
					} 
					
				}
			}
		}
		 return null;
	}
	
	/**
	 * 最大结果集大小
	 * @param oneobj
	 * @return
	 */
	public static String searchMaxResultSetSize(JBrokerParameter parameter) throws Exception{
		Map<String, String[]> map = LDAPConfigManager.getCmds(WSAD);
		String[] array = map.get(LIMITS);
		String domainName = parameter.getDomainName();
		String dn = array[0].replace("*", domainName);
		LDAPConnection connection = (LDAPConnection)parameter.getConnection();
		String value =  query(connection, dn, new String[]{array[1]}, array[1]); 
		if(value != null){
			String[] oneValue = value.split(";");
			if(oneValue != null && oneValue.length > 0){
				for(int i =0 ;i < oneValue.length ;i++ ){
					if(oneValue[i].contains("MaxResultSetSize")){
						return	oneValue[i].substring(17);
					} 
					
				}
			}
		}
		 return null;
	}
	/**
	 * 最大的临时表的大小
	 * @param oneobj
	 * @return
	 */
	public static String searchMaxTempTableSize(JBrokerParameter parameter) throws Exception{
		Map<String, String[]> map = LDAPConfigManager.getCmds(WSAD);
		String[] array = map.get(LIMITS);
		String domainName = parameter.getDomainName();
		String dn = array[0].replace("*", domainName);
		LDAPConnection connection = (LDAPConnection)parameter.getConnection();
		String value =  query(connection, dn, new String[]{array[1]}, array[1]); 
		if(value != null){
			String[] oneValue = value.split(";");
			if(oneValue != null && oneValue.length > 0){
				for(int i =0 ;i < oneValue.length ;i++ ){
					if(oneValue[i].contains("MaxTempTableSize")){
						return	oneValue[i].substring(17);
					} 
					
				}
			}
		}
		 return null;
	}
	
	/**
	 * 最大查询时间
	 * @param oneobj
	 * @return
	 */
	public static String searchMaxQueryDuration(JBrokerParameter parameter) throws Exception{
		Map<String, String[]> map = LDAPConfigManager.getCmds(WSAD);
		String[] array = map.get(LIMITS);
		String domainName = parameter.getDomainName();
		String dn = array[0].replace("*", domainName);
		LDAPConnection connection = (LDAPConnection)parameter.getConnection();
		String value =  query(connection, dn, new String[]{array[1]}, array[1]); 
		if(value != null){
			String[] oneValue = value.split(";");
			if(oneValue != null && oneValue.length > 0){
				for(int i =0 ;i < oneValue.length ;i++ ){
					if(oneValue[i].contains("MaxQueryDuration")){
						return	oneValue[i].substring(17);
					} 
					
				}
			}
		}
		 return null;
	}
	
	/**
	 * 最大活动查询
	 * @param oneobj
	 * @return
	 */
	public static String searchMaxActiveQueries(JBrokerParameter parameter) throws Exception{
		Map<String, String[]> map = LDAPConfigManager.getCmds(WSAD);
		String[] array = map.get(LIMITS);
		String domainName = parameter.getDomainName();
		String dn = array[0].replace("*", domainName);
		LDAPConnection connection = (LDAPConnection)parameter.getConnection();
		String value =  query(connection, dn, new String[]{array[1]}, array[1]); 
		if(value != null){
			String[] oneValue = value.split(";");
			if(oneValue != null && oneValue.length > 0){
				for(int i =0 ;i < oneValue.length ;i++ ){
					if(oneValue[i].contains("MaxActiveQueries")){
						return	oneValue[i].substring(17);
					} 
					
				}
			}
		}
		 return null;
	}
	
	/**
	 * 连接空闲超时时间
	 * @param oneobj
	 * @return
	 */
	public static String searchMaxConnIdleTime(JBrokerParameter parameter) throws Exception{
		Map<String, String[]> map = LDAPConfigManager.getCmds(WSAD);
		String[] array = map.get(LIMITS);
		String domainName = parameter.getDomainName();
		String dn = array[0].replace("*", domainName);
		LDAPConnection connection = (LDAPConnection)parameter.getConnection();
		String value =  query(connection, dn, new String[]{array[1]}, array[1]); 
		if(value != null){
			String[] oneValue = value.split(";");
			if(oneValue != null && oneValue.length > 0){
				for(int i =0 ;i < oneValue.length ;i++ ){
					if(oneValue[i].contains("MaxConnIdleTime")){
						return	oneValue[i].substring(16);
					} 
					
				}
			}
		}
		 return null;
	}
	/**
	 * 可用性
	 * @return
	 */
	public static String searchAvailability(JBrokerParameter parameter) throws Exception{
		LDAPConnection connection = (LDAPConnection)parameter.getConnection();
		if(connection.isConnected() || connection.isAuthenticated())
			return "1";
		else
			return "0";
	}
	
}
