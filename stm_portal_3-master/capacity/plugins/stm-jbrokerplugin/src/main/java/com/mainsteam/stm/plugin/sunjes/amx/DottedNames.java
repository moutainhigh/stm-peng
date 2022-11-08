package com.mainsteam.stm.plugin.sunjes.amx;

/**
 * 点分割的字符串对象 <br>
 * <p>
 * Create on : 2014-2-09<br>
 * <p>
 * </p>
 * <br>
 * 
 * @version sunjes v1.0
 *          <p>
 *          <br>
 *          <strong>Modify History:</strong><br>
 *          user modify_date modify_content<br>
 *          -------------------------------------------<br>
 *          <br>
 */
public class DottedNames {
	/**
	 * <code>S_RESOURCES</code> - 资源字符串
	 */
	private static final String S_RESOURCES = ".resources.";

	/**
	 * <code>S_THREADPOOLS_ORB</code> - 线程池字符串
	 */
	private static final String S_THREADPOOLS_ORB = ".thread-pools.orb\\.threadpool\\.";

	/**
	 * <code>S_APPLICATION</code> - 应用字符串
	 */
	private static final String S_APPLICATION = ".applications.";

	// =============================================
	// Attribute
	// =============================================

	/**
	 * <code>instanceName</code> - 实例名
	 */
	private String m_instanceName = "";

	// =============================================
	// Constructor
	// =============================================

	/**
	 * Constructors.
	 * 
	 * @param instanceName
	 *            实例名
	 */
	public DottedNames(final String instanceName) {
		this.m_instanceName = instanceName;
	}

	/**
	 * 获得实例
	 * 
	 * @param instanceName
	 *            实例名
	 * @return String DottedNames
	 */
	public static DottedNames getInstance(final String instanceName) {
		return new DottedNames(instanceName);
	}

	// =============================================
	// Basic
	// =============================================

	/**
	 * 实例状态字符串
	 * 
	 * @return String
	 */
	public String getInstanceState() {
		return "amx:j2eeType=J2EEServer,name=" + this.m_instanceName;
	}

	/**
	 * 版本字符串
	 * 
	 * @return String
	 */
	public String getAppVersion() {
		return this.m_instanceName + ".http-service.versionserver-current";
	}

	/**
	 * 主机名
	 * 
	 * @return String
	 */
	public String getHostName() {
		return "com.sun.aas.hostName";
	}

	/**
	 * 操作系统名称
	 * 
	 * @return String
	 */
	public String getOSName() {
		return "os.name";
	}

	/**
	 * 操作系统版本
	 * 
	 * @return String
	 */
	public String getOSVersion() {
		return "os.version";
	}

	// =============================================
	// JMS
	// =============================================

	/**
	 * 平均等待时间
	 * 
	 * @param jmsName
	 *            jms池名称
	 * @return String
	 */
	public String getJmsAvgConnWaitTime(final String jmsName) {
		return m_instanceName + S_RESOURCES + jmsName
				+ ".averageconnwaittime-count";
	}

	/**
	 * JMS使用连接数
	 * 
	 * @param jmsName
	 *            jms池名称
	 * @return String String
	 */
	public String getJmsNumConnUsed(final String jmsName) {
		return m_instanceName + S_RESOURCES + jmsName + ".numconnused-current";
	}

	/**
	 * jms连接超时数
	 * 
	 * @param jmsName
	 *            jms池名称
	 * @return String
	 */
	public String getJmsNumConnTimedOut(final String jmsName) {
		return m_instanceName + S_RESOURCES + jmsName
				+ ".numconntimedout-count";
	}

	/**
	 * jms连接等待数
	 * 
	 * @param jmsName
	 *            jms池名称
	 * @return String
	 */
	public String getJmsWaitQueueLength(final String jmsName) {
		return m_instanceName + S_RESOURCES + jmsName
				+ ".waitqueuelength-count";
	}

	// =============================================
	// Jdbc
	// =============================================

	/**
	 * JDBC平均连接等待时间
	 * 
	 * @param jdbcName
	 *            jdbc连接池名称
	 * @return String
	 */
	public String getJdbcAvgConnWaitTime(final String jdbcName) {
		return m_instanceName + S_RESOURCES + jdbcName
				+ ".averageconnwaittime-count";
	}

	/**
	 * JDBC活动的连接数
	 * 
	 * @param jdbcName
	 *            jdbc连接池名称
	 * @return String
	 */
	public String getJdbcNumConnUsed(final String jdbcName) {
		return m_instanceName + S_RESOURCES + jdbcName + ".numconnused-current";
	}

	/**
	 * JDBC超时的连接数
	 * 
	 * @param jdbcName
	 *            jdbc连接池名称
	 * @return String
	 */
	public String getJdbcNumConnTimedOut(final String jdbcName) {
		return m_instanceName + S_RESOURCES + jdbcName
				+ ".numconntimedout-count";
	}

	/**
	 * JDBC连接等待数
	 * 
	 * @param jdbcName
	 *            jdbc连接池名称
	 * @return String
	 */
	public String getJdbcWaitQueueLength(final String jdbcName) {
		return m_instanceName + S_RESOURCES + jdbcName
				+ ".waitqueuelength-count";
	}

	// =============================================
	// JVM
	// =============================================

	/**
	 * JVM允许使用的最大堆大小
	 * 
	 * @return String
	 */
	public String getJvmUpperboundHeapSize() {
		return m_instanceName + ".jvm.heapsize-upperbound";
	}

	/**
	 * JVM运行时已分配的堆大小
	 * 
	 * @return String
	 */
	public String getJvmCommittedHeapSize() {
		return m_instanceName + ".jvm.memory.committedheapsize-count";
	}

	/**
	 * JVM运行时已使用堆大小
	 * 
	 * @return String
	 */
	public String getJvmUsedHeapSize() {
		return m_instanceName + ".jvm.memory.usedheapsize-count";
	}

	// =============================================
	// Transactions Services
	// =============================================

	/**
	 * 当前活动事务数
	 * 
	 * @return String
	 */
	public String getTransactionsActiveCount() {
		return this.m_instanceName + ".transaction-service.activecount-count";
	}

	/**
	 * 已提交的事务数目
	 * 
	 * @return String
	 */
	public String getTransactionsCommittedCount() {
		return this.m_instanceName
				+ ".transaction-service.committedcount-count";
	}

	/**
	 * 回滚事务数
	 * 
	 * @return String
	 */
	public String getTransactionsRolledbackCount() {
		return this.m_instanceName
				+ ".transaction-service.rolledbackcount-count";
	}

	// =============================================
	// Thread Pool
	// =============================================

	/**
	 * 平均等待时间
	 * 
	 * @param threadPoolName
	 *            线程池名称
	 * @return String
	 */
	public String getThreadAvgTimeInQueue(final String threadPoolName) {
		return this.m_instanceName + S_THREADPOOLS_ORB + threadPoolName
				+ ".averagetimeinqueue-current";
	}

	/**
	 * 平均完成时间
	 * 
	 * @param threadPoolName
	 *            线程池名称
	 * @return String
	 */
	public String getThreadAvgWorkCompletionTime(final String threadPoolName) {
		return this.m_instanceName + S_THREADPOOLS_ORB + threadPoolName
				+ ".averageworkcompletiontime-current";
	}

	/**
	 * 当前线程数
	 * 
	 * @param threadPoolName
	 *            线程池名称
	 * @return String
	 */
	public String getThreadCurrentCount(final String threadPoolName) {
		return this.m_instanceName + S_THREADPOOLS_ORB + threadPoolName
				+ ".currentnumberofthreads-current";
	}

	/**
	 * 可用线程数
	 * 
	 * @param threadPoolName
	 *            线程池名称
	 * @return String
	 */
	public String getThreadAvailableCount(final String threadPoolName) {
		return this.m_instanceName + S_THREADPOOLS_ORB + threadPoolName
				+ ".numberofavailablethreads-count";
	}

	/**
	 * 繁忙线程数
	 * 
	 * @param threadPoolName
	 *            线程池名称
	 * @return String
	 */
	public String getThreadBusyCount(final String threadPoolName) {
		return this.m_instanceName + S_THREADPOOLS_ORB + threadPoolName
				+ ".numberofbusythreads-count";
	}

	/**
	 * 总工作项
	 * 
	 * @param threadPoolName
	 *            线程池名称
	 * @return String
	 */
	public String getThreadTotalWorkItemsAdded(final String threadPoolName) {
		return this.m_instanceName + S_THREADPOOLS_ORB + threadPoolName
				+ ".totalworkitemsadded-count";
	}

	// =============================================
	// HTTP Service
	// =============================================

	/**
	 * 最大线程数量
	 * 
	 * @return String
	 */
	public String getHttpMaxThreads() {
		return this.m_instanceName + ".http-service.maxthreads-count";
	}

	/**
	 * 15分钟平均负载
	 * 
	 * @return String
	 */
	public String getHttpLoad15Minute() {
		return this.m_instanceName + ".http-service.load15minuteaverage-count";
	}

	/**
	 * 1分钟平均负载
	 * 
	 * @return String
	 */
	public String getHttpLoad1Minute() {
		return this.m_instanceName + ".http-service.load1minuteaverage-count";
	}

	/**
	 * 5分钟平均负载
	 * 
	 * @return String
	 */
	public String getHttpLoad5Minute() {
		return this.m_instanceName + ".http-service.load5minuteaverage-count";
	}

	// =============================================
	// WebMoudle Application
	// =============================================

	/**
	 * 活动的会话数
	 * 
	 * @param webappName
	 *            web应用的名称
	 * @return String
	 */
	public String getWebAppSessionCount(final String webappName) {
		return this.m_instanceName + S_APPLICATION + webappName
				+ ".server.activesessionscurrent-count";
	}

	/**
	 * 最大活动会话数
	 * 
	 * @param webappName
	 *            web应用的名称
	 * @return String
	 */
	public String getWebAppHighSessionCount(final String webappName) {
		return this.m_instanceName + S_APPLICATION + webappName
				+ ".server.activesessionshigh-count";
	}

	/**
	 * {method description}.
	 * 
	 * @param webappName
	 *            web应用的名称
	 * @return String
	 */
	public String getWebAppErrorCount(final String webappName) {
		return this.m_instanceName + S_APPLICATION + webappName
				+ ".server.default.errorcount-count";
	}

	/**
	 * {method description}.
	 * 
	 * @param webappName
	 *            web应用的名称
	 * @return String
	 */
	public String getWebAppMaxtimeCount(final String webappName) {
		return this.m_instanceName + S_APPLICATION + webappName
				+ ".server.default.maxtime-count";
	}

	/**
	 * {method description}.
	 * 
	 * @param webappName
	 *            web应用的名称
	 * @return String
	 */
	public String getWebAppRequestCount(final String webappName) {
		return this.m_instanceName + S_APPLICATION + webappName
				+ ".server.default.requestcount-count";
	}

	/**
	 * {method description}.
	 * 
	 * @param webappName
	 *            web应用的名称
	 * @return String
	 */
	public String getWebAppProcessingTimeCount(final String webappName) {
		return this.m_instanceName + S_APPLICATION + webappName
				+ ".server.default.processingtime-count";
	}
}
