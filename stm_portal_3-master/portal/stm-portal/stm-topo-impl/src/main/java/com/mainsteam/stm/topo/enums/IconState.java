package com.mainsteam.stm.topo.enums;
/**
 * 图标的状态
 * @author 富强
 *
 */
public enum IconState {
	DISABLED("disabled",0),WARNING("warning",1),DANGER("danger",2),NORMAL("normal",3),SEVERITY("severity",4),NOT_MONITORED("not_monitored",5),MONITORED("monitored",6),NODATA("nodata",7);
	private String name;
	private int index;
	private IconState(String name,int idx){
		this.setName(name);
		this.setIndex(idx);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public static IconState nameOf(String name){
		for(IconState state:IconState.values()){
			if(state.getName().equals(name)){
				return state;
			}
		}
		return NORMAL;
	}
}
