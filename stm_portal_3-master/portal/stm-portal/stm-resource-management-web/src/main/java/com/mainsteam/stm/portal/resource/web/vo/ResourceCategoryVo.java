package com.mainsteam.stm.portal.resource.web.vo;

import java.io.Serializable;


import java.util.List;

/**
 * <li>文件名称: ResourceCategoryVo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年7月24日
 * @author   pengl
 */
public class ResourceCategoryVo implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8879890776298720568L;
	
	/**
	 * ID
	 */
	private String id;
	/**
	 * name
	 */
	private String name;
	/**
	 * Child Category
	 */
	private List<ResourceCategoryVo> childCategorys;
	
	/**
	 * 资源个数
	 */
	private  int resourceNumber;
	
	public int getResourceNumber() {
		return resourceNumber;
	}

	public void setResourceNumber(int resourceNumber) {
		this.resourceNumber = resourceNumber;
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

	public List<ResourceCategoryVo> getChildCategorys() {
		return childCategorys;
	}

	public void setChildCategorys(List<ResourceCategoryVo> childCategorys) {
		this.childCategorys = childCategorys;
	}

	
	
	
	
}
