package com.mainsteam.stm.topo.enums;

public enum TopoType {
	UNKNOWN_TOPO("未知拓扑",-1),SECOND_TOPO("二层拓扑",0),THIRD_TOPOD("三层拓扑",1),ROOM_TOPO("机房拓扑",2),MAP_TOPO("地图拓扑",3);
	private String name;
	private int id;
	private TopoType(String name,int id){
		this.name=name;
		this.id=id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public static TopoType byId(Long id) {
		if(id==null) return SECOND_TOPO;
		for(TopoType type:TopoType.values()){
			if(type.getId()==id){
				return type;
			}
		}
		return UNKNOWN_TOPO;
	}
	
}
