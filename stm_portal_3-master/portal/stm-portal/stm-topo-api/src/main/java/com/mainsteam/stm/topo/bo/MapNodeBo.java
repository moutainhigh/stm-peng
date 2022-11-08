package com.mainsteam.stm.topo.bo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class MapNodeBo {
	private Long id;
	private Long instanceId;
	private String attr;
	private String nodeid;
	private Long mapid;
	private Long nextMapId;
	private Integer level;
	private Integer fyjb;
	private String area;
	public Integer getFyjb() {
		return fyjb;
	}
	public void setFyjb(Integer fyjb) {
		this.fyjb = fyjb;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	//不存数据库
	private String ip;
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(Long instanceId) {
		this.instanceId = instanceId;
	}
	public String getAttr() {
		return attr;
	}
	public void setAttr(String attr) {
		this.attr = attr;
	}
	public String getNodeid() {
		return nodeid;
	}
	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}
	public Long getMapid() {
		return mapid;
	}
	public void setMapid(Long mapid) {
		this.mapid = mapid;
	}
	
	public Long getNextMapId() {
		return nextMapId;
	}
	public void setNextMapId(Long nextMapId) {
		this.nextMapId = nextMapId;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public JSONObject getAttrJson() {
		String attr = getAttr();
		if(null==attr || "".equals(attr)){
			return new JSONObject();
		}else{
			return JSON.parseObject(attr);
		}
	}
}
