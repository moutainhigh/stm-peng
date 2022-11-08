package com.mainsteam.stm.system.resource.bo;

import java.util.HashSet;
import java.util.Set;

/**
 * <li>文件名称: CategoryBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年11月27日
 * @author   ziwenwen
 */
public class CategoryBo {
	private String id;
	
	private String pid;
	
	private String name;
	
	/**
	 * 树上所有子资源类别id
	 */
	private Set<String> subCategoryIds=new HashSet<String>();
	
	/**
	 * 树上所有子模型id
	 */
//	private Set<String> subModuleIds=new HashSet<String>();
	
	private CategoryBo[] children;

//	public void addSubModuleId(String moduleId){
//		subModuleIds.add(moduleId);
//	}
//
//	public void addSubModuleId(Set<String> moduleIds){
//		subModuleIds.addAll(moduleIds);
//	}
//
//	public Set<String> getSubModuleIds() {
//		return subModuleIds;
//	}

	public void addSubCategoryId(String categoryId){
		subCategoryIds.add(categoryId);
	}

	public void addSubCategoryId(Set<String> categoryIds){
		subCategoryIds.addAll(categoryIds);
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<String> getSubCategoryIds() {
		return subCategoryIds;
	}

	public CategoryBo[] getChildren() {
		return children;
	}

	public void setChildren(CategoryBo[] children) {
		this.children = children;
	}
}


