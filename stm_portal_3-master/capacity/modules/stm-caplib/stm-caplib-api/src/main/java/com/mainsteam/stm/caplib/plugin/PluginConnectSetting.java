package com.mainsteam.stm.caplib.plugin;

/**
 * 插件连接参数设置
 * 
 * @author Administrator
 * 
 */
public class PluginConnectSetting implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5843601326987708172L;
	/**
	 * 参数id
	 */
	private String parameterId;
	/**
	 * 参数值
	 */
	private String parameterValue;

	public PluginConnectSetting() {
	}

	public String getParameterId() {
		return parameterId;
	}

	public void setParameterId(String parameterId) {
		this.parameterId = parameterId;
	}

	public String getParameterValue() {
		return parameterValue;
	}

	public void setParameterValue(String parameterValue) {
		this.parameterValue = parameterValue;
	}

	@Override
	public String toString() {
		return "PluginConnectSetting [parameterId=" + parameterId
				+ ", parameterValue=" + parameterValue + "]";
	}

}
