package com.mainsteam.stm.portal.netflow.po;

public class FlowPo {

	private int id;
	private String name;
	private long flow;
	private long flowPackage;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getFlow() {
		return flow;
	}

	public void setFlow(long flow) {
		this.flow = flow;
	}

	public long getFlowPackage() {
		return flowPackage;
	}

	public void setFlowPackage(long flowPackage) {
		this.flowPackage = flowPackage;
	}

}
