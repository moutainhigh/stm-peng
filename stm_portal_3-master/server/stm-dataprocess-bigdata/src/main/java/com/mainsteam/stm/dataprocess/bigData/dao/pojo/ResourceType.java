package com.mainsteam.stm.dataprocess.bigData.dao.pojo;

public class ResourceType {

	private String parentId;
	private int level;
	private String id;
	private String name;
	private String description;
	private String kmTypeEnum;
	
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getKmTypeEnum() {
		return kmTypeEnum;
	}
	public void setKmTypeEnum(String kmTypeEnum) {
		this.kmTypeEnum = kmTypeEnum;
	}
}
