package com.mainsteam.stm.topo.bo;

public class MapLineBo {
	private Long id;
	private String fromId;
	private String toId;
	private Long instanceId;
	private String attr;
	private Long mapid;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getAttr() {
		return attr;
	}
	public void setAttr(String attr) {
		this.attr = attr;
	}
	public Long getMapid() {
		return mapid;
	}
	public void setMapid(Long mapid) {
		this.mapid = mapid;
	}
	public String getFromId() {
		return fromId;
	}
	public void setFromId(String fromId) {
		this.fromId = fromId;
	}
	public String getToId() {
		return toId;
	}
	public void setToId(String toId) {
		this.toId = toId;
	}
	public Long getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(Long instanceId) {
		this.instanceId = instanceId;
	}
}
