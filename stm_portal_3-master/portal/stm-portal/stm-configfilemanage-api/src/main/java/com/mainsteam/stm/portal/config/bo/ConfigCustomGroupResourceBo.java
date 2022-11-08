package com.mainsteam.stm.portal.config.bo;

import java.io.Serializable;
/**
 * <li>文件名称: ConfigCustomGroupResourceBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月13日
 * @author   caoyong
 */
public class ConfigCustomGroupResourceBo implements Serializable{
	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = -4084833068517449208L;
	/**
	 * 自定义配置组ID
	 */
	private Long groupId;

	private String resourceID;

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getResourceID() {
		return resourceID;
	}

	public void setResourceID(String resourceID) {
		this.resourceID = resourceID;
	}

}
