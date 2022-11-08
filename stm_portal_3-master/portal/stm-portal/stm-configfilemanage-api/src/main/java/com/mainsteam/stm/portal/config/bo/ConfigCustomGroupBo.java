package com.mainsteam.stm.portal.config.bo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
/**
 * <li>文件名称: ConfigCustomGroupBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月13日
 * @author   caoyong
 */
public class ConfigCustomGroupBo implements Serializable{
	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 2131588513206589287L;
	/**
	 * 自定义配置组ID
	 */
	private Long id;
	/**
	 * 自定义配置组名称
	 */
	private String name;
	/**
	 * 自定义资源组描述
	 */
	private String description;
	/**
	 * 创建人
	 */
	private Long entryId;
	/**
	 * 创建时间
	 */
	private Date entryDateTime;
	/**
	 * 关联的网络设备资源ids
	 */
	private List<String> resourceInstanceIds;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Long getEntryId() {
		return entryId;
	}
	public void setEntryId(Long entryId) {
		this.entryId = entryId;
	}
	public Date getEntryDateTime() {
		return entryDateTime;
	}
	public void setEntryDateTime(Date entryDateTime) {
		this.entryDateTime = entryDateTime;
	}
	public List<String> getResourceInstanceIds() {
		return resourceInstanceIds;
	}
	public void setResourceInstanceIds(List<String> resourceInstanceIds) {
		this.resourceInstanceIds = resourceInstanceIds;
	}
	
	
	
}
