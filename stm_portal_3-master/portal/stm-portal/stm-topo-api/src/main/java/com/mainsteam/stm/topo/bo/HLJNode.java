package com.mainsteam.stm.topo.bo;

public class HLJNode {
	private Long id;
	private String nodeId;
	private Integer nextMapId;
	private Integer mapId;
	private String instanceIds;
	private String area;
	private Integer parentMapId;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	public String getInstanceIds() {
		return instanceIds;
	}
	public void setInstanceIds(String instanceIds) {
		this.instanceIds = instanceIds;
	}
	public Integer getNextMapId() {
		return nextMapId;
	}
	public void setNextMapId(Integer nextMapId) {
		this.nextMapId = nextMapId;
	}
	public Integer getMapId() {
		return mapId;
	}
	public void setMapId(Integer mapId) {
		this.mapId = mapId;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public Integer getParentMapId() {
		return parentMapId;
	}
	public void setParentMapId(Integer parentMapId) {
		this.parentMapId = parentMapId;
	}
	
}
