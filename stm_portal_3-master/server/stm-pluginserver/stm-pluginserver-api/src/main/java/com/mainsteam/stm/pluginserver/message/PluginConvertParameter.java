/**
 * 
 */
package com.mainsteam.stm.pluginserver.message;

import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.PluginResultSetConverter;

/**
 * @author ziw
 *
 */
public class PluginConvertParameter {
	
	private Class<? extends PluginResultSetConverter> converterClass;
	
	private ProcessParameter parameter;

	/**
	 * 
	 */
	public PluginConvertParameter() {
	}

	public Class<? extends PluginResultSetConverter> getConverterClass() {
		return converterClass;
	}

	public void setConverterClass(Class<? extends PluginResultSetConverter> converterClass) {
		this.converterClass = converterClass;
	}

	public ProcessParameter getParameter() {
		return parameter;
	}

	public void setParameter(ProcessParameter parameter) {
		this.parameter = parameter;
	}
}
