/**
 * 
 */
package com.mainsteam.stm.platform.system.config.bean;

import java.io.Serializable;

/**
 * <li>文件名称: SystemConfigBo</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月15日 下午8:12:17
 * @author   俊峰
 */
public class SystemConfigBo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2359051809002440950L;
	private long id;
	private String content;
	private String description;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public SystemConfigBo(String content, String description) {
		super();
		this.content = content;
		this.description = description;
	}
	public SystemConfigBo() {
		super();
	}
	public SystemConfigBo(long id, String content, String description) {
		super();
		this.id = id;
		this.content = content;
		this.description = description;
	}
	
	
}
