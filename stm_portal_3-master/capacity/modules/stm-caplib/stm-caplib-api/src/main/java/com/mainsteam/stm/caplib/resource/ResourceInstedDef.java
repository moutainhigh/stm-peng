package com.mainsteam.stm.caplib.resource;

import org.apache.commons.lang.StringUtils;

public class ResourceInstedDef implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4402941183129282207L;
	private String[] instanceId;
	private String instanceName;

	/**
	 * 获取instance id
	 * @return
	 */
	public String[] getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		if (null != instanceId) {
			String[] array = StringUtils.split(instanceId,",");
			this.instanceId = array;
		}
	}

	/**
	 * 获取instance 名称
	 * @return
	 */
	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

}
