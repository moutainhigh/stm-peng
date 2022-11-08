package com.mainsteam.stm.portal.inspect.bo;

import java.util.List;

public class ModelTree {

	private String id;
	private String name;
	private List<ModelTree> childModelTrees;
	private boolean isResource = false;

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

	public List<ModelTree> getChildModelTrees() {
		return childModelTrees;
	}

	public void setChildModelTrees(List<ModelTree> childModelTrees) {
		this.childModelTrees = childModelTrees;
	}

	public boolean isResource() {
		return isResource;
	}

	public void setResource(boolean isResource) {
		this.isResource = isResource;
	}

}
