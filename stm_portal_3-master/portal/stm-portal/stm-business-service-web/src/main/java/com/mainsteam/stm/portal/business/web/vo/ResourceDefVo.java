package com.mainsteam.stm.portal.business.web.vo;

import java.io.Serializable;
import java.util.List;


/**
 * <li>文件名称: ResourceInstanceVo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月21日
 * @author   caoyong
 */
public class ResourceDefVo implements Serializable{
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private List<ResourceDefVo> children;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<ResourceDefVo> getChildren() {
		return children;
	}
	public void setChildren(List<ResourceDefVo> children) {
		this.children = children;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
