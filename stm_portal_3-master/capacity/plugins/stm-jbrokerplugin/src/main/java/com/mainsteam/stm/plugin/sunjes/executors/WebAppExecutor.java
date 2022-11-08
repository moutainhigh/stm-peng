package com.mainsteam.stm.plugin.sunjes.executors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mainsteam.stm.plugin.sunjes.amx.AMXManager;
import com.mainsteam.stm.plugin.sunjes.amx.DottedNames;
import com.mainsteam.stm.plugin.sunjes.util.SunJESConnInfo;
import com.sun.appserv.management.util.misc.ListUtil;

/**
 * 执行器<br>
 * <br>
 */
public class WebAppExecutor extends BaseExecutor {
	/**
	 * Constructors.
	 * 
	 * @param source
	 *            连接信息
	 */
	public WebAppExecutor(final SunJESConnInfo source) {
		this.m_connInfo = source;
		this.m_dottedNames = DottedNames.getInstance(m_connInfo
				.getInstanceName());
	}

	/**
	 * 最大活动会话数
	 * 
	 * @param manager
	 *            AMXManager
	 * @return Map<String, String>
	 * @throws PluginException
	 */
	public Map<String, String> getMaxActiveSessionCount(final AMXManager manager) {
		Map<String, String> map = new HashMap<String, String>();
		List<String> t_list = getWebAppNames(manager);
		for (String t_name : t_list) {
			String t_value = manager.getMonitoringDottedNameValue(
					m_dottedNames.getWebAppHighSessionCount(t_name)).toString();
			map.put(t_name, t_value);
		}
		return map;
	}

	/**
	 * 可用性
	 * 
	 * @param manager
	 *            AMXManager
	 * @return Map<String, String>
	 * @throws PluginException
	 */
	public Map<String, String> getWebappAvailability(final AMXManager manager) {
		Map<String, String> map = new HashMap<String, String>();
		List<String> t_list = getWebAppNames(manager);
		for (String t_name : t_list) {

			map.put(t_name, "1");
		}
		return map;
	}

	/**
	 * 得到所有的Web应用名称
	 * 
	 * @param manager
	 *            AMXManager
	 * @return List<String>
	 * @throws PluginException
	 */
	private List<String> getWebAppNames(final AMXManager manager) {
		Map<String, String> t_webapps = manager.getWebModuleVirtualServerName();

		t_webapps.remove("adminapp");
		t_webapps.remove("admingui");
		t_webapps.remove("com_sun_web_ui");

		String[] t_names = t_webapps.keySet().toArray(new String[0]);
		List<String> t_list = new ArrayList<String>();
		ListUtil.addArray(t_list, t_names);
		return t_list;
	}

	public String getWebAppResult(final AMXManager manager) {
		try {
			List<String> names = getName(manager);
			Map<String, String> maxActiveSessionCount = getMaxActiveSessionCount(manager);
			Map<String, String> availability = getWebappAvailability(manager);
			Map<String, String> activeSessionCount = getActiveSessionCount(manager);
			StringBuilder sb = new StringBuilder();
			for (String name : names) {
				sb.append(name + "\t");
				sb.append(maxActiveSessionCount.get(name) + "\t");
				sb.append(availability.get(name) + "\t");
				sb.append(activeSessionCount.get(name) + "\t");
			}
			sb.deleteCharAt(sb.length() - 1);
			return sb.toString();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}

	/**
	 * Web应用名称
	 * 
	 * @param manager
	 *            AMXManager
	 * @return Map<String, String>
	 * @throws PluginException
	 */
	public List<String> getName(final AMXManager manager) {
		List<String> t_list = getWebAppNames(manager);
		return t_list;
	}

	/**
	 * 活动的会话数
	 * 
	 * @param manager
	 *            AMXManager
	 * @return Map<String, String>
	 * @throws PluginException
	 */
	public Map<String, String> getActiveSessionCount(final AMXManager manager) {
		Map<String, String> map = new HashMap<String, String>();
		List<String> t_list = getWebAppNames(manager);
		for (String t_name : t_list) {
			String t_value = manager.getMonitoringDottedNameValue(
					m_dottedNames.getWebAppSessionCount(t_name)).toString();
			map.put(t_name, t_value);
		}
		return map;
	}

}
