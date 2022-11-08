package com.mainsteam.stm.system.um.role.bo;
 
import java.util.Date;

import com.mainsteam.stm.platform.web.vo.IRole;

/**
 * <li>文件名称: Role.java</li>
 * <li>文件描述: 本类描述</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>公    司: 武汉美新翔盛科技有限公司</li>
 * <li>内容摘要: 无</li>
 * <li>其他说明:无</li>
 * <li>完成日期：@date</li>
 * <li>修改记录: 无</li>
 * @version 3.0
 * @author  ziwen
 * @date	2019年8月19日
 */
public class Role implements IRole,java.io.Serializable{
	private static final long serialVersionUID = 4970510841369309036L;
	
	/**
	 * 默认角色：管理者
	 */
	public static final long ROLE_MANAGER_ID = 1L;
	/**
	 * 默认角色：域管理员
	 */
	public static final long ROLE_DOMAIN_MANAGER_ID=2L;
	
	private Long id;
	private String name;
	private Integer status;
	private Long creatorId;
	private Date createdTime;
	private String description;
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
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Long getCreatorId() {
		return creatorId;
	}
	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}
	public Date getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
