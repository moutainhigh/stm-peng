package com.mainsteam.stm.knowledge.type.bo;

public class AccidentKMTypeBo {

	private String id;
	private String pId;
	private String name;
	private int level;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getpId() {
		return pId;
	}
	public void setpId(String pId) {
		this.pId = pId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public AccidentKMTypeBo(String id, String pId, String name, int level) {
		super();
		this.id = id;
		this.pId = pId;
		this.name = name;
		this.level = level;
	}
	public AccidentKMTypeBo() {
		super();
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	
	
}
