package com.mainsteam.stm.profilelib.obj;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 资源自动刷新策略
 * @author Administrator
 *
 */
public class ProfilelibAutoRediscover implements Serializable {
	private static final long serialVersionUID = -5323108845912795883L;

	private long id;
	private String profileName;
	private String profileDesc;
	private int isUse;
	private long domainId;
	private long createUser;
	private Date createTime;
	private Long updateUser;
	private Date updateTime;
	private int executRepeat;
	private int isRemoveHistory;
	
	private List<ProfilelibAutoRediscoverInstance> profileInstanceRel;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getProfileName() {
		return profileName;
	}
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	public String getProfileDesc() {
		return profileDesc;
	}
	public void setProfileDesc(String profileDesc) {
		this.profileDesc = profileDesc;
	}
	public int getIsUse() {
		return isUse;
	}
	public void setIsUse(int isUse) {
		this.isUse = isUse;
	}
	public long getDomainId() {
		return domainId;
	}
	public void setDomainId(long domainId) {
		this.domainId = domainId;
	}
	public long getCreateUser() {
		return createUser;
	}
	public void setCreateUser(long createUser) {
		this.createUser = createUser;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Long getUpdateUser() {
		return updateUser;
	}
	public void setUpdateUser(Long updateUser) {
		this.updateUser = updateUser;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public int getExecutRepeat() {
		return executRepeat;
	}
	public void setExecutRepeat(int executRepeat) {
		this.executRepeat = executRepeat;
	}
	public int getIsRemoveHistory() {
		return isRemoveHistory;
	}
	public void setIsRemoveHistory(int isRemoveHistory) {
		this.isRemoveHistory = isRemoveHistory;
	}
	public List<ProfilelibAutoRediscoverInstance> getProfileInstanceRel() {
		return profileInstanceRel;
	}
	public void setProfileInstanceRel(List<ProfilelibAutoRediscoverInstance> profileInstanceRel) {
		this.profileInstanceRel = profileInstanceRel;
	}
	
}
