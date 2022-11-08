package com.mainsteam.stm.plugin.sunjes.executors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mainsteam.stm.plugin.sunjes.amx.AMXManager;
import com.mainsteam.stm.plugin.sunjes.amx.DottedNames;
import com.mainsteam.stm.plugin.sunjes.util.SunJESConnInfo;

/**
 * 执行器<br>
 * <br>
 */
public class JMSExecutor extends BaseExecutor {

	/**
	 * Constructors.
	 * 
	 * @param source
	 *            连接信息
	 */
	public JMSExecutor(final SunJESConnInfo source) {
		this.m_connInfo = source;
		this.m_dottedNames = DottedNames.getInstance(m_connInfo
				.getInstanceName());
	}

	/**
	 * JMS名称
	 * 
	 * @param manager
	 *            AMXManager
	 * @return Map<String, String>
	 */
	public List<String> getName(final AMXManager manager) {
		List<String> t_list = manager.getConnectorConnectionPoolNames();
		return t_list;
	}

	/**
	 * 超时的连接数
	 * 
	 * @param manager
	 *            AMXManager
	 * @return Map<String, String>
	 */
	public Map<String, String> getTimeoutConnCount(final AMXManager manager) {
		Map<String, String> map = new HashMap<String, String>();
		List<String> t_list = manager.getConnectorConnectionPoolNames();
		if (t_list != null) {
			for (String t_name : t_list) {
				String t_value = manager.getMonitoringDottedNameValue(
						m_dottedNames.getJmsNumConnTimedOut(t_name)).toString();
				map.put(t_name, t_value);
			}
		}
		return map;
	}

	/**
	 * 连接等待数
	 * 
	 * @param manager
	 *            AMXManager
	 * @return Map<String, String>
	 */
	public Map<String, String> getWaitingConnCount(final AMXManager manager) {
		Map<String, String> map = new HashMap<String, String>();
		List<String> t_list = manager.getConnectorConnectionPoolNames();
		if (t_list != null) {
			for (String t_name : t_list) {
				String t_value = manager.getMonitoringDottedNameValue(
						m_dottedNames.getJmsWaitQueueLength(t_name)).toString();
				map.put(t_name, t_value);
			}
		}
		return map;
	}

	/**
	 * 平均连接等待时间
	 * 
	 * @param manager
	 *            AMXManager
	 * @return Map<String, String>
	 */
	public Map<String, String> getAvgConnWaitTime(final AMXManager manager) {
		Map<String, String> map = new HashMap<String, String>();
		List<String> t_list = manager.getConnectorConnectionPoolNames();
		if (t_list != null) {
			for (String t_name : t_list) {
				String t_value = manager.getMonitoringDottedNameValue(
						m_dottedNames.getJmsAvgConnWaitTime(t_name)).toString();
				map.put(t_name, t_value);
			}
		}
		return map;
	}

	/**
	 * 活动的连接数
	 * 
	 * @param manager
	 *            AMXManager
	 * @return Map<String, String>
	 */
	public Map<String, String> getActiveConnCount(final AMXManager manager) {
		Map<String, String> map = new HashMap<String, String>();
		List<String> t_list = manager.getConnectorConnectionPoolNames();
		if (t_list != null) {
			for (String t_name : t_list) {
				String t_value = manager.getMonitoringDottedNameValue(
						m_dottedNames.getJmsNumConnUsed(t_name)).toString();
				map.put(t_name, t_value);
			}
		}
		return map;
	}

}
