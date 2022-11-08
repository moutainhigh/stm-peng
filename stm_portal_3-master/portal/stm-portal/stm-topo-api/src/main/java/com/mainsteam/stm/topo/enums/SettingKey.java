package com.mainsteam.stm.topo.enums;

public enum SettingKey {
	/**
	 * 拓扑初始化标记
	 */
	TOPO_INIT_FLAG("topo_init_flag","拓扑初始化标记"),
	/**
	 * 拓扑机房机柜现有ID
	 */
	TOPO_ROOM_CABINET_IDS("topo_room_cabinet_ids","拓扑机房机柜现有ID");
	private String key;
	private String comment;
	private SettingKey(String key,String comment){
		this.key=key;
		this.comment=comment;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
}
