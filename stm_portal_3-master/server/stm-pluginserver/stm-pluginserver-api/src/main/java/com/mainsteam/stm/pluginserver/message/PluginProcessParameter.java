/**
 * 
 */
package com.mainsteam.stm.pluginserver.message;

import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.PluginResultSetProcessor;

/**
 * @author ziw
 *
 */
public class PluginProcessParameter {

	private Class<? extends PluginResultSetProcessor> processorClass;
	
	private ProcessParameter parameter;
	
	/**
	 * 
	 */
	public PluginProcessParameter() {
	}

	public Class<? extends PluginResultSetProcessor> getProcessorClass() {
		return processorClass;
	}

	public void setProcessorClass(
			Class<? extends PluginResultSetProcessor> processorClass) {
		this.processorClass = processorClass;
	}

	public ProcessParameter getParameter() {
		return parameter;
	}

	public void setParameter(ProcessParameter parameter) {
		this.parameter = parameter;
	}
}
