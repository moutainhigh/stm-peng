/**
 * 
 */
package com.mainsteam.stm.executor.generate.detail;

import java.util.Map;

import com.mainsteam.stm.caplib.collect.MetricCollect;
import com.mainsteam.stm.caplib.dict.ParameterTypeEnum;
import com.mainsteam.stm.caplib.plugin.ParameterDef;
import com.mainsteam.stm.caplib.plugin.PluginParameter;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginserver.message.PluginRequest;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginArrayExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginMapExecutorParameter;

/**
 * @author ziw
 * 
 */
public class PluginExecutorParameterGenerator {

	private ParameterValueGenerator valueGenerator;

	public void setValueGenerator(ParameterValueGenerator valueGenerator) {
		this.valueGenerator = valueGenerator;
	}

	/**
	 * 
	 */
	public PluginExecutorParameterGenerator() {
	}

	public void generatePluginExecutorParameter(PluginRequest request,
			long resourceInstanceId, MetricCollect collect,
			Map<String, String> param) throws InstancelibException {
		if (collect == null) {
			return;
		}
		PluginParameter parameter = collect.getPluginParameter();
		if (parameter == null) {
			return;
		}
		ParameterDef[] parameterDefs = parameter.getParameters();
		/* map类型参数 */
		if (ParameterTypeEnum.MapType == parameter.getType()) {
			PluginMapExecutorParameter executorParameter = new PluginMapExecutorParameter();
			if (parameterDefs != null && parameterDefs.length > 0) {
				for (ParameterDef def : parameterDefs) {
					ParameterValue value = valueGenerator
							.generateParameterValue(resourceInstanceId, def,
									param);
					executorParameter.addParameter(value.getKey(), value);
				}
			}
			request.setPluginExecutorParameter(executorParameter);
		} else {
			/* 数组类型参数 */
			PluginArrayExecutorParameter pluginArrayExecutorParameter = new PluginArrayExecutorParameter();
			if (parameterDefs != null && parameterDefs.length > 0) {
				Parameter[] parameterArray = new Parameter[parameterDefs.length];
				for (int i = 0; i < parameterDefs.length; i++) {
					ParameterValue value = valueGenerator
							.generateParameterValue(resourceInstanceId,
									parameterDefs[i], param);
					parameterArray[i] = value;
				}
				pluginArrayExecutorParameter.setParameters(parameterArray);
			}
			request.setPluginExecutorParameter(pluginArrayExecutorParameter);
		}
	}
}
