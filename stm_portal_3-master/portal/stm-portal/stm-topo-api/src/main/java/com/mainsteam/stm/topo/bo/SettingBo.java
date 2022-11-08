package com.mainsteam.stm.topo.bo;
/**
 * 数据库配置映射表（拓扑发现配置信息业务Bo）
 */
public class SettingBo {
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
