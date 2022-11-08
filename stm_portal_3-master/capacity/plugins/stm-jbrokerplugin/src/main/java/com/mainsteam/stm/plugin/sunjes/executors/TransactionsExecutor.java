package com.mainsteam.stm.plugin.sunjes.executors;

import com.mainsteam.stm.plugin.sunjes.amx.AMXManager;
import com.mainsteam.stm.plugin.sunjes.amx.DottedNames;
import com.mainsteam.stm.plugin.sunjes.util.SunJESConnInfo;

/**
 * TransactionsExecutor <br>
 */
public class TransactionsExecutor extends BaseExecutor {

	/**
	 * Constructors.
	 * 
	 * @param source
	 *            连接信息
	 */
	public TransactionsExecutor(final SunJESConnInfo source) {
		this.m_connInfo = source;
		this.m_dottedNames = DottedNames.getInstance(m_connInfo
				.getInstanceName());
	}

	/**
	 * 回滚事务数
	 * 
	 * @param manager
	 *            AMXManager
	 * @return String
	 */
	public String getRolledbackTransactionCount(final AMXManager manager) {
		Object t_value = manager.getMonitoringDottedNameValue(m_dottedNames
				.getTransactionsRolledbackCount());
		return t_value.toString();
	}

	/**
	 * 当前活动事务数
	 * 
	 * @param manager
	 *            AMXManager
	 * @return String
	 */
	public String getActiveTransactionCount(final AMXManager manager) {
		Object t_value = manager.getMonitoringDottedNameValue(m_dottedNames
				.getTransactionsActiveCount());
		return t_value.toString();
	}

	/**
	 * 已提交的事务数目
	 * 
	 * @param manager
	 *            AMXManager
	 * @return String
	 */
	public String getCommittedTransactionCount(final AMXManager manager) {
		Object t_value = manager.getMonitoringDottedNameValue(m_dottedNames
				.getTransactionsCommittedCount());
		return t_value.toString();
	}

}
