/**
 * 
 */
package com.mainsteam.stm.plugin.weblogic;

import com.mainsteam.stm.plugin.parameter.JBrokerParameter;


/**
 * @author lij
 *
 */
public class EJBResCollector {
	//private final static String[] EJB = {"", "EJBComponentRuntime", "", ""};
	private final static String[] STATEFUL_EJB_FOUE = {"", "EJBComponentRuntime", "StatefulEJBRuntime", ""};
	private final static String[] STATEFUL_EJB_FIVE = {"", "EJBComponentRuntime", "StatefulEJBRuntime", "", ""};
	private final static String[] STATELESS_EJB_FOUE = {"", "EJBComponentRuntime", "StatelessEJBRuntime", ""};
	private final static String[] STATELESS_EJB_FIVE = {"", "EJBComponentRuntime", "StatelessEJBRuntime", "", ""};
	private final static String[] ENTITY_EJB_FOUE = {"", "EJBComponentRuntime", "EntityEJBRuntime", ""};
	private final static String[] ENTITY_EJB_FIVE = {"", "EJBComponentRuntime", "EntityEJBRuntime", "", ""};
	private final static String[] MESSAGE_EJB_FOUE = {"", "EJBComponentRuntime", "MessageDrivenEJBRuntime", ""};
	private final static String[] MESSAGE_EJB_FIVE = {"", "EJBComponentRuntime", "MessageDrivenEJBRuntime", "", ""};
	//有状态bean
	/**
	 * 应用名
	 * @param oneobj
	 * @return
	 */
	public static String getSFBName(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#EJBRuntimes@#Name", STATEFUL_EJB_FOUE);
	}
	/**
	 * 组件名
	 * @param oneobj
	 * @return
	 */
	public static String getSFBEJBName(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#EJBRuntimes@#EJBName", STATEFUL_EJB_FOUE);
	}
	/**
	 * 事务提交总数
	 * @param oneobj
	 * @return
	 */
	public static String getSFBTransactionsCommittedTotalCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#EJBRuntimes@#TransactionRuntime@#TransactionsCommittedTotalCount", STATEFUL_EJB_FIVE);
	}
	/**
	 * 事务回滚总数
	 * @param oneobj
	 * @return
	 */
	public static String getSFBTransactionsRolledBackTotalCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#EJBRuntimes@#TransactionRuntime@#TransactionsTimedOutTotalCount", STATEFUL_EJB_FIVE);
	}
	/**
	 * 事务超时总数
	 * @param oneobj
	 * @return
	 */
	public static String getSFBTransactionsTimedOutTotalCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#EJBRuntimes@#TransactionRuntime@#TransactionsRolledBackTotalCount", STATEFUL_EJB_FIVE);
	}
	/**
	 * 缓存访问数
	 * @param oneobj
	 * @return
	 */
	public static String getSFBCacheAccessCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#EJBRuntimes@#CacheRuntime@#CacheAccessCount", STATEFUL_EJB_FIVE);
	}
	/**
	 * 缓存丢失数
	 * @param oneobj
	 * @return
	 */
	public static String getSFBCacheMissCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#EJBRuntimes@#CacheRuntime@#CacheMissCount", STATEFUL_EJB_FIVE);
	}
	
	//无状态bean
	/**
	 * 应用名
	 * @param oneobj
	 * @return
	 */
	public static String getSLBName(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#EJBRuntimes@#Name", STATELESS_EJB_FOUE);
	}
	/**
	 * 组件名
	 * @param oneobj
	 * @return
	 */
	public static String getSLBEJBName(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#EJBRuntimes@#EJBName", STATELESS_EJB_FOUE);
	}
	/**
	 * 事务提交总数
	 * @param oneobj
	 * @return
	 */
	public static String getSLBTransactionsCommittedTotalCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#EJBRuntimes@#TransactionRuntime@#TransactionsCommittedTotalCount", STATELESS_EJB_FIVE);
	}
	/**
	 * 事务回滚总数
	 * @param oneobj
	 * @return
	 */
	public static String getSLBTransactionsRolledBackTotalCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#EJBRuntimes@#TransactionRuntime@#TransactionsTimedOutTotalCount", STATELESS_EJB_FIVE);
	}
	/**
	 * 事务超时总数
	 * @param oneobj
	 * @return
	 */
	public static String getSLBTransactionsTimedOutTotalCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#EJBRuntimes@#TransactionRuntime@#TransactionsRolledBackTotalCount", STATELESS_EJB_FIVE);
	}
	
	//实体bean
	/**
	 * 应用名
	 * @param oneobj
	 * @return
	 */
	public static String getEBName(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#EJBRuntimes@#Name", ENTITY_EJB_FOUE);
	}
	/**
	 * 组件名
	 * @param oneobj
	 * @return
	 */
	public static String getEBEJBName(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#EJBRuntimes@#EJBName", ENTITY_EJB_FOUE);
	}
	/**
	 * 事务提交总数
	 * @param oneobj
	 * @return
	 */
	public static String getEBTransactionsCommittedTotalCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#EJBRuntimes@#TransactionRuntime@#TransactionsCommittedTotalCount", ENTITY_EJB_FIVE);
	}
	/**
	 * 事务回滚总数
	 * @param oneobj
	 * @return
	 */
	public static String getEBTransactionsRolledBackTotalCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#EJBRuntimes@#TransactionRuntime@#TransactionsTimedOutTotalCount", ENTITY_EJB_FIVE);
	}
	/**
	 * 事务超时总数
	 * @param oneobj
	 * @return
	 */
	public static String getEBTransactionsTimedOutTotalCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#EJBRuntimes@#TransactionRuntime@#TransactionsRolledBackTotalCount", ENTITY_EJB_FIVE);
	}
	/**
	 * 缓存访问数
	 * @param oneobj
	 * @return
	 */
	public static String getEBCacheAccessCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#EJBRuntimes@#CacheRuntime@#CacheAccessCount", ENTITY_EJB_FIVE);
	}
	/**
	 * 缓存丢失数
	 * @param oneobj
	 * @return
	 */
	public static String getEBCacheMissCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#EJBRuntimes@#CacheRuntime@#CacheMissCount", ENTITY_EJB_FIVE);
	}
	/**
	 * 池等待数
	 * @param oneobj
	 * @return
	 */
	public static String getEBWaiterCurrentCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#EJBRuntimes@#PoolRuntime@#WaiterCurrentCount", ENTITY_EJB_FIVE);
	}
	/**
	 * 当前bean数
	 * @param oneobj
	 * @return
	 */
	public static String getEBPooledBeansCurrentCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#EJBRuntimes@#PoolRuntime@#PooledBeansCurrentCount", ENTITY_EJB_FIVE);
	}
	
	//消息bean
	/**
	 * 应用名
	 * @param oneobj
	 * @return
	 */
	public static String getMBName(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#EJBRuntimes@#Name", MESSAGE_EJB_FOUE);
	}
	/**
	 * 组件名
	 * @param oneobj
	 * @return
	 */
	public static String getMBEJBName(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#EJBRuntimes@#EJBName", MESSAGE_EJB_FOUE);
	}
	/**
	 * 事务提交总数
	 * @param oneobj
	 * @return
	 */
	public static String getMBTransactionsCommittedTotalCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#EJBRuntimes@#TransactionRuntime@#TransactionsCommittedTotalCount", MESSAGE_EJB_FIVE);
	}
	/**
	 * 事务回滚总数
	 * @param oneobj
	 * @return
	 */
	public static String getMBTransactionsRolledBackTotalCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#EJBRuntimes@#TransactionRuntime@#TransactionsTimedOutTotalCount", MESSAGE_EJB_FIVE);
	}
	/**
	 * 事务超时总数
	 * @param oneobj
	 * @return
	 */
	public static String getMBTransactionsTimedOutTotalCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#EJBRuntimes@#TransactionRuntime@#TransactionsRolledBackTotalCount", MESSAGE_EJB_FIVE);
	}
	/**
	 * 池等待数
	 * @param oneobj
	 * @return
	 */
	public static String getMBWaiterCurrentCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#EJBRuntimes@#PoolRuntime@#WaiterCurrentCount", MESSAGE_EJB_FIVE);
	}
	/**
	 * 当前bean数
	 * @param oneobj
	 * @return
	 */
	public static String getMBPooledBeansCurrentCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "ApplicationRuntimes@#ComponentRuntimes@#EJBRuntimes@#PoolRuntime@#PooledBeansCurrentCount", MESSAGE_EJB_FIVE);
	}
}
