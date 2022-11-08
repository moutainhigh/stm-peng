package com.mainsteam.stm.caplib.handler;

import com.mainsteam.stm.caplib.plugin.ParameterDef;

/**
 * 转换器，用于取值列
 * 
 * @author Administrator
 *
 */
public class PluginDataConverter implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2463603557432449564L;
	private String classFullName;
	private ParameterDef[] parameters;

	public PluginDataConverter() {

	}

	public void setParameterDefs(ParameterDef[] converterParameters) {
		this.parameters = converterParameters;
	}

	/**
	 * 获取转换参数集合
	 * @return
	 */
	public ParameterDef[] getParameterDefs() {
		return this.parameters;
	}

	/**
	 * 获取代理class的classPath
	 * @return
	 */
	public String getClassFullName() {
		return classFullName;
	}

	public void setClassFullName(String classFullName) {
		this.classFullName = classFullName;
	}
}
