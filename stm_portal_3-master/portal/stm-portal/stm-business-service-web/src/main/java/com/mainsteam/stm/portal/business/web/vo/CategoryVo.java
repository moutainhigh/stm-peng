package com.mainsteam.stm.portal.business.web.vo;

import java.io.Serializable;
import java.util.List;
/**
 * <li>文件名称: CategoryVo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月21日
 * @author   caoyong
 */
public class CategoryVo implements Serializable {

	private static final long serialVersionUID = -3749310201347180950L;

	private String id;
	private String name;
	private String pid;
	private String type;
	private String level;
	private List<CategoryVo> childcategorys;

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

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public List<CategoryVo> getChildcategorys() {
		return childcategorys;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setChildcategorys(List<CategoryVo> childcategorys) {
		this.childcategorys = childcategorys;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

}
