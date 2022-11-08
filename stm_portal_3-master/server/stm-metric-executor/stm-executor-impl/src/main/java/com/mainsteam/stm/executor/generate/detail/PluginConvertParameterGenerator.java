/**
 * 
 */
package com.mainsteam.stm.executor.generate.detail;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.collect.MetricCollect;
import com.mainsteam.stm.caplib.dict.ValueTypeEnum;
import com.mainsteam.stm.caplib.handler.PluginDataConverter;
import com.mainsteam.stm.caplib.plugin.ParameterDef;
import com.mainsteam.stm.executor.exception.MetricExecutorException;
import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginprocessor.PluginResultSetConverter;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginserver.message.PluginConvertParameter;
import com.mainsteam.stm.pluginserver.message.PluginRequest;

/**
 * @author ziw
 * 
 */
public class PluginConvertParameterGenerator {

	private static final Log logger = LogFactory
			.getLog(PluginConvertParameterGenerator.class);

	private ParameterValueGenerator valueGenerator;

	public void setValueGenerator(ParameterValueGenerator valueGenerator) {
		this.valueGenerator = valueGenerator;
	}

	/**
	 * 
	 */
	public PluginConvertParameterGenerator() {
	}

	@SuppressWarnings("unchecked")
	public Class<? extends PluginResultSetConverter> getConverterClass(
			String className) throws MetricExecutorException {
		try {
			return (Class<? extends PluginResultSetConverter>) this.getClass()
					.getClassLoader().loadClass(className);
		} catch (ClassNotFoundException e) {
			if (logger.isErrorEnabled()) {
				logger.error("getProcessorClass className=" + className, e);
			}
			throw new MetricExecutorException(e);
		}
	}

	public void generatePluginConvertParameter(
			PluginRequest request, long resourceInstanceId,
			Map<String, String> param, MetricCollect collect) throws Exception {
		if (logger.isTraceEnabled()) {
			logger.trace("bindPluginConvertParameter start");
		}
		if (collect == null) {
			return;
		}
		PluginDataConverter converter = collect.getPluginDataConverter();
		if (converter == null) {
			return;
		}
		PluginConvertParameter convertParameter = new PluginConvertParameter();
		convertParameter.setConverterClass(getConverterClass(converter
				.getClassFullName()));
		ParameterDef[] parameters = converter.getParameterDefs();
		ProcessParameter parameter = new ProcessParameter();
		if (parameters != null && parameters.length > 0) {
			for (ParameterDef def : parameters) {
				if (logger.isDebugEnabled()) {
					logger.debug("bindPluginConvertParameter bindPluginConvertParameter key="
							+ def.getKey()
							+ " value="
							+ def.getValue()
							+ " type=" + def.getType());
				}
				ParameterValue value = valueGenerator.generateParameterValue(
						resourceInstanceId, def, param);
				parameter.addParameter(value);
				if (ValueTypeEnum.ResourceProperty == def.getType()
						&& "InstPropertyKey".equals(def.getKey())) {
					request.setConvertParameterIndexProperty(def.getValue());
				}
			}
		}
		convertParameter.setParameter(parameter);
		request.setPluginConvertParameter(convertParameter);
		if (logger.isTraceEnabled()) {
			logger.trace("bindPluginConvertParameter end");
		}
	}
}
