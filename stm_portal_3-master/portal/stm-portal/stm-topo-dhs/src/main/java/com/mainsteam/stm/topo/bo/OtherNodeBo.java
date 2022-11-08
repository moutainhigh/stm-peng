package com.mainsteam.stm.topo.bo;

import com.alibaba.fastjson.JSON;

public class OtherNodeBo {
	private Long id;
	private String attr;
	private Long subTopoId;
	private boolean visible=true;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getSubTopoId() {
		return subTopoId;
	}
	public void setSubTopoId(Long subTopoId) {
		this.subTopoId = subTopoId;
	}
	public String getAttr() {
		return attr;
	}
	public void setAttr(String attr) {
		this.attr = attr;
	}
	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	@SuppressWarnings("unchecked")
	public <T> T parseAttr(Class<?> type){
		return (T) JSON.parse(attr);
	}
}
