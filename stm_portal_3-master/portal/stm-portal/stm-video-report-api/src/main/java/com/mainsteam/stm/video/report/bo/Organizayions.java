package com.mainsteam.stm.video.report.bo;

import java.util.List;

public class Organizayions {

	private String Id;
	private String OrgName;
	private String ParentOrgId;
	private String Type;
	private int Level;
	private List<Organizayions> childrens;
	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}
	public String getOrgName() {
		return OrgName;
	}
	public void setOrgName(String orgName) {
		OrgName = orgName;
	}
	public String getParentOrgId() {
		return ParentOrgId;
	}
	public void setParentOrgId(String parentOrgId) {
		ParentOrgId = parentOrgId;
	}
	public String getType() {
		return Type;
	}
	public void setType(String type) {
		Type = type;
	}
	public List<Organizayions> getChildrens() {
		return childrens;
	}
	public void setChildrens(List<Organizayions> childrens) {
		this.childrens = childrens;
	}
	public int getLevel() {
		return Level;
	}
	public void setLevel(int Level) {
		this.Level = Level;
	}
	
	
	
}
