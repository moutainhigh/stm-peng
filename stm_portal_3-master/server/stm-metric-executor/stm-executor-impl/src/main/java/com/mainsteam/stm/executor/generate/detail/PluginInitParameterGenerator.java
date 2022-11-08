/**
 * 
 */
package com.mainsteam.stm.executor.generate.detail;

import java.util.Map;

import com.mainsteam.stm.caplib.collect.MetricCollect;
import com.mainsteam.stm.caplib.plugin.PluginConnectSetting;
import com.mainsteam.stm.executor.obj.PluginInitParameterData;
import com.mainsteam.stm.pluginserver.message.PluginRequest;

/**
 * @author ziw
 * 
 */
public class PluginInitParameterGenerator {

	/**
	 * 
	 */
	public PluginInitParameterGenerator() {
	}

	public void generatePluginInitParameter(PluginRequest request,
			Map<String, String> parameterMap, MetricCollect collect) {
		PluginInitParameterData parameter = new PluginInitParameterData();
		if (collect != null) {
			PluginConnectSetting[] connectSettings = collect
					.getPluginConnectSettings();
			if (connectSettings != null && connectSettings.length > 0) {
				for (int i = 0; i < connectSettings.length; i++) {
					if (parameterMap.containsKey(connectSettings[i]
							.getParameterId())) {
						continue;
					}
					parameterMap.put(connectSettings[i].getParameterId(),
							connectSettings[i].getParameterValue());
				}
			}
		}
		parameter.setParameter(parameterMap);
		request.setPluginInitParameter(parameter);
	}
}
