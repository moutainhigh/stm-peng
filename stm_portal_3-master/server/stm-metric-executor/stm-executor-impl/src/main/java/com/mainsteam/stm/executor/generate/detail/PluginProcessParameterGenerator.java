/**
 * 
 */
package com.mainsteam.stm.executor.generate.detail;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.collect.MetricCollect;
import com.mainsteam.stm.caplib.handler.PluginDataHandler;
import com.mainsteam.stm.caplib.plugin.ParameterDef;
import com.mainsteam.stm.errorcode.ServerErrorCodeConstant;
import com.mainsteam.stm.executor.exception.MetricExecutorException;
import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginprocessor.PluginResultSetProcessor;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginserver.message.PluginProcessParameter;
import com.mainsteam.stm.pluginserver.message.PluginRequest;

/**
 * @author ziw
 * 
 */
public class PluginProcessParameterGenerator {

	private static final Log logger = LogFactory
			.getLog(PluginProcessParameterGenerator.class);
	
	private ParameterValueGenerator valueGenerator;

	public void setValueGenerator(ParameterValueGenerator valueGenerator) {
		this.valueGenerator = valueGenerator;
	}

	/**
	 * 
	 */
	public PluginProcessParameterGenerator() {
	}

	@SuppressWarnings("unchecked")
	public Class<? extends PluginResultSetProcessor> getProcessorClass(
			String className) throws MetricExecutorException {
		try {
			return (Class<? extends PluginResultSetProcessor>) this.getClass()
					.getClassLoader().loadClass(className);
		} catch (ClassNotFoundException e) {
			if (logger.isErrorEnabled()) {
				logger.error("getProcessorClass className=" + className, e);
			}
			throw new MetricExecutorException(e);
		}
	}
	
	public void generatePluginProcessParameter(
			PluginRequest request, long resourceInstanceId,
			Map<String, String> param, MetricCollect collect) throws Exception {
		if (collect == null) {
			return;
		}
		PluginDataHandler[] dataHandlers = collect.getPluginDataHandlers();
		if (dataHandlers != null && dataHandlers.length > 0) {
			PluginProcessParameter[] pluginProcessParameters = new PluginProcessParameter[dataHandlers.length];
			for (int j = 0; j < dataHandlers.length; j++) {
				PluginDataHandler pluginDataHandler = dataHandlers[j];
				PluginProcessParameter pluginProcessParameter = new PluginProcessParameter();
				pluginProcessParameters[j] = pluginProcessParameter;
				String className = pluginDataHandler.getClassFullName();
				if (className == null || className.equals("")) {
					throw new MetricExecutorException(
							ServerErrorCodeConstant.ERR_SERVER_EXECUTOR_EMPTY_PROCESSER_CLASS_NAME,
							"pluginDataHandler.getClassFullName not exist.");
				}
				pluginProcessParameter
						.setProcessorClass(getProcessorClass(className));
				ParameterDef[] parameters = pluginDataHandler
						.getParameterDefs();
				ProcessParameter parameter = new ProcessParameter();
				pluginProcessParameter.setParameter(parameter);

				if (parameters != null && parameters.length > 0) {
					for (ParameterDef def : parameters) {
						ParameterValue value = valueGenerator
								.generateParameterValue(resourceInstanceId,
										def, param);
						parameter.addParameter(value);
					}
				}
			}
			request.setPluginProcessParameters(pluginProcessParameters);
		}
	}
}
