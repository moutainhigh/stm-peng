package com.mainsteam.stm.portal.config.bo;

import java.io.Serializable;
import java.util.List;

/**
 * <li>文件名称: ResourceCategoryBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月15日
 * @author   caoyong
 */
public class ResourceCategoryBo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8683578489307124317L;
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
	private List<ResourceCategoryBo> childCategorys;
	
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

	public List<ResourceCategoryBo> getChildCategorys() {
		return childCategorys;
	}

	public void setChildCategorys(List<ResourceCategoryBo> childCategorys) {
		this.childCategorys = childCategorys;
	}
}

