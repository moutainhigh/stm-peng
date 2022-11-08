package com.mainsteam.stm.caplib.plugin;

import com.mainsteam.stm.caplib.dict.ParameterTypeEnum;

/**
 * 插件参数
 * @author Administrator
 *
 */
public class PluginParameter implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3842899321235148743L;
	/**
	 * 参数内容
	 */
	private ParameterDef[] parameters;
	/**
	 * 类型，是数组还是map
	 */
	private ParameterTypeEnum type;

	/**
	 * 获取插件执行参数集合
	 * @return
	 */
	public ParameterDef[] getParameters() {
		return parameters;
	}

	public void setParameters(ParameterDef[] parameters) {
		this.parameters = parameters;
	}

	/**
	 * 获取类型
	 * @return
	 */
	public ParameterTypeEnum getType() {
		return type;
	}

	public void setType(ParameterTypeEnum type) {
		this.type = type;
	}
	
	
}
