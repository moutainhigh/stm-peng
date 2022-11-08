package com.mainsteam.stm.portal.config.bo;

import java.io.Serializable;
/**
 * <li>文件名称: ConfigWarnResourceBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月23日
 * @author   caoyong
 */
public class ConfigWarnResourceBo implements Serializable{
	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 2131588513206589287L;
	/**
	 * 告警ID
	 */
	private Long warnId;
	/**
	 * 资源ID
	 */
	private long resourceId;
	
	public Long getWarnId() {
		return warnId;
	}
	public void setWarnId(Long warnId) {
		this.warnId = warnId;
	}
	public long getResourceId() {
		return resourceId;
	}
	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}
}
