/**
 * 
 */
package com.mainsteam.stm.instancelib.dao.pojo;


/**
 * @author ziw
 *
 */
public class CustomPropDefinitionDO {


	/*
	 *  属性key
	 *  
	 *  该值让用户自己输入，便于从key来判断其含义。
	 */
	private String key;
	
	/*
	 * 属性名称
	 */
	private String name;
	
	/*
	 * 对的资源实例类别的属性
	 */
	private String category;
	
	/**
	 * 对该属性的最近修改时间
	 */
	private long updateTime;
	
	/**
	 * 
	 */
	public CustomPropDefinitionDO() {
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}
}
