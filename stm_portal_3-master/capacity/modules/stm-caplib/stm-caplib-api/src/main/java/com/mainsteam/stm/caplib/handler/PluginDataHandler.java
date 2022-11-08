package com.mainsteam.stm.caplib.handler;

import com.mainsteam.stm.caplib.plugin.ParameterDef;

/**
 * 数据处理器
 * @author Administrator
 *
 */
public class PluginDataHandler implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7029128838054169939L;
	/**
	 * 处理器的类全名
	 */
	private String classFullName;
	/**
	 * 参数定义，里面可以定义${key1}等
	 */
	private ParameterDef[] parameterDefs;

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

	/**
	 * 获取数据处理参数集合
	 * @return
	 */
	public ParameterDef[] getParameterDefs() {
		return parameterDefs;
	}

	public void setParameterDefs(ParameterDef[] parameterDefs) {
		this.parameterDefs = parameterDefs;
	}
	
	
	
}
