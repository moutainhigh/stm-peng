package com.mainsteam.stm.portal.extendedplatform.resourceprofile.po;

import java.io.Serializable;
import java.util.List;

public class CategoryPo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8323754732367139166L;

	private String id;
	
	private String name;
	
	private List<CategoryPo> childCategorys;

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

	public List<CategoryPo> getChildCategorys() {
		return childCategorys;
	}

	public void setChildCategorys(List<CategoryPo> childCategorys) {
		this.childCategorys = childCategorys;
	}
	
	
}
