package com.mainsteam.stm.instancelib.dao.pojo;
/**
 * 实例属性类型
 * @author xiaoruqiang
 */
public class CustomModulePropDO {

	/**
	 * 属性实例Id
	 */
	private long instanceId;
	
	/**
	 * 属性实例key
	 */
	private String propKey;
	
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
	public String getPropKey() {
		return propKey;
	}

	/**
	 * 实例属性key赋值
	 * @param key 实例属性key
	 */
	public void setPropKey(String propKey) {
		this.propKey = propKey;
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

}

