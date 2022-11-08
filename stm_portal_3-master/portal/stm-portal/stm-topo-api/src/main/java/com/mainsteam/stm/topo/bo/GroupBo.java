package com.mainsteam.stm.topo.bo;

import java.util.ArrayList;
import java.util.List;

public class GroupBo {
	private Long id;
	private String name;
	private Double x;
	private Double y;
	private Double height;
	private Double width;
	private List<Long> children = new ArrayList<Long>();
	private Long subTopoId;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getX() {
		return x;
	}
	public void setX(Double x) {
		this.x = x;
	}
	public Double getY() {
		return y;
	}
	public void setY(Double y) {
		this.y = y;
	}
	public Double getHeight() {
		return height;
	}
	public void setHeight(Double height) {
		this.height = height;
	}
	public Double getWidth() {
		return width;
	}
	public void setWidth(Double width) {
		this.width = width;
	}
	public List<Long> getChildren() {
		return children;
	}
	public void setChildren(List<Long> children) {
		this.children = children;
	}
	public Long getSubTopoId() {
		return subTopoId;
	}
	public void setSubTopoId(Long subTopoId) {
		this.subTopoId = subTopoId;
	}
}
