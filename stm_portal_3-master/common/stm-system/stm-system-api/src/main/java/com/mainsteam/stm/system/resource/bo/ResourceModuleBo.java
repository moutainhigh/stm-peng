package com.mainsteam.stm.system.resource.bo;

import java.io.Serializable;

/**
 * <li>文件名称: ModuleBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 资源模型</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月3日
 * @author   ziwenwen
 */
public class ResourceModuleBo implements Serializable{
	private static final long serialVersionUID = -31934451387669400L;
	private String id;
	private String name;
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
	
	
}


