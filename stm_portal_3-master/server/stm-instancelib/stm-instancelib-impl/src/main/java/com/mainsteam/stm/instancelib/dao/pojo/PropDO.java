package com.mainsteam.stm.instancelib.dao.pojo;
/**
 * 实例属性类型
 * @author xiaoruqiang
 */
public class PropDO {

	/**
	 * 实例属性Id
	 */
	private long instanceId;
	
	/**
	 * 实例属性key
	 */
	private String propKey;
	
	/**
	 * 实例属性value
	 */
	private String propValue;
	
	/**
	 * 实例属性类型
	 */
	private String propType;
	
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
	
	public String getPropKey() {
		return propKey;
	}

	public String getPropValue() {
		return propValue;
	}

	public String getPropType() {
		return propType;
	}

	public void setPropKey(String propKey) {
		this.propKey = propKey;
	}

	public void setPropValue(String propValue) {
		this.propValue = propValue;
	}

	public void setPropType(String propType) {
		this.propType = propType;
	}
}

