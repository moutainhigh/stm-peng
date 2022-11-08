package com.mainsteam.stm.portal.business.bo;

import java.io.Serializable;

public class BizCanvasBo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3296170502605686582L;

	private long id;
	
	//业务ID
	private long bizId;
	
	//业务绘图全局相关属性
	private String attr;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getBizId() {
		return bizId;
	}

	public void setBizId(long bizId) {
		this.bizId = bizId;
	}

	public String getAttr() {
		return attr;
	}

	public void setAttr(String attr) {
		this.attr = attr;
	}
	
}
