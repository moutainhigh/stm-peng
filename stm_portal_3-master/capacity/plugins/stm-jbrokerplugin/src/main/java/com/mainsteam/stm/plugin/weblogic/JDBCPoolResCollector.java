/**
 * 
 */
package com.mainsteam.stm.plugin.weblogic;

import com.mainsteam.stm.plugin.parameter.JBrokerParameter;

public class JDBCPoolResCollector {
	private final static String[] TYPES = {"", "", ""};
	/**
	 * 连接池名称
	 * @param oneobj
	 * @return
	 */
	public static String getJdbcPoolName(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "JDBCServiceRuntime@#JDBCDataSourceRuntimeMBeans@#Name", TYPES);
	}
	/**
	 * 连接池状态
	 * @param oneobj
	 * @return
	 */
	public static String getStatus(JBrokerParameter oneobj) {
		String result = WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "JDBCServiceRuntime@#JDBCDataSourceRuntimeMBeans@#State", TYPES);
		return result;
	}
	/**
	 * 活动的连接数
	 * @param oneobj
	 * @return
	 */
	public static String getActiveConnCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "JDBCServiceRuntime@#JDBCDataSourceRuntimeMBeans@#ActiveConnectionsCurrentCount", TYPES);
	}
	/**
	 * 平均的活动连接数
	 * @param oneobj
	 * @return
	 */
	public static String getActiveConnAverageCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "JDBCServiceRuntime@#JDBCDataSourceRuntimeMBeans@#ActiveConnectionsAverageCount", TYPES);
	}
	/**
	 * 等待的连接数
	 * @param oneobj
	 * @return
	 */
	public static String getCurWaitingConnCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "JDBCServiceRuntime@#JDBCDataSourceRuntimeMBeans@#WaitingForConnectionCurrentCount", TYPES);
	}
	/**
	 * 最高活动连接数
	 * @param oneobj
	 * @return
	 */
	public static String getActiveConnectionsHighCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "JDBCServiceRuntime@#JDBCDataSourceRuntimeMBeans@#ActiveConnectionsHighCount", TYPES);
	}
	/**
	 * 最大连接等待数
	 * @param oneobj
	 * @return
	 */
	public static String getHighWaitingConnCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "JDBCServiceRuntime@#JDBCDataSourceRuntimeMBeans@#WaitingForConnectionHighCount", TYPES);
	}
	/**
	 * 连接延迟时间
	 * @param oneobj
	 * @return
	 */
	public static String getConnectionDelayTime(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "JDBCServiceRuntime@#JDBCDataSourceRuntimeMBeans@#ConnectionDelayTime", TYPES);
	}
	/**
	 * 连接总数
	 * @param oneobj
	 * @return
	 */
	public static String getConnectionsTotalCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "JDBCServiceRuntime@#JDBCDataSourceRuntimeMBeans@#ConnectionsTotalCount", TYPES);
	}
	/**
	 * 最长等待时间
	 * @param oneobj
	 * @return
	 */
	public static String getHighWaitSecConn(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "JDBCServiceRuntime@#JDBCDataSourceRuntimeMBeans@#WaitSecondsHighCount", TYPES);
	}
	/**
	 * 泄漏的连接数
	 * @param oneobj
	 * @return
	 */
	public static String getLeakedConnCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "JDBCServiceRuntime@#JDBCDataSourceRuntimeMBeans@#LeakedConnectionCount", TYPES);
	}
}
