/**
 * 
 */
package com.mainsteam.stm.plugin.weblogic;

import com.mainsteam.stm.plugin.parameter.JBrokerParameter;


/**
 * @author lij
 * JVM子资源采集
 */
public class JVMResCollector {
	/**
	 * 运行的请求数
	 * @param oneobj
	 * @return
	 */
	public static String getServicedRequestTotalCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "ExecuteQueueRuntimes@#ServicedRequestTotalCount");
	}
	/**
	 * 空闲线程数
	 * @param oneobj
	 * @return
	 */
	public static String getExecuteThreadCurrentIdleCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "ExecuteQueueRuntimes@#ExecuteThreadCurrentIdleCount");
	}
	/**
	 * 队列名称
	 * @param oneobj
	 * @return
	 */
	public static String getName(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "ExecuteQueueRuntimes@#Name");
	}
	/**
	 * 最长等待时间
	 * @param oneobj
	 * @return
	 */
	public static String getPendingRequestOldestTime(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "ExecuteQueueRuntimes@#PendingRequestOldestTime");
	}
	/**
	 * 等待的请求数
	 * @param oneobj
	 * @return
	 */
	public static String getPendingRequestCurrentCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "ExecuteQueueRuntimes@#PendingRequestCurrentCount");
	}
	/**
	 * 总线程数
	 * @param oneobj
	 * @return
	 */
	public static String getExecuteThreadTotalCount(JBrokerParameter oneobj){
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "ExecuteQueueRuntimes@#ExecuteThreadTotalCount");
	}
}
