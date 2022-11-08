package com.mainsteam.stm.portal.inspect.bo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.mainsteam.stm.system.um.role.bo.Role;

public class InspectDomainRole implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3386388448610761118L;
 
	private Long id;
	private String roleName;

	private List<Role> roles;
	
	private String checked;
	private String name;
	private Integer status;
	private Long creatorId;
	private Date createdTime;
	private String description;

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

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public String getChecked() {
		return checked;
	}

	public void setChecked(String checked) {
		this.checked = checked;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
}
