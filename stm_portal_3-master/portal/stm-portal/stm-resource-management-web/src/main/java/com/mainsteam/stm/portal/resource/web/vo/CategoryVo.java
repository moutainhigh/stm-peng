package com.mainsteam.stm.portal.resource.web.vo;

import java.io.Serializable;

import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.resource.ResourceDef;

public class CategoryVo implements Serializable {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * ID
	 */
	private String id;
	/**
	 * name
	 */
	private String name;
	/**
	 * Parent Category
	 */
	private CategoryDef parentCategory;
	/**
	 * Child Categorys
	 */
	private CategoryDef[] childCategorys;
	/**
	 * Resource Def
	 */
	private ResourceDef[] resourceDefs;
	
	
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
	public CategoryDef getParentCategory() {
		return parentCategory;
	}
	public void setParentCategory(CategoryDef parentCategory) {
		this.parentCategory = parentCategory;
	}
	public CategoryDef[] getChildCategorys() {
		return childCategorys;
	}
	public void setChildCategorys(CategoryDef[] childCategorys) {
		this.childCategorys = childCategorys;
	}
	public ResourceDef[] getResourceDefs() {
		return resourceDefs;
	}
	public void setResourceDefs(ResourceDef[] resourceDefs) {
		this.resourceDefs = resourceDefs;
	}

}
