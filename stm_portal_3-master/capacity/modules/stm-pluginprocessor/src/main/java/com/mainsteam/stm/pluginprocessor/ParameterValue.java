/**
 * 
 */
package com.mainsteam.stm.pluginprocessor;

import java.io.Serializable;

import com.mainsteam.stm.pluginsession.parameter.Parameter;

/**
 * @author ziw
 * 
 */
public class ParameterValue implements Parameter,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7411987022233805350L;
	private String key = "";
	private String value;
	private String type;

	/**
	 * 
	 */
	public ParameterValue() {
	}

	public String getKey() {
		return key;
	}

	public void setType(String type) {
		if (null != type) {
			this.type = type;
		}
	}

	public void setKey(String key) {
		if (null != key) {
			this.key = key;
		}
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String getType() {
		return type;
	}
}
