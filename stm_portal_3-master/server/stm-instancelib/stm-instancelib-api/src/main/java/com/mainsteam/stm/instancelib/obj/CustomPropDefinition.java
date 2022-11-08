/**
 * 
 */
package com.mainsteam.stm.instancelib.obj;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户自定义属性可管理对象
 * 
 * @author ziw
 *
 */
public class CustomPropDefinition implements Serializable{

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
	private Date updateTime;
	
	/**
	 * 
	 */
	public CustomPropDefinition() {
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

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}
