package com.mainsteam.stm.webService.cmdb.server;

import java.util.List;


public class MOType {
	/**
	 * 管理对象类型唯一标识
	 */
	private String moTypeId;
	/**
	 * 管理对象类型名称
	 */
	private String moTypeName;
	/**
	 * 管理对象特征属性定义信息列表或子属性定义信息列表
	 */
	private List<AttributeDefination> definations;

	public String getMoTypeId() {
		return moTypeId;
	}

	public void setMoTypeId(String moTypeId) {
		this.moTypeId = moTypeId;
	}

	public String getMoTypeName() {
		return moTypeName;
	}

	public void setMoTypeName(String moTypeName) {
		this.moTypeName = moTypeName;
	}

	public List<AttributeDefination> getDefinations() {
		return definations;
	}

	public void setDefinations(List<AttributeDefination> definations) {
		this.definations = definations;
	}
}
