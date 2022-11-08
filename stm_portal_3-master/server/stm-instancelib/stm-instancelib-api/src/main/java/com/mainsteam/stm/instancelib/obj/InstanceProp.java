package com.mainsteam.stm.instancelib.obj;

import java.io.Serializable;

/**
 * 实例属性类
 */
public abstract class InstanceProp implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3184827961986725694L;

	/**
	 * 属性实例Id
	 */
	private long instanceId;
	
	/**
	 * 属性实例key
	 */
	private String key;
	
	/**
	 * 属性实例values
	 */
	private String[] values;
	
//	/**
//	 * 属性实例类型
//	 */
//	private String type;
	
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

	/**
	 * 获取实例属性values
	 * @return 实例属性values
	 */
	public String[] getValues() {
		return values;
	}

	/**
	 * 实例属性values 赋值
	 * @param values 实例属性values
	 */
	public void setValues(String[] values) {
		this.values = values;
	}

//	/**
//	 * 获取实例类型
//	 * @return 实例类型
//	 */
//	public String getType() {
//		return type;
//	}
//
//	/**
//	 * 实例类型赋值
//	 * @param type 实例类型
//	 */
//	public void setType(String type) {
//		this.type = type;
//	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder(100);
		str.append("{instanceId:"+getInstanceId()+",");
		str.append("key:"+getKey()+",");
		String[] values = getValues();
		if(values != null){
			str.append("values:{");
			for (String value : values) {
				str.append(value+",");
			}
			str.append("}");
		}else{
			str.append("values:[]}");
		}
		return str.toString();
	}	
}
