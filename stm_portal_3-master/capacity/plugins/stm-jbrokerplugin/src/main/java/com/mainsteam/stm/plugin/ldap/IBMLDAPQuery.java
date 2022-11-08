package com.mainsteam.stm.plugin.ldap;

import java.util.Map;

import netscape.ldap.LDAPConnection;

import com.mainsteam.stm.plugin.parameter.JBrokerParameter;

/**
 * IBM目录服务器采集
 * @author xiaop_000
 *
 */
public class IBMLDAPQuery extends LDAPQuery {
	
	private static final String CUR_READWAITER_COUNT = "CurReadwaiterCount";
	private static final String CUR_WRITEWAITER_COUNT = "CurWritewaiterCount";
	private static final String CUR_WORKQUEUE_COUNT = "CurWorkqueueCount";
	private static final String MAX_WORKQUEUE_COUNT = "MaxWorkqueueCount";
	private static final String MAX_RETURN_COUNT = "MaxReturnCount";
	private static final String MAX_CONN_SIZE = "MaxConnSize";
	private static final String DATABASE_CONN_COUNT = "DatabaseConnCount";
	private static final String DATABASE_PASSWORD = "DatabasePassword";
	private static final String DATABASE_USER_ID = "DatabaseUserId";
	private static final String DATABASE_NAME = "DatabaseName";
	private static final String DATABASE_INSTANCE = "DatabaseInstance";
	private static final String INSTANC_DIRECTORY = "InstancDirectory";
	private static final String SERVER_DISPLAY_NAME = "ServerDisplayName";
	private static final String SERVER_NAME = "ServerName";
	private static final String VERSION = "Version";
	private static final String SERVER_ID = "ServerId";
	private static final String CUR_CONN_COUNT = "CurConnCount";
	private static final String IBM = "ibm";
	
	/**
	 * 当前连接数
	 * @return
	 */
	public static String searchCurConnCount(JBrokerParameter parameter) throws Exception {
		Map<String, String[]> map = LDAPConfigManager.getCmds(IBM);
		String[] array = map.get(CUR_CONN_COUNT);
		LDAPConnection connection = (LDAPConnection)parameter.getConnection();
		return query(connection, array[0], new String[]{array[1]}, array[1]); 
	}
	
	/**
	 * Server ID
	 * @param oneobj
	 * @return
	 */
	public static String searchServerId(JBrokerParameter parameter) throws Exception{
		Map<String, String[]> map = LDAPConfigManager.getCmds(IBM);
		String[] array = map.get(SERVER_ID);
		LDAPConnection connection = (LDAPConnection)parameter.getConnection();
		return query(connection, array[0], new String[]{array[1]}, array[1]); 
		
	}
	
	/**
	 * 版本
	 * @return
	 */
	public static String searchVersion(JBrokerParameter parameter) throws Exception{
		Map<String, String[]> map = LDAPConfigManager.getCmds(IBM);
		String[] array = map.get(VERSION);
		LDAPConnection connection = (LDAPConnection)parameter.getConnection(); 
		return query(connection, array[0], new String[]{array[1]}, array[1]); 
	}
	
	/**
	 * 服务器名称
	 * @return
	 */
	public static String searchServerName(JBrokerParameter parameter) throws Exception{
		Map<String, String[]> map = LDAPConfigManager.getCmds(IBM);
		String[] array = map.get(SERVER_NAME);
		LDAPConnection connection = (LDAPConnection)parameter.getConnection(); 
		return query(connection, array[0], new String[]{array[1]}, array[1]); 
	}
	
	/**
	 * 服务器显示名称
	 * @return
	 */
	public static String searchServerDisplayName(JBrokerParameter parameter) throws Exception{
		Map<String, String[]> map = LDAPConfigManager.getCmds(IBM);
		String[] array = map.get(SERVER_DISPLAY_NAME);
		LDAPConnection connection = (LDAPConnection)parameter.getConnection(); 
		return query(connection, array[0], new String[]{array[1]}, array[1]);  
	}
	
	/**
	 * 安装目录
	 * @return
	 */
	public static String searchInstancDirectory(JBrokerParameter parameter) throws Exception{
		Map<String, String[]> map = LDAPConfigManager.getCmds(IBM);
		String[] array = map.get(INSTANC_DIRECTORY);
		LDAPConnection connection = (LDAPConnection)parameter.getConnection(); 
		return query(connection, array[0], new String[]{array[1]}, array[1]);  
	}
	
	/**
	 * 数据库实例
	 * @param oneobj
	 * @return
	 */
	public static String searchDatabaseInstance(JBrokerParameter parameter) throws Exception{
		Map<String, String[]> map = LDAPConfigManager.getCmds(IBM);
		String[] array = map.get(DATABASE_INSTANCE);
		LDAPConnection connection = (LDAPConnection)parameter.getConnection(); 
		return query(connection, array[0], new String[]{array[1]}, array[1]);  
		
	}
	
	/**
	 * 数据库名称
	 * @param oneobj
	 * @return
	 */
	public static String searchDatabaseName(JBrokerParameter parameter) throws Exception{
		Map<String, String[]> map = LDAPConfigManager.getCmds(IBM);
		String[] array = map.get(DATABASE_NAME);
		LDAPConnection connection = (LDAPConnection)parameter.getConnection(); 
		return query(connection, array[0], new String[]{array[1]}, array[1]); 
		
	}
	
	/**
	 * 数据库用户名
	 * @param oneobj
	 * @return
	 */
	public static String searchDatabaseUserId(JBrokerParameter parameter) throws Exception{
		Map<String, String[]> map = LDAPConfigManager.getCmds(IBM);
		String[] array = map.get(DATABASE_USER_ID);
		LDAPConnection connection = (LDAPConnection)parameter.getConnection(); 
		return query(connection, array[0], new String[]{array[1]}, array[1]); 
		
	}
	
	/**
	 * 数据库密码
	 * @return
	 */
	public static String searchDatabasePassword(JBrokerParameter parameter) throws Exception{
		Map<String, String[]> map = LDAPConfigManager.getCmds(IBM);
		String[] array = map.get(DATABASE_PASSWORD);
		LDAPConnection connection = (LDAPConnection)parameter.getConnection(); 
		return query(connection, array[0], new String[]{array[1]}, array[1]);  
		
	}
	
	/**
	 * 数据库连接数
	 * @return
	 */
	public static String searchDatabaseConnCount(JBrokerParameter parameter) throws Exception{
		Map<String, String[]> map = LDAPConfigManager.getCmds(IBM);
		String[] array = map.get(DATABASE_CONN_COUNT);
		LDAPConnection connection = (LDAPConnection)parameter.getConnection(); 
		return query(connection, array[0], new String[]{array[1]}, array[1]); 
		
	}
	/**
	 * 最大连接数
	 * @return
	 */
	public static String searchMaxConnSize(JBrokerParameter parameter) throws Exception{
		Map<String, String[]> map = LDAPConfigManager.getCmds(IBM);
		String[] array = map.get(MAX_CONN_SIZE);
		LDAPConnection connection = (LDAPConnection)parameter.getConnection(); 
		return query(connection, array[0], new String[]{array[1]}, array[1]); 
	}
	
	/**
	 * 最大返回数
	 * @return
	 */
	public static String searchMaxReturnCount(JBrokerParameter parameter) throws Exception{
		Map<String, String[]> map = LDAPConfigManager.getCmds(IBM);
		String[] array = map.get(MAX_RETURN_COUNT);
		LDAPConnection connection = (LDAPConnection)parameter.getConnection(); 
		return query(connection, array[0], new String[]{array[1]}, array[1]);  
	}
	
	/**
	 *	最大工作队列数
	 * @return
	 */
	public static String searchMaxWorkqueueCount(JBrokerParameter parameter) throws Exception{
		Map<String, String[]> map = LDAPConfigManager.getCmds(IBM);
		String[] array = map.get(MAX_WORKQUEUE_COUNT);
		LDAPConnection connection = (LDAPConnection)parameter.getConnection(); 
		return query(connection, array[0], new String[]{array[1]}, array[1]); 
	}
	
	/**
	 * 当前工作队列数
	 * @return
	 */
	public static String searchCurWorkqueueCount(JBrokerParameter parameter) throws Exception{
		Map<String, String[]> map = LDAPConfigManager.getCmds(IBM);
		String[] array = map.get(CUR_WORKQUEUE_COUNT);
		LDAPConnection connection = (LDAPConnection)parameter.getConnection(); 
		return query(connection, array[0], new String[]{array[1]}, array[1]);  
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
	
	/**
	 * 当前写等待数
	 * @return
	 */
	public static String searchCurWritewaiterCount(JBrokerParameter parameter) throws Exception{
		Map<String, String[]> map = LDAPConfigManager.getCmds(IBM);
		String[] array = map.get(CUR_WRITEWAITER_COUNT);
		LDAPConnection connection = (LDAPConnection)parameter.getConnection(); 
		return query(connection, array[0], new String[]{array[1]}, array[1]); 
	}
	
	/**
	 * 当前读等待数
	 * @return
	 */
	public static String searchCurReadwaiterCount(JBrokerParameter parameter) throws Exception{
		Map<String, String[]> map = LDAPConfigManager.getCmds(IBM);
		String[] array = map.get(CUR_READWAITER_COUNT);
		LDAPConnection connection = (LDAPConnection)parameter.getConnection(); 
		return query(connection, array[0], new String[]{array[1]}, array[1]); 
	}

}
