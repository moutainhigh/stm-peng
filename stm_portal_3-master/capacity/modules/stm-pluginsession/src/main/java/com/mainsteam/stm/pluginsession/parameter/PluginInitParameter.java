/**
 * 
 */
package com.mainsteam.stm.pluginsession.parameter;

/**
 * plugin初始化参数
 * 
 * @author ziw
 * 
 */
public interface PluginInitParameter {
	public Parameter[] getParameters();
	public String getParameterValueByKey(String key);
}
