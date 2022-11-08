package com.mainsteam.stm.instancelib.obj;

import java.io.Serializable;

/**
 * 实例类
 * @author xiaoruqiang
 */
public class Instance implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4158748564294195130L;

	/**
	 * 实例标识
	 */
	private long id;
	
	/**
	 *  实例名称
	 */
	private String name;

	
	/**
	 * 获取实例ID
	 * @return 实例ID
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * 实例ID值（系统维护，添加时不需要手动指定值）
	 */
	public void setId(long id) {
		this.id = id;
	}
	
	/**
	 * 获取实例名称
	 * @return 实例ID
	 */
	public String getName() {
		return name;
	}
	/**
	 * 实例名称赋值
	 * @return 实例名称
	 */
	public void setName(String name) {
		this.name = name;
	} 
	
}
