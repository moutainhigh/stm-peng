package com.mainsteam.stm.plugin.ldap;

import java.util.Map;

import netscape.ldap.LDAPConnection;

import com.mainsteam.stm.plugin.parameter.JBrokerParameter;

/**
 * SUN目录服务器采集类
 * @author xiaop_000
 *
 */
public class SUNLDAPQuery extends LDAPQuery {
	
	private static final String CUR_READWAITER_COUNT = "CurReadwaiterCount";
	private static final String CUR_THREAD_COUNT = "CurThreadCount";
	private static final String MAX_RETURN_COUNT = "MaxReturnCount";
	private static final String MAX_CONN_COUNT = "MaxConnCount";
	private static final String INSTALL_DIRECTORY = "InstallDirectory";
	private static final String SERVER_DISPLAY_NAME = "ServerDisplayName";
	private static final String SERVER_NAME = "ServerName";
	private static final String VERSION = "Version";
	private static final String CUR_CONN_COUNT = "CurConnCount";
	private static final String SUN = "sun";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		ONEOBJ one = new ONEOBJ("172.16.7.58");
//		MidAppParam param = one.getMidAppParam();
//		param.setAppProtocol("LDAP");
//		param.setAppUser("cn=Directory Manager");
//		param.setAppPassword("password");
//		param.setAppPort(389);
//		
//		String str = searchCurConnCount(one);
//		System.out.println(str);
//		System.exit(0);
//		SUNLDAPQuery query = new SUNLDAPQuery();
//		String ip="172.16.7.58";
//		Map<String, String> map = new HashMap<String, String>();
//		map.put("appPort", "389");
//		map.put("appUser", "cn=Directory Manager");
//		map.put("appPassword", "password");
//		boolean flag = query.isAppOnline(ip, map, null);
//		System.out.println(flag);

	}
	
	/**
	 * 当前连接数
	 * @return
	 */
	public static String searchCurConnCount(JBrokerParameter parameter) throws Exception{
		Map<String, String[]> map = LDAPConfigManager.getCmds(SUN);
		String[] array = map.get(CUR_CONN_COUNT);
		LDAPConnection connection = (LDAPConnection)parameter.getConnection(); 
		return query(connection, array[0], new String[]{array[1]}, array[1]); 
	}
	
	/**
	 * 版本
	 * @return
	 */
	public static String searchVersion(JBrokerParameter parameter) throws Exception{
		Map<String, String[]> map = LDAPConfigManager.getCmds(SUN);
		String[] array = map.get(VERSION);
		LDAPConnection connection = (LDAPConnection)parameter.getConnection(); 
		return query(connection, array[0], new String[]{array[1]}, array[1]); 
	}
	
	/**
	 * 服务器名称
	 * @return
	 */
	public static String searchServerName(JBrokerParameter parameter) throws Exception{
		Map<String, String[]> map = LDAPConfigManager.getCmds(SUN);
		String[] array = map.get(SERVER_NAME);
		LDAPConnection connection = (LDAPConnection)parameter.getConnection(); 
		return query(connection, array[0], new String[]{array[1]}, array[1]);
	}
	
	/**
	 * 服务器显示名称
	 * @return
	 */
	public static String searchServerDisplayName(JBrokerParameter parameter) throws Exception{
		Map<String, String[]> map = LDAPConfigManager.getCmds(SUN);
		String[] array = map.get(SERVER_DISPLAY_NAME);
		LDAPConnection connection = (LDAPConnection)parameter.getConnection(); 
		return query(connection, array[0], new String[]{array[1]}, array[1]); 
	}
	
	/**
	 * 安装目录
	 * @return
	 */
	public static String searchInstallDirectory(JBrokerParameter parameter) throws Exception{
		Map<String, String[]> map = LDAPConfigManager.getCmds(SUN);
		String[] array = map.get(INSTALL_DIRECTORY);
		LDAPConnection connection = (LDAPConnection)parameter.getConnection(); 
		return query(connection, array[0], new String[]{array[1]}, array[1]); 
	}

	/**
	 * 最大连接数
	 * @return
	 */
	public static String searchMaxConnCount(JBrokerParameter parameter) throws Exception{
		Map<String, String[]> map = LDAPConfigManager.getCmds(SUN);
		String[] array = map.get(MAX_CONN_COUNT);
		LDAPConnection connection = (LDAPConnection)parameter.getConnection(); 
		return query(connection, array[0], new String[]{array[1]}, array[1]); 
	}
	
	/**
	 * 最大返回数
	 * @return
	 */
	public static String searchMaxReturnCount(JBrokerParameter parameter) throws Exception{
		Map<String, String[]> map = LDAPConfigManager.getCmds(SUN);
		String[] array = map.get(MAX_RETURN_COUNT);
		LDAPConnection connection = (LDAPConnection)parameter.getConnection(); 
		return query(connection, array[0], new String[]{array[1]}, array[1]); 
	}
	
	/**
	 * 可用性
	 * @return
	 */
	public static String searchAvailability(JBrokerParameter parameter) throws Exception{
		LDAPConnection connection = (LDAPConnection)parameter.getConnection();
		if(connection.isAuthenticated() || connection.isConnected())
			return "1";
		else
			return "0";
	}
	
	/**
	 * 当前线程数
	 * @return
	 */
	public static String searchCurThreadCount(JBrokerParameter parameter) throws Exception{
		Map<String, String[]> map = LDAPConfigManager.getCmds(SUN);
		String[] array = map.get(CUR_THREAD_COUNT);
		LDAPConnection connection = (LDAPConnection)parameter.getConnection(); 
		return query(connection, array[0], new String[]{array[1]}, array[1]);
	}
	
	/**
	 * 当前读等待数
	 * @return
	 */
	public static String searchCurReadwaiterCount(JBrokerParameter parameter) throws Exception{
		Map<String, String[]> map = LDAPConfigManager.getCmds(SUN);
		String[] array = map.get(CUR_READWAITER_COUNT);
		LDAPConnection connection = (LDAPConnection)parameter.getConnection(); 
		return query(connection, array[0], new String[]{array[1]}, array[1]);
	}
}
