package com.mainsteam.stm.caplib.common;

import java.util.ArrayList;
import java.util.List;

import com.mainsteam.stm.caplib.resource.ResourceDef;

/**
 * 
 * @author Administrator
 * 
 */
public class CategoryDef implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4309495259861877888L;

	/**
	 * id
	 */
	private String id;

	private boolean isDisplay = true;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 父类
	 */
	private CategoryDef parentCategory;
	/**
	 * 子类
	 */
	private CategoryDef[] childCategorys;
	/**
	 * 资源模型
	 */
	private ResourceDef[] resourceDefs;

	public CategoryDef() {

	}

	/**
	 * 获取当前Category下的主资源集合
	 * 
	 * @return
	 */
	public ResourceDef[] getResourceDefs() {
		return resourceDefs;
	}

	/**
	 * 获取当前Category下的主资源ID的集合
	 * 
	 * @return
	 */
	public String[] getResourceIds() {
		List<String> ids = new ArrayList<String>();
		if (null != resourceDefs) {
			for (ResourceDef resourceDef : resourceDefs) {
				ids.add(resourceDef.getId());
			}
		}
		String[] idsArray = new String[ids.size()];
		return ids.toArray(idsArray);
	}

	public void setResourceDefs(ResourceDef[] resourceDefs) {
		this.resourceDefs = resourceDefs;
	}

	/**
	 * 获取当前Category的id
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 获取当前Category的名称
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取当前Category的上一级Category
	 * 
	 * @return
	 */
	public CategoryDef getParentCategory() {
		return parentCategory;
	}

	public void setParentCategory(CategoryDef parentCategory) {
		this.parentCategory = parentCategory;
	}

	/**
	 * 获取当前Category的下一级Category集合
	 * 
	 * @return
	 */
	public CategoryDef[] getChildCategorys() {
		return childCategorys;
	}

	public void setChildCategorys(CategoryDef[] childCategorys) {
		this.childCategorys = childCategorys;
	}

	/**
	 * 获取当前Category的是否显示在分类树上
	 * 
	 * @return true:需要显示 false:不需要显示
	 */
	public boolean isDisplay() {
		return isDisplay;
	}

	/**
	 * 
	 * 设置当前Category的是否显示在分类树上
	 * 
	 * @param isDisplay
	 *            true:需要显示 false:不需要显示
	 */
	public void setDisplay(boolean isDisplay) {
		this.isDisplay = isDisplay;
	}

}
