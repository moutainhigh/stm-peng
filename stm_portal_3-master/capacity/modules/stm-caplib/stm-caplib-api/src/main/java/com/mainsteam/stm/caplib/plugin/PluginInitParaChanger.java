package com.mainsteam.stm.caplib.plugin;

import java.io.Serializable;

/**
 * 初始化参数变更
 * 
 * @author Administrator
 * 
 */
public class PluginInitParaChanger implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 446093849700870920L;
	/**
	 * 插件ID
	 */
	private String pluginId;
	/**
	 * 参数ID
	 */
	private String parameterId;
	/**
	 * 属性ID
	 */
	private String propertyId;
	/**
	 * 属性值名称
	 */
	private String propertyValue;

	public PluginInitParaChanger() {

	}

	public String getPluginId() {
		return pluginId;
	}

	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}

	public String getParameterId() {
		return parameterId;
	}

	public void setParameterId(String parameterId) {
		this.parameterId = parameterId;
	}

	public String getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(String propertyId) {
		this.propertyId = propertyId;
	}

	public String getPropertyValue() {
		return propertyValue;
	}

	public void setPropertyValue(String proertyValue) {
		this.propertyValue = proertyValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((parameterId == null) ? 0 : parameterId.hashCode());
		result = prime * result
				+ ((pluginId == null) ? 0 : pluginId.hashCode());
		result = prime * result
				+ ((propertyId == null) ? 0 : propertyId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PluginInitParaChanger other = (PluginInitParaChanger) obj;
		if (parameterId == null) {
			if (other.parameterId != null)
				return false;
		} else if (!parameterId.equals(other.parameterId))
			return false;
		if (pluginId == null) {
			if (other.pluginId != null)
				return false;
		} else if (!pluginId.equals(other.pluginId))
			return false;
		if (propertyId == null) {
			if (other.propertyId != null)
				return false;
		} else if (!propertyId.equals(other.propertyId))
			return false;
		return true;
	}

}
