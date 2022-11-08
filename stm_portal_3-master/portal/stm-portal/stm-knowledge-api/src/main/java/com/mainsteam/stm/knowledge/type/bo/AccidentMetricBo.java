package com.mainsteam.stm.knowledge.type.bo;

public class AccidentMetricBo {

	private String id;
	private String name;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public AccidentMetricBo(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	public AccidentMetricBo() {
		super();
	}
	
}
