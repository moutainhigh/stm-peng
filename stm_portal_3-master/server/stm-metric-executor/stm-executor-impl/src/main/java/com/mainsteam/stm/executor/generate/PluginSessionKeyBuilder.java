/**
 * 
 */
package com.mainsteam.stm.executor.generate;

import java.util.Map;

import com.mainsteam.stm.caplib.collect.MetricCollect;
import com.mainsteam.stm.executor.exception.MetricExecutorException;

/**
 * @author ziw
 *
 */
public class PluginSessionKeyBuilder {

	/**
	 * 
	 */
	public PluginSessionKeyBuilder() {
	}

	public static String buildPluginSessionKey(MetricCollect collect,
			Map<String, String> discoveryInfo) throws MetricExecutorException {
		com.mainsteam.stm.caplib.plugin.PluginInitParameter[] initParameters = collect
				.getPlugin().getPluginInitParameters();
		StringBuilder builder = new StringBuilder();
		builder.append(collect.getPlugin().getId()).append('|');
		for (int i = 0; i < initParameters.length; i++) {
			com.mainsteam.stm.caplib.plugin.PluginInitParameter p = initParameters[i];
			if (p.isSessionKey()) {
				String pValue = discoveryInfo.get(p.getId());
				if (pValue == null) {
					pValue = p.getDefaultValue();
					if (pValue == null) {
						pValue = "";
					}
				}
				builder.append(pValue).append('|');
			}
		}
		return builder.toString();
	}
}
