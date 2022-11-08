package com.mainsteam.stm.portal.inspect.bo;

import java.util.List;

public class ResourceTree {
	private String id;
	private String name;
	List<ResourceTree> resourceTree;

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

	public List<ResourceTree> getResourceTree() {
		return resourceTree;
	}

	public void setResourceTree(List<ResourceTree> resourceTree) {
		this.resourceTree = resourceTree;
	}

}
