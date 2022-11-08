package com.mainsteam.stm.plugin.sunjes.executors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mainsteam.stm.plugin.sunjes.amx.AMXManager;
import com.mainsteam.stm.plugin.sunjes.amx.DottedNames;
import com.mainsteam.stm.plugin.sunjes.util.SunJESConnInfo;

/**
 * 执行器 <br>
 */
public class JdbcPoolExecutor extends BaseExecutor {

	/**
	 * 平均连接等待时间
	 * 
	 * @param manager
	 *            AMXManager
	 * @return Matric
	 */
	public Map<String, String> getAvgConnWaitTime(final AMXManager manager) {
		Map<String, String> map = new HashMap<String, String>();
		List<String> t_list = manager.getJDBCConnectionPoolNames();
		for (String t_name : t_list) {
			String t_value = manager.getMonitoringDottedNameValue(
					m_dottedNames.getJdbcAvgConnWaitTime(t_name)).toString();
			map.put(t_name, t_value);
		}
		return map;
	}

	/**
	 * 活动连接数
	 * 
	 * @param manager
	 *            AMXManager
	 * @return Matric
	 */
	public Map<String, String> getActiveConnCount(final AMXManager manager) {
		Map<String, String> map = new HashMap<String, String>();
		List<String> t_list = manager.getJDBCConnectionPoolNames();
		for (String t_name : t_list) {
			String t_value = manager.getMonitoringDottedNameValue(
					m_dottedNames.getJdbcNumConnUsed(t_name)).toString();
			map.put(t_name, t_value);
		}
		return map;
	}

	/**
	 * Constructors.
	 * 
	 * @param source
	 *            连接信息
	 */
	public JdbcPoolExecutor(final SunJESConnInfo source) {
		this.m_connInfo = source;
		this.m_dottedNames = DottedNames.getInstance(m_connInfo
				.getInstanceName());
	}

	/**
	 * jdbc连接池名称
	 * 
	 * @param manager
	 *            AMXManager
	 * @return Matric
	 */
	public List<String> getName(final AMXManager manager) {
		List<String> t_list = manager.getJDBCConnectionPoolNames();
		return t_list;
	}

	/**
	 * 超时的连接数
	 * 
	 * @param manager
	 *            AMXManager
	 * @return Matric
	 */
	public Map<String, String> getTimedoutConnCount(final AMXManager manager) {
		Map<String, String> map = new HashMap<String, String>();
		List<String> t_list = manager.getJDBCConnectionPoolNames();
		for (String t_name : t_list) {
			String t_value = manager.getMonitoringDottedNameValue(
					m_dottedNames.getJdbcNumConnTimedOut(t_name)).toString();
			map.put(t_name, t_value);
		}
		return map;
	}

	public String getJdbcResult(final AMXManager manager) {
		try {
			List<String> names = getName(manager);
			Map<String, String> avgWaitTime = getAvgConnWaitTime(manager);
			Map<String, String> activeCnt = getActiveConnCount(manager);
			Map<String, String> availability = getJdbcAvailability(manager);
			Map<String, String> waitingCnt = getWaitingConnCount(manager);
			Map<String, String> timeoutCnt = getTimedoutConnCount(manager);
			StringBuilder sb = new StringBuilder();
			sb.append("name\tavgWaitTime\tactiveCnt\tavailability\twaitingCnt\ttimeoutCnt\n");
			for (String name : names) {
				sb.append(name + "\t");
				sb.append(avgWaitTime.get(name) + "\t");
				sb.append(activeCnt.get(name) + "\t");
				sb.append(availability.get(name) + "\t");
				sb.append(waitingCnt.get(name) + "\t");
				sb.append(timeoutCnt.get(name) + "\n");
			}
			sb.deleteCharAt(sb.length() - 1);
			return sb.toString();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}

	/**
	 * 连接等待数
	 * 
	 * @param manager
	 *            AMXManager
	 * @return Matric
	 */
	public Map<String, String> getWaitingConnCount(final AMXManager manager) {
		Map<String, String> map = new HashMap<String, String>();
		List<String> t_list = manager.getJDBCConnectionPoolNames();
		for (String t_name : t_list) {
			String t_value = manager.getMonitoringDottedNameValue(
					m_dottedNames.getJdbcWaitQueueLength(t_name)).toString();
			map.put(t_name, t_value);
		}
		return map;
	}

	/**
	 * JDBC可用性
	 * 
	 * @param manager
	 *            AMXManager
	 * @return Matric
	 */
	public Map<String, String> getJdbcAvailability(final AMXManager manager) {
		Map<String, String> map = new HashMap<String, String>();
		List<String> t_list = manager.getJDBCConnectionPoolNames();
		for (String t_name : t_list) {
			map.put(t_name, "1");
		}
		return map;
	}

}
