package com.mainsteam.stm.portal.resource.bo;

import java.io.Serializable;

/**
 * <li>文件名称: ResourceDefBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年7月30日
 * @author   pengl
 */
public class ResourceDefBo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1111060880226907928L;

	private String id;
	
	private String name;
	
	private ResourceCategoryBo category;

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

	public ResourceCategoryBo getCategory() {
		return category;
	}

	public void setCategory(ResourceCategoryBo category) {
		this.category = category;
	}
	
	
}
