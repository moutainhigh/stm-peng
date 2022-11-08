package com.mainsteam.stm.topo.po;
/**
 * 拓扑发现配置信息持久化Po
 */
public class SettingPo {
	private Integer id;
	private String key;
	private String value;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		if(value==null){
			return "{}";
		}else{
			return value;
		}
	}
	public void setValue(String value) {
		this.value = value;
	}
}
