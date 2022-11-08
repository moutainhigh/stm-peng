package com.mainsteam.stm.portal.inspect.web.utils.entity;

import java.util.List;

public class InspectionReport {

	private String name;
	private List<Head> heads;
	private List<Body> bodys;
	private List<Conclusion> conclusions;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Head> getHeads() {
		return heads;
	}

	public void setHeads(List<Head> heads) {
		this.heads = heads;
	}

	public List<Body> getBodys() {
		return bodys;
	}

	public void setBodys(List<Body> bodys) {
		this.bodys = bodys;
	}

	public List<Conclusion> getConclusions() {
		return conclusions;
	}

	public void setConclusions(List<Conclusion> conclusions) {
		this.conclusions = conclusions;
	}

}
