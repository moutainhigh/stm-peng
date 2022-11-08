package com.mainsteam.stm.instancelib.obj;

import java.io.Serializable;

public class CustomModuleProp  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1541269823715529906L;

	/**
	 * 属性实例Id
	 */
	private long instanceId;
	
	/**
	 * 属性实例key
	 */
	private String key;
	
	/**
	 * 属性采集值value
	 */
	private String realtimeValue ;
	
	/**
	 * 用户自定义值
	 */
	private String userValue;
	
	/**
	 * 获取实例属性Id
	 * @return 实例属性Id
	 */
	public long getInstanceId() {
		return instanceId;
	}

	/**
	 * 实例属性Id赋值
	 * @param instanceId 属性Id
	 */
	public void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
	}

	/**
	 * 获取实例属性key
	 * @return 实例属性key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * 实例属性key赋值
	 * @param key 实例属性key
	 */
	public void setKey(String key) {
		this.key = key;
	}
	
	
	public String getRealtimeValue() {
		return realtimeValue;
	}

	public void setRealtimeValue(String realtimeValue) {
		this.realtimeValue = realtimeValue;
	}

	public String getUserValue() {
		return userValue;
	}

	public void setUserValue(String userValue) {
		this.userValue = userValue;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder(100);
		str.append("{instanceId:").append(getInstanceId()).append(",");
		str.append("key:").append(getKey()).append(",");
		str.append("realtimeValue:").append(getRealtimeValue());
		str.append("userValue:").append(getUserValue());
		str.append("}");
		return str.toString();
	}

	
	
}
