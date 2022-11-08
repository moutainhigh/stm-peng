package com.mainsteam.stm.caplib.plugin;

import com.mainsteam.stm.caplib.dict.ValueTypeEnum;

/**
 * 参数定义
 * 
 * @author Administrator
 * 
 */
public class ParameterDef implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1345342563308017940L;
	/**
	 * 指明改key是从发现信息还是资源实例的属性里来取
	 */
	ValueTypeEnum type;
	/**
	 * 取值的key
	 */
	String key;
	/**
	 * 
	 */
	String value;

	/**
	 * 获取类型
	 * 
	 * @return
	 */
	public ValueTypeEnum getType() {
		return type;
	}

	public void setType(ValueTypeEnum type) {
		this.type = type;
	}

	/**
	 * 获取key
	 * 
	 * @return
	 */
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * 获取value
	 * 
	 * @return
	 */
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "ParameterDef [type=" + type + ", key=" + key + ", value="
				+ value + "]";
	}

}
