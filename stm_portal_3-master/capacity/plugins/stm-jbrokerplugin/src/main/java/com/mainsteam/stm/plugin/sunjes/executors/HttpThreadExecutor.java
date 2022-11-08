package com.mainsteam.stm.plugin.sunjes.executors;

import com.mainsteam.stm.plugin.sunjes.amx.AMXManager;
import com.mainsteam.stm.plugin.sunjes.amx.DottedNames;
import com.mainsteam.stm.plugin.sunjes.util.SunJESConnInfo;

/**
 * http线程执行器 <br>
 */
public class HttpThreadExecutor extends BaseExecutor {

	/**
	 * Constructors.
	 * 
	 * @param source
	 *            连接 信息
	 */
	public HttpThreadExecutor(final SunJESConnInfo source) {
		this.m_connInfo = source;
		this.m_dottedNames = DottedNames.getInstance(m_connInfo
				.getInstanceName());
	}

	/**
	 * 15分钟平均负载
	 * 
	 * @param manager
	 *            AMXManager
	 * @return Matric
	 */
	public String getAvgLoad15m(final AMXManager manager) {
		Object t_value = manager.getMonitoringDottedNameValue(m_dottedNames
				.getHttpLoad15Minute());
		return t_value.toString();
	}

	/**
	 * 最大线程数量
	 * 
	 * @param manager
	 *            AMXManager
	 * @return Matric
	 */
	public String getMaxThreadCount(final AMXManager manager) {
		Object t_value = manager.getMonitoringDottedNameValue(m_dottedNames
				.getHttpMaxThreads());
		return t_value.toString();
	}

	/**
	 * 1分钟平均负载
	 * 
	 * @param manager
	 *            AMXManager
	 * @return Matric
	 */
	public String getAvgLoad1m(final AMXManager manager) {
		Object t_value = manager.getMonitoringDottedNameValue(m_dottedNames
				.getHttpLoad1Minute());
		return t_value.toString();
	}

	/**
	 * 5分钟平均负载
	 * 
	 * @param manager
	 *            AMXManager
	 * @return Matric
	 */
	public String getAvgLoad5m(final AMXManager manager) {
		Object t_value = manager.getMonitoringDottedNameValue(m_dottedNames
				.getHttpLoad5Minute());
		return t_value.toString();
	}

}
