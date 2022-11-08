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
public class ThreadPoolExecutor extends BaseExecutor {
	/**
	 * Constructors.
	 * 
	 * @param source
	 *            连接信息
	 */
	public ThreadPoolExecutor(final SunJESConnInfo source) {
		this.m_connInfo = source;
		this.m_dottedNames = DottedNames.getInstance(m_connInfo
				.getInstanceName());
	}

	/**
	 * 平均等待时间
	 * 
	 * @param manager
	 *            AMXManager
	 * @return Map<String, String>
	 */
	public Map<String, String> getAvgWaitTime(final AMXManager manager) {
		Map<String, String> map = new HashMap<String, String>();
		List<String> t_list = manager.getThreadPoolNames();
		if (t_list != null) {
			for (String t_name : t_list) {
				String t_value = manager.getMonitoringDottedNameValue(
						m_dottedNames.getThreadAvgTimeInQueue(t_name))
						.toString();
				map.put(t_name, t_value);
			}
		}
		return map;
	}

	/**
	 * 平均完成时间
	 * 
	 * @param manager
	 *            AMXManager
	 * @return Map<String, String>
	 */
	public Map<String, String> getAvgCompletionTime(final AMXManager manager) {
		Map<String, String> map = new HashMap<String, String>();
		List<String> t_list = manager.getThreadPoolNames();
		if (t_list != null) {
			for (String t_name : t_list) {
				String t_value = manager.getMonitoringDottedNameValue(
						m_dottedNames.getThreadAvgWorkCompletionTime(t_name))
						.toString();
				map.put(t_name, t_value);
			}
		}
		return map;
	}

	/**
	 * 当前线程数
	 * 
	 * @param manager
	 *            AMXManager
	 * @return Map<String, String>
	 */
	public Map<String, String> getCurThreadCount(final AMXManager manager) {
		Map<String, String> map = new HashMap<String, String>();
		List<String> t_list = manager.getThreadPoolNames();
		if (t_list != null) {
			for (String t_name : t_list) {
				String t_value = manager.getMonitoringDottedNameValue(
						m_dottedNames.getThreadCurrentCount(t_name)).toString();
				map.put(t_name, t_value);
			}
		}
		return map;
	}

	/**
	 * 可用线程数
	 * 
	 * @param manager
	 *            AMXManager
	 * @return Map<String, String>
	 */
	public Map<String, String> getAvailableThreadCount(final AMXManager manager) {
		Map<String, String> map = new HashMap<String, String>();
		List<String> t_list = manager.getThreadPoolNames();
		if (t_list != null) {
			for (String t_name : t_list) {
				String t_value = manager.getMonitoringDottedNameValue(
						m_dottedNames.getThreadAvailableCount(t_name))
						.toString();
				map.put(t_name, t_value);
			}
		}
		return map;
	}

	/**
	 * 线程池名称
	 * 
	 * @param manager
	 *            AMXManager
	 * @return Map<String, String>
	 */
	public List<String> getName(final AMXManager manager) {
		List<String> t_list = manager.getThreadPoolNames();
		return t_list;
	}

	/**
	 * 繁忙线程数
	 * 
	 * @param manager
	 *            AMXManager
	 * @return Map<String, String>
	 */
	public Map<String, String> getBusyThreadCount(final AMXManager manager) {
		Map<String, String> map = new HashMap<String, String>();
		List<String> t_list = manager.getThreadPoolNames();
		if (t_list != null) {
			for (String t_name : t_list) {
				String t_value = manager.getMonitoringDottedNameValue(
						m_dottedNames.getThreadBusyCount(t_name)).toString();
				map.put(t_name, t_value);
			}
		}
		return map;
	}

	/**
	 * 总共工作项
	 * 
	 * @param manager
	 *            AMXManager
	 * @return Map<String, String>
	 */
	public Map<String, String> getTotalWorkItemCount(final AMXManager manager) {
		Map<String, String> map = new HashMap<String, String>();
		List<String> t_list = manager.getThreadPoolNames();
		if (t_list != null) {
			for (String t_name : t_list) {
				String t_value = manager.getMonitoringDottedNameValue(
						m_dottedNames.getThreadTotalWorkItemsAdded(t_name))
						.toString();
				map.put(t_name, t_value);
			}
		}
		return map;
	}

	public String getThreadResult(final AMXManager manager) {
		try {
			List<String> names = getName(manager);
			Map<String, String> busyThreadCount = getBusyThreadCount(manager);
			Map<String, String> availableThreadCount = getAvailableThreadCount(manager);
			Map<String, String> totalWorkItemCount = getTotalWorkItemCount(manager);
			Map<String, String> curThreadCount = getCurThreadCount(manager);
			Map<String, String> avgCompletionTime = getAvgCompletionTime(manager);
			Map<String, String> avgWaitTime = getAvgWaitTime(manager);
			StringBuilder sb = new StringBuilder();
			for (String name : names) {
				sb.append(name + "\t");
				sb.append(busyThreadCount.get(name) + "\t");
				sb.append(availableThreadCount.get(name) + "\t");
				sb.append(totalWorkItemCount.get(name) + "\t");
				sb.append(curThreadCount.get(name) + "\t");
				sb.append(avgCompletionTime.get(name) + "\n");
				sb.append(avgWaitTime.get(name) + "\n");
			}
			sb.deleteCharAt(sb.length() - 1);
			return sb.toString();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}
}
