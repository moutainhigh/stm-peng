package com.mainsteam.stm.system.um.resources.bo;

import java.io.Serializable;

/**
 * <li>文件名称: userResourceBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月15日
 * @author   ziwenwen
 */
public class UserResourceBo implements Serializable{
	private static final long serialVersionUID = 7778375334320460928L;
	private Long userId;
	private Long resourceId;
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getResourceId() {
		return resourceId;
	}
	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}
}


