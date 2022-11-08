package com.mainsteam.stm.topo.web.vo;

import java.io.Serializable;


/**
 * 拓扑发现配置前段交互Vo
 */
public class SettingVo implements Serializable{
	private static final long serialVersionUID = 6774717552233973903L;
	private Long id;
	private String key;
	private String value;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
